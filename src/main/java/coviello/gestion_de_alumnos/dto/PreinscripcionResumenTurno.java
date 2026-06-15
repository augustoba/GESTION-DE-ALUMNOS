package coviello.gestion_de_alumnos.dto;

public record PreinscripcionResumenTurno(
        Long id,
        String codigoFormulario,
        String nombre,
        String apellido,
        String carreraNombre
) {}
