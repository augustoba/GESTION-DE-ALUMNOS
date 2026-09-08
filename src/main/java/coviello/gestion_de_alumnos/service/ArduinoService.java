package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.ArduinoAlumnoResponse;
import coviello.gestion_de_alumnos.dto.ArduinoHuellaRequest;
import coviello.gestion_de_alumnos.dto.ArduinoInfoResponse;
import coviello.gestion_de_alumnos.model.Arduino;
import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.HuellaAlumno;
import coviello.gestion_de_alumnos.model.TipoArduino;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.ArduinoRepository;
import coviello.gestion_de_alumnos.repository.HuellaAlumnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ArduinoService {

    private final ArduinoRepository arduinoRepository;
    private final AlumnoRepository alumnoRepository;
    private final HuellaAlumnoRepository huellaAlumnoRepository;

    public ArduinoService(ArduinoRepository arduinoRepository,
                          AlumnoRepository alumnoRepository,
                          HuellaAlumnoRepository huellaAlumnoRepository) {
        this.arduinoRepository = arduinoRepository;
        this.alumnoRepository = alumnoRepository;
        this.huellaAlumnoRepository = huellaAlumnoRepository;
    }

    public ArduinoInfoResponse ping(String identificadorHardware) {
        Arduino arduino = getArduinoActivo(identificadorHardware);
        return toInfo(arduino);
    }

    // Solo para Arduino tipo REGISTRO: busca alumno por DNI antes de enrolar la huella
    public ArduinoAlumnoResponse buscarAlumnoPorDni(String identificadorHardware, String dni) {
        Arduino arduino = getArduinoActivo(identificadorHardware);
        if (arduino.getTipo() != TipoArduino.REGISTRO) {
            throw new RuntimeException("Este Arduino no tiene permisos para consultar alumnos");
        }
        Alumno alumno = alumnoRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con DNI: " + dni));
        return toAlumnoResponse(alumno);
    }

    // El Arduino REGISTRO llama a este endpoint después de enrolar exitosamente la huella
    // Guarda el mapeo alumnoId <-> sensorId para que luego el Arduino AULA pueda identificar al alumno
    @Transactional
    public void registrarHuella(String identificadorHardware, ArduinoHuellaRequest req) {
        Arduino arduino = getArduinoActivo(identificadorHardware);
        if (arduino.getTipo() != TipoArduino.REGISTRO) {
            throw new RuntimeException("Este Arduino no tiene permisos para registrar huellas");
        }

        Alumno alumno = alumnoRepository.findById(req.alumnoId())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + req.alumnoId()));

        // Si ya existe un sensorId distinto asignado a otro alumno, rechazar
        huellaAlumnoRepository.findBySensorId(req.sensorId()).ifPresent(existente -> {
            if (!existente.getAlumno().getId().equals(alumno.getId())) {
                throw new RuntimeException("El sensorId " + req.sensorId() + " ya está asignado a otro alumno");
            }
        });

        HuellaAlumno huella = huellaAlumnoRepository.findByAlumnoId(alumno.getId())
                .orElse(new HuellaAlumno());
        huella.setAlumno(alumno);
        huella.setSensorId(req.sensorId());
        huella.setPinAlternativo(req.pinAlternativo());
        huella.setFechaRegistro(LocalDateTime.now());
        // templateData no se usa: el template vive en la memoria del sensor Arduino
        huella.setTemplateData(new byte[0]);
        huellaAlumnoRepository.save(huella);
    }

    // Elimina el mapeo de huella de un alumno (cuando se da de baja o se re-enrola)
    @Transactional
    public void eliminarHuella(String identificadorHardware, Long alumnoId) {
        Arduino arduino = getArduinoActivo(identificadorHardware);
        if (arduino.getTipo() != TipoArduino.REGISTRO) {
            throw new RuntimeException("Este Arduino no tiene permisos para eliminar huellas");
        }
        HuellaAlumno huella = huellaAlumnoRepository.findByAlumnoId(alumnoId)
                .orElseThrow(() -> new RuntimeException("El alumno no tiene huella registrada"));
        huellaAlumnoRepository.delete(huella);
    }

    private Arduino getArduinoActivo(String identificadorHardware) {
        return arduinoRepository.findByIdentificadorHardware(identificadorHardware)
                .filter(Arduino::isActivo)
                .orElseThrow(() -> new RuntimeException(
                        "Arduino no reconocido o inactivo: " + identificadorHardware));
    }

    private ArduinoInfoResponse toInfo(Arduino a) {
        return new ArduinoInfoResponse(
                a.getId(),
                a.getIdentificadorHardware(),
                a.getTipo().name(),
                a.getAula() != null ? a.getAula().getId() : null,
                a.getAula() != null ? a.getAula().getNombre() : null,
                a.isActivo()
        );
    }

    private ArduinoAlumnoResponse toAlumnoResponse(Alumno alumno) {
        return huellaAlumnoRepository.findByAlumnoId(alumno.getId())
                .map(h -> new ArduinoAlumnoResponse(
                        alumno.getId(), alumno.getNombres(), alumno.getApellidos(),
                        alumno.getDni(), true, h.getSensorId()))
                .orElse(new ArduinoAlumnoResponse(
                        alumno.getId(), alumno.getNombres(), alumno.getApellidos(),
                        alumno.getDni(), false, null));
    }
}
