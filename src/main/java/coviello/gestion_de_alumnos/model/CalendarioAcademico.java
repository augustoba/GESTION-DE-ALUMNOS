package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "calendario_academico")
public class CalendarioAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate fecha;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCalendario tipo;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "afecta_a", nullable = false, length = 20)
    private AlcanceCalendario afectaA = AlcanceCalendario.TODAS;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    public CalendarioAcademico() {
    }

    public Long getId() {
        return this.id;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public TipoCalendario getTipo() {
        return this.tipo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public AlcanceCalendario getAfectaA() {
        return this.afectaA;
    }

    public Carrera getCarrera() {
        return this.carrera;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setFecha(final LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setTipo(final TipoCalendario tipo) {
        this.tipo = tipo;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public void setAfectaA(final AlcanceCalendario afectaA) {
        this.afectaA = afectaA;
    }

    public void setCarrera(final Carrera carrera) {
        this.carrera = carrera;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CalendarioAcademico)) return false;
        final CalendarioAcademico other = (CalendarioAcademico) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$fecha = this.getFecha();
        final Object other$fecha = other.getFecha();
        if (this$fecha == null ? other$fecha != null : !this$fecha.equals(other$fecha)) return false;
        final Object this$tipo = this.getTipo();
        final Object other$tipo = other.getTipo();
        if (this$tipo == null ? other$tipo != null : !this$tipo.equals(other$tipo)) return false;
        final Object this$descripcion = this.getDescripcion();
        final Object other$descripcion = other.getDescripcion();
        if (this$descripcion == null ? other$descripcion != null : !this$descripcion.equals(other$descripcion)) return false;
        final Object this$afectaA = this.getAfectaA();
        final Object other$afectaA = other.getAfectaA();
        if (this$afectaA == null ? other$afectaA != null : !this$afectaA.equals(other$afectaA)) return false;
        final Object this$carrera = this.getCarrera();
        final Object other$carrera = other.getCarrera();
        if (this$carrera == null ? other$carrera != null : !this$carrera.equals(other$carrera)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CalendarioAcademico;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $fecha = this.getFecha();
        result = result * PRIME + ($fecha == null ? 43 : $fecha.hashCode());
        final Object $tipo = this.getTipo();
        result = result * PRIME + ($tipo == null ? 43 : $tipo.hashCode());
        final Object $descripcion = this.getDescripcion();
        result = result * PRIME + ($descripcion == null ? 43 : $descripcion.hashCode());
        final Object $afectaA = this.getAfectaA();
        result = result * PRIME + ($afectaA == null ? 43 : $afectaA.hashCode());
        final Object $carrera = this.getCarrera();
        result = result * PRIME + ($carrera == null ? 43 : $carrera.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CalendarioAcademico(id=" + this.getId() + ", fecha=" + this.getFecha() + ", tipo=" + this.getTipo() + ", descripcion=" + this.getDescripcion() + ", afectaA=" + this.getAfectaA() + ", carrera=" + this.getCarrera() + ")";
    }
}
