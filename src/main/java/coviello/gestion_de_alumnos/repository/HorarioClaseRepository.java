package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.DiaSemana;
import coviello.gestion_de_alumnos.model.HorarioClase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HorarioClaseRepository extends JpaRepository<HorarioClase, Long> {

    List<HorarioClase> findByMateriaId(Long materiaId);

    List<HorarioClase> findByDocenteId(Long docenteId);

    // Clase activa en un aula a una hora y día determinados (para el Arduino)
    @Query("""
           SELECT h FROM HorarioClase h
           WHERE h.aula.id = :aulaId
             AND h.diaSemana = :dia
             AND h.horaInicio <= :hora
             AND h.horaFin > :hora
             AND h.fechaInicioCursada <= :fecha
             AND h.fechaFinCursada >= :fecha
           """)
    Optional<HorarioClase> findClaseActivaEnAula(
            @Param("aulaId") Long aulaId,
            @Param("dia") DiaSemana dia,
            @Param("hora") LocalTime hora,
            @Param("fecha") LocalDate fecha);

    // Total de clases de una materia en el año (descontando feriados se hace en el servicio)
    @Query("""
           SELECT COUNT(DISTINCT h.id) FROM HorarioClase h
           WHERE h.materia.id = :materiaId
             AND h.fechaInicioCursada <= :hasta
             AND h.fechaFinCursada >= :desde
           """)
    long countClasesByMateriaAndPeriodo(
            @Param("materiaId") Long materiaId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}
