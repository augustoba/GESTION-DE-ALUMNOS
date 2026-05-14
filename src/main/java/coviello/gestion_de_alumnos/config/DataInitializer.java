package coviello.gestion_de_alumnos.config;

import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.model.Usuario;
import coviello.gestion_de_alumnos.repository.CarreraRepository;
import coviello.gestion_de_alumnos.repository.RolRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CarreraRepository carreraRepository;

    private static final String ADMIN_EMAIL    = "admin@coviello.com";
    private static final String ADMIN_PASSWORD = "Admin1234";

    @Override
    public void run(ApplicationArguments args) {
        crearRolSiNoExiste("ALUMNO");
        crearRolSiNoExiste("DOCENTE");
        crearRolSiNoExiste("ADMIN");
        crearAdminSiNoExiste();
        crearCarrerasSiNoExisten();
    }

    private void crearRolSiNoExiste(String nombre) {
        if (rolRepository.findByNombre(nombre).isEmpty()) {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            rolRepository.save(rol);
            log.info("Rol '{}' creado", nombre);
        }
    }

    private void crearCarrerasSiNoExisten() {
        if (carreraRepository.count() > 0) return;

        String[][] datos = {
            { "Técnico Superior en Sistemas Informáticos",   "Carrera orientada al desarrollo de software, bases de datos y redes." },
            { "Técnico Superior en Diseño Gráfico",          "Carrera orientada al diseño visual, branding e identidad corporativa." },
            { "Técnico Superior en Administración de Empresas", "Carrera orientada a la gestión, contabilidad y organización empresarial." }
        };

        for (String[] d : datos) {
            Carrera c = new Carrera();
            c.setNombre(d[0]);
            c.setDescripcion(d[1]);
            c.setActiva(true);
            c.setCupoMaximo(30);
            carreraRepository.save(c);
            log.info("Carrera creada: {}", d[0]);
        }
    }

    private void crearAdminSiNoExiste() {
        if (usuarioRepository.findByUsername(ADMIN_EMAIL).isPresent()) {
            return;
        }
        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado"));

        Usuario admin = new Usuario();
        admin.setUsername(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRol(rolAdmin);
        usuarioRepository.save(admin);

        log.info("Usuario admin creado: {}", ADMIN_EMAIL);
    }
}
