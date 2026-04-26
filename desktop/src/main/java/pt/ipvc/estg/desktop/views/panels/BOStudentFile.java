package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.desktop.controllers.EvaluationController;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Evaluation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog para visualizar detalhes de um estudante com 6 abas
 */
public class BOStudentFile extends JDialog {

    private final StudentController studentController;
    private final FlightController flightController;
    private final EvaluationController evaluationController;
    private Student student;
    
    // Document management
    private static final String DOCUMENTS_DIR = "documentos";
    private List<File> uploadedDocuments;

    // UI Components
    private JLabel lblStudentName;
    private JLabel lblStudentStatus;
    private JLabel lblStudentEmail;
    private JLabel lblFlightHours;
    private JLabel lblProgress;
    private JLabel lblTheoreticalHours;
    private JLabel lblEnrollmentDate;
    private JTabbedPane tabbedPane;
    private JPanel documentsList;

    public BOStudentFile(JFrame parent, Integer studentId) {
        super(parent, "Ficha do Estudante", true);
        this.studentController = new StudentController();
        this.flightController = new FlightController();
        this.evaluationController = new EvaluationController();
        this.uploadedDocuments = new ArrayList<>();

        // Load student
        this.student = studentController.obterEstudante(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado"));
        
        // Initialize document directory
        initializeDocumentDirectory();
        loadUploadedDocuments();

        initializeUI();
        loadStudentData();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dados Pessoais", createPersonalDataTab());
        tabbedPane.addTab("Documentos", createDocumentsTab());
        tabbedPane.addTab("Progresso do Curso", createProgressTab());
        tabbedPane.addTab("Histórico de Voos", createFlightsTab());
        tabbedPane.addTab("Avaliações", createEvaluationsTab());
        tabbedPane.addTab("Pagamentos", createPaymentsTab());

        add(tabbedPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // Back button
        JButton btnBack = new JButton("← Voltar");
        btnBack.addActionListener(e -> dispose());

        // Student info
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.setOpaque(false);

        infoPanel.add(new JLabel("Nome:"));
        lblStudentName = new JLabel();
        infoPanel.add(lblStudentName);

        infoPanel.add(new JLabel("Estado:"));
        lblStudentStatus = new JLabel();
        infoPanel.add(lblStudentStatus);

        infoPanel.add(new JLabel("Email:"));
        lblStudentEmail = new JLabel();
        infoPanel.add(lblStudentEmail);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setOpaque(false);

        JButton btnEdit = new JButton("✎ Editar Perfil");
        btnEdit.addActionListener(e -> editProfile());
        actionPanel.add(btnEdit);

        JButton btnExport = new JButton("⬇ Exportar Ficha");
        btnExport.addActionListener(e -> exportFile());
        actionPanel.add(btnExport);

        // Combine
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(btnBack, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.setBackground(new Color(240, 240, 240));

        // Horas de Voo
        JPanel card1 = createStatCard("Horas de Voo", "");
        lblFlightHours = (JLabel) card1.getComponent(1);
        panel.add(card1);

        // Progresso
        JPanel card2 = createStatCard("Progresso", "");
        lblProgress = (JLabel) card2.getComponent(1);
        panel.add(card2);

        // Teóricas
        JPanel card3 = createStatCard("Horas Teóricas", "");
        lblTheoreticalHours = (JLabel) card3.getComponent(1);
        panel.add(card3);

        // Matrícula
        JPanel card4 = createStatCard("Data Matrícula", "");
        lblEnrollmentDate = (JLabel) card4.getComponent(1);
        panel.add(card4);

        return panel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(100, 100, 100));
        card.add(lblTitle);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 16));
        lblValue.setForeground(new Color(21, 101, 192));
        card.add(lblValue);

        return card;
    }

    private JPanel createPersonalDataTab() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Nome:"));
        JTextField name = new JTextField();
        panel.add(name);

        panel.add(new JLabel("Email:"));
        JTextField email = new JTextField();
        panel.add(email);

        panel.add(new JLabel("Telemóvel:"));
        JTextField phone = new JTextField();
        panel.add(phone);

        panel.add(new JLabel("NIF:"));
        JTextField nif = new JTextField();
        panel.add(nif);

        panel.add(new JLabel("Data Nascimento:"));
        JTextField birthdate = new JTextField();
        panel.add(birthdate);
        
        panel.add(new JLabel("Nacionalidade:"));
        JTextField nationality = new JTextField();
        panel.add(nationality);
        
        panel.add(new JLabel("Morada:"));
        JTextField address = new JTextField();
        panel.add(address);
        
        panel.add(new JLabel("Avatar (Iniciais):"));
        JTextField avatar = new JTextField();
        panel.add(avatar);

        // Load data
        name.setText(student.getName());
        email.setText(student.getEmail());
        phone.setText(student.getPhone() != null ? student.getPhone() : "");
        nif.setText(student.getNif() != null ? student.getNif() : "");
        birthdate.setText(student.getBirthdate() != null ? student.getBirthdate().toString() : "");
        nationality.setText(student.getNationality() != null ? student.getNationality() : "");
        address.setText(student.getAddress() != null ? student.getAddress() : "");
        avatar.setText(student.getAvatar() != null ? student.getAvatar() : "");

        // Disable all fields for now (view-only)
        name.setEditable(false);
        email.setEditable(false);
        phone.setEditable(false);
        nif.setEditable(false);
        birthdate.setEditable(false);
        nationality.setEditable(false);
        address.setEditable(false);
        avatar.setEditable(false);

        return panel;
    }
    
    private JPanel createDocumentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Documentos section com scroll
        documentsList = new JPanel();
        documentsList.setLayout(new BoxLayout(documentsList, BoxLayout.Y_AXIS));
        documentsList.setBorder(BorderFactory.createTitledBorder("📄 Documentos do Aluno"));
        
        JScrollPane scrollPane = new JScrollPane(documentsList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Carrega inicialmente
        refreshDocumentsList();
        
        return panel;
    }

    private JPanel createProgressTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Progress bars section
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createTitledBorder("📊 Progresso Detalhado"));
        
        // Horas de Voo
        progressPanel.add(createProgressBar("Horas de Voo", 
            Math.min(100, (int)(student.getFlightHours() != null ? student.getFlightHours() * 100 / 150 : 0)),
            String.format("%.1f / 150h", student.getFlightHours() != null ? student.getFlightHours() : 0)));
        progressPanel.add(Box.createVerticalStrut(10));
        
        // Horas Teóricas
        progressPanel.add(createProgressBar("Horas Teóricas",
            Math.min(100, (int)(student.getTheoreticalHours() != null ? student.getTheoreticalHours() * 100 / 100 : 0)),
            String.format("%.1f / 100h", student.getTheoreticalHours() != null ? student.getTheoreticalHours() : 0)));
        progressPanel.add(Box.createVerticalStrut(10));
        
        // Módulos Concluídos
        int modulesProgress = (student.getProgress() != null ? student.getProgress() : 0);
        progressPanel.add(createProgressBar("Módulos Concluídos",
            modulesProgress,
            (modulesProgress / 25) + " / 4 módulos"));
        progressPanel.add(Box.createVerticalStrut(10));
        
        // Avaliações Aprovadas
        progressPanel.add(createProgressBar("Avaliações Aprovadas",
            Math.min(100, modulesProgress),
            (modulesProgress / 20) + " / 5 avaliações"));
        
        panel.add(progressPanel, BorderLayout.NORTH);
        
        // Course info
        JPanel coursePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        coursePanel.setBorder(BorderFactory.createTitledBorder("Informações do Curso"));
        coursePanel.add(new JLabel("Curso:"));
        coursePanel.add(new JLabel(student.getCourse() != null ? student.getCourse().getName() : "N/A"));

        coursePanel.add(new JLabel("Data Inscrição:"));
        coursePanel.add(new JLabel(student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : "N/A"));

        coursePanel.add(new JLabel("Estado:"));
        coursePanel.add(new JLabel(student.getStatus() != null ? student.getStatus() : "N/A"));
        
        coursePanel.add(new JLabel("Instrutor:"));
        coursePanel.add(new JLabel(student.getInstructor() != null ? student.getInstructor().getName() : "N/A"));

        panel.add(coursePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProgressBar(String label, int progress, String details) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblLabel, BorderLayout.WEST);
        
        JProgressBar progBar = new JProgressBar(0, 100);
        progBar.setValue(progress);
        progBar.setStringPainted(true);
        progBar.setString(progress + "%");
        panel.add(progBar, BorderLayout.CENTER);
        
        JLabel lblDetails = new JLabel(details);
        lblDetails.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDetails.setForeground(new Color(100, 100, 100));
        panel.add(lblDetails, BorderLayout.EAST);
        
        return panel;
    }

    private JPanel createFlightsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Data", "Hora", "Instructor", "Tipo", "Duração", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Load flights
        List<Flight> flights = flightController.listarVoos();
        for (Flight flight : flights) {
            if (flight.getStudent() != null &&
                    flight.getStudent().getId() != null &&
                    flight.getStudent().getId().equals(student.getId())) {
                Object[] row = {
                    flight.getId(),
                    flight.getFlightDate(),
                    flight.getFlightTime(),
                    flight.getInstructor() != null ? flight.getInstructor().getName() : "N/A",
                    flight.getFlightType(),
                    flight.getDuration(),
                    flight.getStatus()
                };
                tableModel.addRow(row);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEvaluationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Data", "Tipo", "Nota", "Feedback"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Load evaluations
        List<Evaluation> evaluations = evaluationController.listarAvaliacoes();
        for (Evaluation eval : evaluations) {
            if (eval.getStudent() != null &&
                    eval.getStudent().getId() != null &&
                    eval.getStudent().getId().equals(student.getId())) {
                Object[] row = {
                    eval.getId(),
                    eval.getEvaluationDate(),
                    eval.getEvaluationType(),
                    eval.getScore(),
                    eval.getNotes() != null ? eval.getNotes() : ""
                };
                tableModel.addRow(row);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("💳 Resumo de Pagamentos"));
        
        // Total pago
        JPanel card1 = createPaymentCard("Total Pago", "€ 4.500,00", new Color(220, 252, 231));
        summaryPanel.add(card1);
        
        // Pendente
        JPanel card2 = createPaymentCard("Pendente", "€ 1.200,00", new Color(254, 243, 199));
        summaryPanel.add(card2);
        
        // Em atraso
        JPanel card3 = createPaymentCard("Em Atraso", "€ 450,00", new Color(254, 226, 226));
        summaryPanel.add(card3);
        
        panel.add(summaryPanel, BorderLayout.NORTH);

        // Payment history table
        String[] columns = {"ID", "Data", "Conceito", "Valor", "Status", "Método"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Sample payment data
        Object[][] sampleData = {
            {1, "2024-01-15", "Tuition", "€1.500,00", "Completed", "Credit Card"},
            {2, "2024-02-20", "Flight Hours", "€800,00", "Completed", "Bank Transfer"},
            {3, "2024-03-10", "Exam Fee", "€200,00", "Pending", "Check"},
            {4, "2024-04-05", "Installation", "€2.000,00", "Overdue", "Cash"}
        };
        
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createPaymentCard(String title, String amount, Color bgColor) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        card.setBackground(bgColor);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitle.setForeground(new Color(60, 60, 60));
        card.add(lblTitle);
        
        JLabel lblAmount = new JLabel(amount);
        lblAmount.setFont(new Font("Arial", Font.BOLD, 18));
        lblAmount.setForeground(new Color(21, 101, 192));
        card.add(lblAmount);
        
        return card;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnClose = new JButton("Fechar");
        btnClose.addActionListener(e -> dispose());
        panel.add(btnClose);

        return panel;
    }

    private void loadStudentData() {
        lblStudentName.setText(student.getName());
        lblStudentStatus.setText(student.getStatus() != null ? student.getStatus() : "Ativo");
        lblStudentEmail.setText(student.getEmail());
        lblFlightHours.setText((student.getFlightHours() != null ? student.getFlightHours() : 0) + "h");
        lblProgress.setText((student.getProgress() != null ? student.getProgress() : 0) + "%");
        lblTheoreticalHours.setText("45h");  // Placeholder
        lblEnrollmentDate.setText(student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : "N/A");
    }

    private void editProfile() {
        JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(student.getName().replace(" ", "_") + "_Ficha.pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            generatePDF(selectedFile);
        }
    }
    
    private void generatePDF(File outputFile) {
        try {
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(
                new com.itextpdf.kernel.pdf.PdfWriter(outputFile));
            com.itextpdf.layout.Document doc = new com.itextpdf.layout.Document(pdfDoc);
            
            // Título
            com.itextpdf.layout.element.Paragraph title = new com.itextpdf.layout.element.Paragraph("FICHA DO ESTUDANTE")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
            doc.add(title);
            doc.add(new com.itextpdf.layout.element.Paragraph(""));
            
            // Dados Pessoais
            doc.add(new com.itextpdf.layout.element.Paragraph("DADOS PESSOAIS")
                .setBold()
                .setFontSize(14));
            
            com.itextpdf.layout.element.Table table1 = new com.itextpdf.layout.element.Table(2);
            table1.addCell("Nome:");
            table1.addCell(student.getName());
            table1.addCell("Email:");
            table1.addCell(student.getEmail());
            table1.addCell("Telemóvel:");
            table1.addCell(student.getPhone() != null ? student.getPhone() : "N/A");
            table1.addCell("NIF:");
            table1.addCell(student.getNif() != null ? student.getNif() : "N/A");
            table1.addCell("Data de Nascimento:");
            table1.addCell(student.getBirthdate() != null ? student.getBirthdate().toString() : "N/A");
            table1.addCell("Nacionalidade:");
            table1.addCell(student.getNationality() != null ? student.getNationality() : "N/A");
            table1.addCell("Morada:");
            table1.addCell(student.getAddress() != null ? student.getAddress() : "N/A");
            doc.add(table1);
            doc.add(new com.itextpdf.layout.element.Paragraph(""));
            
            // Informações do Curso
            doc.add(new com.itextpdf.layout.element.Paragraph("INFORMAÇÕES DO CURSO")
                .setBold()
                .setFontSize(14));
            
            com.itextpdf.layout.element.Table table2 = new com.itextpdf.layout.element.Table(2);
            table2.addCell("Curso:");
            table2.addCell(student.getCourse() != null ? student.getCourse().getName() : "N/A");
            table2.addCell("Estado:");
            table2.addCell(student.getStatus());
            table2.addCell("Data de Inscrição:");
            table2.addCell(student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : "N/A");
            table2.addCell("Progresso:");
            table2.addCell((student.getProgress() != null ? student.getProgress() : 0) + "%");
            table2.addCell("Horas de Voo:");
            table2.addCell((student.getFlightHours() != null ? student.getFlightHours() : 0) + "h");
            table2.addCell("Horas Teóricas:");
            table2.addCell((student.getTheoreticalHours() != null ? student.getTheoreticalHours() : 0) + "h");
            doc.add(table2);
            doc.add(new com.itextpdf.layout.element.Paragraph(""));
            
            // Documentos
            if (!uploadedDocuments.isEmpty()) {
                doc.add(new com.itextpdf.layout.element.Paragraph("DOCUMENTOS")
                    .setBold()
                    .setFontSize(14));
                
                com.itextpdf.layout.element.Table table3 = new com.itextpdf.layout.element.Table(1);
                for (File doc_file : uploadedDocuments) {
                    table3.addCell(doc_file.getName());
                }
                doc.add(table3);
                doc.add(new com.itextpdf.layout.element.Paragraph(""));
            }
            
            // Gerado em
            doc.add(new com.itextpdf.layout.element.Paragraph(
                "Gerado em: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            
            doc.close();
            
            JOptionPane.showMessageDialog(this, 
                "Ficha exportada com sucesso para:\n" + outputFile.getAbsolutePath(), 
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir arquivo
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(outputFile);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao exportar PDF: " + e.getMessage(), 
                "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // ===== Métodos para gerenciar documentos =====
    
    private void initializeDocumentDirectory() {
        Path docDir = Paths.get(DOCUMENTS_DIR, "student_" + student.getId());
        try {
            Files.createDirectories(docDir);
        } catch (IOException e) {
            System.err.println("Erro ao criar pasta de documentos: " + e.getMessage());
        }
    }
    
    private void loadUploadedDocuments() {
        uploadedDocuments.clear();
        Path docDir = Paths.get(DOCUMENTS_DIR, "student_" + student.getId());
        try {
            if (Files.exists(docDir)) {
                Files.list(docDir)
                    .filter(Files::isRegularFile)
                    .forEach(path -> uploadedDocuments.add(path.toFile()));
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar documentos: " + e.getMessage());
        }
    }
    
    private void uploadDocument() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "PDF e Imagens (*.pdf, *.jpg, *.png)", "pdf", "jpg", "jpeg", "png"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Path destDir = Paths.get(DOCUMENTS_DIR, "student_" + student.getId());
                Files.createDirectories(destDir);
                
                Path destPath = destDir.resolve(selectedFile.getName());
                Files.copy(selectedFile.toPath(), destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                uploadedDocuments.add(destPath.toFile());
                refreshDocumentsList();
                
                JOptionPane.showMessageDialog(this, "Documento uploadado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao fazer upload: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void downloadDocument(File document) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(document.getName()));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File destFile = fileChooser.getSelectedFile();
            try {
                Files.copy(document.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "Documento guardado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
                // Abrir com aplicação padrão
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(destFile);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao guardar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteDocument(File document) {
        int confirm = JOptionPane.showConfirmDialog(this, "Tem a certeza que quer eliminar este documento?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Files.delete(document.toPath());
                uploadedDocuments.remove(document);
                refreshDocumentsList();
                JOptionPane.showMessageDialog(this, "Documento eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao eliminar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshDocumentsList() {
        documentsList.removeAll();
        
        // Botão de upload
        JButton btnUpload = new JButton("➕ Upload Novo Documento");
        btnUpload.setBackground(new Color(220, 252, 231));
        btnUpload.setForeground(new Color(22, 163, 74));
        btnUpload.addActionListener(e -> uploadDocument());
        documentsList.add(btnUpload);
        
        // Listar documentos
        if (uploadedDocuments.isEmpty()) {
            JLabel lblEmpty = new JLabel("Nenhum documento carregado");
            lblEmpty.setForeground(new Color(100, 100, 100));
            documentsList.add(lblEmpty);
        } else {
            for (File doc : uploadedDocuments) {
                JPanel docPanel = new JPanel(new BorderLayout(10, 0));
                docPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
                docPanel.setBackground(Color.WHITE);
                docPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JLabel lblDoc = new JLabel("📄 " + doc.getName());
                lblDoc.setFont(new Font("Arial", Font.BOLD, 12));
                docPanel.add(lblDoc, BorderLayout.WEST);
                
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                btnPanel.setOpaque(false);
                
                JButton btnDownload = new JButton("⬇ Download");
                btnDownload.addActionListener(e -> downloadDocument(doc));
                btnPanel.add(btnDownload);
                
                JButton btnDelete = new JButton("🗑 Eliminar");
                btnDelete.addActionListener(e -> deleteDocument(doc));
                btnPanel.add(btnDelete);
                
                docPanel.add(btnPanel, BorderLayout.EAST);
                documentsList.add(docPanel);
            }
        }
        
        documentsList.revalidate();
        documentsList.repaint();
    }
}
