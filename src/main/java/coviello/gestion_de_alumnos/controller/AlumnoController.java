package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alumno")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Alumnos (Admin)", description = "Consulta y gestión de alumnos registrados. Requiere rol ADMIN.")
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping("/alumnos")
    @Operation(
        summary = "Listar todos los alumnos",
        description = "Devuelve la lista completa de alumnos registrados en el sistema."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> listarAlumnos() {
        List<Alumno> alumnoList = alumnoService.alumnoList();
        String mensaje = alumnoList.isEmpty() ? "No se encontraron alumnos" : "Listado de alumnos";
        return ResponseEntity.ok(new ApiResponse(mensaje, alumnoList));
    }

    @GetMapping("/alumnosPag")
    @Operation(
        summary = "Listar alumnos con paginación",
        description = "Devuelve una página de alumnos. Incluye información de paginación (página actual, total de páginas, etc.)."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Página devuelta correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> listarAlumnosPaginado(
            @Parameter(description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de registros por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Alumno> alumnoPage = alumnoService.alumnoListPaged(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", alumnoPage.getContent());
        response.put("currentPage", alumnoPage.getNumber());
        response.put("totalItems", alumnoPage.getTotalElements());
        response.put("totalPages", alumnoPage.getTotalPages());
        response.put("pageSize", alumnoPage.getSize());

        String mensaje = alumnoPage.isEmpty() ? "No se encontraron alumnos" : "Listado de alumnos paginado";
        return ResponseEntity.ok(new ApiResponse(mensaje, response));
    }

    @GetMapping("/alumnos-nombre")
    @Operation(
        summary = "Buscar alumnos por nombre o apellido",
        description = "Búsqueda sin distinción de mayúsculas. Podés buscar solo por nombre, solo por apellido, o por ambos."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resultados devueltos correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> buscarPorNombre(
            @Parameter(description = "Nombre a buscar (parcial o completo)", example = "Juan")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Apellido a buscar (parcial o completo)", example = "Pérez")
            @RequestParam(required = false) String apellidos) {

        List<Alumno> alumnoList = alumnoService.alumnoListName(nombre, apellidos);
        String mensaje = alumnoList.isEmpty() ? "No se encontraron alumnos" : "Listado de alumnos";
        return ResponseEntity.ok(new ApiResponse(mensaje, alumnoList));
    }

    @GetMapping("/alumnosnombrepag")
    @Operation(
        summary = "Buscar alumnos por nombre con paginación",
        description = "Igual que la búsqueda por nombre, pero con paginación. Devuelve también si hay página siguiente/anterior."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Resultados paginados devueltos correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    public ResponseEntity<ApiResponse> buscarPorNombrePaginado(
            @Parameter(description = "Nombre a buscar (parcial o completo)", example = "Juan")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Apellido a buscar (parcial o completo)", example = "Pérez")
            @RequestParam(required = false) String apellidos,
            @Parameter(description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de registros por página", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Alumno> alumnoPage = alumnoService.alumnoListNamePaged(nombre, apellidos, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", alumnoPage.getContent());
        response.put("currentPage", alumnoPage.getNumber());
        response.put("totalItems", alumnoPage.getTotalElements());
        response.put("totalPages", alumnoPage.getTotalPages());
        response.put("pageSize", alumnoPage.getSize());
        response.put("hasNext", alumnoPage.hasNext());
        response.put("hasPrevious", alumnoPage.hasPrevious());
        response.put("searchParams", Map.of(
                "nombre", nombre != null ? nombre : "",
                "apellidos", apellidos != null ? apellidos : ""
        ));

        String mensaje = alumnoPage.isEmpty() ? "No se encontraron alumnos" : "Resultados de búsqueda paginados";
        return ResponseEntity.ok(new ApiResponse(mensaje, response));
    }

    @PutMapping("/alumnos/{id}")
    @Operation(
        summary = "Actualizar datos de un alumno",
        description = "Actualiza solo los campos que se envíen en el body (los campos nulos se ignoran). Valida que DNI, CUIL y email no estén duplicados."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Alumno actualizado correctamente."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "DNI, CUIL o email ya pertenecen a otro alumno."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "No tenés permisos (se requiere rol ADMIN).")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = """
                {
                  "nombres": "Juan Carlos",
                  "apellidos": "Pérez",
                  "email": "nuevo.email@gmail.com",
                  "direccion": "Av. Siempreviva 742"
                }
                """)
        )
    )
    public ResponseEntity<ApiResponse> actualizarAlumno(
            @Parameter(description = "ID del alumno a actualizar", example = "1")
            @PathVariable Long id,
            @RequestBody Alumno alumnoDetails) {

        Alumno alumnoActualizado = alumnoService.updateAlumno(id, alumnoDetails);
        return ResponseEntity.ok(new ApiResponse("Alumno actualizado correctamente", alumnoActualizado));
    }
}
