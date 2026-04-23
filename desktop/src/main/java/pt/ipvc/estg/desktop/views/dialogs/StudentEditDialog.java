package pt.ipvc.estg.desktop.views.dialogs;

import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.entities.Student;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog para editar dados de um estudante
 */
public class StudentEditDialog extends JDialog {
    private final StudentController studentController;
    private final Student student;
    private boolean saved = false;

    // UI Components
    private JTextField textName;
    private JTextField textEmail;
    private JTextField textPhone;
    private JTextField textNIF;
    private JTextField textBirthdate;
    private JTextField textNationality;
    private JTextField textAddress;
    private JComboBox<String> comboStatus;

    public StudentEditDialog(JFrame parent, Student student) {
        super(parent, "Editar Estudante: " + student.getName(), true);
        this.student = student;
        this.studentController = new StudentController();
        
        initializeUI();
        loadStudentData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Nome
        panel.add(new JLabel("Nome:"));
        textName = new JTextField();
        panel.add(textName);

        // Email
        panel.add(new JLabel("Email:"));
        textEmail = new JTextField();
        panel.add(textEmail);

        // Telemóvel
        panel.add(new JLabel("Telemóvel:"));
        textPhone = new JTextField();
        panel.add(textPhone);

        // NIF
        panel.add(new JLabel("NIF:"));
        textNIF = new JTextField();
        panel.add(textNIF);

        // Data Nascimento (formato: yyyy-MM-dd)
        panel.add(new JLabel("Data Nascimento (yyyy-MM-dd):"));
        textBirthdate = new JTextField();
        panel.add(textBirthdate);

        // Nacionalidade
        panel.add(new JLabel("Nacionalidade:"));
        textNationality = new JTextField();
        panel.add(textNationality);

        // Morada
        panel.add(new JLabel("Morada:"));
        textAddress = new JTextField();
        panel.add(textAddress);

        // Status
        panel.add(new JLabel("Estado:"));
        String[] statuses = {"active", "suspended", "completed"};
        comboStatus = new JComboBox<>(statuses);
        panel.add(comboStatus);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        JButton btnSave = new JButton("💾 Guardar");
        btnSave.addActionListener(e -> saveChanges());
        panel.add(btnSave);
        
        JButton btnCancel = new JButton("✕ Cancelar");
        btnCancel.addActionListener(e -> dispose());
        panel.add(btnCancel);
        
        return panel;
    }

    private void loadStudentData() {
        textName.setText(student.getName());
        textEmail.setText(student.getEmail());
        textPhone.setText(student.getPhone() != null ? student.getPhone() : "");
        textNIF.setText(student.getNif() != null ? student.getNif() : "");
        textBirthdate.setText(student.getBirthdate() != null ? student.getBirthdate().toString() : "");
        textNationality.setText(student.getNationality() != null ? student.getNationality() : "");
        textAddress.setText(student.getAddress() != null ? student.getAddress() : "");
        comboStatus.setSelectedItem(student.getStatus());
    }

    private void saveChanges() {
        try {
            String name = textName.getText().trim();
            String email = textEmail.getText().trim();
            String phone = textPhone.getText().trim();
            String nif = textNIF.getText().trim();
            String birthdate = textBirthdate.getText().trim();
            String nationality = textNationality.getText().trim();
            String address = textAddress.getText().trim();
            String status = (String) comboStatus.getSelectedItem();

            // Validações básicas
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse birthdate
            LocalDate parsedBirthdate = null;
            if (!birthdate.isEmpty()) {
                try {
                    parsedBirthdate = LocalDate.parse(birthdate, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Data inválida! Use formato yyyy-MM-dd", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Atualizar estudante
            studentController.atualizarEstudante(student.getId(), name, email, phone, nif, parsedBirthdate);
            
            // Atualizar campos adicionais
            student.setNationality(nationality.isEmpty() ? null : nationality);
            student.setAddress(address.isEmpty() ? null : address);
            student.setStatus(status);

            saved = true;
            JOptionPane.showMessageDialog(this, "Estudante atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao guardar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
