package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno,Long> {

    List<Alumno> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);

    Page<Alumno> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos, Pageable pageable);

    Optional<Alumno> findByDni(String dni);
    Optional<Alumno> findByCuil(String cuil);
    Optional<Alumno> findByEmail(String email);
}
