package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.service.AlumnoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @GetMapping("/alumnosPag")
    public ResponseEntity<ApiResponse> listarAlumnosPaginated (@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Alumno> alumnoPage = alumnoService.alumnoListPaged(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", alumnoPage.getContent());
        response.put("currentPage", alumnoPage.getNumber());
        response.put("totalItems", alumnoPage.getTotalElements());
        response.put("totalPages", alumnoPage.getTotalPages());
        response.put("pageSize", alumnoPage.getSize());

        String message = alumnoPage.isEmpty() ? "No se encontraron alumnos" : "Listado de alumnos paginado";

        return ResponseEntity.ok(new ApiResponse(message, response));
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