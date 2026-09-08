package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

/**
 * @deprecated Reemplazada por DocumentoChecklist (proceso presencial)
 *             y DocumentoDigital (subida durante el año).
 *             Se mantiene temporalmente para evitar errores de compilación
 *             mientras se migran los servicios y controladores.
 */
@Deprecated
@Entity
@Table(name = "documento_legacy")
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Documento)) return false;
        final Documento other = (Documento) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Documento;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Documento(id=" + this.getId() + ")";
    }

    public Documento() {
    }
}
