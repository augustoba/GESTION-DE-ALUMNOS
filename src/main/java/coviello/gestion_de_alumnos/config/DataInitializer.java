package coviello.gestion_de_alumnos.config;

import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CarreraRepository carreraRepository;
    private final PermisoRepository permisoRepository;
    private final DocenteRepository docenteRepository;
    private final AlumnoRepository alumnoRepository;
    private final AnioCarreraRepository anioCarreraRepository;
    private final ComisionRepository comisionRepository;
    private final MateriaRepository materiaRepository;
    private final HorarioClaseRepository horarioClaseRepository;

    private static final String SUPER_ADMIN_EMAIL = "superadmin@coviello.com";
    private static final String ADMIN_EMAIL        = "admin@coviello.com";
    private static final String DOCENTE_EMAIL      = "docente@coviello.com";
    private static final String ALUMNO_EMAIL       = "alumno@coviello.com";
    private static final String DEFAULT_PASSWORD   = "Admin1234";

    /*
     * Cuentas creadas al iniciar la aplicación:
     *
     *   ROL          EMAIL                       PASSWORD
     *   SUPER_ADMIN  superadmin@coviello.com     Admin1234
     *   ADMIN        admin@coviello.com          Admin1234
     *   DOCENTE      docente@coviello.com        Admin1234
     *   ALUMNO       alumno@coviello.com         Admin1234
     */

    // Materias que dicta el docente de ejemplo, con el día asignado (sin superposición)
    private static final Map<String, DiaSemana> HORARIO_POR_MATERIA = Map.of(
        "Programación I",   DiaSemana.LUNES,
        "Programación II",  DiaSemana.MARTES,
        "Programación III", DiaSemana.MIERCOLES
    );
    private static final Set<String> MATERIAS_DOCENTE = HORARIO_POR_MATERIA.keySet();

    @Override
    public void run(ApplicationArguments args) {
        crearRolSiNoExiste("SUPER_ADMIN");
        crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("DOCENTE");
        crearRolSiNoExiste("ALUMNO");
        crearPermisosSiNoExisten();
        crearSuperAdminSiNoExiste();
        crearAdminSiNoExiste();
        crearDocenteSiNoExiste();
        crearAlumnoSiNoExiste();
        crearCarrerasSiNoExisten();
        crearAniosYMateriasSiNoExisten();
        asignarComisionAlumno();
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

    // Credenciales de ejemplo: docente@coviello.com / Admin1234
    private void crearDocenteSiNoExiste() {
        if (usuarioRepository.findByUsername(DOCENTE_EMAIL).isPresent()) return;
        Rol rol = rolRepository.findByNombre("DOCENTE")
                .orElseThrow(() -> new IllegalStateException("Rol DOCENTE no encontrado"));

        Usuario u = new Usuario();
        u.setUsername(DOCENTE_EMAIL);
        u.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        u.setRol(rol);
        usuarioRepository.save(u);

        Docente d = new Docente();
        d.setNombres("Juan");
        d.setApellidos("Pérez");
        d.setDni("12345678");
        d.setEmail(DOCENTE_EMAIL);
        d.setTelefono("1122334455");
        d.setActivo(true);
        d.setUsuario(u);
        docenteRepository.save(d);
        log.info("Docente creado: {}", DOCENTE_EMAIL);
    }

    private void asignarComisionAlumno() {
        Alumno alumno = alumnoRepository.findByEmail(ALUMNO_EMAIL).orElse(null);
        if (alumno == null || alumno.getComision() != null) return;

        // Busca el 1° año de la primera carrera (Sistemas Informáticos)
        Carrera sistemas = carreraRepository.findAll().stream()
                .filter(c -> c.getNombre().contains("Sistemas"))
                .findFirst().orElse(null);
        if (sistemas == null) return;

        AnioCarrera primerAnio = anioCarreraRepository
                .findByCarreraIdOrderByNumeroAnio(sistemas.getId())
                .stream().findFirst().orElse(null);
        if (primerAnio == null) return;

        // Crea la comisión si no existe aún para ese año
        Comision comision = comisionRepository
                .findByAnioCarreraIdOrderByNombre(primerAnio.getId())
                .stream().findFirst().orElseGet(() -> {
                    Comision c = new Comision();
                    c.setNombre("Comisión A");
                    c.setCupoMaximo(30);
                    c.setAnioCarrera(primerAnio);
                    c.setActiva(true);
                    comisionRepository.save(c);
                    log.info("Comisión creada para 1° año de Sistemas");
                    return c;
                });

        alumno.setComision(comision);
        alumnoRepository.save(alumno);
        log.info("Alumno '{}' asignado a '{}'", ALUMNO_EMAIL, comision.getNombre());
    }

    // Credenciales de ejemplo: alumno@coviello.com / Admin1234
    private void crearAlumnoSiNoExiste() {
        if (usuarioRepository.findByUsername(ALUMNO_EMAIL).isPresent()) return;
        Rol rol = rolRepository.findByNombre("ALUMNO")
                .orElseThrow(() -> new IllegalStateException("Rol ALUMNO no encontrado"));

        Usuario u = new Usuario();
        u.setUsername(ALUMNO_EMAIL);
        u.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        u.setRol(rol);
        usuarioRepository.save(u);

        Alumno a = new Alumno();
        a.setNombres("María");
        a.setApellidos("García");
        a.setDni("87654321");
        a.setEmail(ALUMNO_EMAIL);
        a.setTelefono("1199887766");
        a.setFechaNac(LocalDate.of(2000, 5, 15));
        a.setHabilitado(true);
        a.setUsuario(u);
        alumnoRepository.save(a);
        log.info("Alumno creado: {}", ALUMNO_EMAIL);
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

    private void crearAniosYMateriasSiNoExisten() {
        if (anioCarreraRepository.count() > 0) return;

        Docente docente = docenteRepository.findByUsuario_Username(DOCENTE_EMAIL).orElse(null);
        List<Carrera> carreras = carreraRepository.findAll();
        if (carreras.isEmpty()) return;

        // materias[i] = { nombre, descripcion } para cada año de cada carrera
        String[][][][] materiasPorCarreraYAnio = {
            // Sistemas Informáticos — 3 años
            {
                { { "Programación I", "Fundamentos de programación y algoritmos" },
                  { "Matemática I", "Álgebra y cálculo aplicado a sistemas" },
                  { "Arquitectura de Computadoras", "Hardware, CPU, memoria y periféricos" },
                  { "Introducción a los Sistemas", "Conceptos generales de TI y sistemas de información" } },
                { { "Programación II", "Programación orientada a objetos" },
                  { "Base de Datos", "Diseño y gestión de bases de datos relacionales" },
                  { "Redes I", "Fundamentos de redes y protocolos TCP/IP" },
                  { "Sistemas Operativos", "Gestión de procesos, memoria y sistemas de archivos" } },
                { { "Programación III", "Desarrollo web y aplicaciones multicapa" },
                  { "Ingeniería de Software", "Metodologías, diseño y gestión de proyectos" },
                  { "Seguridad Informática", "Criptografía, vulnerabilidades y protección de sistemas" },
                  { "Proyecto Final", "Desarrollo de un proyecto integrador de carrera" } }
            },
            // Diseño Gráfico — 3 años
            {
                { { "Diseño I", "Principios fundamentales del diseño visual" },
                  { "Fotografía", "Técnica fotográfica y edición de imagen" },
                  { "Historia del Arte", "Corrientes artísticas y su influencia en el diseño" },
                  { "Color y Tipografía", "Teoría del color y uso tipográfico" } },
                { { "Diseño II", "Diseño editorial y composición avanzada" },
                  { "Ilustración Digital", "Técnicas de ilustración con herramientas digitales" },
                  { "Diseño Web", "Diseño de interfaces y experiencia de usuario" },
                  { "Branding", "Identidad de marca y sistemas visuales corporativos" } },
                { { "Diseño III", "Proyectos de diseño aplicado" },
                  { "Motion Graphics", "Animación y gráficos en movimiento" },
                  { "Packaging", "Diseño de envases y embalajes" },
                  { "Proyecto Integrador", "Producción de un portfolio profesional" } }
            },
            // Administración de Empresas — 3 años
            {
                { { "Contabilidad I", "Principios de contabilidad general" },
                  { "Economía", "Micro y macroeconomía aplicada a las organizaciones" },
                  { "Matemática Financiera", "Interés, amortización y evaluación de proyectos" },
                  { "Administración I", "Teorías administrativas y procesos organizacionales" } },
                { { "Contabilidad II", "Estados contables y análisis financiero" },
                  { "Gestión de RRHH", "Selección, capacitación y liquidación de haberes" },
                  { "Marketing", "Estrategias de mercado y comportamiento del consumidor" },
                  { "Derecho Empresarial", "Marco legal de las empresas y contratos comerciales" } },
                { { "Administración Estratégica", "Planificación y toma de decisiones empresariales" },
                  { "Finanzas Corporativas", "Gestión financiera y valuación de empresas" },
                  { "Comercio Internacional", "Exportación, importación y mercados globales" },
                  { "Proyecto Empresarial", "Elaboración de un plan de negocios completo" } }
            }
        };

        for (int ci = 0; ci < carreras.size() && ci < materiasPorCarreraYAnio.length; ci++) {
            Carrera carrera = carreras.get(ci);
            String[][][] anios = materiasPorCarreraYAnio[ci];

            for (int a = 0; a < anios.length; a++) {
                AnioCarrera anio = new AnioCarrera();
                anio.setCarrera(carrera);
                anio.setNumeroAnio(a + 1);
                anioCarreraRepository.save(anio);
                log.info("Año {} creado para carrera '{}'", a + 1, carrera.getNombre());

                for (String[] mat : anios[a]) {
                    boolean asignarDocente = docente != null && MATERIAS_DOCENTE.contains(mat[0]);

                    Materia m = new Materia();
                    m.setNombre(mat[0]);
                    m.setDescripcion(mat[1]);
                    m.setAnioCarrera(anio);
                    m.setDocente(asignarDocente ? docente : null);
                    materiaRepository.save(m);
                    log.info("  Materia creada: {}", mat[0]);

                    if (asignarDocente) {
                        HorarioClase h = new HorarioClase();
                        h.setMateria(m);
                        h.setDocente(docente);
                        h.setDiaSemana(HORARIO_POR_MATERIA.get(mat[0]));
                        h.setHoraInicio(LocalTime.of(8, 0));
                        h.setHoraFin(LocalTime.of(10, 0));
                        h.setFechaInicioCursada(LocalDate.of(2026, 3, 1));
                        h.setFechaFinCursada(LocalDate.of(2026, 12, 15));
                        horarioClaseRepository.save(h);
                        log.info("    Horario creado: {} {}", HORARIO_POR_MATERIA.get(mat[0]), "08:00-10:00");
                    }
                }
            }
        }
    }
}
