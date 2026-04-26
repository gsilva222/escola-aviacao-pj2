package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.controllers.EvaluationController;
import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Perfil do aluno com layout moderno.
 */
public class FOProfile extends JPanel {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color SOFT = new Color(148, 163, 184);       // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0
    private static final Color BLUE_DARK = new Color(13, 71, 161);    // #0D47A1
    private static final Color GREEN = new Color(22, 163, 74);        // #16A34A
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Student student;
    private final FlightController flightController;
    private final EvaluationController evaluationController;
    private final List<Flight> flights = new ArrayList<>();
    private final List<Evaluation> evaluations = new ArrayList<>();

    private boolean editing = false;
    private final Map<String, JTextField> personalFields = new LinkedHashMap<>();
    private final CardLayout tabLayout = new CardLayout();
    private final JPanel tabContent = new JPanel(tabLayout);
    private final Map<String, JButton> tabButtons = new LinkedHashMap<>();

    public FOProfile(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        this.evaluationController = new EvaluationController();
        loadData();
        initializeUI();
    }

    private void loadData() {
        flights.clear();
        List<Flight> flightList = flightController.obterVoosPorEstudante(student.getId());
        if (flightList != null) {
            flights.addAll(flightList);
        }

        evaluations.clear();
        List<Evaluation> evaluationList = evaluationController.obterAvaliacoesPorEstudante(student.getId());
        if (evaluationList != null) {
            evaluations.addAll(evaluationList);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(createHeaderCard());
        content.add(Box.createVerticalStrut(12));
        content.add(createTabsBar());
        content.add(Box.createVerticalStrut(10));

        tabContent.setOpaque(false);
        tabContent.add(createPersonalTab(), "personal");
        tabContent.add(createSecurityTab(), "security");
        tabContent.add(createNotificationsTab(), "notifications");
        content.add(tabContent);

        setActiveTab("personal");

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderCard() {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setOpaque(false);

        JPanel avatar = new JPanel(new GridBagLayout());
        avatar.setOpaque(true);
        avatar.setBackground(BLUE_DARK);
        avatar.setPreferredSize(new Dimension(82, 82));
        JLabel initials = new JLabel(studentInitials());
        initials.setForeground(Color.WHITE);
        initials.setFont(new Font("Inter", Font.BOLD, 28));
        avatar.add(initials);
        left.add(avatar, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(safe(student.getName(), "Aluno"));
        name.setForeground(TITLE);
        name.setFont(new Font("Inter", Font.BOLD, 24));
        JLabel course = new JLabel(safeCourse() + " | N. Aluno 100" + student.getId());
        course.setForeground(MUTED);
        course.setFont(new Font("Inter", Font.PLAIN, 12));
        JLabel contact = new JLabel(safe(student.getEmail(), "-") + " | " + safe(student.getPhone(), "-"));
        contact.setForeground(SOFT);
        contact.setFont(new Font("Inter", Font.PLAIN, 11));
        info.add(name);
        info.add(course);
        info.add(contact);
        left.add(info, BorderLayout.CENTER);
        card.add(left, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        JButton editButton = new JButton("Editar Perfil");
        stylePrimaryButton(editButton);
        editButton.addActionListener(e -> toggleEditing());
        actions.add(editButton);
        actions.add(Box.createVerticalStrut(8));

        JPanel status = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        status.setOpaque(true);
        status.setBackground(new Color(220, 252, 231));
        status.setBorder(new EmptyBorder(4, 8, 4, 8));
        JLabel statusText = new JLabel("Ativo");
        statusText.setForeground(GREEN);
        statusText.setFont(new Font("Inter", Font.BOLD, 11));
        status.add(statusText);
        actions.add(status);
        card.add(actions, BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(1, 6, 6, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(12, 0, 0, 0));
        stats.add(createQuickStat(formatHours(completedHours()), "Horas"));
        stats.add(createQuickStat((student.getProgress() != null ? student.getProgress() : 0) + "%", "Progresso"));
        stats.add(createQuickStat(String.valueOf(evaluations.size()), "Avaliacoes"));
        stats.add(createQuickStat(String.valueOf(passedEvaluations()), "Aprovadas"));
        stats.add(createQuickStat(String.valueOf(countDocuments()), "Documentos"));
        stats.add(createQuickStat(String.valueOf(upcomingFlights()), "Voos Agend."));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(card, BorderLayout.NORTH);
        wrapper.add(stats, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createQuickStat(String value, String label) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(248, 250, 252));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setForeground(BLUE);
        val.setFont(new Font("Inter", Font.BOLD, 16));
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel txt = new JLabel(label, SwingConstants.CENTER);
        txt.setForeground(SOFT);
        txt.setFont(new Font("Inter", Font.PLAIN, 10));
        txt.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(val);
        card.add(txt);
        return card;
    }

    private JPanel createTabsBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        bar.setOpaque(true);
        bar.setBackground(WHITE);
        bar.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        addTabButton(bar, "personal", "Dados Pessoais");
        addTabButton(bar, "security", "Seguranca");
        addTabButton(bar, "notifications", "Notificacoes");
        return bar;
    }

    private void addTabButton(JPanel parent, String id, String label) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.addActionListener(e -> setActiveTab(id));
        tabButtons.put(id, button);
        parent.add(button);
    }

    private void setActiveTab(String id) {
        tabLayout.show(tabContent, id);
        tabButtons.forEach((key, button) -> {
            boolean active = key.equals(id);
            button.setOpaque(true);
            button.setBackground(active ? BLUE : WHITE);
            button.setForeground(active ? Color.WHITE : MUTED);
            button.setFont(new Font("Inter", active ? Font.BOLD : Font.PLAIN, 12));
        });
    }

    private JPanel createPersonalTab() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 10, 0));
        grid.setOpaque(false);
        grid.add(createPersonalDataCard());
        grid.add(createAcademicCard());
        return grid;
    }

    private JPanel createPersonalDataCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Dados Pessoais");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        card.add(title);
        card.add(Box.createVerticalStrut(10));

        addEditableField(card, "Nome Completo", safe(student.getName(), ""));
        addEditableField(card, "Email", safe(student.getEmail(), ""));
        addEditableField(card, "Telefone", safe(student.getPhone(), ""));
        addEditableField(card, "Data de Nascimento", student.getBirthdate() != null ? student.getBirthdate().format(DATE_FMT) : "-");
        addEditableField(card, "NIF", safe(student.getNif(), ""));
        addEditableField(card, "Morada", safe(student.getAddress(), ""));
        addEditableField(card, "Nacionalidade", safe(student.getNationality(), ""));

        return card;
    }

    private void addEditableField(JPanel parent, String label, String value) {
        JPanel field = new JPanel();
        field.setOpaque(false);
        field.setLayout(new BoxLayout(field, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setForeground(SOFT);
        lbl.setFont(new Font("Inter", Font.BOLD, 10));

        JTextField input = new JTextField(value);
        input.setEditable(false);
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(7, 8, 7, 8)
        ));
        input.setFont(new Font("Inter", Font.PLAIN, 12));
        input.setForeground(TITLE);
        input.setBackground(new Color(248, 250, 252));
        personalFields.put(label, input);

        field.add(lbl);
        field.add(Box.createVerticalStrut(4));
        field.add(input);
        field.add(Box.createVerticalStrut(8));
        parent.add(field);
    }

    private JPanel createAcademicCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Informacao Academica");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        card.add(title);
        card.add(Box.createVerticalStrut(10));

        card.add(createInfoRow("Curso", safeCourse()));
        card.add(createInfoRow("Instrutor Responsavel", safeInstructor()));
        card.add(createInfoRow("Data de Matricula", student.getEnrollmentDate() != null ? student.getEnrollmentDate().format(DATE_FMT) : "-"));
        card.add(createInfoRow("Numero de Aluno", "100" + student.getId()));
        card.add(Box.createVerticalStrut(12));

        JLabel progressTitle = new JLabel("Progresso do Curso");
        progressTitle.setForeground(TITLE);
        progressTitle.setFont(new Font("Inter", Font.BOLD, 13));
        card.add(progressTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(createProgressRow("Progresso Geral", student.getProgress() != null ? student.getProgress() : 0, 100, BLUE));
        card.add(createProgressRow("Horas de Voo", completedHours(), 45, new Color(124, 58, 237)));
        card.add(createProgressRow("Horas Teoricas", safeDouble(student.getTheoreticalHours()), 100, new Color(5, 150, 105)));
        return card;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(0, 0, 8, 0));
        JLabel lbl = new JLabel(label);
        lbl.setForeground(SOFT);
        lbl.setFont(new Font("Inter", Font.PLAIN, 11));
        JLabel val = new JLabel(value);
        val.setForeground(TITLE);
        val.setFont(new Font("Inter", Font.BOLD, 12));
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel createProgressRow(String label, double current, double total, Color color) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(MUTED);
        lbl.setFont(new Font("Inter", Font.PLAIN, 11));
        JLabel val = new JLabel(String.format(Locale.ROOT, "%.1f/%.1f", current, total));
        val.setForeground(color);
        val.setFont(new Font("Inter", Font.BOLD, 11));
        top.add(lbl, BorderLayout.WEST);
        top.add(val, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.min(100, Math.round((current * 100.0) / Math.max(1.0, total))));
        bar.setForeground(color);
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(200, 8));

        row.add(top);
        row.add(Box.createVerticalStrut(4));
        row.add(bar);
        row.add(Box.createVerticalStrut(8));
        return row;
    }

    private JPanel createSecurityTab() {
        JPanel card = createSimpleCard("Seguranca da Conta");
        card.add(createActionRow("Alterar Password", "Ultima alteracao: ha 3 meses"));
        card.add(Box.createVerticalStrut(8));
        card.add(createActionRow("Autenticacao em Dois Fatores", "Nao configurada"));
        card.add(Box.createVerticalStrut(8));
        card.add(createActionRow("Sessoes Ativas", "1 sessao ativa (este dispositivo)"));
        return card;
    }

    private JPanel createNotificationsTab() {
        JPanel card = createSimpleCard("Preferencias de Notificacoes");
        card.add(createToggleRow("Voos Agendados", "Receber lembretes 24h antes", true));
        card.add(Box.createVerticalStrut(8));
        card.add(createToggleRow("Avaliacoes", "Resultados e datas de exames", true));
        card.add(Box.createVerticalStrut(8));
        card.add(createToggleRow("Pagamentos", "Vencimentos e confirmacoes", true));
        card.add(Box.createVerticalStrut(8));
        card.add(createToggleRow("Novidades do Curso", "Atualizacoes de conteudo", false));
        card.add(Box.createVerticalStrut(8));
        card.add(createToggleRow("Newsletter AeroSchool", "Noticias e eventos", false));
        return card;
    }

    private JPanel createSimpleCard(String title) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TITLE);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        return card;
    }

    private JPanel createActionRow(String title, String subtitle) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(new Color(248, 250, 252));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TITLE);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel sub = new JLabel(subtitle);
        sub.setForeground(SOFT);
        sub.setFont(new Font("Inter", Font.PLAIN, 10));
        text.add(titleLabel);
        text.add(sub);
        row.add(text, BorderLayout.CENTER);

        JLabel chevron = new JLabel(">", SwingConstants.CENTER);
        chevron.setForeground(new Color(203, 213, 225));
        chevron.setFont(new Font("Inter", Font.BOLD, 12));
        row.add(chevron, BorderLayout.EAST);
        return row;
    }

    private JPanel createToggleRow(String title, String subtitle, boolean active) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(new Color(248, 250, 252));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TITLE);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel sub = new JLabel(subtitle);
        sub.setForeground(SOFT);
        sub.setFont(new Font("Inter", Font.PLAIN, 10));
        text.add(titleLabel);
        text.add(sub);
        row.add(text, BorderLayout.CENTER);

        JToggleButton toggle = new JToggleButton();
        toggle.setSelected(active);
        toggle.setPreferredSize(new Dimension(42, 22));
        toggle.setFocusPainted(false);
        toggle.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        toggle.setBackground(active ? BLUE : new Color(226, 232, 240));
        toggle.addActionListener(e -> toggle.setBackground(toggle.isSelected() ? BLUE : new Color(226, 232, 240)));
        row.add(toggle, BorderLayout.EAST);
        return row;
    }

    private void toggleEditing() {
        editing = !editing;
        personalFields.values().forEach(field -> {
            field.setEditable(editing);
            field.setBackground(editing ? WHITE : new Color(248, 250, 252));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(editing ? new Color(191, 219, 254) : BORDER, 1),
                    new EmptyBorder(7, 8, 7, 8)
            ));
        });

        JOptionPane.showMessageDialog(
                this,
                editing
                        ? "Modo de edicao ativado. Alteracoes locais no painel."
                        : "Modo de edicao desativado.",
                "Perfil",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void stylePrimaryButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE_DARK);
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
    }

    private String studentInitials() {
        String name = safe(student.getName(), "AL");
        String[] parts = name.split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        }
        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    private String safeCourse() {
        return student.getCourse() != null && student.getCourse().getName() != null
                ? student.getCourse().getName()
                : "Curso";
    }

    private String safeInstructor() {
        return student.getInstructor() != null && student.getInstructor().getName() != null
                ? student.getInstructor().getName()
                : "A definir";
    }

    private int countDocuments() {
        java.io.File folder = new java.io.File("documentos/student_" + student.getId());
        java.io.File[] files = folder.listFiles();
        return files == null ? 0 : (int) java.util.Arrays.stream(files).filter(java.io.File::isFile).count();
    }

    private int upcomingFlights() {
        return (int) flights.stream()
                .filter(f -> !safe(f.getStatus(), "").toLowerCase(Locale.ROOT).contains("complete"))
                .count();
    }

    private long passedEvaluations() {
        return evaluations.stream()
                .filter(e -> "passed".equalsIgnoreCase(safe(e.getStatus(), "")))
                .count();
    }

    private double completedHours() {
        return flights.stream()
                .filter(f -> safe(f.getStatus(), "").toLowerCase(Locale.ROOT).contains("complete"))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    private double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }

    private String formatHours(double value) {
        return String.format(Locale.ROOT, "%.1fh", value);
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
