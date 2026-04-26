package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.CourseController;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BOStudents extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);      // #EEF2F7
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);       // #E2E8F0
    private static final Color MUTED = new Color(100, 116, 139);        // #64748B
    private static final Color TITLE = new Color(15, 35, 68);           // #0F2344
    private static final Color BLUE = new Color(21, 101, 192);          // #1565C0
    private static final Color BLUE_DARK = new Color(13, 71, 161);

    private final StudentController studentController;
    private final CourseController courseController;

    private JTextField searchField;
    private JComboBox<String> courseFilter;
    private JComboBox<String> statusFilter;
    private JLabel statsLabel;
    private JLabel pageInfoLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JTable table;
    private DefaultTableModel tableModel;

    private List<Student> filteredStudents = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 10;

    public BOStudents() {
        this.studentController = new StudentController();
        this.courseController = new CourseController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 14));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createHeaderPanel());
        top.add(Box.createVerticalStrut(12));
        top.add(createFiltersPanel());
        add(top, BorderLayout.NORTH);

        add(createTablePanel(), BorderLayout.CENTER);
        add(createPaginationPanel(), BorderLayout.SOUTH);
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

        JButton exportBtn = new JButton("Exportar");
        styleSecondaryButton(exportBtn);
        exportBtn.addActionListener(e -> exportarPDF());
        actions.add(exportBtn);

        JButton newBtn = new JButton("Novo Aluno");
        stylePrimaryButton(newBtn);
        newBtn.addActionListener(e -> criarEstudanteDialog());
        actions.add(newBtn);

        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel createFiltersPanel() {
        JPanel card = createCardPanel();
        card.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1;

        searchField = new JTextField();
        searchField.setFont(new Font("Inter", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        card.add(searchField, gbc);

        courseFilter = new JComboBox<>();
        styleCombo(courseFilter);
        courseFilter.addItem("Todos os Cursos");
        courseFilter.addActionListener(e -> applyFilters());

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        card.add(courseFilter, gbc);

        statusFilter = new JComboBox<>(new String[]{"Todos os Estados", "active", "suspended", "completed"});
        styleCombo(statusFilter);
        statusFilter.addActionListener(e -> applyFilters());

        gbc.gridx = 2;
        card.add(statusFilter, gbc);

        JButton moreFilters = new JButton("Mais filtros");
        styleSecondaryButton(moreFilters);
        moreFilters.addActionListener(e -> JOptionPane.showMessageDialog(this, "Filtros avançados em desenvolvimento."));

        gbc.gridx = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(moreFilters, gbc);

        populateCourseFilter();
        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout());

        String[] columns = {"Aluno", "Curso", "Instrutor", "Progresso", "Horas Voo", "Pagamentos", "Estado", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(62);
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

        table.getColumnModel().getColumn(0).setCellRenderer(new StudentRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new ProgressRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new PaymentBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ArrowRenderer());
        table.getColumnModel().getColumn(7).setPreferredWidth(26);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(0).setPreferredWidth(240);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Student student = (Student) tableModel.getValueAt(row, 0);
                        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(BOStudents.this);
                        new BOStudentFile(parent, student.getId()).setVisible(true);
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

    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        pageInfoLabel = new JLabel("A mostrar 0 de 0");
        pageInfoLabel.setForeground(new Color(148, 163, 184));
        pageInfoLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        panel.add(pageInfoLabel, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        controls.setOpaque(false);

        prevButton = new JButton("<");
        stylePaginationButton(prevButton);
        prevButton.addActionListener(e -> previousPage());
        controls.add(prevButton);

        nextButton = new JButton(">");
        stylePaginationButton(nextButton);
        nextButton.addActionListener(e -> nextPage());
        controls.add(nextButton);

        panel.add(controls, BorderLayout.EAST);
        return panel;
    }

    private void populateCourseFilter() {
        List<Course> courses = courseController.listarCursos();
        for (Course course : courses) {
            courseFilter.addItem(course.getName());
        }
    }

    private void loadData() {
        filteredStudents = studentController.listarEstudantes();
        currentPage = 0;
        updateStats();
        displayPage();
    }

    private void applyFilters() {
        currentPage = 0;
        String search = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedCourse = (String) courseFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        filteredStudents = studentController.listarEstudantes().stream()
                .filter(s -> {
                    boolean matchSearch = search.isEmpty()
                            || s.getName().toLowerCase(Locale.ROOT).contains(search)
                            || s.getEmail().toLowerCase(Locale.ROOT).contains(search);
                    boolean matchCourse = "Todos os Cursos".equals(selectedCourse)
                            || (s.getCourse() != null && s.getCourse().getName().equals(selectedCourse));
                    boolean matchStatus = "Todos os Estados".equals(selectedStatus)
                            || selectedStatus.equals(s.getStatus());
                    return matchSearch && matchCourse && matchStatus;
                })
                .collect(Collectors.toList());

        updateStats();
        displayPage();
    }

    private void updateStats() {
        List<Student> all = studentController.listarEstudantes();
        long active = all.stream().filter(s -> "active".equals(s.getStatus())).count();
        statsLabel.setText(all.size() + " alunos registados · " + active + " ativos");
    }

    private void displayPage() {
        tableModel.setRowCount(0);

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, filteredStudents.size());
        if (start < filteredStudents.size()) {
            for (int i = start; i < end; i++) {
                Student s = filteredStudents.get(i);
                tableModel.addRow(new Object[]{
                        s,
                        s.getCourse() != null ? s.getCourse().getName() : "N/A",
                        s.getInstructor() != null ? s.getInstructor().getName() : "N/A",
                        s.getProgress() != null ? s.getProgress() : 0,
                        String.format(Locale.ROOT, "%.1fh", s.getFlightHours() != null ? s.getFlightHours() : 0.0),
                        s.getPaymentStatus() != null ? s.getPaymentStatus() : "pending",
                        s.getStatus() != null ? s.getStatus() : "inactive",
                        ">"
                });
            }
        }

        int total = filteredStudents.size();
        int totalPages = (int) Math.ceil((double) Math.max(total, 1) / pageSize);
        pageInfoLabel.setText("A mostrar " + (end - start) + " de " + total + " alunos · Pagina "
                + (currentPage + 1) + " de " + totalPages);
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayPage();
        }
    }

    private void nextPage() {
        int totalPages = (int) Math.ceil((double) Math.max(filteredStudents.size(), 1) / pageSize);
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayPage();
        }
    }

    private void criarEstudanteDialog() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> courseCombo = new JComboBox<>();
        List<Course> courses = courseController.listarCursos();
        for (Course c : courses) {
            courseCombo.addItem(c.getName());
        }

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Nome"));
        form.add(nameField);
        form.add(new JLabel("Email"));
        form.add(emailField);
        form.add(new JLabel("Curso"));
        form.add(courseCombo);

        int result = JOptionPane.showConfirmDialog(this, form, "Novo Aluno", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                int idx = courseCombo.getSelectedIndex();
                if (idx < 0 || idx >= courses.size()) {
                    throw new IllegalArgumentException("Curso invalido.");
                }
                studentController.criarEstudante(name, email, courses.get(idx));
                loadData();
                JOptionPane.showMessageDialog(this, "Aluno criado com sucesso.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao criar aluno: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, "Exportacao em desenvolvimento.");
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return panel;
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

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void stylePaginationButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 12));
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(5, 9, 5, 9)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Inter", Font.PLAIN, 12));
        combo.setBackground(WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
    }

    private static class StudentRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Student s = (Student) value;

            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : WHITE);
            panel.setBorder(new EmptyBorder(6, 8, 6, 8));

            AvatarCircle avatar = new AvatarCircle(getInitials(s.getName()), getAvatarColor(s.getName()));
            panel.add(avatar, BorderLayout.WEST);

            JPanel text = new JPanel();
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.setOpaque(false);

            JLabel name = new JLabel(s.getName());
            name.setForeground(TITLE);
            name.setFont(new Font("Inter", Font.BOLD, 12));

            JLabel email = new JLabel(s.getEmail());
            email.setForeground(new Color(148, 163, 184));
            email.setFont(new Font("Inter", Font.PLAIN, 11));

            text.add(name);
            text.add(email);
            panel.add(text, BorderLayout.CENTER);

            return panel;
        }

        private String getInitials(String name) {
            if (name == null || name.isBlank()) {
                return "AL";
            }
            String[] parts = name.trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
            }
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }

        private Color getAvatarColor(String name) {
            Color[] palette = {
                    new Color(21, 101, 192),
                    new Color(37, 99, 235),
                    new Color(29, 78, 216),
                    new Color(14, 116, 144),
                    new Color(79, 70, 229),
                    new Color(124, 58, 237),
                    new Color(190, 24, 93),
                    new Color(225, 29, 72),
                    new Color(217, 119, 6),
                    new Color(202, 138, 4),
                    new Color(22, 163, 74),
                    new Color(5, 150, 105)
            };
            int idx = Math.abs((name == null ? 0 : name.hashCode())) % palette.length;
            return palette[idx];
        }
    }

    private static class ProgressRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int progress = value instanceof Number ? ((Number) value).intValue() : 0;

            JPanel panel = new JPanel(new BorderLayout(8, 0));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : WHITE);
            panel.setBorder(new EmptyBorder(6, 6, 6, 6));

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(Math.max(0, Math.min(100, progress)));
            bar.setStringPainted(false);
            bar.setForeground(progress >= 100 ? new Color(34, 197, 94) : BLUE);
            bar.setBackground(new Color(226, 232, 240));
            bar.setPreferredSize(new Dimension(85, 8));
            panel.add(bar, BorderLayout.CENTER);

            JLabel percent = new JLabel(progress + "%");
            percent.setForeground(MUTED);
            percent.setFont(new Font("Inter", Font.PLAIN, 11));
            panel.add(percent, BorderLayout.EAST);

            return panel;
        }
    }

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            String status = String.valueOf(value);
            switch (status) {
                case "active" -> {
                    label.setText("Ativo");
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(new Color(22, 163, 74));
                }
                case "suspended" -> {
                    label.setText("Suspenso");
                    label.setBackground(new Color(254, 243, 199));
                    label.setForeground(new Color(217, 119, 6));
                }
                case "completed" -> {
                    label.setText("Concluido");
                    label.setBackground(new Color(219, 234, 254));
                    label.setForeground(new Color(29, 78, 216));
                }
                default -> {
                    label.setText("Inativo");
                    label.setBackground(new Color(241, 245, 249));
                    label.setForeground(new Color(100, 116, 139));
                }
            }
            return label;
        }
    }

    private static class PaymentBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            String payment = String.valueOf(value);
            switch (payment) {
                case "up_to_date" -> {
                    label.setText("Regularizado");
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(new Color(22, 163, 74));
                }
                case "pending" -> {
                    label.setText("Pendente");
                    label.setBackground(new Color(254, 243, 199));
                    label.setForeground(new Color(217, 119, 6));
                }
                case "overdue" -> {
                    label.setText("Em Atraso");
                    label.setBackground(new Color(254, 226, 226));
                    label.setForeground(new Color(220, 38, 38));
                }
                default -> {
                    label.setText("Pendente");
                    label.setBackground(new Color(254, 243, 199));
                    label.setForeground(new Color(217, 119, 6));
                }
            }
            return label;
        }
    }

    private static class ArrowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(">", SwingConstants.CENTER);
            label.setForeground(new Color(203, 213, 225));
            label.setFont(new Font("Inter", Font.BOLD, 12));
            label.setOpaque(true);
            label.setBackground(isSelected ? table.getSelectionBackground() : WHITE);
            return label;
        }
    }

    private static class AvatarCircle extends JComponent {
        private final String initials;
        private final Color color;

        AvatarCircle(String initials, Color color) {
            this.initials = initials;
            this.color = color;
            setPreferredSize(new Dimension(34, 34));
            setMinimumSize(new Dimension(34, 34));
            setMaximumSize(new Dimension(34, 34));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Inter", Font.BOLD, 11));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(initials)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2d.drawString(initials, x, y);
            g2d.dispose();
        }
    }
}
