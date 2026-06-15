package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.*;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final HorarioClaseRepository horarioClaseRepository;
    private final AlumnoRepository alumnoRepository;
    private final ArduinoRepository arduinoRepository;
    private final HuellaAlumnoRepository huellaAlumnoRepository;
    private final InscripcionMateriaRepository inscripcionMateriaRepository;
    private final ConfiguracionAsistenciaRepository configAsistenciaRepository;
    private final CalendarioAcademicoRepository calendarioRepository;
    private final UsuarioRepository usuarioRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository,
                             HorarioClaseRepository horarioClaseRepository,
                             AlumnoRepository alumnoRepository,
                             ArduinoRepository arduinoRepository,
                             HuellaAlumnoRepository huellaAlumnoRepository,
                             InscripcionMateriaRepository inscripcionMateriaRepository,
                             ConfiguracionAsistenciaRepository configAsistenciaRepository,
                             CalendarioAcademicoRepository calendarioRepository,
                             UsuarioRepository usuarioRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.horarioClaseRepository = horarioClaseRepository;
        this.alumnoRepository = alumnoRepository;
        this.arduinoRepository = arduinoRepository;
        this.huellaAlumnoRepository = huellaAlumnoRepository;
        this.inscripcionMateriaRepository = inscripcionMateriaRepository;
        this.configAsistenciaRepository = configAsistenciaRepository;
        this.calendarioRepository = calendarioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Llamado por el Arduino de aula al detectar huella o PIN
    @Transactional
    public AsistenciaDetalleFechaResponse registrarDesdeArduino(AsistenciaRegistroRequest req) {
        Arduino arduino = arduinoRepository.findByIdentificadorHardware(req.identificadorArduino())
                .orElseThrow(() -> new RuntimeException("Arduino no reconocido: " + req.identificadorArduino()));

        if (arduino.getAula() == null) {
            throw new RuntimeException("El Arduino no está asociado a ningún aula");
        }

        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        DiaSemana diaSemana = mapDiaSemana(hoy.getDayOfWeek());

        HorarioClase horario = horarioClaseRepository
                .findClaseActivaEnAula(arduino.getAula().getId(), diaSemana, ahora, hoy)
                .orElseThrow(() -> new RuntimeException("No hay clase activa en este aula ahora mismo"));

        // Resolver alumno: por ID (huella) o por PIN
        Alumno alumno;
        MetodoAsistencia metodo;
        if ("HUELLA".equalsIgnoreCase(req.metodo()) && req.alumnoId() != null) {
            alumno = alumnoRepository.findById(req.alumnoId())
                    .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + req.alumnoId()));
            metodo = MetodoAsistencia.HUELLA;
        } else if ("PIN".equalsIgnoreCase(req.metodo()) && req.pin() != null) {
            alumno = huellaAlumnoRepository.findAll().stream()
                    .filter(h -> req.pin().equals(h.getPinAlternativo()))
                    .map(HuellaAlumno::getAlumno)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("PIN incorrecto"));
            metodo = MetodoAsistencia.PIN;
        } else {
            throw new RuntimeException("Método de registro inválido o datos incompletos");
        }

        // Verificar que el alumno está inscripto en la materia
        Long materiaId = horario.getMateria().getId();
        boolean inscripto = inscripcionMateriaRepository
                .findByAlumnoId(alumno.getId()).stream()
                .anyMatch(i -> i.getMateria().getId().equals(materiaId)
                        && i.getEstado() == EstadoInscripcionMateria.CONFIRMADA);
        if (!inscripto) {
            throw new RuntimeException("El alumno no está inscripto en la materia de esta clase");
        }

        // Verificar si ya marcó presencia
        if (asistenciaRepository.findByAlumnoIdAndHorarioClaseIdAndFecha(
                alumno.getId(), horario.getId(), hoy).isPresent()) {
            throw new RuntimeException("El alumno ya registró asistencia para esta clase hoy");
        }

        // Determinar estado: PRESENTE o TARDANZA (según tolerancia configurada)
        int toleranciaMinutos = obtenerToleranciaMinutos(horario.getMateria());
        LocalTime limitePresente = horario.getHoraInicio().plusMinutes(toleranciaMinutos);
        EstadoAsistencia estado = ahora.isAfter(limitePresente)
                ? EstadoAsistencia.TARDANZA : EstadoAsistencia.PRESENTE;

        Asistencia asistencia = new Asistencia();
        asistencia.setAlumno(alumno);
        asistencia.setHorarioClase(horario);
        asistencia.setFecha(hoy);
        asistencia.setHoraRegistro(ahora);
        asistencia.setEstado(estado);
        asistencia.setMetodo(metodo);

        asistenciaRepository.save(asistencia);

        return toDetalle(asistencia, alumno);
    }

    // El docente o admin modifica una asistencia manualmente
    @Transactional
    public AsistenciaDetalleFechaResponse modificar(Long asistenciaId, AsistenciaModificarRequest req,
                                                     String usuarioUsername) {
        Asistencia asistencia = asistenciaRepository.findById(asistenciaId)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada: " + asistenciaId));

        asistencia.setEstado(req.estado());
        asistencia.setJustificacion(req.justificacion());
        asistencia.setMetodo(MetodoAsistencia.MANUAL);
        asistencia.setModificadoEn(LocalDateTime.now());

        usuarioRepository.findByUsername(usuarioUsername)
                .ifPresent(asistencia::setModificadoPor);

        return toDetalle(asistenciaRepository.save(asistencia), asistencia.getAlumno());
    }

    // Lista de asistencias de un horario en una fecha (para docente en su vista día)
    public List<AsistenciaDetalleFechaResponse> listarPorHorarioYFecha(Long horarioClaseId, LocalDate fecha) {
        return asistenciaRepository.findByHorarioClaseIdAndFecha(horarioClaseId, fecha).stream()
                .map(a -> toDetalle(a, a.getAlumno()))
                .toList();
    }

    // Resumen de asistencia de un alumno en una materia (para calcular regularidad)
    public AsistenciaResumenResponse resumenAlumnoMateria(Long alumnoId, Long materiaId,
                                                           LocalDate desde, LocalDate hasta) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + alumnoId));

        long presentes = asistenciaRepository.countByAlumnoIdAndHorarioClaseMateriaIdAndEstado(
                alumnoId, materiaId, EstadoAsistencia.PRESENTE);
        long tardanzas = asistenciaRepository.countByAlumnoIdAndHorarioClaseMateriaIdAndEstado(
                alumnoId, materiaId, EstadoAsistencia.TARDANZA);
        long ausentes = asistenciaRepository.countByAlumnoIdAndHorarioClaseMateriaIdAndEstado(
                alumnoId, materiaId, EstadoAsistencia.AUSENTE);

        long totalClases = presentes + tardanzas + ausentes;
        long asistidos = presentes + tardanzas;
        double porcentaje = totalClases > 0 ? (double) asistidos / totalClases * 100 : 100.0;

        double minimoRequerido = obtenerPorcentajeMinimo(materiaId);
        boolean libre = porcentaje < minimoRequerido;

        String materiaNombre = asistenciaRepository.findByMateriaAndRango(materiaId, desde, hasta)
                .stream().findFirst()
                .map(a -> a.getHorarioClase().getMateria().getNombre())
                .orElse("Materia ID " + materiaId);

        return new AsistenciaResumenResponse(
                alumnoId, alumno.getNombres() + " " + alumno.getApellidos(),
                materiaId, materiaNombre,
                totalClases, presentes, tardanzas, ausentes, porcentaje, libre
        );
    }

    // Vista calendario: asistencias de una materia en un rango de fechas agrupadas por fecha
    public Map<LocalDate, List<AsistenciaDetalleFechaResponse>> vistaCalendario(Long materiaId,
                                                                                  LocalDate desde,
                                                                                  LocalDate hasta) {
        return asistenciaRepository.findByMateriaAndRango(materiaId, desde, hasta).stream()
                .collect(Collectors.groupingBy(Asistencia::getFecha,
                        Collectors.mapping(a -> toDetalle(a, a.getAlumno()), Collectors.toList())));
    }

    private int obtenerToleranciaMinutos(Materia materia) {
        return configAsistenciaRepository
                .findByAplicaAAndMateriaId(NivelConfiguracionAsistencia.MATERIA, materia.getId())
                .map(ConfiguracionAsistencia::getToleranciaMinutos)
                .orElseGet(() -> configAsistenciaRepository
                        .findFirstByAplicaA(NivelConfiguracionAsistencia.GLOBAL)
                        .map(ConfiguracionAsistencia::getToleranciaMinutos)
                        .orElse(15));
    }

    private double obtenerPorcentajeMinimo(Long materiaId) {
        return configAsistenciaRepository
                .findByAplicaAAndMateriaId(NivelConfiguracionAsistencia.MATERIA, materiaId)
                .map(c -> (double) c.getPorcentajeMinimo())
                .orElseGet(() -> configAsistenciaRepository
                        .findFirstByAplicaA(NivelConfiguracionAsistencia.GLOBAL)
                        .map(c -> (double) c.getPorcentajeMinimo())
                        .orElse(75.0));
    }

    private DiaSemana mapDiaSemana(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> DiaSemana.LUNES;
            case TUESDAY   -> DiaSemana.MARTES;
            case WEDNESDAY -> DiaSemana.MIERCOLES;
            case THURSDAY  -> DiaSemana.JUEVES;
            case FRIDAY    -> DiaSemana.VIERNES;
            case SATURDAY  -> DiaSemana.SABADO;
            case SUNDAY    -> DiaSemana.DOMINGO;
        };
    }

    private AsistenciaDetalleFechaResponse toDetalle(Asistencia a, Alumno alumno) {
        return new AsistenciaDetalleFechaResponse(
                a.getId(), alumno.getId(),
                alumno.getNombres() + " " + alumno.getApellidos(),
                alumno.getDni(), a.getFecha(), a.getHoraRegistro(),
                a.getEstado(), a.getMetodo(), a.getJustificacion()
        );
    }
}
