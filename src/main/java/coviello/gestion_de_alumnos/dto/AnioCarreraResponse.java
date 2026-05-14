package coviello.gestion_de_alumnos.dto;

import java.util.List;

public record AnioCarreraResponse(Long id, int numeroAnio, List<MateriaResponse> materias) {}
