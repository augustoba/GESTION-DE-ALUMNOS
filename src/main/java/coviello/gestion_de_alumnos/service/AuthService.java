package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.ActivarCuentaRequest;
import coviello.gestion_de_alumnos.dto.LoginRequest;
import coviello.gestion_de_alumnos.dto.LoginResponse;
import coviello.gestion_de_alumnos.dto.RegistroRequest;
import coviello.gestion_de_alumnos.dto.ValidarTokenResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.model.Usuario;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.RolRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import coviello.gestion_de_alumnos.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthService(UsuarioRepository usuarioRepository, AlumnoRepository alumnoRepository,
                       RolRepository rolRepository, PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                       EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoRepository = alumnoRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public void registrar(RegistroRequest request) {
        if (usuarioRepository.findByUsername(request.email()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (alumnoRepository.findByDni(request.dni()).isPresent()) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        Rol rolAlumno = rolRepository.findByNombre("ALUMNO")
                .orElseThrow(() -> new RuntimeException("Rol ALUMNO no encontrado en la base de datos"));

        Usuario usuario = new Usuario();
        usuario.setUsername(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(rolAlumno);
        usuarioRepository.save(usuario);

        Alumno alumno = new Alumno();
        alumno.setNombres(request.nombres());
        alumno.setApellidos(request.apellidos());
        alumno.setDni(request.dni());
        alumno.setEmail(request.email());
        alumno.setHabilitado(false);
        alumno.setUsuario(usuario);
        alumnoRepository.save(alumno);

        try {
            emailService.enviarBienvenida(request.email(), request.nombres());
        } catch (MailException e) {
            log.error("No se pudo enviar el email de bienvenida a {}: {}", request.email(), e.getMessage());
        }
    }

    public void recuperarPassword(String email) {
        Usuario usuario = usuarioRepository.findByUsername(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta registrada con ese email"));

        String nuevaPassword = generarPasswordAleatoria();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        String nombre = alumnoRepository.findByEmail(email)
                .map(Alumno::getNombres)
                .orElse("Usuario");

        try {
            emailService.enviarNuevaContrasena(email, nombre, nuevaPassword);
        } catch (MailException e) {
            log.error("No se pudo enviar el email de recuperación a {}: {}", email, e.getMessage());
            throw new RuntimeException("Error al enviar el email. Intentá nuevamente más tarde.");
        }
    }

    public ValidarTokenResponse validarToken(String token) {
        Usuario usuario = usuarioRepository.findByTokenActivacion(token)
                .orElseThrow(() -> new RuntimeException("El enlace de activación no es válido"));

        if (usuario.getTokenActivacionExpiracion() == null ||
                LocalDateTime.now().isAfter(usuario.getTokenActivacionExpiracion())) {
            throw new RuntimeException("El enlace de activación expiró. Contactá a la administración.");
        }

        String nombres = alumnoRepository.findByEmail(usuario.getUsername())
                .map(Alumno::getNombres)
                .orElse("");

        return new ValidarTokenResponse(usuario.getUsername(), nombres);
    }

    public void activarCuenta(ActivarCuentaRequest request) {
        Usuario usuario = usuarioRepository.findByTokenActivacion(request.token())
                .orElseThrow(() -> new RuntimeException("El enlace de activación no es válido"));

        if (usuario.getTokenActivacionExpiracion() == null ||
                LocalDateTime.now().isAfter(usuario.getTokenActivacionExpiracion())) {
            throw new RuntimeException("El enlace de activación expiró. Contactá a la administración.");
        }

        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setTokenActivacion(null);
        usuario.setTokenActivacionExpiracion(null);
        usuarioRepository.save(usuario);
    }

    private String generarPasswordAleatoria() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String token = jwtUtil.generateToken(usuario);

        Boolean habilitado = null;
        if ("ALUMNO".equals(usuario.getRol().getNombre())) {
            habilitado = alumnoRepository.findByEmail(usuario.getUsername())
                    .map(Alumno::isHabilitado)
                    .orElse(false);
        }

        return new LoginResponse(token, usuario.getUsername(), usuario.getRol().getNombre(), habilitado,
                usuario.isMustChangePassword());
    }

    public void cambiarPassword(String username, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuario.setMustChangePassword(false);
        usuarioRepository.save(usuario);
    }

    public LoginResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token requerido");
        }
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!jwtUtil.isTokenValid(token, usuario)) {
            throw new RuntimeException("Token inválido o expirado");
        }
        String newToken = jwtUtil.generateToken(usuario);
        Boolean habilitado = null;
        if ("ALUMNO".equals(usuario.getRol().getNombre())) {
            habilitado = alumnoRepository.findByEmail(usuario.getUsername())
                    .map(Alumno::isHabilitado)
                    .orElse(false);
        }
        return new LoginResponse(newToken, usuario.getUsername(), usuario.getRol().getNombre(),
                habilitado, usuario.isMustChangePassword());
    }
}
