package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comision")
@Data
@NoArgsConstructor
public class Comision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "cupo_maximo")
    private int cupoMaximo = 0;

    @Column(name = "prefijo_turno", length = 5)
    private String prefijoTurno;

    private Boolean activa = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_carrera_id", nullable = false)
    private AnioCarrera anioCarrera;

    @JsonIgnore
    @OneToMany(mappedBy = "comision")
    private List<Alumno> alumnos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "comision")
    private List<HorarioClase> horarios = new ArrayList<>();
}
