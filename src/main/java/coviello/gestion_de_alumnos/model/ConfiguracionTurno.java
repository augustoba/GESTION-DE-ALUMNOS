package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "configuracion_turno")
@Data
public class ConfiguracionTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fin", nullable = false)
    private LocalTime horarioFin;

    @Column(name = "intervalo_minutos", nullable = false)
    private int intervaloMinutos = 5;

    @Column(name = "cupo_maximo", nullable = false)
    private int cupoMaximo = 100;

    @Column(nullable = false)
    private boolean activo = false;
}
