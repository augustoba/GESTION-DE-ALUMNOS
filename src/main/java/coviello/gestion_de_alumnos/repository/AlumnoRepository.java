package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<Alumno,Long> {


}
