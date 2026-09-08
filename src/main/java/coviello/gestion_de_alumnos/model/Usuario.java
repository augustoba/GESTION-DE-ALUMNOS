package coviello.gestion_de_alumnos.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @ManyToOne(fetch = FetchType.EAGER)
    private Rol rol;
    @Column(unique = true)
    private String tokenActivacion;
    private LocalDateTime tokenActivacionExpiracion;
    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public Rol getRol() {
        return this.rol;
    }

    public String getTokenActivacion() {
        return this.tokenActivacion;
    }

    public LocalDateTime getTokenActivacionExpiracion() {
        return this.tokenActivacionExpiracion;
    }

    public boolean isMustChangePassword() {
        return this.mustChangePassword;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setRol(final Rol rol) {
        this.rol = rol;
    }

    public void setTokenActivacion(final String tokenActivacion) {
        this.tokenActivacion = tokenActivacion;
    }

    public void setTokenActivacionExpiracion(final LocalDateTime tokenActivacionExpiracion) {
        this.tokenActivacionExpiracion = tokenActivacionExpiracion;
    }

    public void setMustChangePassword(final boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Usuario)) return false;
        final Usuario other = (Usuario) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.isMustChangePassword() != other.isMustChangePassword()) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$username = this.getUsername();
        final Object other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) return false;
        final Object this$password = this.getPassword();
        final Object other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) return false;
        final Object this$rol = this.getRol();
        final Object other$rol = other.getRol();
        if (this$rol == null ? other$rol != null : !this$rol.equals(other$rol)) return false;
        final Object this$tokenActivacion = this.getTokenActivacion();
        final Object other$tokenActivacion = other.getTokenActivacion();
        if (this$tokenActivacion == null ? other$tokenActivacion != null : !this$tokenActivacion.equals(other$tokenActivacion)) return false;
        final Object this$tokenActivacionExpiracion = this.getTokenActivacionExpiracion();
        final Object other$tokenActivacionExpiracion = other.getTokenActivacionExpiracion();
        if (this$tokenActivacionExpiracion == null ? other$tokenActivacionExpiracion != null : !this$tokenActivacionExpiracion.equals(other$tokenActivacionExpiracion)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Usuario;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + (this.isMustChangePassword() ? 79 : 97);
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $username = this.getUsername();
        result = result * PRIME + ($username == null ? 43 : $username.hashCode());
        final Object $password = this.getPassword();
        result = result * PRIME + ($password == null ? 43 : $password.hashCode());
        final Object $rol = this.getRol();
        result = result * PRIME + ($rol == null ? 43 : $rol.hashCode());
        final Object $tokenActivacion = this.getTokenActivacion();
        result = result * PRIME + ($tokenActivacion == null ? 43 : $tokenActivacion.hashCode());
        final Object $tokenActivacionExpiracion = this.getTokenActivacionExpiracion();
        result = result * PRIME + ($tokenActivacionExpiracion == null ? 43 : $tokenActivacionExpiracion.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Usuario(id=" + this.getId() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", rol=" + this.getRol() + ", tokenActivacion=" + this.getTokenActivacion() + ", tokenActivacionExpiracion=" + this.getTokenActivacionExpiracion() + ", mustChangePassword=" + this.isMustChangePassword() + ")";
    }

    public Usuario() {
    }

    public Usuario(final Long id, final String username, final String password, final Rol rol, final String tokenActivacion, final LocalDateTime tokenActivacionExpiracion, final boolean mustChangePassword) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.tokenActivacion = tokenActivacion;
        this.tokenActivacionExpiracion = tokenActivacionExpiracion;
        this.mustChangePassword = mustChangePassword;
    }
}
