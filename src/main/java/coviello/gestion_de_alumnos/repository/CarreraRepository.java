package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    // Buscar carreras activas
    List<Carrera> findByActivaTrue();

    // Buscar por nombre (ignorando mayúsculas)
    List<Carrera> findByNombreContainingIgnoreCase(String nombre);
}
