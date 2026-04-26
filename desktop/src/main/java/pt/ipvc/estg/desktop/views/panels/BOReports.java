package pt.ipvc.estg.desktop.views.panels;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de relatorios com charts e exportacao simplificada.
 */
public class BOReports extends JPanel {

    private String selectedPeriod = "jan-mar-2025";

    public BOReports() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createKPIPanel(), BorderLayout.BEFORE_FIRST_LINE);

        JPanel chartsPanel = createChartsPanel();
        JScrollPane scrollPane = new JScrollPane(chartsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        periodPanel.setOpaque(false);
        periodPanel.add(new JLabel("Periodo:"));

        String[] periods = {"Jan-Mar 2025", "2024", "Personalizado"};
        for (String period : periods) {
            JButton btn = new JButton(period);
            btn.addActionListener(e -> selectedPeriod = period.toLowerCase().replace(" ", "-"));
            periodPanel.add(btn);
        }

        panel.add(periodPanel, BorderLayout.WEST);

        JButton exportBtn = new JButton("Exportar Relatorio PDF");
        exportBtn.setBackground(new Color(21, 101, 192));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.addActionListener(e -> exportPDF());
        panel.add(exportBtn, BorderLayout.EAST);

        return panel;
    }

    private JPanel createKPIPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createKPICard("Total de Alunos", "40", "+12% vs ano anterior"));
        panel.add(createKPICard("Horas Voadas 2025", "900h", "de 1200h meta"));
        panel.add(createKPICard("Taxa de Aprovacao", "87%", "nota media 17.5"));
        panel.add(createKPICard("Receita Acumulada", "EUR 148k", "Jan-Mar 2025"));

        return panel;
    }

    private JPanel createKPICard(String title, String value, String subtitle) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(100, 100, 100));
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(new Color(21, 101, 192));
        card.add(valueLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        card.add(subtitleLabel);

        return card;
    }

    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        panel.add(createRevenueChartPanel());
        panel.add(createCoursePieChartPanel());
        panel.add(createApprovalPlaceholderPanel());
        panel.add(createWorkloadTablePanel());

        return panel;
    }

    private JPanel createRevenueChartPanel() {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Receita vs Despesa (EUR)"));

        XYChart chart = new XYChartBuilder()
                .width(500).height(300)
                .title("Receita vs Despesa")
                .xAxisTitle("Mes")
                .yAxisTitle("Valor")
                .build();

        chart.addSeries("Receita", new double[]{1, 2, 3}, new double[]{5000, 6500, 7200});
        chart.addSeries("Despesa", new double[]{1, 2, 3}, new double[]{3000, 3500, 4000});
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

        chartPanel.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        return chartPanel;
    }

    private JPanel createCoursePieChartPanel() {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Distribuicao por Curso"));

        PieChart pieChart = new PieChartBuilder()
                .width(500).height(300)
                .title("Alunos por Curso")
                .build();

        pieChart.addSeries("PPL", 20);
        pieChart.addSeries("CPL", 12);
        pieChart.addSeries("IR", 5);
        pieChart.addSeries("ATPL", 3);

        chartPanel.add(new XChartPanel<>(pieChart), BorderLayout.CENTER);
        return chartPanel;
    }

    private JPanel createApprovalPlaceholderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Taxa de Aprovacao por Curso"));
        panel.add(new JLabel("Grafico de taxa de aprovacao (placeholder)", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createWorkloadTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Carga de Trabalho - Instrutores"));

        String[] columns = {"Instrutor", "Horas/Mes", "Nr Alunos", "Taxa Ocupacao"};
        Object[][] data = {
                {"Capt. Ferreira", "120h", "8", "92%"},
                {"Capt. Lopes", "110h", "7", "88%"},
                {"Capt. Silva", "95h", "6", "78%"},
                {"Capt. Oliveira", "85h", "5", "70%"},
        };

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setEnabled(false);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void exportPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Relatorio_AeroSchool_" + selectedPeriod + ".pdf"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                generatePDF(filePath);
                JOptionPane.showMessageDialog(
                        this,
                        "Relatorio exportado com sucesso!\n" + filePath,
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
    }

    private void generatePDF(String filePath) throws Exception {
        try {
            java.nio.file.Files.write(
                    java.nio.file.Paths.get(filePath),
                    ("Relatorio de Analise - AeroSchool\n" +
                            "Periodo: " + selectedPeriod + "\n\n" +
                            "KPIs:\n" +
                            "- Total de Alunos: 40 (+12%)\n" +
                            "- Horas Voadas: 900h de 1200h\n" +
                            "- Taxa de Aprovacao: 87%\n" +
                            "- Receita Acumulada: EUR 148k").getBytes()
            );
        } catch (Exception e) {
            throw new Exception("Erro ao exportar relatorio: " + e.getMessage());
        }
    }
}
