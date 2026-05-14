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
        LocalDate fechaNacimiento,
        String carrera,
        LocalDateTime fechaCreacion,
        EstadoPreinscripcion estado,
        Boolean pagoValidado,
        Boolean documentosCompletos,
        List<DocumentoResumen> documentos
) {}
