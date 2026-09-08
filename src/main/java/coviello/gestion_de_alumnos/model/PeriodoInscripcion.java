package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "periodo_inscripcion")
public class PeriodoInscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
    @Column(nullable = false)
    private boolean activo = false;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoPeriodoInscripcion tipo;

    public PeriodoInscripcion() {
    }

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public LocalDate getFechaInicio() {
        return this.fechaInicio;
    }

    public LocalDate getFechaFin() {
        return this.fechaFin;
    }

    public boolean isActivo() {
        return this.activo;
    }

    public TipoPeriodoInscripcion getTipo() {
        return this.tipo;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setFechaInicio(final LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(final LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setActivo(final boolean activo) {
        this.activo = activo;
    }

    public void setTipo(final TipoPeriodoInscripcion tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PeriodoInscripcion)) return false;
        final PeriodoInscripcion other = (PeriodoInscripcion) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isActivo() != other.isActivo()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$nombre = this.getNombre();
        final Object other$nombre = other.getNombre();
        if (this$nombre == null ? other$nombre != null : !this$nombre.equals(other$nombre)) return false;
        final Object this$fechaInicio = this.getFechaInicio();
        final Object other$fechaInicio = other.getFechaInicio();
        if (this$fechaInicio == null ? other$fechaInicio != null : !this$fechaInicio.equals(other$fechaInicio)) return false;
        final Object this$fechaFin = this.getFechaFin();
        final Object other$fechaFin = other.getFechaFin();
        if (this$fechaFin == null ? other$fechaFin != null : !this$fechaFin.equals(other$fechaFin)) return false;
        final Object this$tipo = this.getTipo();
        final Object other$tipo = other.getTipo();
        if (this$tipo == null ? other$tipo != null : !this$tipo.equals(other$tipo)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PeriodoInscripcion;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isActivo() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $nombre = this.getNombre();
        result = result * PRIME + ($nombre == null ? 43 : $nombre.hashCode());
        final Object $fechaInicio = this.getFechaInicio();
        result = result * PRIME + ($fechaInicio == null ? 43 : $fechaInicio.hashCode());
        final Object $fechaFin = this.getFechaFin();
        result = result * PRIME + ($fechaFin == null ? 43 : $fechaFin.hashCode());
        final Object $tipo = this.getTipo();
        result = result * PRIME + ($tipo == null ? 43 : $tipo.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "PeriodoInscripcion(id=" + this.getId() + ", nombre=" + this.getNombre() + ", fechaInicio=" + this.getFechaInicio() + ", fechaFin=" + this.getFechaFin() + ", activo=" + this.isActivo() + ", tipo=" + this.getTipo() + ")";
    }
}
