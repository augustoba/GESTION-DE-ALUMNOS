package coviello.gestion_de_alumnos.dto;

public record ArduinoAlumnoResponse(
        Long id,
        String nombres,
        String apellidos,
        String dni,
        boolean huellaRegistrada,
        Integer sensorId
) {}
