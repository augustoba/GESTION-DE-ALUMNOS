package coviello.gestion_de_alumnos.service;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.repository.PreinscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PreinscripcionService {
    private final PreinscripcionRepository preinscripcionRepository;

    public PreinscripcionService(PreinscripcionRepository preinscripcionRepository) {
        this.preinscripcionRepository = preinscripcionRepository;
    }

    // Crear nueva preinscripción con comprobante
    public Preinscripcion crearPreinscripcion(Preinscripcion preinscripcion, MultipartFile comprobante) throws IOException {
        preinscripcion.setComprobantePago(comprobante.getBytes());
        preinscripcion.setFechaCreacion(LocalDateTime.now());
        preinscripcion.setPagoValidado(false);
        preinscripcion.setDocumentosCompletos(false);
        return preinscripcionRepository.save(preinscripcion);
    }

    // Obtener todas las preinscripciones
    public List<Preinscripcion> obtenerTodas() {
        return preinscripcionRepository.findAll();
    }

    // Obtener por ID
    public Preinscripcion obtenerPorId(Long id) {
        return preinscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada con ID: " + id));
    }

    // Validar pago (admin)
    public Preinscripcion validarPago(Long id) {
        Preinscripcion pre = obtenerPorId(id);
        pre.setPagoValidado(true);
        return preinscripcionRepository.save(pre);
    }

    // Marcar documentos como completos (admin)
    public Preinscripcion completarDocumentos(Long id) {
        Preinscripcion pre = obtenerPorId(id);
        pre.setDocumentosCompletos(true);
        return preinscripcionRepository.save(pre);
    }

    // Rechazar preinscripción (admin)
    public void rechazarPreinscripcion(Long id) {
        preinscripcionRepository.deleteById(id);
    }

    // Buscar por email
    public List<Preinscripcion> buscarPorEmail(String email) {
        return preinscripcionRepository.findByEmail(email);
    }

    // Obtener preinscripciones pendientes de validación de pago
    public List<Preinscripcion> obtenerPendientesPago() {
        return preinscripcionRepository.findByPagoValidadoIsNullOrPagoValidadoFalse();
    }

    public Preinscripcion guardar(Preinscripcion preinscripcion, MultipartFile comprobante) throws IOException {
        preinscripcion.setComprobantePago(comprobante.getBytes());
        preinscripcion.setFechaCreacion(LocalDateTime.now());
        preinscripcion.setPagoValidado(false);
        preinscripcion.setDocumentosCompletos(false);
        return preinscripcionRepository.save(preinscripcion);
    }

    // Buscar por ID
    public Preinscripcion buscarPorId(Long id) {
        return preinscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrada"));
    }

}

