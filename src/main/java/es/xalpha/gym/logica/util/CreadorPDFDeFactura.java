package es.xalpha.gym.logica.util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import es.xalpha.gym.logica.entidad.Factura;

import java.io.IOException;

public class CreadorPDFDeFactura {

    public static void crearPDF(Factura factura, Configuracion configuracion) throws IOException {
        try (PdfWriter pdfWriter = new PdfWriter(obtenerPath())) {
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument);
            darEstilo(document, factura, configuracion);
            document.close();
        }
    }

    private static String obtenerPath() {
        String path = ManipularArchivo.obtenerPath("", "PDF (*.pdf)", ".pdf");
        if (path != null && (path.trim().isEmpty() || path.equals(".pdf"))) {
            path = ManipularArchivo.pathDefault("nuevo documento pdf", ".pdf");
        }
        return path;
    }

    private static void darEstilo(Document document, Factura factura,
                                 Configuracion config) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(
                StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(
                StandardFonts.HELVETICA);

        Paragraph header = new Paragraph("***** FACTURA *****").setFont(
                boldFont).setFontSize(24).setTextAlignment(
                TextAlignment.CENTER).setFontColor(
                ColorConstants.BLUE).setMarginBottom(15);

        document.add(header);

        document.add(new LineSeparator(new SolidLine(2f)).setMarginBottom(15));

        document.add(tituloSeccion("Información del Local", boldFont));

        document.add(
                parrafoConBorde("Nombre del local: ", factura.getNomLocal(),
                        boldFont, regularFont));
        document.add(
                parrafoConBorde("Direccion: ", config.getDomicilio().getCalle(),
                        boldFont, regularFont));

        document.add(parrafoConBorde("Email: ", config.getContacto().getEmail(),
                boldFont, regularFont));

        document.add(parrafoConBorde("Teléfono: ",
                config.getContacto().getTelefono(), boldFont, regularFont));

        document.add(tituloSeccion("Información de la Factura", boldFont));

        document.add(parrafoConBorde("Nro. de factura: ",
                "" + factura.getNroFactura(), boldFont, regularFont));

        document.add(
                parrafoConBorde("Monto: ", "" + factura.getMonto(), boldFont,
                        regularFont));

        document.add(parrafoConBorde("Cliente: ",
                factura.getCliente().nombreCompleto(), boldFont, regularFont));

        document.add(parrafoConBorde("Fecha de emisión: ",
                UtilLogica.formatoFecha(factura.getFechaEmision(),
                        "dd-MM-yyyy"), boldFont, regularFont));

        document.add(new LineSeparator(new SolidLine(1f)).setMarginTop(
                10).setMarginBottom(20));

        Paragraph footer = new Paragraph(
                "***** ¡Muchas gracias por elegirnos! *****").setFont(
                boldFont).setFontSize(14).setTextAlignment(
                TextAlignment.CENTER).setFontColor(
                ColorConstants.BLUE).setMarginTop(30);

        document.add(footer);
    }

    private static Paragraph tituloSeccion(String titulo, PdfFont font) {
        return new Paragraph(titulo).setFont(font).setFontSize(16).setFontColor(
                ColorConstants.WHITE).setBackgroundColor(
                ColorConstants.DARK_GRAY).setTextAlignment(
                TextAlignment.CENTER).setPadding(5).setMarginBottom(10);
    }

    private static Paragraph parrafoConBorde(String label, String valor,
                                             PdfFont boldFont,
                                             PdfFont regularFont) {
        if (label == null) {
            label = "";
        }
        if (valor == null) {
            valor = "";
        }
        Text labelText = new Text(label).setFont(regularFont);
        Text valueText = new Text(valor).setFont(boldFont);
        return new Paragraph().add(labelText).add(valueText).setFontSize(
                12).setFontColor(ColorConstants.DARK_GRAY).setBorderBottom(
                new SolidBorder(0.5f)).setPaddingBottom(5).setMarginBottom(10);
    }
}
