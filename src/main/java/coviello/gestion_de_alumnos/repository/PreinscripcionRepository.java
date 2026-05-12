package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.EstadoPreinscripcion;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PreinscripcionRepository extends JpaRepository<Preinscripcion, Long> {

    List<Preinscripcion> findByEmail(String email);

    List<Preinscripcion> findByDni(String dni);

    List<Preinscripcion> findByEstado(EstadoPreinscripcion estado);

    // Para el scheduler: inscripciones pendientes de pago creadas hace más de 48hs
    List<Preinscripcion> findByEstadoAndFechaCreacionBefore(
            EstadoPreinscripcion estado, LocalDateTime fechaLimite);

    // Para el control de cupos: cuenta activas (todo excepto EXPIRADA)
    long countByCarreraIdAndEstadoNot(Long carreraId, EstadoPreinscripcion estadoExcluido);
}
