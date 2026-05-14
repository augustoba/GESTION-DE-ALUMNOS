package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {
    List<Docente> findByActivoTrue();
    Optional<Docente> findByUsuario_Username(String username);
}
