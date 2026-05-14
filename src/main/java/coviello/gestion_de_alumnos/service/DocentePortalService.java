package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.AlumnoPortalResponse;
import coviello.gestion_de_alumnos.dto.MateriaDetalleDocente;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocentePortalService {

    private final DocenteRepository docenteRepository;
    private final MateriaRepository materiaRepository;
    private final PreinscripcionRepository preinscripcionRepository;

    public DocentePortalService(DocenteRepository docenteRepository,
                                MateriaRepository materiaRepository,
                                PreinscripcionRepository preinscripcionRepository) {
        this.docenteRepository = docenteRepository;
        this.materiaRepository = materiaRepository;
        this.preinscripcionRepository = preinscripcionRepository;
    }

    @Transactional(readOnly = true)
    public List<MateriaDetalleDocente> getMisMaterias(String username) {
        Docente docente = findByUsername(username);
        return materiaRepository.findByDocente_Id(docente.getId()).stream()
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

    @Transactional(readOnly = true)
    public List<AlumnoPortalResponse> getMisAlumnos(String username) {
        Docente docente = findByUsername(username);

        List<Long> carreraIds = materiaRepository.findByDocente_Id(docente.getId()).stream()
                .filter(m -> m.getAnioCarrera() != null && m.getAnioCarrera().getCarrera() != null)
                .map(m -> m.getAnioCarrera().getCarrera().getId())
                .distinct()
                .toList();

        if (carreraIds.isEmpty()) return List.of();

        return preinscripcionRepository.findByCarreraIdInAndEstado(carreraIds, EstadoPreinscripcion.APROBADA)
                .stream()
                .map(p -> new AlumnoPortalResponse(
                        p.getId(), p.getNombre(), p.getApellido(),
                        p.getDni(), p.getEmail(), p.getTelefono(),
                        p.getCarrera() != null ? p.getCarrera().getNombre() : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AlumnoPortalResponse getAlumno(Long id) {
        return preinscripcionRepository.findById(id)
                .map(p -> new AlumnoPortalResponse(
                        p.getId(), p.getNombre(), p.getApellido(),
                        p.getDni(), p.getEmail(), p.getTelefono(),
                        p.getCarrera() != null ? p.getCarrera().getNombre() : null
                ))
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
    }

    private Docente findByUsername(String username) {
        return docenteRepository.findByUsuario_Username(username)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado para el usuario: " + username));
    }
}
