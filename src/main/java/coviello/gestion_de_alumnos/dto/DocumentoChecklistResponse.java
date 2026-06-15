package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.TipoDocumento;

import java.time.LocalDateTime;

public record DocumentoChecklistResponse(
        Long id,
        TipoDocumento tipoDocumento,
        boolean presentado,
        LocalDateTime fechaPresentacion
) {}
