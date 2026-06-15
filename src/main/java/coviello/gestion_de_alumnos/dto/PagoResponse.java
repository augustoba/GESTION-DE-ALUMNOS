package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        EstadoPago estado,
        BigDecimal montoTotal,
        BigDecimal montoAbonado,
        LocalDateTime fechaUltimoPago
) {}
