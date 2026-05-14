package coviello.gestion_de_alumnos.dto;

import coviello.gestion_de_alumnos.model.EstadoDocumento;

import java.util.List;

public record RevisionDocumentosRequest(List<DecisionDocumento> decisiones) {

    public record DecisionDocumento(Long documentoId, EstadoDocumento estado) {}
}
