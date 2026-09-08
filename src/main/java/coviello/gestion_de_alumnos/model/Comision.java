package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comision")
public class Comision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String nombre;
    @Column(name = "cupo_maximo")
    private int cupoMaximo = 0;
    @Column(name = "prefijo_turno", length = 5)
    private String prefijoTurno;
    private Boolean activa = true;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_carrera_id", nullable = false)
    private AnioCarrera anioCarrera;
    @JsonIgnore
    @OneToMany(mappedBy = "comision")
    private List<Alumno> alumnos = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "comision")
    private List<HorarioClase> horarios = new ArrayList<>();

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getCupoMaximo() {
        return this.cupoMaximo;
    }

    public String getPrefijoTurno() {
        return this.prefijoTurno;
    }

    public Boolean getActiva() {
        return this.activa;
    }

    public AnioCarrera getAnioCarrera() {
        return this.anioCarrera;
    }

    public List<Alumno> getAlumnos() {
        return this.alumnos;
    }

    public List<HorarioClase> getHorarios() {
        return this.horarios;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setCupoMaximo(final int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public void setPrefijoTurno(final String prefijoTurno) {
        this.prefijoTurno = prefijoTurno;
    }

    public void setActiva(final Boolean activa) {
        this.activa = activa;
    }

    public void setAnioCarrera(final AnioCarrera anioCarrera) {
        this.anioCarrera = anioCarrera;
    }

    public void setAlumnos(final List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public void setHorarios(final List<HorarioClase> horarios) {
        this.horarios = horarios;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Comision)) return false;
        final Comision other = (Comision) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getCupoMaximo() != other.getCupoMaximo()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$activa = this.getActiva();
        final Object other$activa = other.getActiva();
        if (this$activa == null ? other$activa != null : !this$activa.equals(other$activa)) return false;
        final Object this$nombre = this.getNombre();
        final Object other$nombre = other.getNombre();
        if (this$nombre == null ? other$nombre != null : !this$nombre.equals(other$nombre)) return false;
        final Object this$prefijoTurno = this.getPrefijoTurno();
        final Object other$prefijoTurno = other.getPrefijoTurno();
        if (this$prefijoTurno == null ? other$prefijoTurno != null : !this$prefijoTurno.equals(other$prefijoTurno)) return false;
        final Object this$anioCarrera = this.getAnioCarrera();
        final Object other$anioCarrera = other.getAnioCarrera();
        if (this$anioCarrera == null ? other$anioCarrera != null : !this$anioCarrera.equals(other$anioCarrera)) return false;
        final Object this$alumnos = this.getAlumnos();
        final Object other$alumnos = other.getAlumnos();
        if (this$alumnos == null ? other$alumnos != null : !this$alumnos.equals(other$alumnos)) return false;
        final Object this$horarios = this.getHorarios();
        final Object other$horarios = other.getHorarios();
        if (this$horarios == null ? other$horarios != null : !this$horarios.equals(other$horarios)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Comision;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getCupoMaximo();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $activa = this.getActiva();
        result = result * PRIME + ($activa == null ? 43 : $activa.hashCode());
        final Object $nombre = this.getNombre();
        result = result * PRIME + ($nombre == null ? 43 : $nombre.hashCode());
        final Object $prefijoTurno = this.getPrefijoTurno();
        result = result * PRIME + ($prefijoTurno == null ? 43 : $prefijoTurno.hashCode());
        final Object $anioCarrera = this.getAnioCarrera();
        result = result * PRIME + ($anioCarrera == null ? 43 : $anioCarrera.hashCode());
        final Object $alumnos = this.getAlumnos();
        result = result * PRIME + ($alumnos == null ? 43 : $alumnos.hashCode());
        final Object $horarios = this.getHorarios();
        result = result * PRIME + ($horarios == null ? 43 : $horarios.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Comision(id=" + this.getId() + ", nombre=" + this.getNombre() + ", cupoMaximo=" + this.getCupoMaximo() + ", prefijoTurno=" + this.getPrefijoTurno() + ", activa=" + this.getActiva() + ", anioCarrera=" + this.getAnioCarrera() + ", alumnos=" + this.getAlumnos() + ", horarios=" + this.getHorarios() + ")";
    }

    public Comision() {
    }
}
