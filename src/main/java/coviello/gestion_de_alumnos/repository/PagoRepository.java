package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.EstadoPago;
import coviello.gestion_de_alumnos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPreinscripcionId(Long preinscripcionId);

    long countByEstado(EstadoPago estado);
}
