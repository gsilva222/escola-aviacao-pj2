package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Documentos do Aluno (FrontOffice)
 * Mostra documentos armazenados do aluno
 */
public class FODocuments extends JPanel {

    private final Student student;

    public FODocuments(Student student) {
        this.student = student;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new java.awt.BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new java.awt.Color(245, 245, 245));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Título da seção
        JLabel titleLabel = new JLabel("Meus Documentos");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
        titleLabel.setForeground(new java.awt.Color(15, 35, 68));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Listar documentos da pasta do aluno
        String docPath = "documentos/student_" + student.getId();
        File docFolder = new File(docPath);
        
        if (docFolder.exists() && docFolder.isDirectory()) {
            File[] files = docFolder.listFiles();
            if (files != null && files.length > 0) {
                JPanel documentsPanel = new JPanel();
                documentsPanel.setLayout(new java.awt.GridLayout(0, 2, 15, 15));
                documentsPanel.setOpaque(false);
                
                for (File file : files) {
                    if (!file.isHidden()) {
                        JPanel docCard = createDocumentCard(file.getName(), file);
                        documentsPanel.add(docCard);
                    }
                }
                mainPanel.add(documentsPanel);
            } else {
                JLabel emptyLabel = new JLabel("Nenhum documento adicionado ainda");
                emptyLabel.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
                emptyLabel.setForeground(new java.awt.Color(150, 150, 150));
                mainPanel.add(emptyLabel);
            }
        } else {
            JLabel emptyLabel = new JLabel("Nenhum documento adicionado ainda");
            emptyLabel.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
            emptyLabel.setForeground(new java.awt.Color(150, 150, 150));
            mainPanel.add(emptyLabel);
        }

        mainPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getViewport().setBackground(new java.awt.Color(245, 245, 245));
        add(scrollPane, java.awt.BorderLayout.CENTER);

        // Botão para baixar documento de exemplo
        JPanel bottomPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false);
        JButton downloadBtn = new JButton("⬇️ Ver Ficha do Aluno em PDF");
        downloadBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Funcionalidade em desenvolvimento\n\nPDF da ficha será disponibilizado em breve"));
        bottomPanel.add(downloadBtn);
        
        add(bottomPanel, java.awt.BorderLayout.SOUTH);
    }

    private JPanel createDocumentCard(String filename, File file) {
        JPanel card = new JPanel();
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY, 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Ícone baseado em tipo de ficheiro
        String icon = "📄";
        if (filename.endsWith(".pdf")) icon = "📕";
        else if (filename.endsWith(".jpg") || filename.endsWith(".png")) icon = "🖼️";
        else if (filename.endsWith(".zip")) icon = "📦";

        JLabel titleLabel = new JLabel(icon + " " + filename);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        titleLabel.setForeground(new java.awt.Color(15, 35, 68));
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(10));

        JLabel sizeLabel = new JLabel("Tamanho: " + (file.length() / 1024) + " KB");
        sizeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        sizeLabel.setForeground(new java.awt.Color(100, 100, 100));
        card.add(sizeLabel);

        JLabel dateLabel = new JLabel("Adicionado: " + new java.text.SimpleDateFormat("dd/MM/yyyy")
                .format(file.lastModified()));
        dateLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        dateLabel.setForeground(new java.awt.Color(100, 100, 100));
        card.add(dateLabel);

        card.add(Box.createVerticalGlue());

        JButton downloadBtn = new JButton("⬇️ Download");
        downloadBtn.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        downloadBtn.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        downloadBtn.setBackground(new java.awt.Color(21, 101, 192));
        downloadBtn.setForeground(java.awt.Color.WHITE);
        downloadBtn.setFocusPainted(false);
        downloadBtn.addActionListener(e -> downloadFile(file));
        card.add(downloadBtn);

        return card;
    }

    private void downloadFile(File file) {
        try {
            java.awt.Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir ficheiro: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
