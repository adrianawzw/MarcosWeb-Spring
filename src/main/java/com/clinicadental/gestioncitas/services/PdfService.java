package com.clinicadental.gestioncitas.services;

import com.clinicadental.gestioncitas.entities.Cita;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generarComprobanteCita(Cita cita) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // 🔹 Encabezado
            Paragraph header = new Paragraph("COMPROBANTE DE CITA MÉDICA")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18);
            document.add(header);

            document.add(new Paragraph("\n"));

            // 🔹 Información de la clínica
            Paragraph clinica = new Paragraph("Clínica Dental Sonrisa Saludable")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14);
            document.add(clinica);

            Paragraph direccion = new Paragraph("Av. Principal #123 - Tel: (01) 234-5678")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10);
            document.add(direccion);

            document.add(new Paragraph("\n"));

            // 🔹 Tabla con los detalles
            float[] columnWidths = {2, 5};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            
            // Formateador de fecha
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            table.addCell("N° de Cita:");
            table.addCell(cita.getIdCita().toString());

            table.addCell("Paciente:");
            table.addCell(cita.getPaciente().getUsuario().getNombre() + " " + 
                         cita.getPaciente().getUsuario().getApellido());

            table.addCell("DNI:");
            table.addCell(cita.getPaciente().getUsuario().getDni());

            table.addCell("Fecha:");
            table.addCell(cita.getFecha().format(dateFormatter));

            table.addCell("Hora:");
            table.addCell(cita.getHoraInicio().format(timeFormatter) + " - " + 
                         cita.getHoraFin().format(timeFormatter));

            table.addCell("Odontólogo:");
            table.addCell(cita.getOdontologo().getUsuario().getNombre() + " " + 
                         cita.getOdontologo().getUsuario().getApellido());

            table.addCell("Consultorio:");
            table.addCell(cita.getConsultorio().getNombre() + " - " + 
                         cita.getConsultorio().getUbicacion());

            table.addCell("Servicio:");
            table.addCell(cita.getServicio().getNombre());

            table.addCell("Estado:");
            table.addCell(cita.getEstado());

            if (cita.getObservaciones() != null && !cita.getObservaciones().isEmpty()) {
                table.addCell("Observaciones:");
                table.addCell(cita.getObservaciones());
            }

            document.add(table);

            document.add(new Paragraph("\n\n"));

            // 🔹 Pie de página
            Paragraph footer = new Paragraph(
                    "Por favor presente este comprobante el día de su cita. " +
                    "Llegar 15 minutos antes de la hora programada.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic()
                    .setFontSize(10);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }
}