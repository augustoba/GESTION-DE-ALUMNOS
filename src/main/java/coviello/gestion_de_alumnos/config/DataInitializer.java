package coviello.gestion_de_alumnos.config;

import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
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
    private final PermisoRepository permisoRepository;

    private static final String SUPER_ADMIN_EMAIL = "superadmin@coviello.com";
    private static final String ADMIN_EMAIL        = "admin@coviello.com";
    private static final String DEFAULT_PASSWORD   = "Admin1234";

    @Override
    public void run(ApplicationArguments args) {
        crearRolSiNoExiste("SUPER_ADMIN");
        crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("DOCENTE");
        crearRolSiNoExiste("ALUMNO");
        crearPermisosSiNoExisten();
        crearSuperAdminSiNoExiste();
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

    private void crearPermisosSiNoExisten() {
        for (CodigoPermiso codigo : CodigoPermiso.values()) {
            if (permisoRepository.findByCodigo(codigo).isEmpty()) {
                Permiso p = new Permiso();
                p.setCodigo(codigo);
                p.setDescripcion(codigo.name().replace('_', ' ').toLowerCase());
                permisoRepository.save(p);
                log.info("Permiso '{}' creado", codigo);
            }
        }
    }

    private void crearSuperAdminSiNoExiste() {
        if (usuarioRepository.findByUsername(SUPER_ADMIN_EMAIL).isPresent()) return;
        Rol rol = rolRepository.findByNombre("SUPER_ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol SUPER_ADMIN no encontrado"));
        Usuario u = new Usuario();
        u.setUsername(SUPER_ADMIN_EMAIL);
        u.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        u.setRol(rol);
        usuarioRepository.save(u);
        log.info("Super admin creado: {}", SUPER_ADMIN_EMAIL);
    }

    private void crearAdminSiNoExiste() {
        if (usuarioRepository.findByUsername(ADMIN_EMAIL).isPresent()) return;
        Rol rol = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no encontrado"));
        Usuario u = new Usuario();
        u.setUsername(ADMIN_EMAIL);
        u.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        u.setRol(rol);
        usuarioRepository.save(u);
        log.info("Admin creado: {}", ADMIN_EMAIL);
    }

    private void crearCarrerasSiNoExisten() {
        if (carreraRepository.count() > 0) return;

        String[][] datos = {
            { "Técnico Superior en Sistemas Informáticos",
              "Carrera orientada al desarrollo de software, bases de datos y redes." },
            { "Técnico Superior en Diseño Gráfico",
              "Carrera orientada al diseño visual, branding e identidad corporativa." },
            { "Técnico Superior en Administración de Empresas",
              "Carrera orientada a la gestión, contabilidad y organización empresarial." }
        };

        for (String[] d : datos) {
            Carrera c = new Carrera();
            c.setNombre(d[0]);
            c.setDescripcion(d[1]);
            c.setActiva(true);
            carreraRepository.save(c);
            log.info("Carrera creada: {}", d[0]);
        }
    }
}
