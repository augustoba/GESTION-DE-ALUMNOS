package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
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

    public Preinscripcion() {
    }

    public Long getId() {
        return this.id;
    }

    public String getCodigoFormulario() {
        return this.codigoFormulario;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public String getDni() {
        return this.dni;
    }

    public String getEmail() {
        return this.email;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public String getLocalidad() {
        return this.localidad;
    }

    public LocalDate getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public String getLugarNacimiento() {
        return this.lugarNacimiento;
    }

    public String getNacionalidad() {
        return this.nacionalidad;
    }

    public String getFotoUrl() {
        return this.fotoUrl;
    }

    public Carrera getCarrera() {
        return this.carrera;
    }

    public EstadoPreinscripcion getEstado() {
        return this.estado;
    }

    public LocalDateTime getFechaCreacion() {
        return this.fechaCreacion;
    }

    public Alumno getAlumno() {
        return this.alumno;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setCodigoFormulario(final String codigoFormulario) {
        this.codigoFormulario = codigoFormulario;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(final String apellido) {
        this.apellido = apellido;
    }

    public void setDni(final String dni) {
        this.dni = dni;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setTelefono(final String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(final String direccion) {
        this.direccion = direccion;
    }

    public void setLocalidad(final String localidad) {
        this.localidad = localidad;
    }

    public void setFechaNacimiento(final LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setLugarNacimiento(final String lugarNacimiento) {
        this.lugarNacimiento = lugarNacimiento;
    }

    public void setNacionalidad(final String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public void setFotoUrl(final String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setCarrera(final Carrera carrera) {
        this.carrera = carrera;
    }

    public void setEstado(final EstadoPreinscripcion estado) {
        this.estado = estado;
    }

    public void setFechaCreacion(final LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setAlumno(final Alumno alumno) {
        this.alumno = alumno;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Preinscripcion)) return false;
        final Preinscripcion other = (Preinscripcion) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$codigoFormulario = this.getCodigoFormulario();
        final Object other$codigoFormulario = other.getCodigoFormulario();
        if (this$codigoFormulario == null ? other$codigoFormulario != null : !this$codigoFormulario.equals(other$codigoFormulario)) return false;
        final Object this$nombre = this.getNombre();
        final Object other$nombre = other.getNombre();
        if (this$nombre == null ? other$nombre != null : !this$nombre.equals(other$nombre)) return false;
        final Object this$apellido = this.getApellido();
        final Object other$apellido = other.getApellido();
        if (this$apellido == null ? other$apellido != null : !this$apellido.equals(other$apellido)) return false;
        final Object this$dni = this.getDni();
        final Object other$dni = other.getDni();
        if (this$dni == null ? other$dni != null : !this$dni.equals(other$dni)) return false;
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final Object this$telefono = this.getTelefono();
        final Object other$telefono = other.getTelefono();
        if (this$telefono == null ? other$telefono != null : !this$telefono.equals(other$telefono)) return false;
        final Object this$direccion = this.getDireccion();
        final Object other$direccion = other.getDireccion();
        if (this$direccion == null ? other$direccion != null : !this$direccion.equals(other$direccion)) return false;
        final Object this$localidad = this.getLocalidad();
        final Object other$localidad = other.getLocalidad();
        if (this$localidad == null ? other$localidad != null : !this$localidad.equals(other$localidad)) return false;
        final Object this$fechaNacimiento = this.getFechaNacimiento();
        final Object other$fechaNacimiento = other.getFechaNacimiento();
        if (this$fechaNacimiento == null ? other$fechaNacimiento != null : !this$fechaNacimiento.equals(other$fechaNacimiento)) return false;
        final Object this$lugarNacimiento = this.getLugarNacimiento();
        final Object other$lugarNacimiento = other.getLugarNacimiento();
        if (this$lugarNacimiento == null ? other$lugarNacimiento != null : !this$lugarNacimiento.equals(other$lugarNacimiento)) return false;
        final Object this$nacionalidad = this.getNacionalidad();
        final Object other$nacionalidad = other.getNacionalidad();
        if (this$nacionalidad == null ? other$nacionalidad != null : !this$nacionalidad.equals(other$nacionalidad)) return false;
        final Object this$fotoUrl = this.getFotoUrl();
        final Object other$fotoUrl = other.getFotoUrl();
        if (this$fotoUrl == null ? other$fotoUrl != null : !this$fotoUrl.equals(other$fotoUrl)) return false;
        final Object this$carrera = this.getCarrera();
        final Object other$carrera = other.getCarrera();
        if (this$carrera == null ? other$carrera != null : !this$carrera.equals(other$carrera)) return false;
        final Object this$estado = this.getEstado();
        final Object other$estado = other.getEstado();
        if (this$estado == null ? other$estado != null : !this$estado.equals(other$estado)) return false;
        final Object this$fechaCreacion = this.getFechaCreacion();
        final Object other$fechaCreacion = other.getFechaCreacion();
        if (this$fechaCreacion == null ? other$fechaCreacion != null : !this$fechaCreacion.equals(other$fechaCreacion)) return false;
        final Object this$alumno = this.getAlumno();
        final Object other$alumno = other.getAlumno();
        if (this$alumno == null ? other$alumno != null : !this$alumno.equals(other$alumno)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Preinscripcion;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $codigoFormulario = this.getCodigoFormulario();
        result = result * PRIME + ($codigoFormulario == null ? 43 : $codigoFormulario.hashCode());
        final Object $nombre = this.getNombre();
        result = result * PRIME + ($nombre == null ? 43 : $nombre.hashCode());
        final Object $apellido = this.getApellido();
        result = result * PRIME + ($apellido == null ? 43 : $apellido.hashCode());
        final Object $dni = this.getDni();
        result = result * PRIME + ($dni == null ? 43 : $dni.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $telefono = this.getTelefono();
        result = result * PRIME + ($telefono == null ? 43 : $telefono.hashCode());
        final Object $direccion = this.getDireccion();
        result = result * PRIME + ($direccion == null ? 43 : $direccion.hashCode());
        final Object $localidad = this.getLocalidad();
        result = result * PRIME + ($localidad == null ? 43 : $localidad.hashCode());
        final Object $fechaNacimiento = this.getFechaNacimiento();
        result = result * PRIME + ($fechaNacimiento == null ? 43 : $fechaNacimiento.hashCode());
        final Object $lugarNacimiento = this.getLugarNacimiento();
        result = result * PRIME + ($lugarNacimiento == null ? 43 : $lugarNacimiento.hashCode());
        final Object $nacionalidad = this.getNacionalidad();
        result = result * PRIME + ($nacionalidad == null ? 43 : $nacionalidad.hashCode());
        final Object $fotoUrl = this.getFotoUrl();
        result = result * PRIME + ($fotoUrl == null ? 43 : $fotoUrl.hashCode());
        final Object $carrera = this.getCarrera();
        result = result * PRIME + ($carrera == null ? 43 : $carrera.hashCode());
        final Object $estado = this.getEstado();
        result = result * PRIME + ($estado == null ? 43 : $estado.hashCode());
        final Object $fechaCreacion = this.getFechaCreacion();
        result = result * PRIME + ($fechaCreacion == null ? 43 : $fechaCreacion.hashCode());
        final Object $alumno = this.getAlumno();
        result = result * PRIME + ($alumno == null ? 43 : $alumno.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Preinscripcion(id=" + this.getId() + ", codigoFormulario=" + this.getCodigoFormulario() + ", nombre=" + this.getNombre() + ", apellido=" + this.getApellido() + ", dni=" + this.getDni() + ", email=" + this.getEmail() + ", telefono=" + this.getTelefono() + ", direccion=" + this.getDireccion() + ", localidad=" + this.getLocalidad() + ", fechaNacimiento=" + this.getFechaNacimiento() + ", lugarNacimiento=" + this.getLugarNacimiento() + ", nacionalidad=" + this.getNacionalidad() + ", fotoUrl=" + this.getFotoUrl() + ", carrera=" + this.getCarrera() + ", estado=" + this.getEstado() + ", fechaCreacion=" + this.getFechaCreacion() + ", alumno=" + this.getAlumno() + ")";
    }
}
