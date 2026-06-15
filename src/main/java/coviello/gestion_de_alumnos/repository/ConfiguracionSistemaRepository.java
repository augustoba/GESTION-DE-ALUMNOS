package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Long> {
}
