package pt.ipvc.estg.desktop.views.panels;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Painel de relatorios alinhado ao visual dos mockups.
 */
public class BOReports extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);     // #EEF2F7
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);      // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);          // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);       // #64748B
    private static final Color SOFT = new Color(148, 163, 184);        // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);         // #1565C0
    private static final Color BLUE_DARK = new Color(13, 71, 161);     // #0D47A1
    private static final Color GREEN = new Color(22, 163, 74);         // #16A34A
    private static final Color PURPLE = new Color(124, 58, 237);       // #7C3AED
    private static final Color ORANGE = new Color(217, 119, 6);        // #D97706
    private static final Color RED = new Color(220, 38, 38);           // #DC2626

    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    private final Map<String, JButton> periodButtons = new LinkedHashMap<>();
    private String selectedPeriod = "Jan-Mar 2025";

    public BOReports() {
        initializeUI();
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

        JPanel chartsGrid = createChartsGrid();
        JScrollPane scrollPane = new JScrollPane(chartsGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel periods = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        periods.setOpaque(false);

        addPeriodButton(periods, "Jan-Mar 2025");
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
            selectedPeriod = label;
            updatePeriodButtons();
        });
        periodButtons.put(label, button);
        container.add(button);
    }

    private void updatePeriodButtons() {
        periodButtons.forEach((label, button) -> stylePeriodButton(button, label.equals(selectedPeriod)));
    }

    private JPanel createKPIRow() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        panel.add(createKPICard("Total de Alunos", "40", "+12% vs ano anterior", BLUE, new Color(219, 234, 254)));
        panel.add(createKPICard("Horas Voadas (2025)", "900h", "Meta: 1200h anuais", PURPLE, new Color(243, 232, 255)));
        panel.add(createKPICard("Taxa de Aprovacao", "87%", "Exames e avaliacoes", GREEN, new Color(220, 252, 231)));
        panel.add(createKPICard("Receita Acumulada", "EUR 148k", "Jan-Mar 2025", ORANGE, new Color(254, 243, 199)));
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
        panel.add(createRevenueCard());
        panel.add(createCourseDistributionCard());
        panel.add(createPassRateCard());
        panel.add(createInstructorLoadCard());
        return panel;
    }

    private JPanel createRevenueCard() {
        JPanel card = createChartCard("Receita Mensal", "Receitas vs despesas operacionais");

        XYChart chart = new XYChartBuilder()
                .width(560)
                .height(300)
                .title("")
                .xAxisTitle("")
                .yAxisTitle("")
                .build();

        styleXYChart(chart);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setXAxisMin(1.0);
        chart.getStyler().setXAxisMax(7.0);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setYAxisMax(17000.0);
        chart.getStyler().setYAxisDecimalPattern("EUR #,###");

        double[] months = {1, 2, 3, 4, 5, 6, 7};
        double[] revenue = {8500, 9800, 11200, 12600, 13700, 15100, 14800};
        double[] expense = {4200, 4700, 5300, 5900, 6300, 7100, 6900};

        XYSeries revSeries = chart.addSeries("Receita", months, revenue);
        revSeries.setLineColor(BLUE);
        revSeries.setFillColor(new Color(21, 101, 192, 45));
        revSeries.setMarkerColor(BLUE);

        XYSeries expSeries = chart.addSeries("Despesa", months, expense);
        expSeries.setLineColor(RED);
        expSeries.setFillColor(new Color(220, 38, 38, 38));
        expSeries.setMarkerColor(RED);

        card.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return card;
    }

    private JPanel createCourseDistributionCard() {
        JPanel card = createChartCard("Distribuicao por Curso", "Alunos matriculados por tipo de licenca");

        JPanel content = new JPanel(new BorderLayout(10, 0));
        content.setOpaque(false);

        PieChart pieChart = new PieChartBuilder()
                .width(320)
                .height(220)
                .title("")
                .build();

        pieChart.getStyler().setChartBackgroundColor(WHITE);
        pieChart.getStyler().setPlotBackgroundColor(WHITE);
        pieChart.getStyler().setLegendVisible(false);
        pieChart.getStyler().setPlotBorderVisible(false);
        pieChart.getStyler().setLabelsVisible(false);
        pieChart.getStyler().setPlotContentSize(.84);
        pieChart.getStyler().setSeriesColors(new Color[]{
                BLUE,
                PURPLE,
                new Color(5, 150, 105),
                ORANGE
        });

        pieChart.addSeries("PPL", 20);
        pieChart.addSeries("CPL", 12);
        pieChart.addSeries("IR", 5);
        pieChart.addSeries("ATPL", 3);

        content.add(new XChartPanel<>(pieChart), BorderLayout.WEST);
        content.add(createCourseLegendPanel(), BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCourseLegendPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(createLegendRow("PPL", 20, BLUE));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createLegendRow("CPL", 12, PURPLE));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createLegendRow("IR", 5, new Color(5, 150, 105)));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createLegendRow("ATPL", 3, ORANGE));
        return panel;
    }

    private JPanel createLegendRow(String label, int value, Color color) {
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

        JProgressBar bar = new JProgressBar(0, 40);
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

    private JPanel createPassRateCard() {
        JPanel card = createChartCard("Taxa de Aprovacao por Curso", "Percentual de aprovacoes em avaliacoes");

        XYChart chart = new XYChartBuilder()
                .width(560)
                .height(300)
                .title("")
                .xAxisTitle("")
                .yAxisTitle("")
                .build();

        styleXYChart(chart);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setXAxisMin(1.0);
        chart.getStyler().setXAxisMax(7.0);
        chart.getStyler().setYAxisMin(60.0);
        chart.getStyler().setYAxisMax(100.0);
        chart.getStyler().setYAxisDecimalPattern("##'% '");
        chart.getStyler().setMarkerSize(4);

        double[] months = {1, 2, 3, 4, 5, 6, 7};
        double[] ppl = {88, 90, 85, 92, 88, 95, 87};
        double[] cpl = {80, 85, 78, 82, 88, 90, 85};
        double[] ir = {75, 70, 80, 72, 85, 88, 82};

        XYSeries pplSeries = chart.addSeries("PPL", months, ppl);
        pplSeries.setLineColor(BLUE);
        pplSeries.setMarkerColor(BLUE);

        XYSeries cplSeries = chart.addSeries("CPL", months, cpl);
        cplSeries.setLineColor(PURPLE);
        cplSeries.setMarkerColor(PURPLE);

        XYSeries irSeries = chart.addSeries("IR", months, ir);
        irSeries.setLineColor(new Color(5, 150, 105));
        irSeries.setMarkerColor(new Color(5, 150, 105));

        card.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return card;
    }

    private JPanel createInstructorLoadCard() {
        JPanel card = createChartCard("Carga de Instrutores", "Horas e numero de alunos por instrutor");

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(createInstructorRow("Capt. Ferreira", 145, 12));
        content.add(Box.createVerticalStrut(8));
        content.add(createInstructorRow("Capt. Lopes", 118, 8));
        content.add(Box.createVerticalStrut(8));
        content.add(createInstructorRow("Capt. Pereira", 165, 6));

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
        bar.setValue(hours);
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
        chart.getStyler().setAxisTitleFont(new Font("Inter", Font.PLAIN, 10));
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
        fileChooser.setSelectedFile(new java.io.File("Relatorio_AeroSchool_" + selectedPeriod.replace(" ", "_") + ".pdf"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            generatePDF(filePath);
            JOptionPane.showMessageDialog(
                    this,
                    "Relatorio exportado com sucesso.\n" + filePath,
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao exportar PDF: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void generatePDF(String filePath) throws Exception {
        try {
            String content = "Relatorio de Analise - AeroSchool\n"
                    + "Periodo: " + selectedPeriod + "\n"
                    + "Gerado em: " + LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_DATE) + "\n\n"
                    + "KPIs:\n"
                    + "- Total de Alunos: 40\n"
                    + "- Horas Voadas: 900h\n"
                    + "- Taxa de Aprovacao: 87%\n"
                    + "- Receita Acumulada: EUR 148k\n";
            Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new Exception("Erro ao gerar relatorio: " + e.getMessage());
        }
    }
}
