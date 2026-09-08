package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
public class Permiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 100)
    private CodigoPermiso codigo;
    private String descripcion;

    public Permiso() {
    }

    public Long getId() {
        return this.id;
    }

    public CodigoPermiso getCodigo() {
        return this.codigo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setCodigo(final CodigoPermiso codigo) {
        this.codigo = codigo;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Permiso)) return false;
        final Permiso other = (Permiso) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$codigo = this.getCodigo();
        final Object other$codigo = other.getCodigo();
        if (this$codigo == null ? other$codigo != null : !this$codigo.equals(other$codigo)) return false;
        final Object this$descripcion = this.getDescripcion();
        final Object other$descripcion = other.getDescripcion();
        if (this$descripcion == null ? other$descripcion != null : !this$descripcion.equals(other$descripcion)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Permiso;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $codigo = this.getCodigo();
        result = result * PRIME + ($codigo == null ? 43 : $codigo.hashCode());
        final Object $descripcion = this.getDescripcion();
        result = result * PRIME + ($descripcion == null ? 43 : $descripcion.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Permiso(id=" + this.getId() + ", codigo=" + this.getCodigo() + ", descripcion=" + this.getDescripcion() + ")";
    }
}
