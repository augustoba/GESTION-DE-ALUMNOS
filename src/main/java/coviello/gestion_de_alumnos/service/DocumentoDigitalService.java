package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.Alumno;
import coviello.gestion_de_alumnos.model.DocumentoDigital;
import coviello.gestion_de_alumnos.model.EstadoDocumento;
import coviello.gestion_de_alumnos.model.TipoDocumento;
import coviello.gestion_de_alumnos.repository.AlumnoRepository;
import coviello.gestion_de_alumnos.repository.DocumentoDigitalRepository;
import coviello.gestion_de_alumnos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentoDigitalService {

    @Value("${app.upload.dir:uploads/documentos}")
    private String uploadDir;

    private final DocumentoDigitalRepository documentoDigitalRepository;
    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;

    public DocumentoDigitalService(DocumentoDigitalRepository documentoDigitalRepository,
                                   AlumnoRepository alumnoRepository,
                                   UsuarioRepository usuarioRepository) {
        this.documentoDigitalRepository = documentoDigitalRepository;
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<DocumentoDigital> listarPorAlumno(Long alumnoId) {
        return documentoDigitalRepository.findByAlumnoId(alumnoId);
    }

    // El alumno registra la URL de su documento (subido previamente a almacenamiento externo)
    @Transactional
    public DocumentoDigital registrarDocumento(Long alumnoId, TipoDocumento tipoDocumento, String archivoUrl) {
        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + alumnoId));

        // Si ya existe un documento del mismo tipo, se reemplaza
        documentoDigitalRepository.findByAlumnoIdAndTipoDocumento(alumnoId, tipoDocumento)
                .ifPresent(documentoDigitalRepository::delete);

        DocumentoDigital doc = new DocumentoDigital();
        doc.setAlumno(alumno);
        doc.setTipoDocumento(tipoDocumento);
        doc.setArchivoUrl(archivoUrl);
        doc.setEstado(EstadoDocumento.PENDIENTE);
        doc.setFechaSubida(LocalDateTime.now());

        return documentoDigitalRepository.save(doc);
    }

    @Transactional
    public DocumentoDigital aprobar(Long documentoId, String adminUsername) {
        DocumentoDigital doc = findById(documentoId);
        doc.setEstado(EstadoDocumento.VALIDADO);
        doc.setFechaValidacion(LocalDateTime.now());
        doc.setMotivoRechazo(null);
        usuarioRepository.findByUsername(adminUsername)
                .ifPresent(doc::setValidadoPor);
        return documentoDigitalRepository.save(doc);
    }

    @Transactional
    public DocumentoDigital rechazar(Long documentoId, String motivo, String adminUsername) {
        DocumentoDigital doc = findById(documentoId);
        doc.setEstado(EstadoDocumento.RECHAZADO);
        doc.setFechaValidacion(LocalDateTime.now());
        doc.setMotivoRechazo(motivo);
        usuarioRepository.findByUsername(adminUsername)
                .ifPresent(doc::setValidadoPor);
        return documentoDigitalRepository.save(doc);
    }

    public DocumentoDigital obtenerPorId(Long documentoId) {
        return findById(documentoId);
    }

    @Transactional
    public DocumentoDigital subirDocumento(Long alumnoId, TipoDocumento tipo, MultipartFile archivo) {
        if (archivo.isEmpty()) throw new RuntimeException("El archivo está vacío");

        String contentType = archivo.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new RuntimeException("Solo se permiten imágenes (JPG, PNG) o PDF");
        }

        String ext = obtenerExtension(archivo.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        Path dirPath = Paths.get(uploadDir);
        try {
            Files.createDirectories(dirPath);
            Files.copy(archivo.getInputStream(), dirPath.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }

        Alumno alumno = alumnoRepository.findById(alumnoId)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado: " + alumnoId));

        DocumentoDigital doc = documentoDigitalRepository
                .findByAlumnoIdAndTipoDocumento(alumnoId, tipo)
                .orElseGet(() -> {
                    DocumentoDigital nuevo = new DocumentoDigital();
                    nuevo.setAlumno(alumno);
                    nuevo.setTipoDocumento(tipo);
                    return nuevo;
                });

        doc.setArchivoUrl("/api/perfil/documentos/archivo/" + filename);
        doc.setEstado(EstadoDocumento.SUBIDO);
        doc.setFechaSubida(LocalDateTime.now());
        doc.setFechaValidacion(null);
        doc.setMotivoRechazo(null);
        doc.setValidadoPor(null);

        return documentoDigitalRepository.save(doc);
    }

    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.')).toLowerCase();
    }

    private DocumentoDigital findById(Long id) {
        return documentoDigitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado: " + id));
    }
}
