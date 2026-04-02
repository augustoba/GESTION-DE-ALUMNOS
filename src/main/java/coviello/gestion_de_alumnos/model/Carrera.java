package coviello.gestion_de_alumnos.model;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "activa")
    private Boolean activa = true;

    @ManyToMany
    @JoinTable(
            name = "carrera_docente",
            joinColumns = @JoinColumn(name = "carrera_id"),
            inverseJoinColumns = @JoinColumn(name = "docente_id")
    )
    private List<Docente> docentes = new ArrayList<>();


}
