package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preinscripcion_id", nullable = false, unique = true)
    private Preinscripcion preinscripcion;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.SIN_PAGO;
    @Column(name = "monto_total", precision = 10, scale = 2)
    private BigDecimal montoTotal;
    @Column(name = "monto_abonado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoAbonado = BigDecimal.ZERO;
    @Column(name = "fecha_ultimo_pago")
    private LocalDateTime fechaUltimoPago;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    public Pago() {
    }

    public Long getId() {
        return this.id;
    }

    public Preinscripcion getPreinscripcion() {
        return this.preinscripcion;
    }

    public EstadoPago getEstado() {
        return this.estado;
    }

    public BigDecimal getMontoTotal() {
        return this.montoTotal;
    }

    public BigDecimal getMontoAbonado() {
        return this.montoAbonado;
    }

    public LocalDateTime getFechaUltimoPago() {
        return this.fechaUltimoPago;
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

    public void setEstado(final EstadoPago estado) {
        this.estado = estado;
    }

    public void setMontoTotal(final BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public void setMontoAbonado(final BigDecimal montoAbonado) {
        this.montoAbonado = montoAbonado;
    }

    public void setFechaUltimoPago(final LocalDateTime fechaUltimoPago) {
        this.fechaUltimoPago = fechaUltimoPago;
    }

    public void setRegistradoPor(final Usuario registradoPor) {
        this.registradoPor = registradoPor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Pago)) return false;
        final Pago other = (Pago) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$preinscripcion = this.getPreinscripcion();
        final Object other$preinscripcion = other.getPreinscripcion();
        if (this$preinscripcion == null ? other$preinscripcion != null : !this$preinscripcion.equals(other$preinscripcion)) return false;
        final Object this$estado = this.getEstado();
        final Object other$estado = other.getEstado();
        if (this$estado == null ? other$estado != null : !this$estado.equals(other$estado)) return false;
        final Object this$montoTotal = this.getMontoTotal();
        final Object other$montoTotal = other.getMontoTotal();
        if (this$montoTotal == null ? other$montoTotal != null : !this$montoTotal.equals(other$montoTotal)) return false;
        final Object this$montoAbonado = this.getMontoAbonado();
        final Object other$montoAbonado = other.getMontoAbonado();
        if (this$montoAbonado == null ? other$montoAbonado != null : !this$montoAbonado.equals(other$montoAbonado)) return false;
        final Object this$fechaUltimoPago = this.getFechaUltimoPago();
        final Object other$fechaUltimoPago = other.getFechaUltimoPago();
        if (this$fechaUltimoPago == null ? other$fechaUltimoPago != null : !this$fechaUltimoPago.equals(other$fechaUltimoPago)) return false;
        final Object this$registradoPor = this.getRegistradoPor();
        final Object other$registradoPor = other.getRegistradoPor();
        if (this$registradoPor == null ? other$registradoPor != null : !this$registradoPor.equals(other$registradoPor)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Pago;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $preinscripcion = this.getPreinscripcion();
        result = result * PRIME + ($preinscripcion == null ? 43 : $preinscripcion.hashCode());
        final Object $estado = this.getEstado();
        result = result * PRIME + ($estado == null ? 43 : $estado.hashCode());
        final Object $montoTotal = this.getMontoTotal();
        result = result * PRIME + ($montoTotal == null ? 43 : $montoTotal.hashCode());
        final Object $montoAbonado = this.getMontoAbonado();
        result = result * PRIME + ($montoAbonado == null ? 43 : $montoAbonado.hashCode());
        final Object $fechaUltimoPago = this.getFechaUltimoPago();
        result = result * PRIME + ($fechaUltimoPago == null ? 43 : $fechaUltimoPago.hashCode());
        final Object $registradoPor = this.getRegistradoPor();
        result = result * PRIME + ($registradoPor == null ? 43 : $registradoPor.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Pago(id=" + this.getId() + ", preinscripcion=" + this.getPreinscripcion() + ", estado=" + this.getEstado() + ", montoTotal=" + this.getMontoTotal() + ", montoAbonado=" + this.getMontoAbonado() + ", fechaUltimoPago=" + this.getFechaUltimoPago() + ", registradoPor=" + this.getRegistradoPor() + ")";
    }
}
