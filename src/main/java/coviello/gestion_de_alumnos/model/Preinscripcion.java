package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Lob
    @Column(name = "comprobante_pago", columnDefinition = "LONGBLOB")
    private byte[] comprobantePago;

    @ManyToOne
    private Carrera carrera;

    private Boolean pagoValidado;
    private Boolean documentosCompletos;

    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "preinscripcion", cascade = CascadeType.ALL)
    private List<Documento> documentos;

    public Preinscripcion() {
    }

    public Preinscripcion(Long id, String nombre, String apellido, String dni, String email, String telefono, byte[] comprobantePago, Carrera carrera, Boolean pagoValidado, Boolean documentosCompletos, LocalDateTime fechaCreacion, List<Documento> documentos) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.comprobantePago = comprobantePago;
        this.carrera = carrera;
        this.pagoValidado = pagoValidado;
        this.documentosCompletos = documentosCompletos;
        this.fechaCreacion = fechaCreacion;
        this.documentos = documentos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public byte[] getComprobantePago() {
        return comprobantePago;
    }

    public void setComprobantePago(byte[] comprobantePago) {
        this.comprobantePago = comprobantePago;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Boolean getPagoValidado() {
        return pagoValidado;
    }

    public void setPagoValidado(Boolean pagoValidado) {
        this.pagoValidado = pagoValidado;
    }

    public Boolean getDocumentosCompletos() {
        return documentosCompletos;
    }

    public void setDocumentosCompletos(Boolean documentosCompletos) {
        this.documentosCompletos = documentosCompletos;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<Documento> documentos) {
        this.documentos = documentos;
    }

    @Override
    public String toString() {
        return "Preinscripcion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", comprobantePago=" + Arrays.toString(comprobantePago) +
                ", carrera=" + carrera +
                ", pagoValidado=" + pagoValidado +
                ", documentosCompletos=" + documentosCompletos +
                ", fechaCreacion=" + fechaCreacion +
                ", documentos=" + documentos +
                '}';
    }
}
