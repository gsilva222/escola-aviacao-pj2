package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.dal.mock.AircraftDAOMock;
import pt.ipvc.estg.dal.mock.InstructorDAOMock;
import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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
 * Painel de voos com modo Lista/Calendário inspirado no mockup.
 */
public class BOFlights extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);    // #EEF2F7
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0

    private enum ViewMode { LIST, CALENDAR }

    private final FlightController flightController;
    private final StudentController studentController;
    private final InstructorDAOMock instructorDAO;
    private final AircraftDAOMock aircraftDAO;

    private final CardLayout centerLayout = new CardLayout();
    private final JPanel centerPanel = new JPanel(centerLayout);
    private final JPanel calendarPanel = new JPanel();
    private final JTable flightsTable;
    private final DefaultTableModel tableModel;

    private final JButton listBtn;
    private final JButton calendarBtn;
    private final JComboBox<String> statusFilter;

    private final JLabel totalScheduledValue;
    private final JLabel completedTodayValue;
    private final JLabel hoursTodayValue;

    private List<Flight> flights = new ArrayList<>();
    private ViewMode viewMode = ViewMode.LIST;

    public BOFlights() {
        this.flightController = new FlightController();
        this.studentController = new StudentController();
        this.instructorDAO = new InstructorDAOMock();
        this.aircraftDAO = new AircraftDAOMock();

        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"Data & Hora", "Aluno", "Instrutor", "Aeronave", "Rota", "Tipo", "Duração", "Estado"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(tableModel);
        styleTable(flightsTable);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(WHITE);
        listPanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        listPanel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);

        calendarPanel.setLayout(new BorderLayout());
        calendarPanel.setBackground(WHITE);
        calendarPanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        centerPanel.setOpaque(false);
        centerPanel.add(listPanel, "list");
        centerPanel.add(calendarPanel, "calendar");
        add(centerPanel, BorderLayout.CENTER);

        JPanel summary = new JPanel(new GridLayout(1, 3, 10, 0));
        summary.setOpaque(false);
        totalScheduledValue = new JLabel("0");
        completedTodayValue = new JLabel("0");
        hoursTodayValue = new JLabel("0.0h");
        summary.add(createSummaryCard("Total Agendados", totalScheduledValue, new Color(219, 234, 254), new Color(29, 78, 216)));
        summary.add(createSummaryCard("Concluídos Hoje", completedTodayValue, new Color(220, 252, 231), new Color(22, 163, 74)));
        summary.add(createSummaryCard("Horas Voadas Hoje", hoursTodayValue, new Color(243, 232, 255), new Color(124, 58, 237)));
        add(summary, BorderLayout.SOUTH);

        listBtn = (JButton) findByName(toolbar, "listBtn");
        calendarBtn = (JButton) findByName(toolbar, "calendarBtn");
        statusFilter = (JComboBox<String>) findByName(toolbar, "statusFilter");

        loadData();
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JButton list = new JButton("Lista");
        list.setName("listBtn");
        styleViewButton(list);
        list.addActionListener(e -> switchView(ViewMode.LIST));

        JButton calendar = new JButton("Calendário");
        calendar.setName("calendarBtn");
        styleViewButton(calendar);
        calendar.addActionListener(e -> switchView(ViewMode.CALENDAR));

        left.add(list);
        left.add(calendar);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JComboBox<String> status = new JComboBox<>(new String[]{"Todos os Estados", "scheduled", "completed", "cancelled"});
        status.setName("statusFilter");
        status.setFont(new Font("Inter", Font.PLAIN, 12));
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
        status.addActionListener(e -> refreshViews());
        right.add(status);

        JButton schedule = new JButton("Agendar Voo");
        schedule.setFont(new Font("Inter", Font.BOLD, 12));
        schedule.setForeground(Color.WHITE);
        schedule.setBackground(BLUE);
        schedule.setFocusPainted(false);
        schedule.setBorder(new EmptyBorder(8, 12, 8, 12));
        schedule.setCursor(new Cursor(Cursor.HAND_CURSOR));
        schedule.addActionListener(e -> scheduleFlightDialog());
        right.add(schedule);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        return toolbar;
    }

    private void styleViewButton(JButton button) {
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

    private void switchView(ViewMode mode) {
        this.viewMode = mode;
        centerLayout.show(centerPanel, mode == ViewMode.LIST ? "list" : "calendar");
        updateViewButtons();
    }

    private void updateViewButtons() {
        boolean listActive = viewMode == ViewMode.LIST;
        listBtn.setBackground(listActive ? BLUE : WHITE);
        listBtn.setForeground(listActive ? Color.WHITE : MUTED);
        listBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(listActive ? BLUE : BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        boolean calActive = viewMode == ViewMode.CALENDAR;
        calendarBtn.setBackground(calActive ? BLUE : WHITE);
        calendarBtn.setForeground(calActive ? Color.WHITE : MUTED);
        calendarBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(calActive ? BLUE : BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
    }

    private void loadData() {
        flights = new ArrayList<>(flightController.listarVoos());
        refreshViews();
        switchView(ViewMode.LIST);
    }

    private void refreshViews() {
        List<Flight> visible = getFilteredFlights();
        populateTable(visible);
        populateCalendar(visible);
        updateSummary(visible);
    }

    private List<Flight> getFilteredFlights() {
        String selected = (String) statusFilter.getSelectedItem();
        if (selected == null || "Todos os Estados".equals(selected)) {
            return new ArrayList<>(flights);
        }
        return flights.stream()
                .filter(f -> selected.equalsIgnoreCase(f.getStatus()))
                .toList();
    }

    private void populateTable(List<Flight> visible) {
        tableModel.setRowCount(0);
        List<Flight> sorted = visible.stream()
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM", new Locale("pt", "PT"));
        for (Flight f : sorted) {
            String date = f.getFlightDate() != null ? f.getFlightDate().format(dateFmt) : "N/A";
            String time = f.getFlightTime() != null ? f.getFlightTime().toString() : "--:--";
            tableModel.addRow(new Object[]{
                    date + " · " + time,
                    f.getStudent() != null ? f.getStudent().getName() : "N/A",
                    f.getInstructor() != null ? f.getInstructor().getName().replace("Capt. ", "") : "N/A",
                    f.getAircraft() != null ? f.getAircraft().getRegistration() : "N/A",
                    (f.getOrigin() != null ? f.getOrigin() : "OPO") + " -> " + (f.getDestination() != null ? f.getDestination() : "---"),
                    f.getFlightType() != null ? f.getFlightType() : "Local",
                    String.format(Locale.ROOT, "%.1fh", f.getDuration() != null ? f.getDuration() : 0),
                    f.getStatus() != null ? f.getStatus() : "scheduled"
            });
        }
    }

    private void populateCalendar(List<Flight> visible) {
        calendarPanel.removeAll();

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Calendário semanal");
        title.setFont(new Font("Inter", Font.BOLD, 14));
        title.setForeground(TITLE);
        wrapper.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 7, 8, 0));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(8, 0, 0, 0));

        LocalDate start = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1L);
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEE dd", new Locale("pt", "PT"));

        for (int i = 0; i < 7; i++) {
            LocalDate day = start.plusDays(i);
            JPanel dayCol = new JPanel(new BorderLayout(0, 6));
            dayCol.setOpaque(true);
            dayCol.setBackground(new Color(248, 250, 252));
            dayCol.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    new EmptyBorder(8, 8, 8, 8)
            ));

            JLabel dayLabel = new JLabel(day.format(dayFmt));
            dayLabel.setForeground(MUTED);
            dayLabel.setFont(new Font("Inter", Font.BOLD, 11));
            dayCol.add(dayLabel, BorderLayout.NORTH);

            JPanel list = new JPanel();
            list.setOpaque(false);
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

            List<Flight> dayFlights = visible.stream()
                    .filter(f -> day.equals(f.getFlightDate()))
                    .sorted(Comparator.comparing(Flight::getFlightTime, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();

            if (dayFlights.isEmpty()) {
                JLabel empty = new JLabel("Sem voos");
                empty.setForeground(new Color(148, 163, 184));
                empty.setFont(new Font("Inter", Font.PLAIN, 10));
                list.add(empty);
            } else {
                for (Flight f : dayFlights) {
                    list.add(createFlightPill(f));
                    list.add(Box.createVerticalStrut(6));
                }
            }

            dayCol.add(list, BorderLayout.CENTER);
            grid.add(dayCol);
        }

        wrapper.add(grid, BorderLayout.CENTER);
        calendarPanel.add(wrapper, BorderLayout.CENTER);
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    private JComponent createFlightPill(Flight flight) {
        Color bg;
        Color fg;
        String status = flight.getStatus() != null ? flight.getStatus() : "scheduled";
        switch (status) {
            case "completed" -> {
                bg = new Color(220, 252, 231);
                fg = new Color(22, 163, 74);
            }
            case "cancelled" -> {
                bg = new Color(254, 226, 226);
                fg = new Color(220, 38, 38);
            }
            default -> {
                bg = new Color(219, 234, 254);
                fg = new Color(29, 78, 216);
            }
        }

        JPanel pill = new JPanel(new BorderLayout());
        pill.setOpaque(true);
        pill.setBackground(bg);
        pill.setBorder(new EmptyBorder(5, 6, 5, 6));

        String time = flight.getFlightTime() != null ? flight.getFlightTime().toString() : "--:--";
        String reg = flight.getAircraft() != null ? flight.getAircraft().getRegistration() : "N/A";
        String student = flight.getStudent() != null ? firstName(flight.getStudent().getName()) : "Aluno";

        JLabel top = new JLabel(time + " · " + reg);
        top.setForeground(fg);
        top.setFont(new Font("Inter", Font.BOLD, 10));

        JLabel bottom = new JLabel(student);
        bottom.setForeground(fg);
        bottom.setFont(new Font("Inter", Font.PLAIN, 10));

        pill.add(top, BorderLayout.NORTH);
        pill.add(bottom, BorderLayout.SOUTH);
        return pill;
    }

    private void updateSummary(List<Flight> visible) {
        long scheduled = visible.stream().filter(f -> "scheduled".equalsIgnoreCase(f.getStatus())).count();
        long completedToday = visible.stream()
                .filter(f -> LocalDate.now().equals(f.getFlightDate()) && "completed".equalsIgnoreCase(f.getStatus()))
                .count();
        double hoursToday = visible.stream()
                .filter(f -> LocalDate.now().equals(f.getFlightDate()))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();

        totalScheduledValue.setText(String.valueOf(scheduled));
        completedTodayValue.setText(String.valueOf(completedToday));
        hoursTodayValue.setText(String.format(Locale.ROOT, "%.1fh", hoursToday));
    }

    private JPanel createSummaryCard(String label, JLabel valueLabel, Color badgeBg, Color badgeFg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel icon = new JLabel("VO", SwingConstants.CENTER);
        icon.setOpaque(true);
        icon.setBackground(badgeBg);
        icon.setForeground(badgeFg);
        icon.setFont(new Font("Inter", Font.BOLD, 10));
        icon.setPreferredSize(new Dimension(30, 30));
        card.add(icon, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        valueLabel.setForeground(badgeFg);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 18));
        JLabel subtitle = new JLabel(label);
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 11));
        text.add(valueLabel);
        text.add(subtitle);
        card.add(text, BorderLayout.CENTER);

        return card;
    }

    private void scheduleFlightDialog() {
        JComboBox<Student> studentCombo = new JComboBox<>(studentController.listarEstudantes().toArray(new Student[0]));
        studentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Student student = (Student) value;
                return super.getListCellRendererComponent(list, student != null ? student.getName() : "", index, isSelected, cellHasFocus);
            }
        });

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Local", "Navigation", "IFR"});
        JTextField dateField = new JTextField(LocalDate.now().toString());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Aluno"));
        form.add(studentCombo);
        form.add(new JLabel("Tipo"));
        form.add(typeCombo);
        form.add(new JLabel("Data (yyyy-mm-dd)"));
        form.add(dateField);

        int result = JOptionPane.showConfirmDialog(this, form, "Agendar Voo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Student student = (Student) studentCombo.getSelectedItem();
                if (student == null) {
                    throw new IllegalArgumentException("Selecione um aluno.");
                }
                LocalDate date = LocalDate.parse(dateField.getText().trim());

                Instructor instructor = instructorDAO.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Sem instrutores disponíveis."));
                Aircraft aircraft = aircraftDAO.findAll().stream()
                        .filter(a -> "operational".equalsIgnoreCase(a.getStatus()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Sem aeronaves operacionais."));

                flightController.criarVoo(date, student, instructor, aircraft);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao agendar voo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(46);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TITLE);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 11));

        table.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new RegistrationRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new StudentNameRenderer());
    }

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Inter", Font.BOLD, 11));
            String status = String.valueOf(value);
            switch (status) {
                case "completed" -> {
                    label.setText("Concluído");
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(new Color(22, 163, 74));
                }
                case "cancelled" -> {
                    label.setText("Cancelado");
                    label.setBackground(new Color(254, 226, 226));
                    label.setForeground(new Color(220, 38, 38));
                }
                default -> {
                    label.setText("Agendado");
                    label.setBackground(new Color(219, 234, 254));
                    label.setForeground(new Color(29, 78, 216));
                }
            }
            return label;
        }
    }

    private static class RegistrationRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setForeground(BLUE);
            label.setFont(new Font("Monospaced", Font.BOLD, 12));
            return label;
        }
    }

    private static class StudentNameRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : WHITE);

            String fullName = String.valueOf(value);
            String initials = initials(fullName);

            JLabel avatar = new JLabel(initials, SwingConstants.CENTER);
            avatar.setOpaque(true);
            avatar.setBackground(BLUE);
            avatar.setForeground(Color.WHITE);
            avatar.setFont(new Font("Inter", Font.BOLD, 10));
            avatar.setPreferredSize(new Dimension(26, 26));

            JLabel name = new JLabel(fullName);
            name.setForeground(new Color(55, 65, 81));
            name.setFont(new Font("Inter", Font.PLAIN, 12));

            panel.add(avatar);
            panel.add(name);
            return panel;
        }

        private String initials(String name) {
            if (name == null || name.isBlank()) {
                return "AL";
            }
            String[] parts = name.trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
            }
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }
    }

    private static String firstName(String name) {
        if (name == null || name.isBlank()) {
            return "Aluno";
        }
        int i = name.indexOf(' ');
        return i > 0 ? name.substring(0, i) : name;
    }

    private static Component findByName(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (name.equals(c.getName())) {
                return c;
            }
            if (c instanceof Container child) {
                Component found = findByName(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
