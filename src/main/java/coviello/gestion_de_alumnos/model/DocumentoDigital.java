package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento_digital")
public class DocumentoDigital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 50)
    private TipoDocumento tipoDocumento;
    @Column(name = "archivo_url", length = 500)
    private String archivoUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoDocumento estado = EstadoDocumento.PENDIENTE;
    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;
    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por")
    private Usuario validadoPor;
    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    public DocumentoDigital() {
    }

    public Long getId() {
        return this.id;
    }

    public Alumno getAlumno() {
        return this.alumno;
    }

    public TipoDocumento getTipoDocumento() {
        return this.tipoDocumento;
    }

    public String getArchivoUrl() {
        return this.archivoUrl;
    }

    public EstadoDocumento getEstado() {
        return this.estado;
    }

    public LocalDateTime getFechaSubida() {
        return this.fechaSubida;
    }

    public LocalDateTime getFechaValidacion() {
        return this.fechaValidacion;
    }

    public Usuario getValidadoPor() {
        return this.validadoPor;
    }

    public String getMotivoRechazo() {
        return this.motivoRechazo;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setAlumno(final Alumno alumno) {
        this.alumno = alumno;
    }

    public void setTipoDocumento(final TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setArchivoUrl(final String archivoUrl) {
        this.archivoUrl = archivoUrl;
    }

    public void setEstado(final EstadoDocumento estado) {
        this.estado = estado;
    }

    public void setFechaSubida(final LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public void setFechaValidacion(final LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public void setValidadoPor(final Usuario validadoPor) {
        this.validadoPor = validadoPor;
    }

    public void setMotivoRechazo(final String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DocumentoDigital)) return false;
        final DocumentoDigital other = (DocumentoDigital) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$alumno = this.getAlumno();
        final Object other$alumno = other.getAlumno();
        if (this$alumno == null ? other$alumno != null : !this$alumno.equals(other$alumno)) return false;
        final Object this$tipoDocumento = this.getTipoDocumento();
        final Object other$tipoDocumento = other.getTipoDocumento();
        if (this$tipoDocumento == null ? other$tipoDocumento != null : !this$tipoDocumento.equals(other$tipoDocumento)) return false;
        final Object this$archivoUrl = this.getArchivoUrl();
        final Object other$archivoUrl = other.getArchivoUrl();
        if (this$archivoUrl == null ? other$archivoUrl != null : !this$archivoUrl.equals(other$archivoUrl)) return false;
        final Object this$estado = this.getEstado();
        final Object other$estado = other.getEstado();
        if (this$estado == null ? other$estado != null : !this$estado.equals(other$estado)) return false;
        final Object this$fechaSubida = this.getFechaSubida();
        final Object other$fechaSubida = other.getFechaSubida();
        if (this$fechaSubida == null ? other$fechaSubida != null : !this$fechaSubida.equals(other$fechaSubida)) return false;
        final Object this$fechaValidacion = this.getFechaValidacion();
        final Object other$fechaValidacion = other.getFechaValidacion();
        if (this$fechaValidacion == null ? other$fechaValidacion != null : !this$fechaValidacion.equals(other$fechaValidacion)) return false;
        final Object this$validadoPor = this.getValidadoPor();
        final Object other$validadoPor = other.getValidadoPor();
        if (this$validadoPor == null ? other$validadoPor != null : !this$validadoPor.equals(other$validadoPor)) return false;
        final Object this$motivoRechazo = this.getMotivoRechazo();
        final Object other$motivoRechazo = other.getMotivoRechazo();
        if (this$motivoRechazo == null ? other$motivoRechazo != null : !this$motivoRechazo.equals(other$motivoRechazo)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DocumentoDigital;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $alumno = this.getAlumno();
        result = result * PRIME + ($alumno == null ? 43 : $alumno.hashCode());
        final Object $tipoDocumento = this.getTipoDocumento();
        result = result * PRIME + ($tipoDocumento == null ? 43 : $tipoDocumento.hashCode());
        final Object $archivoUrl = this.getArchivoUrl();
        result = result * PRIME + ($archivoUrl == null ? 43 : $archivoUrl.hashCode());
        final Object $estado = this.getEstado();
        result = result * PRIME + ($estado == null ? 43 : $estado.hashCode());
        final Object $fechaSubida = this.getFechaSubida();
        result = result * PRIME + ($fechaSubida == null ? 43 : $fechaSubida.hashCode());
        final Object $fechaValidacion = this.getFechaValidacion();
        result = result * PRIME + ($fechaValidacion == null ? 43 : $fechaValidacion.hashCode());
        final Object $validadoPor = this.getValidadoPor();
        result = result * PRIME + ($validadoPor == null ? 43 : $validadoPor.hashCode());
        final Object $motivoRechazo = this.getMotivoRechazo();
        result = result * PRIME + ($motivoRechazo == null ? 43 : $motivoRechazo.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DocumentoDigital(id=" + this.getId() + ", alumno=" + this.getAlumno() + ", tipoDocumento=" + this.getTipoDocumento() + ", archivoUrl=" + this.getArchivoUrl() + ", estado=" + this.getEstado() + ", fechaSubida=" + this.getFechaSubida() + ", fechaValidacion=" + this.getFechaValidacion() + ", validadoPor=" + this.getValidadoPor() + ", motivoRechazo=" + this.getMotivoRechazo() + ")";
    }
}
