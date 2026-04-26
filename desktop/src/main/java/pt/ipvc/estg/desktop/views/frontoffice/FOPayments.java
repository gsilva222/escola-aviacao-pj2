package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.controllers.PaymentController;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Pagamentos do Aluno (FrontOffice)
 * Versao visual alinhada ao mockup.
 */
public class FOPayments extends JPanel {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color SOFT = new Color(148, 163, 184);       // #94A3B8
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0
    private static final Color BLUE_DARK = new Color(13, 71, 161);    // #0D47A1
    private static final Color GREEN = new Color(22, 163, 74);        // #16A34A
    private static final Color ORANGE = new Color(217, 119, 6);       // #D97706
    private static final Color RED = new Color(220, 38, 38);          // #DC2626

    private static final Locale PT = Locale.forLanguageTag("pt-PT");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Student student;
    private final PaymentController paymentController;

    private final List<Payment> studentPayments = new ArrayList<>();
    private JPanel historyListPanel;

    public FOPayments(Student student) {
        this.student = student;
        this.paymentController = new PaymentController();
        initializeUI();
        loadPayments();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(center);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);

        top.add(createSummaryBanner());
        top.add(Box.createVerticalStrut(12));
        top.add(createStatsRow());

        center.add(createHistoryCard());
        center.add(Box.createVerticalStrut(12));
        center.add(createPaymentMethodsCard());
    }

    private void loadPayments() {
        studentPayments.clear();
        List<Payment> payments = paymentController.obterPagamentosPorEstudante(student.getId());
        if (payments != null) {
            studentPayments.addAll(payments);
        }
        studentPayments.sort(Comparator.comparing(Payment::getDueDate, Comparator.nullsLast(Comparator.reverseOrder())));
        removeAll();
        initializeUI();
        revalidate();
        repaint();
    }

    private JPanel createSummaryBanner() {
        double totalPaid = studentPayments.stream()
                .filter(p -> "paid".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalPending = studentPayments.stream()
                .filter(p -> "pending".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalOverdue = studentPayments.stream()
                .filter(p -> "overdue".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalCourse = totalPaid + totalPending + totalOverdue;
        int progress = totalCourse <= 0 ? 0 : (int) Math.round((totalPaid * 100.0) / totalCourse);

        JPanel banner = new JPanel(new BorderLayout(10, 0));
        banner.setBackground(TITLE);
        banner.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel small = new JLabel("Resumo Financeiro");
        small.setForeground(new Color(147, 197, 253));
        small.setFont(new Font("Inter", Font.PLAIN, 12));
        left.add(small);

        JLabel paid = new JLabel(formatEuro(totalPaid) + " pagos");
        paid.setForeground(Color.WHITE);
        paid.setFont(new Font("Inter", Font.BOLD, 28));
        left.add(paid);

        JLabel total = new JLabel("de " + formatEuro(totalCourse) + " total do curso");
        total.setForeground(new Color(147, 197, 253));
        total.setFont(new Font("Inter", Font.PLAIN, 12));
        left.add(total);
        left.add(Box.createVerticalStrut(10));

        JLabel progressLabel = new JLabel("Progresso de pagamento: " + progress + "%");
        progressLabel.setForeground(new Color(191, 219, 254));
        progressLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        left.add(progressLabel);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setForeground(new Color(52, 211, 153));
        progressBar.setBackground(new Color(59, 130, 246, 70));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(260, 10));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        left.add(Box.createVerticalStrut(6));
        left.add(progressBar);

        banner.add(left, BorderLayout.CENTER);

        if (totalPending + totalOverdue > 0) {
            JPanel pending = new JPanel();
            pending.setOpaque(true);
            pending.setBackground(new Color(245, 158, 11, 60));
            pending.setLayout(new BoxLayout(pending, BoxLayout.Y_AXIS));
            pending.setBorder(new EmptyBorder(12, 14, 12, 14));

            JLabel value = new JLabel(formatEuro(totalPending + totalOverdue));
            value.setForeground(new Color(254, 240, 138));
            value.setFont(new Font("Inter", Font.BOLD, 20));
            JLabel desc = new JLabel("Pendente");
            desc.setForeground(new Color(252, 211, 77));
            desc.setFont(new Font("Inter", Font.PLAIN, 11));
            pending.add(value);
            pending.add(desc);
            banner.add(pending, BorderLayout.EAST);
        }

        return banner;
    }

    private JPanel createStatsRow() {
        double totalPaid = studentPayments.stream()
                .filter(p -> "paid".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalPending = studentPayments.stream()
                .filter(p -> !"paid".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        long receipts = studentPayments.stream().filter(p -> "paid".equals(resolveStatus(p))).count();

        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setOpaque(false);
        panel.add(createStatCard("Total Pago", formatEuro(totalPaid), GREEN, new Color(220, 252, 231)));
        panel.add(createStatCard("Pendente", formatEuro(totalPending), ORANGE, new Color(254, 243, 199)));
        panel.add(createStatCard("Recibos Emitidos", String.valueOf(receipts), BLUE, new Color(219, 234, 254)));
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, Color bg) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel icon = new JPanel(new GridBagLayout());
        icon.setOpaque(true);
        icon.setBackground(bg);
        icon.setPreferredSize(new Dimension(34, 34));
        JLabel iconText = new JLabel("$");
        iconText.setForeground(color);
        iconText.setFont(new Font("Inter", Font.BOLD, 12));
        icon.add(iconText);

        JPanel iconWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconWrap.setOpaque(false);
        iconWrap.add(icon);
        card.add(iconWrap);
        card.add(Box.createVerticalStrut(8));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 22));
        card.add(valueLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(MUTED);
        titleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        card.add(titleLabel);
        return card;
    }

    private JPanel createHistoryCard() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(WHITE);
        header.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel title = new JLabel("Historico de Pagamentos");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        header.add(title, BorderLayout.WEST);

        JButton exportButton = new JButton("Exportar extrato");
        styleSecondaryButton(exportButton);
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Exportacao em desenvolvimento.",
                "Informacao",
                JOptionPane.INFORMATION_MESSAGE
        ));
        header.add(exportButton, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        historyListPanel = new JPanel();
        historyListPanel.setOpaque(true);
        historyListPanel.setBackground(WHITE);
        historyListPanel.setLayout(new BoxLayout(historyListPanel, BoxLayout.Y_AXIS));
        historyListPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        if (studentPayments.isEmpty()) {
            JLabel empty = new JLabel("Sem pagamentos registados.");
            empty.setForeground(SOFT);
            empty.setFont(new Font("Inter", Font.PLAIN, 12));
            empty.setBorder(new EmptyBorder(20, 14, 20, 14));
            historyListPanel.add(empty);
        } else {
            for (int i = 0; i < studentPayments.size(); i++) {
                historyListPanel.add(createPaymentRow(studentPayments.get(i)));
                if (i < studentPayments.size() - 1) {
                    historyListPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
                }
            }
        }

        card.add(historyListPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createPaymentRow(Payment payment) {
        String status = resolveStatus(payment);
        Color statusBg = switch (status) {
            case "paid" -> new Color(220, 252, 231);
            case "overdue" -> new Color(254, 226, 226);
            default -> new Color(254, 243, 199);
        };
        Color statusColor = switch (status) {
            case "paid" -> GREEN;
            case "overdue" -> RED;
            default -> ORANGE;
        };
        String statusLabel = switch (status) {
            case "paid" -> "Pago";
            case "overdue" -> "Em Atraso";
            default -> "Pendente";
        };

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(true);
        row.setBackground(WHITE);
        row.setBorder(new EmptyBorder(10, 12, 10, 12));

        JPanel icon = new JPanel(new GridBagLayout());
        icon.setOpaque(true);
        icon.setBackground(statusBg);
        icon.setPreferredSize(new Dimension(38, 38));
        JLabel iconText = new JLabel("paid".equals(status) ? "\u2714" : ("overdue".equals(status) ? "!" : "\u23F3"));
        iconText.setForeground(statusColor);
        iconText.setFont(new Font("Dialog", Font.BOLD, 12));
        icon.add(iconText);
        row.add(icon, BorderLayout.WEST);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel description = new JLabel(safeText(payment.getDescription(), "Mensalidade"));
        description.setForeground(TITLE);
        description.setFont(new Font("Inter", Font.BOLD, 12));
        details.add(description);

        String dueText = "Vencimento: " + formatDate(payment.getDueDate());
        if (payment.getPaidDate() != null) {
            dueText += "  |  Pago em: " + formatDate(payment.getPaidDate());
        }
        JLabel dates = new JLabel(dueText);
        dates.setForeground(SOFT);
        dates.setFont(new Font("Inter", Font.PLAIN, 11));
        details.add(dates);
        row.add(details, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        right.setOpaque(false);

        JLabel amount = new JLabel(formatEuro(payment.getAmount() != null ? payment.getAmount() : 0.0));
        amount.setForeground(TITLE);
        amount.setFont(new Font("Inter", Font.BOLD, 16));
        right.add(amount);

        JLabel badge = new JLabel(statusLabel, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(statusBg);
        badge.setForeground(statusColor);
        badge.setFont(new Font("Inter", Font.BOLD, 11));
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));
        right.add(badge);

        JButton action = new JButton("paid".equals(status) ? "Recibo" : "Pagar agora");
        if ("paid".equals(status)) {
            styleSecondaryButton(action);
            action.addActionListener(e -> JOptionPane.showMessageDialog(
                    this,
                    "Download de recibo em desenvolvimento.",
                    "Informacao",
                    JOptionPane.INFORMATION_MESSAGE
            ));
        } else {
            stylePrimaryButton(action);
            action.addActionListener(e -> JOptionPane.showMessageDialog(
                    this,
                    "Pagamento online em desenvolvimento.",
                    "Informacao",
                    JOptionPane.INFORMATION_MESSAGE
            ));
        }
        right.add(action);

        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel createPaymentMethodsCard() {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JLabel title = new JLabel("Metodos de Pagamento Disponiveis");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 14));
        card.add(title);
        card.add(Box.createVerticalStrut(10));

        JPanel methods = new JPanel(new GridLayout(2, 2, 8, 8));
        methods.setOpaque(false);
        methods.add(createMethodCard("\uD83C\uDFE6", "Transferencia Bancaria"));
        methods.add(createMethodCard("\uD83D\uDCF1", "MB Way"));
        methods.add(createMethodCard("\uD83D\uDCB3", "Cartao Debito/Credito"));
        methods.add(createMethodCard("\uD83C\uDFE7", "Multibanco Referencia"));
        card.add(methods);
        card.add(Box.createVerticalStrut(10));

        JLabel help = new JLabel("<html><body style='width:780px'>"
                + "Para pagamentos por transferencia, utilize o IBAN indicado pela secretaria e inclua o numero do aluno na referencia. "
                + "Para suporte contacte: secretaria@aeroschool.pt"
                + "</body></html>");
        help.setForeground(SOFT);
        help.setFont(new Font("Inter", Font.PLAIN, 11));
        card.add(help);

        return card;
    }

    private JPanel createMethodCard(String icon, String label) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(248, 250, 252));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel text = new JLabel(label);
        text.setForeground(new Color(55, 65, 81));
        text.setFont(new Font("Inter", Font.BOLD, 11));
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(text);
        return card;
    }

    private String resolveStatus(Payment payment) {
        String raw = payment.getStatus() == null ? "" : payment.getStatus().trim().toLowerCase(Locale.ROOT);
        if ("paid".equals(raw) || "completed".equals(raw)) {
            return "paid";
        }
        if ("overdue".equals(raw)) {
            return "overdue";
        }
        if (payment.getPaidDate() != null) {
            return "paid";
        }
        if (payment.getDueDate() != null && payment.getDueDate().isBefore(LocalDate.now())) {
            return "overdue";
        }
        return "pending";
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FMT);
    }

    private String formatEuro(double value) {
        return String.format(PT, "\u20AC%.0f", value);
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE_DARK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(7, 10, 7, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 11));
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 9, 6, 9)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
