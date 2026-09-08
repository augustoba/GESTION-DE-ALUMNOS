package coviello.gestion_de_alumnos.dto;

import jakarta.validation.constraints.NotNull;

public record ArduinoHuellaRequest(
        @NotNull Long alumnoId,
        @NotNull Integer sensorId,
        String pinAlternativo
) {}
