package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.controllers.EvaluationController;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Avaliacoes do aluno em layout moderno.
 */
public class FOEvaluations extends JPanel {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color SOFT = new Color(148, 163, 184);       // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0
    private static final Color GREEN = new Color(22, 163, 74);        // #16A34A
    private static final Color PURPLE = new Color(124, 58, 237);      // #7C3AED
    private static final Color ORANGE = new Color(217, 119, 6);       // #D97706
    private static final Color RED = new Color(220, 38, 38);          // #DC2626

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Student student;
    private final EvaluationController evaluationController;
    private final List<Evaluation> evaluations = new ArrayList<>();

    public FOEvaluations(Student student) {
        this.student = student;
        this.evaluationController = new EvaluationController();
        loadData();
        initializeUI();
    }

    private void loadData() {
        evaluations.clear();
        List<Evaluation> list = evaluationController.obterAvaliacoesPorEstudante(student.getId());
        if (list != null) {
            evaluations.addAll(list);
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
        content.add(createStatsRow());
        content.add(Box.createVerticalStrut(12));
        content.add(createMainGrid());
        content.add(Box.createVerticalStrut(12));
        content.add(createHistoryCard());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatsRow() {
        long passed = evaluations.stream().filter(e -> "passed".equals(normalizeStatus(e))).count();
        long total = evaluations.size();
        int avg = (int) Math.round(evaluations.stream()
                .filter(e -> e.getScore() != null)
                .mapToInt(Evaluation::getScore)
                .average()
                .orElse(0.0));
        int best = evaluations.stream()
                .filter(e -> e.getScore() != null)
                .mapToInt(Evaluation::getScore)
                .max()
                .orElse(0);

        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.add(createStatCard("Avaliacoes Realizadas", String.valueOf(total), BLUE));
        row.add(createStatCard("Aprovadas", String.valueOf(passed), GREEN));
        row.add(createStatCard("Nota Media", avg + "/100", PURPLE));
        row.add(createStatCard("Melhor Nota", best + "/100", ORANGE));
        return row;
    }

    private JPanel createStatCard(String label, String value, Color color) {
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

        JLabel text = new JLabel(label);
        text.setForeground(MUTED);
        text.setFont(new Font("Inter", Font.PLAIN, 12));
        card.add(text);
        return card;
    }

    private JPanel createMainGrid() {
        JPanel grid = new JPanel(new GridLayout(1, 3, 10, 0));
        grid.setOpaque(false);
        grid.add(createPerformanceCard());
        grid.add(createUpcomingCard());
        grid.add(createResultsCard());
        return grid;
    }

    private JPanel createPerformanceCard() {
        JPanel card = createCard("Desempenho por Area", "Pontuacao media por disciplina");
        Map<String, Integer> bySubject = buildSubjectAverages();

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        if (bySubject.isEmpty()) {
            JLabel empty = new JLabel("Sem dados suficientes.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            list.add(empty);
        } else {
            for (Map.Entry<String, Integer> entry : bySubject.entrySet()) {
                list.add(createScoreBar(entry.getKey(), entry.getValue()));
                list.add(Box.createVerticalStrut(8));
            }
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel createUpcomingCard() {
        JPanel card = createCard("Proximas Avaliacoes", "Planeamento das proximas provas");

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<Evaluation> upcoming = evaluations.stream()
                .filter(e -> "pending".equals(normalizeStatus(e)))
                .limit(3)
                .toList();

        if (upcoming.isEmpty()) {
            JLabel empty = new JLabel("Sem avaliacoes agendadas.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            list.add(empty);
        } else {
            for (int i = 0; i < upcoming.size(); i++) {
                list.add(createUpcomingRow(upcoming.get(i)));
                if (i < upcoming.size() - 1) {
                    list.add(Box.createVerticalStrut(8));
                }
            }
        }

        JPanel hint = new JPanel(new BorderLayout());
        hint.setOpaque(true);
        hint.setBackground(new Color(255, 247, 237));
        hint.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 215, 170), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        JLabel text = new JLabel("Nota minima de aprovacao: 75/100");
        text.setForeground(new Color(146, 64, 14));
        text.setFont(new Font("Inter", Font.PLAIN, 11));
        hint.add(text, BorderLayout.CENTER);

        card.add(list, BorderLayout.CENTER);
        card.add(hint, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createResultsCard() {
        JPanel card = createCard("Resultados", "Pontuacao por avaliacao");
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        if (evaluations.isEmpty()) {
            JLabel empty = new JLabel("Sem resultados.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            list.add(empty);
        } else {
            for (int i = 0; i < Math.min(6, evaluations.size()); i++) {
                list.add(createResultBar(evaluations.get(i)));
                if (i < Math.min(6, evaluations.size()) - 1) {
                    list.add(Box.createVerticalStrut(8));
                }
            }
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel createHistoryCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("Historico de Avaliacoes");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        card.add(title);
        card.add(Box.createVerticalStrut(8));

        if (evaluations.isEmpty()) {
            JLabel empty = new JLabel("Sem avaliacoes registadas.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            card.add(empty);
            return card;
        }

        for (int i = 0; i < evaluations.size(); i++) {
            card.add(createHistoryRow(evaluations.get(i)));
            if (i < evaluations.size() - 1) {
                card.add(Box.createVerticalStrut(8));
            }
        }
        return card;
    }

    private JPanel createHistoryRow(Evaluation evaluation) {
        String status = normalizeStatus(evaluation);
        Color bg = switch (status) {
            case "passed" -> new Color(220, 252, 231);
            case "failed" -> new Color(254, 226, 226);
            default -> new Color(219, 234, 254);
        };
        Color color = switch (status) {
            case "passed" -> GREEN;
            case "failed" -> RED;
            default -> BLUE;
        };

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(true);
        row.setBackground(new Color(248, 250, 252));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel icon = new JLabel("passed".equals(status) ? "\u2714" : ("failed".equals(status) ? "\u2716" : "\u23F3"), SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(bg);
        icon.setForeground(color);
        icon.setPreferredSize(new Dimension(34, 34));
        icon.setFont(new Font("Dialog", Font.BOLD, 12));
        row.add(icon, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel exam = new JLabel(safe(evaluation.getExamName(), "Avaliacao"));
        exam.setForeground(TITLE);
        exam.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel meta = new JLabel(formatDate(evaluation.getEvaluationDate()) + " | " + typeLabel(evaluation));
        meta.setForeground(SOFT);
        meta.setFont(new Font("Inter", Font.PLAIN, 10));
        details.add(exam);
        details.add(meta);
        row.add(details, BorderLayout.CENTER);

        JLabel score = new JLabel(scoreLabel(evaluation), SwingConstants.RIGHT);
        score.setForeground(scoreColor(evaluation));
        score.setFont(new Font("Inter", Font.BOLD, 15));
        row.add(score, BorderLayout.EAST);
        return row;
    }

    private JPanel createScoreBar(String subject, int value) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel name = new JLabel(subject);
        name.setForeground(MUTED);
        name.setFont(new Font("Inter", Font.PLAIN, 11));
        JLabel score = new JLabel(String.valueOf(value));
        score.setForeground(scoreToColor(value));
        score.setFont(new Font("Inter", Font.BOLD, 12));
        top.add(name, BorderLayout.WEST);
        top.add(score, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(value);
        bar.setForeground(scoreToColor(value));
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(180, 8));

        panel.add(top);
        panel.add(Box.createVerticalStrut(4));
        panel.add(bar);
        return panel;
    }

    private JPanel createUpcomingRow(Evaluation evaluation) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(true);
        row.setBackground(new Color(248, 250, 252));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel icon = new JLabel(typeIcon(evaluation), SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(typeBg(evaluation));
        icon.setForeground(typeColor(evaluation));
        icon.setPreferredSize(new Dimension(32, 32));
        row.add(icon, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel exam = new JLabel(safe(evaluation.getExamName(), "Avaliacao"));
        exam.setForeground(TITLE);
        exam.setFont(new Font("Inter", Font.BOLD, 11));
        JLabel info = new JLabel(formatDate(evaluation.getEvaluationDate()) + " | " + typeLabel(evaluation));
        info.setForeground(MUTED);
        info.setFont(new Font("Inter", Font.PLAIN, 10));
        details.add(exam);
        details.add(info);
        row.add(details, BorderLayout.CENTER);
        return row;
    }

    private JPanel createResultBar(Evaluation evaluation) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        int score = evaluation.getScore() != null ? evaluation.getScore() : 0;

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel name = new JLabel(shortExamName(evaluation));
        name.setForeground(new Color(55, 65, 81));
        name.setFont(new Font("Inter", Font.PLAIN, 11));
        JLabel value = new JLabel(String.valueOf(score));
        value.setForeground(scoreToColor(score));
        value.setFont(new Font("Inter", Font.BOLD, 12));
        top.add(name, BorderLayout.WEST);
        top.add(value, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(score);
        bar.setForeground(scoreToColor(score));
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(160, 8));

        panel.add(top);
        panel.add(Box.createVerticalStrut(4));
        panel.add(bar);
        return panel;
    }

    private JPanel createCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TITLE);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(SOFT);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        head.add(titleLabel);
        head.add(subtitleLabel);
        card.add(head, BorderLayout.NORTH);
        return card;
    }

    private Map<String, Integer> buildSubjectAverages() {
        Map<String, List<Integer>> bySubject = new LinkedHashMap<>();
        for (Evaluation evaluation : evaluations) {
            if (evaluation.getScore() == null) {
                continue;
            }
            String subject = shortExamName(evaluation);
            bySubject.computeIfAbsent(subject, k -> new ArrayList<>()).add(evaluation.getScore());
        }

        Map<String, Integer> average = new LinkedHashMap<>();
        bySubject.forEach((subject, scores) -> {
            int avg = (int) Math.round(scores.stream().mapToInt(Integer::intValue).average().orElse(0.0));
            average.put(subject, avg);
        });
        return average;
    }

    private String normalizeStatus(Evaluation evaluation) {
        String status = safe(evaluation.getStatus(), "").toLowerCase(Locale.ROOT);
        if ("passed".equals(status) || "failed".equals(status)) {
            return status;
        }
        if (evaluation.getScore() != null) {
            return evaluation.getScore() >= 75 ? "passed" : "failed";
        }
        return "pending";
    }

    private String typeLabel(Evaluation evaluation) {
        String type = safe(evaluation.getEvaluationType(), "theoretical").toLowerCase(Locale.ROOT);
        if (type.contains("sim")) {
            return "Simulador";
        }
        if (type.contains("prat") || type.contains("practical")) {
            return "Pratico";
        }
        return "Teorico";
    }

    private String typeIcon(Evaluation evaluation) {
        String label = typeLabel(evaluation);
        if ("Pratico".equals(label)) {
            return "\u2708";
        }
        if ("Simulador".equals(label)) {
            return "\u25A3";
        }
        return "\uD83D\uDCD6";
    }

    private Color typeBg(Evaluation evaluation) {
        String label = typeLabel(evaluation);
        if ("Pratico".equals(label)) {
            return new Color(220, 252, 231);
        }
        if ("Simulador".equals(label)) {
            return new Color(243, 232, 255);
        }
        return new Color(219, 234, 254);
    }

    private Color typeColor(Evaluation evaluation) {
        String label = typeLabel(evaluation);
        if ("Pratico".equals(label)) {
            return GREEN;
        }
        if ("Simulador".equals(label)) {
            return PURPLE;
        }
        return BLUE;
    }

    private String shortExamName(Evaluation evaluation) {
        String exam = safe(evaluation.getExamName(), "Avaliacao");
        int sep = exam.indexOf('-');
        return sep > 0 ? exam.substring(0, sep).trim() : exam;
    }

    private String scoreLabel(Evaluation evaluation) {
        return evaluation.getScore() != null ? evaluation.getScore() + "/100" : "-";
    }

    private Color scoreColor(Evaluation evaluation) {
        if (evaluation.getScore() == null) {
            return SOFT;
        }
        return scoreToColor(evaluation.getScore());
    }

    private Color scoreToColor(int score) {
        if (score >= 90) {
            return new Color(34, 197, 94);
        }
        if (score >= 75) {
            return BLUE;
        }
        return RED;
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FMT);
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
