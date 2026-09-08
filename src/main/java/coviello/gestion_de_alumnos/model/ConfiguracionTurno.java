package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "configuracion_turno")
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

    public ConfiguracionTurno() {
    }

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public LocalTime getHorarioInicio() {
        return this.horarioInicio;
    }

    public LocalTime getHorarioFin() {
        return this.horarioFin;
    }

    public int getIntervaloMinutos() {
        return this.intervaloMinutos;
    }

    public int getCupoMaximo() {
        return this.cupoMaximo;
    }

    public boolean isActivo() {
        return this.activo;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(final LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHorarioInicio(final LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public void setHorarioFin(final LocalTime horarioFin) {
        this.horarioFin = horarioFin;
    }

    public void setIntervaloMinutos(final int intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    public void setCupoMaximo(final int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public void setActivo(final boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ConfiguracionTurno)) return false;
        final ConfiguracionTurno other = (ConfiguracionTurno) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getIntervaloMinutos() != other.getIntervaloMinutos()) return false;
        if (this.getCupoMaximo() != other.getCupoMaximo()) return false;
        if (this.isActivo() != other.isActivo()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$nombre = this.getNombre();
        final Object other$nombre = other.getNombre();
        if (this$nombre == null ? other$nombre != null : !this$nombre.equals(other$nombre)) return false;
        final Object this$fecha = this.getFecha();
        final Object other$fecha = other.getFecha();
        if (this$fecha == null ? other$fecha != null : !this$fecha.equals(other$fecha)) return false;
        final Object this$horarioInicio = this.getHorarioInicio();
        final Object other$horarioInicio = other.getHorarioInicio();
        if (this$horarioInicio == null ? other$horarioInicio != null : !this$horarioInicio.equals(other$horarioInicio)) return false;
        final Object this$horarioFin = this.getHorarioFin();
        final Object other$horarioFin = other.getHorarioFin();
        if (this$horarioFin == null ? other$horarioFin != null : !this$horarioFin.equals(other$horarioFin)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ConfiguracionTurno;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getIntervaloMinutos();
        result = result * PRIME + this.getCupoMaximo();
        result = result * PRIME + (this.isActivo() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $nombre = this.getNombre();
        result = result * PRIME + ($nombre == null ? 43 : $nombre.hashCode());
        final Object $fecha = this.getFecha();
        result = result * PRIME + ($fecha == null ? 43 : $fecha.hashCode());
        final Object $horarioInicio = this.getHorarioInicio();
        result = result * PRIME + ($horarioInicio == null ? 43 : $horarioInicio.hashCode());
        final Object $horarioFin = this.getHorarioFin();
        result = result * PRIME + ($horarioFin == null ? 43 : $horarioFin.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ConfiguracionTurno(id=" + this.getId() + ", nombre=" + this.getNombre() + ", fecha=" + this.getFecha() + ", horarioInicio=" + this.getHorarioInicio() + ", horarioFin=" + this.getHorarioFin() + ", intervaloMinutos=" + this.getIntervaloMinutos() + ", cupoMaximo=" + this.getCupoMaximo() + ", activo=" + this.isActivo() + ")";
    }
}
