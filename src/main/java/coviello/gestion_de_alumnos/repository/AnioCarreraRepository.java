package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.AnioCarrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnioCarreraRepository extends JpaRepository<AnioCarrera, Long> {
    List<AnioCarrera> findByCarreraIdOrderByNumeroAnio(Long carreraId);
    boolean existsByCarreraIdAndNumeroAnio(Long carreraId, int numeroAnio);
}
