package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.TurnoAsignado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TurnoAsignadoRepository extends JpaRepository<TurnoAsignado, Long> {

    Optional<TurnoAsignado> findByPreinscripcionId(Long preinscripcionId);

    Optional<TurnoAsignado> findByTokenConfirmacion(String token);

    // Cantidad de turnos ya asignados para una carrera en una configuración
    // (para calcular el próximo número de turno: A1, A2, A3...)
    long countByConfiguracionTurnoIdAndCarreraId(Long configuracionTurnoId, Long carreraId);
}
