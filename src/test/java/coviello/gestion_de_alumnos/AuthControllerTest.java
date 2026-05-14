package coviello.gestion_de_alumnos;

import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.RolRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import coviello.gestion_de_alumnos.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Auth - Registro y Login")
class AuthControllerTest {

    @Autowired WebApplicationContext context;
    @Autowired RolRepository rolRepository;
    @Autowired AlumnoRepository alumnoRepository;
    @Autowired UsuarioRepository usuarioRepository;

    @MockitoBean EmailService emailService;

    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        alumnoRepository.deleteAll();
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        Rol rolAlumno = new Rol();
        rolAlumno.setNombre("ALUMNO");
        rolRepository.save(rolAlumno);
    }

    // ─────────────────────────────────────────────
    // REGISTRO
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-01 | Registro exitoso con datos válidos → 200")
    void registroExitoso() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "12345678",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value(containsString("Registro exitoso")));
    }

    @Test
    @DisplayName("TC-02 | Registro con nombre que contiene números → 400")
    void registroNombreConNumeros() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan123",
                "apellidos", "Perez",
                "dni",       "12345678",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Error de validación"))
                .andExpect(jsonPath("$.data", hasItem(containsString("nombres"))));
    }

    @Test
    @DisplayName("TC-03 | Registro con apellido que contiene números → 400")
    void registroApellidoConNumeros() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Per3z",
                "dni",       "12345678",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data", hasItem(containsString("apellidos"))));
    }

    @Test
    @DisplayName("TC-04 | Registro con DNI menor a 8 dígitos → 400")
    void registroDniCorto() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "1234",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data", hasItem(containsString("dni"))));
    }

    @Test
    @DisplayName("TC-05 | Registro con DNI que contiene letras → 400")
    void registroDniConLetras() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "1234ABCD",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data", hasItem(containsString("dni"))));
    }

    @Test
    @DisplayName("TC-06 | Registro con email con formato inválido → 400")
    void registroEmailInvalido() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "12345678",
                "email",     "no-es-un-email",
                "password",  "Contrasenia123"
        ));

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data", hasItem(containsString("email"))));
    }

    @Test
    @DisplayName("TC-07 | Registro con DNI ya existente → 400")
    void registroDniDuplicado() throws Exception {
        String primerUsuario = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "12345678",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(primerUsuario));

        String segundoUsuario = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Pedro",
                "apellidos", "Garcia",
                "dni",       "12345678",
                "email",     "pedro@gmail.com",
                "password",  "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(segundoUsuario))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("DNI")));
    }

    @Test
    @DisplayName("TC-08 | Registro con email ya existente → 400")
    void registroEmailDuplicado() throws Exception {
        String primerUsuario = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "12345678",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(primerUsuario));

        String segundoUsuario = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Pedro",
                "apellidos", "Garcia",
                "dni",       "87654321",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(segundoUsuario))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("email")));
    }

    // ─────────────────────────────────────────────
    // LOGIN
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("TC-09 | Login exitoso → 200 con token JWT")
    void loginExitoso() throws Exception {
        String registro = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "12345678",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registro));

        String login = objectMapper.writeValueAsString(Map.of(
                "username", "juan@gmail.com",
                "password", "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.username").value("juan@gmail.com"))
                .andExpect(jsonPath("$.data.rol").value("ALUMNO"));
    }

    @Test
    @DisplayName("TC-10 | Login con contraseña incorrecta → 400")
    void loginContrasenaIncorrecta() throws Exception {
        String registro = objectMapper.writeValueAsString(Map.of(
                "nombres",   "Juan",
                "apellidos", "Perez",
                "dni",       "12345678",
                "email",     "juan@gmail.com",
                "password",  "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registro));

        String login = objectMapper.writeValueAsString(Map.of(
                "username", "juan@gmail.com",
                "password", "ContraseniaMAL"
        ));
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("TC-11 | Login con usuario inexistente → 400")
    void loginUsuarioInexistente() throws Exception {
        String login = objectMapper.writeValueAsString(Map.of(
                "username", "noexiste@gmail.com",
                "password", "Contrasenia123"
        ));
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }
}
