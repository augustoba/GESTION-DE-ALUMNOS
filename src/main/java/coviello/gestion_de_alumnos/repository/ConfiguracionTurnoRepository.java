package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.ConfiguracionTurno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionTurnoRepository extends JpaRepository<ConfiguracionTurno, Long> {

    Optional<ConfiguracionTurno> findFirstByActivoTrue();
}
