package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Docente {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, length = 20)
    private String dni;

    @Column(unique = true)
    private String email;

    @Column(length = 50)
    private String telefono;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;
}
