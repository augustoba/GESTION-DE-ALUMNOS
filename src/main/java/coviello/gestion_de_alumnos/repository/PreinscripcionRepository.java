package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.EstadoPreinscripcion;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreinscripcionRepository extends JpaRepository<Preinscripcion, Long> {

    Optional<Preinscripcion> findByCodigoFormulario(String codigoFormulario);

    Optional<Preinscripcion> findByDni(String dni);

    List<Preinscripcion> findByEstado(EstadoPreinscripcion estado);

    Page<Preinscripcion> findByEstado(EstadoPreinscripcion estado, Pageable pageable);

    Page<Preinscripcion> findByApellidoContainingIgnoreCaseOrNombreContainingIgnoreCase(
            String apellido, String nombre, Pageable pageable);

    long countByCarreraIdAndEstadoNot(Long carreraId, EstadoPreinscripcion estadoExcluido);
}
