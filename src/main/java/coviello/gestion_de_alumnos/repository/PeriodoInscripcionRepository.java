package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.PeriodoInscripcion;
import coviello.gestion_de_alumnos.model.TipoPeriodoInscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeriodoInscripcionRepository extends JpaRepository<PeriodoInscripcion, Long> {

    Optional<PeriodoInscripcion> findFirstByActivoTrueAndTipo(TipoPeriodoInscripcion tipo);

    boolean existsByActivoTrueAndTipo(TipoPeriodoInscripcion tipo);
}
