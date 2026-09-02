package com.eldiamante360.factura.infrastructure.pdf;

import com.eldiamante360.factura.application.dto.FacturaResult;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;

/**
 * Genera el PDF (formato ticket, para impresora termica) de una factura ya
 * creada. Renderiza la plantilla Thymeleaf {@code factura/factura-pdf.html}
 * a HTML y la convierte a PDF con openhtmltopdf. El logo se embebe como
 * data URI en base64 para no depender de resolver rutas de archivo en
 * tiempo de render (funciona igual empaquetado en el jar).
 */
@Service
public class FacturaPdfService {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", new Locale("es", "CO")).withZone(ZoneId.systemDefault());

    private final TemplateEngine templateEngine;
    private final String logoBase64;

    public FacturaPdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.logoBase64 = cargarLogoBase64();
    }

    public byte[] generar(FacturaResult factura) {
        Context contexto = new Context();
        contexto.setVariable("factura", factura);
        contexto.setVariable("fechaFormateada", factura.fecha() != null ? FORMATO_FECHA.format(factura.fecha()) : "");
        contexto.setVariable("logoBase64", logoBase64);

        String html = templateEngine.process("factura/factura-pdf", contexto);

        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(salida);
            builder.run();
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el PDF de la factura " + factura.numero(), e);
        }
    }

    private String cargarLogoBase64() {
        try {
            byte[] bytes = new ClassPathResource("static/images/logo.jpeg").getInputStream().readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar el logo para el PDF de factura", e);
        }
    }
}
