package pt.ipvc.estg.desktop.views.panels;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import pt.ipvc.estg.backend.services.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BODashboard extends JPanel {
    private static final Color DARK_BG = new Color(15, 35, 68);        // #0F2344
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192);  // #1565C0
    private static final Color LIGHT_BG = new Color(240, 244, 248);     // #F0F4F8
    private static final Color WHITE = Color.WHITE;
    private static final Color GRAY_TEXT = new Color(100, 116, 139);    // #64748B
    private static final Color BORDER_COLOR = new Color(226, 232, 240); // #E2E8F0
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);  // green-500
    private static final Color WARNING_COLOR = new Color(245, 158, 11); // yellow-500
    private static final Color ERROR_COLOR = new Color(239, 68, 68);    // red-500

    private StudentService studentService;
    private FlightService flightService;
    private CourseService courseService;
    private MaintenanceService maintenanceService;

    public BODashboard() {
        setBackground(LIGHT_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Initialize services
        studentService = new StudentService();
        flightService = new FlightService();
        courseService = new CourseService();
        maintenanceService = new MaintenanceService();

        // Welcome banner
        add(createWelcomeBanner());
        add(Box.createVerticalStrut(20));

        // KPI Cards
        add(createKPICards());
        add(Box.createVerticalStrut(20));

        // Charts section
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        chartsPanel.add(createEnrollmentChart());
        chartsPanel.add(createAircraftStatusChart());

        add(chartsPanel);
        add(Box.createVerticalStrut(20));

        // Recent activity table
        add(createRecentActivityPanel());

        add(Box.createVerticalGlue());
    }

    private JPanel createWelcomeBanner() {
        JPanel banner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, DARK_BG,
                        getWidth(), getHeight(), BLUE_PRIMARY
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                // Border
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            }
        };

        banner.setLayout(new BorderLayout(20, 10));
        banner.setBorder(new EmptyBorder(25, 25, 25, 25));
        banner.setOpaque(false);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Left section
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setOpaque(false);

        JLabel greeting = new JLabel("Bom dia, Administrador! ✈");
        greeting.setFont(new Font("Inter", Font.BOLD, 18));
        greeting.setForeground(WHITE);
        leftSection.add(greeting);

        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new java.util.Locale("pt", "PT")));
        int flightsToday = flightService.getAllFlights().size(); // Simplified
        JLabel dateLabel = new JLabel("Hoje é " + dateStr + " · " + flightsToday + " voos agendados");
        dateLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(191, 219, 254)); // blue-200
        leftSection.add(dateLabel);

        banner.add(leftSection, BorderLayout.WEST);

        // Right section - Stats
        JPanel rightSection = new JPanel(new GridLayout(1, 2, 15, 0));
        rightSection.setOpaque(false);

        JPanel statCard1 = createBannerStat("245h", "Horas este mês");
        JPanel statCard2 = createBannerStat("€24.5k", "Receita mensal");

        rightSection.add(statCard1);
        rightSection.add(statCard2);

        banner.add(rightSection, BorderLayout.EAST);

        return banner;
    }

    private JPanel createBannerStat(String value, String label) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 255, 255, 15));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        panel.setOpaque(true);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 20));
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
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        panel.add(createKPICard("Alunos Ativos", "40", "de 48 matriculados", BLUE_PRIMARY));
        panel.add(createKPICard("Voos Hoje", "12", "3 completados, 9 agendados", new Color(124, 58, 237))); // purple
        panel.add(createKPICard("Aeronaves Disponíveis", "4", "de 5 no total", SUCCESS_COLOR));
        panel.add(createKPICard("Pagamentos Pendentes", "2", "Valor: €2.980", WARNING_COLOR));
        panel.add(createKPICard("Manutenções em Curso", "2", "1 aguarda peças", ERROR_COLOR));

        return panel;
    }

    private JPanel createKPICard(String label, String value, String sub, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon + Arrow
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setOpaque(false);

        JLabel icon = new JLabel("📊");
        icon.setFont(new Font("Arial", Font.PLAIN, 20));
        icon.setForeground(color);
        topRow.add(icon);

        topRow.add(Box.createHorizontalGlue());

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("Arial", Font.PLAIN, 14));
        arrow.setForeground(BORDER_COLOR);
        topRow.add(arrow);

        card.add(topRow);
        card.add(Box.createVerticalStrut(10));

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 28));
        valueLabel.setForeground(DARK_BG);
        card.add(valueLabel);

        // Label
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        labelLabel.setForeground(GRAY_TEXT);
        card.add(labelLabel);

        // Sub info
        if (sub != null && !sub.isEmpty()) {
            JLabel subLabel = new JLabel(sub);
            subLabel.setFont(new Font("Inter", Font.PLAIN, 10));
            subLabel.setForeground(new Color(148, 163, 184)); // slate-400
            card.add(subLabel);
        }

        return card;
    }

    private JPanel createEnrollmentChart() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Matrículas por Mês");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        titleLabel.setForeground(DARK_BG);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JLabel trendinglabel = new JLabel("↗ +18% vs ano anterior");
        trendinglabel.setFont(new Font("Inter", Font.PLAIN, 11));
        trendinglabel.setForeground(SUCCESS_COLOR);
        titlePanel.add(trendinglabel, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        // Chart
        XYChart chart = new XYChartBuilder()
                .width(400)
                .height(220)
                .theme(Styler.ChartTheme.Light)
                .title("")
                .build();

        // Sample data
        List<Double> months = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0);
        List<Double> enrollments = Arrays.asList(45.0, 52.0, 48.0, 67.0, 78.0, 85.0, 92.0);

        chart.addSeries("Matrículas", months, enrollments);
        chart.getStyler().setDefaultSeriesRenderStyle(org.knowm.xchart.XySeriesRenderStyle.Area);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setChartTitleVisible(false);

        JPanel chartPanel = new SwingWrapper<>(chart).getChartPanel();
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAircraftStatusChart() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Estado das Aeronaves");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        titleLabel.setForeground(DARK_BG);
        panel.add(titleLabel);

        JLabel subLabel = new JLabel("5 aeronaves no total");
        subLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        subLabel.setForeground(GRAY_TEXT);
        panel.add(subLabel);

        panel.add(Box.createVerticalStrut(15));

        // Status list
        String[][] status = {
                {"Operacional", "4", new Color(34, 197, 94)},           // green
                {"Em Manutenção", "1", new Color(239, 68, 68)}          // red
        };

        for (String[] s : status) {
            JPanel statusRow = new JPanel(new BorderLayout(10, 0));
            statusRow.setOpaque(false);

            JLabel statusLabel = new JLabel(s[0]);
            statusLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            statusLabel.setForeground(GRAY_TEXT);

            JLabel countLabel = new JLabel(s[1]);
            countLabel.setFont(new Font("Inter", Font.BOLD, 12));
            countLabel.setForeground(DARK_BG);

            statusRow.add(statusLabel, BorderLayout.WEST);
            statusRow.add(countLabel, BorderLayout.EAST);

            panel.add(statusRow);
            panel.add(Box.createVerticalStrut(8));
        }

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createRecentActivityPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = new JLabel("Atividade Recente");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        titleLabel.setForeground(DARK_BG);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(15));

        // Activity items
        String[][] activities = {
                {"✓", "Voo CS-AER concluído", "João Silva · há 2h", SUCCESS_COLOR},
                {"⏳", "Manutenção CS-NAV iniciada", "Técnico · há 1h", WARNING_COLOR},
                {"✉", "Avaliação atualizada", "Instrutor · há 30m", BLUE_PRIMARY},
                {"💳", "Pagamento recebido", "€2000 · há 15m", SUCCESS_COLOR}
        };

        for (String[] activity : activities) {
            JPanel activityRow = new JPanel(new BorderLayout(15, 0));
            activityRow.setOpaque(false);

            JLabel icon = new JLabel(activity[0]);
            icon.setFont(new Font("Arial", Font.BOLD, 12));
            icon.setForeground(Color.decode(activity[3].substring(activity[3].lastIndexOf("R") + 1)));
            icon.setPreferredSize(new Dimension(30, 30));

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel titleActivity = new JLabel(activity[1]);
            titleActivity.setFont(new Font("Inter", Font.PLAIN, 12));
            titleActivity.setForeground(DARK_BG);
            textPanel.add(titleActivity);

            JLabel subActivity = new JLabel(activity[2]);
            subActivity.setFont(new Font("Inter", Font.PLAIN, 10));
            subActivity.setForeground(GRAY_TEXT);
            textPanel.add(subActivity);

            activityRow.add(icon, BorderLayout.WEST);
            activityRow.add(textPanel, BorderLayout.CENTER);

            panel.add(activityRow);
            panel.add(Box.createVerticalStrut(12));
        }

        return panel;
    }
}
