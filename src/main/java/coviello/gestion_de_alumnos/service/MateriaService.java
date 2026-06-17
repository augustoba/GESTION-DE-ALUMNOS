package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.DocenteResumen;
import coviello.gestion_de_alumnos.dto.HorarioRequest;
import coviello.gestion_de_alumnos.dto.HorarioResponse;
import coviello.gestion_de_alumnos.dto.MateriaRequest;
import coviello.gestion_de_alumnos.dto.MateriaResponse;
import coviello.gestion_de_alumnos.model.AnioCarrera;
import coviello.gestion_de_alumnos.model.Docente;
import coviello.gestion_de_alumnos.model.HorarioClase;
import coviello.gestion_de_alumnos.model.Materia;
import coviello.gestion_de_alumnos.repository.AnioCarreraRepository;
import coviello.gestion_de_alumnos.repository.DocenteRepository;
import coviello.gestion_de_alumnos.repository.HorarioClaseRepository;
import coviello.gestion_de_alumnos.repository.MateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final AnioCarreraRepository anioCarreraRepository;
    private final DocenteRepository docenteRepository;
    private final HorarioClaseRepository horarioRepository;

    public MateriaService(MateriaRepository materiaRepository,
                          AnioCarreraRepository anioCarreraRepository,
                          DocenteRepository docenteRepository,
                          HorarioClaseRepository horarioRepository) {
        this.materiaRepository = materiaRepository;
        this.anioCarreraRepository = anioCarreraRepository;
        this.docenteRepository = docenteRepository;
        this.horarioRepository = horarioRepository;
    }

    public List<MateriaResponse> listarTodas() {
        return materiaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<MateriaResponse> listarPorAnioCarrera(Long anioCarreraId) {
        return materiaRepository.findByAnioCarreraIdOrderByNombre(anioCarreraId)
                .stream().map(this::toResponse).toList();
    }

    public MateriaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public MateriaResponse crear(MateriaRequest req) {
        AnioCarrera anioCarrera = anioCarreraRepository.findById(req.anioCarreraId())
                .orElseThrow(() -> new RuntimeException("Año de carrera no encontrado: " + req.anioCarreraId()));

        Materia materia = new Materia();
        materia.setNombre(req.nombre());
        materia.setDescripcion(req.descripcion());
        materia.setAnioCarrera(anioCarrera);

        if (req.docenteId() != null) {
            Docente docente = docenteRepository.findById(req.docenteId())
                    .orElseThrow(() -> new RuntimeException("Docente no encontrado: " + req.docenteId()));
            materia.setDocente(docente);
        }

        materia = materiaRepository.save(materia);

        if (req.horarios() != null) {
            for (HorarioRequest h : req.horarios()) {
                agregarHorario(materia, h);
            }
        }

        return toResponse(materiaRepository.save(materia));
    }

    @Transactional
    public MateriaResponse actualizar(Long id, MateriaRequest req) {
        Materia materia = findById(id);

        AnioCarrera anioCarrera = anioCarreraRepository.findById(req.anioCarreraId())
                .orElseThrow(() -> new RuntimeException("Año de carrera no encontrado: " + req.anioCarreraId()));

        materia.setNombre(req.nombre());
        materia.setDescripcion(req.descripcion());
        materia.setAnioCarrera(anioCarrera);

        if (req.docenteId() != null) {
            Docente docente = docenteRepository.findById(req.docenteId())
                    .orElseThrow(() -> new RuntimeException("Docente no encontrado: " + req.docenteId()));
            materia.setDocente(docente);
        } else {
            materia.setDocente(null);
        }

        // Reemplazar horarios solo si se envían
        if (req.horarios() != null) {
            materia.getHorarios().clear();
            for (HorarioRequest h : req.horarios()) {
                agregarHorario(materia, h);
            }
        }

        return toResponse(materiaRepository.save(materia));
    }

    @Transactional
    public void eliminar(Long id) {
        Materia materia = findById(id);
        materiaRepository.delete(materia);
    }

    @Transactional
    public MateriaResponse asignarDocente(Long materiaId, Long docenteId) {
        Materia materia = findById(materiaId);
        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado: " + docenteId));
        materia.setDocente(docente);
        return toResponse(materiaRepository.save(materia));
    }

    @Transactional
    public MateriaResponse desasignarDocente(Long materiaId) {
        Materia materia = findById(materiaId);
        materia.setDocente(null);
        return toResponse(materiaRepository.save(materia));
    }

    private void agregarHorario(Materia materia, HorarioRequest h) {
        HorarioClase horario = new HorarioClase();
        horario.setMateria(materia);
        horario.setDiaSemana(h.diaSemana());
        horario.setHoraInicio(h.horaInicio());
        horario.setHoraFin(h.horaFin());
        horario.setFechaInicioCursada(h.fechaInicioCursada());
        horario.setFechaFinCursada(h.fechaFinCursada());
        if (materia.getDocente() != null) {
            horario.setDocente(materia.getDocente());
        }
        materia.getHorarios().add(horario);
    }

    private Materia findById(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + id));
    }

    private MateriaResponse toResponse(Materia m) {
        AnioCarrera anio = m.getAnioCarrera();
        String carreraNombre = null;
        int numeroAnio = 0;
        Long anioCarreraId = null;
        if (anio != null) {
            anioCarreraId = anio.getId();
            numeroAnio = anio.getNumeroAnio();
            if (anio.getCarrera() != null) carreraNombre = anio.getCarrera().getNombre();
        }

        DocenteResumen docenteResumen = null;
        if (m.getDocente() != null) {
            Docente d = m.getDocente();
            docenteResumen = new DocenteResumen(d.getId(), d.getNombres(), d.getApellidos(), d.getEmail());
        }

        List<HorarioResponse> horarios = m.getHorarios().stream()
                .map(h -> new HorarioResponse(h.getId(), h.getDiaSemana(),
                        h.getHoraInicio(), h.getHoraFin(),
                        h.getFechaInicioCursada(), h.getFechaFinCursada()))
                .toList();

        return new MateriaResponse(m.getId(), m.getNombre(), m.getDescripcion(),
                anioCarreraId, numeroAnio, carreraNombre, docenteResumen, horarios);
    }
}
