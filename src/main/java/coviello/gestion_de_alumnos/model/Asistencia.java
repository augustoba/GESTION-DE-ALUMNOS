package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "asistencia",
       uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "horario_clase_id", "fecha"}))
@Data
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horario_clase_id", nullable = false)
    private HorarioClase horarioClase;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_registro")
    private LocalTime horaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoAsistencia estado;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private MetodoAsistencia metodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modificado_por")
    private Usuario modificadoPor;

    @Column(name = "modificado_en")
    private LocalDateTime modificadoEn;

    @Column(length = 500)
    private String justificacion;
}
