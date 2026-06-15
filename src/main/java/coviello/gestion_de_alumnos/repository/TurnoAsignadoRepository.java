package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.TurnoAsignado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TurnoAsignadoRepository extends JpaRepository<TurnoAsignado, Long> {

    // Primer turno de una preinscripción (compatibilidad con flujo admin)
    Optional<TurnoAsignado> findFirstByPreinscripcionId(Long preinscripcionId);

    // Todos los turnos de una preinscripción (puede tener uno por día si faltó)
    List<TurnoAsignado> findAllByPreinscripcionId(Long preinscripcionId);

    Optional<TurnoAsignado> findByTokenConfirmacion(String token);

    // Unicidad: una preinscripción no puede tener dos turnos el mismo día
    boolean existsByPreinscripcionIdAndConfiguracionTurnoId(Long preinscripcionId, Long configuracionTurnoId);

    // Cupo: total de turnos ya asignados para un día (configuracion)
    long countByConfiguracionTurnoId(Long configuracionTurnoId);

    // Para calcular el próximo número de turno por carrera y día
    long countByConfiguracionTurnoIdAndCarreraId(Long configuracionTurnoId, Long carreraId);
}
