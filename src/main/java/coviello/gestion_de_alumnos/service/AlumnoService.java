package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
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

    public List<Alumno> alumnoList(){
    return alumnoRepository.findAll();}

    public Page<Alumno> alumnoListPaged(Pageable pageable) {
        return alumnoRepository.findAll(pageable);
    }

    public List<Alumno> alumnoListName(String nombre, String apellidos) {
        return alumnoRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, apellidos);
    }

    public Page<Alumno> alumnoListNamePaged(String nombre, String apellidos, Pageable pageable) {
        return alumnoRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, apellidos, pageable);
    }

    public Alumno updateAlumno(Long id, Alumno alumnoDetails) {
        // Buscar el alumno existente
        Alumno alumnoExistente = alumnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con id: " + id));

        // Actualizar solo los campos que no son nulos
        if (alumnoDetails.getNombres() != null) {
            alumnoExistente.setNombres(alumnoDetails.getNombres());
        }
        if (alumnoDetails.getApellidos() != null) {
            alumnoExistente.setApellidos(alumnoDetails.getApellidos());
        }
        if (alumnoDetails.getDni() != null) {
            // Verificar que el nuevo DNI no exista en otro alumno
            if (!alumnoExistente.getDni().equals(alumnoDetails.getDni()) &&
                    alumnoRepository.findByDni(alumnoDetails.getDni()).isPresent()) {
                throw new RuntimeException("Ya existe un alumno con el DNI: " + alumnoDetails.getDni());
            }
            alumnoExistente.setDni(alumnoDetails.getDni());
        }
        if (alumnoDetails.getCuil() != null) {
            // Verificar que el nuevo CUIL no exista en otro alumno
            if (!alumnoExistente.getCuil().equals(alumnoDetails.getCuil()) &&
                    alumnoRepository.findByCuil(alumnoDetails.getCuil()).isPresent()) {
                throw new RuntimeException("Ya existe un alumno con el CUIL: " + alumnoDetails.getCuil());
            }
            alumnoExistente.setCuil(alumnoDetails.getCuil());
        }
        if (alumnoDetails.getEmail() != null) {
            // Verificar que el nuevo email no exista en otro alumno
            if (!alumnoExistente.getEmail().equals(alumnoDetails.getEmail()) &&
                    alumnoRepository.findByEmail(alumnoDetails.getEmail()).isPresent()) {
                throw new RuntimeException("Ya existe un alumno con el email: " + alumnoDetails.getEmail());
            }
            alumnoExistente.setEmail(alumnoDetails.getEmail());
        }
        if (alumnoDetails.getDireccion() != null) {
            alumnoExistente.setDireccion(alumnoDetails.getDireccion());
        }
        if (alumnoDetails.getFechaNac() != null) {
            alumnoExistente.setFechaNac(alumnoDetails.getFechaNac());
        }
        if (alumnoDetails.getStatus() != null) {
            alumnoExistente.setStatus(alumnoDetails.getStatus());
        }

        // Guardar el alumno actualizado
        return alumnoRepository.save(alumnoExistente);
    }

}
