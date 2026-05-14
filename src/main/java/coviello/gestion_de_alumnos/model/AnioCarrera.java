package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "anio_carrera")
public class AnioCarrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;

    @Column(name = "numero_anio", nullable = false)
    private int numeroAnio;

    @OneToMany(mappedBy = "anioCarrera", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("nombre ASC")
    private List<Materia> materias = new ArrayList<>();

    public AnioCarrera() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public int getNumeroAnio() { return numeroAnio; }
    public void setNumeroAnio(int numeroAnio) { this.numeroAnio = numeroAnio; }

    public List<Materia> getMaterias() { return materias; }
    public void setMaterias(List<Materia> materias) { this.materias = materias; }
}
