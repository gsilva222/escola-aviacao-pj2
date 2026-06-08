package pt.ipvc.estg.desktop.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class PdfExportHelper {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private PdfExportHelper() {
    }

    public static void exportStudentList(File outputFile, List<Student> students) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputFile));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("LISTA DE ALUNOS - AeroSchool")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(DATETIME_FMT))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(" "));

        Table table = new Table(new float[]{3, 2, 2, 1, 1, 1});
        table.addHeaderCell(new Cell().add(new Paragraph("Nome").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Email").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Curso").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Progresso").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Horas").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Estado").setBold()));

        for (Student s : students) {
            table.addCell(s.getName() != null ? s.getName() : "");
            table.addCell(s.getEmail() != null ? s.getEmail() : "");
            table.addCell(s.getCourse() != null ? s.getCourse().getName() : "N/A");
            table.addCell((s.getProgress() != null ? s.getProgress() : 0) + "%");
            table.addCell(String.format(Locale.ROOT, "%.1fh", s.getFlightHours() != null ? s.getFlightHours() : 0.0));
            table.addCell(s.getStatus() != null ? s.getStatus() : "");
        }

        doc.add(table);
        doc.close();
    }

    public static void exportReportsSummary(File outputFile, String period,
                                            String totalStudents, String flightHours,
                                            String passRate, String revenue) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputFile));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("RELATORIO DE ANALISE - AeroSchool")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Periodo: " + period).setFontSize(12));
        doc.add(new Paragraph("Gerado em: " + LocalDate.now().format(DATE_FMT)).setFontSize(10));
        doc.add(new Paragraph(" "));

        Table table = new Table(2);
        table.addCell("Total de Alunos");
        table.addCell(totalStudents);
        table.addCell("Horas Voadas");
        table.addCell(flightHours);
        table.addCell("Taxa de Aprovacao");
        table.addCell(passRate);
        table.addCell("Receita Acumulada");
        table.addCell(revenue);
        doc.add(table);
        doc.close();
    }

    public static void exportPaymentStatement(File outputFile, Student student, List<Payment> payments) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputFile));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("EXTRATO DE PAGAMENTOS")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Aluno: " + student.getName()).setFontSize(12));
        doc.add(new Paragraph("Email: " + student.getEmail()).setFontSize(10));
        doc.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(DATETIME_FMT)).setFontSize(10));
        doc.add(new Paragraph(" "));

        Table table = new Table(new float[]{3, 2, 2, 2, 2});
        table.addHeaderCell(new Cell().add(new Paragraph("Descricao").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Valor").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Vencimento").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Pago em").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Estado").setBold()));

        for (Payment p : payments) {
            table.addCell(p.getDescription() != null ? p.getDescription() : "");
            table.addCell(String.format(Locale.ROOT, "EUR %.2f", p.getAmount() != null ? p.getAmount() : 0.0));
            table.addCell(p.getDueDate() != null ? p.getDueDate().format(DATE_FMT) : "-");
            table.addCell(p.getPaidDate() != null ? p.getPaidDate().format(DATE_FMT) : "-");
            table.addCell(p.getStatus() != null ? p.getStatus() : "");
        }
        doc.add(table);
        doc.close();
    }

    public static void exportPaymentReceipt(File outputFile, Student student, Payment payment) throws Exception {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputFile));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("RECIBO DE PAGAMENTO")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("AeroSchool - Escola de Aviacao").setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(" "));

        Table table = new Table(2);
        table.addCell("Aluno");
        table.addCell(student.getName());
        table.addCell("Email");
        table.addCell(student.getEmail());
        table.addCell("Descricao");
        table.addCell(payment.getDescription() != null ? payment.getDescription() : "Pagamento");
        table.addCell("Valor");
        table.addCell(String.format(Locale.ROOT, "EUR %.2f", payment.getAmount() != null ? payment.getAmount() : 0.0));
        table.addCell("Data de pagamento");
        table.addCell(payment.getPaidDate() != null ? payment.getPaidDate().format(DATE_FMT) : LocalDate.now().format(DATE_FMT));
        table.addCell("Metodo");
        table.addCell(payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "Transferencia");
        table.addCell("Referencia");
        table.addCell("#" + (payment.getId() != null ? payment.getId() : "N/A"));
        doc.add(table);

        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Documento gerado automaticamente pelo portal do aluno.")
                .setFontSize(9)
                .setItalic());
        doc.close();
    }
}
