package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.dto.DocumentoChecklistResponse;
import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentoChecklistService {

    private final DocumentoChecklistRepository checklistRepository;
    private final PreinscripcionRepository preinscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    public DocumentoChecklistService(DocumentoChecklistRepository checklistRepository,
                                     PreinscripcionRepository preinscripcionRepository,
                                     UsuarioRepository usuarioRepository) {
        this.checklistRepository = checklistRepository;
        this.preinscripcionRepository = preinscripcionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<DocumentoChecklistResponse> listarPorPreinscripcion(Long preinscripcionId) {
        return checklistRepository.findByPreinscripcionId(preinscripcionId).stream()
                .map(c -> new DocumentoChecklistResponse(c.getId(), c.getTipoDocumento(),
                        c.isPresentado(), c.getFechaPresentacion()))
                .toList();
    }

    @Transactional
    public DocumentoChecklistResponse marcarPresentado(Long preinscripcionId, TipoDocumento tipo,
                                                       String adminUsername) {
        Preinscripcion pre = preinscripcionRepository.findById(preinscripcionId)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada: " + preinscripcionId));

        DocumentoChecklist item = checklistRepository
                .findByPreinscripcionIdAndTipoDocumento(preinscripcionId, tipo)
                .orElseGet(() -> {
                    DocumentoChecklist nuevo = new DocumentoChecklist();
                    nuevo.setPreinscripcion(pre);
                    nuevo.setTipoDocumento(tipo);
                    return nuevo;
                });

        item.setPresentado(true);
        item.setFechaPresentacion(LocalDateTime.now());

        usuarioRepository.findByUsername(adminUsername)
                .ifPresent(item::setRegistradoPor);

        return toResponse(checklistRepository.save(item));
    }

    @Transactional
    public DocumentoChecklistResponse desmarcarPresentado(Long preinscripcionId, TipoDocumento tipo) {
        DocumentoChecklist item = checklistRepository
                .findByPreinscripcionIdAndTipoDocumento(preinscripcionId, tipo)
                .orElseThrow(() -> new RuntimeException("Item no encontrado para tipo: " + tipo));

        item.setPresentado(false);
        item.setFechaPresentacion(null);
        return toResponse(checklistRepository.save(item));
    }

    public long contarPresentados(Long preinscripcionId) {
        return checklistRepository.countByPreinscripcionIdAndPresentadoTrue(preinscripcionId);
    }

    public long contarFaltantes(Long preinscripcionId) {
        return checklistRepository.countByPreinscripcionIdAndPresentadoFalse(preinscripcionId);
    }

    private DocumentoChecklistResponse toResponse(DocumentoChecklist c) {
        return new DocumentoChecklistResponse(c.getId(), c.getTipoDocumento(),
                c.isPresentado(), c.getFechaPresentacion());
    }
}
