package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.ConfiguracionAsistencia;
import coviello.gestion_de_alumnos.model.NivelConfiguracionAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionAsistenciaRepository extends JpaRepository<ConfiguracionAsistencia, Long> {

    Optional<ConfiguracionAsistencia> findFirstByAplicaA(NivelConfiguracionAsistencia nivel);

    Optional<ConfiguracionAsistencia> findByAplicaAAndCarreraId(
            NivelConfiguracionAsistencia nivel, Long carreraId);

    Optional<ConfiguracionAsistencia> findByAplicaAAndMateriaId(
            NivelConfiguracionAsistencia nivel, Long materiaId);
}
