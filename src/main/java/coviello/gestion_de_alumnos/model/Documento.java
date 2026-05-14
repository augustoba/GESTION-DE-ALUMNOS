package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipo;

    @JsonIgnore
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] archivo;

    private String nombreArchivo;
    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDocumento estado = EstadoDocumento.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preinscripcion_id")
    @JsonIgnore
    private Preinscripcion preinscripcion;
}
