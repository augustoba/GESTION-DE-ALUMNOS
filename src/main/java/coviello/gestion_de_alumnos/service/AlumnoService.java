package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
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

    public List<Alumno> alumnoListName(String nombre, String apellidos) {
        return alumnoRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, apellidos);
    }

}
