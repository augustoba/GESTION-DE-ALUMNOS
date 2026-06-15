package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.AdminCrearRequest;
import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.model.Usuario;
import coviello.gestion_de_alumnos.repository.RolRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@Slf4j
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioAdminService(UsuarioRepository usuarioRepository,
                               RolRepository rolRepository,
                               PasswordEncoder passwordEncoder,
                               EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<Usuario> listarAdmins() {
        Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() != null && u.getRol().getId().equals(rolAdmin.getId()))
                .toList();
    }

    @Transactional
    public Usuario crearAdmin(AdminCrearRequest req) {
        if (usuarioRepository.findByUsername(req.email()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con el email: " + req.email());
        }

        String rolNombre = req.rol() != null ? req.rol().toUpperCase() : "ADMIN";
        if (!List.of("ADMIN", "DOCENTE").contains(rolNombre)) {
            throw new RuntimeException("Rol inválido. Solo se permite ADMIN o DOCENTE.");
        }

        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol " + rolNombre + " no encontrado"));

        String passwordTemporal = generarPassword();

        Usuario usuario = new Usuario();
        usuario.setUsername(req.email());
        usuario.setPassword(passwordEncoder.encode(passwordTemporal));
        usuario.setRol(rol);
        usuario = usuarioRepository.save(usuario);

        try {
            emailService.enviarBienvenidaAdmin(req.email(), req.nombres(), passwordTemporal);
        } catch (Exception e) {
            log.error("No se pudo enviar email de bienvenida admin a {}: {}", req.email(), e.getMessage());
        }

        return usuario;
    }

    @Transactional
    public void desactivarAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        if ("SUPER_ADMIN".equals(usuario.getRol().getNombre())) {
            throw new RuntimeException("No se puede desactivar al super admin");
        }

        // Cambiar rol a inactivo o similar — en este modelo simplemente lo eliminamos
        // (en un sistema real habría un campo activo en Usuario)
        usuarioRepository.delete(usuario);
    }

    private String generarPassword() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(chars.charAt(rng.nextInt(chars.length())));
        return sb.toString();
    }
}
