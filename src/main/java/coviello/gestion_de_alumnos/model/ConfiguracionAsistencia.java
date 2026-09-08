package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion_asistencia")
public class ConfiguracionAsistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "porcentaje_minimo", nullable = false)
    private int porcentajeMinimo = 75;
    @Column(name = "tolerancia_minutos", nullable = false)
    private int toleranciaMinutos = 15;
    @Enumerated(EnumType.STRING)
    @Column(name = "aplica_a", nullable = false, length = 20)
    private NivelConfiguracionAsistencia aplicaA = NivelConfiguracionAsistencia.GLOBAL;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    public ConfiguracionAsistencia() {
    }

    public Long getId() {
        return this.id;
    }

    public int getPorcentajeMinimo() {
        return this.porcentajeMinimo;
    }

    public int getToleranciaMinutos() {
        return this.toleranciaMinutos;
    }

    public NivelConfiguracionAsistencia getAplicaA() {
        return this.aplicaA;
    }

    public Carrera getCarrera() {
        return this.carrera;
    }

    public Materia getMateria() {
        return this.materia;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setPorcentajeMinimo(final int porcentajeMinimo) {
        this.porcentajeMinimo = porcentajeMinimo;
    }

    public void setToleranciaMinutos(final int toleranciaMinutos) {
        this.toleranciaMinutos = toleranciaMinutos;
    }

    public void setAplicaA(final NivelConfiguracionAsistencia aplicaA) {
        this.aplicaA = aplicaA;
    }

    public void setCarrera(final Carrera carrera) {
        this.carrera = carrera;
    }

    public void setMateria(final Materia materia) {
        this.materia = materia;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ConfiguracionAsistencia)) return false;
        final ConfiguracionAsistencia other = (ConfiguracionAsistencia) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getPorcentajeMinimo() != other.getPorcentajeMinimo()) return false;
        if (this.getToleranciaMinutos() != other.getToleranciaMinutos()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$aplicaA = this.getAplicaA();
        final Object other$aplicaA = other.getAplicaA();
        if (this$aplicaA == null ? other$aplicaA != null : !this$aplicaA.equals(other$aplicaA)) return false;
        final Object this$carrera = this.getCarrera();
        final Object other$carrera = other.getCarrera();
        if (this$carrera == null ? other$carrera != null : !this$carrera.equals(other$carrera)) return false;
        final Object this$materia = this.getMateria();
        final Object other$materia = other.getMateria();
        if (this$materia == null ? other$materia != null : !this$materia.equals(other$materia)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ConfiguracionAsistencia;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getPorcentajeMinimo();
        result = result * PRIME + this.getToleranciaMinutos();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $aplicaA = this.getAplicaA();
        result = result * PRIME + ($aplicaA == null ? 43 : $aplicaA.hashCode());
        final Object $carrera = this.getCarrera();
        result = result * PRIME + ($carrera == null ? 43 : $carrera.hashCode());
        final Object $materia = this.getMateria();
        result = result * PRIME + ($materia == null ? 43 : $materia.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ConfiguracionAsistencia(id=" + this.getId() + ", porcentajeMinimo=" + this.getPorcentajeMinimo() + ", toleranciaMinutos=" + this.getToleranciaMinutos() + ", aplicaA=" + this.getAplicaA() + ", carrera=" + this.getCarrera() + ", materia=" + this.getMateria() + ")";
    }
}
