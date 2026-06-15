package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    private Integer capacidad;

    private String descripcion;
}
