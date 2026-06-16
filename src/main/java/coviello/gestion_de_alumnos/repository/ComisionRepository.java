package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Comision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComisionRepository extends JpaRepository<Comision, Long> {
    List<Comision> findByAnioCarreraIdOrderByNombre(Long anioCarreraId);
    long countByAlumnosId(Long alumnoId);
}
