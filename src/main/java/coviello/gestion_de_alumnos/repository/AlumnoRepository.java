package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Page<Alumno> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
            String nombres, String apellidos, Pageable pageable);

    Optional<Alumno> findByDni(String dni);
    Optional<Alumno> findByEmail(String email);
    Optional<Alumno> findByUsuarioId(Long usuarioId);

    List<Alumno> findByComisionId(Long comisionId);
    List<Alumno> findByComision_AnioCarrera_CarreraId(Long carreraId);
    List<Alumno> findByComision_AnioCarreraId(Long anioCarreraId);
    List<Alumno> findByHabilitadoTrue();
    long countByComisionId(Long comisionId);
}
