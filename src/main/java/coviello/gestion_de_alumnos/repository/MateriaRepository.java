package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    List<Materia> findByAnioCarreraIdOrderByNombre(Long anioCarreraId);
    List<Materia> findByDocente_Id(Long docenteId);
}
