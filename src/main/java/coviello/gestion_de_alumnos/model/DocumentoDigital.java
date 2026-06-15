package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "documento_digital")
@Data
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
}
