package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.HorarioClaseRequest;
import coviello.gestion_de_alumnos.dto.HorarioClaseResponse;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HorarioClaseService {

    private final HorarioClaseRepository horarioClaseRepository;
    private final MateriaRepository materiaRepository;
    private final AulaRepository aulaRepository;
    private final DocenteRepository docenteRepository;

    public HorarioClaseService(HorarioClaseRepository horarioClaseRepository,
                               MateriaRepository materiaRepository,
                               AulaRepository aulaRepository,
                               DocenteRepository docenteRepository) {
        this.horarioClaseRepository = horarioClaseRepository;
        this.materiaRepository = materiaRepository;
        this.aulaRepository = aulaRepository;
        this.docenteRepository = docenteRepository;
    }

    public List<HorarioClaseResponse> listarPorMateria(Long materiaId) {
        return horarioClaseRepository.findByMateriaId(materiaId).stream()
                .map(this::toResponse).toList();
    }

    public List<HorarioClaseResponse> listarPorDocente(Long docenteId) {
        return horarioClaseRepository.findByDocenteId(docenteId).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public HorarioClaseResponse crear(Long materiaId, HorarioClaseRequest req) {
        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + materiaId));

        HorarioClase horario = new HorarioClase();
        horario.setMateria(materia);
        horario.setDiaSemana(req.diaSemana());
        horario.setHoraInicio(req.horaInicio());
        horario.setHoraFin(req.horaFin());
        horario.setFechaInicioCursada(req.fechaInicioCursada());
        horario.setFechaFinCursada(req.fechaFinCursada());

        if (req.aulaId() != null) {
            aulaRepository.findById(req.aulaId())
                    .ifPresent(horario::setAula);
        }
        if (req.docenteId() != null) {
            docenteRepository.findById(req.docenteId())
                    .ifPresent(horario::setDocente);
        }

        return toResponse(horarioClaseRepository.save(horario));
    }

    @Transactional
    public HorarioClaseResponse actualizar(Long horarioId, HorarioClaseRequest req) {
        HorarioClase horario = horarioClaseRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado: " + horarioId));

        horario.setDiaSemana(req.diaSemana());
        horario.setHoraInicio(req.horaInicio());
        horario.setHoraFin(req.horaFin());
        horario.setFechaInicioCursada(req.fechaInicioCursada());
        horario.setFechaFinCursada(req.fechaFinCursada());

        if (req.aulaId() != null) {
            aulaRepository.findById(req.aulaId()).ifPresent(horario::setAula);
        }
        if (req.docenteId() != null) {
            docenteRepository.findById(req.docenteId()).ifPresent(horario::setDocente);
        }

        return toResponse(horarioClaseRepository.save(horario));
    }

    @Transactional
    public void eliminar(Long horarioId) {
        if (!horarioClaseRepository.existsById(horarioId)) {
            throw new RuntimeException("Horario no encontrado: " + horarioId);
        }
        horarioClaseRepository.deleteById(horarioId);
    }

    private HorarioClaseResponse toResponse(HorarioClase h) {
        return new HorarioClaseResponse(
                h.getId(), h.getDiaSemana(), h.getHoraInicio(), h.getHoraFin(),
                h.getFechaInicioCursada(), h.getFechaFinCursada(),
                h.getAula() != null ? h.getAula().getNombre() : null,
                h.getAula() != null ? h.getAula().getId() : null,
                h.getDocente() != null ? h.getDocente().getNombres() + " " + h.getDocente().getApellidos() : null,
                h.getDocente() != null ? h.getDocente().getId() : null
        );
    }
}
