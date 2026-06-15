package coviello.gestion_de_alumnos.dto;

import java.math.BigDecimal;

public record PagoRequest(
        BigDecimal montoTotal,
        BigDecimal montoAbonado
) {}
