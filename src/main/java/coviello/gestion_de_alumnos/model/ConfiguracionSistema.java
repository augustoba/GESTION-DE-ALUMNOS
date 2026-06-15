package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "configuracion_sistema")
public class ConfiguracionSistema {

    @Id
    private Long id = 1L;

    @Column(name = "preinscripcion_habilitada", nullable = false)
    private boolean preinscripcionHabilitada = true;

    @Column(name = "turnos_habilitados", nullable = false)
    private boolean turnosHabilitados = false;
}
