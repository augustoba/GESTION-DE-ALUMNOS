package coviello.gestion_de_alumnos.dto;

public record AprobarRequest(
        Boolean tituloSecundario,
        Boolean constanciaTituloTramite,
        Boolean dni,
        Boolean foto,
        Boolean actaNacimiento,
        Boolean psicofisico,
        Boolean buenaConducta
) {}
