package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "huella_alumno")
public class HuellaAlumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false, unique = true)
    private Alumno alumno;
    @Lob
    @Column(name = "template_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] templateData;
    // ID del slot en la memoria del sensor de huella del Arduino (único por alumno)
    @Column(name = "sensor_id", unique = true)
    private Integer sensorId;
    @Column(name = "pin_alternativo", length = 10)
    private String pinAlternativo;
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public HuellaAlumno() {
    }

    public Long getId() {
        return this.id;
    }

    public Alumno getAlumno() {
        return this.alumno;
    }

    public byte[] getTemplateData() {
        return this.templateData;
    }

    public Integer getSensorId() {
        return this.sensorId;
    }

    public String getPinAlternativo() {
        return this.pinAlternativo;
    }

    public LocalDateTime getFechaRegistro() {
        return this.fechaRegistro;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setAlumno(final Alumno alumno) {
        this.alumno = alumno;
    }

    public void setTemplateData(final byte[] templateData) {
        this.templateData = templateData;
    }

    public void setSensorId(final Integer sensorId) {
        this.sensorId = sensorId;
    }

    public void setPinAlternativo(final String pinAlternativo) {
        this.pinAlternativo = pinAlternativo;
    }

    public void setFechaRegistro(final LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof HuellaAlumno)) return false;
        final HuellaAlumno other = (HuellaAlumno) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$sensorId = this.getSensorId();
        final Object other$sensorId = other.getSensorId();
        if (this$sensorId == null ? other$sensorId != null : !this$sensorId.equals(other$sensorId)) return false;
        final Object this$alumno = this.getAlumno();
        final Object other$alumno = other.getAlumno();
        if (this$alumno == null ? other$alumno != null : !this$alumno.equals(other$alumno)) return false;
        if (!java.util.Arrays.equals(this.getTemplateData(), other.getTemplateData())) return false;
        final Object this$pinAlternativo = this.getPinAlternativo();
        final Object other$pinAlternativo = other.getPinAlternativo();
        if (this$pinAlternativo == null ? other$pinAlternativo != null : !this$pinAlternativo.equals(other$pinAlternativo)) return false;
        final Object this$fechaRegistro = this.getFechaRegistro();
        final Object other$fechaRegistro = other.getFechaRegistro();
        if (this$fechaRegistro == null ? other$fechaRegistro != null : !this$fechaRegistro.equals(other$fechaRegistro)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HuellaAlumno;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $sensorId = this.getSensorId();
        result = result * PRIME + ($sensorId == null ? 43 : $sensorId.hashCode());
        final Object $alumno = this.getAlumno();
        result = result * PRIME + ($alumno == null ? 43 : $alumno.hashCode());
        result = result * PRIME + java.util.Arrays.hashCode(this.getTemplateData());
        final Object $pinAlternativo = this.getPinAlternativo();
        result = result * PRIME + ($pinAlternativo == null ? 43 : $pinAlternativo.hashCode());
        final Object $fechaRegistro = this.getFechaRegistro();
        result = result * PRIME + ($fechaRegistro == null ? 43 : $fechaRegistro.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "HuellaAlumno(id=" + this.getId() + ", alumno=" + this.getAlumno() + ", templateData=" + java.util.Arrays.toString(this.getTemplateData()) + ", sensorId=" + this.getSensorId() + ", pinAlternativo=" + this.getPinAlternativo() + ", fechaRegistro=" + this.getFechaRegistro() + ")";
    }
}
