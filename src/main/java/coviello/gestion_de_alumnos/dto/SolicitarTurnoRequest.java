package coviello.gestion_de_alumnos.dto;

public record SolicitarTurnoRequest(
        Long configuracionTurnoId,
        Long preinscripcionId, // si se provee, salta la búsqueda por texto
        String tipoBusqueda,   // "CODIGO" | "DNI" | "NOMBRE"
        String valor,          // código (parcial) o DNI
        String nombre,         // solo si tipoBusqueda = NOMBRE
        String apellido        // solo si tipoBusqueda = NOMBRE
) {}
