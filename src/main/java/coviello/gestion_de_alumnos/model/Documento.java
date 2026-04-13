package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo; // DNI_FRENTE, DNI_DORSO, FOTO, TITULO

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] archivo;

    private boolean validado = false;

    @ManyToOne
    @JoinColumn(name = "preinscripcion_id")
    private Preinscripcion preinscripcion;
}