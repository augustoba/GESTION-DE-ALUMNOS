package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.EstadoDocumento;
import coviello.gestion_de_alumnos.model.EstadoPreinscripcion;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PreinscripcionRepository extends JpaRepository<Preinscripcion, Long> {

    List<Preinscripcion> findByEmail(String email);

    List<Preinscripcion> findByDni(String dni);

    List<Preinscripcion> findByEstado(EstadoPreinscripcion estado);

    List<Preinscripcion> findByEstadoAndFechaCreacionBefore(
            EstadoPreinscripcion estado, LocalDateTime fechaLimite);

    long countByCarreraIdAndEstadoNot(Long carreraId, EstadoPreinscripcion estadoExcluido);

    @Query("SELECT DISTINCT p FROM Preinscripcion p JOIN p.documentos d WHERE d.estado = :estado")
    List<Preinscripcion> findDistinctByDocumentosEstado(@Param("estado") EstadoDocumento estado);

    List<Preinscripcion> findByCarreraIdInAndEstado(List<Long> carreraIds, EstadoPreinscripcion estado);
}
