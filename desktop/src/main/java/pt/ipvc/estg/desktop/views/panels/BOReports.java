package pt.ipvc.estg.desktop.views.panels;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import pt.ipvc.estg.desktop.api.dto.ReportsSummaryResponse;
import pt.ipvc.estg.desktop.services.BoAdminService;
import pt.ipvc.estg.desktop.util.PdfExportHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Painel de relatorios com dados da API e exportacao PDF real.
 */
public class BOReports extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TITLE = new Color(15, 35, 68);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color SOFT = new Color(148, 163, 184);
    private static final Color BLUE = new Color(21, 101, 192);
    private static final Color BLUE_DARK = new Color(13, 71, 161);
    private static final Color GREEN = new Color(22, 163, 74);
    private static final Color PURPLE = new Color(124, 58, 237);
    private static final Color ORANGE = new Color(217, 119, 6);
    private static final Color RED = new Color(220, 38, 38);

    private final BoAdminService adminService = new BoAdminService();

    private final Map<String, JButton> periodButtons = new LinkedHashMap<>();
    private String selectedPeriod = "Jan-Mar " + LocalDate.now().getYear();

    private JLabel kpiStudents;
    private JLabel kpiHours;
    private JLabel kpiPassRate;
    private JLabel kpiRevenue;
    private JPanel chartsGrid;

    private String cachedStudents = "-";
    private String cachedHours = "-";
    private String cachedPassRate = "-";
    private String cachedRevenue = "-";

    public BOReports() {
        initializeUI();
        loadReportsData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        setBackground(PAGE_BG);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createHeaderControls());
        top.add(Box.createVerticalStrut(12));
        top.add(createKPIRow());
        add(top, BorderLayout.NORTH);

        chartsGrid = createChartsGrid();
        JScrollPane scrollPane = new JScrollPane(chartsGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadReportsData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            ReportsSummaryResponse summary;
            LocalDate from;
            LocalDate to;
            double hours;
            double revenue;
            int passRate;

            @Override
            protected Void doInBackground() {
                LocalDate[] range = BoAdminService.periodRange(selectedPeriod);
                from = range[0];
                to = range[1];
                try {
                    summary = adminService.getReportsSummary();
                    hours = adminService.flightHoursInPeriod(from, to);
                    revenue = adminService.paidRevenueInPeriod(from, to);
                    passRate = adminService.passRatePercent(from, to);
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar relatorios: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                applyReportsData(summary, from, to, hours, revenue, passRate);
            }
        };
        worker.execute();
    }

    private void applyReportsData(ReportsSummaryResponse summary, LocalDate from, LocalDate to,
                                  double hours, double revenue, int passRate) {
        long totalStudents = summary != null ? summary.totalStudents() : 0;
        cachedStudents = String.valueOf(totalStudents);
        cachedHours = String.format(Locale.ROOT, "%.0fh", hours);
        cachedPassRate = passRate + "%";
        cachedRevenue = formatEuro(revenue);

        kpiStudents.setText(cachedStudents);
        kpiHours.setText(cachedHours);
        kpiPassRate.setText(cachedPassRate);
        kpiRevenue.setText(cachedRevenue);

        rebuildCharts(summary, from, to);
    }

    private String formatEuro(double value) {
        if (value >= 1000) {
            return String.format(Locale.ROOT, "EUR %.0fk", value / 1000.0);
        }
        return String.format(Locale.ROOT, "EUR %.0f", value);
    }

    private JPanel createHeaderControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel periods = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        periods.setOpaque(false);

        int year = LocalDate.now().getYear();
        addPeriodButton(periods, "Jan-Mar " + year);
        addPeriodButton(periods, "2024");
        addPeriodButton(periods, "Personalizado");
        updatePeriodButtons();

        panel.add(periods, BorderLayout.WEST);

        JButton exportBtn = new JButton("Exportar Relatorio PDF");
        stylePrimaryButton(exportBtn);
        exportBtn.addActionListener(e -> exportPDF());
        panel.add(exportBtn, BorderLayout.EAST);
        return panel;
    }

    private void addPeriodButton(JPanel container, String label) {
        JButton button = new JButton(label);
        stylePeriodButton(button, false);
        button.addActionListener(e -> {
            if ("Personalizado".equals(label)) {
                showCustomPeriodDialog();
                return;
            }
            selectedPeriod = label;
            updatePeriodButtons();
            loadReportsData();
        });
        periodButtons.put(label, button);
        container.add(button);
    }

    private void showCustomPeriodDialog() {
        JTextField fromField = new JTextField(LocalDate.now().withMonth(1).withDayOfMonth(1).toString());
        JTextField toField = new JTextField(LocalDate.now().toString());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("De (AAAA-MM-DD)"));
        form.add(fromField);
        form.add(new JLabel("Ate (AAAA-MM-DD)"));
        form.add(toField);

        int result = JOptionPane.showConfirmDialog(this, form, "Periodo personalizado",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            selectedPeriod = "Personalizado:" + fromField.getText().trim() + "|" + toField.getText().trim();
            updatePeriodButtons();
            loadReportsData();
        }
    }

    private void updatePeriodButtons() {
        periodButtons.forEach((label, button) -> {
            boolean active = selectedPeriod.equals(label)
                    || ("Personalizado".equals(label) && selectedPeriod.startsWith("Personalizado:"));
            stylePeriodButton(button, active);
        });
    }

    private JPanel createKPIRow() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        JPanel c1 = createKPICard("Total de Alunos", "-", "dados da API", BLUE, new Color(219, 234, 254));
        kpiStudents = (JLabel) c1.getComponent(2);
        panel.add(c1);

        JPanel c2 = createKPICard("Horas Voadas", "-", selectedPeriod, PURPLE, new Color(243, 232, 255));
        kpiHours = (JLabel) c2.getComponent(2);
        panel.add(c2);

        JPanel c3 = createKPICard("Taxa de Aprovacao", "-", "Exames e avaliacoes", GREEN, new Color(220, 252, 231));
        kpiPassRate = (JLabel) c3.getComponent(2);
        panel.add(c3);

        JPanel c4 = createKPICard("Receita Acumulada", "-", selectedPeriod, ORANGE, new Color(254, 243, 199));
        kpiRevenue = (JLabel) c4.getComponent(2);
        panel.add(c4);
        return panel;
    }

    private JPanel createKPICard(String title, String value, String subtitle, Color color, Color iconBg) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(true);
        iconWrap.setBackground(iconBg);
        iconWrap.setPreferredSize(new Dimension(36, 36));
        JLabel icon = new JLabel("\u2197");
        icon.setForeground(color);
        icon.setFont(new Font("Inter", Font.BOLD, 14));
        iconWrap.add(icon);

        JPanel iconHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconHolder.setOpaque(false);
        iconHolder.add(iconWrap);
        card.add(iconHolder);
        card.add(Box.createVerticalStrut(10));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 26));
        card.add(valueLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TITLE);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 12));
        card.add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(SOFT);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        card.add(subtitleLabel);
        return card;
    }

    private JPanel createChartsGrid() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setOpaque(false);
        panel.add(createRevenueCard(LocalDate.now().withMonth(1).withDayOfMonth(1), LocalDate.now()));
        panel.add(createCourseDistributionCard(Map.of()));
        panel.add(createPassRateCard(0));
        panel.add(createInstructorLoadCard(LocalDate.now().withMonth(1).withDayOfMonth(1), LocalDate.now()));
        return panel;
    }

    private void rebuildCharts(ReportsSummaryResponse summary, LocalDate from, LocalDate to) {
        Container parent = chartsGrid.getParent();
        if (parent instanceof JViewport viewport) {
            parent = viewport.getParent();
        }
        if (!(parent instanceof JScrollPane scrollPane)) {
            return;
        }
        JPanel replacement = new JPanel(new GridLayout(2, 2, 12, 12));
        replacement.setOpaque(false);
        replacement.add(createRevenueCard(from, to));
        replacement.add(createCourseDistributionCard(adminService.studentsByCourse(summary)));
        replacement.add(createPassRateCard(adminService.passRatePercent(from, to)));
        replacement.add(createInstructorLoadCard(from, to));

        scrollPane.setViewportView(replacement);
        chartsGrid = replacement;
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    private JPanel createRevenueCard(LocalDate from, LocalDate to) {
        JPanel card = createChartCard("Receita Mensal", "Receitas vs custos operacionais estimados");

        XYChart chart = new XYChartBuilder()
                .width(560).height(300).title("").xAxisTitle("").yAxisTitle("").build();
        styleXYChart(chart);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setLegendVisible(true);

        Map<Integer, double[]> monthly = adminService.monthlyRevenueExpense(from, to);
        double[] months = monthly.keySet().stream().mapToDouble(Integer::doubleValue).toArray();
        double[] revenue = monthly.values().stream().mapToDouble(v -> v[0]).toArray();
        double[] expense = monthly.values().stream().mapToDouble(v -> v[1]).toArray();

        if (months.length == 0) {
            months = new double[]{1};
            revenue = new double[]{0};
            expense = new double[]{0};
        }

        XYSeries revSeries = chart.addSeries("Receita", months, revenue);
        revSeries.setLineColor(BLUE);
        revSeries.setFillColor(new Color(21, 101, 192, 45));
        revSeries.setMarkerColor(BLUE);

        XYSeries expSeries = chart.addSeries("Custo", months, expense);
        expSeries.setLineColor(RED);
        expSeries.setFillColor(new Color(220, 38, 38, 38));
        expSeries.setMarkerColor(RED);

        card.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return card;
    }

    private JPanel createCourseDistributionCard(Map<String, Long> byCourse) {
        JPanel card = createChartCard("Distribuicao por Curso", "Alunos matriculados por tipo de licenca");

        JPanel content = new JPanel(new BorderLayout(10, 0));
        content.setOpaque(false);

        PieChart pieChart = new PieChartBuilder().width(320).height(220).title("").build();
        pieChart.getStyler().setChartBackgroundColor(WHITE);
        pieChart.getStyler().setPlotBackgroundColor(WHITE);
        pieChart.getStyler().setLegendVisible(false);
        pieChart.getStyler().setPlotBorderVisible(false);
        pieChart.getStyler().setLabelsVisible(false);
        pieChart.getStyler().setPlotContentSize(.84);
        pieChart.getStyler().setSeriesColors(new Color[]{BLUE, PURPLE, new Color(5, 150, 105), ORANGE});

        if (byCourse.isEmpty()) {
            pieChart.addSeries("Sem dados", 1);
        } else {
            byCourse.forEach((name, count) -> pieChart.addSeries(shortName(name), count.intValue()));
        }

        content.add(new XChartPanel<>(pieChart), BorderLayout.WEST);
        content.add(createCourseLegendPanel(byCourse), BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private String shortName(String name) {
        if (name == null || name.length() <= 12) {
            return name != null ? name : "?";
        }
        return name.substring(0, 12);
    }

    private JPanel createCourseLegendPanel(Map<String, Long> byCourse) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Color[] colors = {BLUE, PURPLE, new Color(5, 150, 105), ORANGE};
        int i = 0;
        long max = byCourse.values().stream().mapToLong(Long::longValue).max().orElse(1);

        if (byCourse.isEmpty()) {
            panel.add(new JLabel("Sem dados disponiveis"));
            return panel;
        }

        for (Map.Entry<String, Long> entry : byCourse.entrySet()) {
            if (i > 0) {
                panel.add(Box.createVerticalStrut(8));
            }
            panel.add(createLegendRow(shortName(entry.getKey()), entry.getValue().intValue(),
                    colors[i % colors.length], (int) max));
            i++;
        }
        return panel;
    }

    private JPanel createLegendRow(String label, int value, Color color, int max) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        left.setOpaque(false);
        JLabel dot = new JLabel("\u25CF");
        dot.setForeground(color);
        dot.setFont(new Font("Dialog", Font.PLAIN, 8));
        JLabel name = new JLabel(label);
        name.setForeground(new Color(55, 65, 81));
        name.setFont(new Font("Inter", Font.PLAIN, 12));
        left.add(dot);
        left.add(name);

        JLabel amount = new JLabel(String.valueOf(value));
        amount.setForeground(color);
        amount.setFont(new Font("Inter", Font.BOLD, 12));

        top.add(left, BorderLayout.WEST);
        top.add(amount, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, Math.max(max, 1));
        bar.setValue(value);
        bar.setForeground(color);
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(160, 6));

        row.add(top);
        row.add(Box.createVerticalStrut(4));
        row.add(bar);
        return row;
    }

    private JPanel createPassRateCard(int passRate) {
        JPanel card = createChartCard("Taxa de Aprovacao", "Percentual de aprovacoes no periodo");

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        JLabel value = new JLabel(passRate + "%");
        value.setFont(new Font("Inter", Font.BOLD, 48));
        value.setForeground(GREEN);
        content.add(value);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createInstructorLoadCard(LocalDate from, LocalDate to) {
        JPanel card = createChartCard("Carga de Instrutores", "Horas e numero de alunos por instrutor");

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        var loads = adminService.instructorLoads(from, to);
        if (loads.isEmpty()) {
            content.add(new JLabel("Sem dados de instrutores"));
        } else {
            for (int i = 0; i < loads.size(); i++) {
                BoAdminService.InstructorLoad load = loads.get(i);
                content.add(createInstructorRow(load.name, load.hours, load.students));
                if (i < loads.size() - 1) {
                    content.add(Box.createVerticalStrut(8));
                }
            }
        }

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createInstructorRow(String name, int hours, int students) {
        JPanel row = new JPanel();
        row.setOpaque(true);
        row.setBackground(new Color(248, 250, 252));
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(TITLE);
        nameLabel.setFont(new Font("Inter", Font.BOLD, 12));

        JLabel badge = new JLabel(hours + "h / " + students + " alunos");
        badge.setForeground(BLUE);
        badge.setFont(new Font("Inter", Font.BOLD, 11));

        top.add(nameLabel, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 180);
        bar.setValue(Math.min(hours, 180));
        bar.setForeground(BLUE);
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(220, 8));

        row.add(top);
        row.add(Box.createVerticalStrut(6));
        row.add(bar);
        return row;
    }

    private JPanel createChartCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TITLE);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setForeground(SOFT);
        subLabel.setFont(new Font("Inter", Font.PLAIN, 11));

        header.add(titleLabel);
        header.add(subLabel);
        card.add(header, BorderLayout.NORTH);
        return card;
    }

    private void styleXYChart(XYChart chart) {
        chart.getStyler().setChartBackgroundColor(WHITE);
        chart.getStyler().setPlotBackgroundColor(WHITE);
        chart.getStyler().setPlotGridLinesColor(new Color(241, 245, 249));
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setLegendBorderColor(WHITE);
        chart.getStyler().setLegendBackgroundColor(WHITE);
        chart.getStyler().setLegendFont(new Font("Inter", Font.PLAIN, 11));
        chart.getStyler().setAxisTickLabelsColor(SOFT);
        chart.getStyler().setAxisTickLabelsFont(new Font("Inter", Font.PLAIN, 10));
        chart.getStyler().setChartFontColor(MUTED);
        chart.getStyler().setCursorColor(BLUE);
    }

    private void stylePeriodButton(JButton button, boolean active) {
        button.setFont(new Font("Inter", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(7, 14, 7, 14));
        if (active) {
            button.setBackground(BLUE);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BLUE, 1),
                    new EmptyBorder(7, 14, 7, 14)
            ));
        } else {
            button.setBackground(WHITE);
            button.setForeground(MUTED);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1),
                    new EmptyBorder(7, 14, 7, 14)
            ));
        }
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE_DARK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void exportPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Relatorio_AeroSchool_" + selectedPeriod.replace(" ", "_").replace(":", "_") + ".pdf"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }
            PdfExportHelper.exportReportsSummary(file, selectedPeriod,
                    cachedStudents, cachedHours, cachedPassRate, cachedRevenue);
            JOptionPane.showMessageDialog(this, "Relatorio exportado com sucesso.\n" + file.getAbsolutePath(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
