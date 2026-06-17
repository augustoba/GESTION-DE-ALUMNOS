package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Preinscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_formulario", nullable = true, unique = true, length = 20)
    private String codigoFormulario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, length = 20)
    private String dni;

    private String email;

    @Column(length = 50)
    private String telefono;

    private String direccion;

    @Column(length = 100)
    private String localidad;

    private LocalDate fechaNacimiento;

    @Column(length = 100)
    private String lugarNacimiento;

    @Column(length = 100)
    private String nacionalidad;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPreinscripcion estado = EstadoPreinscripcion.PENDIENTE;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;
}
