package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
public class Arduino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "identificador_hardware", nullable = false, unique = true, length = 100)
    private String identificadorHardware;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoArduino tipo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id")
    private Aula aula; // null si es el Arduino de administración
    private String descripcion;
    @Column(nullable = false)
    private boolean activo = true;

    public Arduino() {
    }

    public Long getId() {
        return this.id;
    }

    public String getIdentificadorHardware() {
        return this.identificadorHardware;
    }

    public TipoArduino getTipo() {
        return this.tipo;
    }

    public Aula getAula() {
        return this.aula;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public boolean isActivo() {
        return this.activo;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setIdentificadorHardware(final String identificadorHardware) {
        this.identificadorHardware = identificadorHardware;
    }

    public void setTipo(final TipoArduino tipo) {
        this.tipo = tipo;
    }

    public void setAula(final Aula aula) {
        this.aula = aula;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public void setActivo(final boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Arduino)) return false;
        final Arduino other = (Arduino) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isActivo() != other.isActivo()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$identificadorHardware = this.getIdentificadorHardware();
        final Object other$identificadorHardware = other.getIdentificadorHardware();
        if (this$identificadorHardware == null ? other$identificadorHardware != null : !this$identificadorHardware.equals(other$identificadorHardware)) return false;
        final Object this$tipo = this.getTipo();
        final Object other$tipo = other.getTipo();
        if (this$tipo == null ? other$tipo != null : !this$tipo.equals(other$tipo)) return false;
        final Object this$aula = this.getAula();
        final Object other$aula = other.getAula();
        if (this$aula == null ? other$aula != null : !this$aula.equals(other$aula)) return false;
        final Object this$descripcion = this.getDescripcion();
        final Object other$descripcion = other.getDescripcion();
        if (this$descripcion == null ? other$descripcion != null : !this$descripcion.equals(other$descripcion)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Arduino;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isActivo() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $identificadorHardware = this.getIdentificadorHardware();
        result = result * PRIME + ($identificadorHardware == null ? 43 : $identificadorHardware.hashCode());
        final Object $tipo = this.getTipo();
        result = result * PRIME + ($tipo == null ? 43 : $tipo.hashCode());
        final Object $aula = this.getAula();
        result = result * PRIME + ($aula == null ? 43 : $aula.hashCode());
        final Object $descripcion = this.getDescripcion();
        result = result * PRIME + ($descripcion == null ? 43 : $descripcion.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Arduino(id=" + this.getId() + ", identificadorHardware=" + this.getIdentificadorHardware() + ", tipo=" + this.getTipo() + ", aula=" + this.getAula() + ", descripcion=" + this.getDescripcion() + ", activo=" + this.isActivo() + ")";
    }
}
