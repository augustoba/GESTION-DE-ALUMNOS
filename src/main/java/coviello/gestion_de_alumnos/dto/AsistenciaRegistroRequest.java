package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AsistenciaRegistroRequest(
        @NotBlank String identificadorArduino,
        Integer sensorId,   // slot del sensor de huella en el Arduino (método HUELLA)
        String pin,         // PIN alternativo del alumno (método PIN)
        @NotNull String metodo  // "HUELLA" | "PIN"
) {}
