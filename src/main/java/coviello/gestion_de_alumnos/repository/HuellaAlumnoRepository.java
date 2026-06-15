package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.HuellaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HuellaAlumnoRepository extends JpaRepository<HuellaAlumno, Long> {

    Optional<HuellaAlumno> findByAlumnoId(Long alumnoId);

    boolean existsByAlumnoId(Long alumnoId);
}
