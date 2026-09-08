package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "turno_asignado")
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

    public TurnoAsignado() {
    }

    public Long getId() {
        return this.id;
    }

    public Preinscripcion getPreinscripcion() {
        return this.preinscripcion;
    }

    public ConfiguracionTurno getConfiguracionTurno() {
        return this.configuracionTurno;
    }

    public Carrera getCarrera() {
        return this.carrera;
    }

    public String getNumeroTurno() {
        return this.numeroTurno;
    }

    public LocalTime getHoraAsignada() {
        return this.horaAsignada;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }

    public LocalDateTime getFechaConfirmacion() {
        return this.fechaConfirmacion;
    }

    public String getTokenConfirmacion() {
        return this.tokenConfirmacion;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setPreinscripcion(final Preinscripcion preinscripcion) {
        this.preinscripcion = preinscripcion;
    }

    public void setConfiguracionTurno(final ConfiguracionTurno configuracionTurno) {
        this.configuracionTurno = configuracionTurno;
    }

    public void setCarrera(final Carrera carrera) {
        this.carrera = carrera;
    }

    public void setNumeroTurno(final String numeroTurno) {
        this.numeroTurno = numeroTurno;
    }

    public void setHoraAsignada(final LocalTime horaAsignada) {
        this.horaAsignada = horaAsignada;
    }

    public void setConfirmado(final boolean confirmado) {
        this.confirmado = confirmado;
    }

    public void setFechaConfirmacion(final LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public void setTokenConfirmacion(final String tokenConfirmacion) {
        this.tokenConfirmacion = tokenConfirmacion;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TurnoAsignado)) return false;
        final TurnoAsignado other = (TurnoAsignado) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isConfirmado() != other.isConfirmado()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$preinscripcion = this.getPreinscripcion();
        final Object other$preinscripcion = other.getPreinscripcion();
        if (this$preinscripcion == null ? other$preinscripcion != null : !this$preinscripcion.equals(other$preinscripcion)) return false;
        final Object this$configuracionTurno = this.getConfiguracionTurno();
        final Object other$configuracionTurno = other.getConfiguracionTurno();
        if (this$configuracionTurno == null ? other$configuracionTurno != null : !this$configuracionTurno.equals(other$configuracionTurno)) return false;
        final Object this$carrera = this.getCarrera();
        final Object other$carrera = other.getCarrera();
        if (this$carrera == null ? other$carrera != null : !this$carrera.equals(other$carrera)) return false;
        final Object this$numeroTurno = this.getNumeroTurno();
        final Object other$numeroTurno = other.getNumeroTurno();
        if (this$numeroTurno == null ? other$numeroTurno != null : !this$numeroTurno.equals(other$numeroTurno)) return false;
        final Object this$horaAsignada = this.getHoraAsignada();
        final Object other$horaAsignada = other.getHoraAsignada();
        if (this$horaAsignada == null ? other$horaAsignada != null : !this$horaAsignada.equals(other$horaAsignada)) return false;
        final Object this$fechaConfirmacion = this.getFechaConfirmacion();
        final Object other$fechaConfirmacion = other.getFechaConfirmacion();
        if (this$fechaConfirmacion == null ? other$fechaConfirmacion != null : !this$fechaConfirmacion.equals(other$fechaConfirmacion)) return false;
        final Object this$tokenConfirmacion = this.getTokenConfirmacion();
        final Object other$tokenConfirmacion = other.getTokenConfirmacion();
        if (this$tokenConfirmacion == null ? other$tokenConfirmacion != null : !this$tokenConfirmacion.equals(other$tokenConfirmacion)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TurnoAsignado;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isConfirmado() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $preinscripcion = this.getPreinscripcion();
        result = result * PRIME + ($preinscripcion == null ? 43 : $preinscripcion.hashCode());
        final Object $configuracionTurno = this.getConfiguracionTurno();
        result = result * PRIME + ($configuracionTurno == null ? 43 : $configuracionTurno.hashCode());
        final Object $carrera = this.getCarrera();
        result = result * PRIME + ($carrera == null ? 43 : $carrera.hashCode());
        final Object $numeroTurno = this.getNumeroTurno();
        result = result * PRIME + ($numeroTurno == null ? 43 : $numeroTurno.hashCode());
        final Object $horaAsignada = this.getHoraAsignada();
        result = result * PRIME + ($horaAsignada == null ? 43 : $horaAsignada.hashCode());
        final Object $fechaConfirmacion = this.getFechaConfirmacion();
        result = result * PRIME + ($fechaConfirmacion == null ? 43 : $fechaConfirmacion.hashCode());
        final Object $tokenConfirmacion = this.getTokenConfirmacion();
        result = result * PRIME + ($tokenConfirmacion == null ? 43 : $tokenConfirmacion.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "TurnoAsignado(id=" + this.getId() + ", preinscripcion=" + this.getPreinscripcion() + ", configuracionTurno=" + this.getConfiguracionTurno() + ", carrera=" + this.getCarrera() + ", numeroTurno=" + this.getNumeroTurno() + ", horaAsignada=" + this.getHoraAsignada() + ", confirmado=" + this.isConfirmado() + ", fechaConfirmacion=" + this.getFechaConfirmacion() + ", tokenConfirmacion=" + this.getTokenConfirmacion() + ")";
    }
}
