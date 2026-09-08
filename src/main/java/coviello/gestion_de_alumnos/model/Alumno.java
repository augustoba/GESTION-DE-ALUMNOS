package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombres;
    @Column(nullable = false)
    private String apellidos;
    @Column(nullable = false, unique = true, length = 20)
    private String dni;
    @Column(length = 20)
    private String cuil;
    @Column(unique = true)
    private String email;
    @Column(length = 50)
    private String telefono;
    private String direccion;
    @Column(length = 100)
    private String localidad;
    @Column(name = "fecha_nac")
    private LocalDate fechaNac;
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;
    @Column(nullable = false)
    private boolean habilitado = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comision_id")
    private Comision comision;
    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    public Alumno() {
    }

    public Long getId() {
        return this.id;
    }

    public String getNombres() {
        return this.nombres;
    }

    public String getApellidos() {
        return this.apellidos;
    }

    public String getDni() {
        return this.dni;
    }

    public String getCuil() {
        return this.cuil;
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

    public LocalDate getFechaNac() {
        return this.fechaNac;
    }

    public String getFotoUrl() {
        return this.fotoUrl;
    }

    public boolean isHabilitado() {
        return this.habilitado;
    }

    public Comision getComision() {
        return this.comision;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNombres(final String nombres) {
        this.nombres = nombres;
    }

    public void setApellidos(final String apellidos) {
        this.apellidos = apellidos;
    }

    public void setDni(final String dni) {
        this.dni = dni;
    }

    public void setCuil(final String cuil) {
        this.cuil = cuil;
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

    public void setFechaNac(final LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    public void setFotoUrl(final String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public void setHabilitado(final boolean habilitado) {
        this.habilitado = habilitado;
    }

    public void setComision(final Comision comision) {
        this.comision = comision;
    }

    public void setUsuario(final Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Alumno)) return false;
        final Alumno other = (Alumno) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isHabilitado() != other.isHabilitado()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$nombres = this.getNombres();
        final Object other$nombres = other.getNombres();
        if (this$nombres == null ? other$nombres != null : !this$nombres.equals(other$nombres)) return false;
        final Object this$apellidos = this.getApellidos();
        final Object other$apellidos = other.getApellidos();
        if (this$apellidos == null ? other$apellidos != null : !this$apellidos.equals(other$apellidos)) return false;
        final Object this$dni = this.getDni();
        final Object other$dni = other.getDni();
        if (this$dni == null ? other$dni != null : !this$dni.equals(other$dni)) return false;
        final Object this$cuil = this.getCuil();
        final Object other$cuil = other.getCuil();
        if (this$cuil == null ? other$cuil != null : !this$cuil.equals(other$cuil)) return false;
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
        final Object this$fechaNac = this.getFechaNac();
        final Object other$fechaNac = other.getFechaNac();
        if (this$fechaNac == null ? other$fechaNac != null : !this$fechaNac.equals(other$fechaNac)) return false;
        final Object this$fotoUrl = this.getFotoUrl();
        final Object other$fotoUrl = other.getFotoUrl();
        if (this$fotoUrl == null ? other$fotoUrl != null : !this$fotoUrl.equals(other$fotoUrl)) return false;
        final Object this$comision = this.getComision();
        final Object other$comision = other.getComision();
        if (this$comision == null ? other$comision != null : !this$comision.equals(other$comision)) return false;
        final Object this$usuario = this.getUsuario();
        final Object other$usuario = other.getUsuario();
        if (this$usuario == null ? other$usuario != null : !this$usuario.equals(other$usuario)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Alumno;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isHabilitado() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $nombres = this.getNombres();
        result = result * PRIME + ($nombres == null ? 43 : $nombres.hashCode());
        final Object $apellidos = this.getApellidos();
        result = result * PRIME + ($apellidos == null ? 43 : $apellidos.hashCode());
        final Object $dni = this.getDni();
        result = result * PRIME + ($dni == null ? 43 : $dni.hashCode());
        final Object $cuil = this.getCuil();
        result = result * PRIME + ($cuil == null ? 43 : $cuil.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $telefono = this.getTelefono();
        result = result * PRIME + ($telefono == null ? 43 : $telefono.hashCode());
        final Object $direccion = this.getDireccion();
        result = result * PRIME + ($direccion == null ? 43 : $direccion.hashCode());
        final Object $localidad = this.getLocalidad();
        result = result * PRIME + ($localidad == null ? 43 : $localidad.hashCode());
        final Object $fechaNac = this.getFechaNac();
        result = result * PRIME + ($fechaNac == null ? 43 : $fechaNac.hashCode());
        final Object $fotoUrl = this.getFotoUrl();
        result = result * PRIME + ($fotoUrl == null ? 43 : $fotoUrl.hashCode());
        final Object $comision = this.getComision();
        result = result * PRIME + ($comision == null ? 43 : $comision.hashCode());
        final Object $usuario = this.getUsuario();
        result = result * PRIME + ($usuario == null ? 43 : $usuario.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Alumno(id=" + this.getId() + ", nombres=" + this.getNombres() + ", apellidos=" + this.getApellidos() + ", dni=" + this.getDni() + ", cuil=" + this.getCuil() + ", email=" + this.getEmail() + ", telefono=" + this.getTelefono() + ", direccion=" + this.getDireccion() + ", localidad=" + this.getLocalidad() + ", fechaNac=" + this.getFechaNac() + ", fotoUrl=" + this.getFotoUrl() + ", habilitado=" + this.isHabilitado() + ", comision=" + this.getComision() + ", usuario=" + this.getUsuario() + ")";
    }
}
