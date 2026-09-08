package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "asistencia", uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "horario_clase_id", "fecha"}))
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

    public Asistencia() {
    }

    public Long getId() {
        return this.id;
    }

    public Alumno getAlumno() {
        return this.alumno;
    }

    public HorarioClase getHorarioClase() {
        return this.horarioClase;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public LocalTime getHoraRegistro() {
        return this.horaRegistro;
    }

    public EstadoAsistencia getEstado() {
        return this.estado;
    }

    public MetodoAsistencia getMetodo() {
        return this.metodo;
    }

    public Usuario getModificadoPor() {
        return this.modificadoPor;
    }

    public LocalDateTime getModificadoEn() {
        return this.modificadoEn;
    }

    public String getJustificacion() {
        return this.justificacion;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setAlumno(final Alumno alumno) {
        this.alumno = alumno;
    }

    public void setHorarioClase(final HorarioClase horarioClase) {
        this.horarioClase = horarioClase;
    }

    public void setFecha(final LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHoraRegistro(final LocalTime horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public void setEstado(final EstadoAsistencia estado) {
        this.estado = estado;
    }

    public void setMetodo(final MetodoAsistencia metodo) {
        this.metodo = metodo;
    }

    public void setModificadoPor(final Usuario modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public void setModificadoEn(final LocalDateTime modificadoEn) {
        this.modificadoEn = modificadoEn;
    }

    public void setJustificacion(final String justificacion) {
        this.justificacion = justificacion;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Asistencia)) return false;
        final Asistencia other = (Asistencia) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$alumno = this.getAlumno();
        final Object other$alumno = other.getAlumno();
        if (this$alumno == null ? other$alumno != null : !this$alumno.equals(other$alumno)) return false;
        final Object this$horarioClase = this.getHorarioClase();
        final Object other$horarioClase = other.getHorarioClase();
        if (this$horarioClase == null ? other$horarioClase != null : !this$horarioClase.equals(other$horarioClase)) return false;
        final Object this$fecha = this.getFecha();
        final Object other$fecha = other.getFecha();
        if (this$fecha == null ? other$fecha != null : !this$fecha.equals(other$fecha)) return false;
        final Object this$horaRegistro = this.getHoraRegistro();
        final Object other$horaRegistro = other.getHoraRegistro();
        if (this$horaRegistro == null ? other$horaRegistro != null : !this$horaRegistro.equals(other$horaRegistro)) return false;
        final Object this$estado = this.getEstado();
        final Object other$estado = other.getEstado();
        if (this$estado == null ? other$estado != null : !this$estado.equals(other$estado)) return false;
        final Object this$metodo = this.getMetodo();
        final Object other$metodo = other.getMetodo();
        if (this$metodo == null ? other$metodo != null : !this$metodo.equals(other$metodo)) return false;
        final Object this$modificadoPor = this.getModificadoPor();
        final Object other$modificadoPor = other.getModificadoPor();
        if (this$modificadoPor == null ? other$modificadoPor != null : !this$modificadoPor.equals(other$modificadoPor)) return false;
        final Object this$modificadoEn = this.getModificadoEn();
        final Object other$modificadoEn = other.getModificadoEn();
        if (this$modificadoEn == null ? other$modificadoEn != null : !this$modificadoEn.equals(other$modificadoEn)) return false;
        final Object this$justificacion = this.getJustificacion();
        final Object other$justificacion = other.getJustificacion();
        if (this$justificacion == null ? other$justificacion != null : !this$justificacion.equals(other$justificacion)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Asistencia;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $alumno = this.getAlumno();
        result = result * PRIME + ($alumno == null ? 43 : $alumno.hashCode());
        final Object $horarioClase = this.getHorarioClase();
        result = result * PRIME + ($horarioClase == null ? 43 : $horarioClase.hashCode());
        final Object $fecha = this.getFecha();
        result = result * PRIME + ($fecha == null ? 43 : $fecha.hashCode());
        final Object $horaRegistro = this.getHoraRegistro();
        result = result * PRIME + ($horaRegistro == null ? 43 : $horaRegistro.hashCode());
        final Object $estado = this.getEstado();
        result = result * PRIME + ($estado == null ? 43 : $estado.hashCode());
        final Object $metodo = this.getMetodo();
        result = result * PRIME + ($metodo == null ? 43 : $metodo.hashCode());
        final Object $modificadoPor = this.getModificadoPor();
        result = result * PRIME + ($modificadoPor == null ? 43 : $modificadoPor.hashCode());
        final Object $modificadoEn = this.getModificadoEn();
        result = result * PRIME + ($modificadoEn == null ? 43 : $modificadoEn.hashCode());
        final Object $justificacion = this.getJustificacion();
        result = result * PRIME + ($justificacion == null ? 43 : $justificacion.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Asistencia(id=" + this.getId() + ", alumno=" + this.getAlumno() + ", horarioClase=" + this.getHorarioClase() + ", fecha=" + this.getFecha() + ", horaRegistro=" + this.getHoraRegistro() + ", estado=" + this.getEstado() + ", metodo=" + this.getMetodo() + ", modificadoPor=" + this.getModificadoPor() + ", modificadoEn=" + this.getModificadoEn() + ", justificacion=" + this.getJustificacion() + ")";
    }
}
