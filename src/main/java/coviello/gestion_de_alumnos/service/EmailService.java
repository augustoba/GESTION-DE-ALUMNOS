package coviello.gestion_de_alumnos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    private void enviar(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(from);
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
}
