package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AulaRepository extends JpaRepository<Aula, Long> {
}
