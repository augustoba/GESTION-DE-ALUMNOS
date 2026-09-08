package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "horario_clase")
public class HorarioClase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comision_id")
    private Comision comision;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id")
    private Aula aula;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DiaSemana diaSemana;
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    @Column(name = "fecha_inicio_cursada")
    private LocalDate fechaInicioCursada;
    @Column(name = "fecha_fin_cursada")
    private LocalDate fechaFinCursada;

    public HorarioClase() {
    }

    public Long getId() {
        return this.id;
    }

    public Materia getMateria() {
        return this.materia;
    }

    public Comision getComision() {
        return this.comision;
    }

    public Aula getAula() {
        return this.aula;
    }

    public Docente getDocente() {
        return this.docente;
    }

    public DiaSemana getDiaSemana() {
        return this.diaSemana;
    }

    public LocalTime getHoraInicio() {
        return this.horaInicio;
    }

    public LocalTime getHoraFin() {
        return this.horaFin;
    }

    public LocalDate getFechaInicioCursada() {
        return this.fechaInicioCursada;
    }

    public LocalDate getFechaFinCursada() {
        return this.fechaFinCursada;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setMateria(final Materia materia) {
        this.materia = materia;
    }

    public void setComision(final Comision comision) {
        this.comision = comision;
    }

    public void setAula(final Aula aula) {
        this.aula = aula;
    }

    public void setDocente(final Docente docente) {
        this.docente = docente;
    }

    public void setDiaSemana(final DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public void setHoraInicio(final LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setHoraFin(final LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setFechaInicioCursada(final LocalDate fechaInicioCursada) {
        this.fechaInicioCursada = fechaInicioCursada;
    }

    public void setFechaFinCursada(final LocalDate fechaFinCursada) {
        this.fechaFinCursada = fechaFinCursada;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof HorarioClase)) return false;
        final HorarioClase other = (HorarioClase) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$materia = this.getMateria();
        final Object other$materia = other.getMateria();
        if (this$materia == null ? other$materia != null : !this$materia.equals(other$materia)) return false;
        final Object this$comision = this.getComision();
        final Object other$comision = other.getComision();
        if (this$comision == null ? other$comision != null : !this$comision.equals(other$comision)) return false;
        final Object this$aula = this.getAula();
        final Object other$aula = other.getAula();
        if (this$aula == null ? other$aula != null : !this$aula.equals(other$aula)) return false;
        final Object this$docente = this.getDocente();
        final Object other$docente = other.getDocente();
        if (this$docente == null ? other$docente != null : !this$docente.equals(other$docente)) return false;
        final Object this$diaSemana = this.getDiaSemana();
        final Object other$diaSemana = other.getDiaSemana();
        if (this$diaSemana == null ? other$diaSemana != null : !this$diaSemana.equals(other$diaSemana)) return false;
        final Object this$horaInicio = this.getHoraInicio();
        final Object other$horaInicio = other.getHoraInicio();
        if (this$horaInicio == null ? other$horaInicio != null : !this$horaInicio.equals(other$horaInicio)) return false;
        final Object this$horaFin = this.getHoraFin();
        final Object other$horaFin = other.getHoraFin();
        if (this$horaFin == null ? other$horaFin != null : !this$horaFin.equals(other$horaFin)) return false;
        final Object this$fechaInicioCursada = this.getFechaInicioCursada();
        final Object other$fechaInicioCursada = other.getFechaInicioCursada();
        if (this$fechaInicioCursada == null ? other$fechaInicioCursada != null : !this$fechaInicioCursada.equals(other$fechaInicioCursada)) return false;
        final Object this$fechaFinCursada = this.getFechaFinCursada();
        final Object other$fechaFinCursada = other.getFechaFinCursada();
        if (this$fechaFinCursada == null ? other$fechaFinCursada != null : !this$fechaFinCursada.equals(other$fechaFinCursada)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HorarioClase;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $materia = this.getMateria();
        result = result * PRIME + ($materia == null ? 43 : $materia.hashCode());
        final Object $comision = this.getComision();
        result = result * PRIME + ($comision == null ? 43 : $comision.hashCode());
        final Object $aula = this.getAula();
        result = result * PRIME + ($aula == null ? 43 : $aula.hashCode());
        final Object $docente = this.getDocente();
        result = result * PRIME + ($docente == null ? 43 : $docente.hashCode());
        final Object $diaSemana = this.getDiaSemana();
        result = result * PRIME + ($diaSemana == null ? 43 : $diaSemana.hashCode());
        final Object $horaInicio = this.getHoraInicio();
        result = result * PRIME + ($horaInicio == null ? 43 : $horaInicio.hashCode());
        final Object $horaFin = this.getHoraFin();
        result = result * PRIME + ($horaFin == null ? 43 : $horaFin.hashCode());
        final Object $fechaInicioCursada = this.getFechaInicioCursada();
        result = result * PRIME + ($fechaInicioCursada == null ? 43 : $fechaInicioCursada.hashCode());
        final Object $fechaFinCursada = this.getFechaFinCursada();
        result = result * PRIME + ($fechaFinCursada == null ? 43 : $fechaFinCursada.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "HorarioClase(id=" + this.getId() + ", materia=" + this.getMateria() + ", comision=" + this.getComision() + ", aula=" + this.getAula() + ", docente=" + this.getDocente() + ", diaSemana=" + this.getDiaSemana() + ", horaInicio=" + this.getHoraInicio() + ", horaFin=" + this.getHoraFin() + ", fechaInicioCursada=" + this.getFechaInicioCursada() + ", fechaFinCursada=" + this.getFechaFinCursada() + ")";
    }
}
