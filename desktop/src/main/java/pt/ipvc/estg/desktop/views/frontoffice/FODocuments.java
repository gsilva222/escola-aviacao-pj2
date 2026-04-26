package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Documentos do aluno com visual alinhado ao mockup.
 */
public class FODocuments extends JPanel {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color SOFT = new Color(148, 163, 184);       // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0
    private static final Color GREEN = new Color(22, 163, 74);        // #16A34A
    private static final Color ORANGE = new Color(217, 119, 6);       // #D97706

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Student student;
    private final List<DocumentInfo> documents = new ArrayList<>();

    public FODocuments(Student student) {
        this.student = student;
        loadDocuments();
        initializeUI();
    }

    private void loadDocuments() {
        documents.clear();
        File folder = new File("documentos/student_" + student.getId());
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isHidden() || !file.isFile()) {
                continue;
            }
            documents.add(new DocumentInfo(file));
        }
        documents.sort(Comparator.comparing((DocumentInfo doc) -> doc.file.lastModified()).reversed());
        if (documents.isEmpty()) {
            addMockDocuments();
        }
    }

    private void addMockDocuments() {
        documents.add(DocumentInfo.mock("Certificado de Matricula", "Certificado", "certificate", LocalDate.of(2024, 1, 15), null));
        documents.add(DocumentInfo.mock("Certificado Medico Classe 2", "Medico", "medical", LocalDate.of(2024, 8, 20), LocalDate.of(2026, 8, 20)));
        documents.add(DocumentInfo.mock("Cartao de Identificacao de Estudante", "Identificacao", "id", LocalDate.of(2024, 1, 15), null));
        documents.add(DocumentInfo.mock("Contrato de Formacao", "Contrato", "contract", LocalDate.of(2024, 1, 15), null));
        documents.add(DocumentInfo.mock("Seguro de Acidentes Pessoais", "Seguro", "insurance", LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31)));
        documents.add(DocumentInfo.mock("Declaracao de Frequencia - Fev 2025", "Declaracao", "declaration", LocalDate.of(2025, 2, 28), null));
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(createSummaryRow());
        content.add(Box.createVerticalStrut(12));
        content.add(createCertificatesSection());
        content.add(Box.createVerticalStrut(12));
        content.add(createAllDocumentsSection());
        content.add(Box.createVerticalStrut(12));
        content.add(createInfoBanner());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryRow() {
        long active = documents.stream().filter(doc -> doc.valid).count();
        long certificates = documents.stream().filter(doc -> "certificate".equals(doc.type)).count();
        long expiring = documents.stream().filter(DocumentInfo::isNearExpiry).count();

        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.add(createSummaryCard("Documentos Ativos", String.valueOf(active), GREEN));
        row.add(createSummaryCard("A Expirar em 30d", String.valueOf(expiring), ORANGE));
        row.add(createSummaryCard("Certificados", String.valueOf(certificates), BLUE));
        row.add(createSummaryCard("Total de Documentos", String.valueOf(documents.size()), MUTED));
        return row;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 24));
        card.add(valueLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        card.add(titleLabel);
        return card;
    }

    private JPanel createCertificatesSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Certificados e Diplomas");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        section.add(title);
        section.add(Box.createVerticalStrut(8));

        List<DocumentInfo> certs = documents.stream()
                .filter(doc -> "certificate".equals(doc.type) || "declaration".equals(doc.type))
                .limit(2)
                .toList();

        JPanel cards = new JPanel(new GridLayout(1, Math.max(1, certs.size()), 10, 0));
        cards.setOpaque(false);

        if (certs.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(true);
            empty.setBackground(WHITE);
            empty.setBorder(BorderFactory.createLineBorder(BORDER, 1));
            JLabel text = new JLabel("Sem certificados disponiveis.");
            text.setForeground(SOFT);
            text.setFont(new Font("Inter", Font.PLAIN, 12));
            empty.add(text);
            cards.add(empty);
        } else {
            for (DocumentInfo doc : certs) {
                cards.add(createCertificateCard(doc));
            }
        }

        section.add(cards);
        return section;
    }

    private JPanel createCertificateCard(DocumentInfo doc) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel icon = new JPanel(new GridBagLayout());
        icon.setOpaque(true);
        icon.setBackground(new Color(219, 234, 254));
        icon.setPreferredSize(new Dimension(42, 42));
        JLabel iconLabel = new JLabel("\u2605");
        iconLabel.setForeground(new Color(29, 78, 216));
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        icon.add(iconLabel);
        top.add(icon, BorderLayout.WEST);

        JLabel status = new JLabel(doc.valid ? "Valido" : "Invalido");
        status.setOpaque(true);
        status.setBackground(doc.valid ? new Color(220, 252, 231) : new Color(254, 226, 226));
        status.setForeground(doc.valid ? GREEN : new Color(220, 38, 38));
        status.setBorder(new EmptyBorder(4, 8, 4, 8));
        status.setFont(new Font("Inter", Font.BOLD, 11));
        top.add(status, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel name = new JLabel(doc.displayName);
        name.setForeground(TITLE);
        name.setFont(new Font("Inter", Font.BOLD, 12));
        body.add(name);

        JLabel desc = new JLabel(doc.description);
        desc.setForeground(MUTED);
        desc.setFont(new Font("Inter", Font.PLAIN, 11));
        body.add(desc);

        JLabel issued = new JLabel("Emitido: " + doc.formatDate(doc.issueDate));
        issued.setForeground(SOFT);
        issued.setFont(new Font("Inter", Font.PLAIN, 10));
        body.add(issued);
        body.add(Box.createVerticalStrut(8));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.setOpaque(false);
        JButton viewBtn = createSecondaryButton("Abrir");
        viewBtn.addActionListener(e -> openOrPreview(doc));
        JButton downloadBtn = createPrimaryButton("Download");
        downloadBtn.addActionListener(e -> saveOrPreview(doc));
        actions.add(viewBtn);
        actions.add(downloadBtn);
        body.add(actions);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAllDocumentsSection() {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Todos os Documentos");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        section.add(title);
        section.add(Box.createVerticalStrut(8));

        JPanel listCard = new JPanel();
        listCard.setOpaque(true);
        listCard.setBackground(WHITE);
        listCard.setLayout(new BoxLayout(listCard, BoxLayout.Y_AXIS));
        listCard.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        if (documents.isEmpty()) {
            JLabel empty = new JLabel("Nenhum documento disponivel.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            empty.setBorder(new EmptyBorder(18, 12, 18, 12));
            listCard.add(empty);
        } else {
            for (int i = 0; i < documents.size(); i++) {
                listCard.add(createDocumentRow(documents.get(i)));
                if (i < documents.size() - 1) {
                    listCard.add(new JSeparator(SwingConstants.HORIZONTAL));
                }
            }
        }

        section.add(listCard);
        return section;
    }

    private JPanel createDocumentRow(DocumentInfo doc) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(true);
        row.setBackground(WHITE);
        row.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel icon = new JPanel(new GridBagLayout());
        icon.setOpaque(true);
        icon.setBackground(doc.typeBg);
        icon.setPreferredSize(new Dimension(38, 38));
        JLabel iconLabel = new JLabel(doc.typeIcon);
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        row.add(icon, BorderLayout.WEST);
        icon.add(iconLabel);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel firstLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        firstLine.setOpaque(false);
        JLabel name = new JLabel(doc.displayName);
        name.setForeground(TITLE);
        name.setFont(new Font("Inter", Font.BOLD, 12));
        firstLine.add(name);
        JLabel typeBadge = new JLabel(doc.typeLabel);
        typeBadge.setOpaque(true);
        typeBadge.setBackground(doc.typeBg);
        typeBadge.setForeground(doc.typeColor);
        typeBadge.setFont(new Font("Inter", Font.BOLD, 10));
        typeBadge.setBorder(new EmptyBorder(3, 7, 3, 7));
        firstLine.add(typeBadge);
        center.add(firstLine);

        JLabel meta = new JLabel("Emitido: " + doc.formatDate(doc.issueDate)
                + " | Tamanho: " + doc.sizeKb + " KB"
                + (doc.expiryDate != null ? " | Validade: " + doc.formatDate(doc.expiryDate) : ""));
        meta.setForeground(SOFT);
        meta.setFont(new Font("Inter", Font.PLAIN, 10));
        center.add(meta);
        row.add(center, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        JLabel status = new JLabel(doc.valid ? "Valido" : "Invalido");
        status.setOpaque(true);
        status.setBackground(doc.valid ? new Color(220, 252, 231) : new Color(254, 226, 226));
        status.setForeground(doc.valid ? GREEN : new Color(220, 38, 38));
        status.setFont(new Font("Inter", Font.BOLD, 10));
        status.setBorder(new EmptyBorder(3, 7, 3, 7));
        right.add(status);

        JButton viewBtn = createSecondaryButton("Abrir");
        viewBtn.addActionListener(e -> openOrPreview(doc));
        right.add(viewBtn);

        JButton downloadBtn = createSecondaryButton("Guardar");
        downloadBtn.addActionListener(e -> saveOrPreview(doc));
        right.add(downloadBtn);

        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel createInfoBanner() {
        JPanel banner = new JPanel(new BorderLayout(10, 0));
        banner.setOpaque(true);
        banner.setBackground(new Color(239, 246, 255));
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel shield = new JLabel("\u26E8");
        shield.setForeground(new Color(29, 78, 216));
        shield.setFont(new Font("Dialog", Font.PLAIN, 18));
        banner.add(shield, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Seguranca dos Documentos");
        title.setForeground(new Color(29, 78, 216));
        title.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel desc = new JLabel("<html>Os seus documentos sao armazenados de forma segura e apenas utilizadores autorizados podem aceder aos mesmos.</html>");
        desc.setForeground(new Color(59, 130, 246));
        desc.setFont(new Font("Inter", Font.PLAIN, 11));
        text.add(title);
        text.add(desc);
        banner.add(text, BorderLayout.CENTER);
        return banner;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFont(new Font("Inter", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(6, 10, 6, 10));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFont(new Font("Inter", Font.PLAIN, 11));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(5, 9, 5, 9)
        ));
        return button;
    }

    private void openDocument(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir documento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openOrPreview(DocumentInfo doc) {
        if (doc.file.exists()) {
            openDocument(doc.file);
        } else {
            JOptionPane.showMessageDialog(this, doc.displayName + "\n" + doc.description, "Documento", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveOrPreview(DocumentInfo doc) {
        if (doc.file.exists()) {
            saveCopy(doc.file);
        } else {
            JOptionPane.showMessageDialog(this, "Download demo em desenvolvimento.", "Documento", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveCopy(File file) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(file.getName()));
        int option = chooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            Files.copy(file.toPath(), chooser.getSelectedFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(this, "Copia guardada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao guardar copia: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class DocumentInfo {
        private final File file;
        private final String displayName;
        private final String description;
        private final LocalDate issueDate;
        private final LocalDate expiryDate;
        private final boolean valid;
        private final long sizeKb;
        private final String type;
        private final String typeLabel;
        private final String typeIcon;
        private final Color typeColor;
        private final Color typeBg;

        private DocumentInfo(File file) {
            this.file = file;
            this.displayName = stripExtension(file.getName());
            this.description = "Documento oficial do percurso academico";
            this.issueDate = Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
            this.expiryDate = inferExpiryDate(file, issueDate);
            this.valid = !file.getName().toLowerCase(Locale.ROOT).contains("invalid")
                    && (expiryDate == null || !expiryDate.isBefore(LocalDate.now()));
            this.sizeKb = Math.max(1, file.length() / 1024);

            String ext = extension(file.getName());
            if ("pdf".equals(ext)) {
                this.type = "certificate";
                this.typeLabel = "Certificado";
                this.typeIcon = "\uD83D\uDCC4";
                this.typeColor = new Color(29, 78, 216);
                this.typeBg = new Color(219, 234, 254);
            } else if ("jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext)) {
                this.type = "id";
                this.typeLabel = "Identificacao";
                this.typeIcon = "\uD83D\uDCB3";
                this.typeColor = new Color(124, 58, 237);
                this.typeBg = new Color(243, 232, 255);
            } else if ("zip".equals(ext)) {
                this.type = "insurance";
                this.typeLabel = "Arquivo";
                this.typeIcon = "\uD83D\uDCE6";
                this.typeColor = new Color(217, 119, 6);
                this.typeBg = new Color(254, 243, 199);
            } else {
                this.type = "declaration";
                this.typeLabel = "Declaracao";
                this.typeIcon = "\uD83D\uDCC3";
                this.typeColor = new Color(100, 116, 139);
                this.typeBg = new Color(241, 245, 249);
            }
        }

        private DocumentInfo(String displayName, String typeLabel, String type, LocalDate issueDate, LocalDate expiryDate) {
            this.file = new File(displayName + ".pdf");
            this.displayName = displayName;
            this.description = switch (type) {
                case "certificate" -> "Certificado de inscricao no curso de Piloto Privado (PPL) da AeroSchool";
                case "declaration" -> "Declaracao de frequencia do percurso PPL";
                case "medical" -> "Certificado medico necessario para formacao pratica";
                case "contract" -> "Contrato de formacao assinado";
                case "insurance" -> "Apolice de seguro de acidentes pessoais";
                default -> "Documento do aluno";
            };
            this.issueDate = issueDate;
            this.expiryDate = expiryDate;
            this.valid = expiryDate == null || !expiryDate.isBefore(LocalDate.now());
            this.sizeKb = 245;
            this.type = type;
            this.typeLabel = typeLabel;
            this.typeIcon = switch (type) {
                case "medical" -> "\u26E8";
                case "id" -> "\u25AD";
                case "contract" -> "\uD83D\uDCC4";
                case "insurance" -> "\u25EF";
                case "declaration" -> "\uD83D\uDCC3";
                default -> "\u269D";
            };
            this.typeColor = switch (type) {
                case "medical" -> new Color(22, 163, 74);
                case "id" -> new Color(124, 58, 237);
                case "contract" -> new Color(217, 119, 6);
                case "insurance" -> new Color(220, 38, 38);
                case "declaration" -> new Color(100, 116, 139);
                default -> new Color(29, 78, 216);
            };
            this.typeBg = switch (type) {
                case "medical" -> new Color(220, 252, 231);
                case "id" -> new Color(243, 232, 255);
                case "contract" -> new Color(254, 243, 199);
                case "insurance" -> new Color(254, 226, 226);
                case "declaration" -> new Color(241, 245, 249);
                default -> new Color(219, 234, 254);
            };
        }

        private static DocumentInfo mock(String displayName, String typeLabel, String type, LocalDate issueDate, LocalDate expiryDate) {
            return new DocumentInfo(displayName, typeLabel, type, issueDate, expiryDate);
        }

        private static String extension(String name) {
            int idx = name.lastIndexOf('.');
            return idx < 0 ? "" : name.substring(idx + 1).toLowerCase(Locale.ROOT);
        }

        private static String stripExtension(String name) {
            int idx = name.lastIndexOf('.');
            return idx < 0 ? name : name.substring(0, idx);
        }

        private static LocalDate inferExpiryDate(File file, LocalDate issueDate) {
            String lower = file.getName().toLowerCase(Locale.ROOT);
            if (lower.contains("medical") || lower.contains("medico")) {
                return issueDate.plusYears(1);
            }
            if (lower.contains("insurance") || lower.contains("seguro")) {
                return issueDate.plusYears(1);
            }
            if (lower.contains("id") || lower.contains("ident")) {
                return issueDate.plusYears(5);
            }
            return null;
        }

        private boolean isNearExpiry() {
            return expiryDate != null && !expiryDate.isBefore(LocalDate.now()) && !expiryDate.isAfter(LocalDate.now().plusDays(30));
        }

        private String formatDate(LocalDate date) {
            return date == null ? "-" : date.format(DATE_FMT);
        }
    }
}
