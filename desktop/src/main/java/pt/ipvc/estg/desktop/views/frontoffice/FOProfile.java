package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.desktop.controllers.StudentController;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Perfil do Aluno (FrontOffice)
 * Mostra dados pessoais do aluno autenticado
 */
public class FOProfile extends JPanel {

    private final Student student;
    private final StudentController studentController;

    public FOProfile(Student student) {
        this.student = student;
        this.studentController = new StudentController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBackground(java.awt.Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados Pessoais"));

        addFormField(formPanel, "Nome:", student.getName() != null ? student.getName() : "N/A");
        addFormField(formPanel, "Email:", student.getEmail() != null ? student.getEmail() : "N/A");
        addFormField(formPanel, "Telemóvel:", student.getPhone() != null ? student.getPhone() : "N/A");
        addFormField(formPanel, "NIF:", student.getNif() != null ? student.getNif() : "N/A");
        
        String birthDate = student.getBirthdate() != null ? 
                student.getBirthdate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
        addFormField(formPanel, "Data Nascimento:", birthDate);
        
        addFormField(formPanel, "Morada:", student.getAddress() != null ? student.getAddress() : "N/A");
        addFormField(formPanel, "Curso:", student.getCourse() != null ? student.getCourse().getName() : "N/A");
        addFormField(formPanel, "Status:", student.getStatus() != null ? student.getStatus() : "N/A");

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        JButton editBtn = new JButton("✎ Editar Perfil");
        editBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Funcionalidade em desenvolvimento\n\nOs dados do perfil são gerenciados pelo administrador"));
        buttonPanel.add(editBtn);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getViewport().setBackground(new java.awt.Color(245, 245, 245));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        panel.add(lblLabel);

        JTextField txtValue = new JTextField(value);
        txtValue.setEditable(false);
        panel.add(txtValue);
    }
}
