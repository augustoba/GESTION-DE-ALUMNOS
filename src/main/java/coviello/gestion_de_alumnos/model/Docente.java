package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;

@Entity
public class Docente {
    @Id
    private Long id;
    @Column(nullable = false)
    private String nombres;
    @Column(nullable = false)
    private String apellidos;
    @Column(unique = true, length = 20)
    private String dni;
    @Column(unique = true)
    private String email;
    @Column(length = 50)
    private String telefono;
    @Column(nullable = false)
    private boolean activo = true;
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;

    public Docente() {
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

    public String getEmail() {
        return this.email;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public boolean isActivo() {
        return this.activo;
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

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setTelefono(final String telefono) {
        this.telefono = telefono;
    }

    public void setActivo(final boolean activo) {
        this.activo = activo;
    }

    public void setUsuario(final Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Docente)) return false;
        final Docente other = (Docente) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isActivo() != other.isActivo()) return false;
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
        final Object this$email = this.getEmail();
        final Object other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
        final Object this$telefono = this.getTelefono();
        final Object other$telefono = other.getTelefono();
        if (this$telefono == null ? other$telefono != null : !this$telefono.equals(other$telefono)) return false;
        final Object this$usuario = this.getUsuario();
        final Object other$usuario = other.getUsuario();
        if (this$usuario == null ? other$usuario != null : !this$usuario.equals(other$usuario)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Docente;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isActivo() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $nombres = this.getNombres();
        result = result * PRIME + ($nombres == null ? 43 : $nombres.hashCode());
        final Object $apellidos = this.getApellidos();
        result = result * PRIME + ($apellidos == null ? 43 : $apellidos.hashCode());
        final Object $dni = this.getDni();
        result = result * PRIME + ($dni == null ? 43 : $dni.hashCode());
        final Object $email = this.getEmail();
        result = result * PRIME + ($email == null ? 43 : $email.hashCode());
        final Object $telefono = this.getTelefono();
        result = result * PRIME + ($telefono == null ? 43 : $telefono.hashCode());
        final Object $usuario = this.getUsuario();
        result = result * PRIME + ($usuario == null ? 43 : $usuario.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Docente(id=" + this.getId() + ", nombres=" + this.getNombres() + ", apellidos=" + this.getApellidos() + ", dni=" + this.getDni() + ", email=" + this.getEmail() + ", telefono=" + this.getTelefono() + ", activo=" + this.isActivo() + ", usuario=" + this.getUsuario() + ")";
    }
}
