package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Historico e proximos voos do aluno.
 */
public class FOFlights extends JPanel {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color SOFT = new Color(148, 163, 184);       // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0
    private static final Color GREEN = new Color(5, 150, 105);        // #059669
    private static final Color PURPLE = new Color(124, 58, 237);      // #7C3AED
    private static final Color ORANGE = new Color(217, 119, 6);       // #D97706

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Student student;
    private final FlightController flightController;

    private final List<Flight> allFlights = new ArrayList<>();
    private final List<Flight> upcomingFlights = new ArrayList<>();
    private final List<Flight> historyFlights = new ArrayList<>();
    private List<Flight> filteredHistory = new ArrayList<>();

    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private JPanel upcomingListPanel;
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private JButton upcomingTabButton;
    private JButton historyTabButton;
    private final CardLayout contentCards = new CardLayout();
    private final JPanel contentPanel = new JPanel(contentCards);

    public FOFlights(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        loadFlights();
        initializeUI();
    }

    private void loadFlights() {
        allFlights.clear();
        List<Flight> flights = flightController.obterVoosPorEstudante(student.getId());
        if (flights != null) {
            allFlights.addAll(flights);
        }

        upcomingFlights.clear();
        historyFlights.clear();

        for (Flight flight : allFlights) {
            if (isUpcoming(flight)) {
                upcomingFlights.add(flight);
            } else {
                historyFlights.add(flight);
            }
        }

        upcomingFlights.sort(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder())));
        historyFlights.sort(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.reverseOrder())));
        filteredHistory = new ArrayList<>(historyFlights);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createStatsRow());
        top.add(Box.createVerticalStrut(12));
        top.add(createTabsPanel());
        add(top, BorderLayout.NORTH);

        contentPanel.setOpaque(false);
        contentPanel.add(createUpcomingView(), "upcoming");
        contentPanel.add(createHistoryView(), "history");
        add(contentPanel, BorderLayout.CENTER);

        switchTab("upcoming");
    }

    private JPanel createStatsRow() {
        double totalHours = allFlights.stream()
                .filter(f -> "completed".equals(normalizeStatus(f)))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();

        double localHours = allFlights.stream()
                .filter(f -> "local".equals(normalizeType(f)))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();

        double navHours = allFlights.stream()
                .filter(f -> "navigation".equals(normalizeType(f)) || "ifr".equals(normalizeType(f)))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();

        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.add(createStatCard("Total de Voos", String.valueOf(allFlights.size()), BLUE));
        row.add(createStatCard("Horas Totais", formatHours(totalHours), PURPLE));
        row.add(createStatCard("Voos Locais", formatHours(localHours), GREEN));
        row.add(createStatCard("Navegacao", formatHours(navHours), ORANGE));
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

        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(MUTED);
        labelComp.setFont(new Font("Inter", Font.PLAIN, 12));
        card.add(labelComp);
        return card;
    }

    private JPanel createTabsPanel() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        tabs.setOpaque(true);
        tabs.setBackground(WHITE);
        tabs.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        upcomingTabButton = new JButton("Proximos Voos (" + upcomingFlights.size() + ")");
        upcomingTabButton.addActionListener(e -> switchTab("upcoming"));
        tabs.add(upcomingTabButton);

        historyTabButton = new JButton("Historico (" + historyFlights.size() + ")");
        historyTabButton.addActionListener(e -> switchTab("history"));
        tabs.add(historyTabButton);

        wrapper.add(tabs);
        return wrapper;
    }

    private JPanel createUpcomingView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        upcomingListPanel = new JPanel();
        upcomingListPanel.setOpaque(false);
        upcomingListPanel.setLayout(new BoxLayout(upcomingListPanel, BoxLayout.Y_AXIS));

        if (upcomingFlights.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(true);
            empty.setBackground(WHITE);
            empty.setBorder(BorderFactory.createLineBorder(BORDER, 1));
            JLabel text = new JLabel("Sem voos agendados.");
            text.setForeground(SOFT);
            text.setFont(new Font("Inter", Font.PLAIN, 13));
            empty.add(text);
            upcomingListPanel.add(empty);
        } else {
            for (int i = 0; i < upcomingFlights.size(); i++) {
                upcomingListPanel.add(createUpcomingCard(upcomingFlights.get(i)));
                if (i < upcomingFlights.size() - 1) {
                    upcomingListPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(upcomingListPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUpcomingCard(Flight flight) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel left = new JPanel(new BorderLayout(10, 0));
        left.setOpaque(false);

        JPanel icon = new JPanel(new GridBagLayout());
        icon.setOpaque(true);
        icon.setBackground(new Color(219, 234, 254));
        icon.setPreferredSize(new Dimension(46, 46));
        JLabel plane = new JLabel("\u2708");
        plane.setForeground(new Color(29, 78, 216));
        plane.setFont(new Font("Dialog", Font.PLAIN, 18));
        icon.add(plane);
        left.add(icon, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel objective = new JLabel(safe(flight.getObjectives(), "Sessao de voo"));
        objective.setForeground(TITLE);
        objective.setFont(new Font("Inter", Font.BOLD, 13));

        JLabel date = new JLabel(buildUpcomingInfo(flight));
        date.setForeground(MUTED);
        date.setFont(new Font("Inter", Font.PLAIN, 11));

        JLabel route = new JLabel(buildRoute(flight));
        route.setForeground(SOFT);
        route.setFont(new Font("Inter", Font.PLAIN, 11));

        details.add(objective);
        details.add(date);
        details.add(route);
        left.add(details, BorderLayout.CENTER);

        card.add(left, BorderLayout.CENTER);

        JButton detailsBtn = new JButton("Ver detalhes");
        detailsBtn.setFont(new Font("Inter", Font.PLAIN, 11));
        detailsBtn.setForeground(MUTED);
        detailsBtn.setBackground(WHITE);
        detailsBtn.setFocusPainted(false);
        detailsBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(7, 10, 7, 10)
        ));
        detailsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        detailsBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Voo em " + formatDate(flight.getFlightDate()) + "\n"
                        + "Aeronave: " + safeAircraft(flight) + "\n"
                        + "Instrutor: " + safeInstructor(flight),
                "Detalhes do Voo",
                JOptionPane.INFORMATION_MESSAGE
        ));
        card.add(detailsBtn, BorderLayout.EAST);
        return card;
    }

    private JPanel createHistoryView() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel filters = new JPanel(new BorderLayout(8, 0));
        filters.setOpaque(true);
        filters.setBackground(WHITE);
        filters.setBorder(BorderFactory.createCompoundBorder(
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
                applyHistoryFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyHistoryFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyHistoryFilters();
            }
        });
        filters.add(searchField, BorderLayout.CENTER);

        typeFilter = new JComboBox<>(new String[]{"all", "local", "navigation", "ifr"});
        typeFilter.setFont(new Font("Inter", Font.PLAIN, 12));
        typeFilter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        typeFilter.addActionListener(e -> applyHistoryFilters());
        filters.add(typeFilter, BorderLayout.EAST);
        panel.add(filters, BorderLayout.NORTH);

        String[] cols = {"Data", "Aeronave", "Tipo", "Rota", "Duracao", "Objetivos", "Nota"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(historyModel);
        historyTable.setRowHeight(44);
        historyTable.setShowVerticalLines(false);
        historyTable.setGridColor(new Color(241, 245, 249));
        historyTable.setIntercellSpacing(new Dimension(0, 1));
        historyTable.setSelectionBackground(new Color(239, 246, 255));
        historyTable.setSelectionForeground(TITLE);

        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.getTableHeader().setBackground(new Color(248, 250, 252));
        historyTable.getTableHeader().setForeground(MUTED);
        historyTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 11));

        historyTable.getColumnModel().getColumn(0).setCellRenderer(new DateRenderer());
        historyTable.getColumnModel().getColumn(2).setCellRenderer(new TypeBadgeRenderer());
        historyTable.getColumnModel().getColumn(6).setCellRenderer(new GradeRenderer());

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollPane.getViewport().setBackground(WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        applyHistoryFilters();
        return panel;
    }

    private void applyHistoryFilters() {
        String search = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedType = typeFilter == null ? "all" : String.valueOf(typeFilter.getSelectedItem());

        filteredHistory = historyFlights.stream()
                .filter(flight -> {
                    String objective = safe(flight.getObjectives(), "").toLowerCase(Locale.ROOT);
                    String aircraft = safeAircraft(flight).toLowerCase(Locale.ROOT);
                    String type = normalizeType(flight);
                    boolean matchesSearch = search.isEmpty() || objective.contains(search) || aircraft.contains(search);
                    boolean matchesType = "all".equals(selectedType) || selectedType.equals(type);
                    return matchesSearch && matchesType;
                })
                .toList();

        historyModel.setRowCount(0);
        for (Flight flight : filteredHistory) {
            historyModel.addRow(new Object[]{
                    flight.getFlightDate(),
                    safeAircraft(flight),
                    normalizeType(flight),
                    buildRoute(flight),
                    formatHours(flight.getDuration() != null ? flight.getDuration() : 0.0),
                    safe(flight.getObjectives(), "-"),
                    safe(flight.getGrade(), "-")
            });
        }
    }

    private void switchTab(String tab) {
        contentCards.show(contentPanel, tab);
        boolean upcoming = "upcoming".equals(tab);

        styleTabButton(upcomingTabButton, upcoming);
        styleTabButton(historyTabButton, !upcoming);
    }

    private void styleTabButton(JButton button, boolean active) {
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Inter", active ? Font.BOLD : Font.PLAIN, 12));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setOpaque(true);
        button.setForeground(active ? Color.WHITE : MUTED);
        button.setBackground(active ? BLUE : WHITE);
    }

    private boolean isUpcoming(Flight flight) {
        String status = normalizeStatus(flight);
        LocalDate date = flight.getFlightDate();
        if ("scheduled".equals(status)) {
            return true;
        }
        if ("completed".equals(status) || "cancelled".equals(status)) {
            return false;
        }
        return date != null && !date.isBefore(LocalDate.now());
    }

    private String normalizeStatus(Flight flight) {
        String status = safe(flight.getStatus(), "scheduled").toLowerCase(Locale.ROOT);
        if ("completed".equals(status)) {
            return "completed";
        }
        if ("cancelled".equals(status)) {
            return "cancelled";
        }
        return "scheduled";
    }

    private String normalizeType(Flight flight) {
        String type = safe(flight.getFlightType(), "local").toLowerCase(Locale.ROOT);
        if (type.contains("ifr")) {
            return "ifr";
        }
        if (type.contains("nav")) {
            return "navigation";
        }
        if (type.contains("loc")) {
            return "local";
        }
        return type;
    }

    private String buildRoute(Flight flight) {
        return safe(flight.getOrigin(), "---") + " -> " + safe(flight.getDestination(), "---");
    }

    private String buildUpcomingInfo(Flight flight) {
        String date = formatDate(flight.getFlightDate());
        String time = flight.getFlightTime() != null ? flight.getFlightTime().toString() : "09:00";
        return date + " as " + time + " | " + safeAircraft(flight) + " | " + safeInstructor(flight);
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

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FMT);
    }

    private String formatHours(double value) {
        return String.format(Locale.ROOT, "%.1fh", value);
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static class DateRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof LocalDate date) {
                label.setText(date.format(DATE_FMT));
                label.setForeground(new Color(55, 65, 81));
            } else {
                label.setText("-");
                label.setForeground(new Color(148, 163, 184));
            }
            label.setFont(new Font("Inter", Font.PLAIN, 11));
            return label;
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
                case "navigation" -> {
                    label.setText("Navegacao");
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(new Color(22, 163, 74));
                }
                case "ifr" -> {
                    label.setText("IFR");
                    label.setBackground(new Color(243, 232, 255));
                    label.setForeground(new Color(124, 58, 237));
                }
                default -> {
                    label.setText("Local");
                    label.setBackground(new Color(219, 234, 254));
                    label.setForeground(new Color(29, 78, 216));
                }
            }
            return label;
        }
    }

    private static class GradeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            String grade = String.valueOf(value);
            if (grade == null || grade.isBlank() || "-".equals(grade)) {
                label.setText("-");
                label.setBackground(Color.WHITE);
                label.setForeground(new Color(148, 163, 184));
                return label;
            }

            if ("A".equalsIgnoreCase(grade) || "A+".equalsIgnoreCase(grade)) {
                label.setBackground(new Color(220, 252, 231));
                label.setForeground(new Color(22, 163, 74));
            } else if ("B".equalsIgnoreCase(grade) || "B+".equalsIgnoreCase(grade)) {
                label.setBackground(new Color(219, 234, 254));
                label.setForeground(new Color(29, 78, 216));
            } else {
                label.setBackground(new Color(254, 243, 199));
                label.setForeground(new Color(217, 119, 6));
            }
            label.setText(grade);
            return label;
        }
    }
}
