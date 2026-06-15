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
    private final AlumnoRepository alumnoRepository;

    public DocentePortalService(DocenteRepository docenteRepository,
                                MateriaRepository materiaRepository,
                                AlumnoRepository alumnoRepository) {
        this.docenteRepository = docenteRepository;
        this.materiaRepository = materiaRepository;
        this.alumnoRepository = alumnoRepository;
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
                    return new MateriaDetalleDocente(m.getId(), m.getNombre(), m.getDescripcion(), carreraNombre, numeroAnio);
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

        return alumnoRepository.findAll().stream()
                .filter(a -> a.isHabilitado() && a.getCarrera() != null && carreraIds.contains(a.getCarrera().getId()))
                .map(a -> new AlumnoPortalResponse(
                        a.getId(), a.getNombres(), a.getApellidos(),
                        a.getDni(), a.getEmail(), a.getTelefono(),
                        a.getCarrera() != null ? a.getCarrera().getNombre() : null
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public AlumnoPortalResponse getAlumno(Long id) {
        return alumnoRepository.findById(id)
                .map(a -> new AlumnoPortalResponse(
                        a.getId(), a.getNombres(), a.getApellidos(),
                        a.getDni(), a.getEmail(), a.getTelefono(),
                        a.getCarrera() != null ? a.getCarrera().getNombre() : null
                ))
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
    }

    private Docente findByUsername(String username) {
        return docenteRepository.findByUsuario_Username(username)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado para el usuario: " + username));
    }
}
