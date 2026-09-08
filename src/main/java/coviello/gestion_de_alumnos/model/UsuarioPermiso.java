package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_permiso")
public class UsuarioPermiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
    @Column(name = "fecha_desde", nullable = false)
    private LocalDateTime fechaDesde = LocalDateTime.now();
    @Column(name = "fecha_hasta")
    private LocalDateTime fechaHasta; // null = sin vencimiento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "otorgado_por", nullable = false)
    private Usuario otorgadoPor;

    public UsuarioPermiso() {
    }

    public Long getId() {
        return this.id;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public Permiso getPermiso() {
        return this.permiso;
    }

    public LocalDateTime getFechaDesde() {
        return this.fechaDesde;
    }

    public LocalDateTime getFechaHasta() {
        return this.fechaHasta;
    }

    public Usuario getOtorgadoPor() {
        return this.otorgadoPor;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setUsuario(final Usuario usuario) {
        this.usuario = usuario;
    }

    public void setPermiso(final Permiso permiso) {
        this.permiso = permiso;
    }

    public void setFechaDesde(final LocalDateTime fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public void setFechaHasta(final LocalDateTime fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public void setOtorgadoPor(final Usuario otorgadoPor) {
        this.otorgadoPor = otorgadoPor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof UsuarioPermiso)) return false;
        final UsuarioPermiso other = (UsuarioPermiso) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$usuario = this.getUsuario();
        final Object other$usuario = other.getUsuario();
        if (this$usuario == null ? other$usuario != null : !this$usuario.equals(other$usuario)) return false;
        final Object this$permiso = this.getPermiso();
        final Object other$permiso = other.getPermiso();
        if (this$permiso == null ? other$permiso != null : !this$permiso.equals(other$permiso)) return false;
        final Object this$fechaDesde = this.getFechaDesde();
        final Object other$fechaDesde = other.getFechaDesde();
        if (this$fechaDesde == null ? other$fechaDesde != null : !this$fechaDesde.equals(other$fechaDesde)) return false;
        final Object this$fechaHasta = this.getFechaHasta();
        final Object other$fechaHasta = other.getFechaHasta();
        if (this$fechaHasta == null ? other$fechaHasta != null : !this$fechaHasta.equals(other$fechaHasta)) return false;
        final Object this$otorgadoPor = this.getOtorgadoPor();
        final Object other$otorgadoPor = other.getOtorgadoPor();
        if (this$otorgadoPor == null ? other$otorgadoPor != null : !this$otorgadoPor.equals(other$otorgadoPor)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UsuarioPermiso;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $usuario = this.getUsuario();
        result = result * PRIME + ($usuario == null ? 43 : $usuario.hashCode());
        final Object $permiso = this.getPermiso();
        result = result * PRIME + ($permiso == null ? 43 : $permiso.hashCode());
        final Object $fechaDesde = this.getFechaDesde();
        result = result * PRIME + ($fechaDesde == null ? 43 : $fechaDesde.hashCode());
        final Object $fechaHasta = this.getFechaHasta();
        result = result * PRIME + ($fechaHasta == null ? 43 : $fechaHasta.hashCode());
        final Object $otorgadoPor = this.getOtorgadoPor();
        result = result * PRIME + ($otorgadoPor == null ? 43 : $otorgadoPor.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "UsuarioPermiso(id=" + this.getId() + ", usuario=" + this.getUsuario() + ", permiso=" + this.getPermiso() + ", fechaDesde=" + this.getFechaDesde() + ", fechaHasta=" + this.getFechaHasta() + ", otorgadoPor=" + this.getOtorgadoPor() + ")";
    }
}
