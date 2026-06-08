package pt.ipvc.estg.desktop.views.panels;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import pt.ipvc.estg.desktop.api.SessionContext;
import pt.ipvc.estg.desktop.api.dto.BoDashboardResponse;
import pt.ipvc.estg.desktop.api.dto.ReportsSummaryResponse;
import pt.ipvc.estg.desktop.services.BoAdminService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard BackOffice com dados da API ou fallback local.
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

    private final BoAdminService adminService = new BoAdminService();

    private JLabel greetingLabel;
    private JLabel dateLabel;
    private JLabel bannerHoursLabel;
    private JLabel bannerRevenueLabel;

    private JLabel kpiActiveStudents;
    private JLabel kpiActiveSubtitle;
    private JLabel kpiFlightsToday;
    private JLabel kpiFlightsSubtitle;
    private JLabel kpiAircraft;
    private JLabel kpiAircraftSubtitle;
    private JLabel kpiPayments;
    private JLabel kpiPaymentsSubtitle;
    private JLabel kpiMaintenance;
    private JLabel kpiMaintenanceSubtitle;

    private JPanel enrollmentChartPanel;
    private JLabel aircraftTotalLabel;
    private JPanel aircraftStatusPanel;
    private JPanel recentActivityPanel;

    public BODashboard() {
        setBackground(LIGHT_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(22, 20, 20, 20));

        add(createWelcomeBanner());
        add(Box.createVerticalStrut(16));
        add(createKPICards());
        add(Box.createVerticalStrut(16));

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 282));
        enrollmentChartPanel = createEnrollmentChart();
        chartsPanel.add(enrollmentChartPanel);
        chartsPanel.add(createAircraftStatusPanel());
        add(chartsPanel);

        add(Box.createVerticalStrut(14));
        recentActivityPanel = createRecentActivityPanel();
        add(recentActivityPanel);
        add(Box.createVerticalGlue());

        loadDashboardData();
    }

    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            ReportsSummaryResponse summary;
            long flightsToday;
            long completedToday;
            long scheduledToday;
            double monthHours;
            double monthRevenue;
            List<BoAdminService.ActivityItem> activities = List.of();
            Map<String, Long> byCourse = Map.of();

            @Override
            protected Void doInBackground() {
                try {
                    if (adminService.useApi()) {
                        BoDashboardResponse dashboard = adminService.getDashboard();
                        if (dashboard != null) {
                            summary = dashboard.reports();
                            flightsToday = dashboard.flightsToday();
                            completedToday = dashboard.completedFlightsToday();
                            scheduledToday = dashboard.scheduledFlightsToday();
                            monthHours = dashboard.flightHoursThisMonth();
                            monthRevenue = dashboard.revenueThisMonth();
                            activities = dashboard.recentActivity().stream()
                                    .map(a -> new BoAdminService.ActivityItem(
                                            a.icon(), a.title(), a.subtitle(), a.type()))
                                    .toList();
                            byCourse = adminService.studentsByCourse(summary);
                            return null;
                        }
                    }
                    summary = adminService.getReportsSummary();
                    flightsToday = adminService.flightsToday();
                    completedToday = adminService.completedFlightsToday();
                    scheduledToday = adminService.scheduledFlightsToday();
                    monthHours = adminService.flightHoursThisMonth();
                    monthRevenue = adminService.revenueThisMonth();
                    activities = adminService.recentActivity(4);
                    byCourse = adminService.studentsByCourse(summary);
                } catch (Exception ex) {
                    System.err.println("Erro ao carregar dashboard: " + ex.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                applyDashboardData(summary, flightsToday, completedToday, scheduledToday,
                        monthHours, monthRevenue, activities, byCourse);
            }
        };
        worker.execute();
    }

    private void applyDashboardData(ReportsSummaryResponse summary, long flightsToday,
                                    long completedToday, long scheduledToday,
                                    double monthHours, double monthRevenue,
                                    List<BoAdminService.ActivityItem> activities,
                                    Map<String, Long> byCourse) {
        String adminName = SessionContext.getUsername() != null ? SessionContext.getUsername() : "Administrador";
        greetingLabel.setText("Bom dia, " + adminName);

        String dateStr = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new Locale("pt", "PT")));
        dateLabel.setText("Hoje e " + dateStr + " - " + flightsToday + " voos agendados para hoje");

        bannerHoursLabel.setText(String.format(Locale.ROOT, "%.0fh", monthHours));
        bannerRevenueLabel.setText(formatEuroShort(monthRevenue));

        if (summary != null) {
            kpiActiveStudents.setText(String.valueOf(summary.activeStudents()));
            kpiActiveSubtitle.setText("de " + summary.totalStudents() + " matriculados");
            kpiAircraft.setText(String.valueOf(summary.operationalAircraft()));
            kpiAircraftSubtitle.setText("de " + summary.totalAircraft() + " no total");
            kpiPayments.setText(String.valueOf(summary.pendingPayments() + summary.overduePayments()));
            kpiPaymentsSubtitle.setText("Valor: " + formatEuroShort(summary.totalPendingAmount()));
            kpiMaintenance.setText(String.valueOf(summary.activeMaintenances()));
            kpiMaintenanceSubtitle.setText(summary.maintenanceAircraft() + " aeronaves em manutencao");
            aircraftTotalLabel.setText(summary.totalAircraft() + " aeronaves no total");
            updateAircraftRows(summary.operationalAircraft(), summary.maintenanceAircraft(), summary.groundedAircraft());
        } else {
            kpiActiveStudents.setText("-");
            kpiActiveSubtitle.setText("ligue a API para dados reais");
            kpiAircraft.setText("-");
            kpiAircraftSubtitle.setText("-");
            kpiPayments.setText("-");
            kpiPaymentsSubtitle.setText("-");
            kpiMaintenance.setText("-");
            kpiMaintenanceSubtitle.setText("-");
        }

        kpiFlightsToday.setText(String.valueOf(flightsToday));
        kpiFlightsSubtitle.setText(completedToday + " completados, " + scheduledToday + " agendados");

        rebuildEnrollmentChart(byCourse);
        rebuildRecentActivity(activities);
    }

    private String formatEuroShort(double value) {
        if (value >= 1000) {
            return String.format(Locale.ROOT, "EUR %.1fk", value / 1000.0);
        }
        return String.format(Locale.ROOT, "EUR %.0f", value);
    }

    private JPanel createWelcomeBanner() {
        JPanel banner = new JPanel(new BorderLayout(20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, DARK_BG, getWidth(), getHeight(), BLUE_PRIMARY);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);
        banner.setBorder(new EmptyBorder(18, 22, 18, 22));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 96));

        JPanel leftSection = new JPanel();
        leftSection.setOpaque(false);
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));

        greetingLabel = new JLabel("Bom dia, Administrador");
        greetingLabel.setFont(new Font("Inter", Font.BOLD, 17));
        greetingLabel.setForeground(WHITE);
        leftSection.add(greetingLabel);

        dateLabel = new JLabel("A carregar...");
        dateLabel.setFont(new Font("Inter", Font.BOLD, 11));
        dateLabel.setForeground(new Color(191, 219, 254));
        leftSection.add(dateLabel);

        banner.add(leftSection, BorderLayout.WEST);
        banner.add(createBannerStats(), BorderLayout.EAST);
        return banner;
    }

    private JPanel createBannerStats() {
        JPanel stats = new JPanel(new GridLayout(1, 2, 10, 0));
        stats.setOpaque(false);

        JPanel hours = createBannerStat("0h", "Horas este mes");
        bannerHoursLabel = (JLabel) hours.getComponent(0);
        stats.add(hours);

        JPanel revenue = createBannerStat("EUR 0", "Receita mensal");
        bannerRevenueLabel = (JLabel) revenue.getComponent(0);
        stats.add(revenue);
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
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));

        JPanel card1 = createKPICard("Alunos Ativos", "...", "...", BLUE_PRIMARY);
        kpiActiveStudents = (JLabel) card1.getComponent(1);
        kpiActiveSubtitle = (JLabel) card1.getComponent(2);
        panel.add(card1);

        JPanel card2 = createKPICard("Voos Hoje", "...", "...", new Color(124, 58, 237));
        kpiFlightsToday = (JLabel) card2.getComponent(1);
        kpiFlightsSubtitle = (JLabel) card2.getComponent(2);
        panel.add(card2);

        JPanel card3 = createKPICard("Aeronaves Disponiveis", "...", "...", SUCCESS_COLOR);
        kpiAircraft = (JLabel) card3.getComponent(1);
        kpiAircraftSubtitle = (JLabel) card3.getComponent(2);
        panel.add(card3);

        JPanel card4 = createKPICard("Pagamentos Pendentes", "...", "...", WARNING_COLOR);
        kpiPayments = (JLabel) card4.getComponent(1);
        kpiPaymentsSubtitle = (JLabel) card4.getComponent(2);
        panel.add(card4);

        JPanel card5 = createKPICard("Manutencoes em Curso", "...", "...", ERROR_COLOR);
        kpiMaintenance = (JLabel) card5.getComponent(1);
        kpiMaintenanceSubtitle = (JLabel) card5.getComponent(2);
        panel.add(card5);

        return panel;
    }

    private JPanel createKPICard(String title, String value, String subtitle, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        titleLabel.setForeground(GRAY_TEXT);
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 28));
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

        JLabel title = new JLabel("Alunos por Curso");
        title.setFont(new Font("Inter", Font.BOLD, 14));
        title.setForeground(DARK_BG);
        panel.add(title, BorderLayout.NORTH);
        panel.add(buildCourseChart(Map.of()), BorderLayout.CENTER);
        return panel;
    }

    private void rebuildEnrollmentChart(Map<String, Long> byCourse) {
        Container parent = enrollmentChartPanel.getParent();
        if (parent == null) {
            return;
        }
        int index = -1;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == enrollmentChartPanel) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return;
        }
        JPanel replacement = createEnrollmentChart();
        enrollmentChartPanel = replacement;
        parent.remove(index);
        if (parent.getLayout() instanceof GridLayout grid) {
            parent.add(replacement, index);
        } else {
            parent.add(replacement);
        }
        parent.revalidate();
        parent.repaint();
    }

    private XChartPanel<XYChart> buildCourseChart(Map<String, Long> byCourse) {
        XYChart chart = new XYChartBuilder()
                .width(420)
                .height(240)
                .title("")
                .xAxisTitle("")
                .yAxisTitle("")
                .build();

        List<Integer> x = new ArrayList<>();
        List<Integer> y = new ArrayList<>();
        int i = 1;
        for (Map.Entry<String, Long> entry : byCourse.entrySet()) {
            x.add(i++);
            y.add(entry.getValue().intValue());
        }
        if (x.isEmpty()) {
            x.add(1);
            y.add(0);
        }

        chart.addSeries("Alunos", x, y);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.getStyler().setPlotGridLinesVisible(false);
        chart.getStyler().setYAxisMin(0.0);
        chart.getStyler().setChartBackgroundColor(WHITE);
        chart.getStyler().setPlotBackgroundColor(WHITE);
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setChartPadding(8);
        return new XChartPanel<>(chart);
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

        aircraftTotalLabel = new JLabel("A carregar...");
        aircraftTotalLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        aircraftTotalLabel.setForeground(GRAY_TEXT);
        panel.add(aircraftTotalLabel);
        panel.add(Box.createVerticalStrut(12));

        aircraftStatusPanel = new JPanel();
        aircraftStatusPanel.setLayout(new BoxLayout(aircraftStatusPanel, BoxLayout.Y_AXIS));
        aircraftStatusPanel.setOpaque(false);
        panel.add(aircraftStatusPanel);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void updateAircraftRows(long operational, long maintenance, long grounded) {
        aircraftStatusPanel.removeAll();
        aircraftStatusPanel.add(createStatusRow("Operacional", String.valueOf(operational), SUCCESS_COLOR));
        aircraftStatusPanel.add(Box.createVerticalStrut(8));
        aircraftStatusPanel.add(createStatusRow("Em Manutencao", String.valueOf(maintenance), ERROR_COLOR));
        aircraftStatusPanel.add(Box.createVerticalStrut(8));
        aircraftStatusPanel.add(createStatusRow("Indisponivel", String.valueOf(grounded), WARNING_COLOR));
        aircraftStatusPanel.revalidate();
        aircraftStatusPanel.repaint();
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
        panel.add(new JLabel("A carregar..."));
        return panel;
    }

    private void rebuildRecentActivity(List<BoAdminService.ActivityItem> activities) {
        Container parent = recentActivityPanel.getParent();
        if (parent == null) {
            return;
        }
        int index = -1;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == recentActivityPanel) {
                index = i;
                break;
            }
        }

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

        if (activities.isEmpty()) {
            panel.add(new JLabel("Sem atividade recente"));
        } else {
            for (BoAdminService.ActivityItem item : activities) {
                Color color = switch (item.type()) {
                    case "success" -> SUCCESS_COLOR;
                    case "warning" -> WARNING_COLOR;
                    default -> BLUE_PRIMARY;
                };
                panel.add(createActivityRow(item.icon(), item.title(), item.subtitle(), color));
            }
        }

        if (index >= 0) {
            parent.remove(index);
            parent.add(panel, index);
        }
        recentActivityPanel = panel;
        parent.revalidate();
        parent.repaint();
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
