package pt.ipvc.estg.desktop.views.panels;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Dashboard BackOffice simplificado e consistente com a stack atual.
 */
public class BODashboard extends JPanel {

    private static final Color DARK_BG = new Color(15, 35, 68);
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192);
    private static final Color LIGHT_BG = new Color(240, 244, 248);
    private static final Color WHITE = Color.WHITE;
    private static final Color GRAY_TEXT = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);

    public BODashboard() {
        setBackground(LIGHT_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        add(createWelcomeBanner());
        add(Box.createVerticalStrut(20));
        add(createKPICards());
        add(Box.createVerticalStrut(20));

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        chartsPanel.add(createEnrollmentChart());
        chartsPanel.add(createAircraftStatusPanel());
        add(chartsPanel);

        add(Box.createVerticalStrut(20));
        add(createRecentActivityPanel());
        add(Box.createVerticalGlue());
    }

    private JPanel createWelcomeBanner() {
        JPanel banner = new JPanel(new BorderLayout(20, 10));
        banner.setBackground(DARK_BG);
        banner.setBorder(new EmptyBorder(20, 24, 20, 24));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel leftSection = new JPanel();
        leftSection.setOpaque(false);
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));

        JLabel greeting = new JLabel("Bom dia, Administrador");
        greeting.setFont(new Font("Inter", Font.BOLD, 18));
        greeting.setForeground(WHITE);
        leftSection.add(greeting);

        String dateStr = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt", "PT")));
        JLabel dateLabel = new JLabel("Hoje e " + dateStr + " · 12 voos agendados");
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(191, 219, 254));
        leftSection.add(dateLabel);

        banner.add(leftSection, BorderLayout.WEST);
        banner.add(createBannerStats(), BorderLayout.EAST);

        return banner;
    }

    private JPanel createBannerStats() {
        JPanel stats = new JPanel(new GridLayout(1, 2, 10, 0));
        stats.setOpaque(false);
        stats.add(createBannerStat("245h", "Horas este mes"));
        stats.add(createBannerStat("EUR 24.5k", "Receita mensal"));
        return stats;
    }

    private JPanel createBannerStat(String value, String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        panel.setBackground(new Color(255, 255, 255, 20));
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 18));
        valueLabel.setForeground(WHITE);
        panel.add(valueLabel);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        labelLabel.setForeground(new Color(191, 219, 254));
        panel.add(labelLabel);

        return panel;
    }

    private JPanel createKPICards() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        panel.add(createKPICard("Alunos Ativos", "40", "de 48 matriculados", BLUE_PRIMARY));
        panel.add(createKPICard("Voos Hoje", "12", "3 completados, 9 agendados", new Color(124, 58, 237)));
        panel.add(createKPICard("Aeronaves Disponiveis", "4", "de 5 no total", SUCCESS_COLOR));
        panel.add(createKPICard("Pagamentos Pendentes", "2", "Valor: EUR 2.980", WARNING_COLOR));
        panel.add(createKPICard("Manutencoes em Curso", "2", "1 aguarda pecas", ERROR_COLOR));

        return panel;
    }

    private JPanel createKPICard(String title, String value, String subtitle, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        titleLabel.setForeground(GRAY_TEXT);
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 24));
        valueLabel.setForeground(accent);
        card.add(valueLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 10));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        card.add(subtitleLabel);

        return card;
    }

    private JPanel createEnrollmentChart() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("Matriculas por Mes");
        title.setFont(new Font("Inter", Font.BOLD, 14));
        title.setForeground(DARK_BG);
        panel.add(title, BorderLayout.NORTH);

        XYChart chart = new XYChartBuilder()
                .width(420)
                .height(240)
                .title("")
                .xAxisTitle("")
                .yAxisTitle("")
                .build();

        List<Integer> months = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<Integer> values = Arrays.asList(45, 52, 48, 67, 78, 85, 92);
        chart.addSeries("Matriculas", months, values);

        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setChartBackgroundColor(WHITE);
        chart.getStyler().setPlotBackgroundColor(WHITE);
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setAxisTicksMarksVisible(false);
        chart.getStyler().setXAxisTicksVisible(false);
        chart.getStyler().setYAxisTicksVisible(false);
        chart.getStyler().setChartPadding(8);

        panel.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAircraftStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel title = new JLabel("Estado das Aeronaves");
        title.setFont(new Font("Inter", Font.BOLD, 14));
        title.setForeground(DARK_BG);
        panel.add(title);

        JLabel subtitle = new JLabel("5 aeronaves no total");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 11));
        subtitle.setForeground(GRAY_TEXT);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(12));

        panel.add(createStatusRow("Operacional", "4", SUCCESS_COLOR));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createStatusRow("Em Manutencao", "1", ERROR_COLOR));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createStatusRow(String label, String value, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(color);
        dot.setFont(new Font("Dialog", Font.BOLD, 12));
        row.add(dot, BorderLayout.WEST);

        JLabel name = new JLabel(label);
        name.setFont(new Font("Inter", Font.PLAIN, 12));
        name.setForeground(GRAY_TEXT);
        row.add(name, BorderLayout.CENTER);

        JLabel count = new JLabel(value);
        count.setFont(new Font("Inter", Font.BOLD, 12));
        count.setForeground(DARK_BG);
        row.add(count, BorderLayout.EAST);

        return row;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        JLabel title = new JLabel("Atividade Recente");
        title.setFont(new Font("Inter", Font.BOLD, 14));
        title.setForeground(DARK_BG);
        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        panel.add(createActivityRow("✓", "Voo CS-AER concluido", "Joao Silva · ha 2h", SUCCESS_COLOR));
        panel.add(createActivityRow("⏳", "Manutencao CS-NAV iniciada", "Tecnico · ha 1h", WARNING_COLOR));
        panel.add(createActivityRow("✉", "Avaliacao atualizada", "Instrutor · ha 30m", BLUE_PRIMARY));
        panel.add(createActivityRow("$", "Pagamento recebido", "EUR 2000 · ha 15m", SUCCESS_COLOR));

        return panel;
    }

    private JPanel createActivityRow(String iconText, String title, String subtitle, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel icon = new JLabel(iconText);
        icon.setFont(new Font("Dialog", Font.BOLD, 12));
        icon.setForeground(color);
        icon.setPreferredSize(new Dimension(24, 20));
        row.add(icon, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        titleLabel.setForeground(DARK_BG);
        text.add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 10));
        subtitleLabel.setForeground(GRAY_TEXT);
        text.add(subtitleLabel);

        row.add(text, BorderLayout.CENTER);
        return row;
    }
}
