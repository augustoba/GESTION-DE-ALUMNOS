package coviello.gestion_de_alumnos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Preinscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;

    @ManyToOne
    private Carrera carrera;

    private Boolean pagoValidado;
    private Boolean documentosCompletos;

    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoPreinscripcion estado;

    @JsonIgnore
    @OneToMany(mappedBy = "preinscripcion", cascade = CascadeType.ALL)
    private List<Documento> documentos;

    public Preinscripcion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public Boolean getPagoValidado() { return pagoValidado; }
    public void setPagoValidado(Boolean pagoValidado) { this.pagoValidado = pagoValidado; }

    public Boolean getDocumentosCompletos() { return documentosCompletos; }
    public void setDocumentosCompletos(Boolean documentosCompletos) { this.documentosCompletos = documentosCompletos; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public EstadoPreinscripcion getEstado() { return estado; }
    public void setEstado(EstadoPreinscripcion estado) { this.estado = estado; }

    public List<Documento> getDocumentos() { return documentos; }
    public void setDocumentos(List<Documento> documentos) { this.documentos = documentos; }

    @Override
    public String toString() {
        return "Preinscripcion{id=" + id + ", nombre='" + nombre + "', apellido='" + apellido +
                "', dni='" + dni + "', email='" + email + "', direccion='" + direccion +
                "', fechaNacimiento=" + fechaNacimiento + ", estado=" + estado + '}';
    }
}
