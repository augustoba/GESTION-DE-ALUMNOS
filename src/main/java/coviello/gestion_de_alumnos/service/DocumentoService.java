package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.*;
import coviello.gestion_de_alumnos.repository.DocumentoRepository;
import coviello.gestion_de_alumnos.repository.PreinscripcionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class DocumentoService {

    private static final List<TipoDocumento> DOCS_OBLIGATORIOS =
            List.of(TipoDocumento.DNI_FRENTE, TipoDocumento.DNI_DORSO, TipoDocumento.TITULO, TipoDocumento.FOTO_CARNET);

    private final DocumentoRepository documentoRepository;
    private final PreinscripcionRepository preinscripcionRepository;

    public DocumentoService(DocumentoRepository documentoRepository,
                            PreinscripcionRepository preinscripcionRepository) {
        this.documentoRepository = documentoRepository;
        this.preinscripcionRepository = preinscripcionRepository;
    }

    public Documento subirDocumento(Long preinscripcionId, TipoDocumento tipo, MultipartFile archivo)
            throws IOException {

        Preinscripcion preinscripcion = preinscripcionRepository.findById(preinscripcionId)
                .orElseThrow(() -> new RuntimeException("Preinscripción no encontrada con ID: " + preinscripcionId));

        if (preinscripcion.getEstado() == EstadoPreinscripcion.EXPIRADA) {
            throw new RuntimeException("No se pueden subir documentos: la inscripción está expirada");
        }

        // Si ya existe un documento del mismo tipo, reemplazarlo y resetear su estado
        Documento documento = documentoRepository
                .findByPreinscripcionIdAndTipo(preinscripcionId, tipo)
                .orElse(new Documento());

        documento.setTipo(tipo);
        documento.setArchivo(archivo.getBytes());
        documento.setNombreArchivo(archivo.getOriginalFilename());
        documento.setContentType(archivo.getContentType());
        documento.setEstado(EstadoDocumento.PENDIENTE);
        documento.setPreinscripcion(preinscripcion);

        return documentoRepository.save(documento);
    }

    public Documento obtenerPorId(Long documentoId) {
        return documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + documentoId));
    }

    public List<Documento> listarPorPreinscripcion(Long preinscripcionId) {
        if (!preinscripcionRepository.existsById(preinscripcionId)) {
            throw new RuntimeException("Preinscripción no encontrada con ID: " + preinscripcionId);
        }
        return documentoRepository.findByPreinscripcionId(preinscripcionId);
    }

    public Documento validarDocumento(Long documentoId) {
        Documento documento = obtenerPorId(documentoId);
        documento.setEstado(EstadoDocumento.VALIDADO);
        Documento guardado = documentoRepository.save(documento);

        verificarDocumentosCompletos(documento.getPreinscripcion().getId());

        return guardado;
    }

    public Documento solicitarResubida(Long documentoId) {
        Documento documento = obtenerPorId(documentoId);
        documento.setEstado(EstadoDocumento.RESUBIR);
        return documentoRepository.save(documento);
    }

    private void verificarDocumentosCompletos(Long preinscripcionId) {
        long validados = documentoRepository.countByPreinscripcionIdAndTipoInAndEstado(
                preinscripcionId, DOCS_OBLIGATORIOS, EstadoDocumento.VALIDADO);

        if (validados >= DOCS_OBLIGATORIOS.size()) {
            preinscripcionRepository.findById(preinscripcionId).ifPresent(pre -> {
                pre.setEstado(EstadoPreinscripcion.DOCUMENTOS_COMPLETOS);
                pre.setDocumentosCompletos(true);
                preinscripcionRepository.save(pre);
                log.info("Preinscripción {} marcada como DOCUMENTOS_COMPLETOS", preinscripcionId);
            });
        }
    }
}
