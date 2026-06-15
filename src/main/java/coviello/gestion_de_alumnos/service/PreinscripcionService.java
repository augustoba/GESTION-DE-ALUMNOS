package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.DocumentoChecklistResponse;
import coviello.gestion_de_alumnos.dto.PagoResponse;
import coviello.gestion_de_alumnos.dto.PreinscripcionDetalleResponse;
import coviello.gestion_de_alumnos.dto.PreinscripcionRequest;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
@Slf4j
public class PreinscripcionService {

    private final PreinscripcionRepository preinscripcionRepository;
    private final CarreraRepository carreraRepository;
    private final DocumentoChecklistRepository checklistRepository;
    private final PagoRepository pagoRepository;
    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PdfService pdfService;

    public PreinscripcionService(PreinscripcionRepository preinscripcionRepository,
                                 CarreraRepository carreraRepository,
                                 DocumentoChecklistRepository checklistRepository,
                                 PagoRepository pagoRepository,
                                 AlumnoRepository alumnoRepository,
                                 UsuarioRepository usuarioRepository,
                                 RolRepository rolRepository,
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService,
                                 PdfService pdfService) {
        this.preinscripcionRepository = preinscripcionRepository;
        this.carreraRepository = carreraRepository;
        this.checklistRepository = checklistRepository;
        this.pagoRepository = pagoRepository;
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.pdfService = pdfService;
    }

    @Transactional
    public Preinscripcion crear(PreinscripcionRequest req) {
        if (preinscripcionRepository.findByDni(req.dni()).isPresent()) {
            throw new RuntimeException("Ya existe una preinscripción con el DNI: " + req.dni());
        }

        Preinscripcion pre = new Preinscripcion();
        pre.setNombre(req.nombre());
        pre.setApellido(req.apellido());
        pre.setDni(req.dni());
        pre.setEmail(req.email());
        pre.setTelefono(req.telefono());
        pre.setDireccion(req.direccion());
        pre.setLocalidad(req.localidad());
        pre.setFechaNacimiento(req.fechaNacimiento());
        pre.setLugarNacimiento(req.lugarNacimiento());
        pre.setNacionalidad(req.nacionalidad());
        pre.setFotoUrl(req.fotoUrl());
        pre.setEstado(EstadoPreinscripcion.PENDIENTE);
        pre.setFechaCreacion(LocalDateTime.now());

        if (req.carreraId() != null) {
            Carrera carrera = carreraRepository.findById(req.carreraId())
                    .orElseThrow(() -> new RuntimeException("Carrera no encontrada: " + req.carreraId()));
            verificarCupo(carrera);
            pre.setCarrera(carrera);
        }

        Preinscripcion guardada = preinscripcionRepository.save(pre);
        guardada.setCodigoFormulario(String.valueOf(guardada.getId()));
        guardada = preinscripcionRepository.save(guardada);

        // Crear registro de pago vacío
        Pago pago = new Pago();
        pago.setPreinscripcion(guardada);
        pago.setEstado(EstadoPago.SIN_PAGO);
        pagoRepository.save(pago);

        // Enviar PDF por email si tiene email
        if (guardada.getEmail() != null && !guardada.getEmail().isBlank()) {
            try {
                byte[] pdf = pdfService.generarFormularioPreinscripcion(guardada);
                String nombre = guardada.getNombre() + " " + guardada.getApellido();
                emailService.enviarFormularioPreinscripcion(guardada.getEmail(), nombre,
                        guardada.getCodigoFormulario(), pdf);
            } catch (Exception e) {
                log.error("No se pudo enviar el formulario PDF a {}: {}", guardada.getEmail(), e.getMessage());
            }
        }

        return guardada;
    }

    public Preinscripcion obtenerPorId(Long id) {
        return preinscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada con ID: " + id));
    }

    public List<Preinscripcion> buscarPorCodigo(String codigo) {
        return preinscripcionRepository.findByCodigoFormularioContaining(codigo.trim());
    }

    public Preinscripcion buscarPorDni(String dni) {
        return preinscripcionRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada con DNI: " + dni));
    }

    public Page<Preinscripcion> buscarPorNombre(String apellido, String nombre, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("apellido").ascending());
        return preinscripcionRepository.findByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(
                apellido, nombre, pageable);
    }

    public Page<Preinscripcion> listarTodas(int page, int size) {
        return preinscripcionRepository.findAll(PageRequest.of(page, size, Sort.by("fechaCreacion").descending()));
    }

    public Page<Preinscripcion> listarPorEstado(EstadoPreinscripcion estado, int page, int size) {
        return preinscripcionRepository.findByEstado(estado, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public PreinscripcionDetalleResponse obtenerDetalle(Long id) {
        Preinscripcion pre = obtenerPorId(id);

        PagoResponse pagoResponse = pagoRepository.findByPreinscripcionId(id)
                .map(p -> new PagoResponse(p.getId(), p.getEstado(), p.getMontoTotal(),
                        p.getMontoAbonado(), p.getFechaUltimoPago()))
                .orElse(null);

        List<DocumentoChecklistResponse> checklist = checklistRepository.findByPreinscripcionId(id).stream()
                .map(c -> new DocumentoChecklistResponse(c.getId(), c.getTipoDocumento(),
                        c.isPresentado(), c.getFechaPresentacion()))
                .toList();

        return new PreinscripcionDetalleResponse(
                pre.getId(), pre.getCodigoFormulario(), pre.getNombre(), pre.getApellido(),
                pre.getDni(), pre.getEmail(), pre.getTelefono(), pre.getDireccion(), pre.getLocalidad(),
                pre.getFechaNacimiento(), pre.getLugarNacimiento(), pre.getNacionalidad(), pre.getFotoUrl(),
                pre.getCarrera() != null ? pre.getCarrera().getNombre() : null,
                pre.getCarrera() != null ? pre.getCarrera().getId() : null,
                pre.getEstado(), pre.getFechaCreacion(),
                pre.getAlumno() != null ? pre.getAlumno().getId() : null,
                pagoResponse, checklist
        );
    }

    public byte[] generarPdf(Long id) {
        return pdfService.generarFormularioPreinscripcion(obtenerPorId(id));
    }

    @Transactional
    public Preinscripcion cambiarEstado(Long id, EstadoPreinscripcion nuevoEstado) {
        Preinscripcion pre = obtenerPorId(id);
        pre.setEstado(nuevoEstado);
        return preinscripcionRepository.save(pre);
    }

    @Transactional
    public Preinscripcion habilitarComoAlumno(Long id) {
        Preinscripcion pre = obtenerPorId(id);

        if (pre.getAlumno() == null) {
            Rol rolAlumno = rolRepository.findByNombre("ALUMNO")
                    .orElseThrow(() -> new RuntimeException("Rol ALUMNO no encontrado"));

            // Generar token de activación (válido 72 hs)
            String token = UUID.randomUUID().toString();

            Usuario usuario = new Usuario();
            usuario.setUsername(pre.getEmail());
            // Contraseña inutilizable hasta que el alumno active su cuenta
            usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            usuario.setRol(rolAlumno);
            usuario.setTokenActivacion(token);
            usuario.setTokenActivacionExpiracion(LocalDateTime.now().plusHours(72));
            usuario = usuarioRepository.save(usuario);

            // Crear entidad Alumno a partir de los datos de la preinscripción
            Alumno alumno = new Alumno();
            alumno.setNombres(pre.getNombre());
            alumno.setApellidos(pre.getApellido());
            alumno.setDni(pre.getDni());
            alumno.setEmail(pre.getEmail());
            alumno.setTelefono(pre.getTelefono());
            alumno.setDireccion(pre.getDireccion());
            alumno.setLocalidad(pre.getLocalidad());
            alumno.setFechaNac(pre.getFechaNacimiento());
            alumno.setFotoUrl(pre.getFotoUrl());
            alumno.setCarrera(pre.getCarrera());
            alumno.setHabilitado(true);
            alumno.setUsuario(usuario);
            alumno = alumnoRepository.save(alumno);

            pre.setAlumno(alumno);

            // Enviar email con link de activación
            try {
                String nombre = pre.getNombre() + " " + pre.getApellido();
                String carrera = pre.getCarrera() != null ? pre.getCarrera().getNombre() : "la carrera seleccionada";
                emailService.enviarActivacionCuenta(pre.getEmail(), nombre, carrera, token);
            } catch (MailException e) {
                log.error("No se pudo enviar email de activación a {}: {}", pre.getEmail(), e.getMessage());
            }
        } else {
            // El alumno ya existe — solo habilitarlo
            pre.getAlumno().setHabilitado(true);
            alumnoRepository.save(pre.getAlumno());
        }

        pre.setEstado(EstadoPreinscripcion.HABILITADO);
        return preinscripcionRepository.save(pre);
    }

    @Transactional
    public Preinscripcion rechazar(Long id) {
        Preinscripcion pre = obtenerPorId(id);
        pre.setEstado(EstadoPreinscripcion.RECHAZADO);
        return preinscripcionRepository.save(pre);
    }


    private void verificarCupo(Carrera carrera) {
        if (carrera.getCupoMaximo() == 0) return;
        long activos = preinscripcionRepository.countByCarreraIdAndEstadoNot(
                carrera.getId(), EstadoPreinscripcion.RECHAZADO);
        if (activos >= carrera.getCupoMaximo()) {
            throw new RuntimeException("No hay cupos disponibles para la carrera: " + carrera.getNombre());
        }
    }
}
