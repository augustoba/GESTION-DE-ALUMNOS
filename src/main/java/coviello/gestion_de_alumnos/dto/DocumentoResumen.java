package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoDocumento;
import coviello.gestion_de_alumnos.model.TipoDocumento;

public record DocumentoResumen(
        Long id,
        TipoDocumento tipoDocumento,
        String archivoUrl,
        EstadoDocumento estado,
        String motivoRechazo
) {}
