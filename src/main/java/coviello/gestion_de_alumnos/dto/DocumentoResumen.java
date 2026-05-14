package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoDocumento;
import coviello.gestion_de_alumnos.model.TipoDocumento;

public record DocumentoResumen(
        Long id,
        Long preinscripcionId,
        TipoDocumento tipo,
        String nombreArchivo,
        String contentType,
        EstadoDocumento estado
) {}
