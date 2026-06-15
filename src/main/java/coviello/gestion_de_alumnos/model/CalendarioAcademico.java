package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "calendario_academico")
@Data
public class CalendarioAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCalendario tipo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "afecta_a", nullable = false, length = 20)
    private AlcanceCalendario afectaA = AlcanceCalendario.TODAS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;
}
