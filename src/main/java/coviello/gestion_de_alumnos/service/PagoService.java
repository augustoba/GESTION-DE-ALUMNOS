package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.PagoRequest;
import coviello.gestion_de_alumnos.dto.PagoResponse;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PreinscripcionRepository preinscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    public PagoService(PagoRepository pagoRepository,
                       PreinscripcionRepository preinscripcionRepository,
                       UsuarioRepository usuarioRepository) {
        this.pagoRepository = pagoRepository;
        this.preinscripcionRepository = preinscripcionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PagoResponse obtenerPorPreinscripcion(Long preinscripcionId) {
        return pagoRepository.findByPreinscripcionId(preinscripcionId)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para preinscripción: " + preinscripcionId));
    }

    @Transactional
    public PagoResponse registrarPago(Long preinscripcionId, PagoRequest req, String adminUsername) {
        preinscripcionRepository.findById(preinscripcionId)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada: " + preinscripcionId));

        Pago pago = pagoRepository.findByPreinscripcionId(preinscripcionId)
                .orElseGet(() -> {
                    Pago nuevo = new Pago();
                    nuevo.setPreinscripcion(preinscripcionRepository.getReferenceById(preinscripcionId));
                    nuevo.setMontoAbonado(BigDecimal.ZERO);
                    return nuevo;
                });

        if (req.montoTotal() != null) pago.setMontoTotal(req.montoTotal());

        BigDecimal incremento = req.montoAbonado() != null ? req.montoAbonado() : BigDecimal.ZERO;
        BigDecimal nuevoAbonado = pago.getMontoAbonado().add(incremento);
        pago.setMontoAbonado(nuevoAbonado);
        pago.setFechaUltimoPago(LocalDateTime.now());

        usuarioRepository.findByUsername(adminUsername)
                .ifPresent(pago::setRegistradoPor);

        // Determinar estado en base al monto
        if (pago.getMontoTotal() != null && nuevoAbonado.compareTo(pago.getMontoTotal()) >= 0) {
            pago.setEstado(EstadoPago.COMPLETO);
        } else if (nuevoAbonado.compareTo(BigDecimal.ZERO) > 0) {
            pago.setEstado(EstadoPago.PARCIAL);
        } else {
            pago.setEstado(EstadoPago.SIN_PAGO);
        }

        return toResponse(pagoRepository.save(pago));
    }

    @Transactional
    public PagoResponse anularPago(Long preinscripcionId) {
        Pago pago = pagoRepository.findByPreinscripcionId(preinscripcionId)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado para preinscripción: " + preinscripcionId));
        pago.setEstado(EstadoPago.SIN_PAGO);
        pago.setMontoAbonado(BigDecimal.ZERO);
        pago.setFechaUltimoPago(null);
        return toResponse(pagoRepository.save(pago));
    }

    private PagoResponse toResponse(Pago p) {
        return new PagoResponse(p.getId(), p.getEstado(), p.getMontoTotal(),
                p.getMontoAbonado(), p.getFechaUltimoPago());
    }
}
