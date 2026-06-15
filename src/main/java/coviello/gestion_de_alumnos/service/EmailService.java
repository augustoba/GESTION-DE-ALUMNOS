package coviello.gestion_de_alumnos.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.institucion}")
    private String institucion;

    @Value("${frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarFormularioPreinscripcion(String destinatario, String nombre, String codigo, byte[] pdf) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject("Formulario de Preinscripción " + codigo + " - " + institucion);
            helper.setText("""
                    Hola %s,

                    Recibimos tu formulario de preinscripción correctamente.

                    Tu código de formulario es: %s

                    Adjunto encontrás el formulario en formato PDF. Por favor imprimilo y
                    presentalo el día de tu inscripción presencial junto con la documentación
                    requerida: DNI (original y copia), título secundario (original y copia),
                    foto carné 4x4, acta de nacimiento, psicofísico y certificado de buena conducta.

                    Saludos,
                    Administración - %s
                    """.formatted(nombre, codigo, institucion));
            helper.addAttachment(
                    "formulario-preinscripcion-" + codigo + ".pdf",
                    new ByteArrayResource(pdf),
                    "application/pdf");
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el formulario por email: " + e.getMessage(), e);
        }
    }

    public void enviarBienvenida(String destinatario, String nombre) {
        enviar(destinatario,
                "Bienvenido/a al sistema de inscripciones - " + institucion,
                """
                Hola %s,

                Tu cuenta fue creada exitosamente en el sistema de inscripciones de %s.

                Ya podés iniciar sesión con tu email y la contraseña que elegiste.

                Saludos,
                Administración - %s
                """.formatted(nombre, institucion, institucion));
    }

    public void enviarActivacionCuenta(String destinatario, String nombre, String carrera, String token) {
        String urlActivacion = frontendUrl + "/activar-cuenta?token=" + token;
        enviar(destinatario,
                "Activá tu cuenta — " + institucion,
                """
                Hola %s,

                ¡Tu inscripción para "%s" fue aprobada! Tu cuenta fue creada en el sistema de %s.

                Para ingresar al sistema por primera vez, necesitás elegir una contraseña.
                Hacé click en el siguiente enlace (válido por 72 horas):

                  %s

                Si no solicitaste esta cuenta o el enlace ya no funciona, contactá a la administración.

                Saludos,
                Administración - %s
                """.formatted(nombre, carrera, institucion, urlActivacion, institucion));
    }

    public void enviarAlumnoHabilitado(String destinatario, String nombre, String carrera) {
        enviar(destinatario,
                "Tu inscripción fue aprobada - " + institucion,
                """
                Hola %s,

                ¡Felicitaciones! Tu inscripción para la carrera "%s" fue procesada
                y tu cuenta de alumno fue habilitada correctamente.

                Ya podés iniciar sesión con tu email y contraseña habituales para acceder
                al sistema del instituto.

                Bienvenido/a.

                Saludos,
                Administración - %s
                """.formatted(nombre, carrera, institucion));
    }

    public void enviarTurnoAsignado(String destinatario, String nombre, String numeroTurno,
                                    String fecha, String hora, String urlConfirmacion) {
        enviar(destinatario,
                "Tu turno de inscripción: " + numeroTurno + " - " + institucion,
                """
                Hola %s,

                Se te asignó el siguiente turno para la inscripción presencial:

                  Turno: %s
                  Fecha: %s
                  Hora: %s

                Para confirmar tu asistencia hacé click en el siguiente enlace:

                  %s

                Si no confirmás tu turno, podría ser reasignado.

                Saludos,
                Administración - %s
                """.formatted(nombre, numeroTurno, fecha, hora, urlConfirmacion, institucion));
    }

    public void enviarNuevaContrasena(String destinatario, String nombre, String nuevaContrasena) {
        enviar(destinatario,
                "Recuperación de contraseña - " + institucion,
                """
                Hola %s,

                Recibiste este email porque solicitaste recuperar tu contraseña en el sistema de %s.

                Tu nueva contraseña temporal es:

                  %s

                Por seguridad, te recomendamos cambiarla una vez que ingreses al sistema.

                Si no solicitaste este cambio, ignorá este mensaje.

                Saludos,
                Administración - %s
                """.formatted(nombre, institucion, nuevaContrasena, institucion));
    }

    public void enviarBienvenidaDocente(String destinatario, String nombres, String passwordTemporal) {
        enviar(destinatario,
                "Bienvenido/a al sistema docente - " + institucion,
                """
                Hola %s,

                Tu cuenta docente fue creada exitosamente en el sistema de %s.

                Podés iniciar sesión con tu email y la siguiente contraseña temporal:

                  Contraseña temporal: %s

                Al ingresar por primera vez el sistema te pedirá que establezcas una nueva contraseña.

                Saludos,
                Administración - %s
                """.formatted(nombres, institucion, passwordTemporal, institucion));
    }

    public void enviarBienvenidaAdmin(String destinatario, String nombres, String passwordTemporal) {
        enviar(destinatario,
                "Cuenta de administrador creada - " + institucion,
                """
                Hola %s,

                Se creó tu cuenta de administrador en el sistema de %s.

                Podés iniciar sesión con tu email y la siguiente contraseña temporal:

                  Contraseña: %s

                Por seguridad, te recomendamos cambiarla al ingresar por primera vez.

                Saludos,
                Super Administración - %s
                """.formatted(nombres, institucion, passwordTemporal, institucion));
    }

    public void enviarSolicitudDocumentos(String destinatario, String nombre, List<String> tiposFaltantes) {
        String lista = tiposFaltantes.stream()
                .map(t -> "  - " + nombreLegibleDocumento(t))
                .collect(java.util.stream.Collectors.joining("\n"));

        enviar(destinatario,
                "Documentos pendientes de entrega - " + institucion,
                """
                Hola %s,

                Desde administración te informamos que tenés los siguientes documentos pendientes:

                %s

                Te pedimos que los presentes a la brevedad en la administración del instituto.

                Saludos,
                Administración - %s
                """.formatted(nombre, lista, institucion));
    }

    public void enviarAperturaTurnos(String destinatario, String nombre, String mensajeExtra) {
        enviar(destinatario,
                "Apertura de turnos para inscripción presencial - " + institucion,
                """
                Hola %s,

                Te informamos que ya podés solicitar tu turno para la inscripción presencial en %s.

                %s

                Ingresá al siguiente enlace para solicitar tu turno:

                  %s/solicitar-turno

                Recordá que los turnos tienen cupo limitado por día, así que te recomendamos
                solicitarlo lo antes posible.

                Saludos,
                Administración - %s
                """.formatted(nombre, institucion, mensajeExtra, frontendUrl, institucion));
    }

    public void enviarMasivo(String destinatario, String asunto, String cuerpo) {
        enviar(destinatario, asunto, cuerpo);
    }

    private String nombreLegibleDocumento(String tipo) {
        return switch (tipo) {
            case "DNI_FRENTE"    -> "DNI (frente)";
            case "DNI_DORSO"     -> "DNI (dorso)";
            case "TITULO"        -> "Título secundario";
            case "FOTO_CARNET"   -> "Foto carnet";
            case "ACTA_NACIMIENTO" -> "Acta de nacimiento";
            case "PSICOFISICO"   -> "Psicofísico";
            case "BUENA_CONDUCTA" -> "Certificado de buena conducta";
            default              -> tipo;
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
