package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoInscripcionMateria;

import java.time.LocalDateTime;

public record InscripcionMateriaResponse(
        Long id,
        Long alumnoId,
        String alumnoNombre,
        Long materiaId,
        String materiaNombre,
        String carreraNombre,
        int anioCarrera,
        EstadoInscripcionMateria estado,
        LocalDateTime fechaSolicitud
) {}
