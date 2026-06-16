package coviello.gestion_de_alumnos.dto;

public record ComisionRequest(
    String nombre,
    int cupoMaximo,
    String prefijoTurno,
    Boolean activa
) {}
