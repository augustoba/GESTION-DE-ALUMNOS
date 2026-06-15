package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
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
}
