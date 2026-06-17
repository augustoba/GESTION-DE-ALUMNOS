package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.DocumentoChecklistResponse;
import coviello.gestion_de_alumnos.dto.PagoResponse;
import coviello.gestion_de_alumnos.dto.PreinscripcionDetalleResponse;
import coviello.gestion_de_alumnos.dto.PreinscripcionRequest;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import coviello.gestion_de_alumnos.repository.ComisionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PreinscripcionService {

    private final PreinscripcionRepository preinscripcionRepository;
    private final CarreraRepository carreraRepository;
    private final ComisionRepository comisionRepository;
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
                                 ComisionRepository comisionRepository,
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
        this.comisionRepository = comisionRepository;
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

        // Enviar PDF por email en segundo plano para no bloquear la respuesta
        if (guardada.getEmail() != null && !guardada.getEmail().isBlank()) {
            byte[] pdf = pdfService.generarFormularioPreinscripcion(guardada);
            String nombre = guardada.getNombre() + " " + guardada.getApellido();
            emailService.enviarFormularioPreinscripcion(guardada.getEmail(), nombre,
                    guardada.getCodigoFormulario(), pdf);
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
    public Preinscripcion habilitarComoAlumno(Long id, Long comisionId) {
        Preinscripcion pre = obtenerPorId(id);

        Comision comision = comisionRepository.findById(comisionId)
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada con ID: " + comisionId));

        verificarCupoComision(comision);

        if (pre.getAlumno() != null) {
            // Preinscripción ya tiene un alumno vinculado: solo actualizar
            Alumno alumno = pre.getAlumno();
            alumno.setHabilitado(true);
            alumno.setComision(comision);
            alumnoRepository.save(alumno);
        } else {
            // Buscar si ya existe un alumno con ese DNI para no duplicar
            Optional<Alumno> alumnoExistente = alumnoRepository.findByDni(pre.getDni());

            if (alumnoExistente.isPresent()) {
                Alumno alumno = alumnoExistente.get();
                alumno.setHabilitado(true);
                alumno.setComision(comision);
                alumnoRepository.save(alumno);
                pre.setAlumno(alumno);
            } else {
                Rol rolAlumno = rolRepository.findByNombre("ALUMNO")
                        .orElseThrow(() -> new RuntimeException("Rol ALUMNO no encontrado"));

                String token = UUID.randomUUID().toString();

                Usuario usuario = new Usuario();
                usuario.setUsername(pre.getEmail());
                usuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                usuario.setRol(rolAlumno);
                usuario.setTokenActivacion(token);
                usuario.setTokenActivacionExpiracion(LocalDateTime.now().plusHours(72));
                usuario = usuarioRepository.save(usuario);

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
                alumno.setComision(comision);
                alumno.setHabilitado(true);
                alumno.setUsuario(usuario);
                alumno = alumnoRepository.save(alumno);

                pre.setAlumno(alumno);

                try {
                    String nombre = pre.getNombre() + " " + pre.getApellido();
                    String carreraNombre = comision.getAnioCarrera() != null
                            && comision.getAnioCarrera().getCarrera() != null
                            ? comision.getAnioCarrera().getCarrera().getNombre()
                            : "la carrera seleccionada";
                    emailService.enviarActivacionCuenta(pre.getEmail(), nombre, carreraNombre, token);
                } catch (MailException e) {
                    log.error("No se pudo enviar email de activación a {}: {}", pre.getEmail(), e.getMessage());
                }
            }
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


    private void verificarCupoComision(Comision comision) {
        if (comision.getCupoMaximo() == 0) return;
        long ocupado = alumnoRepository.countByComisionId(comision.getId());
        if (ocupado >= comision.getCupoMaximo()) {
            throw new RuntimeException(
                "No hay cupos disponibles en la comisión \"" + comision.getNombre() + "\". "
                + "Capacidad máxima: " + comision.getCupoMaximo() + ", alumnos actuales: " + ocupado
            );
        }
    }
}
