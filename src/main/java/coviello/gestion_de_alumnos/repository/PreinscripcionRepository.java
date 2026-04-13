package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Preinscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreinscripcionRepository extends JpaRepository<Preinscripcion, Long> {


    List<Preinscripcion> findByEmail(String email);


    List<Preinscripcion> findByDni(String dni);


    List<Preinscripcion> findByPagoValidadoTrue();


    List<Preinscripcion> findByDocumentosCompletosTrue();


    List<Preinscripcion> findByPagoValidadoIsNullOrPagoValidadoFalse();
}