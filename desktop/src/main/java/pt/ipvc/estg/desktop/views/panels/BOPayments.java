package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.PaymentController;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BOPayments extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color TITLE = new Color(15, 35, 68);
    private static final Color BLUE = new Color(21, 101, 192);
    private static final Color BLUE_DARK = new Color(13, 71, 161);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color WARNING = new Color(217, 119, 6);
    private static final Color DANGER = new Color(220, 38, 38);

    private static final Locale PT = Locale.forLanguageTag("pt-PT");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PaymentController paymentController;
    private final StudentController studentController;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel statsLabel;
    private JButton registerButton;
    private JButton deleteButton;

    private JLabel revenueValueLabel;
    private JLabel revenueSubLabel;
    private JLabel pendingValueLabel;
    private JLabel pendingSubLabel;
    private JLabel overdueValueLabel;
    private JLabel overdueSubLabel;
    private JLabel collectionRateValueLabel;
    private JLabel collectionRateSubLabel;

    private JTable paymentsTable;
    private DefaultTableModel tableModel;

    private List<Student> students = new ArrayList<>();
    private List<Payment> allPayments = new ArrayList<>();
    private List<Payment> filteredPayments = new ArrayList<>();

    public BOPayments() {
        this.paymentController = new PaymentController();
        this.studentController = new StudentController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 14));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createSummaryPanel());
        top.add(Box.createVerticalStrut(12));
        top.add(createHeaderActionsPanel());
        top.add(Box.createVerticalStrut(12));
        top.add(createFiltersPanel());
        add(top, BorderLayout.NORTH);

        add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        panel.add(createSummaryCard("\u20AC", "Receita Recebida", SUCCESS, new Color(220, 252, 231), "revenue"));
        panel.add(createSummaryCard("\u23F3", "Pendente", WARNING, new Color(254, 243, 199), "pending"));
        panel.add(createSummaryCard("\u26A0", "Em Atraso", DANGER, new Color(254, 226, 226), "overdue"));
        panel.add(createSummaryCard("%", "Taxa de Cobranca", BLUE, new Color(219, 234, 254), "rate"));
        return panel;
    }

    private JPanel createSummaryCard(String iconText, String title, Color valueColor, Color iconBg, String key) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel iconWrap = new JPanel(new GridBagLayout());
        iconWrap.setOpaque(true);
        iconWrap.setBackground(iconBg);
        iconWrap.setPreferredSize(new Dimension(34, 34));
        JLabel icon = new JLabel(iconText);
        icon.setForeground(valueColor);
        icon.setFont(new Font("Inter", Font.BOLD, 12));
        iconWrap.add(icon);

        JPanel iconHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconHolder.setOpaque(false);
        iconHolder.add(iconWrap);
        card.add(iconHolder, BorderLayout.NORTH);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel value = new JLabel("EUR 0.00");
        value.setForeground(valueColor);
        value.setFont(new Font("Inter", Font.BOLD, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(55, 65, 81));
        titleLabel.setFont(new Font("Inter", Font.BOLD, 12));

        JLabel sub = new JLabel("-");
        sub.setForeground(new Color(148, 163, 184));
        sub.setFont(new Font("Inter", Font.PLAIN, 11));

        text.add(value);
        text.add(Box.createVerticalStrut(2));
        text.add(titleLabel);
        text.add(sub);
        card.add(text, BorderLayout.CENTER);

        switch (key) {
            case "revenue" -> {
                revenueValueLabel = value;
                revenueSubLabel = sub;
            }
            case "pending" -> {
                pendingValueLabel = value;
                pendingSubLabel = sub;
            }
            case "overdue" -> {
                overdueValueLabel = value;
                overdueSubLabel = sub;
            }
            case "rate" -> {
                collectionRateValueLabel = value;
                collectionRateSubLabel = sub;
            }
            default -> {
            }
        }

        return card;
    }

    private JPanel createHeaderActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        statsLabel = new JLabel("A carregar...");
        statsLabel.setForeground(MUTED);
        statsLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        panel.add(statsLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton refreshBtn = new JButton("Recarregar");
        styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadData());
        actions.add(refreshBtn);

        registerButton = new JButton("Registar");
        styleSecondaryButton(registerButton);
        registerButton.addActionListener(e -> registerSelectedPayment());
        actions.add(registerButton);

        deleteButton = new JButton("Eliminar");
        styleDangerButton(deleteButton);
        deleteButton.addActionListener(e -> deleteSelectedPayment());
        actions.add(deleteButton);

        JButton newPaymentBtn = new JButton("Novo Pagamento");
        stylePrimaryButton(newPaymentBtn);
        newPaymentBtn.addActionListener(e -> createPaymentDialog());
        actions.add(newPaymentBtn);

        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel createFiltersPanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(10, 0));

        searchField = new JTextField();
        searchField.setFont(new Font("Inter", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });
        card.add(searchField, BorderLayout.CENTER);

        statusFilter = new JComboBox<>(new String[]{"Todos os Estados", "paid", "pending", "overdue"});
        styleCombo(statusFilter);
        statusFilter.addActionListener(e -> applyFilters());
        card.add(statusFilter, BorderLayout.EAST);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout());

        String[] columns = {"Aluno", "Descricao", "Valor", "Vencimento", "Pagamento", "Metodo", "Estado", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentsTable = new JTable(tableModel);
        paymentsTable.setRowHeight(56);
        paymentsTable.setShowVerticalLines(false);
        paymentsTable.setGridColor(new Color(241, 245, 249));
        paymentsTable.setSelectionBackground(new Color(239, 246, 255));
        paymentsTable.setSelectionForeground(TITLE);
        paymentsTable.setIntercellSpacing(new Dimension(0, 1));
        paymentsTable.setBorder(null);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        paymentsTable.getSelectionModel().addListSelectionListener(e -> updateActionButtons());

        paymentsTable.getTableHeader().setReorderingAllowed(false);
        paymentsTable.getTableHeader().setBackground(new Color(248, 250, 252));
        paymentsTable.getTableHeader().setForeground(MUTED);
        paymentsTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 11));

        paymentsTable.getColumnModel().getColumn(0).setCellRenderer(new StudentPaymentRenderer());
        paymentsTable.getColumnModel().getColumn(2).setCellRenderer(new CurrencyRenderer());
        paymentsTable.getColumnModel().getColumn(3).setCellRenderer(new DateRenderer());
        paymentsTable.getColumnModel().getColumn(4).setCellRenderer(new DateRenderer());
        paymentsTable.getColumnModel().getColumn(5).setCellRenderer(new MethodRenderer());
        paymentsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        paymentsTable.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());

        paymentsTable.getColumnModel().getColumn(0).setPreferredWidth(260);
        paymentsTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        paymentsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        paymentsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        paymentsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        paymentsTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        paymentsTable.getColumnModel().getColumn(6).setPreferredWidth(110);
        paymentsTable.getColumnModel().getColumn(7).setPreferredWidth(90);

        paymentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = paymentsTable.rowAtPoint(e.getPoint());
                int col = paymentsTable.columnAtPoint(e.getPoint());
                if (row < 0) {
                    return;
                }
                if (col == 7 && e.getClickCount() == 1) {
                    registerPaymentByRow(row);
                }
                if (e.getClickCount() == 2) {
                    paymentsTable.setRowSelectionInterval(row, row);
                    registerPaymentByRow(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(paymentsTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        students = studentController.listarEstudantes();
        allPayments = new ArrayList<>(paymentController.listarPagamentos());
        allPayments.sort(Comparator.comparing(Payment::getDueDate, Comparator.nullsLast(Comparator.reverseOrder())));
        applyFilters();
        updateSummaryCards();
    }

    private void applyFilters() {
        String search = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String status = statusFilter == null ? "Todos os Estados" : String.valueOf(statusFilter.getSelectedItem());

        filteredPayments = allPayments.stream()
                .filter(payment -> {
                    String studentName = payment.getStudent() != null ? safeLower(payment.getStudent().getName()) : "";
                    String studentEmail = payment.getStudent() != null ? safeLower(payment.getStudent().getEmail()) : "";
                    String description = safeLower(payment.getDescription());

                    boolean matchSearch = search.isEmpty()
                            || studentName.contains(search)
                            || studentEmail.contains(search)
                            || description.contains(search);

                    String resolvedStatus = resolveStatus(payment);
                    boolean matchStatus = "Todos os Estados".equals(status) || status.equals(resolvedStatus);
                    return matchSearch && matchStatus;
                })
                .toList();

        refreshTable();
        updateStatsLabel();
        updateActionButtons();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Payment payment : filteredPayments) {
            tableModel.addRow(new Object[]{
                    payment,
                    payment.getDescription(),
                    payment.getAmount(),
                    payment.getDueDate(),
                    payment.getPaidDate(),
                    payment.getPaymentMethod(),
                    resolveStatus(payment),
                    resolveStatus(payment).equals("paid") ? "-" : "Registar"
            });
        }
    }

    private void updateStatsLabel() {
        long pending = filteredPayments.stream().filter(p -> "pending".equals(resolveStatus(p))).count();
        long overdue = filteredPayments.stream().filter(p -> "overdue".equals(resolveStatus(p))).count();
        statsLabel.setText(filteredPayments.size() + " pagamentos visiveis - " + pending + " pendentes - " + overdue + " em atraso");
    }

    private void updateSummaryCards() {
        double totalPaid = allPayments.stream()
                .filter(p -> "paid".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalPending = allPayments.stream()
                .filter(p -> "pending".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalOverdue = allPayments.stream()
                .filter(p -> "overdue".equals(resolveStatus(p)))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();

        long paidCount = allPayments.stream().filter(p -> "paid".equals(resolveStatus(p))).count();
        long totalCount = allPayments.size();
        int rate = totalCount == 0 ? 0 : (int) Math.round((paidCount * 100.0) / totalCount);

        revenueValueLabel.setText(formatEuro(totalPaid));
        revenueSubLabel.setText("Total recebido");
        pendingValueLabel.setText(formatEuro(totalPending));
        pendingSubLabel.setText(countByStatus("pending") + " pagamentos");
        overdueValueLabel.setText(formatEuro(totalOverdue));
        overdueSubLabel.setText(countByStatus("overdue") + " pagamentos");
        collectionRateValueLabel.setText(rate + "%");
        collectionRateSubLabel.setText(paidCount + " de " + totalCount + " pagos");
    }

    private long countByStatus(String status) {
        return allPayments.stream().filter(p -> status.equals(resolveStatus(p))).count();
    }

    private String resolveStatus(Payment payment) {
        String status = safeLower(payment.getStatus());
        if ("paid".equals(status)) {
            return "paid";
        }
        if ("overdue".equals(status)) {
            return "overdue";
        }
        if ("pending".equals(status) && payment.getDueDate() != null && payment.getDueDate().isBefore(LocalDate.now())) {
            return "overdue";
        }
        return "pending";
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private void createPaymentDialog() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nao existem estudantes para associar ao pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<StudentChoice> studentCombo = new JComboBox<>();
        for (Student student : students) {
            studentCombo.addItem(new StudentChoice(student));
        }

        JTextField descriptionField = new JTextField();
        JTextField amountField = new JTextField();
        SpinnerDateModel dateModel = new SpinnerDateModel(
                Date.from(LocalDate.now().plusDays(15).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH
        );
        JSpinner dueDateSpinner = new JSpinner(dateModel);
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "dd/MM/yyyy"));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Estudante"));
        form.add(studentCombo);
        form.add(new JLabel("Descricao"));
        form.add(descriptionField);
        form.add(new JLabel("Valor (EUR)"));
        form.add(amountField);
        form.add(new JLabel("Data de vencimento"));
        form.add(dueDateSpinner);

        int result = JOptionPane.showConfirmDialog(this, form, "Novo Pagamento", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            StudentChoice choice = (StudentChoice) studentCombo.getSelectedItem();
            if (choice == null) {
                throw new IllegalArgumentException("Estudante invalido.");
            }
            String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
            if (description.isEmpty()) {
                throw new IllegalArgumentException("Descricao obrigatoria.");
            }

            double amount = Double.parseDouble(amountField.getText().trim().replace(",", "."));
            if (amount <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero.");
            }

            Date selectedDate = (Date) dueDateSpinner.getValue();
            LocalDate dueDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            paymentController.criarPagamento(choice.student, description, amount, dueDate);
            loadData();
            JOptionPane.showMessageDialog(this, "Pagamento criado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor invalido. Use apenas numeros.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar pagamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerSelectedPayment() {
        int row = paymentsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        registerPaymentByRow(row);
    }

    private void registerPaymentByRow(int row) {
        if (row < 0 || row >= filteredPayments.size()) {
            return;
        }

        Payment payment = filteredPayments.get(row);
        if ("paid".equals(resolveStatus(payment))) {
            JOptionPane.showMessageDialog(this, "Este pagamento ja esta registado.", "Informacao", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] methods = {"cash", "card", "transfer", "check"};
        String selectedMethod = (String) JOptionPane.showInputDialog(
                this,
                "Escolha o metodo de pagamento:",
                "Registar Pagamento",
                JOptionPane.PLAIN_MESSAGE,
                null,
                methods,
                methods[0]
        );

        if (selectedMethod == null) {
            return;
        }

        try {
            paymentController.registarPagamento(payment.getId(), LocalDate.now(), selectedMethod);
            loadData();
            JOptionPane.showMessageDialog(this, "Pagamento registado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registar pagamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedPayment() {
        int row = paymentsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Payment payment = filteredPayments.get(row);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirma eliminacao do pagamento #" + payment.getId() + "?",
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            paymentController.eliminarPagamento(payment.getId());
            loadData();
            JOptionPane.showMessageDialog(this, "Pagamento eliminado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao eliminar pagamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateActionButtons() {
        int row = paymentsTable == null ? -1 : paymentsTable.getSelectedRow();
        boolean hasSelection = row >= 0 && row < filteredPayments.size();
        deleteButton.setEnabled(hasSelection);
        if (!hasSelection) {
            registerButton.setEnabled(false);
            return;
        }
        Payment selected = filteredPayments.get(row);
        registerButton.setEnabled(!"paid".equals(resolveStatus(selected)));
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return panel;
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 12));
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(9, 12, 9, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Inter", Font.PLAIN, 12));
        combo.setBackground(WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private String formatEuro(double value) {
        return String.format(PT, "EUR %,.2f", value);
    }

    private static class StudentChoice {
        private final Student student;

        private StudentChoice(Student student) {
            this.student = student;
        }

        @Override
        public String toString() {
            if (student == null) {
                return "N/A";
            }
            String name = student.getName() != null ? student.getName() : "Sem nome";
            String email = student.getEmail() != null ? student.getEmail() : "";
            return name + " - " + email;
        }
    }

    private static class StudentPaymentRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Payment payment = (Payment) value;
            Student student = payment != null ? payment.getStudent() : null;

            JPanel panel = new JPanel(new BorderLayout(8, 0));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : WHITE);
            panel.setBorder(new EmptyBorder(6, 8, 6, 8));

            JLabel avatar = new JLabel(initialsOf(student), SwingConstants.CENTER);
            avatar.setOpaque(true);
            avatar.setForeground(Color.WHITE);
            avatar.setBackground(colorFor(student));
            avatar.setPreferredSize(new Dimension(30, 30));
            avatar.setFont(new Font("Inter", Font.BOLD, 10));

            JPanel text = new JPanel();
            text.setOpaque(false);
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(student != null && student.getName() != null ? student.getName() : "N/A");
            name.setForeground(TITLE);
            name.setFont(new Font("Inter", Font.BOLD, 12));

            JLabel email = new JLabel(student != null && student.getEmail() != null ? student.getEmail() : "");
            email.setForeground(new Color(148, 163, 184));
            email.setFont(new Font("Inter", Font.PLAIN, 11));

            text.add(name);
            text.add(email);

            panel.add(avatar, BorderLayout.WEST);
            panel.add(text, BorderLayout.CENTER);
            return panel;
        }

        private String initialsOf(Student student) {
            if (student == null || student.getName() == null || student.getName().isBlank()) {
                return "AL";
            }
            String[] parts = student.getName().trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
            }
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }

        private Color colorFor(Student student) {
            Color[] palette = {
                    new Color(21, 101, 192),
                    new Color(37, 99, 235),
                    new Color(14, 116, 144),
                    new Color(79, 70, 229),
                    new Color(124, 58, 237)
            };
            int hash = student == null || student.getName() == null ? 0 : Math.abs(student.getName().hashCode());
            return palette[hash % palette.length];
        }
    }

    private static class CurrencyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            double amount = value instanceof Number ? ((Number) value).doubleValue() : 0.0;
            label.setText(String.format(PT, "EUR %,.2f", amount));
            label.setForeground(TITLE);
            label.setFont(new Font("Inter", Font.BOLD, 12));
            return label;
        }
    }

    private static class DateRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof LocalDate date) {
                label.setText(date.format(DATE_FORMAT));
                label.setForeground(MUTED);
            } else {
                label.setText("-");
                label.setForeground(new Color(148, 163, 184));
            }
            label.setFont(new Font("Inter", Font.PLAIN, 12));
            return label;
        }
    }

    private static class MethodRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String method = value == null ? "" : value.toString().trim();
            if (method.isEmpty()) {
                label.setText("-");
                label.setForeground(new Color(148, 163, 184));
            } else {
                label.setText(method);
                label.setForeground(MUTED);
            }
            label.setFont(new Font("Inter", Font.PLAIN, 12));
            return label;
        }
    }

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));

            String status = String.valueOf(value);
            switch (status) {
                case "paid" -> {
                    label.setText("Pago");
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(SUCCESS);
                }
                case "overdue" -> {
                    label.setText("Em Atraso");
                    label.setBackground(new Color(254, 226, 226));
                    label.setForeground(DANGER);
                }
                default -> {
                    label.setText("Pendente");
                    label.setBackground(new Color(254, 243, 199));
                    label.setForeground(WARNING);
                }
            }
            return label;
        }
    }

    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(String.valueOf(value), SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 11));
            label.setOpaque(true);
            label.setBorder(new EmptyBorder(4, 8, 4, 8));

            if ("Registar".equals(value)) {
                label.setBackground(BLUE_DARK);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(isSelected ? table.getSelectionBackground() : WHITE);
                label.setForeground(new Color(203, 213, 225));
            }
            return label;
        }
    }
}
