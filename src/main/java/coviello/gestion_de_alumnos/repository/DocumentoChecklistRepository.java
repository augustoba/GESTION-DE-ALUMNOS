package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.DocumentoChecklist;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoChecklistRepository extends JpaRepository<DocumentoChecklist, Long> {

    List<DocumentoChecklist> findByPreinscripcionId(Long preinscripcionId);

    Optional<DocumentoChecklist> findByPreinscripcionIdAndTipoDocumento(
            Long preinscripcionId, TipoDocumento tipo);

    long countByPreinscripcionIdAndPresentadoTrue(Long preinscripcionId);

    long countByPreinscripcionIdAndPresentadoFalse(Long preinscripcionId);
}
