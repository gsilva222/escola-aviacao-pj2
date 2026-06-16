package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.dto.UserAccountResponse;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.desktop.controllers.UserController;
import pt.ipvc.estg.desktop.security.RoleMenuPolicy;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestão de utilizadores (contas de acesso) no BackOffice desktop.
 * Espelha a página web (/bo/users) usando os mesmos endpoints da API.
 */
public class BOUsers extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color TITLE = new Color(15, 35, 68);
    private static final Color BLUE = new Color(21, 101, 192);

    private static final String[] STAFF_PROFILES = {
            "Administrador", "Secretaria", "Gestor Operacional", "Instrutor", "Técnico de Manutenção"
    };

    private final UserController userController = new UserController();
    private final StudentController studentController = new StudentController();

    private JLabel statsLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<UserAccountResponse> users = new ArrayList<>();

    public BOUsers() {
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 14));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        statsLabel = new JLabel("A carregar...");
        statsLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        statsLabel.setForeground(MUTED);
        panel.add(statsLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton refreshBtn = new JButton("Atualizar");
        styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadData());
        actions.add(refreshBtn);

        JButton toggleBtn = new JButton("Ativar / Desativar");
        styleSecondaryButton(toggleBtn);
        toggleBtn.addActionListener(e -> toggleSelectedUserActive());
        actions.add(toggleBtn);

        JButton newBtn = new JButton("Novo Utilizador");
        stylePrimaryButton(newBtn);
        newBtn.addActionListener(e -> criarUtilizadorDialog());
        actions.add(newBtn);

        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        String[] columns = {"Utilizador", "Tipo", "Perfil / Aluno", "Permissões", "Estado"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(48);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TITLE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBorder(null);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 11));

        table.getColumnModel().getColumn(1).setCellRenderer(new RoleBadgeRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new PermissionsRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(3).setPreferredWidth(280);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0 && row < users.size()) {
                        gerirUtilizadorDialog(users.get(row));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        if (!userController.isAvailable()) {
            users = new ArrayList<>();
            tableModel.setRowCount(0);
            statsLabel.setText("Gestão de utilizadores indisponível em modo offline.");
            return;
        }
        try {
            users = userController.listarUtilizadores();
            displayUsers();
            long admins = users.stream().filter(u -> isAdmin(u.role())).count();
            statsLabel.setText(users.size() + " utilizadores · " + admins + " administradores");
        } catch (ApiException ex) {
            tableModel.setRowCount(0);
            statsLabel.setText("Erro ao carregar utilizadores.");
            JOptionPane.showMessageDialog(this, "Erro ao carregar utilizadores: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayUsers() {
        tableModel.setRowCount(0);
        for (UserAccountResponse u : users) {
            String profile = isAdmin(u.role())
                    ? (u.staffProfile() != null ? u.staffProfile() : "Administrador")
                    : null;
            String detail = profile != null
                    ? profile
                    : (u.studentName() != null ? u.studentName() : "—");
            String permissions = profile != null
                    ? RoleMenuPolicy.permissionsSummary(profile)
                    : "Área do aluno";
            tableModel.addRow(new Object[]{
                    u.username(),
                    u.role() != null ? u.role().toUpperCase() : "—",
                    detail,
                    permissions,
                    u.active() ? "active" : "inactive"
            });
        }
    }

    private void criarUtilizadorDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMIN", "STUDENT"});
        JComboBox<String> staffCombo = new JComboBox<>(STAFF_PROFILES);

        List<Student> students = studentController.listarEstudantes();
        JComboBox<StudentChoice> studentCombo = new JComboBox<>();
        for (Student s : students) {
            studentCombo.addItem(new StudentChoice(s));
        }

        JLabel staffLabel = new JLabel("Perfil de staff");
        JLabel studentLabel = new JLabel("Aluno associado");
        JLabel permissionsHint = new JLabel(" ");
        permissionsHint.setFont(new Font("Inter", Font.PLAIN, 10));
        permissionsHint.setForeground(MUTED);

        Runnable refreshVisibility = () -> {
            boolean admin = "ADMIN".equals(roleCombo.getSelectedItem());
            staffLabel.setVisible(admin);
            staffCombo.setVisible(admin);
            permissionsHint.setVisible(admin);
            studentLabel.setVisible(!admin);
            studentCombo.setVisible(!admin);
            if (admin) {
                String profile = (String) staffCombo.getSelectedItem();
                permissionsHint.setText("Acesso: " + RoleMenuPolicy.permissionsSummary(profile));
            }
        };
        roleCombo.addActionListener(e -> refreshVisibility.run());
        staffCombo.addActionListener(e -> refreshVisibility.run());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Nome de utilizador"));
        form.add(usernameField);
        form.add(new JLabel("Palavra-passe (mín. 6 caracteres)"));
        form.add(passwordField);
        form.add(new JLabel("Tipo de conta"));
        form.add(roleCombo);
        form.add(staffLabel);
        form.add(staffCombo);
        form.add(permissionsHint);
        form.add(studentLabel);
        form.add(studentCombo);
        refreshVisibility.run();

        int result = JOptionPane.showConfirmDialog(this, form, "Novo Utilizador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String username = usernameField.getText() != null ? usernameField.getText().trim() : "";
        String password = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.isBlank()) {
            JOptionPane.showMessageDialog(this, "O nome de utilizador é obrigatório.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "A palavra-passe deve ter pelo menos 6 caracteres.", "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer studentId = null;
        String staffProfile = null;
        if ("STUDENT".equals(role)) {
            StudentChoice choice = (StudentChoice) studentCombo.getSelectedItem();
            if (choice == null) {
                JOptionPane.showMessageDialog(this, "Selecione o aluno a associar à conta.", "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }
            studentId = choice.student.getId();
        } else {
            staffProfile = (String) staffCombo.getSelectedItem();
        }

        try {
            userController.criarUtilizador(username, password, role, studentId, staffProfile);
            loadData();
            JOptionPane.showMessageDialog(this, "Utilizador criado com sucesso.");
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar utilizador: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleSelectedUserActive() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= users.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um utilizador na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UserAccountResponse user = users.get(row);
        boolean newActive = !user.active();
        String action = newActive ? "ativar" : "desativar";
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja " + action + " o utilizador \"" + user.username() + "\"?\n"
                        + (newActive ? "" : "Uma conta desativada deixa de poder iniciar sessão."),
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            userController.definirAtivo(user.id(), newActive);
            loadData();
            JOptionPane.showMessageDialog(this, "Utilizador " + (newActive ? "ativado" : "desativado") + " com sucesso.");
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar utilizador: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerirUtilizadorDialog(UserAccountResponse user) {
        boolean admin = isAdmin(user.role());

        JCheckBox activeCheck = new JCheckBox("Conta ativa", user.active());
        JComboBox<String> staffCombo = new JComboBox<>(STAFF_PROFILES);
        if (user.staffProfile() != null) {
            staffCombo.setSelectedItem(user.staffProfile());
        }
        JLabel permissionsHint = new JLabel("Acesso: " + RoleMenuPolicy.permissionsSummary(
                user.staffProfile() != null ? user.staffProfile() : "Administrador"));
        permissionsHint.setFont(new Font("Inter", Font.PLAIN, 10));
        permissionsHint.setForeground(MUTED);
        staffCombo.addActionListener(e -> permissionsHint.setText("Acesso: "
                + RoleMenuPolicy.permissionsSummary((String) staffCombo.getSelectedItem())));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Utilizador: " + user.username()));
        form.add(new JLabel("Tipo: " + (user.role() != null ? user.role().toUpperCase() : "—")));
        form.add(activeCheck);
        if (admin) {
            form.add(new JLabel("Perfil de staff"));
            form.add(staffCombo);
            form.add(permissionsHint);
        }

        int result = JOptionPane.showConfirmDialog(this, form, "Gerir Utilizador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            boolean changed = false;
            if (activeCheck.isSelected() != user.active()) {
                userController.definirAtivo(user.id(), activeCheck.isSelected());
                changed = true;
            }
            if (admin) {
                String selectedProfile = (String) staffCombo.getSelectedItem();
                if (selectedProfile != null && !selectedProfile.equals(user.staffProfile())) {
                    userController.alterarPerfil(user.id(), selectedProfile);
                    changed = true;
                }
            }
            if (changed) {
                loadData();
                JOptionPane.showMessageDialog(this, "Utilizador atualizado com sucesso.");
            }
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar utilizador: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static boolean isAdmin(String role) {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 12));
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 14, 8, 14)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static class StudentChoice {
        private final Student student;

        StudentChoice(Student student) {
            this.student = student;
        }

        @Override
        public String toString() {
            return student.getName() + (student.getEmail() != null ? " (" + student.getEmail() + ")" : "");
        }
    }

    private static class PermissionsRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel label) {
                label.setFont(new Font("Inter", Font.PLAIN, 11));
                label.setForeground(isSelected ? table.getSelectionForeground() : MUTED);
                String text = value != null ? String.valueOf(value) : "";
                label.setToolTipText(text.isBlank() ? null : text);
            }
            return c;
        }
    }

    private static class RoleBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            String role = String.valueOf(value);
            if ("ADMIN".equalsIgnoreCase(role)) {
                label.setText("Administração");
                label.setBackground(new Color(219, 234, 254));
                label.setForeground(new Color(29, 78, 216));
            } else {
                label.setText("Aluno");
                label.setBackground(new Color(241, 245, 249));
                label.setForeground(new Color(100, 116, 139));
            }
            return label;
        }
    }

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            if ("active".equals(String.valueOf(value))) {
                label.setText("Ativo");
                label.setBackground(new Color(220, 252, 231));
                label.setForeground(new Color(22, 163, 74));
            } else {
                label.setText("Inativo");
                label.setBackground(new Color(254, 226, 226));
                label.setForeground(new Color(220, 38, 38));
            }
            return label;
        }
    }
}
