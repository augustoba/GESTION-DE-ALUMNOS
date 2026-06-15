package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "envio_mail")
@Data
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
}
