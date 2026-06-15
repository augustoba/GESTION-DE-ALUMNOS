package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.TipoDestinatarioMail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnvioMailRequest(
        @NotBlank String asunto,
        @NotBlank String cuerpo,
        @NotNull TipoDestinatarioMail destinatarioTipo,
        Long carreraId,
        Long anioCarreraId
) {}
