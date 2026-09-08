package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripcion_materia", uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "materia_id", "periodo_inscripcion_id"}))
public class InscripcionMateria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_inscripcion_id", nullable = false)
    private PeriodoInscripcion periodoInscripcion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoInscripcionMateria estado = EstadoInscripcionMateria.SOLICITADA;
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();
    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resuelto_por")
    private Usuario resueltoPor;

    public InscripcionMateria() {
    }

    public Long getId() {
        return this.id;
    }

    public Alumno getAlumno() {
        return this.alumno;
    }

    public Materia getMateria() {
        return this.materia;
    }

    public PeriodoInscripcion getPeriodoInscripcion() {
        return this.periodoInscripcion;
    }

    public EstadoInscripcionMateria getEstado() {
        return this.estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return this.fechaSolicitud;
    }

    public LocalDateTime getFechaResolucion() {
        return this.fechaResolucion;
    }

    public Usuario getResueltoPor() {
        return this.resueltoPor;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setAlumno(final Alumno alumno) {
        this.alumno = alumno;
    }

    public void setMateria(final Materia materia) {
        this.materia = materia;
    }

    public void setPeriodoInscripcion(final PeriodoInscripcion periodoInscripcion) {
        this.periodoInscripcion = periodoInscripcion;
    }

    public void setEstado(final EstadoInscripcionMateria estado) {
        this.estado = estado;
    }

    public void setFechaSolicitud(final LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public void setFechaResolucion(final LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public void setResueltoPor(final Usuario resueltoPor) {
        this.resueltoPor = resueltoPor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof InscripcionMateria)) return false;
        final InscripcionMateria other = (InscripcionMateria) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$alumno = this.getAlumno();
        final Object other$alumno = other.getAlumno();
        if (this$alumno == null ? other$alumno != null : !this$alumno.equals(other$alumno)) return false;
        final Object this$materia = this.getMateria();
        final Object other$materia = other.getMateria();
        if (this$materia == null ? other$materia != null : !this$materia.equals(other$materia)) return false;
        final Object this$periodoInscripcion = this.getPeriodoInscripcion();
        final Object other$periodoInscripcion = other.getPeriodoInscripcion();
        if (this$periodoInscripcion == null ? other$periodoInscripcion != null : !this$periodoInscripcion.equals(other$periodoInscripcion)) return false;
        final Object this$estado = this.getEstado();
        final Object other$estado = other.getEstado();
        if (this$estado == null ? other$estado != null : !this$estado.equals(other$estado)) return false;
        final Object this$fechaSolicitud = this.getFechaSolicitud();
        final Object other$fechaSolicitud = other.getFechaSolicitud();
        if (this$fechaSolicitud == null ? other$fechaSolicitud != null : !this$fechaSolicitud.equals(other$fechaSolicitud)) return false;
        final Object this$fechaResolucion = this.getFechaResolucion();
        final Object other$fechaResolucion = other.getFechaResolucion();
        if (this$fechaResolucion == null ? other$fechaResolucion != null : !this$fechaResolucion.equals(other$fechaResolucion)) return false;
        final Object this$resueltoPor = this.getResueltoPor();
        final Object other$resueltoPor = other.getResueltoPor();
        if (this$resueltoPor == null ? other$resueltoPor != null : !this$resueltoPor.equals(other$resueltoPor)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof InscripcionMateria;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $alumno = this.getAlumno();
        result = result * PRIME + ($alumno == null ? 43 : $alumno.hashCode());
        final Object $materia = this.getMateria();
        result = result * PRIME + ($materia == null ? 43 : $materia.hashCode());
        final Object $periodoInscripcion = this.getPeriodoInscripcion();
        result = result * PRIME + ($periodoInscripcion == null ? 43 : $periodoInscripcion.hashCode());
        final Object $estado = this.getEstado();
        result = result * PRIME + ($estado == null ? 43 : $estado.hashCode());
        final Object $fechaSolicitud = this.getFechaSolicitud();
        result = result * PRIME + ($fechaSolicitud == null ? 43 : $fechaSolicitud.hashCode());
        final Object $fechaResolucion = this.getFechaResolucion();
        result = result * PRIME + ($fechaResolucion == null ? 43 : $fechaResolucion.hashCode());
        final Object $resueltoPor = this.getResueltoPor();
        result = result * PRIME + ($resueltoPor == null ? 43 : $resueltoPor.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "InscripcionMateria(id=" + this.getId() + ", alumno=" + this.getAlumno() + ", materia=" + this.getMateria() + ", periodoInscripcion=" + this.getPeriodoInscripcion() + ", estado=" + this.getEstado() + ", fechaSolicitud=" + this.getFechaSolicitud() + ", fechaResolucion=" + this.getFechaResolucion() + ", resueltoPor=" + this.getResueltoPor() + ")";
    }
}
