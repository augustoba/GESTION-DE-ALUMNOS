package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AsistenciaRegistroRequest(
        @NotBlank String identificadorArduino,
        Long alumnoId,
        String pin,
        @NotNull String metodo  // "HUELLA" | "PIN"
) {}
