package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.AlumnoAdminResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.AnioCarrera;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Comision;
import coviello.gestion_de_alumnos.model.Usuario;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public AlumnoService(AlumnoRepository alumnoRepository,
                         UsuarioRepository usuarioRepository,
                         EmailService emailService) {
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    public Page<AlumnoAdminResponse> listarPaginados(Pageable pageable) {
        return alumnoRepository.findAll(pageable).map(this::toAdminResponse);
    }

    public Page<AlumnoAdminResponse> buscarPorNombre(String nombre, String apellido, Pageable pageable) {
        return alumnoRepository
                .findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, apellido, pageable)
                .map(this::toAdminResponse);
    }

    public List<AlumnoAdminResponse> listarHabilitados() {
        return alumnoRepository.findByHabilitadoTrue().stream()
                .map(this::toAdminResponse).toList();
    }

    public List<AlumnoAdminResponse> listarPorCarrera(Long carreraId) {
        return alumnoRepository.findByComision_AnioCarrera_CarreraId(carreraId).stream()
                .map(this::toAdminResponse).toList();
    }

    public List<AlumnoAdminResponse> listarPorComision(Long comisionId) {
        return alumnoRepository.findByComisionId(comisionId).stream()
                .map(this::toAdminResponse).toList();
    }

    public AlumnoAdminResponse obtenerPorId(Long id) {
        return toAdminResponse(alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id)));
    }

    public AlumnoAdminResponse habilitar(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
        alumno.setHabilitado(true);
        return toAdminResponse(alumnoRepository.save(alumno));
    }

    public AlumnoAdminResponse deshabilitar(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
        alumno.setHabilitado(false);
        return toAdminResponse(alumnoRepository.save(alumno));
    }

    public void reenviarActivacion(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));

        Usuario usuario = alumno.getUsuario();
        if (usuario == null) {
            throw new RuntimeException("El alumno no tiene una cuenta de usuario asociada.");
        }
        if (usuario.getTokenActivacion() == null) {
            throw new RuntimeException("La cuenta ya fue activada. El alumno puede iniciar sesión normalmente.");
        }

        String nuevoToken = UUID.randomUUID().toString();
        usuario.setTokenActivacion(nuevoToken);
        usuario.setTokenActivacionExpiracion(LocalDateTime.now().plusHours(72));
        usuarioRepository.save(usuario);

        String nombre = alumno.getNombres() + " " + alumno.getApellidos();
        String carreraNombre = resolverCarreraNombre(alumno);
        emailService.enviarActivacionCuenta(alumno.getEmail(), nombre, carreraNombre, nuevoToken);
    }

    private String resolverCarreraNombre(Alumno a) {
        if (a.getComision() != null && a.getComision().getAnioCarrera() != null
                && a.getComision().getAnioCarrera().getCarrera() != null) {
            return a.getComision().getAnioCarrera().getCarrera().getNombre();
        }
        return "la carrera seleccionada";
    }

    private AlumnoAdminResponse toAdminResponse(Alumno a) {
        Long comisionId = null;
        String comisionNombre = null;
        Integer anioNumero = null;
        Long carreraId = null;
        String carreraNombre = null;
        Comision com = a.getComision();
        if (com != null) {
            comisionId = com.getId();
            comisionNombre = com.getNombre();
            AnioCarrera anio = com.getAnioCarrera();
            if (anio != null) {
                anioNumero = anio.getNumeroAnio();
                Carrera carrera = anio.getCarrera();
                if (carrera != null) {
                    carreraId = carrera.getId();
                    carreraNombre = carrera.getNombre();
                }
            }
        }
        return new AlumnoAdminResponse(
                a.getId(), a.getNombres(), a.getApellidos(),
                a.getDni(), a.getEmail(), a.getTelefono(), a.isHabilitado(),
                comisionId, comisionNombre, anioNumero, carreraId, carreraNombre
        );
    }

    public AlumnoAdminResponse actualizar(Long id, Alumno datos) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));

        if (datos.getNombres() != null) alumno.setNombres(datos.getNombres());
        if (datos.getApellidos() != null) alumno.setApellidos(datos.getApellidos());
        if (datos.getTelefono() != null) alumno.setTelefono(datos.getTelefono());
        if (datos.getDireccion() != null) alumno.setDireccion(datos.getDireccion());
        if (datos.getLocalidad() != null) alumno.setLocalidad(datos.getLocalidad());
        if (datos.getFechaNac() != null) alumno.setFechaNac(datos.getFechaNac());
        if (datos.getFotoUrl() != null) alumno.setFotoUrl(datos.getFotoUrl());

        if (datos.getDni() != null && !datos.getDni().equals(alumno.getDni())) {
            if (alumnoRepository.findByDni(datos.getDni()).isPresent()) {
                throw new RuntimeException("Ya existe un alumno con el DNI: " + datos.getDni());
            }
            alumno.setDni(datos.getDni());
        }

        if (datos.getEmail() != null && !datos.getEmail().equals(alumno.getEmail())) {
            if (alumnoRepository.findByEmail(datos.getEmail()).isPresent()) {
                throw new RuntimeException("Ya existe un alumno con el email: " + datos.getEmail());
            }
            alumno.setEmail(datos.getEmail());
        }

        return toAdminResponse(alumnoRepository.save(alumno));
    }
}
