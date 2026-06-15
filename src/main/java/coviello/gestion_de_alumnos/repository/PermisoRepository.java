package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.CodigoPermiso;
import coviello.gestion_de_alumnos.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    Optional<Permiso> findByCodigo(CodigoPermiso codigo);
}
