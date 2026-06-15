package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.EstadoInscripcionMateria;
import coviello.gestion_de_alumnos.model.InscripcionMateria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionMateriaRepository extends JpaRepository<InscripcionMateria, Long> {

    List<InscripcionMateria> findByAlumnoId(Long alumnoId);

    List<InscripcionMateria> findByAlumnoIdAndPeriodoInscripcionId(Long alumnoId, Long periodoId);

    List<InscripcionMateria> findByMateriaIdAndEstado(Long materiaId, EstadoInscripcionMateria estado);

    List<InscripcionMateria> findByAlumnoIdAndEstado(Long alumnoId, EstadoInscripcionMateria estado);

    boolean existsByAlumnoIdAndMateriaIdAndPeriodoInscripcionId(
            Long alumnoId, Long materiaId, Long periodoId);
}
