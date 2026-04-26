package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.AircraftController;
import pt.ipvc.estg.desktop.controllers.MaintenanceController;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Maintenance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BOMaintenance extends JPanel {

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
    private static final Color PURPLE = new Color(124, 58, 237);

    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    private final MaintenanceController maintenanceController;
    private final AircraftController aircraftController;

    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel listStatsLabel;

    private JLabel inProgressValueLabel;
    private JLabel waitingPartsValueLabel;
    private JLabel scheduledValueLabel;
    private JLabel costValueLabel;

    private final DefaultListModel<Maintenance> listModel = new DefaultListModel<>();
    private JList<Maintenance> maintenanceList;
    private JPanel detailPanel;

    private JButton completeButton;
    private JButton updateButton;
    private JButton deleteButton;

    private List<Aircraft> aircraftList = new ArrayList<>();
    private List<Maintenance> allMaintenances = new ArrayList<>();
    private List<Maintenance> filteredMaintenances = new ArrayList<>();
    private Integer selectedMaintenanceId;

    public BOMaintenance() {
        this.maintenanceController = new MaintenanceController();
        this.aircraftController = new AircraftController();
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
        top.add(createHeaderPanel());
        add(top, BorderLayout.NORTH);

        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        panel.add(createSummaryCard("Em Curso", new Color(29, 78, 216), new Color(219, 234, 254), "inProgress"));
        panel.add(createSummaryCard("Aguarda Pecas", WARNING, new Color(254, 243, 199), "waitingParts"));
        panel.add(createSummaryCard("Agendadas", PURPLE, new Color(243, 232, 255), "scheduled"));
        panel.add(createSummaryCard("Custo Total", TITLE, new Color(241, 245, 249), "cost"));
        return panel;
    }

    private JPanel createSummaryCard(String title, Color valueColor, Color bgColor, String key) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel accent = new JPanel();
        accent.setPreferredSize(new Dimension(26, 4));
        accent.setBackground(bgColor.darker());

        JLabel value = new JLabel("0");
        value.setForeground(valueColor);
        value.setFont(new Font("Inter", Font.BOLD, 22));

        JLabel subtitle = new JLabel(title);
        subtitle.setForeground(new Color(100, 116, 139));
        subtitle.setFont(new Font("Inter", Font.PLAIN, 12));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(value);
        text.add(Box.createVerticalStrut(2));
        text.add(subtitle);

        card.add(accent, BorderLayout.NORTH);
        card.add(text, BorderLayout.CENTER);

        switch (key) {
            case "inProgress" -> inProgressValueLabel = value;
            case "waitingParts" -> waitingPartsValueLabel = value;
            case "scheduled" -> scheduledValueLabel = value;
            case "cost" -> costValueLabel = value;
            default -> {
            }
        }
        return card;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        listStatsLabel = new JLabel("A carregar...");
        listStatsLabel.setForeground(MUTED);
        listStatsLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        panel.add(listStatsLabel, BorderLayout.WEST);

        JButton newMaintenanceButton = new JButton("Nova Intervencao");
        stylePrimaryButton(newMaintenanceButton);
        newMaintenanceButton.addActionListener(e -> createMaintenanceDialog());
        panel.add(newMaintenanceButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setOpaque(false);
        panel.add(createListPanel());
        panel.add(createDetailPanel());
        return panel;
    }

    private JPanel createListPanel() {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel filters = new JPanel(new BorderLayout(8, 0));
        filters.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Inter", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 8, 8, 8)
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
        filters.add(searchField, BorderLayout.CENTER);

        statusFilter = new JComboBox<>(new String[]{"Todos", "in_progress", "waiting_parts", "scheduled", "completed"});
        styleCombo(statusFilter);
        statusFilter.addActionListener(e -> applyFilters());
        filters.add(statusFilter, BorderLayout.EAST);

        card.add(filters, BorderLayout.NORTH);

        maintenanceList = new JList<>(listModel);
        maintenanceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        maintenanceList.setCellRenderer(new MaintenanceListRenderer());
        maintenanceList.setBackground(WHITE);
        maintenanceList.setFixedCellHeight(106);
        maintenanceList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Maintenance selected = maintenanceList.getSelectedValue();
                selectedMaintenanceId = selected != null ? selected.getId() : null;
                renderMaintenanceDetail(selected);
                updateActionButtons();
            }
        });

        JScrollPane scrollPane = new JScrollPane(maintenanceList);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(WHITE);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDetailPanel() {
        detailPanel = createCardPanel();
        detailPanel.setLayout(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));
        renderMaintenanceDetail(null);
        return detailPanel;
    }

    private void loadData() {
        aircraftList = aircraftController.listarAvioes();
        allMaintenances = loadAllMaintenances();
        allMaintenances.sort(Comparator.comparing(Maintenance::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())));

        updateSummaryCards();
        applyFilters();
    }

    private List<Maintenance> loadAllMaintenances() {
        String[] statuses = {"in_progress", "waiting_parts", "scheduled", "completed"};
        Map<Integer, Maintenance> byId = new LinkedHashMap<>();
        for (String status : statuses) {
            for (Maintenance maintenance : maintenanceController.obterManutencoesPorStatus(status)) {
                if (maintenance.getId() != null) {
                    byId.put(maintenance.getId(), maintenance);
                }
            }
        }
        return new ArrayList<>(byId.values());
    }

    private void applyFilters() {
        String search = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedStatus = statusFilter == null ? "Todos" : String.valueOf(statusFilter.getSelectedItem());

        filteredMaintenances = allMaintenances.stream()
                .filter(maintenance -> {
                    String aircraft = maintenance.getAircraft() != null ? safeLower(maintenance.getAircraft().getRegistration()) : "";
                    String description = safeLower(maintenance.getDescription());
                    String type = safeLower(maintenance.getMaintenanceType());
                    String technician = safeLower(maintenance.getTechnician());

                    boolean matchSearch = search.isEmpty()
                            || aircraft.contains(search)
                            || description.contains(search)
                            || type.contains(search)
                            || technician.contains(search);

                    boolean matchStatus = "Todos".equals(selectedStatus)
                            || selectedStatus.equals(safeLower(maintenance.getStatus()));
                    return matchSearch && matchStatus;
                })
                .toList();

        Integer previousSelection = selectedMaintenanceId;
        listModel.clear();
        for (Maintenance maintenance : filteredMaintenances) {
            listModel.addElement(maintenance);
        }

        listStatsLabel.setText(filteredMaintenances.size() + " intervencoes visiveis de " + allMaintenances.size());

        if (!filteredMaintenances.isEmpty()) {
            int indexToSelect = 0;
            if (previousSelection != null) {
                for (int i = 0; i < filteredMaintenances.size(); i++) {
                    if (previousSelection.equals(filteredMaintenances.get(i).getId())) {
                        indexToSelect = i;
                        break;
                    }
                }
            }
            maintenanceList.setSelectedIndex(indexToSelect);
            selectedMaintenanceId = filteredMaintenances.get(indexToSelect).getId();
        } else {
            selectedMaintenanceId = null;
            renderMaintenanceDetail(null);
            updateActionButtons();
        }
    }

    private void updateSummaryCards() {
        long inProgress = allMaintenances.stream().filter(m -> "in_progress".equals(safeLower(m.getStatus()))).count();
        long waiting = allMaintenances.stream().filter(m -> "waiting_parts".equals(safeLower(m.getStatus()))).count();
        long scheduled = allMaintenances.stream().filter(m -> "scheduled".equals(safeLower(m.getStatus()))).count();
        double totalCost = allMaintenances.stream().mapToDouble(m -> m.getCost() != null ? m.getCost() : 0.0).sum();

        inProgressValueLabel.setText(String.valueOf(inProgress));
        waitingPartsValueLabel.setText(String.valueOf(waiting));
        scheduledValueLabel.setText(String.valueOf(scheduled));
        costValueLabel.setText(String.format(PT, "EUR %,.2f", totalCost));
    }

    private void renderMaintenanceDetail(Maintenance maintenance) {
        detailPanel.removeAll();

        if (maintenance == null) {
            JLabel empty = new JLabel("Selecione uma intervencao para ver o detalhe.", SwingConstants.CENTER);
            empty.setForeground(new Color(148, 163, 184));
            empty.setFont(new Font("Inter", Font.PLAIN, 13));
            detailPanel.add(empty, BorderLayout.CENTER);
            detailPanel.revalidate();
            detailPanel.repaint();
            return;
        }

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);

        String registration = maintenance.getAircraft() != null ? maintenance.getAircraft().getRegistration() : "N/A";
        JLabel title = new JLabel(registration);
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);

        JLabel cost = new JLabel(String.format(PT, "EUR %,.2f", maintenance.getCost() != null ? maintenance.getCost() : 0.0));
        cost.setForeground(TITLE);
        cost.setFont(new Font("Inter", Font.BOLD, 20));
        header.add(cost, BorderLayout.EAST);
        content.add(header);
        content.add(Box.createVerticalStrut(8));

        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badges.setOpaque(false);
        badges.add(createBadge(statusLabel(maintenance), statusBackground(maintenance), statusForeground(maintenance)));
        badges.add(createBadge(typeLabel(maintenance), typeBackground(maintenance), typeForeground(maintenance)));
        badges.add(createBadge("Prioridade " + priorityLabel(maintenance), priorityBackground(maintenance), priorityForeground(maintenance)));
        content.add(badges);
        content.add(Box.createVerticalStrut(12));

        JTextArea description = new JTextArea(maintenance.getDescription() != null ? maintenance.getDescription() : "Sem descricao.");
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setEditable(false);
        description.setBackground(new Color(248, 250, 252));
        description.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        description.setFont(new Font("Inter", Font.PLAIN, 12));
        description.setForeground(new Color(55, 65, 81));
        content.add(description);
        content.add(Box.createVerticalStrut(12));

        JPanel infoGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        infoGrid.setOpaque(false);
        infoGrid.add(createInfoCard("Tecnico", safeText(maintenance.getTechnician(), "Nao definido")));
        infoGrid.add(createInfoCard("Inicio", formatDate(maintenance.getStartDate())));
        infoGrid.add(createInfoCard("Fim estimado", formatDate(maintenance.getEstimatedEndDate())));
        infoGrid.add(createInfoCard("Duracao", buildDuration(maintenance)));
        content.add(infoGrid);
        content.add(Box.createVerticalStrut(14));

        int progress = estimatedProgress(maintenance);
        JPanel progressPanel = new JPanel();
        progressPanel.setOpaque(false);
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        JLabel progressLabel = new JLabel("Progresso estimado: " + progress + "%");
        progressLabel.setForeground(MUTED);
        progressLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(progress);
        progressBar.setForeground(BLUE);
        progressBar.setBackground(new Color(226, 232, 240));
        progressBar.setPreferredSize(new Dimension(200, 8));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        progressPanel.add(progressLabel);
        progressPanel.add(Box.createVerticalStrut(6));
        progressPanel.add(progressBar);
        content.add(progressPanel);
        content.add(Box.createVerticalStrut(14));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);

        updateButton = new JButton("Atualizar");
        styleSecondaryButton(updateButton);
        updateButton.addActionListener(e -> updateSelectedMaintenance());
        actions.add(updateButton);

        completeButton = new JButton("Marcar Concluida");
        stylePrimaryButton(completeButton);
        completeButton.addActionListener(e -> completeSelectedMaintenance());
        actions.add(completeButton);

        deleteButton = new JButton("Eliminar");
        styleDangerButton(deleteButton);
        deleteButton.addActionListener(e -> deleteSelectedMaintenance());
        actions.add(deleteButton);

        content.add(actions);

        detailPanel.add(content, BorderLayout.CENTER);
        detailPanel.revalidate();
        detailPanel.repaint();
        updateActionButtons();
    }

    private JPanel createInfoCard(String label, String value) {
        JPanel card = new JPanel();
        card.setOpaque(true);
        card.setBackground(new Color(248, 250, 252));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setForeground(new Color(148, 163, 184));
        labelComponent.setFont(new Font("Inter", Font.PLAIN, 11));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setForeground(TITLE);
        valueComponent.setFont(new Font("Inter", Font.BOLD, 12));

        card.add(labelComponent);
        card.add(Box.createVerticalStrut(3));
        card.add(valueComponent);
        return card;
    }

    private JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setFont(new Font("Inter", Font.BOLD, 11));
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));
        return badge;
    }

    private void createMaintenanceDialog() {
        if (aircraftList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nao existem aeronaves para associar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<AircraftChoice> aircraftCombo = new JComboBox<>();
        for (Aircraft aircraft : aircraftList) {
            aircraftCombo.addItem(new AircraftChoice(aircraft));
        }
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Major", "Scheduled 50h", "Scheduled 100h", "Unscheduled", "Avionics"});
        JTextArea descriptionArea = new JTextArea(4, 26);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Aeronave"));
        form.add(aircraftCombo);
        form.add(new JLabel("Tipo de intervencao"));
        form.add(typeCombo);
        form.add(new JLabel("Descricao"));
        form.add(new JScrollPane(descriptionArea));

        int result = JOptionPane.showConfirmDialog(this, form, "Nova Intervencao", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            AircraftChoice choice = (AircraftChoice) aircraftCombo.getSelectedItem();
            if (choice == null) {
                throw new IllegalArgumentException("Aeronave invalida.");
            }
            String type = String.valueOf(typeCombo.getSelectedItem());
            String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();
            if (description.isEmpty()) {
                throw new IllegalArgumentException("Descricao obrigatoria.");
            }

            maintenanceController.criarManutencao(choice.aircraft, type, description);
            loadData();
            JOptionPane.showMessageDialog(this, "Intervencao criada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar intervencao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedMaintenance() {
        Maintenance selected = getSelectedMaintenance();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma intervencao.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField technicianField = new JTextField(selected.getTechnician() != null ? selected.getTechnician() : "");
        SpinnerDateModel dateModel = new SpinnerDateModel(
                Date.from((selected.getEstimatedEndDate() != null ? selected.getEstimatedEndDate() : LocalDate.now().plusDays(7))
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()),
                null,
                null,
                java.util.Calendar.DAY_OF_MONTH
        );
        JSpinner estimatedDateSpinner = new JSpinner(dateModel);
        estimatedDateSpinner.setEditor(new JSpinner.DateEditor(estimatedDateSpinner, "dd/MM/yyyy"));

        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"low", "medium", "high"});
        priorityCombo.setSelectedItem(selected.getPriority() != null ? selected.getPriority() : "medium");

        JTextField costField = new JTextField(String.valueOf(selected.getCost() != null ? selected.getCost() : 0.0));

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"scheduled", "in_progress", "waiting_parts", "completed"});
        statusCombo.setSelectedItem(selected.getStatus() != null ? selected.getStatus() : "scheduled");

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Tecnico"));
        form.add(technicianField);
        form.add(new JLabel("Fim estimado"));
        form.add(estimatedDateSpinner);
        form.add(new JLabel("Prioridade"));
        form.add(priorityCombo);
        form.add(new JLabel("Custo estimado (EUR)"));
        form.add(costField);
        form.add(new JLabel("Estado"));
        form.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, form, "Atualizar Intervencao", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String technician = technicianField.getText() == null ? "" : technicianField.getText().trim();
            LocalDate estimatedEnd = ((Date) estimatedDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String priority = String.valueOf(priorityCombo.getSelectedItem());
            double cost = Double.parseDouble(costField.getText().trim().replace(",", "."));
            String status = String.valueOf(statusCombo.getSelectedItem());

            maintenanceController.atualizarManutencao(selected.getId(), technician, estimatedEnd, priority, cost, status);
            selectedMaintenanceId = selected.getId();
            loadData();
            JOptionPane.showMessageDialog(this, "Intervencao atualizada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Custo invalido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeSelectedMaintenance() {
        Maintenance selected = getSelectedMaintenance();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma intervencao.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if ("completed".equals(safeLower(selected.getStatus()))) {
            JOptionPane.showMessageDialog(this, "Intervencao ja esta concluida.", "Informacao", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Marcar a intervencao #" + selected.getId() + " como concluida?",
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            maintenanceController.marcarComoConcluida(selected.getId(), LocalDate.now());
            selectedMaintenanceId = selected.getId();
            loadData();
            JOptionPane.showMessageDialog(this, "Intervencao concluida com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao concluir intervencao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedMaintenance() {
        Maintenance selected = getSelectedMaintenance();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma intervencao.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirma eliminacao da intervencao #" + selected.getId() + "?",
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            maintenanceController.eliminarManutencao(selected.getId());
            selectedMaintenanceId = null;
            loadData();
            JOptionPane.showMessageDialog(this, "Intervencao eliminada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao eliminar intervencao: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Maintenance getSelectedMaintenance() {
        return maintenanceList != null ? maintenanceList.getSelectedValue() : null;
    }

    private void updateActionButtons() {
        Maintenance selected = getSelectedMaintenance();
        boolean hasSelection = selected != null;
        if (updateButton != null) {
            updateButton.setEnabled(hasSelection);
        }
        if (deleteButton != null) {
            deleteButton.setEnabled(hasSelection);
        }
        if (completeButton != null) {
            completeButton.setEnabled(hasSelection && !"completed".equals(safeLower(selected.getStatus())));
        }
    }

    private String statusLabel(Maintenance maintenance) {
        return switch (safeLower(maintenance.getStatus())) {
            case "in_progress" -> "Em Curso";
            case "waiting_parts" -> "Aguarda Pecas";
            case "completed" -> "Concluido";
            default -> "Agendado";
        };
    }

    private Color statusBackground(Maintenance maintenance) {
        return switch (safeLower(maintenance.getStatus())) {
            case "in_progress" -> new Color(219, 234, 254);
            case "waiting_parts" -> new Color(254, 243, 199);
            case "completed" -> new Color(220, 252, 231);
            default -> new Color(243, 232, 255);
        };
    }

    private Color statusForeground(Maintenance maintenance) {
        return switch (safeLower(maintenance.getStatus())) {
            case "in_progress" -> new Color(29, 78, 216);
            case "waiting_parts" -> WARNING;
            case "completed" -> SUCCESS;
            default -> PURPLE;
        };
    }

    private String priorityLabel(Maintenance maintenance) {
        return switch (safeLower(maintenance.getPriority())) {
            case "high" -> "Alta";
            case "low" -> "Baixa";
            default -> "Media";
        };
    }

    private Color priorityBackground(Maintenance maintenance) {
        return switch (safeLower(maintenance.getPriority())) {
            case "high" -> new Color(254, 226, 226);
            case "low" -> new Color(220, 252, 231);
            default -> new Color(254, 243, 199);
        };
    }

    private Color priorityForeground(Maintenance maintenance) {
        return switch (safeLower(maintenance.getPriority())) {
            case "high" -> DANGER;
            case "low" -> SUCCESS;
            default -> WARNING;
        };
    }

    private String typeLabel(Maintenance maintenance) {
        String type = maintenance.getMaintenanceType();
        return type == null || type.isBlank() ? "N/A" : type;
    }

    private Color typeBackground(Maintenance maintenance) {
        String type = safeLower(maintenance.getMaintenanceType());
        if (type.contains("major")) {
            return new Color(254, 226, 226);
        }
        if (type.contains("scheduled")) {
            return new Color(219, 234, 254);
        }
        if (type.contains("avionics")) {
            return new Color(243, 232, 255);
        }
        return new Color(254, 243, 199);
    }

    private Color typeForeground(Maintenance maintenance) {
        String type = safeLower(maintenance.getMaintenanceType());
        if (type.contains("major")) {
            return DANGER;
        }
        if (type.contains("scheduled")) {
            return new Color(29, 78, 216);
        }
        if (type.contains("avionics")) {
            return PURPLE;
        }
        return WARNING;
    }

    private int estimatedProgress(Maintenance maintenance) {
        return switch (safeLower(maintenance.getStatus())) {
            case "completed" -> 100;
            case "in_progress" -> 60;
            case "waiting_parts" -> 45;
            default -> 15;
        };
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private String buildDuration(Maintenance maintenance) {
        LocalDate start = maintenance.getStartDate();
        LocalDate end = maintenance.getEstimatedEndDate();
        if (start == null || end == null) {
            return "-";
        }
        long days = Math.max(0, ChronoUnit.DAYS.between(start, end));
        return days + " dias";
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
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

    private static class AircraftChoice {
        private final Aircraft aircraft;

        private AircraftChoice(Aircraft aircraft) {
            this.aircraft = aircraft;
        }

        @Override
        public String toString() {
            if (aircraft == null) {
                return "N/A";
            }
            String reg = aircraft.getRegistration() != null ? aircraft.getRegistration() : "N/A";
            String model = aircraft.getModel() != null ? aircraft.getModel() : "Modelo";
            return reg + " - " + model;
        }
    }

    private class MaintenanceListRenderer implements ListCellRenderer<Maintenance> {
        @Override
        public Component getListCellRendererComponent(JList<? extends Maintenance> list, Maintenance value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel row = new JPanel(new BorderLayout(8, 6));
            row.setOpaque(true);
            row.setBackground(isSelected ? new Color(239, 246, 255) : WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(isSelected ? BLUE : BORDER, isSelected ? 2 : 1),
                    new EmptyBorder(8, 8, 8, 8)
            ));

            JPanel top = new JPanel(new BorderLayout(8, 0));
            top.setOpaque(false);
            JLabel aircraft = new JLabel(value.getAircraft() != null ? value.getAircraft().getRegistration() : "N/A");
            aircraft.setFont(new Font("Inter", Font.BOLD, 14));
            aircraft.setForeground(TITLE);
            top.add(aircraft, BorderLayout.WEST);
            top.add(createBadge(statusLabel(value), statusBackground(value), statusForeground(value)), BorderLayout.EAST);
            row.add(top, BorderLayout.NORTH);

            JLabel description = new JLabel(safeDescription(value.getDescription()));
            description.setForeground(new Color(55, 65, 81));
            description.setFont(new Font("Inter", Font.PLAIN, 11));
            row.add(description, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new BorderLayout(6, 0));
            bottom.setOpaque(false);

            JPanel leftBadges = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            leftBadges.setOpaque(false);
            leftBadges.add(createBadge(typeLabel(value), typeBackground(value), typeForeground(value)));
            leftBadges.add(createBadge(priorityLabel(value), priorityBackground(value), priorityForeground(value)));
            bottom.add(leftBadges, BorderLayout.WEST);

            LocalDate due = value.getEstimatedEndDate();
            JLabel eta = new JLabel(due != null ? "ate " + formatDate(due) : "sem previsao");
            eta.setForeground(new Color(148, 163, 184));
            eta.setFont(new Font("Inter", Font.PLAIN, 11));
            bottom.add(eta, BorderLayout.EAST);

            row.add(bottom, BorderLayout.SOUTH);
            return row;
        }

        private String safeDescription(String description) {
            if (description == null || description.isBlank()) {
                return "Sem descricao";
            }
            if (description.length() <= 60) {
                return description;
            }
            return description.substring(0, 57) + "...";
        }
    }
}
