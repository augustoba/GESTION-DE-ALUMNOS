package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.TipoDestinatarioMail;

import java.time.LocalDateTime;

public record EnvioMailResponse(
        Long id,
        String asunto,
        TipoDestinatarioMail destinatarioTipo,
        String carreraNombre,
        int totalEnviados,
        LocalDateTime enviadoEn,
        String enviadoPorEmail
) {}
