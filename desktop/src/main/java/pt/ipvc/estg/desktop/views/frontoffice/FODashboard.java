package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.controllers.EvaluationController;
import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Dashboard do aluno em visual moderno.
 */
public class FODashboard extends JPanel {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color SOFT = new Color(148, 163, 184);       // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0
    private static final Color BLUE_DARK = new Color(13, 71, 161);    // #0D47A1
    private static final Color GREEN = new Color(22, 163, 74);        // #16A34A
    private static final Color ORANGE = new Color(217, 119, 6);       // #D97706

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Student student;
    private final FlightController flightController;
    private final EvaluationController evaluationController;
    private final List<Flight> flights = new ArrayList<>();
    private final List<Evaluation> evaluations = new ArrayList<>();

    public FODashboard(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        this.evaluationController = new EvaluationController();
        loadData();
        initializeUI();
    }

    private void loadData() {
        flights.clear();
        List<Flight> studentFlights = flightController.obterVoosPorEstudante(student.getId());
        if (studentFlights != null) {
            flights.addAll(studentFlights);
        }
        flights.sort(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder())));

        evaluations.clear();
        List<Evaluation> studentEvaluations = evaluationController.obterAvaliacoesPorEstudante(student.getId());
        if (studentEvaluations != null) {
            evaluations.addAll(studentEvaluations);
        }
        evaluations.sort(Comparator.comparing(Evaluation::getEvaluationDate, Comparator.nullsLast(Comparator.reverseOrder())));
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(createWelcomeBanner());
        content.add(Box.createVerticalStrut(12));
        content.add(createProgressCard());
        content.add(Box.createVerticalStrut(12));
        content.add(createMainGrid());
        content.add(Box.createVerticalStrut(12));
        content.add(createWeekAgendaCard());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createWelcomeBanner() {
        JPanel banner = new JPanel(new BorderLayout(10, 0));
        banner.setBackground(TITLE);
        banner.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        String firstName = student.getName() == null ? "Aluno" : student.getName().split("\\s+")[0];
        JLabel subtitle = new JLabel("Ola de volta,");
        subtitle.setForeground(new Color(147, 197, 253));
        subtitle.setFont(new Font("Inter", Font.PLAIN, 12));
        JLabel title = new JLabel(firstName + " \u2708");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Inter", Font.BOLD, 28));
        JLabel line = new JLabel(safeCourse() + " | N. Aluno 100" + student.getId());
        line.setForeground(new Color(191, 219, 254));
        line.setFont(new Font("Inter", Font.PLAIN, 12));
        left.add(subtitle);
        left.add(title);
        left.add(line);
        banner.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new GridLayout(1, 2, 8, 0));
        right.setOpaque(false);
        right.add(createBannerKpi(formatHours(totalCompletedHours()), "Horas de Voo"));
        right.add(createBannerKpi((student.getProgress() != null ? student.getProgress() : 0) + "%", "Progresso"));
        banner.add(right, BorderLayout.EAST);
        return banner;
    }

    private JPanel createBannerKpi(String value, String label) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(255, 255, 255, 38));
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 20));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel text = new JLabel(label, SwingConstants.CENTER);
        text.setForeground(new Color(191, 219, 254));
        text.setFont(new Font("Inter", Font.PLAIN, 11));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(valueLabel);
        card.add(text);
        return card;
    }

    private JPanel createProgressCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Progresso do Curso");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        header.add(title, BorderLayout.WEST);
        header.add(createHeaderLinkButton("Ver detalhes", "flights"), BorderLayout.EAST);
        card.add(header);
        card.add(Box.createVerticalStrut(10));

        JPanel metrics = new JPanel(new GridLayout(1, 4, 8, 0));
        metrics.setOpaque(false);
        metrics.add(createMiniProgress("Horas de Voo", totalCompletedHours(), 45, BLUE));
        metrics.add(createMiniProgress("Horas Teoricas", safeDouble(student.getTheoreticalHours()), 100, new Color(124, 58, 237)));
        metrics.add(createMiniProgress("Modulos", estimateCompletedModules(), 8, GREEN));
        metrics.add(createMiniProgress("Avaliacoes", passedEvaluations(), Math.max(1, evaluations.size()), ORANGE));
        card.add(metrics);
        card.add(Box.createVerticalStrut(10));

        int progress = student.getProgress() != null ? student.getProgress() : 0;
        JLabel progressLabel = new JLabel("Progresso Geral: " + progress + "%");
        progressLabel.setForeground(MUTED);
        progressLabel.setFont(new Font("Inter", Font.BOLD, 12));
        card.add(progressLabel);
        card.add(Box.createVerticalStrut(6));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setForeground(BLUE);
        progressBar.setBackground(new Color(226, 232, 240));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(240, 10));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        card.add(progressBar);
        return card;
    }

    private JPanel createMiniProgress(String label, double current, double total, Color color) {
        JPanel box = new JPanel();
        box.setOpaque(true);
        box.setBackground(new Color(248, 250, 252));
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel name = new JLabel(label);
        name.setForeground(MUTED);
        name.setFont(new Font("Inter", Font.PLAIN, 11));
        box.add(name);

        JLabel value = new JLabel(formatMetric(current, total));
        value.setForeground(color);
        value.setFont(new Font("Inter", Font.BOLD, 12));
        box.add(value);
        box.add(Box.createVerticalStrut(6));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.min(100, Math.round((current * 100.0) / Math.max(1.0, total))));
        bar.setForeground(color);
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(120, 6));
        box.add(bar);
        return box;
    }

    private JPanel createMainGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 10, 0));
        grid.setOpaque(false);
        grid.add(createUpcomingFlightsCard());
        grid.add(createNotificationsCard());
        return grid;
    }

    private JPanel createUpcomingFlightsCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Proximos Voos");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        header.add(title, BorderLayout.WEST);
        header.add(createHeaderLinkButton("Ver todos", "flights"), BorderLayout.EAST);
        card.add(header);
        card.add(Box.createVerticalStrut(8));

        List<Flight> upcoming = flights.stream().filter(this::isUpcoming).limit(4).toList();
        if (upcoming.isEmpty()) {
            JLabel empty = new JLabel("Sem voos agendados.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            card.add(empty);
            return card;
        }

        for (int i = 0; i < upcoming.size(); i++) {
            card.add(createUpcomingRow(upcoming.get(i)));
            if (i < upcoming.size() - 1) {
                card.add(Box.createVerticalStrut(8));
            }
        }
        return card;
    }

    private JPanel createUpcomingRow(Flight flight) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(new Color(248, 250, 252));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel dayBox = new JLabel(flight.getFlightDate() != null
                ? flight.getFlightDate().format(DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("pt-PT")))
                : "--", SwingConstants.CENTER);
        dayBox.setOpaque(true);
        dayBox.setBackground(new Color(219, 234, 254));
        dayBox.setForeground(new Color(29, 78, 216));
        dayBox.setBorder(new EmptyBorder(6, 8, 6, 8));
        dayBox.setFont(new Font("Inter", Font.BOLD, 10));
        row.add(dayBox, BorderLayout.WEST);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel objective = new JLabel(safe(flight.getObjectives(), "Sessao de voo"));
        objective.setForeground(TITLE);
        objective.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel meta = new JLabel(safeAircraft(flight) + " | " + safeInstructor(flight) + " | " + safeTime(flight));
        meta.setForeground(MUTED);
        meta.setFont(new Font("Inter", Font.PLAIN, 10));

        info.add(objective);
        info.add(meta);
        row.add(info, BorderLayout.CENTER);

        JLabel badge = new JLabel(typeLabel(flight), SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(typeBg(flight));
        badge.setForeground(typeColor(flight));
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));
        badge.setFont(new Font("Inter", Font.BOLD, 10));
        row.add(badge, BorderLayout.EAST);
        return row;
    }

    private JPanel createNotificationsCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Notificacoes");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        card.add(createNotification("info", "Voo agendado para amanha as " + nextFlightTime(), "Hoje"));
        card.add(Box.createVerticalStrut(8));
        card.add(createNotification("success", "Ultima avaliacao registada com sucesso.", "Recente"));
        card.add(Box.createVerticalStrut(8));
        card.add(createNotification("warning", "Consulte pagamentos pendentes no menu Pagamentos.", "Pendente"));

        card.add(Box.createVerticalStrut(10));
        JButton paymentsButton = createHeaderLinkButton("Abrir pagamentos", "payments");
        paymentsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(paymentsButton);
        return card;
    }

    private JPanel createNotification(String type, String message, String when) {
        Color bg = switch (type) {
            case "success" -> new Color(220, 252, 231);
            case "warning" -> new Color(254, 243, 199);
            default -> new Color(219, 234, 254);
        };
        Color fg = switch (type) {
            case "success" -> GREEN;
            case "warning" -> ORANGE;
            default -> new Color(29, 78, 216);
        };

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(new Color(248, 250, 252));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel dot = new JLabel("\u25CF");
        dot.setForeground(fg);
        dot.setFont(new Font("Dialog", Font.PLAIN, 9));
        dot.setOpaque(true);
        dot.setBackground(bg);
        dot.setHorizontalAlignment(SwingConstants.CENTER);
        dot.setPreferredSize(new Dimension(22, 22));
        row.add(dot, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel msg = new JLabel("<html>" + message + "</html>");
        msg.setForeground(new Color(55, 65, 81));
        msg.setFont(new Font("Inter", Font.PLAIN, 11));
        JLabel time = new JLabel(when);
        time.setForeground(SOFT);
        time.setFont(new Font("Inter", Font.PLAIN, 10));
        text.add(msg);
        text.add(time);
        row.add(text, BorderLayout.CENTER);
        return row;
    }

    private JPanel createWeekAgendaCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Agenda desta Semana");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        header.add(title, BorderLayout.WEST);
        header.add(createHeaderLinkButton("Ver horario completo", "schedule"), BorderLayout.EAST);
        card.add(header);
        card.add(Box.createVerticalStrut(8));

        List<Flight> weekFlights = flights.stream()
                .filter(this::isWithinCurrentWeek)
                .filter(this::isUpcoming)
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        if (weekFlights.isEmpty()) {
            JLabel empty = new JLabel("Sem atividades registadas para esta semana.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            card.add(empty);
            return card;
        }

        JPanel grid = new JPanel(new GridLayout(1, Math.min(5, weekFlights.size()), 8, 0));
        grid.setOpaque(false);
        for (Flight flight : weekFlights.subList(0, Math.min(5, weekFlights.size()))) {
            grid.add(createAgendaTile(flight));
        }
        card.add(grid);
        return card;
    }

    private JPanel createAgendaTile(Flight flight) {
        JPanel tile = new JPanel();
        tile.setOpaque(true);
        tile.setBackground(new Color(248, 250, 252));
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String day = flight.getFlightDate() != null
                ? flight.getFlightDate().getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.forLanguageTag("pt-PT"))
                : "Dia";
        JLabel dayLabel = new JLabel(day.toUpperCase(Locale.ROOT));
        dayLabel.setForeground(SOFT);
        dayLabel.setFont(new Font("Inter", Font.BOLD, 10));

        JLabel objective = new JLabel(safe(flight.getObjectives(), "Sessao"));
        objective.setForeground(TITLE);
        objective.setFont(new Font("Inter", Font.BOLD, 11));

        JLabel info = new JLabel(safeTime(flight) + " | " + safeAircraft(flight));
        info.setForeground(MUTED);
        info.setFont(new Font("Inter", Font.PLAIN, 10));

        tile.add(dayLabel);
        tile.add(objective);
        tile.add(info);
        return tile;
    }

    private boolean isUpcoming(Flight flight) {
        String status = safe(flight.getStatus(), "scheduled").toLowerCase(Locale.ROOT);
        if (status.contains("complete") || status.contains("cancel")) {
            return false;
        }
        return flight.getFlightDate() == null || !flight.getFlightDate().isBefore(LocalDate.now());
    }

    private boolean isWithinCurrentWeek(Flight flight) {
        if (flight.getFlightDate() == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        int shift = today.getDayOfWeek().getValue() - 1;
        LocalDate monday = today.minusDays(shift);
        LocalDate sunday = monday.plusDays(6);
        return !flight.getFlightDate().isBefore(monday) && !flight.getFlightDate().isAfter(sunday);
    }

    private double totalCompletedHours() {
        return flights.stream()
                .filter(f -> safe(f.getStatus(), "").toLowerCase(Locale.ROOT).contains("complete"))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    private double passedEvaluations() {
        return evaluations.stream()
                .filter(e -> "passed".equalsIgnoreCase(safe(e.getStatus(), "")))
                .count();
    }

    private double estimateCompletedModules() {
        int progress = student.getProgress() != null ? student.getProgress() : 0;
        return Math.round(progress / 12.5);
    }

    private String nextFlightTime() {
        return flights.stream()
                .filter(this::isUpcoming)
                .min(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::safeTime)
                .orElse("09:00");
    }

    private String safeCourse() {
        return student.getCourse() != null && student.getCourse().getName() != null
                ? student.getCourse().getName()
                : "Curso";
    }

    private String safeAircraft(Flight flight) {
        return flight.getAircraft() != null && flight.getAircraft().getRegistration() != null
                ? flight.getAircraft().getRegistration()
                : "N/A";
    }

    private String safeInstructor(Flight flight) {
        return flight.getInstructor() != null && flight.getInstructor().getName() != null
                ? flight.getInstructor().getName()
                : "A definir";
    }

    private String safeTime(Flight flight) {
        return flight.getFlightTime() != null ? flight.getFlightTime().toString() : "09:00";
    }

    private String typeLabel(Flight flight) {
        String type = safe(flight.getFlightType(), "local").toLowerCase(Locale.ROOT);
        if (type.contains("nav")) {
            return "Navegacao";
        }
        if (type.contains("ifr")) {
            return "IFR";
        }
        return "Local";
    }

    private Color typeBg(Flight flight) {
        String label = typeLabel(flight);
        if ("Navegacao".equals(label)) {
            return new Color(220, 252, 231);
        }
        if ("IFR".equals(label)) {
            return new Color(243, 232, 255);
        }
        return new Color(219, 234, 254);
    }

    private Color typeColor(Flight flight) {
        String label = typeLabel(flight);
        if ("Navegacao".equals(label)) {
            return GREEN;
        }
        if ("IFR".equals(label)) {
            return new Color(124, 58, 237);
        }
        return new Color(29, 78, 216);
    }

    private String formatMetric(double current, double total) {
        return String.format(Locale.ROOT, "%.0f/%.0f", current, total);
    }

    private String formatHours(double value) {
        return String.format(Locale.ROOT, "%.1fh", value);
    }

    private double safeDouble(Double value) {
        return value != null ? value : 0.0;
    }

    private JButton createHeaderLinkButton(String text, String targetPage) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setForeground(BLUE_DARK);
        button.setFont(new Font("Inter", Font.BOLD, 11));
        button.addActionListener(e -> navigateToPage(targetPage));
        return button;
    }

    private void navigateToPage(String page) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof FOLayout layout) {
            layout.navigateToPage(page);
        }
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
