package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "envio_mail")
public class EnvioMail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String asunto;
    @Lob
    @Column(nullable = false)
    private String cuerpo;
    @Enumerated(EnumType.STRING)
    @Column(name = "destinatario_tipo", nullable = false, length = 30)
    private TipoDestinatarioMail destinatarioTipo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_carrera_id")
    private AnioCarrera anioCarrera;
    @Column(name = "total_enviados")
    private int totalEnviados = 0;
    @Column(name = "enviado_en")
    private LocalDateTime enviadoEn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enviado_por", nullable = false)
    private Usuario enviadoPor;

    public EnvioMail() {
    }

    public Long getId() {
        return this.id;
    }

    public String getAsunto() {
        return this.asunto;
    }

    public String getCuerpo() {
        return this.cuerpo;
    }

    public TipoDestinatarioMail getDestinatarioTipo() {
        return this.destinatarioTipo;
    }

    public Carrera getCarrera() {
        return this.carrera;
    }

    public AnioCarrera getAnioCarrera() {
        return this.anioCarrera;
    }

    public int getTotalEnviados() {
        return this.totalEnviados;
    }

    public LocalDateTime getEnviadoEn() {
        return this.enviadoEn;
    }

    public Usuario getEnviadoPor() {
        return this.enviadoPor;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setAsunto(final String asunto) {
        this.asunto = asunto;
    }

    public void setCuerpo(final String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public void setDestinatarioTipo(final TipoDestinatarioMail destinatarioTipo) {
        this.destinatarioTipo = destinatarioTipo;
    }

    public void setCarrera(final Carrera carrera) {
        this.carrera = carrera;
    }

    public void setAnioCarrera(final AnioCarrera anioCarrera) {
        this.anioCarrera = anioCarrera;
    }

    public void setTotalEnviados(final int totalEnviados) {
        this.totalEnviados = totalEnviados;
    }

    public void setEnviadoEn(final LocalDateTime enviadoEn) {
        this.enviadoEn = enviadoEn;
    }

    public void setEnviadoPor(final Usuario enviadoPor) {
        this.enviadoPor = enviadoPor;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EnvioMail)) return false;
        final EnvioMail other = (EnvioMail) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getTotalEnviados() != other.getTotalEnviados()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$asunto = this.getAsunto();
        final Object other$asunto = other.getAsunto();
        if (this$asunto == null ? other$asunto != null : !this$asunto.equals(other$asunto)) return false;
        final Object this$cuerpo = this.getCuerpo();
        final Object other$cuerpo = other.getCuerpo();
        if (this$cuerpo == null ? other$cuerpo != null : !this$cuerpo.equals(other$cuerpo)) return false;
        final Object this$destinatarioTipo = this.getDestinatarioTipo();
        final Object other$destinatarioTipo = other.getDestinatarioTipo();
        if (this$destinatarioTipo == null ? other$destinatarioTipo != null : !this$destinatarioTipo.equals(other$destinatarioTipo)) return false;
        final Object this$carrera = this.getCarrera();
        final Object other$carrera = other.getCarrera();
        if (this$carrera == null ? other$carrera != null : !this$carrera.equals(other$carrera)) return false;
        final Object this$anioCarrera = this.getAnioCarrera();
        final Object other$anioCarrera = other.getAnioCarrera();
        if (this$anioCarrera == null ? other$anioCarrera != null : !this$anioCarrera.equals(other$anioCarrera)) return false;
        final Object this$enviadoEn = this.getEnviadoEn();
        final Object other$enviadoEn = other.getEnviadoEn();
        if (this$enviadoEn == null ? other$enviadoEn != null : !this$enviadoEn.equals(other$enviadoEn)) return false;
        final Object this$enviadoPor = this.getEnviadoPor();
        final Object other$enviadoPor = other.getEnviadoPor();
        if (this$enviadoPor == null ? other$enviadoPor != null : !this$enviadoPor.equals(other$enviadoPor)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EnvioMail;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getTotalEnviados();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $asunto = this.getAsunto();
        result = result * PRIME + ($asunto == null ? 43 : $asunto.hashCode());
        final Object $cuerpo = this.getCuerpo();
        result = result * PRIME + ($cuerpo == null ? 43 : $cuerpo.hashCode());
        final Object $destinatarioTipo = this.getDestinatarioTipo();
        result = result * PRIME + ($destinatarioTipo == null ? 43 : $destinatarioTipo.hashCode());
        final Object $carrera = this.getCarrera();
        result = result * PRIME + ($carrera == null ? 43 : $carrera.hashCode());
        final Object $anioCarrera = this.getAnioCarrera();
        result = result * PRIME + ($anioCarrera == null ? 43 : $anioCarrera.hashCode());
        final Object $enviadoEn = this.getEnviadoEn();
        result = result * PRIME + ($enviadoEn == null ? 43 : $enviadoEn.hashCode());
        final Object $enviadoPor = this.getEnviadoPor();
        result = result * PRIME + ($enviadoPor == null ? 43 : $enviadoPor.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "EnvioMail(id=" + this.getId() + ", asunto=" + this.getAsunto() + ", cuerpo=" + this.getCuerpo() + ", destinatarioTipo=" + this.getDestinatarioTipo() + ", carrera=" + this.getCarrera() + ", anioCarrera=" + this.getAnioCarrera() + ", totalEnviados=" + this.getTotalEnviados() + ", enviadoEn=" + this.getEnviadoEn() + ", enviadoPor=" + this.getEnviadoPor() + ")";
    }
}
