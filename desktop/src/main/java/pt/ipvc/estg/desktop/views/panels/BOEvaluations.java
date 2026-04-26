package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.CourseController;
import pt.ipvc.estg.desktop.controllers.EvaluationController;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Evaluation;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BOEvaluations extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color TITLE = new Color(15, 35, 68);
    private static final Color BLUE = new Color(21, 101, 192);
    private static final Color BLUE_DARK = new Color(13, 71, 161);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color DANGER = new Color(220, 38, 38);
    private static final Color PURPLE = new Color(124, 58, 237);

    private final EvaluationController evaluationController;
    private final StudentController studentController;
    private final CourseController courseController;

    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JComboBox<String> statusFilter;
    private JButton registerButton;
    private JButton deleteButton;

    private JLabel totalValueLabel;
    private JLabel passRateValueLabel;
    private JLabel avgScoreValueLabel;
    private JLabel failValueLabel;

    private JTable evaluationsTable;
    private DefaultTableModel tableModel;
    private JPanel recentContainer;
    private JPanel distributionContainer;

    private List<Student> students = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<Evaluation> allEvaluations = new ArrayList<>();
    private List<Evaluation> filteredEvaluations = new ArrayList<>();

    public BOEvaluations() {
        this.evaluationController = new EvaluationController();
        this.studentController = new StudentController();
        this.courseController = new CourseController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createSummaryPanel());
        top.add(Box.createVerticalStrut(12));
        top.add(createAnalyticsPanel());
        top.add(Box.createVerticalStrut(12));
        top.add(createFiltersPanel());
        add(top, BorderLayout.NORTH);

        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);
        panel.add(createSummaryCard("Total de Avaliacoes", BLUE, "total"));
        panel.add(createSummaryCard("Taxa de Aprovacao", SUCCESS, "passRate"));
        panel.add(createSummaryCard("Nota Media", PURPLE, "average"));
        panel.add(createSummaryCard("Reprovacoes", DANGER, "fail"));
        return panel;
    }

    private JPanel createSummaryCard(String title, Color valueColor, String key) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel value = new JLabel("0");
        value.setForeground(valueColor);
        value.setFont(new Font("Inter", Font.BOLD, 22));
        JLabel subtitle = new JLabel(title);
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 12));

        card.add(value);
        card.add(Box.createVerticalStrut(2));
        card.add(subtitle);

        switch (key) {
            case "total" -> totalValueLabel = value;
            case "passRate" -> passRateValueLabel = value;
            case "average" -> avgScoreValueLabel = value;
            case "fail" -> failValueLabel = value;
            default -> {
            }
        }
        return card;
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setOpaque(false);

        JPanel distributionCard = createCardPanel();
        distributionCard.setLayout(new BorderLayout(0, 8));
        distributionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel chartTitle = new JLabel("Distribuicao de Notas");
        chartTitle.setForeground(TITLE);
        chartTitle.setFont(new Font("Inter", Font.BOLD, 13));
        distributionCard.add(chartTitle, BorderLayout.NORTH);

        distributionContainer = new JPanel();
        distributionContainer.setOpaque(false);
        distributionContainer.setLayout(new BoxLayout(distributionContainer, BoxLayout.Y_AXIS));
        distributionCard.add(distributionContainer, BorderLayout.CENTER);

        JPanel recentCard = createCardPanel();
        recentCard.setLayout(new BorderLayout(0, 8));
        recentCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel recentTitle = new JLabel("Avaliacoes Recentes");
        recentTitle.setForeground(TITLE);
        recentTitle.setFont(new Font("Inter", Font.BOLD, 13));
        recentCard.add(recentTitle, BorderLayout.NORTH);

        recentContainer = new JPanel();
        recentContainer.setOpaque(false);
        recentContainer.setLayout(new BoxLayout(recentContainer, BoxLayout.Y_AXIS));
        recentCard.add(recentContainer, BorderLayout.CENTER);

        panel.add(distributionCard);
        panel.add(recentCard);
        return panel;
    }

    private JPanel createFiltersPanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(8, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

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
        card.add(searchField, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);

        typeFilter = new JComboBox<>(new String[]{"all", "theoretical", "practical", "simulator"});
        styleCombo(typeFilter);
        typeFilter.addActionListener(e -> applyFilters());
        right.add(typeFilter);

        statusFilter = new JComboBox<>(new String[]{"all", "passed", "failed", "pending"});
        styleCombo(statusFilter);
        statusFilter.addActionListener(e -> applyFilters());
        right.add(statusFilter);

        JButton reloadButton = new JButton("Recarregar");
        styleSecondaryButton(reloadButton);
        reloadButton.addActionListener(e -> loadData());
        right.add(reloadButton);

        registerButton = new JButton("Registar Resultado");
        styleSecondaryButton(registerButton);
        registerButton.addActionListener(e -> registerSelectedResult());
        right.add(registerButton);

        deleteButton = new JButton("Eliminar");
        styleDangerButton(deleteButton);
        deleteButton.addActionListener(e -> deleteSelectedEvaluation());
        right.add(deleteButton);

        JButton createButton = new JButton("Registar Avaliacao");
        stylePrimaryButton(createButton);
        createButton.addActionListener(e -> createEvaluationDialog());
        right.add(createButton);

        card.add(right, BorderLayout.EAST);
        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout());

        String[] columns = {"Aluno", "Exame", "Curso", "Tipo", "Data", "Nota", "Resultado"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        evaluationsTable = new JTable(tableModel);
        evaluationsTable.setRowHeight(52);
        evaluationsTable.setShowVerticalLines(false);
        evaluationsTable.setGridColor(new Color(241, 245, 249));
        evaluationsTable.setSelectionBackground(new Color(239, 246, 255));
        evaluationsTable.setSelectionForeground(TITLE);
        evaluationsTable.setIntercellSpacing(new Dimension(0, 1));
        evaluationsTable.setBorder(null);
        evaluationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evaluationsTable.getSelectionModel().addListSelectionListener(e -> updateActionButtons());

        evaluationsTable.getTableHeader().setReorderingAllowed(false);
        evaluationsTable.getTableHeader().setBackground(new Color(248, 250, 252));
        evaluationsTable.getTableHeader().setForeground(MUTED);
        evaluationsTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 11));

        evaluationsTable.getColumnModel().getColumn(0).setCellRenderer(new StudentEvaluationRenderer());
        evaluationsTable.getColumnModel().getColumn(3).setCellRenderer(new TypeBadgeRenderer());
        evaluationsTable.getColumnModel().getColumn(4).setCellRenderer(new DateRenderer());
        evaluationsTable.getColumnModel().getColumn(5).setCellRenderer(new ScoreRenderer());
        evaluationsTable.getColumnModel().getColumn(6).setCellRenderer(new ResultBadgeRenderer());

        evaluationsTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        evaluationsTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        evaluationsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        evaluationsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        evaluationsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        evaluationsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        evaluationsTable.getColumnModel().getColumn(6).setPreferredWidth(110);

        evaluationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = evaluationsTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        evaluationsTable.setRowSelectionInterval(row, row);
                        registerSelectedResult();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(evaluationsTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        students = studentController.listarEstudantes();
        courses = courseController.listarCursos();

        allEvaluations = new ArrayList<>(evaluationController.listarAvaliacoes());
        allEvaluations.sort(Comparator.comparing(Evaluation::getEvaluationDate, Comparator.nullsLast(Comparator.reverseOrder())));

        updateSummary();
        updateRecentPanel();
        updateDistributionPanel();
        applyFilters();
    }

    private void applyFilters() {
        String search = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedType = typeFilter == null ? "all" : String.valueOf(typeFilter.getSelectedItem());
        String selectedStatus = statusFilter == null ? "all" : String.valueOf(statusFilter.getSelectedItem());

        filteredEvaluations = allEvaluations.stream()
                .filter(evaluation -> {
                    String studentName = evaluation.getStudent() != null ? safeLower(evaluation.getStudent().getName()) : "";
                    String examName = safeLower(evaluation.getExamName());
                    String resolvedType = resolveType(evaluation);
                    String resolvedStatus = resolveStatus(evaluation);

                    boolean matchSearch = search.isEmpty()
                            || studentName.contains(search)
                            || examName.contains(search);
                    boolean matchType = "all".equals(selectedType) || selectedType.equals(resolvedType);
                    boolean matchStatus = "all".equals(selectedStatus) || selectedStatus.equals(resolvedStatus);
                    return matchSearch && matchType && matchStatus;
                })
                .toList();

        refreshTable();
        updateActionButtons();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Evaluation evaluation : filteredEvaluations) {
            tableModel.addRow(new Object[]{
                    evaluation,
                    safeText(evaluation.getExamName(), "Exame"),
                    evaluation.getCourse() != null ? safeText(evaluation.getCourse().getName(), "-") : "-",
                    resolveType(evaluation),
                    evaluation.getEvaluationDate(),
                    evaluation.getScore(),
                    resolveStatus(evaluation)
            });
        }
    }

    private void updateSummary() {
        long total = allEvaluations.size();
        long passed = allEvaluations.stream().filter(e -> "passed".equals(resolveStatus(e))).count();
        long failed = allEvaluations.stream().filter(e -> "failed".equals(resolveStatus(e))).count();
        long scored = allEvaluations.stream().filter(e -> e.getScore() != null).count();
        int averageScore = scored == 0
                ? 0
                : (int) Math.round(allEvaluations.stream()
                .filter(e -> e.getScore() != null)
                .mapToInt(Evaluation::getScore)
                .average()
                .orElse(0.0));
        int passRate = (passed + failed) == 0 ? 0 : (int) Math.round((passed * 100.0) / (passed + failed));

        totalValueLabel.setText(String.valueOf(total));
        passRateValueLabel.setText(passRate + "%");
        avgScoreValueLabel.setText(averageScore + "/100");
        failValueLabel.setText(String.valueOf(failed));
    }

    private void updateDistributionPanel() {
        distributionContainer.removeAll();

        int[] bins = new int[6];
        for (Evaluation evaluation : allEvaluations) {
            Integer score = evaluation.getScore();
            if (score == null) {
                continue;
            }
            if (score < 50) {
                bins[0]++;
            } else if (score < 60) {
                bins[1]++;
            } else if (score < 70) {
                bins[2]++;
            } else if (score < 80) {
                bins[3]++;
            } else if (score < 90) {
                bins[4]++;
            } else {
                bins[5]++;
            }
        }

        String[] labels = {"0-49", "50-59", "60-69", "70-79", "80-89", "90-100"};
        int max = 1;
        for (int count : bins) {
            max = Math.max(max, count);
        }

        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);

            JLabel label = new JLabel(labels[i]);
            label.setForeground(MUTED);
            label.setFont(new Font("Inter", Font.PLAIN, 11));
            label.setPreferredSize(new Dimension(50, 18));

            JProgressBar bar = new JProgressBar(0, max);
            bar.setValue(bins[i]);
            bar.setStringPainted(false);
            bar.setBackground(new Color(241, 245, 249));
            bar.setForeground(i < 2 ? DANGER : (i < 3 ? new Color(245, 158, 11) : SUCCESS));
            bar.setBorderPainted(false);

            JLabel count = new JLabel(String.valueOf(bins[i]));
            count.setForeground(TITLE);
            count.setFont(new Font("Inter", Font.BOLD, 11));
            count.setPreferredSize(new Dimension(22, 18));
            count.setHorizontalAlignment(SwingConstants.RIGHT);

            row.add(label, BorderLayout.WEST);
            row.add(bar, BorderLayout.CENTER);
            row.add(count, BorderLayout.EAST);
            distributionContainer.add(row);
            if (i < labels.length - 1) {
                distributionContainer.add(Box.createVerticalStrut(6));
            }
        }

        distributionContainer.revalidate();
        distributionContainer.repaint();
    }

    private void updateRecentPanel() {
        recentContainer.removeAll();

        if (allEvaluations.isEmpty()) {
            JLabel empty = new JLabel("Sem avaliacoes recentes.");
            empty.setForeground(new Color(148, 163, 184));
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            recentContainer.add(empty);
            recentContainer.revalidate();
            recentContainer.repaint();
            return;
        }

        int max = Math.min(4, allEvaluations.size());
        for (int i = 0; i < max; i++) {
            Evaluation evaluation = allEvaluations.get(i);

            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(true);
            row.setBackground(new Color(248, 250, 252));
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    new EmptyBorder(8, 8, 8, 8)
            ));

            JLabel dot = new JLabel(resolveStatus(evaluation).equals("passed") ? "\u2714" : "\u2716", SwingConstants.CENTER);
            dot.setOpaque(true);
            dot.setPreferredSize(new Dimension(24, 24));
            if ("passed".equals(resolveStatus(evaluation))) {
                dot.setBackground(new Color(220, 252, 231));
                dot.setForeground(SUCCESS);
            } else if ("failed".equals(resolveStatus(evaluation))) {
                dot.setBackground(new Color(254, 226, 226));
                dot.setForeground(DANGER);
            } else {
                dot.setBackground(new Color(219, 234, 254));
                dot.setForeground(BLUE);
            }

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            JLabel line1 = new JLabel((evaluation.getStudent() != null ? safeText(evaluation.getStudent().getName(), "Aluno") : "Aluno")
                    + " - " + safeText(evaluation.getExamName(), "Exame"));
            line1.setForeground(TITLE);
            line1.setFont(new Font("Inter", Font.BOLD, 11));
            JLabel line2 = new JLabel(evaluation.getEvaluationDate() != null ? formatDate(evaluation.getEvaluationDate()) : "Sem data");
            line2.setForeground(new Color(148, 163, 184));
            line2.setFont(new Font("Inter", Font.PLAIN, 10));
            text.add(line1);
            text.add(line2);

            JLabel score = new JLabel(evaluation.getScore() != null ? String.valueOf(evaluation.getScore()) : "-", SwingConstants.RIGHT);
            score.setForeground(scoreColor(evaluation.getScore()));
            score.setFont(new Font("Inter", Font.BOLD, 14));

            row.add(dot, BorderLayout.WEST);
            row.add(text, BorderLayout.CENTER);
            row.add(score, BorderLayout.EAST);

            recentContainer.add(row);
            if (i < max - 1) {
                recentContainer.add(Box.createVerticalStrut(6));
            }
        }

        recentContainer.revalidate();
        recentContainer.repaint();
    }

    private void createEvaluationDialog() {
        if (students.isEmpty() || courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nao existem alunos ou cursos disponiveis.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<StudentChoice> studentCombo = new JComboBox<>();
        for (Student student : students) {
            studentCombo.addItem(new StudentChoice(student));
        }

        JComboBox<CourseChoice> courseCombo = new JComboBox<>();
        for (Course course : courses) {
            courseCombo.addItem(new CourseChoice(course));
        }

        JTextField examField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Aluno"));
        form.add(studentCombo);
        form.add(new JLabel("Curso"));
        form.add(courseCombo);
        form.add(new JLabel("Exame / Avaliacao"));
        form.add(examField);

        int result = JOptionPane.showConfirmDialog(this, form, "Nova Avaliacao", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            StudentChoice studentChoice = (StudentChoice) studentCombo.getSelectedItem();
            CourseChoice courseChoice = (CourseChoice) courseCombo.getSelectedItem();
            if (studentChoice == null || courseChoice == null) {
                throw new IllegalArgumentException("Aluno ou curso invalido.");
            }
            String examName = examField.getText() == null ? "" : examField.getText().trim();
            if (examName.isEmpty()) {
                throw new IllegalArgumentException("Nome do exame obrigatorio.");
            }

            evaluationController.criarAvaliacao(studentChoice.student, courseChoice.course, examName);
            loadData();
            JOptionPane.showMessageDialog(this, "Avaliacao criada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar avaliacao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerSelectedResult() {
        int row = evaluationsTable.getSelectedRow();
        if (row < 0 || row >= filteredEvaluations.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma avaliacao.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Evaluation evaluation = filteredEvaluations.get(row);

        JSpinner scoreSpinner = new JSpinner(new SpinnerNumberModel(
                evaluation.getScore() != null ? evaluation.getScore() : 75,
                0,
                100,
                1
        ));
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"theoretical", "practical", "simulator"});
        if (evaluation.getEvaluationType() != null && !evaluation.getEvaluationType().isBlank()) {
            typeCombo.setSelectedItem(evaluation.getEvaluationType());
        }
        JTextArea notesArea = new JTextArea(evaluation.getNotes() != null ? evaluation.getNotes() : "", 4, 24);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Nota (0-100)"));
        form.add(scoreSpinner);
        form.add(new JLabel("Tipo"));
        form.add(typeCombo);
        form.add(new JLabel("Notas"));
        form.add(new JScrollPane(notesArea));

        int result = JOptionPane.showConfirmDialog(this, form, "Registar Resultado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int score = (int) scoreSpinner.getValue();
            String type = String.valueOf(typeCombo.getSelectedItem());
            String notes = notesArea.getText() == null ? "" : notesArea.getText().trim();
            evaluationController.registarResultado(evaluation.getId(), score, type, notes);
            loadData();
            JOptionPane.showMessageDialog(this, "Resultado registado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registar resultado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedEvaluation() {
        int row = evaluationsTable.getSelectedRow();
        if (row < 0 || row >= filteredEvaluations.size()) {
            JOptionPane.showMessageDialog(this, "Selecione uma avaliacao para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Evaluation evaluation = filteredEvaluations.get(row);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirma eliminacao da avaliacao #" + evaluation.getId() + "?",
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            evaluationController.eliminarAvaliacao(evaluation.getId());
            loadData();
            JOptionPane.showMessageDialog(this, "Avaliacao eliminada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao eliminar avaliacao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateActionButtons() {
        int selectedRow = evaluationsTable == null ? -1 : evaluationsTable.getSelectedRow();
        boolean hasSelection = selectedRow >= 0 && selectedRow < filteredEvaluations.size();
        registerButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }

    private String resolveStatus(Evaluation evaluation) {
        String status = safeLower(evaluation.getStatus());
        if ("passed".equals(status) || "failed".equals(status)) {
            return status;
        }
        if (evaluation.getScore() != null) {
            return evaluation.getScore() >= 50 ? "passed" : "failed";
        }
        return "pending";
    }

    private String resolveType(Evaluation evaluation) {
        String type = safeLower(evaluation.getEvaluationType());
        if ("practical".equals(type) || "simulator".equals(type)) {
            return type;
        }
        return "theoretical";
    }

    private Color scoreColor(Integer score) {
        if (score == null) {
            return new Color(148, 163, 184);
        }
        return score >= 75 ? SUCCESS : DANGER;
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return panel;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Inter", Font.PLAIN, 12));
        combo.setBackground(WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 12));
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE_DARK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 14, 8, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static class StudentChoice {
        private final Student student;

        private StudentChoice(Student student) {
            this.student = student;
        }

        @Override
        public String toString() {
            if (student == null) {
                return "N/A";
            }
            return safe(student.getName()) + " - " + safe(student.getEmail());
        }

        private static String safe(String value) {
            return value == null ? "" : value;
        }
    }

    private static class CourseChoice {
        private final Course course;

        private CourseChoice(Course course) {
            this.course = course;
        }

        @Override
        public String toString() {
            if (course == null) {
                return "N/A";
            }
            return course.getName() != null ? course.getName() : "Curso";
        }
    }

    private static class StudentEvaluationRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Evaluation evaluation = (Evaluation) value;
            Student student = evaluation != null ? evaluation.getStudent() : null;

            JPanel panel = new JPanel(new BorderLayout(8, 0));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : WHITE);
            panel.setBorder(new EmptyBorder(6, 8, 6, 8));

            JLabel avatar = new JLabel(initials(student), SwingConstants.CENTER);
            avatar.setOpaque(true);
            avatar.setForeground(Color.WHITE);
            avatar.setBackground(BLUE);
            avatar.setPreferredSize(new Dimension(28, 28));
            avatar.setFont(new Font("Inter", Font.BOLD, 10));

            JLabel name = new JLabel(student != null ? safe(student.getName(), "Aluno") : "Aluno");
            name.setForeground(TITLE);
            name.setFont(new Font("Inter", Font.PLAIN, 12));

            panel.add(avatar, BorderLayout.WEST);
            panel.add(name, BorderLayout.CENTER);
            return panel;
        }

        private String initials(Student student) {
            if (student == null || student.getName() == null || student.getName().isBlank()) {
                return "AL";
            }
            String[] parts = student.getName().trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
            }
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }

        private String safe(String value, String fallback) {
            return value == null || value.isBlank() ? fallback : value;
        }
    }

    private static class TypeBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            String type = String.valueOf(value);
            switch (type) {
                case "practical" -> {
                    label.setText("Pratico");
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(SUCCESS);
                }
                case "simulator" -> {
                    label.setText("Simulador");
                    label.setBackground(new Color(243, 232, 255));
                    label.setForeground(PURPLE);
                }
                default -> {
                    label.setText("Teorico");
                    label.setBackground(new Color(219, 234, 254));
                    label.setForeground(new Color(29, 78, 216));
                }
            }
            return label;
        }
    }

    private static class DateRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof LocalDate date) {
                label.setText(String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear()));
                label.setForeground(MUTED);
            } else {
                label.setText("-");
                label.setForeground(new Color(148, 163, 184));
            }
            label.setFont(new Font("Inter", Font.PLAIN, 12));
            return label;
        }
    }

    private static class ScoreRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number score) {
                int numericScore = score.intValue();
                label.setText(numericScore + " /100");
                label.setForeground(numericScore >= 75 ? SUCCESS : DANGER);
                label.setFont(new Font("Inter", Font.BOLD, 12));
            } else {
                label.setText("-");
                label.setForeground(new Color(148, 163, 184));
                label.setFont(new Font("Inter", Font.PLAIN, 12));
            }
            return label;
        }
    }

    private static class ResultBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            String status = String.valueOf(value);
            switch (status) {
                case "passed" -> {
                    label.setText("Aprovado");
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(SUCCESS);
                }
                case "failed" -> {
                    label.setText("Reprovado");
                    label.setBackground(new Color(254, 226, 226));
                    label.setForeground(DANGER);
                }
                default -> {
                    label.setText("Pendente");
                    label.setBackground(new Color(219, 234, 254));
                    label.setForeground(BLUE);
                }
            }
            return label;
        }
    }
}
