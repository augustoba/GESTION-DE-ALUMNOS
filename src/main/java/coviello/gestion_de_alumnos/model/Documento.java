package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreArchivo;
    private String tipo; // DNI_FRENTE, DNI_DORSO, TITULO, PAGO
    private String contentType; // image/jpeg, application/pdf

    @Lob
    private byte[] data;

    @ManyToOne
    private Alumno alumno;
}