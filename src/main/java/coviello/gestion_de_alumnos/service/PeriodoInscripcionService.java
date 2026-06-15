package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.PeriodoInscripcionRequest;
import coviello.gestion_de_alumnos.dto.PeriodoInscripcionResponse;
import coviello.gestion_de_alumnos.model.PeriodoInscripcion;
import coviello.gestion_de_alumnos.model.TipoPeriodoInscripcion;
import coviello.gestion_de_alumnos.repository.PeriodoInscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeriodoInscripcionService {

    private final PeriodoInscripcionRepository periodoRepository;

    public PeriodoInscripcionService(PeriodoInscripcionRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    public List<PeriodoInscripcionResponse> listarTodos() {
        return periodoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PeriodoInscripcionResponse obtenerActivo(TipoPeriodoInscripcion tipo) {
        return periodoRepository.findFirstByActivoTrueAndTipo(tipo)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("No hay período activo de tipo: " + tipo));
    }

    @Transactional
    public PeriodoInscripcionResponse crear(PeriodoInscripcionRequest req) {
        if (periodoRepository.existsByActivoTrueAndTipo(req.tipo())) {
            throw new RuntimeException("Ya existe un período activo de tipo: " + req.tipo()
                    + ". Cerralo antes de crear uno nuevo.");
        }

        PeriodoInscripcion periodo = new PeriodoInscripcion();
        periodo.setNombre(req.nombre());
        periodo.setFechaInicio(req.fechaInicio());
        periodo.setFechaFin(req.fechaFin());
        periodo.setTipo(req.tipo());
        periodo.setActivo(true);
        return toResponse(periodoRepository.save(periodo));
    }

    @Transactional
    public PeriodoInscripcionResponse cerrar(Long id) {
        PeriodoInscripcion periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Período no encontrado: " + id));
        periodo.setActivo(false);
        return toResponse(periodoRepository.save(periodo));
    }

    private PeriodoInscripcionResponse toResponse(PeriodoInscripcion p) {
        return new PeriodoInscripcionResponse(p.getId(), p.getNombre(), p.getFechaInicio(),
                p.getFechaFin(), p.isActivo(), p.getTipo());
    }
}
