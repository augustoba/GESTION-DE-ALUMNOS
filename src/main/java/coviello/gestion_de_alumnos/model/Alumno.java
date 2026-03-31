package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;
    private String nombres = null;
    private String apellidos= null;
    private String dni = null;
    private String direccion = null;
    private String email = null;
    private String cuil= null;
    private LocalDate fechaNac= null;
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<Documento> documentos;

    @OneToOne
    @MapsId
    private Usuario usuario;


}
