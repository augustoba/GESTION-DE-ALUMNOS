package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombre;
    private Integer capacidad;
    private String descripcion;

    public Aula() {
    }

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Integer getCapacidad() {
        return this.capacidad;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setCapacidad(final Integer capacidad) {
        this.capacidad = capacidad;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Aula)) return false;
        final Aula other = (Aula) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$capacidad = this.getCapacidad();
        final Object other$capacidad = other.getCapacidad();
        if (this$capacidad == null ? other$capacidad != null : !this$capacidad.equals(other$capacidad)) return false;
        final Object this$nombre = this.getNombre();
        final Object other$nombre = other.getNombre();
        if (this$nombre == null ? other$nombre != null : !this$nombre.equals(other$nombre)) return false;
        final Object this$descripcion = this.getDescripcion();
        final Object other$descripcion = other.getDescripcion();
        if (this$descripcion == null ? other$descripcion != null : !this$descripcion.equals(other$descripcion)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Aula;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $capacidad = this.getCapacidad();
        result = result * PRIME + ($capacidad == null ? 43 : $capacidad.hashCode());
        final Object $nombre = this.getNombre();
        result = result * PRIME + ($nombre == null ? 43 : $nombre.hashCode());
        final Object $descripcion = this.getDescripcion();
        result = result * PRIME + ($descripcion == null ? 43 : $descripcion.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Aula(id=" + this.getId() + ", nombre=" + this.getNombre() + ", capacidad=" + this.getCapacidad() + ", descripcion=" + this.getDescripcion() + ")";
    }
}
