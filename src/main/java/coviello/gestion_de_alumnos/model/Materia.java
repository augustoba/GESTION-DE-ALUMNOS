package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_carrera_id")
    private AnioCarrera anioCarrera;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;
    @JsonIgnore
    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioClase> horarios = new ArrayList<>();

    public Materia() {
    }

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public AnioCarrera getAnioCarrera() {
        return this.anioCarrera;
    }

    public Docente getDocente() {
        return this.docente;
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

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public void setAnioCarrera(final AnioCarrera anioCarrera) {
        this.anioCarrera = anioCarrera;
    }

    public void setDocente(final Docente docente) {
        this.docente = docente;
    }

    public void setHorarios(final List<HorarioClase> horarios) {
        this.horarios = horarios;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Materia)) return false;
        final Materia other = (Materia) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$nombre = this.getNombre();
        final Object other$nombre = other.getNombre();
        if (this$nombre == null ? other$nombre != null : !this$nombre.equals(other$nombre)) return false;
        final Object this$descripcion = this.getDescripcion();
        final Object other$descripcion = other.getDescripcion();
        if (this$descripcion == null ? other$descripcion != null : !this$descripcion.equals(other$descripcion)) return false;
        final Object this$anioCarrera = this.getAnioCarrera();
        final Object other$anioCarrera = other.getAnioCarrera();
        if (this$anioCarrera == null ? other$anioCarrera != null : !this$anioCarrera.equals(other$anioCarrera)) return false;
        final Object this$docente = this.getDocente();
        final Object other$docente = other.getDocente();
        if (this$docente == null ? other$docente != null : !this$docente.equals(other$docente)) return false;
        final Object this$horarios = this.getHorarios();
        final Object other$horarios = other.getHorarios();
        if (this$horarios == null ? other$horarios != null : !this$horarios.equals(other$horarios)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Materia;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $nombre = this.getNombre();
        result = result * PRIME + ($nombre == null ? 43 : $nombre.hashCode());
        final Object $descripcion = this.getDescripcion();
        result = result * PRIME + ($descripcion == null ? 43 : $descripcion.hashCode());
        final Object $anioCarrera = this.getAnioCarrera();
        result = result * PRIME + ($anioCarrera == null ? 43 : $anioCarrera.hashCode());
        final Object $docente = this.getDocente();
        result = result * PRIME + ($docente == null ? 43 : $docente.hashCode());
        final Object $horarios = this.getHorarios();
        result = result * PRIME + ($horarios == null ? 43 : $horarios.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Materia(id=" + this.getId() + ", nombre=" + this.getNombre() + ", descripcion=" + this.getDescripcion() + ", anioCarrera=" + this.getAnioCarrera() + ", docente=" + this.getDocente() + ", horarios=" + this.getHorarios() + ")";
    }
}
