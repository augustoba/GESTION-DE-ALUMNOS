package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno,Long> {

    List<Alumno> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
}
