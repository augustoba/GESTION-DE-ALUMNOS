package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.EnvioMailRequest;
import coviello.gestion_de_alumnos.dto.EnvioMailResponse;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnvioMailService {

    private final EnvioMailRepository envioMailRepository;
    private final AlumnoRepository alumnoRepository;
    private final AnioCarreraRepository anioCarreraRepository;
    private final DocumentoDigitalRepository documentoDigitalRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public EnvioMailService(EnvioMailRepository envioMailRepository,
                            AlumnoRepository alumnoRepository,
                            AnioCarreraRepository anioCarreraRepository,
                            DocumentoDigitalRepository documentoDigitalRepository,
                            UsuarioRepository usuarioRepository,
                            EmailService emailService) {
        this.envioMailRepository = envioMailRepository;
        this.alumnoRepository = alumnoRepository;
        this.anioCarreraRepository = anioCarreraRepository;
        this.documentoDigitalRepository = documentoDigitalRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    public Page<EnvioMailResponse> listarHistorial(int page, int size) {
        return envioMailRepository.findAllByOrderByEnviadoEnDesc(
                PageRequest.of(page, size, Sort.by("enviadoEn").descending()))
                .map(this::toResponse);
    }

    @Transactional
    public EnvioMailResponse enviar(EnvioMailRequest req, String remitenteUsername) {
        Usuario remitente = usuarioRepository.findByUsername(remitenteUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + remitenteUsername));

        List<Alumno> destinatarios = resolverDestinatarios(req);
        int totalEnviados = 0;

        for (Alumno alumno : destinatarios) {
            if (alumno.getEmail() == null || alumno.getEmail().isBlank()) continue;
            try {
                emailService.enviarMasivo(alumno.getEmail(), req.asunto(), req.cuerpo());
                totalEnviados++;
            } catch (Exception e) {
                System.err.println("No se pudo enviar mail a " + alumno.getEmail() + ": " + e.getMessage());
            }
        }

        EnvioMail registro = new EnvioMail();
        registro.setAsunto(req.asunto());
        registro.setCuerpo(req.cuerpo());
        registro.setDestinatarioTipo(req.destinatarioTipo());
        registro.setTotalEnviados(totalEnviados);
        registro.setEnviadoEn(LocalDateTime.now());
        registro.setEnviadoPor(remitente);

        if (req.carreraId() != null && req.destinatarioTipo() != TipoDestinatarioMail.TODOS) {
            // anioCarrera is optional — only set if provided for POR_ANIO
            if (req.anioCarreraId() != null) {
                anioCarreraRepository.findById(req.anioCarreraId())
                        .ifPresent(registro::setAnioCarrera);
            }
        }

        return toResponse(envioMailRepository.save(registro));
    }

    private List<Alumno> resolverDestinatarios(EnvioMailRequest req) {
        return switch (req.destinatarioTipo()) {
            case TODOS -> alumnoRepository.findByHabilitadoTrue();
            case POR_CARRERA -> {
                if (req.carreraId() == null) throw new RuntimeException("carreraId requerido para POR_CARRERA");
                yield alumnoRepository.findByComision_AnioCarrera_CarreraId(req.carreraId()).stream()
                        .filter(Alumno::isHabilitado).toList();
            }
            case POR_ANIO -> {
                if (req.anioCarreraId() == null) throw new RuntimeException("anioCarreraId requerido para POR_ANIO");
                yield alumnoRepository.findByComision_AnioCarreraId(req.anioCarreraId()).stream()
                        .filter(Alumno::isHabilitado).toList();
            }
            case DOCS_FALTANTES -> {
                List<Long> conDocsPendientes = documentoDigitalRepository.findAlumnoIdsConDocumentosPendientes();
                if (conDocsPendientes.isEmpty()) yield List.of();
                yield alumnoRepository.findAllById(conDocsPendientes).stream()
                        .filter(Alumno::isHabilitado).toList();
            }
        };
    }

    private EnvioMailResponse toResponse(EnvioMail e) {
        return new EnvioMailResponse(
                e.getId(), e.getAsunto(), e.getDestinatarioTipo(),
                e.getAnioCarrera() != null && e.getAnioCarrera().getCarrera() != null
                        ? e.getAnioCarrera().getCarrera().getNombre() : null,
                e.getTotalEnviados(), e.getEnviadoEn(),
                e.getEnviadoPor().getUsername()
        );
    }
}
