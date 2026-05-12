package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.EstadoPreinscripcion;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.repository.PreinscripcionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PreinscripcionService {

    private final PreinscripcionRepository preinscripcionRepository;
    private final EmailService emailService;

    public PreinscripcionService(PreinscripcionRepository preinscripcionRepository, EmailService emailService) {
        this.preinscripcionRepository = preinscripcionRepository;
        this.emailService = emailService;
    }

    public Preinscripcion guardar(Preinscripcion preinscripcion, MultipartFile comprobante) throws IOException {
        verificarCupo(preinscripcion);

        preinscripcion.setComprobantePago(comprobante.getBytes());
        preinscripcion.setFechaCreacion(LocalDateTime.now());
        preinscripcion.setPagoValidado(false);
        preinscripcion.setDocumentosCompletos(false);
        preinscripcion.setEstado(EstadoPreinscripcion.PENDIENTE_PAGO);
        return preinscripcionRepository.save(preinscripcion);
    }

    public List<Preinscripcion> obtenerTodas() {
        return preinscripcionRepository.findAll();
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
