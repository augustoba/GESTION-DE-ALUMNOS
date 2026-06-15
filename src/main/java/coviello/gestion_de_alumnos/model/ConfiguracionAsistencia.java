package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "configuracion_asistencia")
@Data
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
}
