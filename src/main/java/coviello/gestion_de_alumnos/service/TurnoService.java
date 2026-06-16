package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.*;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TurnoService {

    private final TurnoAsignadoRepository turnoRepository;
    private final ConfiguracionTurnoRepository configuracionTurnoRepository;
    private final PreinscripcionRepository preinscripcionRepository;
    private final EmailService emailService;

    public TurnoService(TurnoAsignadoRepository turnoRepository,
                        ConfiguracionTurnoRepository configuracionTurnoRepository,
                        PreinscripcionRepository preinscripcionRepository,
                        EmailService emailService) {
        this.turnoRepository = turnoRepository;
        this.configuracionTurnoRepository = configuracionTurnoRepository;
        this.preinscripcionRepository = preinscripcionRepository;
        this.emailService = emailService;
    }

    // ── Configuración de días ──────────────────────────────────────

    public ConfiguracionTurno crearConfiguracion(ConfiguracionTurnoRequest req) {
        ConfiguracionTurno config = new ConfiguracionTurno();
        config.setNombre(req.nombre());
        config.setFecha(req.fecha());
        config.setHorarioInicio(req.horarioInicio());
        config.setHorarioFin(req.horarioFin());
        config.setIntervaloMinutos(req.intervaloMinutos() > 0 ? req.intervaloMinutos() : 5);
        config.setCupoMaximo(req.cupoMaximo() > 0 ? req.cupoMaximo() : 100);
        config.setActivo(true);
        return configuracionTurnoRepository.save(config);
    }

    public List<ConfiguracionTurno> listarTodasConfiguraciones() {
        return configuracionTurnoRepository.findAll()
                .stream()
                .sorted(java.util.Comparator.comparing(ConfiguracionTurno::getFecha))
                .toList();
    }

    @Transactional
    public ConfiguracionTurno actualizarConfiguracion(Long id, ConfiguracionTurnoRequest req) {
        ConfiguracionTurno c = configuracionTurnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración de día no encontrada"));
        c.setNombre(req.nombre());
        c.setFecha(req.fecha());
        c.setHorarioInicio(req.horarioInicio());
        c.setHorarioFin(req.horarioFin());
        if (req.intervaloMinutos() > 0) c.setIntervaloMinutos(req.intervaloMinutos());
        if (req.cupoMaximo() > 0) c.setCupoMaximo(req.cupoMaximo());
        return configuracionTurnoRepository.save(c);
    }

    @Transactional
    public void eliminarConfiguracion(Long id) {
        configuracionTurnoRepository.deleteById(id);
    }

    public List<DiaInscripcionResponse> listarDiasDisponibles() {
        return configuracionTurnoRepository.findAllByActivoTrueOrderByFechaAsc()
                .stream()
                .map(c -> {
                    long ocupado = turnoRepository.countByConfiguracionTurnoId(c.getId());
                    long disponible = Math.max(0, c.getCupoMaximo() - ocupado);
                    return new DiaInscripcionResponse(
                            c.getId(), c.getNombre(), c.getFecha(),
                            c.getHorarioInicio(), c.getHorarioFin(),
                            c.getCupoMaximo(), ocupado, disponible);
                })
                .toList();
    }

    // ── Solicitud de turno por el aspirante (self-service, público) ────────

    @Transactional
    public TurnoResponse solicitarTurno(SolicitarTurnoRequest req) {
        ConfiguracionTurno config = configuracionTurnoRepository.findById(req.configuracionTurnoId())
                .orElseThrow(() -> new RuntimeException("Día de inscripción no encontrado"));

        if (!config.isActivo()) {
            throw new RuntimeException("El día de inscripción seleccionado no está activo");
        }

        long ocupado = turnoRepository.countByConfiguracionTurnoId(config.getId());
        if (ocupado >= config.getCupoMaximo()) {
            throw new RuntimeException("No hay más turnos disponibles para ese día. Intentá con otro día.");
        }

        Preinscripcion pre = buscarPreinscripcion(req);

        if (pre.getEstado() == EstadoPreinscripcion.HABILITADO) {
            throw new RuntimeException("Ya fuiste habilitado como alumno. No necesitás solicitar un turno.");
        }
        if (pre.getEstado() == EstadoPreinscripcion.RECHAZADO) {
            throw new RuntimeException("Tu preinscripción fue rechazada. Comunicate con la administración.");
        }

        if (turnoRepository.existsByPreinscripcionIdAndConfiguracionTurnoId(pre.getId(), config.getId())) {
            throw new RuntimeException("Ya tenés un turno asignado para ese día.");
        }

        return crearTurno(pre, config);
    }

    // ── Asignación por admin ────────────────────────────────────────

    @Transactional
    public TurnoResponse asignarTurno(Long preinscripcionId) {
        Preinscripcion pre = preinscripcionRepository.findById(preinscripcionId)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada: " + preinscripcionId));

        ConfiguracionTurno config = configuracionTurnoRepository.findFirstByActivoTrue()
                .orElseThrow(() -> new RuntimeException("No hay una configuración de turnos activa"));

        if (pre.getCarrera() == null) {
            throw new RuntimeException("La preinscripción no tiene carrera asignada");
        }

        if (turnoRepository.existsByPreinscripcionIdAndConfiguracionTurnoId(pre.getId(), config.getId())) {
            throw new RuntimeException("Esta preinscripción ya tiene un turno para el día activo");
        }

        long ocupado = turnoRepository.countByConfiguracionTurnoId(config.getId());
        if (ocupado >= config.getCupoMaximo()) {
            throw new RuntimeException("No hay más turnos disponibles para ese día");
        }

        return crearTurno(pre, config);
    }

    @Transactional
    public TurnoResponse confirmarTurno(String token) {
        TurnoAsignado turno = turnoRepository.findByTokenConfirmacion(token)
                .orElseThrow(() -> new RuntimeException("Token de confirmación inválido"));

        if (turno.isConfirmado()) {
            throw new RuntimeException("El turno ya fue confirmado");
        }

        turno.setConfirmado(true);
        turno.setFechaConfirmacion(java.time.LocalDateTime.now());
        return toResponse(turnoRepository.save(turno), turno.getConfiguracionTurno());
    }

    public TurnoResponse obtenerPorPreinscripcion(Long preinscripcionId) {
        TurnoAsignado turno = turnoRepository.findFirstByPreinscripcionId(preinscripcionId)
                .orElseThrow(() -> new RuntimeException("No hay turno asignado para esta preinscripción"));
        return toResponse(turno, turno.getConfiguracionTurno());
    }

    // ── Email masivo ────────────────────────────────────────────────

    public int enviarNotificacionMasiva(String mensajeExtra) {
        List<Preinscripcion> destinatarios = preinscripcionRepository
                .findByEstadoNotAndEstadoNot(EstadoPreinscripcion.HABILITADO, EstadoPreinscripcion.RECHAZADO);

        int enviados = 0;
        for (Preinscripcion pre : destinatarios) {
            if (pre.getEmail() == null || pre.getEmail().isBlank()) continue;
            try {
                emailService.enviarAperturaTurnos(pre.getEmail(),
                        pre.getNombre() + " " + pre.getApellido(), mensajeExtra);
                enviados++;
            } catch (MailException e) {
                log.error("No se pudo notificar a {}: {}", pre.getEmail(), e.getMessage());
            }
        }
        return enviados;
    }

    // ── Privados ────────────────────────────────────────────────────

    public List<PreinscripcionResumenTurno> buscarParaTurno(String tipoBusqueda, String valor, String nombre, String apellido) {
        List<Preinscripcion> resultados = switch (tipoBusqueda) {
            case "CODIGO" -> {
                if (valor == null || valor.isBlank()) throw new RuntimeException("Ingresá el número de formulario.");
                yield preinscripcionRepository.findByCodigoFormularioContaining(valor.trim());
            }
            case "DNI" -> {
                if (valor == null || valor.isBlank()) throw new RuntimeException("Ingresá el DNI.");
                yield preinscripcionRepository.findByDni(valor.trim()).map(List::of).orElse(List.of());
            }
            case "NOMBRE" -> {
                if (nombre == null || apellido == null || nombre.isBlank() || apellido.isBlank())
                    throw new RuntimeException("Ingresá nombre y apellido.");
                yield preinscripcionRepository.findAllByNombreIgnoreCaseAndApellidoIgnoreCase(nombre.trim(), apellido.trim());
            }
            default -> throw new RuntimeException("Tipo de búsqueda no válido.");
        };
        return resultados.stream()
                .map(p -> new PreinscripcionResumenTurno(
                        p.getId(), p.getCodigoFormulario(),
                        p.getNombre(), p.getApellido(),
                        p.getCarrera() != null ? p.getCarrera().getNombre() : null))
                .toList();
    }

    private Preinscripcion buscarPreinscripcion(SolicitarTurnoRequest req) {
        if (req.preinscripcionId() != null) {
            return preinscripcionRepository.findById(req.preinscripcionId())
                    .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada."));
        }
        return switch (req.tipoBusqueda()) {
            case "CODIGO" -> {
                List<Preinscripcion> r = preinscripcionRepository.findByCodigoFormularioContaining(req.valor().trim());
                if (r.isEmpty()) throw new RuntimeException("No se encontró preinscripción con ese código.");
                if (r.size() > 1) throw new RuntimeException("Hay múltiples coincidencias. Seleccioná una de la lista.");
                yield r.get(0);
            }
            case "DNI" -> preinscripcionRepository.findByDni(req.valor())
                    .orElseThrow(() -> new RuntimeException("No se encontró preinscripción con ese DNI."));
            case "NOMBRE" -> {
                List<Preinscripcion> r = preinscripcionRepository.findAllByNombreIgnoreCaseAndApellidoIgnoreCase(req.nombre(), req.apellido());
                if (r.isEmpty()) throw new RuntimeException("No se encontró preinscripción con ese nombre y apellido.");
                if (r.size() > 1) throw new RuntimeException("Hay múltiples coincidencias. Seleccioná una de la lista.");
                yield r.get(0);
            }
            default -> throw new RuntimeException("Tipo de búsqueda no válido. Usá CODIGO, DNI o NOMBRE.");
        };
    }

    private TurnoResponse crearTurno(Preinscripcion pre, ConfiguracionTurno config) {
        String prefijo = (pre.getCarrera() != null && pre.getCarrera().getNombre() != null)
                ? pre.getCarrera().getNombre().substring(0, 1).toUpperCase() : "T";
        long siguiente = pre.getCarrera() != null
                ? turnoRepository.countByConfiguracionTurnoIdAndCarreraId(config.getId(), pre.getCarrera().getId()) + 1
                : turnoRepository.countByConfiguracionTurnoId(config.getId()) + 1;
        String numeroTurno = prefijo + siguiente;

        LocalTime horaAsignada = config.getHorarioInicio()
                .plusMinutes((siguiente - 1) * config.getIntervaloMinutos());

        if (horaAsignada.isAfter(config.getHorarioFin())) {
            throw new RuntimeException("No hay más turnos disponibles en el horario configurado");
        }

        TurnoAsignado turno = new TurnoAsignado();
        turno.setPreinscripcion(pre);
        turno.setConfiguracionTurno(config);
        turno.setCarrera(pre.getCarrera());
        turno.setNumeroTurno(numeroTurno);
        turno.setHoraAsignada(horaAsignada);
        turno.setConfirmado(true);
        turno.setTokenConfirmacion(UUID.randomUUID().toString());

        TurnoAsignado guardado = turnoRepository.save(turno);

        if (pre.getEmail() != null && !pre.getEmail().isBlank()) {
            try {
                String nombre = pre.getNombre() + " " + pre.getApellido();
                String fecha = config.getFecha().toString();
                String hora = horaAsignada.toString();
                emailService.enviarTurnoAsignado(pre.getEmail(), nombre, numeroTurno, fecha, hora);
            } catch (MailException e) {
                log.error("No se pudo enviar email de turno a {}: {}", pre.getEmail(), e.getMessage());
            }
        }

        return toResponse(guardado, config);
    }

    private TurnoResponse toResponse(TurnoAsignado t, ConfiguracionTurno config) {
        return new TurnoResponse(
                t.getId(), t.getNumeroTurno(), t.getHoraAsignada(),
                config.getFecha(), t.isConfirmado(),
                t.getCarrera() != null ? t.getCarrera().getNombre() : null);
    }
}
