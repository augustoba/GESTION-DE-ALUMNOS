package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;

    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    public Page<Alumno> listarPaginados(Pageable pageable) {
        return alumnoRepository.findAll(pageable);
    }

    public Page<Alumno> buscarPorNombre(String nombre, String apellido, Pageable pageable) {
        return alumnoRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, apellido, pageable);
    }

    public List<Alumno> listarHabilitados() {
        return alumnoRepository.findByHabilitadoTrue();
    }

    public List<Alumno> listarPorCarrera(Long carreraId) {
        return alumnoRepository.findByCarreraId(carreraId);
    }

    public Alumno obtenerPorId(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con ID: " + id));
    }

    public Alumno habilitar(Long id) {
        Alumno alumno = obtenerPorId(id);
        alumno.setHabilitado(true);
        return alumnoRepository.save(alumno);
    }

    public Alumno deshabilitar(Long id) {
        Alumno alumno = obtenerPorId(id);
        alumno.setHabilitado(false);
        return alumnoRepository.save(alumno);
    }

    public Alumno actualizar(Long id, Alumno datos) {
        Alumno alumno = obtenerPorId(id);

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

        return alumnoRepository.save(alumno);
    }
}
