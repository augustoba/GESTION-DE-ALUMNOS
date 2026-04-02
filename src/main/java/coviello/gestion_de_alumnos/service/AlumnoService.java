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

    /*public Page<Alumno> alumnoListNamePaged(String nombre, String apellidos, DataWebProperties.Pageable pageable) {
        return alumnoRepository.findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(nombre, apellidos, pageable);
    }*/

}
