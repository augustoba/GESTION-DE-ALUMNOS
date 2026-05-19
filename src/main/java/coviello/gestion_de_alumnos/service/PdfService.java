package coviello.gestion_de_alumnos.service;

import coviello.gestion_de_alumnos.model.Preinscripcion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;

@Service
public class PdfService {

    private static final float MARGEN = 50f;
    private static final float ANCHO_PAGINA = PDRectangle.A4.getWidth();
    private static final float ALTO_PAGINA  = PDRectangle.A4.getHeight();

    public byte[] generarFormularioPreinscripcion(Preinscripcion pre) {
        try (PDDocument doc = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            doc.addPage(pagina);

            PDType1Font bold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font italic  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

            try (PDPageContentStream cs = new PDPageContentStream(doc, pagina)) {
                float y = ALTO_PAGINA - MARGEN;
                float col = MARGEN;
                float ancho = ANCHO_PAGINA - 2 * MARGEN;

                // ── N° DE FORMULARIO (arriba, grande y en negrita) ──────
                String nroLabel = "N  de formulario:  ";
                String nroValor = String.valueOf(pre.getId());
                float nroLabelW = bold.getStringWidth(nroLabel) / 1000 * 11;
                float nroX = ANCHO_PAGINA - MARGEN - nroLabelW
                           - bold.getStringWidth(nroValor) / 1000 * 22 - 8;
                escribir(cs, bold, 11, nroLabel, nroX, y);
                escribir(cs, bold, 22, nroValor, nroX + nroLabelW, y - 4);
                y -= 32;

                // ── TÍTULO ──────────────────────────────────────────────
                int anio = LocalDate.now().getYear();
                y = escribirCentrado(cs, bold, 13, "SOLICITUD INSCRIPCION DEFINITIVA " + anio, y);
                y -= 4;
                linea(cs, col, col + ancho, y);
                y -= 6;

                // ── ENCABEZADO INSTITUCIÓN (logo izquierda + texto derecha) ──
                float headerY = y;
                float logoH   = 44f;
                float logoW   = 44f;
                try (InputStream logoIs = getClass().getResourceAsStream("/static/logo.jpg")) {
                    if (logoIs != null) {
                        PDImageXObject img = PDImageXObject.createFromByteArray(doc,
                                logoIs.readAllBytes(), "logo");
                        cs.drawImage(img, col, headerY - logoH, logoW, logoH);
                    }
                } catch (Exception ignored) {}

                float textX = col + logoW + 10;
                escribir(cs, bold, 12, "INSTITUTO DE ENSEÑANZA", textX, headerY - 14);
                escribir(cs, bold, 12, "SUPERIOR ALFREDO COVIELLO", textX, headerY - 30);

                y = headerY - logoH - 8;
                linea(cs, col, col + ancho, y);
                y -= 10;

                // ── CARRERAS ─────────────────────────────────────────────
                String carreraNombre = pre.getCarrera() != null ? pre.getCarrera().getNombre() : "";
                y = escribirCheckbox(cs, regular, bold, 10,
                        "Técnico Superior en Administración de Empresa",
                        carreraNombre.contains("Administración"), col, y);
                y = escribirCheckbox(cs, regular, bold, 10,
                        "Técnico Superior en Desarrollo de Software",
                        carreraNombre.contains("Software"), col, y);
                y = escribirCheckbox(cs, regular, bold, 10,
                        "Técnico Superior en Gestión y Organización de Eventos",
                        carreraNombre.contains("Eventos"), col, y);
                y -= 4;
                linea(cs, col, col + ancho, y);
                y -= 10;

                // ── DATOS PERSONALES ──────────────────────────────────────
                String apellidoNombre = nvl(pre.getApellido()) + ", " + nvl(pre.getNombre());
                y = campoLinea(cs, regular, bold, 10, "Apellido/s y Nombre/s:", apellidoNombre, col, y, ancho);

                String edad = "";
                String fechaNac = "";
                if (pre.getFechaNacimiento() != null) {
                    fechaNac = pre.getFechaNacimiento().getDayOfMonth() + "/" +
                               pre.getFechaNacimiento().getMonthValue() + "/" +
                               pre.getFechaNacimiento().getYear();
                    edad = String.valueOf(Period.between(pre.getFechaNacimiento(), LocalDate.now()).getYears());
                }
                float xDer = col + ancho / 2f;
                y = dosCampos(cs, regular, bold, 10,
                        "DNI:", nvl(pre.getDni()), col,
                        "Fecha Nac.:", fechaNac, xDer,
                        y, ancho / 2f - 5);
                y = campoLinea(cs, regular, bold, 10, "Edad:", edad, xDer, y, ancho / 2f - 5);

                y = dosCampos(cs, regular, bold, 10,
                        "Lugar de Nacimiento:", nvl(pre.getLugarNacimiento()), col,
                        "Nacionalidad:", nvl(pre.getNacionalidad()), col + ancho / 2f,
                        y, ancho / 2f - 5);

                y = dosCampos(cs, regular, bold, 10,
                        "Domicilio:", nvl(pre.getDireccion()), col,
                        "Localidad:", nvl(pre.getLocalidad()), col + ancho / 2f,
                        y, ancho / 2f - 5);

                y = dosCampos(cs, regular, bold, 10,
                        "Teléfono:", nvl(pre.getTelefono()), col,
                        "Correo electrónico:", nvl(pre.getEmail()), col + ancho / 2f,
                        y, ancho / 2f - 5);

                y -= 4;
                linea(cs, col, col + ancho, y);
                y -= 10;

                // ── DATOS EDUCATIVOS ───────────────────────────────────────
                y = campoLinea(cs, regular, bold, 10, "Egresado de:", nvl(pre.getEgresadoDe()), col, y, ancho);
                y = campoLinea(cs, regular, bold, 10, "Titulo de:", nvl(pre.getTituloDe()), col, y, ancho);

                // "Debe materias" sin renglón debajo del SI/NO
                String siNo = Boolean.TRUE.equals(pre.getDebeMaterias()) ? "SI [X]  -  NO [ ]"
                                                                          : "SI [ ]  -  NO [X]";
                if (pre.getDebeMaterias() == null) siNo = "SI [ ]  -  NO [ ]";
                float labelDMW = bold.getStringWidth("Debe materias del Secundario?   ") / 1000 * 10;
                escribir(cs, bold,    10, "Debe materias del Secundario?   ", col, y);
                escribir(cs, regular, 10, siNo, col + labelDMW, y);
                y -= 16;
                y = campoLinea(cs, regular, bold, 10, "Indicar las materias:", nvl(pre.getMateriasAdeudadas()), col, y, ancho);
                y -= 4;
                linea(cs, col, col + ancho, y);
                y -= 10;

                // ── REQUISITOS (NO LLENAR) ────────────────────────────────
                float reqTop = y;
                float xItems = col + 135;   // columna derecha: items
                float xSiNo  = col + ancho - 55; // columna derecha: "Si [ ] No [ ]"

                String[] reqItems = {
                    "- Titulo Secundario:",
                    "- Constancia de Titulo en Tramite:",
                    "- DNI:",
                    "- FOTO:",
                    "- Acta de Nacimiento:",
                    "- Psicofisico:",
                    "- Certificado de Buena Conducta:"
                };
                float itemY = reqTop;
                for (String item : reqItems) {
                    escribir(cs, regular, 9, item, xItems, itemY);
                    siNoCajas(cs, regular, 9, xSiNo, itemY);
                    itemY -= 16;
                }

                // "Requisitos: / NO LLENAR" centrado verticalmente en la columna izquierda
                float blockH   = reqTop - itemY;
                float labelY   = reqTop - blockH / 2f + 10;
                escribir(cs, bold, 10, "Requisitos:", col + 5, labelY);
                escribir(cs, bold,  9, "NO LLENAR",  col + 5, labelY - 13);

                // línea vertical separando columnas
                cs.moveTo(xItems - 8, reqTop + 4);
                cs.lineTo(xItems - 8, itemY + 6);
                cs.stroke();

                y = itemY - 6;
                linea(cs, col, col + ancho, y);
                y -= 10;

                // ── SALUD ──────────────────────────────────────────────────
                y = campoLinea(cs, regular, bold, 10, "Si padece alguna afección específica INDIQUELA:",
                        nvl(pre.getAfeccionEspecifica()), col, y, ancho);
                y = campoLinea(cs, regular, bold, 10, "Grupo Sanguíneo:", nvl(pre.getGrupoSanguineo()), col, y, ancho);
                y -= 4;
                linea(cs, col, col + ancho, y);
                y -= 10;

                // ── DECLARACIÓN JURADA ─────────────────────────────────────
                String decl = "Con carácter de DECLARACION JURADA, suscribe que los datos consignados en la presente,";
                String decl2 = "son exactos y completos. Después de haber tomado conocimiento de las condiciones de ingreso,";
                String decl3 = "cursado, correlatividades, ACEPTO EL COMPROMISO de cumplir las disposiciones del reglamento.";
                escribir(cs, italic, 8, decl, col, y);  y -= 11;
                escribir(cs, italic, 8, decl2, col, y); y -= 11;
                escribir(cs, italic, 8, decl3, col, y); y -= 20;

                // ── FIRMA ──────────────────────────────────────────────────
                y = escribirCentrado(cs, bold, 10, "QUEDO/A NOTIFICADO", y);
                y -= 25;
                float xFirma = ANCHO_PAGINA / 2f - 50;
                linea(cs, xFirma, xFirma + 100, y);
                y -= 10;
                escribirCentrado(cs, regular, 9, "FIRMA", y);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF de preinscripción", e);
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void escribir(PDPageContentStream cs, PDType1Font font, float size, String texto,
                           float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitizar(texto));
        cs.endText();
    }

    private float escribirCentrado(PDPageContentStream cs, PDType1Font font, float size,
                                    String texto, float y) throws IOException {
        float w = font.getStringWidth(sanitizar(texto)) / 1000 * size;
        escribir(cs, font, size, texto, (ANCHO_PAGINA - w) / 2f, y);
        return y - size - 4;
    }

    private void linea(PDPageContentStream cs, float x1, float x2, float y) throws IOException {
        cs.moveTo(x1, y);
        cs.lineTo(x2, y);
        cs.stroke();
    }

    private float campoLinea(PDPageContentStream cs, PDType1Font regular, PDType1Font bold,
                               float size, String label, String valor, float x, float y, float ancho) throws IOException {
        float labelW = bold.getStringWidth(sanitizar(label)) / 1000 * size;
        escribir(cs, bold,    size, label, x, y);
        escribir(cs, regular, size, " " + valor, x + labelW, y);
        float lineaY = y - 2;
        linea(cs, x + labelW + 3, x + ancho, lineaY);
        return y - size - 6;
    }

    private float dosCampos(PDPageContentStream cs, PDType1Font regular, PDType1Font bold,
                              float size,
                              String label1, String valor1, float x1,
                              String label2, String valor2, float x2,
                              float y, float ancho2) throws IOException {
        float lw1 = bold.getStringWidth(sanitizar(label1)) / 1000 * size;
        float lw2 = bold.getStringWidth(sanitizar(label2)) / 1000 * size;
        escribir(cs, bold, size, label1, x1, y);
        escribir(cs, regular, size, " " + valor1, x1 + lw1, y);
        linea(cs, x1 + lw1 + 3, x2 - 5, y - 2);

        escribir(cs, bold, size, label2, x2, y);
        escribir(cs, regular, size, " " + valor2, x2 + lw2, y);
        linea(cs, x2 + lw2 + 3, x2 + ancho2, y - 2);

        return y - size - 6;
    }

    private float escribirCheckbox(PDPageContentStream cs, PDType1Font regular, PDType1Font bold,
                                    float size, String texto, boolean marcado, float x, float y) throws IOException {
        String box = marcado ? "[X] " : "[ ] ";
        float bw = regular.getStringWidth(box) / 1000 * size;
        escribir(cs, regular, size, box, x, y);
        escribir(cs, regular, size, texto, x + bw, y);
        return y - size - 5;
    }

    private void siNoCajas(PDPageContentStream cs, PDType1Font font, float size,
                            float x, float y) throws IOException {
        float siW  = font.getStringWidth("Si ") / 1000 * size;
        float noW  = font.getStringWidth("No ") / 1000 * size;
        float boxW = 14f;
        float boxH = 11f;
        float gap  = 10f;
        float boxY = y - boxH + 2f;

        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText("Si");
        cs.endText();

        cs.addRect(x + siW, boxY, boxW, boxH);
        cs.stroke();

        float noX = x + siW + boxW + gap;
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(noX, y);
        cs.showText("No");
        cs.endText();

        cs.addRect(noX + noW, boxY, boxW, boxH);
        cs.stroke();
    }

    private String sanitizar(String s) {
        if (s == null) return "";
        return s.replace("¿","").replace("¡","")
                .replace("°","").replace("—","-").replace("–","-");
    }

    private String nvl(String s) {
        return s != null && !s.isBlank() ? s : "";
    }
}
