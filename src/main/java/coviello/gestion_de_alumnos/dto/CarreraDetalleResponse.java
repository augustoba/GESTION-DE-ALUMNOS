package coviello.gestion_de_alumnos.dto;

import java.util.List;

public record CarreraDetalleResponse(
    Long id,
    String nombre,
    String descripcion,
    Boolean activa,
    int cupoMaximo,
    List<AnioCarreraResponse> anios
) {}
