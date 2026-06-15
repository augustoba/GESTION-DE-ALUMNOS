package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.InscripcionMateriaRequest;
import coviello.gestion_de_alumnos.dto.InscripcionMateriaResponse;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InscripcionMateriaService {

    private final InscripcionMateriaRepository inscripcionRepository;
    private final AlumnoRepository alumnoRepository;
    private final MateriaRepository materiaRepository;
    private final PeriodoInscripcionRepository periodoRepository;
    private final UsuarioRepository usuarioRepository;

    public InscripcionMateriaService(InscripcionMateriaRepository inscripcionRepository,
                                     AlumnoRepository alumnoRepository,
                                     MateriaRepository materiaRepository,
                                     PeriodoInscripcionRepository periodoRepository,
                                     UsuarioRepository usuarioRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.alumnoRepository = alumnoRepository;
        this.materiaRepository = materiaRepository;
        this.periodoRepository = periodoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // El alumno solicita inscripción a las materias que eligió
    @Transactional
    public List<InscripcionMateriaResponse> solicitarInscripcion(Long alumnoId, InscripcionMateriaRequest req) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + alumnoId));

        PeriodoInscripcion periodo = periodoRepository
                .findFirstByActivoTrueAndTipo(TipoPeriodoInscripcion.REINSCRIPCION)
                .orElseThrow(() -> new RuntimeException("No hay período de reinscripción activo"));

        return req.materiaIds().stream().map(materiaId -> {
            if (inscripcionRepository.existsByAlumnoIdAndMateriaIdAndPeriodoInscripcionId(
                    alumnoId, materiaId, periodo.getId())) {
                throw new RuntimeException("Ya existe una solicitud para la materia ID: " + materiaId);
            }

            Materia materia = materiaRepository.findById(materiaId)
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + materiaId));

            InscripcionMateria inscripcion = new InscripcionMateria();
            inscripcion.setAlumno(alumno);
            inscripcion.setMateria(materia);
            inscripcion.setPeriodoInscripcion(periodo);
            inscripcion.setEstado(EstadoInscripcionMateria.SOLICITADA);
            inscripcion.setFechaSolicitud(LocalDateTime.now());

            return toResponse(inscripcionRepository.save(inscripcion));
        }).toList();
    }

    public List<InscripcionMateriaResponse> listarPorAlumno(Long alumnoId) {
        return inscripcionRepository.findByAlumnoId(alumnoId).stream()
                .map(this::toResponse).toList();
    }

    public List<InscripcionMateriaResponse> listarPorAlumnoYPeriodo(Long alumnoId, Long periodoId) {
        return inscripcionRepository.findByAlumnoIdAndPeriodoInscripcionId(alumnoId, periodoId).stream()
                .map(this::toResponse).toList();
    }

    public List<InscripcionMateriaResponse> listarPorMateria(Long materiaId, EstadoInscripcionMateria estado) {
        return inscripcionRepository.findByMateriaIdAndEstado(materiaId, estado).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public InscripcionMateriaResponse resolverInscripcion(Long inscripcionId,
                                                           EstadoInscripcionMateria nuevoEstado,
                                                           String adminUsername) {
        InscripcionMateria inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada: " + inscripcionId));

        inscripcion.setEstado(nuevoEstado);
        inscripcion.setFechaResolucion(LocalDateTime.now());

        usuarioRepository.findByUsername(adminUsername)
                .ifPresent(inscripcion::setResueltoPor);

        return toResponse(inscripcionRepository.save(inscripcion));
    }

    private InscripcionMateriaResponse toResponse(InscripcionMateria i) {
        String carreraNombre = null;
        int anioCarrera = 0;
        if (i.getMateria().getAnioCarrera() != null) {
            anioCarrera = i.getMateria().getAnioCarrera().getNumeroAnio();
            if (i.getMateria().getAnioCarrera().getCarrera() != null) {
                carreraNombre = i.getMateria().getAnioCarrera().getCarrera().getNombre();
            }
        }
        return new InscripcionMateriaResponse(
                i.getId(),
                i.getAlumno().getId(),
                i.getAlumno().getNombres() + " " + i.getAlumno().getApellidos(),
                i.getMateria().getId(),
                i.getMateria().getNombre(),
                carreraNombre, anioCarrera,
                i.getEstado(), i.getFechaSolicitud()
        );
    }
}
