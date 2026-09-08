package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_sistema")
public class ConfiguracionSistema {
    @Id
    private Long id = 1L;
    @Column(name = "preinscripcion_habilitada", nullable = false)
    private boolean preinscripcionHabilitada = true;
    @Column(name = "turnos_habilitados", nullable = false)
    private boolean turnosHabilitados = false;

    public ConfiguracionSistema() {
    }

    public Long getId() {
        return this.id;
    }

    public boolean isPreinscripcionHabilitada() {
        return this.preinscripcionHabilitada;
    }

    public boolean isTurnosHabilitados() {
        return this.turnosHabilitados;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setPreinscripcionHabilitada(final boolean preinscripcionHabilitada) {
        this.preinscripcionHabilitada = preinscripcionHabilitada;
    }

    public void setTurnosHabilitados(final boolean turnosHabilitados) {
        this.turnosHabilitados = turnosHabilitados;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ConfiguracionSistema)) return false;
        final ConfiguracionSistema other = (ConfiguracionSistema) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isPreinscripcionHabilitada() != other.isPreinscripcionHabilitada()) return false;
        if (this.isTurnosHabilitados() != other.isTurnosHabilitados()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ConfiguracionSistema;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isPreinscripcionHabilitada() ? 79 : 97);
        result = result * PRIME + (this.isTurnosHabilitados() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ConfiguracionSistema(id=" + this.getId() + ", preinscripcionHabilitada=" + this.isPreinscripcionHabilitada() + ", turnosHabilitados=" + this.isTurnosHabilitados() + ")";
    }
}
