package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Horario semanal do aluno.
 */
public class FOSchedule extends JPanel {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color SOFT = new Color(148, 163, 184);       // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0

    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("dd MMM", Locale.forLanguageTag("pt-PT"));
    private static final DateTimeFormatter RANGE_LABEL = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("pt-PT"));

    private final Student student;
    private final FlightController flightController;
    private final List<Flight> flights = new ArrayList<>();

    private int weekOffset = 0;
    private JLabel weekLabel;
    private JPanel daysGridPanel;

    public FOSchedule(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        loadFlights();
        initializeUI();
        refreshWeekGrid();
    }

    private void loadFlights() {
        flights.clear();
        List<Flight> studentFlights = flightController.obterVoosPorEstudante(student.getId());
        if (studentFlights != null) {
            flights.addAll(studentFlights);
        }
        flights.sort(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Flight::getFlightTime, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createHeader());
        top.add(Box.createVerticalStrut(10));
        top.add(createLegend());
        add(top, BorderLayout.NORTH);

        daysGridPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        daysGridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(daysGridPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Horario Semanal");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 22));
        left.add(title);

        String course = student.getCourse() != null && student.getCourse().getName() != null
                ? student.getCourse().getName()
                : "Curso";
        String instructor = student.getInstructor() != null && student.getInstructor().getName() != null
                ? student.getInstructor().getName()
                : "A definir";
        JLabel subtitle = new JLabel(course + " | Instrutor: " + instructor);
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 12));
        left.add(subtitle);

        panel.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton prevButton = createNavButton("<");
        prevButton.addActionListener(e -> {
            weekOffset--;
            refreshWeekGrid();
        });
        right.add(prevButton);

        weekLabel = new JLabel();
        weekLabel.setOpaque(true);
        weekLabel.setBackground(WHITE);
        weekLabel.setForeground(TITLE);
        weekLabel.setFont(new Font("Inter", Font.BOLD, 12));
        weekLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(7, 12, 7, 12)
        ));
        right.add(weekLabel);

        JButton nextButton = createNavButton(">");
        nextButton.addActionListener(e -> {
            weekOffset++;
            refreshWeekGrid();
        });
        right.add(nextButton);

        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel createLegend() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);
        panel.add(createLegendBadge("Local", new Color(219, 234, 254), new Color(29, 78, 216)));
        panel.add(createLegendBadge("Navegacao", new Color(220, 252, 231), new Color(22, 163, 74)));
        panel.add(createLegendBadge("IFR", new Color(243, 232, 255), new Color(124, 58, 237)));
        panel.add(createLegendBadge("Briefing", new Color(254, 243, 199), new Color(217, 119, 6)));
        panel.add(createLegendBadge("Exame", new Color(254, 226, 226), new Color(220, 38, 38)));
        return panel;
    }

    private JLabel createLegendBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setFont(new Font("Inter", Font.BOLD, 11));
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));
        return badge;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return button;
    }

    private void refreshWeekGrid() {
        LocalDate monday = startOfWeek(LocalDate.now()).plusWeeks(weekOffset);
        LocalDate friday = monday.plusDays(4);
        weekLabel.setText(monday.format(RANGE_LABEL) + " - " + friday.format(RANGE_LABEL));

        Map<LocalDate, List<Flight>> byDate = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            byDate.put(monday.plusDays(i), new ArrayList<>());
        }

        for (Flight flight : flights) {
            if (flight.getFlightDate() == null || !byDate.containsKey(flight.getFlightDate()) || !isScheduled(flight)) {
                continue;
            }
            byDate.get(flight.getFlightDate()).add(flight);
        }
        byDate.values().forEach(list -> list.sort(Comparator.comparing(Flight::getFlightTime, Comparator.nullsLast(Comparator.naturalOrder()))));

        daysGridPanel.removeAll();
        for (Map.Entry<LocalDate, List<Flight>> entry : byDate.entrySet()) {
            daysGridPanel.add(createDayColumn(entry.getKey(), entry.getValue()));
        }
        daysGridPanel.revalidate();
        daysGridPanel.repaint();
    }

    private JPanel createDayColumn(LocalDate day, List<Flight> dayFlights) {
        JPanel column = new JPanel(new BorderLayout(0, 6));
        column.setOpaque(false);

        JPanel head = new JPanel();
        head.setOpaque(true);
        head.setBackground(WHITE);
        head.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 6, 8, 6)
        ));
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));

        JLabel weekday = new JLabel(day.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.forLanguageTag("pt-PT")), SwingConstants.CENTER);
        weekday.setForeground(SOFT);
        weekday.setFont(new Font("Inter", Font.BOLD, 10));
        weekday.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel date = new JLabel(day.format(DATE_LABEL), SwingConstants.CENTER);
        date.setForeground(TITLE);
        date.setFont(new Font("Inter", Font.BOLD, 12));
        date.setAlignmentX(Component.CENTER_ALIGNMENT);
        head.add(weekday);
        head.add(date);

        column.add(head, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        if (dayFlights.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(true);
            empty.setBackground(WHITE);
            empty.setBorder(BorderFactory.createLineBorder(BORDER, 1));
            empty.setPreferredSize(new Dimension(180, 90));
            JLabel emptyText = new JLabel("Sem aulas");
            emptyText.setForeground(new Color(203, 213, 225));
            emptyText.setFont(new Font("Inter", Font.PLAIN, 11));
            empty.add(emptyText);
            body.add(empty);
        } else {
            for (int i = 0; i < dayFlights.size(); i++) {
                body.add(createFlightItem(dayFlights.get(i)));
                if (i < dayFlights.size() - 1) {
                    body.add(Box.createVerticalStrut(6));
                }
            }
        }

        column.add(body, BorderLayout.CENTER);
        return column;
    }

    private JPanel createFlightItem(Flight flight) {
        String type = normalizeType(flight);
        Color bg = switch (type) {
            case "navigation" -> new Color(220, 252, 231);
            case "ifr" -> new Color(243, 232, 255);
            default -> new Color(219, 234, 254);
        };
        Color fg = switch (type) {
            case "navigation" -> new Color(22, 163, 74);
            case "ifr" -> new Color(124, 58, 237);
            default -> new Color(29, 78, 216);
        };
        String typeLabel = switch (type) {
            case "navigation" -> "Navegacao";
            case "ifr" -> "IFR";
            default -> "Local";
        };

        JPanel item = new JPanel();
        item.setOpaque(true);
        item.setBackground(bg);
        item.setLayout(new BoxLayout(item, BoxLayout.Y_AXIS));
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg.brighter(), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel badge = new JLabel(typeLabel);
        badge.setForeground(fg);
        badge.setFont(new Font("Inter", Font.BOLD, 10));

        JLabel objective = new JLabel(safe(flight.getObjectives(), "Sessao de voo"));
        objective.setForeground(TITLE);
        objective.setFont(new Font("Inter", Font.BOLD, 11));

        String time = flight.getFlightTime() != null ? flight.getFlightTime().toString() : "09:00";
        String duration = flight.getDuration() != null ? String.format(Locale.ROOT, "%.1fh", flight.getDuration()) : "1.5h";
        JLabel info = new JLabel(time + " | " + duration);
        info.setForeground(MUTED);
        info.setFont(new Font("Inter", Font.PLAIN, 10));

        String room = flight.getAircraft() != null && flight.getAircraft().getRegistration() != null
                ? flight.getAircraft().getRegistration()
                : "A definir";
        JLabel place = new JLabel(room + " | " + safeInstructor(flight));
        place.setForeground(MUTED);
        place.setFont(new Font("Inter", Font.PLAIN, 10));

        item.add(badge);
        item.add(objective);
        item.add(info);
        item.add(place);
        return item;
    }

    private LocalDate startOfWeek(LocalDate date) {
        int delta = date.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (delta < 0) {
            delta += 7;
        }
        return date.minusDays(delta);
    }

    private boolean isScheduled(Flight flight) {
        String status = safe(flight.getStatus(), "scheduled").toLowerCase(Locale.ROOT);
        return !status.contains("complete") && !status.contains("cancel");
    }

    private String normalizeType(Flight flight) {
        String type = safe(flight.getFlightType(), "local").toLowerCase(Locale.ROOT);
        if (type.contains("ifr")) {
            return "ifr";
        }
        if (type.contains("nav")) {
            return "navigation";
        }
        return "local";
    }

    private String safeInstructor(Flight flight) {
        return flight.getInstructor() != null && flight.getInstructor().getName() != null
                ? flight.getInstructor().getName()
                : "A definir";
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
