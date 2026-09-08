package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento_checklist")
public class DocumentoChecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preinscripcion_id", nullable = false)
    private Preinscripcion preinscripcion;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 50)
    private TipoDocumento tipoDocumento;
    @Column(nullable = false)
    private boolean presentado = false;
    @Column(name = "fecha_presentacion")
    private LocalDateTime fechaPresentacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    public DocumentoChecklist() {
    }

    public Long getId() {
        return this.id;
    }

    public Preinscripcion getPreinscripcion() {
        return this.preinscripcion;
    }

    public TipoDocumento getTipoDocumento() {
        return this.tipoDocumento;
    }

    public boolean isPresentado() {
        return this.presentado;
    }

    public LocalDateTime getFechaPresentacion() {
        return this.fechaPresentacion;
    }

    public Usuario getRegistradoPor() {
        return this.registradoPor;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setPreinscripcion(final Preinscripcion preinscripcion) {
        this.preinscripcion = preinscripcion;
    }

    public void setTipoDocumento(final TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setPresentado(final boolean presentado) {
        this.presentado = presentado;
    }

    public void setFechaPresentacion(final LocalDateTime fechaPresentacion) {
        this.fechaPresentacion = fechaPresentacion;
    }

    public void setRegistradoPor(final Usuario registradoPor) {
        this.registradoPor = registradoPor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DocumentoChecklist)) return false;
        final DocumentoChecklist other = (DocumentoChecklist) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isPresentado() != other.isPresentado()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$preinscripcion = this.getPreinscripcion();
        final Object other$preinscripcion = other.getPreinscripcion();
        if (this$preinscripcion == null ? other$preinscripcion != null : !this$preinscripcion.equals(other$preinscripcion)) return false;
        final Object this$tipoDocumento = this.getTipoDocumento();
        final Object other$tipoDocumento = other.getTipoDocumento();
        if (this$tipoDocumento == null ? other$tipoDocumento != null : !this$tipoDocumento.equals(other$tipoDocumento)) return false;
        final Object this$fechaPresentacion = this.getFechaPresentacion();
        final Object other$fechaPresentacion = other.getFechaPresentacion();
        if (this$fechaPresentacion == null ? other$fechaPresentacion != null : !this$fechaPresentacion.equals(other$fechaPresentacion)) return false;
        final Object this$registradoPor = this.getRegistradoPor();
        final Object other$registradoPor = other.getRegistradoPor();
        if (this$registradoPor == null ? other$registradoPor != null : !this$registradoPor.equals(other$registradoPor)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DocumentoChecklist;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isPresentado() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $preinscripcion = this.getPreinscripcion();
        result = result * PRIME + ($preinscripcion == null ? 43 : $preinscripcion.hashCode());
        final Object $tipoDocumento = this.getTipoDocumento();
        result = result * PRIME + ($tipoDocumento == null ? 43 : $tipoDocumento.hashCode());
        final Object $fechaPresentacion = this.getFechaPresentacion();
        result = result * PRIME + ($fechaPresentacion == null ? 43 : $fechaPresentacion.hashCode());
        final Object $registradoPor = this.getRegistradoPor();
        result = result * PRIME + ($registradoPor == null ? 43 : $registradoPor.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DocumentoChecklist(id=" + this.getId() + ", preinscripcion=" + this.getPreinscripcion() + ", tipoDocumento=" + this.getTipoDocumento() + ", presentado=" + this.isPresentado() + ", fechaPresentacion=" + this.getFechaPresentacion() + ", registradoPor=" + this.getRegistradoPor() + ")";
    }
}
