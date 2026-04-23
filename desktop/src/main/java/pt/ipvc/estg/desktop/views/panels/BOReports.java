package pt.ipvc.estg.desktop.views.panels;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.AreaChart;
import org.knowm.xchart.AreaChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Painel de Relatórios Avançados e Exportação de PDF
 */
public class BOReports extends JPanel {

    private JPanel chartsPanel;
    private String selectedPeriod = "jan-mar-2025";

    public BOReports() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // Header com controls
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // KPI Cards
        JPanel kpiPanel = createKPIPanel();
        add(kpiPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Charts e tabelas
        chartsPanel = createChartsPanel();
        JScrollPane scrollPane = new JScrollPane(chartsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Período selector
        JPanel periodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        periodPanel.setOpaque(false);
        periodPanel.add(new JLabel("Período:"));

        String[] periods = {"Jan-Mar 2025", "2024", "Personalizado"};
        for (String period : periods) {
            JButton btn = new JButton(period);
            btn.addActionListener(e -> {
                selectedPeriod = period.toLowerCase().replace(" ", "-").replace("-", "-");
                loadCharts();
            });
            periodPanel.add(btn);
        }

        panel.add(periodPanel, BorderLayout.WEST);

        // Export button
        JButton exportBtn = new JButton("📥 Exportar Relatório PDF");
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
        panel.add(createKPICard("Taxa de Aprovação", "87%", "nota média 17.5"));
        panel.add(createKPICard("Receita Acumulada", "€148k", "Jan-Mar 2025"));

        return panel;
    }

    private JPanel createKPICard(String title, String value, String subtitle) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

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
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 245));

        // Chart 1: Receita vs Despesa (AreaChart)
        JPanel chart1Panel = new JPanel(new BorderLayout());
        chart1Panel.setBackground(Color.WHITE);
        chart1Panel.setBorder(BorderFactory.createTitledBorder("Receita vs Despesa (€)"));

        try {
            AreaChart areaChart = new AreaChartBuilder()
                    .width(500).height(300)
                    .title("Receita vs Despesa")
                    .xAxisTitle("Mês")
                    .yAxisTitle("Valor (€)")
                    .build();

            areaChart.addSeries("Receita", new double[]{1, 2, 3}, new double[]{5000, 6500, 7200});
            areaChart.addSeries("Despesa", new double[]{1, 2, 3}, new double[]{3000, 3500, 4000});

            chart1Panel.add(new XChartPanel(areaChart), BorderLayout.CENTER);
        } catch (Exception e) {
            chart1Panel.add(new JLabel("Erro ao carregar gráfico: " + e.getMessage()));
        }

        panel.add(chart1Panel);

        // Chart 2: Distribuição por Curso (PieChart)
        JPanel chart2Panel = new JPanel(new BorderLayout());
        chart2Panel.setBackground(Color.WHITE);
        chart2Panel.setBorder(BorderFactory.createTitledBorder("Distribuição por Curso"));

        try {
            PieChart pieChart = new PieChartBuilder()
                    .width(500).height(300)
                    .title("Alunos por Curso")
                    .build();

            pieChart.addSeries("PPL", 20);
            pieChart.addSeries("CPL", 12);
            pieChart.addSeries("IR", 5);
            pieChart.addSeries("ATPL", 3);

            chart2Panel.add(new XChartPanel(pieChart), BorderLayout.CENTER);
        } catch (Exception e) {
            chart2Panel.add(new JLabel("Erro ao carregar gráfico: " + e.getMessage()));
        }

        panel.add(chart2Panel);

        // Chart 3: Taxa de Aprovação por Curso (BarChart)
        JPanel chart3Panel = new JPanel(new BorderLayout());
        chart3Panel.setBackground(Color.WHITE);
        chart3Panel.setBorder(BorderFactory.createTitledBorder("Taxa de Aprovação por Curso"));

        try {
            org.knowm.xchart.BarChart barChart = new org.knowm.xchart.BarChartBuilder()
                    .width(500).height(300)
                    .title("Taxa de Aprovação (%)")
                    .xAxisTitle("Curso")
                    .yAxisTitle("Taxa (%)")
                    .build();

            barChart.addSeries("Taxa", new double[]{85, 82, 90, 88}, new double[]{1, 2, 3, 4});

            chart3Panel.add(new XChartPanel(barChart), BorderLayout.CENTER);
        } catch (Exception e) {
            chart3Panel.add(new JLabel("Erro ao carregar gráfico: " + e.getMessage()));
        }

        panel.add(chart3Panel);

        // Table 4: Carga de Trabalho Instrutores
        JPanel table4Panel = new JPanel(new BorderLayout());
        table4Panel.setBackground(Color.WHITE);
        table4Panel.setBorder(BorderFactory.createTitledBorder("Carga de Trabalho - Instrutores"));

        String[] columns = {"Instrutor", "Horas/Mês", "Nº Alunos", "Taxa Ocupação"};
        Object[][] data = {
                {"Capt. Ferreira", "120h", "8", "92%"},
                {"Capt. Lopes", "110h", "7", "88%"},
                {"Capt. Silva", "95h", "6", "78%"},
                {"Capt. Oliveira", "85h", "5", "70%"},
        };

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);

        table4Panel.add(scrollPane, BorderLayout.CENTER);

        panel.add(table4Panel);

        return panel;
    }

    private void loadCharts() {
        chartsPanel.removeAll();
        JPanel newChartsPanel = createChartsPanel();
        chartsPanel = newChartsPanel;
        
        // Atualizar o painel
        Container parent = getParent();
        if (parent != null) {
            parent.validate();
            parent.repaint();
        }
    }

    private void exportPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Relatório_AeroSchool_" + selectedPeriod + ".pdf"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                generatePDF(filePath);
                JOptionPane.showMessageDialog(this, 
                        "Relatório exportado com sucesso!\n" + filePath,
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao exportar PDF: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generatePDF(String filePath) throws Exception {
        // Usando iText para criar PDF
        com.itextpdf.kernel.pdf.PdfDocument pdfDoc = 
            new com.itextpdf.kernel.pdf.PdfDocument(
                new com.itextpdf.kernel.pdf.PdfWriter(filePath));
        
        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);

        // Título
        com.itextpdf.layout.element.Paragraph title = 
            new com.itextpdf.layout.element.Paragraph("Relatório de Análise - AeroSchool")
                .setFontSize(20)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
        document.add(title);

        // Data
        com.itextpdf.layout.element.Paragraph date = 
            new com.itextpdf.layout.element.Paragraph("Período: " + selectedPeriod)
                .setFontSize(12)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
        document.add(date);

        // KPIs
        com.itextpdf.layout.element.Paragraph kpiTitle = 
            new com.itextpdf.layout.element.Paragraph("Indicadores Principais (KPIs)");
        document.add(kpiTitle);

        com.itextpdf.layout.element.Paragraph kpis = 
            new com.itextpdf.layout.element.Paragraph(
                "• Total de Alunos: 40 (+12%)\n" +
                "• Horas Voadas: 900h de 1200h\n" +
                "• Taxa de Aprovação: 87%\n" +
                "• Receita Acumulada: €148k"
            );
        document.add(kpis);

        document.close();
    }
}
