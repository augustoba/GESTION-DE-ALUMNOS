package coviello.gestion_de_alumnos.dto;

public record ArduinoInfoResponse(
        Long id,
        String identificadorHardware,
        String tipo,
        Long aulaId,
        String aulaNombre,
        boolean activo
) {}
