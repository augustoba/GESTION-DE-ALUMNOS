package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_permiso")
@Data
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
}
