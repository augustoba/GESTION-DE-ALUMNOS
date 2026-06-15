package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Arduino;
import coviello.gestion_de_alumnos.model.TipoArduino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArduinoRepository extends JpaRepository<Arduino, Long> {

    Optional<Arduino> findByIdentificadorHardware(String identificadorHardware);

    Optional<Arduino> findByAulaIdAndActivoTrue(Long aulaId);

    List<Arduino> findByTipoAndActivoTrue(TipoArduino tipo);
}
