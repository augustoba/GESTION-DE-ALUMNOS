package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @deprecated Usar DocumentoChecklistRepository o DocumentoDigitalRepository.
 */
@Deprecated
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
}
