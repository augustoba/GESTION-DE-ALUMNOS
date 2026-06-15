package coviello.gestion_de_alumnos.dto;

import java.util.List;

public record InscripcionMateriaRequest(
        List<Long> materiaIds
) {}
