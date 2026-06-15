package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.CalendarioAcademicoRequest;
import coviello.gestion_de_alumnos.dto.CalendarioAcademicoResponse;
import coviello.gestion_de_alumnos.model.CalendarioAcademico;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.TipoCalendario;
import coviello.gestion_de_alumnos.repository.CalendarioAcademicoRepository;
import coviello.gestion_de_alumnos.repository.CarreraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalendarioAcademicoService {

    private final CalendarioAcademicoRepository calendarioRepository;
    private final CarreraRepository carreraRepository;

    public CalendarioAcademicoService(CalendarioAcademicoRepository calendarioRepository,
                                      CarreraRepository carreraRepository) {
        this.calendarioRepository = calendarioRepository;
        this.carreraRepository = carreraRepository;
    }

    public List<CalendarioAcademicoResponse> listarPorRango(LocalDate desde, LocalDate hasta) {
        return calendarioRepository.findByFechaBetweenOrderByFecha(desde, hasta).stream()
                .map(this::toResponse).toList();
    }

    public List<CalendarioAcademicoResponse> listarPorTipo(TipoCalendario tipo) {
        return calendarioRepository.findByTipo(tipo).stream()
                .map(this::toResponse).toList();
    }

    public List<CalendarioAcademicoResponse> listarDiasNoCursables(Long carreraId,
                                                                     LocalDate desde, LocalDate hasta) {
        return calendarioRepository.findDiasNoCursablesByCarreraAndRango(carreraId, desde, hasta).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public CalendarioAcademicoResponse crear(CalendarioAcademicoRequest req) {
        CalendarioAcademico evento = new CalendarioAcademico();
        evento.setFecha(req.fecha());
        evento.setTipo(req.tipo());
        evento.setDescripcion(req.descripcion());
        evento.setAfectaA(req.afectaA());

        if (req.carreraId() != null) {
            Carrera carrera = carreraRepository.findById(req.carreraId())
                    .orElseThrow(() -> new RuntimeException("Carrera no encontrada: " + req.carreraId()));
            evento.setCarrera(carrera);
        }

        return toResponse(calendarioRepository.save(evento));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!calendarioRepository.existsById(id)) {
            throw new RuntimeException("Evento de calendario no encontrado: " + id);
        }
        calendarioRepository.deleteById(id);
    }

    private CalendarioAcademicoResponse toResponse(CalendarioAcademico c) {
        return new CalendarioAcademicoResponse(
                c.getId(), c.getFecha(), c.getTipo(), c.getDescripcion(), c.getAfectaA(),
                c.getCarrera() != null ? c.getCarrera().getNombre() : null
        );
    }
}
