package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ApiResponse> listarAlumnos() {

        List<Alumno> alumnoList = alumnoService.alumnoList();

        if (alumnoList.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse("No se encontraron alumnos", alumnoList)
            );
        }

        return ResponseEntity.ok(
                new ApiResponse("Listado de alumnos", alumnoList)
        );
    }

    @GetMapping("/alumnos-nombre")
    public ResponseEntity<ApiResponse> findByName( @RequestParam(required = false) String nombre,
                                                   @RequestParam(required = false) String apellidos) {

        List<Alumno> alumnoList = alumnoService.alumnoListName(nombre,apellidos);

        if (alumnoList.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponse("No se encontraron alumnos", alumnoList)
            );
        }

        return ResponseEntity.ok(
                new ApiResponse("Listado de alumnos", alumnoList)
        );
    }

}