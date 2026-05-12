package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Documento;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByPreinscripcionId(Long preinscripcionId);

    Optional<Documento> findByPreinscripcionIdAndTipo(Long preinscripcionId, TipoDocumento tipo);

    // Cuenta cuántos documentos requeridos están validados para una preinscripción
    long countByPreinscripcionIdAndTipoInAndValidadoTrue(Long preinscripcionId, List<TipoDocumento> tipos);
}
