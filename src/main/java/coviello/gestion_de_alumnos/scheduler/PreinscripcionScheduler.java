package coviello.gestion_de_alumnos.scheduler;

import coviello.gestion_de_alumnos.service.PreinscripcionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PreinscripcionScheduler {

    private final PreinscripcionService preinscripcionService;

    public PreinscripcionScheduler(PreinscripcionService preinscripcionService) {
        this.preinscripcionService = preinscripcionService;
    }

    // Corre cada hora y expira las inscripciones sin pago validado en más de 48hs
    @Scheduled(fixedRate = 3_600_000)
    public void expirarInscripcionesVencidas() {
        preinscripcionService.expirarPendientes();
    }
}
