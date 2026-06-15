package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.DocumentoDigital;
import coviello.gestion_de_alumnos.model.EstadoDocumento;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DocumentoDigitalRepository extends JpaRepository<DocumentoDigital, Long> {

    List<DocumentoDigital> findByAlumnoId(Long alumnoId);

    Optional<DocumentoDigital> findByAlumnoIdAndTipoDocumento(Long alumnoId, TipoDocumento tipo);

    List<DocumentoDigital> findByAlumnoIdAndEstado(Long alumnoId, EstadoDocumento estado);

    // Alumnos que tienen al menos un documento en estado PENDIENTE (para mailing)
    @Query("SELECT DISTINCT d.alumno.id FROM DocumentoDigital d WHERE d.estado = 'PENDIENTE'")
    List<Long> findAlumnoIdsConDocumentosPendientes();
}
