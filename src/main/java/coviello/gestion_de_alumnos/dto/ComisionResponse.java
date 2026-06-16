package coviello.gestion_de_alumnos.dto;

public record ComisionResponse(
    Long id,
    String nombre,
    int cupoMaximo,
    String prefijoTurno,
    Boolean activa,
    long alumnosCount
) {}
