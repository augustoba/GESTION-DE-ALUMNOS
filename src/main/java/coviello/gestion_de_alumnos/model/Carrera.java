package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    private Boolean activa = true;
    @JsonIgnore
    @OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroAnio ASC")
    private List<AnioCarrera> anios = new ArrayList<>();

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public Boolean getActiva() {
        return this.activa;
    }

    public List<AnioCarrera> getAnios() {
        return this.anios;
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

    public void setActiva(final Boolean activa) {
        this.activa = activa;
    }

    public void setAnios(final List<AnioCarrera> anios) {
        this.anios = anios;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Carrera)) return false;
        final Carrera other = (Carrera) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$activa = this.getActiva();
        final Object other$activa = other.getActiva();
        if (this$activa == null ? other$activa != null : !this$activa.equals(other$activa)) return false;
        final Object this$nombre = this.getNombre();
        final Object other$nombre = other.getNombre();
        if (this$nombre == null ? other$nombre != null : !this$nombre.equals(other$nombre)) return false;
        final Object this$descripcion = this.getDescripcion();
        final Object other$descripcion = other.getDescripcion();
        if (this$descripcion == null ? other$descripcion != null : !this$descripcion.equals(other$descripcion)) return false;
        final Object this$anios = this.getAnios();
        final Object other$anios = other.getAnios();
        if (this$anios == null ? other$anios != null : !this$anios.equals(other$anios)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Carrera;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $activa = this.getActiva();
        result = result * PRIME + ($activa == null ? 43 : $activa.hashCode());
        final Object $nombre = this.getNombre();
        result = result * PRIME + ($nombre == null ? 43 : $nombre.hashCode());
        final Object $descripcion = this.getDescripcion();
        result = result * PRIME + ($descripcion == null ? 43 : $descripcion.hashCode());
        final Object $anios = this.getAnios();
        result = result * PRIME + ($anios == null ? 43 : $anios.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Carrera(id=" + this.getId() + ", nombre=" + this.getNombre() + ", descripcion=" + this.getDescripcion() + ", activa=" + this.getActiva() + ", anios=" + this.getAnios() + ")";
    }

    public Carrera() {
    }
}
