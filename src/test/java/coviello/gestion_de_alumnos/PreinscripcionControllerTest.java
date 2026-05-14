package coviello.gestion_de_alumnos;

import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.repository.*;
import coviello.gestion_de_alumnos.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
    final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
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

        // Registra alumno de prueba
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

        // Hace login y guarda el token
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

    private MockMultipartFile imagen(String campo) {
        return new MockMultipartFile(campo, campo + ".jpg", "image/jpeg",
                ("contenido-" + campo).getBytes());
    }

    private MockMultipartFile pdf(String campo) {
        return new MockMultipartFile(campo, campo + ".pdf", "application/pdf",
                ("contenido-" + campo).getBytes());
    }

    // ─────────────────────────────────────────────
    // TESTS
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-12 | Crear preinscripción con todos los datos y archivos → 201")
    void crearPreinscripcionExitosa() throws Exception {
        mockMvc.perform(multipart("/api/preinscripciones")
                        .file(imagen("comprobante"))
                        .file(imagen("dniFente"))
                        .file(imagen("dniDorso"))
                        .file(pdf("titulo"))
                        .file(imagen("fotoCarnet"))
                        .param("nombre",          "Maria")
                        .param("apellido",        "Lopez")
                        .param("dni",             "99887766")
                        .param("email",           "maria@gmail.com")
                        .param("telefono",        "1122334455")
                        .param("direccion",       "Calle Falsa 123")
                        .param("fechaNacimiento", "1998-03-15")
                        .param("carreraId",       carreraId.toString())
                        .header("Authorization", "Bearer " + tokenAlumno))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value(containsString("Preinscripción creada")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.estado").value("PENDIENTE_PAGO"))
                .andExpect(jsonPath("$.data.pagoValidado").value(false))
                .andExpect(jsonPath("$.data.documentosCompletos").value(false));
    }

    @Test
    @DisplayName("TC-13 | Crear preinscripción sin token → 401")
    void crearPreinscripcionSinAutenticacion() throws Exception {
        mockMvc.perform(multipart("/api/preinscripciones")
                        .file(imagen("comprobante"))
                        .file(imagen("dniFente"))
                        .file(imagen("dniDorso"))
                        .file(pdf("titulo"))
                        .file(imagen("fotoCarnet"))
                        .param("nombre",          "Sin")
                        .param("apellido",        "Token")
                        .param("dni",             "11223344")
                        .param("email",           "sintoken@gmail.com")
                        .param("telefono",        "1100000000")
                        .param("direccion",       "Calle 456")
                        .param("fechaNacimiento", "2000-01-01")
                        .param("carreraId",       carreraId.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("TC-14 | Crear preinscripción con carrera inexistente → 400")
    void crearPreinscripcionCarreraInexistente() throws Exception {
        mockMvc.perform(multipart("/api/preinscripciones")
                        .file(imagen("comprobante"))
                        .file(imagen("dniFente"))
                        .file(imagen("dniDorso"))
                        .file(pdf("titulo"))
                        .file(imagen("fotoCarnet"))
                        .param("nombre",          "Maria")
                        .param("apellido",        "Lopez")
                        .param("dni",             "99887766")
                        .param("email",           "maria@gmail.com")
                        .param("telefono",        "1122334455")
                        .param("direccion",       "Calle Falsa 123")
                        .param("fechaNacimiento", "1998-03-15")
                        .param("carreraId",       "99999")
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
        mockMvc.perform(multipart("/api/preinscripciones")
                .file(imagen("comprobante"))
                .file(imagen("dniFente"))
                .file(imagen("dniDorso"))
                .file(pdf("titulo"))
                .file(imagen("fotoCarnet"))
                .param("nombre",          "Maria")
                .param("apellido",        "Lopez")
                .param("dni",             "99887766")
                .param("email",           "maria@gmail.com")
                .param("telefono",        "1122334455")
                .param("direccion",       "Calle Falsa 123")
                .param("fechaNacimiento", "1998-03-15")
                .param("carreraId",       idCarreraLlena.toString())
                .header("Authorization", "Bearer " + tokenAlumno));

        // Segunda inscripción en la misma carrera → cupo lleno
        mockMvc.perform(multipart("/api/preinscripciones")
                        .file(imagen("comprobante"))
                        .file(imagen("dniFente"))
                        .file(imagen("dniDorso"))
                        .file(pdf("titulo"))
                        .file(imagen("fotoCarnet"))
                        .param("nombre",          "Maria")
                        .param("apellido",        "Lopez")
                        .param("dni",             "99887766")
                        .param("email",           "maria@gmail.com")
                        .param("telefono",        "1122334455")
                        .param("direccion",       "Calle Falsa 123")
                        .param("fechaNacimiento", "1998-03-15")
                        .param("carreraId",       idCarreraLlena.toString())
                        .header("Authorization", "Bearer " + tokenAlumno))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("cupos")));
    }
}
