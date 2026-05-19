package coviello.gestion_de_alumnos;

import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.repository.*;
import coviello.gestion_de_alumnos.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Preinscripcion - Crear")
class PreinscripcionControllerTest {

    @Autowired WebApplicationContext context;
    @Autowired RolRepository rolRepository;
    @Autowired AlumnoRepository alumnoRepository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired CarreraRepository carreraRepository;
    @Autowired PreinscripcionRepository preinscripcionRepository;
    @Autowired DocumentoRepository documentoRepository;

    @MockitoBean EmailService emailService;

    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .registerModule(new JavaTimeModule());
    String tokenAlumno;
    Long carreraId;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();

        documentoRepository.deleteAll();
        preinscripcionRepository.deleteAll();
        alumnoRepository.deleteAll();
        usuarioRepository.deleteAll();
        carreraRepository.deleteAll();
        rolRepository.deleteAll();

        Rol rolAlumno = new Rol();
        rolAlumno.setNombre("ALUMNO");
        rolRepository.save(rolAlumno);

        Carrera carrera = new Carrera();
        carrera.setNombre("Tecnicatura en Desarrollo de Software");
        carrera.setActiva(true);
        carrera.setCupoMaximo(0);
        carreraId = carreraRepository.save(carrera).getId();

        String registro = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Maria",
                "apellidos", "Lopez",
                "dni",       "99887766",
                "email",     "maria@gmail.com",
                "password",  "Password123"
        ));
        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registro));

        String login = objectMapper.writeValueAsString(Map.of(
                "username", "maria@gmail.com",
                "password", "Password123"
        ));
        MvcResult resultado = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andReturn();

        tokenAlumno = objectMapper.readTree(
                resultado.getResponse().getContentAsString()
        ).at("/data/token").asText();
    }

    private String formularioBase(Long idCarrera) throws Exception {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("nombres",           "Maria");
        body.put("apellidos",         "Lopez");
        body.put("dni",               "99887766");
        body.put("fechaNacimiento",   "1998-03-15");
        body.put("lugarNacimiento",   "Córdoba");
        body.put("nacionalidad",      "Argentina");
        body.put("domicilio",         "Calle Falsa 123");
        body.put("localidad",         "Buenos Aires");
        body.put("telefono",          "1122334455");
        body.put("email",             "maria@gmail.com");
        body.put("egresadoSecundaria", true);
        body.put("debeMaterias",       false);
        body.put("materiasAdeudadas",  null);
        body.put("carreraId",          idCarrera);
        return objectMapper.writeValueAsString(body);
    }

    // ─────────────────────────────────────────────
    // TESTS
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-12 | Crear preinscripción con datos completos → 201")
    void crearPreinscripcionExitosa() throws Exception {
        mockMvc.perform(post("/api/preinscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formularioBase(carreraId))
                        .header("Authorization", "Bearer " + tokenAlumno))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value(containsString("Formulario de preinscripción")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.estado").value("ENVIADA"));
    }

    @Test
    @DisplayName("TC-13 | Crear preinscripción sin token → 401")
    void crearPreinscripcionSinAutenticacion() throws Exception {
        mockMvc.perform(post("/api/preinscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formularioBase(carreraId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-14 | Crear preinscripción con carrera inexistente → 400")
    void crearPreinscripcionCarreraInexistente() throws Exception {
        mockMvc.perform(post("/api/preinscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formularioBase(99999L))
                        .header("Authorization", "Bearer " + tokenAlumno))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-15 | Crear preinscripción con carrera con cupo lleno → 400")
    void crearPreinscripcionSinCupo() throws Exception {
        Carrera carreraLlena = new Carrera();
        carreraLlena.setNombre("Carrera Llena");
        carreraLlena.setActiva(true);
        carreraLlena.setCupoMaximo(1);
        Long idCarreraLlena = carreraRepository.save(carreraLlena).getId();

        // Primera inscripción ocupa el único cupo
        mockMvc.perform(post("/api/preinscripciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(formularioBase(idCarreraLlena))
                .header("Authorization", "Bearer " + tokenAlumno));

        // Segunda inscripción → cupo lleno
        mockMvc.perform(post("/api/preinscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(formularioBase(idCarreraLlena))
                        .header("Authorization", "Bearer " + tokenAlumno))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("cupos")));
    }
}
