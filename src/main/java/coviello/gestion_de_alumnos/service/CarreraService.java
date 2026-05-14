package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.*;
import coviello.gestion_de_alumnos.model.AnioCarrera;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Docente;
import coviello.gestion_de_alumnos.model.Materia;
import coviello.gestion_de_alumnos.repository.AnioCarreraRepository;
import coviello.gestion_de_alumnos.repository.CarreraRepository;
import coviello.gestion_de_alumnos.repository.DocenteRepository;
import coviello.gestion_de_alumnos.repository.MateriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;
    private final AnioCarreraRepository anioCarreraRepository;
    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;

    public CarreraService(CarreraRepository carreraRepository,
                          AnioCarreraRepository anioCarreraRepository,
                          MateriaRepository materiaRepository,
                          DocenteRepository docenteRepository) {
        this.carreraRepository = carreraRepository;
        this.anioCarreraRepository = anioCarreraRepository;
        this.materiaRepository = materiaRepository;
        this.docenteRepository = docenteRepository;
    }

    public List<Carrera> obtenerTodas() {
        return carreraRepository.findAll();
    }

    public Carrera obtenerPorId(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public CarreraDetalleResponse obtenerDetalle(Long id) {
        Carrera carrera = obtenerPorId(id);
        List<AnioCarreraResponse> anios = anioCarreraRepository
                .findByCarreraIdOrderByNumeroAnio(id)
                .stream()
                .map(this::toAnioResponse)
                .toList();
        return new CarreraDetalleResponse(
                carrera.getId(), carrera.getNombre(), carrera.getDescripcion(),
                carrera.getActiva(), carrera.getCupoMaximo(), anios
        );
    }

    public Carrera crearCarrera(CarreraRequest req) {
        Carrera c = new Carrera();
        c.setNombre(req.nombre());
        c.setDescripcion(req.descripcion());
        c.setActiva(req.activa() != null ? req.activa() : true);
        c.setCupoMaximo(req.cupoMaximo());
        return carreraRepository.save(c);
    }

    public Carrera actualizarCarrera(Long id, CarreraRequest req) {
        Carrera c = obtenerPorId(id);
        c.setNombre(req.nombre());
        c.setDescripcion(req.descripcion());
        if (req.activa() != null) c.setActiva(req.activa());
        c.setCupoMaximo(req.cupoMaximo());
        return carreraRepository.save(c);
    }

    public void eliminarCarrera(Long id) {
        if (!carreraRepository.existsById(id)) {
            throw new RuntimeException("Carrera no encontrada con ID: " + id);
        }
        carreraRepository.deleteById(id);
    }

    public List<Carrera> buscarPorNombre(String nombre) {
        return carreraRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // ── Años ────────────────────────────────────────────────────

    public AnioCarreraResponse agregarAnio(Long carreraId, AnioCarreraRequest req) {
        Carrera carrera = obtenerPorId(carreraId);
        if (anioCarreraRepository.existsByCarreraIdAndNumeroAnio(carreraId, req.numeroAnio())) {
            throw new RuntimeException("El año " + req.numeroAnio() + " ya existe en esta carrera");
        }
        AnioCarrera anio = new AnioCarrera();
        anio.setCarrera(carrera);
        anio.setNumeroAnio(req.numeroAnio());
        return toAnioResponse(anioCarreraRepository.save(anio));
    }

    public void eliminarAnio(Long anioId) {
        if (!anioCarreraRepository.existsById(anioId)) {
            throw new RuntimeException("Año no encontrado con ID: " + anioId);
        }
        anioCarreraRepository.deleteById(anioId);
    }

    // ── Materias ─────────────────────────────────────────────────

    public MateriaResponse agregarMateria(Long anioId, MateriaRequest req) {
        AnioCarrera anio = anioCarreraRepository.findById(anioId)
                .orElseThrow(() -> new RuntimeException("Año no encontrado con ID: " + anioId));
        Materia m = new Materia();
        aplicarCamposMateria(m, req);
        m.setAnioCarrera(anio);
        return toMateriaResponse(materiaRepository.save(m));
    }

    public MateriaResponse actualizarMateria(Long materiaId, MateriaRequest req) {
        Materia m = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada con ID: " + materiaId));
        aplicarCamposMateria(m, req);
        return toMateriaResponse(materiaRepository.save(m));
    }

    public void eliminarMateria(Long materiaId) {
        if (!materiaRepository.existsById(materiaId)) {
            throw new RuntimeException("Materia no encontrada con ID: " + materiaId);
        }
        materiaRepository.deleteById(materiaId);
    }

    public List<DocenteResumen> listarDocentes() {
        return docenteRepository.findByActivoTrue().stream()
                .map(d -> new DocenteResumen(d.getId(), d.getNombres(), d.getApellidos(), d.getEmail()))
                .toList();
    }

    // ── Helpers ──────────────────────────────────────────────────

    private void aplicarCamposMateria(Materia m, MateriaRequest req) {
        m.setNombre(req.nombre());
        m.setDescripcion(req.descripcion());
        m.setDiaSemana(req.diaSemana());
        m.setHoraInicio(req.horaInicio());
        m.setHoraFin(req.horaFin());
        m.setAula(req.aula());
        if (req.docenteId() != null) {
            Docente docente = docenteRepository.findById(req.docenteId())
                    .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + req.docenteId()));
            m.setDocente(docente);
        } else {
            m.setDocente(null);
        }
    }

    private AnioCarreraResponse toAnioResponse(AnioCarrera a) {
        List<MateriaResponse> materias = materiaRepository
                .findByAnioCarreraIdOrderByNombre(a.getId())
                .stream()
                .map(this::toMateriaResponse)
                .toList();
        return new AnioCarreraResponse(a.getId(), a.getNumeroAnio(), materias);
    }

    private MateriaResponse toMateriaResponse(Materia m) {
        DocenteResumen docenteResumen = null;
        if (m.getDocente() != null) {
            Docente d = m.getDocente();
            docenteResumen = new DocenteResumen(d.getId(), d.getNombres(), d.getApellidos(), d.getEmail());
        }
        return new MateriaResponse(
                m.getId(), m.getNombre(), m.getDescripcion(),
                m.getDiaSemana(), m.getHoraInicio(), m.getHoraFin(),
                m.getAula(), docenteResumen
        );
    }
}
