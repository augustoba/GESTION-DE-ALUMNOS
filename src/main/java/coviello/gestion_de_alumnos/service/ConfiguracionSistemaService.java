package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.ConfiguracionSistema;
import coviello.gestion_de_alumnos.model.Preinscripcion;
import coviello.gestion_de_alumnos.repository.ConfiguracionSistemaRepository;
import coviello.gestion_de_alumnos.repository.PreinscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConfiguracionSistemaService {

    private final ConfiguracionSistemaRepository repo;
    private final PreinscripcionRepository preinscripcionRepo;
    private final EmailService emailService;

    public ConfiguracionSistemaService(ConfiguracionSistemaRepository repo,
                                       PreinscripcionRepository preinscripcionRepo,
                                       EmailService emailService) {
        this.repo = repo;
        this.preinscripcionRepo = preinscripcionRepo;
        this.emailService = emailService;
    }

    public ConfiguracionSistema obtener() {
        return repo.findById(1L).orElseGet(() -> repo.save(new ConfiguracionSistema()));
    }

    // ── Preinscripción ─────────────────────────────────────────────

    public boolean isPreinscripcionHabilitada() {
        return obtener().isPreinscripcionHabilitada();
    }

    @Transactional
    public ConfiguracionSistema setPreinscripcionHabilitada(boolean habilitada) {
        ConfiguracionSistema cfg = obtener();
        cfg.setPreinscripcionHabilitada(habilitada);
        return repo.save(cfg);
    }

    // ── Turnos ─────────────────────────────────────────────────────

    public boolean isTurnosHabilitados() {
        return obtener().isTurnosHabilitados();
    }

    @Transactional
    public ConfiguracionSistema setTurnosHabilitados(boolean habilitado) {
        ConfiguracionSistema cfg = obtener();
        boolean eraHabilitado = cfg.isTurnosHabilitados();
        cfg.setTurnosHabilitados(habilitado);
        cfg = repo.save(cfg);

        if (habilitado && !eraHabilitado) {
            int anio = LocalDate.now().getYear();
            List<Preinscripcion> aspirantes = preinscripcionRepo.findByAnio(anio);
            for (Preinscripcion p : aspirantes) {
                try {
                    emailService.enviarAperturaTurnos(p.getEmail(), p.getNombre(), "");
                } catch (Exception ignored) {}
            }
        }

        return cfg;
    }
}
