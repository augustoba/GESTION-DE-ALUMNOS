package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turno_asignado")
@Data
public class TurnoAsignado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preinscripcion_id", nullable = false)
    private Preinscripcion preinscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracion_turno_id", nullable = false)
    private ConfiguracionTurno configuracionTurno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;

    @Column(name = "numero_turno", nullable = false, length = 10)
    private String numeroTurno; // A1, B3, C12...

    @Column(name = "hora_asignada", nullable = false)
    private LocalTime horaAsignada;

    @Column(nullable = false)
    private boolean confirmado = false;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "token_confirmacion", unique = true, length = 100)
    private String tokenConfirmacion;
}
