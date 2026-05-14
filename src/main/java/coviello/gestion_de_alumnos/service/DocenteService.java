package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.DocenteRequest;
import coviello.gestion_de_alumnos.dto.DocenteResponse;
import coviello.gestion_de_alumnos.dto.MateriaDetalleDocente;
import coviello.gestion_de_alumnos.model.AnioCarrera;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Docente;
import coviello.gestion_de_alumnos.model.Materia;
import coviello.gestion_de_alumnos.model.Rol;
import coviello.gestion_de_alumnos.model.Usuario;
import coviello.gestion_de_alumnos.repository.DocenteRepository;
import coviello.gestion_de_alumnos.repository.MateriaRepository;
import coviello.gestion_de_alumnos.repository.RolRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocenteService {

    private final DocenteRepository docenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final MateriaRepository materiaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public DocenteService(DocenteRepository docenteRepository,
                          UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          MateriaRepository materiaRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.docenteRepository = docenteRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.materiaRepository = materiaRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<DocenteResponse> listarTodos() {
        return docenteRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DocenteResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public DocenteResponse crear(DocenteRequest req) {
        if (usuarioRepository.findByUsername(req.email()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con el email: " + req.email());
        }

        Rol rolDocente = rolRepository.findByNombre("DOCENTE")
                .orElseThrow(() -> new RuntimeException("Rol DOCENTE no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setUsername(req.email());
        usuario.setPassword(passwordEncoder.encode(req.dni()));
        usuario.setRol(rolDocente);
        usuario = usuarioRepository.save(usuario);

        Docente docente = new Docente();
        docente.setNombres(req.nombres());
        docente.setApellidos(req.apellidos());
        docente.setDni(req.dni());
        docente.setEmail(req.email());
        docente.setTelefono(req.telefono());
        docente.setActivo(true);
        docente.setUsuario(usuario);

        DocenteResponse response = toResponse(docenteRepository.save(docente));
        try { emailService.enviarBienvenidaDocente(req.email(), req.nombres(), req.dni()); } catch (Exception ignored) {}
        return response;
    }

    @Transactional
    public DocenteResponse actualizar(Long id, DocenteRequest req) {
        Docente docente = findById(id);

        // Si cambió el email, actualizar también el username del usuario
        if (!docente.getEmail().equals(req.email())) {
            if (usuarioRepository.findByUsername(req.email()).isPresent()) {
                throw new RuntimeException("Ya existe un usuario con el email: " + req.email());
            }
            Usuario usuario = docente.getUsuario();
            usuario.setUsername(req.email());
            usuarioRepository.save(usuario);
        }

        docente.setNombres(req.nombres());
        docente.setApellidos(req.apellidos());
        docente.setDni(req.dni());
        docente.setEmail(req.email());
        docente.setTelefono(req.telefono());

        return toResponse(docenteRepository.save(docente));
    }

    @Transactional
    public DocenteResponse cambiarEstado(Long id, boolean activo) {
        Docente docente = findById(id);
        docente.setActivo(activo);
        return toResponse(docenteRepository.save(docente));
    }

    @Transactional(readOnly = true)
    public List<MateriaDetalleDocente> obtenerMaterias(Long docenteId) {
        findById(docenteId);
        return materiaRepository.findByDocente_Id(docenteId).stream()
                .map(m -> {
                    AnioCarrera anio = m.getAnioCarrera();
                    String carreraNombre = null;
                    int numeroAnio = 0;
                    if (anio != null) {
                        numeroAnio = anio.getNumeroAnio();
                        Carrera carrera = anio.getCarrera();
                        if (carrera != null) carreraNombre = carrera.getNombre();
                    }
                    return new MateriaDetalleDocente(
                            m.getId(), m.getNombre(), m.getDescripcion(),
                            carreraNombre, numeroAnio,
                            m.getDiaSemana(), m.getHoraInicio(), m.getHoraFin(), m.getAula()
                    );
                })
                .toList();
    }

    private Docente findById(Long id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
    }

    private DocenteResponse toResponse(Docente d) {
        return new DocenteResponse(
                d.getId(), d.getNombres(), d.getApellidos(),
                d.getDni(), d.getEmail(), d.getTelefono(), d.isActivo()
        );
    }
}
