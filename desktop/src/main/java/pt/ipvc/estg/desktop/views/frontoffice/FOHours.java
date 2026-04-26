package pt.ipvc.estg.desktop.views.frontoffice;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Minhas horas de voo no FrontOffice.
 */
public class FOHours extends JPanel {

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
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMM yy", Locale.forLanguageTag("pt-PT"));

    private final Student student;
    private final FlightController flightController;
    private final List<Flight> flights = new ArrayList<>();

    public FOHours(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        loadFlights();
        initializeUI();
    }

    private void loadFlights() {
        flights.clear();
        List<Flight> studentFlights = flightController.obterVoosPorEstudante(student.getId());
        if (studentFlights != null) {
            flights.addAll(studentFlights);
        }
        flights.sort(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(PAGE_BG);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createGoalCard());
        top.add(Box.createVerticalStrut(12));
        top.add(createStatsRow());
        add(top, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(createChartsRow());
        content.add(Box.createVerticalStrut(12));
        content.add(createFlightLogCard());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createGoalCard() {
        double requiredHours = 45.0;
        double totalHours = calculateCompletedHours();
        double remaining = Math.max(0, requiredHours - totalHours);
        int progress = requiredHours <= 0 ? 0 : (int) Math.min(100, Math.round((totalHours * 100.0) / requiredHours));

        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(TITLE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel small = new JLabel("Progresso em Horas de Voo");
        small.setForeground(new Color(147, 197, 253));
        small.setFont(new Font("Inter", Font.PLAIN, 12));
        card.add(small);

        JLabel hours = new JLabel(String.format(Locale.ROOT, "%.1fh", totalHours) + " de " + String.format(Locale.ROOT, "%.0fh", requiredHours));
        hours.setForeground(Color.WHITE);
        hours.setFont(new Font("Inter", Font.BOLD, 34));
        card.add(hours);

        JLabel percent = new JLabel(progress + "% concluido");
        percent.setForeground(new Color(134, 239, 172));
        percent.setFont(new Font("Inter", Font.BOLD, 12));
        card.add(percent);
        card.add(Box.createVerticalStrut(8));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(progress);
        bar.setForeground(new Color(52, 211, 153));
        bar.setBackground(new Color(59, 130, 246, 70));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(280, 11));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 11));
        card.add(bar);
        card.add(Box.createVerticalStrut(6));

        JLabel footer = new JLabel("Faltam " + String.format(Locale.ROOT, "%.1fh", remaining) + " para cumprir os requisitos minimos");
        footer.setForeground(new Color(191, 219, 254));
        footer.setFont(new Font("Inter", Font.PLAIN, 11));
        card.add(footer);
        return card;
    }

    private JPanel createStatsRow() {
        double totalHours = calculateCompletedHours();
        double localHours = flights.stream()
                .filter(f -> "completed".equals(normalizeStatus(f)))
                .filter(f -> "local".equals(normalizeType(f)))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
        double navHours = flights.stream()
                .filter(f -> "completed".equals(normalizeStatus(f)))
                .filter(f -> "navigation".equals(normalizeType(f)) || "ifr".equals(normalizeType(f)))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
        double monthlyAverage = totalHours / Math.max(1.0, 6.0);

        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.add(createStatCard("Total de Voos", String.valueOf(flights.size()), BLUE));
        row.add(createStatCard("Voos Locais", formatHours(localHours), GREEN));
        row.add(createStatCard("Navegacao", formatHours(navHours), PURPLE));
        row.add(createStatCard("Media Mensal", formatHours(monthlyAverage), ORANGE));
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
        valueLabel.setFont(new Font("Inter", Font.BOLD, 22));
        card.add(valueLabel);

        JLabel labelComp = new JLabel(label);
        labelComp.setForeground(MUTED);
        labelComp.setFont(new Font("Inter", Font.PLAIN, 12));
        card.add(labelComp);
        return card;
    }

    private JPanel createChartsRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        row.add(createCumulativeChartCard());
        row.add(createMonthlyChartCard());
        return row;
    }

    private JPanel createCumulativeChartCard() {
        JPanel card = createChartCard("Horas Acumuladas", "Evolucao desde o inicio do curso");

        List<YearMonth> months = buildLastMonths(6);
        Map<YearMonth, Double> monthlyTotals = completedHoursByMonth(months);

        double[] x = new double[months.size()];
        double[] cumulative = new double[months.size()];
        double acc = 0.0;
        for (int i = 0; i < months.size(); i++) {
            x[i] = i + 1;
            acc += monthlyTotals.getOrDefault(months.get(i), 0.0);
            cumulative[i] = acc;
        }

        XYChart chart = new XYChartBuilder()
                .width(520)
                .height(260)
                .title("")
                .xAxisTitle("")
                .yAxisTitle("")
                .build();

        styleXYChart(chart);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setXAxisMin(1.0);
        chart.getStyler().setXAxisMax((double) months.size());
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisDecimalPattern("##0.0h");

        XYSeries series = chart.addSeries("Horas", x, cumulative);
        series.setLineColor(BLUE);
        series.setFillColor(new Color(21, 101, 192, 44));
        series.setMarkerColor(BLUE);

        card.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return card;
    }

    private JPanel createMonthlyChartCard() {
        JPanel card = createChartCard("Horas por Mes", "Local vs Navegacao");

        List<YearMonth> months = buildLastMonths(6);
        Map<YearMonth, Double> local = completedHoursByMonthAndType(months, "local");
        Map<YearMonth, Double> nav = completedHoursByMonthAndType(months, "navigation_ifr");

        List<String> labels = new ArrayList<>();
        List<Double> localVals = new ArrayList<>();
        List<Double> navVals = new ArrayList<>();

        for (YearMonth month : months) {
            labels.add(month.format(MONTH_FMT));
            localVals.add(local.getOrDefault(month, 0.0));
            navVals.add(nav.getOrDefault(month, 0.0));
        }

        CategoryChart chart = new CategoryChartBuilder()
                .width(520)
                .height(260)
                .title("")
                .xAxisTitle("")
                .yAxisTitle("")
                .build();
        chart.getStyler().setChartBackgroundColor(WHITE);
        chart.getStyler().setPlotBackgroundColor(WHITE);
        chart.getStyler().setPlotGridLinesColor(new Color(241, 245, 249));
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setAxisTickLabelsColor(SOFT);
        chart.getStyler().setAxisTickLabelsFont(new Font("Inter", Font.PLAIN, 10));
        chart.getStyler().setLegendFont(new Font("Inter", Font.PLAIN, 10));
        chart.getStyler().setAvailableSpaceFill(.22);
        chart.getStyler().setYAxisDecimalPattern("##0.0h");

        chart.addSeries("Local", labels, localVals).setFillColor(BLUE);
        chart.addSeries("Navegacao", labels, navVals).setFillColor(new Color(66, 165, 245));

        card.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return card;
    }

    private JPanel createFlightLogCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JPanel header = new JPanel();
        header.setOpaque(true);
        header.setBackground(WHITE);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(12, 14, 10, 14));

        JLabel title = new JLabel("Registo de Horas de Voo");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        JLabel subtitle = new JLabel("Detalhe completo de cada missao");
        subtitle.setForeground(SOFT);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 11));

        header.add(title);
        header.add(subtitle);
        card.add(header, BorderLayout.NORTH);

        String[] cols = {"Data", "Aeronave", "Origem", "Destino", "Duracao", "Tipo", "Acumulado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Flight> completed = flights.stream()
                .filter(f -> "completed".equals(normalizeStatus(f)))
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        double cumulative = 0.0;
        for (int i = completed.size() - 1; i >= 0; i--) {
            Flight flight = completed.get(i);
            cumulative += flight.getDuration() != null ? flight.getDuration() : 0.0;
            model.insertRow(0, new Object[]{
                    flight.getFlightDate(),
                    flight.getAircraft() != null ? safe(flight.getAircraft().getRegistration(), "-") : "-",
                    safe(flight.getOrigin(), "-"),
                    safe(flight.getDestination(), "-"),
                    formatHours(flight.getDuration() != null ? flight.getDuration() : 0.0),
                    normalizeType(flight),
                    formatHours(cumulative)
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(42);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setIntercellSpacing(new Dimension(0, 1));

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setFont(new Font("Inter", Font.BOLD, 11));

        table.getColumnModel().getColumn(0).setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new TypeRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        card.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(true);
        footer.setBackground(new Color(248, 250, 252));
        footer.setBorder(new EmptyBorder(10, 12, 10, 12));
        JLabel left = new JLabel("Total: " + formatHours(calculateCompletedHours()));
        left.setForeground(new Color(55, 65, 81));
        left.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel right = new JLabel("Meta PPL: 45h");
        right.setForeground(SOFT);
        right.setFont(new Font("Inter", Font.PLAIN, 11));
        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createChartCard(String title, String subtitle) {
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

    private void styleXYChart(XYChart chart) {
        chart.getStyler().setChartBackgroundColor(WHITE);
        chart.getStyler().setPlotBackgroundColor(WHITE);
        chart.getStyler().setPlotGridLinesColor(new Color(241, 245, 249));
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setLegendBorderColor(WHITE);
        chart.getStyler().setAxisTickLabelsColor(SOFT);
        chart.getStyler().setAxisTickLabelsFont(new Font("Inter", Font.PLAIN, 10));
        chart.getStyler().setLegendFont(new Font("Inter", Font.PLAIN, 10));
        chart.getStyler().setMarkerSize(4);
    }

    private List<YearMonth> buildLastMonths(int count) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = count - 1; i >= 0; i--) {
            months.add(now.minusMonths(i));
        }
        return months;
    }

    private Map<YearMonth, Double> completedHoursByMonth(List<YearMonth> months) {
        Map<YearMonth, Double> values = new LinkedHashMap<>();
        for (YearMonth month : months) {
            values.put(month, 0.0);
        }
        for (Flight flight : flights) {
            if (!"completed".equals(normalizeStatus(flight)) || flight.getFlightDate() == null) {
                continue;
            }
            YearMonth ym = YearMonth.from(flight.getFlightDate());
            if (!values.containsKey(ym)) {
                continue;
            }
            values.put(ym, values.get(ym) + (flight.getDuration() != null ? flight.getDuration() : 0.0));
        }
        return values;
    }

    private Map<YearMonth, Double> completedHoursByMonthAndType(List<YearMonth> months, String typeMode) {
        Map<YearMonth, Double> values = new LinkedHashMap<>();
        for (YearMonth month : months) {
            values.put(month, 0.0);
        }
        for (Flight flight : flights) {
            if (!"completed".equals(normalizeStatus(flight)) || flight.getFlightDate() == null) {
                continue;
            }
            String type = normalizeType(flight);
            boolean matches = switch (typeMode) {
                case "local" -> "local".equals(type);
                case "navigation_ifr" -> "navigation".equals(type) || "ifr".equals(type);
                default -> true;
            };
            if (!matches) {
                continue;
            }
            YearMonth ym = YearMonth.from(flight.getFlightDate());
            if (!values.containsKey(ym)) {
                continue;
            }
            values.put(ym, values.get(ym) + (flight.getDuration() != null ? flight.getDuration() : 0.0));
        }
        return values;
    }

    private double calculateCompletedHours() {
        return flights.stream()
                .filter(f -> "completed".equals(normalizeStatus(f)))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    private String normalizeStatus(Flight flight) {
        String status = safe(flight.getStatus(), "scheduled").toLowerCase(Locale.ROOT);
        if (status.contains("complete")) {
            return "completed";
        }
        if (status.contains("cancel")) {
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
        return "local";
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

    private static class TypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));
            String type = String.valueOf(value);
            if ("navigation".equals(type) || "ifr".equals(type)) {
                label.setText("Navegacao");
                label.setBackground(new Color(220, 252, 231));
                label.setForeground(new Color(22, 163, 74));
            } else {
                label.setText("Local");
                label.setBackground(new Color(219, 234, 254));
                label.setForeground(new Color(29, 78, 216));
            }
            return label;
        }
    }
}
