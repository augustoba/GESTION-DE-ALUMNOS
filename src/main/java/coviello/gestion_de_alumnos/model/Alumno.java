package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(length = 20)
    private String cuil;

    @Column(unique = true)
    private String email;

    @Column(length = 50)
    private String telefono;

    private String direccion;

    @Column(length = 100)
    private String localidad;

    @Column(name = "fecha_nac")
    private LocalDate fechaNac;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(nullable = false)
    private boolean habilitado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comision_id")
    private Comision comision;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
}
