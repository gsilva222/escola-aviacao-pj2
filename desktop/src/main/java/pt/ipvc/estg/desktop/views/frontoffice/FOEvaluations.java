package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.desktop.controllers.EvaluationController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Avaliações do Aluno (FrontOffice)
 * Mostra histórico de avaliações do aluno
 */
public class FOEvaluations extends JPanel {

    private final Student student;
    private final EvaluationController evaluationController;

    public FOEvaluations(Student student) {
        this.student = student;
        this.evaluationController = new EvaluationController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new java.awt.BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new java.awt.Color(245, 245, 245));

        // Tabela de avaliações
        String[] columns = {"Data", "Tipo", "Disciplina", "Nota", "Status", "Feedback"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        
        List<Evaluation> myEvaluations = evaluationController.obterAvaliacoesPorEstudante(student.getId());
        
        if (myEvaluations != null) {
            for (Evaluation eval : myEvaluations) {
                tableModel.addRow(new Object[]{
                        eval.getEvaluationDate(),
                        eval.getEvaluationType() != null ? eval.getEvaluationType() : "Prático",
                        eval.getExamName() != null ? eval.getExamName() : "---",
                        eval.getScore() != null ? eval.getScore() : "---",
                        eval.getStatus() != null ? eval.getStatus() : "Pendente",
                        eval.getNotes() != null ? eval.getNotes() : "Sem feedback"
                });
            }
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new java.awt.BorderLayout());
        tablePanel.setBackground(java.awt.Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Minhas Avaliações"));
        tablePanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(tablePanel, java.awt.BorderLayout.CENTER);

        // Estatísticas
        double avgGrade = myEvaluations == null || myEvaluations.isEmpty() ? 0 : 
                myEvaluations.stream()
                        .filter(e -> e.getScore() != null)
                        .mapToDouble(Evaluation::getScore)
                        .average()
                        .orElse(0);
        
        int passedCount = myEvaluations == null ? 0 : (int) myEvaluations.stream()
                .filter(e -> "passed".equals(e.getStatus()))
                .count();

        JPanel summaryPanel = new JPanel(new java.awt.GridLayout(1, 3, 20, 0));
        summaryPanel.setOpaque(false);
        
        JPanel avgCard = createSummaryCard("Média Geral", String.format("%.1f", avgGrade), "/20");
        summaryPanel.add(avgCard);
        
        JPanel passedCard = createSummaryCard("Avaliações Passadas", 
                String.valueOf(passedCount) + "/" + (myEvaluations == null ? 0 : myEvaluations.size()), 
                (myEvaluations == null || myEvaluations.isEmpty() ? 0 : 
                        Math.round((passedCount * 100.0) / myEvaluations.size())) + "%");
        summaryPanel.add(passedCard);
        
        JPanel nextCard = createSummaryCard("Próxima Avaliação", "A designar", "Aguardando confirmação");
        summaryPanel.add(nextCard);

        JPanel northPanel = new JPanel(new java.awt.BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(summaryPanel, java.awt.BorderLayout.NORTH);

        add(northPanel, java.awt.BorderLayout.NORTH);
    }

    private JPanel createSummaryCard(String title, String value, String subtitle) {
        JPanel card = new JPanel();
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY, 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        titleLabel.setForeground(new java.awt.Color(100, 100, 100));
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        valueLabel.setForeground(new java.awt.Color(21, 101, 192));
        card.add(valueLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        subtitleLabel.setForeground(new java.awt.Color(150, 150, 150));
        card.add(subtitleLabel);

        return card;
    }
}
