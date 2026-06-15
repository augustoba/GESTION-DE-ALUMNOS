package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.AlcanceCalendario;
import coviello.gestion_de_alumnos.model.CalendarioAcademico;
import coviello.gestion_de_alumnos.model.TipoCalendario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CalendarioAcademicoRepository extends JpaRepository<CalendarioAcademico, Long> {

    List<CalendarioAcademico> findByFechaBetweenOrderByFecha(LocalDate desde, LocalDate hasta);

    List<CalendarioAcademico> findByTipo(TipoCalendario tipo);

    // Días no cursables en un rango (feriados o suspensiones globales + los de la carrera)
    @Query("""
           SELECT c FROM CalendarioAcademico c
           WHERE c.fecha BETWEEN :desde AND :hasta
             AND c.tipo IN ('FERIADO', 'SUSPENSION')
             AND (c.afectaA = 'TODAS'
                  OR (c.afectaA = 'CARRERA' AND c.carrera.id = :carreraId))
           ORDER BY c.fecha
           """)
    List<CalendarioAcademico> findDiasNoCursablesByCarreraAndRango(
            @Param("carreraId") Long carreraId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}
