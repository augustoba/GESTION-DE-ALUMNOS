package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.DocumentoResumen;
import coviello.gestion_de_alumnos.dto.PreinscripcionDetalleResponse;
import coviello.gestion_de_alumnos.dto.RevisionDocumentosRequest;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.Documento;
import coviello.gestion_de_alumnos.model.EstadoDocumento;
import coviello.gestion_de_alumnos.model.EstadoPreinscripcion;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.DocumentoRepository;
import coviello.gestion_de_alumnos.repository.PreinscripcionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PreinscripcionService {

    private static final List<TipoDocumento> DOCS_OBLIGATORIOS =
            List.of(TipoDocumento.DNI_FRENTE, TipoDocumento.DNI_DORSO,
                    TipoDocumento.TITULO, TipoDocumento.FOTO_CARNET);

    private final PreinscripcionRepository preinscripcionRepository;
    private final DocumentoRepository documentoRepository;
    private final AlumnoRepository alumnoRepository;
    private final EmailService emailService;

    public PreinscripcionService(PreinscripcionRepository preinscripcionRepository,
                                  DocumentoRepository documentoRepository,
                                  AlumnoRepository alumnoRepository,
                                  EmailService emailService) {
        this.preinscripcionRepository = preinscripcionRepository;
        this.documentoRepository = documentoRepository;
        this.alumnoRepository = alumnoRepository;
        this.emailService = emailService;
    }

    public Preinscripcion guardar(Preinscripcion preinscripcion,
                                   MultipartFile comprobante,
                                   MultipartFile dniFente,
                                   MultipartFile dniDorso,
                                   MultipartFile titulo,
                                   MultipartFile fotoCarnet) throws IOException {
        verificarCupo(preinscripcion);

        preinscripcion.setFechaCreacion(LocalDateTime.now());
        preinscripcion.setPagoValidado(false);
        preinscripcion.setDocumentosCompletos(false);
        preinscripcion.setEstado(EstadoPreinscripcion.PENDIENTE_PAGO);
        Preinscripcion guardada = preinscripcionRepository.save(preinscripcion);

        sincronizarAlumno(guardada);

        guardarDocumento(guardada, TipoDocumento.COMPROBANTE_PAGO, comprobante);
        guardarDocumento(guardada, TipoDocumento.DNI_FRENTE,       dniFente);
        guardarDocumento(guardada, TipoDocumento.DNI_DORSO,        dniDorso);
        guardarDocumento(guardada, TipoDocumento.TITULO,           titulo);
        guardarDocumento(guardada, TipoDocumento.FOTO_CARNET,      fotoCarnet);

        return guardada;
    }

    private void guardarDocumento(Preinscripcion preinscripcion, TipoDocumento tipo, MultipartFile archivo)
            throws IOException {
        Documento doc = new Documento();
        doc.setTipo(tipo);
        doc.setArchivo(archivo.getBytes());
        doc.setNombreArchivo(archivo.getOriginalFilename());
        doc.setContentType(archivo.getContentType());
        doc.setEstado(EstadoDocumento.PENDIENTE);
        doc.setPreinscripcion(preinscripcion);
        documentoRepository.save(doc);
    }

    public List<Preinscripcion> obtenerTodas() {
        return preinscripcionRepository.findAll();
    }

    public Page<Preinscripcion> obtenerTodasPaginadas(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").ascending());
        return preinscripcionRepository.findAll(pageable);
    }

    public PreinscripcionDetalleResponse obtenerDetalle(Long id) {
        Preinscripcion pre = obtenerPorId(id);

        List<Documento> documentos = documentoRepository.findByPreinscripcionId(id);
        List<DocumentoResumen> docsResumen = documentos.stream()
                .map(d -> new DocumentoResumen(d.getId(), id, d.getTipo(), d.getNombreArchivo(), d.getContentType(), d.getEstado()))
                .toList();

        String carrera = pre.getCarrera() != null ? pre.getCarrera().getNombre() : null;

        return new PreinscripcionDetalleResponse(
                pre.getId(), pre.getNombre(), pre.getApellido(), pre.getDni(),
                pre.getEmail(), pre.getTelefono(), pre.getDireccion(), pre.getFechaNacimiento(),
                carrera, pre.getFechaCreacion(), pre.getEstado(),
                pre.getPagoValidado(), pre.getDocumentosCompletos(), docsResumen
        );
    }

    public Preinscripcion obtenerPorId(Long id) {
        return preinscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada con ID: " + id));
    }

    public Preinscripcion validarPago(Long id) {
        Preinscripcion pre = obtenerPorId(id);
        pre.setPagoValidado(true);
        pre.setEstado(EstadoPreinscripcion.PAGO_VALIDADO);
        Preinscripcion guardada = preinscripcionRepository.save(pre);

        try {
            String nombreCompleto = pre.getNombre() + " " + pre.getApellido();
            String carrera = pre.getCarrera() != null ? pre.getCarrera().getNombre() : "la carrera seleccionada";
            emailService.enviarPagoValidado(pre.getEmail(), nombreCompleto, carrera);
        } catch (MailException e) {
            log.error("No se pudo enviar email de pago validado a {}: {}", pre.getEmail(), e.getMessage());
        }

        return guardada;
    }

    public Preinscripcion rechazarPago(Long id, String motivo) {
        Preinscripcion pre = obtenerPorId(id);
        pre.setPagoValidado(false);
        pre.setEstado(EstadoPreinscripcion.PENDIENTE_PAGO);
        Preinscripcion guardada = preinscripcionRepository.save(pre);

        try {
            String nombreCompleto = pre.getNombre() + " " + pre.getApellido();
            emailService.enviarPagoRechazado(pre.getEmail(), nombreCompleto, motivo);
        } catch (MailException e) {
            log.error("No se pudo enviar email de rechazo a {}: {}", pre.getEmail(), e.getMessage());
        }

        return guardada;
    }

    public List<Preinscripcion> obtenerPendientesPago() {
        return preinscripcionRepository.findByEstado(EstadoPreinscripcion.PENDIENTE_PAGO);
    }

    public List<Preinscripcion> buscarPorEmail(String email) {
        return preinscripcionRepository.findByEmail(email);
    }

    public List<Preinscripcion> obtenerConDocumentosPendientes() {
        return preinscripcionRepository.findDistinctByDocumentosEstado(EstadoDocumento.PENDIENTE);
    }

    public List<Preinscripcion> obtenerConDocumentosRechazados() {
        return preinscripcionRepository.findDistinctByDocumentosEstado(EstadoDocumento.RESUBIR);
    }

    public List<Preinscripcion> obtenerConDocumentosFaltantes() {
        return preinscripcionRepository.findAll().stream()
                .filter(p -> p.getEstado() != EstadoPreinscripcion.PENDIENTE_PAGO
                          && p.getEstado() != EstadoPreinscripcion.EXPIRADA)
                .filter(p -> documentoRepository.countByPreinscripcionIdAndTipoIn(
                        p.getId(), DOCS_OBLIGATORIOS) < DOCS_OBLIGATORIOS.size())
                .toList();
    }

    public Preinscripcion confirmarRevision(Long preinscripcionId,
                                            RevisionDocumentosRequest request) {
        Preinscripcion pre = obtenerPorId(preinscripcionId);

        for (RevisionDocumentosRequest.DecisionDocumento decision : request.decisiones()) {
            Documento doc = documentoRepository.findById(decision.documentoId())
                    .orElseThrow(() -> new RuntimeException(
                            "Documento no encontrado con ID: " + decision.documentoId()));
            doc.setEstado(decision.estado());
            documentoRepository.save(doc);
        }

        long validados = documentoRepository.countByPreinscripcionIdAndTipoInAndEstado(
                preinscripcionId, DOCS_OBLIGATORIOS, EstadoDocumento.VALIDADO);

        String nombreCompleto = pre.getNombre() + " " + pre.getApellido();
        String carrera = pre.getCarrera() != null ? pre.getCarrera().getNombre() : "la carrera seleccionada";

        if (validados >= DOCS_OBLIGATORIOS.size()) {
            pre.setEstado(EstadoPreinscripcion.APROBADA);
            pre.setDocumentosCompletos(true);
            preinscripcionRepository.save(pre);

            alumnoRepository.findByEmail(pre.getEmail()).ifPresent(alumno -> {
                alumno.setStatus(true);
                alumnoRepository.save(alumno);
            });

            try {
                emailService.enviarDocumentosAprobados(pre.getEmail(), nombreCompleto, carrera);
            } catch (MailException e) {
                log.error("No se pudo enviar email de aprobación a {}: {}", pre.getEmail(), e.getMessage());
            }
        } else {
            List<Documento> rechazados = documentoRepository
                    .findByPreinscripcionIdAndEstado(preinscripcionId, EstadoDocumento.RESUBIR);
            List<String> tiposRechazados = rechazados.stream()
                    .map(d -> d.getTipo().name())
                    .toList();

            try {
                emailService.enviarDocumentosRechazados(pre.getEmail(), nombreCompleto, carrera, tiposRechazados);
            } catch (MailException e) {
                log.error("No se pudo enviar email de documentos rechazados a {}: {}", pre.getEmail(), e.getMessage());
            }
        }

        return pre;
    }

    public void expirarPendientes() {
        LocalDateTime limite = LocalDateTime.now().minusHours(48);
        List<Preinscripcion> vencidas = preinscripcionRepository
                .findByEstadoAndFechaCreacionBefore(EstadoPreinscripcion.PENDIENTE_PAGO, limite);

        for (Preinscripcion pre : vencidas) {
            pre.setEstado(EstadoPreinscripcion.EXPIRADA);
            preinscripcionRepository.save(pre);
            log.info("Preinscripción {} expirada (creada: {})", pre.getId(), pre.getFechaCreacion());

            try {
                String nombreCompleto = pre.getNombre() + " " + pre.getApellido();
                String carrera = pre.getCarrera() != null ? pre.getCarrera().getNombre() : "la carrera seleccionada";
                emailService.enviarInscripcionExpirada(pre.getEmail(), nombreCompleto, carrera);
            } catch (MailException e) {
                log.error("No se pudo enviar email de expiración a {}: {}", pre.getEmail(), e.getMessage());
            }
        }

        if (!vencidas.isEmpty()) {
            log.info("Se expiraron {} preinscripciones vencidas", vencidas.size());
        }
    }

    private void sincronizarAlumno(Preinscripcion pre) {
        alumnoRepository.findByEmail(pre.getEmail()).ifPresent(alumno -> {
            if (pre.getTelefono()       != null) alumno.setTelefono(pre.getTelefono());
            if (pre.getDireccion()      != null) alumno.setDireccion(pre.getDireccion());
            if (pre.getFechaNacimiento()!= null) alumno.setFechaNac(pre.getFechaNacimiento());
            if (pre.getDni()            != null) alumno.setDni(pre.getDni());
            alumnoRepository.save(alumno);
        });
    }

    private void verificarCupo(Preinscripcion preinscripcion) {
        if (preinscripcion.getCarrera() == null) return;

        int cupoMaximo = preinscripcion.getCarrera().getCupoMaximo();
        if (cupoMaximo == 0) return;

        long activos = preinscripcionRepository.countByCarreraIdAndEstadoNot(
                preinscripcion.getCarrera().getId(), EstadoPreinscripcion.EXPIRADA);

        if (activos >= cupoMaximo) {
            throw new RuntimeException(
                    "No hay cupos disponibles para la carrera: " + preinscripcion.getCarrera().getNombre());
        }
    }
}
