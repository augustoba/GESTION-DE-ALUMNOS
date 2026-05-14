package coviello.gestion_de_alumnos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.institucion}")
    private String institucion;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarBienvenida(String destinatario, String nombre) {
        String asunto = "Bienvenido/a al sistema de inscripciones - " + institucion;
        String cuerpo = """
                Hola %s,

                Tu cuenta fue creada exitosamente en el sistema de inscripciones de %s.

                Ya podés iniciar sesión con tu email y la contraseña que elegiste.

                Saludos,
                Administración - %s
                """.formatted(nombre, institucion, institucion);

        enviar(destinatario, asunto, cuerpo);
    }

    public void enviarPagoValidado(String destinatario, String nombre, String carrera) {
        String asunto = "Pago de matrícula validado - " + carrera;
        String cuerpo = """
                Hola %s,

                Tu comprobante de pago para la carrera "%s" fue validado correctamente.

                Para completar tu inscripción debés subir los siguientes documentos:
                  - DNI (frente)
                  - DNI (dorso)
                  - Título secundario

                Accedé al sistema para cargarlos.

                Saludos,
                Administración - %s
                """.formatted(nombre, carrera, institucion);

        enviar(destinatario, asunto, cuerpo);
    }

    public void enviarPagoRechazado(String destinatario, String nombre, String motivo) {
        String asunto = "Problema con tu comprobante de pago - " + institucion;
        String cuerpo = """
                Hola %s,

                Encontramos un problema con tu comprobante de pago:

                  %s

                Por favor, volvé a subir el comprobante corregido desde el sistema.

                Saludos,
                Administración - %s
                """.formatted(nombre, motivo, institucion);

        enviar(destinatario, asunto, cuerpo);
    }

    public void enviarInscripcionExpirada(String destinatario, String nombre, String carrera) {
        String asunto = "Tu inscripción venció - " + institucion;
        String cuerpo = """
                Hola %s,

                Tu inscripción para la carrera "%s" fue dada de baja porque no se completó
                la validación del pago dentro de las 48 horas reglamentarias.

                Si todavía deseás inscribirte, podés volver a iniciar el proceso cuando
                haya cupos disponibles.

                Saludos,
                Administración - %s
                """.formatted(nombre, carrera, institucion);

        enviar(destinatario, asunto, cuerpo);
    }

    public void enviarDocumentosAprobados(String destinatario, String nombre, String carrera) {
        String asunto = "Inscripción aprobada - " + carrera;
        String cuerpo = """
                Hola %s,

                ¡Felicitaciones! Todos tus documentos para la carrera "%s" fueron revisados
                y aprobados por la administración.

                Ya tenés acceso completo al sistema. Podés iniciar sesión con tu email
                y contraseña habituales.

                Bienvenido/a al instituto.

                Saludos,
                Administración - %s
                """.formatted(nombre, carrera, institucion);

        enviar(destinatario, asunto, cuerpo);
    }

    public void enviarDocumentosRechazados(String destinatario, String nombre, String carrera,
                                           List<String> tiposRechazados) {
        String listaDocumentos = tiposRechazados.stream()
                .map(this::nombreLegibleDocumento)
                .map(d -> "  - " + d)
                .collect(java.util.stream.Collectors.joining("\n"));

        String asunto = "Documentos pendientes de corrección - " + carrera;
        String cuerpo = """
                Hola %s,

                Revisamos tu documentación para la carrera "%s" y encontramos que los
                siguientes documentos deben ser corregidos o vueltos a subir:

                %s

                Por favor, ingresá al sistema y subí nuevamente los archivos indicados.
                Una vez resubidos, el equipo de administración los revisará nuevamente.

                Saludos,
                Administración - %s
                """.formatted(nombre, carrera, listaDocumentos, institucion);

        enviar(destinatario, asunto, cuerpo);
    }

    public void enviarBienvenidaDocente(String destinatario, String nombres, String dni) {
        String asunto = "Bienvenido/a al sistema docente - " + institucion;
        String cuerpo = """
                Hola %s,

                Tu cuenta docente fue creada exitosamente en el sistema de %s.

                Podés iniciar sesión con tu email y la siguiente contraseña inicial:

                  Contraseña: %s (tu número de DNI)

                Por seguridad, te recomendamos cambiar tu contraseña una vez que ingreses al sistema.

                Saludos,
                Administración - %s
                """.formatted(nombres, institucion, dni, institucion);

        enviar(destinatario, asunto, cuerpo);
    }

    private String nombreLegibleDocumento(String tipo) {
        return switch (tipo) {
            case "DNI_FRENTE"       -> "DNI (frente)";
            case "DNI_DORSO"        -> "DNI (dorso)";
            case "TITULO"           -> "Título secundario";
            case "FOTO_CARNET"      -> "Foto carnet";
            case "COMPROBANTE_PAGO" -> "Comprobante de pago";
            default                 -> tipo;
        };
    }

    private void enviar(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(from);
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
}
