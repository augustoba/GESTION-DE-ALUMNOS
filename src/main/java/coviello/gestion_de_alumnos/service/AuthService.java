package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.LoginRequest;
import coviello.gestion_de_alumnos.dto.LoginResponse;
import coviello.gestion_de_alumnos.dto.RegistroRequest;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.model.Usuario;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.RolRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import coviello.gestion_de_alumnos.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
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

        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("La contraseña no puede estar vacía");
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
        alumno.setStatus(false);
        alumno.setUsuario(usuario);
        alumnoRepository.save(alumno);

        try {
            emailService.enviarBienvenida(request.email(), request.nombres());
        } catch (MailException e) {
            log.error("No se pudo enviar el email de bienvenida a {}: {}", request.email(), e.getMessage());
        }
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String token = jwtUtil.generateToken(usuario);
        return new LoginResponse(token, usuario.getUsername(), usuario.getRol().getNombre());
    }

}
