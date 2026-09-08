package coviello.gestion_de_alumnos.controller;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import coviello.gestion_de_alumnos.dto.ArduinoHuellaRequest;
import coviello.gestion_de_alumnos.service.ArduinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/arduino")
@Tag(name = "Arduino", description = "Endpoints para dispositivos ESP32/Arduino. Sin JWT — autenticados por X-Arduino-Id.")
public class ArduinoController {

    private final ArduinoService arduinoService;

    public ArduinoController(ArduinoService arduinoService) {
        this.arduinoService = arduinoService;
    }

    @GetMapping("/ping")
    @Operation(
        summary = "Verificar que el Arduino está registrado",
        description = "El ESP32 llama a este endpoint al arrancar para confirmar que su identificador está registrado y activo en el sistema."
    )
    public ResponseEntity<ApiResponse> ping(
            @RequestHeader("X-Arduino-Id") String identificadorHardware) {
        return ResponseEntity.ok(new ApiResponse("Arduino activo",
                arduinoService.ping(identificadorHardware)));
    }

    @GetMapping("/alumno/dni/{dni}")
    @Operation(
        summary = "Buscar alumno por DNI (solo Arduino tipo REGISTRO)",
        description = "El Arduino de inscripción consulta si el DNI ingresado corresponde a un alumno, y si ya tiene huella registrada."
    )
    public ResponseEntity<ApiResponse> buscarAlumnoPorDni(
            @RequestHeader("X-Arduino-Id") String identificadorHardware,
            @PathVariable String dni) {
        return ResponseEntity.ok(new ApiResponse("Alumno encontrado",
                arduinoService.buscarAlumnoPorDni(identificadorHardware, dni)));
    }

    @PostMapping("/huella")
    @Operation(
        summary = "Registrar huella dactilar (solo Arduino tipo REGISTRO)",
        description = "Después de enrolar exitosamente la huella, el Arduino envía el sensorId asignado por el sensor "
                    + "junto con el alumnoId. El backend guarda el mapeo alumno <-> sensorId."
    )
    public ResponseEntity<ApiResponse> registrarHuella(
            @RequestHeader("X-Arduino-Id") String identificadorHardware,
            @RequestBody @Valid ArduinoHuellaRequest req) {
        arduinoService.registrarHuella(identificadorHardware, req);
        return ResponseEntity.ok(new ApiResponse("Huella registrada correctamente", null));
    }

    @DeleteMapping("/huella/{alumnoId}")
    @Operation(
        summary = "Eliminar huella de un alumno (solo Arduino tipo REGISTRO)",
        description = "Borra el mapeo sensorId del alumno. Usar antes de re-enrolar o al dar de baja al alumno."
    )
    public ResponseEntity<ApiResponse> eliminarHuella(
            @RequestHeader("X-Arduino-Id") String identificadorHardware,
            @PathVariable Long alumnoId) {
        arduinoService.eliminarHuella(identificadorHardware, alumnoId);
        return ResponseEntity.ok(new ApiResponse("Huella eliminada", null));
    }
}
