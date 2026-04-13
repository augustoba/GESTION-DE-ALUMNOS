package coviello.gestion_de_alumnos.controller;
import coviello.gestion_de_alumnos.model.Carrera;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.service.CarreraService;
import coviello.gestion_de_alumnos.service.PreinscripcionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/preinscripciones")
public class PreinscripcionController {
    private final PreinscripcionService preinscripcionService;
    private final CarreraService carreraService;

    public PreinscripcionController(PreinscripcionService preinscripcionService, CarreraService carreraService) {
        this.preinscripcionService = preinscripcionService;
        this.carreraService = carreraService;
    }


    @PostMapping
    public ResponseEntity<?> crearPreinscripcion(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String dni,
            @RequestParam String email,
            @RequestParam String telefono,
            @RequestParam Long carreraId,
            @RequestParam("comprobante") MultipartFile comprobante) {

        try {

            Carrera carrera = carreraService.obtenerPorId(carreraId);

            Preinscripcion preinscripcion = new Preinscripcion();
            preinscripcion.setNombre(nombre);
            preinscripcion.setApellido(apellido);
            preinscripcion.setDni(dni);
            preinscripcion.setEmail(email);
            preinscripcion.setTelefono(telefono);
            preinscripcion.setCarrera(carrera);

            Preinscripcion guardada = preinscripcionService.guardar(preinscripcion, comprobante);

            return ResponseEntity.status(HttpStatus.CREATED).body(guardada);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el comprobante: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    @PutMapping("/{id}/validar-pago")
    public ResponseEntity<?> validarPago(@PathVariable Long id) {
        try {
            Preinscripcion pre = preinscripcionService.validarPago(id);
            return ResponseEntity.ok(pre);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Preinscripcion>> obtenerTodas() {
        List<Preinscripcion> lista = preinscripcionService.obtenerTodas();
        return ResponseEntity.ok(lista);
    }


}