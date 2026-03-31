package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alumno")
public class AlumnoController {

 private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping("/alumnos")
    ResponseEntity<Object> listarClientes(){

        List<Alumno> clienteList = alumnoService.alumnoList();
        if (clienteList.isEmpty()){
            System.out.println("asdasdas");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(HttpStatus.BAD_REQUEST);
        }

        return  ResponseEntity.status(HttpStatus.OK).body(clienteList);
    }

}