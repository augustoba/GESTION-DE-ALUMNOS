package coviello.gestion_de_alumnos.repository;

import coviello.gestion_de_alumnos.model.Asistencia;
import coviello.gestion_de_alumnos.model.EstadoAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByAlumnoIdAndHorarioClaseIdAndFecha(
            Long alumnoId, Long horarioClaseId, LocalDate fecha);

    List<Asistencia> findByHorarioClaseIdAndFecha(Long horarioClaseId, LocalDate fecha);

    List<Asistencia> findByAlumnoIdAndHorarioClaseMateriId(Long alumnoId, Long materiaId);

    // Cuenta presentes/tardanzas/ausentes por alumno y materia
    long countByAlumnoIdAndHorarioClaseMateriaIdAndEstado(
            Long alumnoId, Long materiaId, EstadoAsistencia estado);

    // Para la vista calendario del docente: asistencias de una materia en un rango de fechas
    @Query("""
           SELECT a FROM Asistencia a
           WHERE a.horarioClase.materia.id = :materiaId
             AND a.fecha BETWEEN :desde AND :hasta
           ORDER BY a.fecha
           """)
    List<Asistencia> findByMateriaAndRango(
            @Param("materiaId") Long materiaId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    // Resumen diario: cantidad de presentes en una materia en una fecha
    @Query("""
           SELECT COUNT(a) FROM Asistencia a
           WHERE a.horarioClase.materia.id = :materiaId
             AND a.fecha = :fecha
             AND a.estado = :estado
           """)
    long countByMateriaFechaEstado(
            @Param("materiaId") Long materiaId,
            @Param("fecha") LocalDate fecha,
            @Param("estado") EstadoAsistencia estado);
}
