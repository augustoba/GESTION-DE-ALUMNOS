package coviello.gestion_de_alumnos.exception;

import coviello.gestion_de_alumnos.Util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthentication(AuthenticationException e) throws AuthenticationException {
        throw e;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDenied(AccessDeniedException e) throws AccessDeniedException {
        throw e;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntime(RuntimeException e) {
        log.warn("Error de negocio: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ApiResponse(e.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidacion(MethodArgumentNotValidException e) {
        List<String> errores = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest()
                .body(new ApiResponse("Error de validación", errores));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleParamFaltante(MissingServletRequestParameterException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse("Parámetro requerido faltante: " + e.getParameterName(), null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleBodyInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse("El cuerpo de la solicitud es inválido o está mal formado", null));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleArchivoGrande(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ApiResponse("El archivo supera el tamaño máximo permitido", null));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse> handleRutaNoEncontrada(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Ruta no encontrada: " + e.getResourcePath(), null));
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ApiResponse> handleMail(MailException e) {
        log.error("Error al enviar email: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiResponse("Operación completada pero no se pudo enviar el email. Contacte al administrador.", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneral(Exception e) {
        log.error("Error inesperado: {}", e.getMessage(), e);
        return ResponseEntity.internalServerError()
                .body(new ApiResponse("Error interno del servidor", null));
    }
}
