package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoPreinscripcion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PreinscripcionDetalleResponse(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String email,
        String telefono,
        String direccion,
        String localidad,
        LocalDate fechaNacimiento,
        String lugarNacimiento,
        String nacionalidad,
        String egresadoDe,
        String tituloDe,
        Boolean debeMaterias,
        String materiasAdeudadas,
        String afeccionEspecifica,
        String grupoSanguineo,
        String carrera,
        LocalDateTime fechaCreacion,
        EstadoPreinscripcion estado,
        Boolean documentosCompletos,
        Boolean reqTituloSecundario,
        Boolean reqConstanciaTituloTramite,
        Boolean reqDni,
        Boolean reqFoto,
        Boolean reqActaNacimiento,
        Boolean reqPsicofisico,
        Boolean reqBuenaConducta,
        List<DocumentoResumen> documentos
) {}
