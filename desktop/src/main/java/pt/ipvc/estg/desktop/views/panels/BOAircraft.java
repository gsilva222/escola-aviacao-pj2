package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.AircraftController;
import pt.ipvc.estg.entities.Aircraft;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BOAircraft extends JPanel {

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

    private final AircraftController aircraftController;
    private List<Aircraft> aircraftList = new ArrayList<>();

    private JLabel operationalValueLabel;
    private JLabel maintenanceValueLabel;
    private JLabel groundedValueLabel;
    private JLabel fleetInfoLabel;
    private JPanel cardsContainer;

    public BOAircraft() {
        this.aircraftController = new AircraftController();
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(createSummaryPanel());
        top.add(Box.createVerticalStrut(12));
        top.add(createHeaderPanel());
        add(top, BorderLayout.NORTH);

        cardsContainer = new JPanel();
        cardsContainer.setOpaque(false);
        cardsContainer.setLayout(new GridLayout(1, 1, 12, 12));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setOpaque(false);
        panel.add(createStatusSummaryCard("Operacionais", new Color(220, 252, 231), SUCCESS, "operational"));
        panel.add(createStatusSummaryCard("Em Manutencao", new Color(254, 243, 199), WARNING, "maintenance"));
        panel.add(createStatusSummaryCard("Imobilizados", new Color(254, 226, 226), DANGER, "grounded"));
        return panel;
    }

    private JPanel createStatusSummaryCard(String title, Color bg, Color fg, String key) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel icon = new JPanel(new GridBagLayout());
        icon.setOpaque(true);
        icon.setBackground(bg);
        icon.setPreferredSize(new Dimension(40, 40));
        JLabel iconLabel = new JLabel("A");
        iconLabel.setForeground(fg);
        iconLabel.setFont(new Font("Inter", Font.BOLD, 14));
        icon.add(iconLabel);

        JLabel value = new JLabel("0");
        value.setForeground(fg);
        value.setFont(new Font("Inter", Font.BOLD, 22));
        JLabel text = new JLabel(title);
        text.setForeground(MUTED);
        text.setFont(new Font("Inter", Font.PLAIN, 12));

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(value);
        copy.add(text);

        card.add(icon, BorderLayout.WEST);
        card.add(copy, BorderLayout.CENTER);

        switch (key) {
            case "operational" -> operationalValueLabel = value;
            case "maintenance" -> maintenanceValueLabel = value;
            case "grounded" -> groundedValueLabel = value;
            default -> {
            }
        }
        return card;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        fleetInfoLabel = new JLabel("A carregar...");
        fleetInfoLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        fleetInfoLabel.setForeground(MUTED);
        panel.add(fleetInfoLabel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton refreshButton = new JButton("Recarregar");
        styleSecondaryButton(refreshButton);
        refreshButton.addActionListener(e -> loadData());
        actions.add(refreshButton);

        JButton createButton = new JButton("Registar Aeronave");
        stylePrimaryButton(createButton);
        createButton.addActionListener(e -> createAircraftDialog());
        actions.add(createButton);

        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private void loadData() {
        aircraftList = new ArrayList<>(aircraftController.listarAvioes());
        aircraftList.sort(Comparator.comparing(Aircraft::getRegistration, Comparator.nullsLast(String::compareToIgnoreCase)));
        updateSummary();
        renderAircraftCards();
    }

    private void updateSummary() {
        long operational = aircraftList.stream().filter(a -> "operational".equals(safeLower(a.getStatus()))).count();
        long maintenance = aircraftList.stream().filter(a -> "maintenance".equals(safeLower(a.getStatus()))).count();
        long grounded = aircraftList.stream().filter(a -> "grounded".equals(safeLower(a.getStatus()))).count();

        operationalValueLabel.setText(String.valueOf(operational));
        maintenanceValueLabel.setText(String.valueOf(maintenance));
        groundedValueLabel.setText(String.valueOf(grounded));
        fleetInfoLabel.setText(aircraftList.size() + " aeronaves registadas na frota");
    }

    private void renderAircraftCards() {
        cardsContainer.removeAll();

        if (aircraftList.isEmpty()) {
            JPanel empty = new JPanel(new GridBagLayout());
            empty.setOpaque(true);
            empty.setBackground(WHITE);
            empty.setBorder(BorderFactory.createLineBorder(BORDER, 1));
            JLabel message = new JLabel("Sem aeronaves registadas.");
            message.setForeground(new Color(148, 163, 184));
            message.setFont(new Font("Inter", Font.PLAIN, 14));
            empty.add(message);
            cardsContainer.setLayout(new GridLayout(1, 1, 12, 12));
            cardsContainer.add(empty);
            cardsContainer.revalidate();
            cardsContainer.repaint();
            return;
        }

        int columns = Math.min(3, Math.max(1, aircraftList.size()));
        int rows = (int) Math.ceil(aircraftList.size() / (double) columns);
        cardsContainer.setLayout(new GridLayout(rows, columns, 12, 12));

        for (Aircraft aircraft : aircraftList) {
            cardsContainer.add(createAircraftCard(aircraft));
        }
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createAircraftCard(Aircraft aircraft) {
        Color borderColor = switch (safeLower(aircraft.getStatus())) {
            case "operational" -> new Color(187, 247, 208);
            case "maintenance" -> new Color(253, 230, 138);
            case "grounded" -> new Color(254, 202, 202);
            default -> BORDER;
        };
        Color statusBg = switch (safeLower(aircraft.getStatus())) {
            case "operational" -> new Color(220, 252, 231);
            case "maintenance" -> new Color(254, 243, 199);
            case "grounded" -> new Color(254, 226, 226);
            default -> new Color(241, 245, 249);
        };
        Color statusColor = switch (safeLower(aircraft.getStatus())) {
            case "operational" -> SUCCESS;
            case "maintenance" -> WARNING;
            case "grounded" -> DANGER;
            default -> MUTED;
        };

        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(true);
        header.setBackground(new Color(statusBg.getRed(), statusBg.getGreen(), statusBg.getBlue(), 120));
        header.setBorder(new EmptyBorder(12, 12, 10, 12));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));

        JLabel registration = new JLabel(safeText(aircraft.getRegistration(), "N/A"));
        registration.setForeground(TITLE);
        registration.setFont(new Font("Monospaced", Font.BOLD, 20));
        JLabel model = new JLabel(safeText(aircraft.getModel(), "Modelo"));
        model.setForeground(MUTED);
        model.setFont(new Font("Inter", Font.PLAIN, 12));

        titleBox.add(registration);
        titleBox.add(model);

        JLabel status = new JLabel(statusLabel(aircraft), SwingConstants.CENTER);
        status.setOpaque(true);
        status.setBackground(statusBg);
        status.setForeground(statusColor);
        status.setBorder(new EmptyBorder(4, 8, 4, 8));
        status.setFont(new Font("Inter", Font.BOLD, 11));

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        meta.setOpaque(false);
        JLabel location = new JLabel("Local: " + safeText(aircraft.getLocation(), "-"));
        JLabel year = new JLabel("Ano: " + (aircraft.getManufYear() != null ? aircraft.getManufYear() : "-"));
        JLabel type = new JLabel(safeText(aircraft.getType(), "-"));
        for (JLabel label : new JLabel[]{location, year, type}) {
            label.setForeground(MUTED);
            label.setFont(new Font("Inter", Font.PLAIN, 11));
            meta.add(label);
        }

        JPanel headerContent = new JPanel(new BorderLayout(0, 6));
        headerContent.setOpaque(false);
        headerContent.add(titleBox, BorderLayout.NORTH);
        headerContent.add(meta, BorderLayout.SOUTH);

        header.add(headerContent, BorderLayout.CENTER);
        header.add(status, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(12, 12, 12, 12));

        double hours = aircraft.getFlightHours() != null ? aircraft.getFlightHours() : 0.0;
        body.add(createMetricBar("Horas TT", String.format(PT, "%,.0fh", hours), hoursPercent(hours), hoursColor(hoursPercent(hours))));
        body.add(Box.createVerticalStrut(8));

        int fuel = aircraft.getFuelLevel() != null ? aircraft.getFuelLevel() : 0;
        body.add(createMetricBar("Combustivel", fuel + "%", fuel, fuelColor(fuel)));
        body.add(Box.createVerticalStrut(10));

        JPanel dates = new JPanel(new GridLayout(1, 2, 8, 0));
        dates.setOpaque(false);
        dates.add(createDateChip("Ultima Manutencao", formatDate(aircraft.getLastMaintenance()), new Color(248, 250, 252), BORDER));

        long daysToNext = daysToNextMaintenance(aircraft.getNextMaintenance());
        boolean isNearMaintenance = daysToNext >= 0 && daysToNext <= 30;
        Color nextBg = isNearMaintenance ? new Color(255, 247, 237) : new Color(248, 250, 252);
        Color nextBorder = isNearMaintenance ? new Color(253, 215, 170) : BORDER;
        String nextLabel = formatDate(aircraft.getNextMaintenance());
        if (isNearMaintenance) {
            nextLabel = nextLabel + " (" + daysToNext + "d)";
        }
        dates.add(createDateChip("Proxima Manutencao", nextLabel, nextBg, nextBorder));
        body.add(dates);
        body.add(Box.createVerticalStrut(10));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        actions.setOpaque(false);

        JButton editBtn = new JButton("Editar");
        styleGhostButton(editBtn);
        editBtn.addActionListener(e -> editAircraftDialog(aircraft));
        actions.add(editBtn);

        JButton statusBtn = new JButton("Estado");
        styleSecondaryButton(statusBtn);
        statusBtn.addActionListener(e -> changeStatusDialog(aircraft));
        actions.add(statusBtn);

        JButton deleteBtn = new JButton("Eliminar");
        styleDangerButton(deleteBtn);
        deleteBtn.addActionListener(e -> deleteAircraft(aircraft));
        actions.add(deleteBtn);

        body.add(actions);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMetricBar(String label, String value, double percent, Color barColor) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel name = new JLabel(label);
        name.setForeground(MUTED);
        name.setFont(new Font("Inter", Font.PLAIN, 11));
        JLabel amount = new JLabel(value);
        amount.setForeground(barColor);
        amount.setFont(new Font("Inter", Font.BOLD, 11));
        top.add(name, BorderLayout.WEST);
        top.add(amount, BorderLayout.EAST);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) Math.max(0, Math.min(100, percent)));
        bar.setForeground(barColor);
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(100, 8));

        panel.add(top);
        panel.add(Box.createVerticalStrut(4));
        panel.add(bar);
        return panel;
    }

    private JPanel createDateChip(String label, String value, Color bg, Color border) {
        JPanel chip = new JPanel();
        chip.setOpaque(true);
        chip.setBackground(bg);
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel l = new JLabel(label);
        l.setForeground(new Color(148, 163, 184));
        l.setFont(new Font("Inter", Font.PLAIN, 10));
        JLabel v = new JLabel(value);
        v.setForeground(new Color(55, 65, 81));
        v.setFont(new Font("Inter", Font.BOLD, 11));

        chip.add(l);
        chip.add(Box.createVerticalStrut(2));
        chip.add(v);
        return chip;
    }

    private void createAircraftDialog() {
        JTextField registrationField = new JTextField();
        JTextField modelField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Single Engine", "Multi Engine", "Helicopter", "Simulator"});

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Matricula"));
        form.add(registrationField);
        form.add(new JLabel("Modelo"));
        form.add(modelField);
        form.add(new JLabel("Tipo"));
        form.add(typeCombo);

        int result = JOptionPane.showConfirmDialog(this, form, "Registar Aeronave", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String registration = registrationField.getText() == null ? "" : registrationField.getText().trim();
            String model = modelField.getText() == null ? "" : modelField.getText().trim();
            String type = String.valueOf(typeCombo.getSelectedItem());
            aircraftController.criarAviao(registration, model, type);
            loadData();
            JOptionPane.showMessageDialog(this, "Aeronave registada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao registar aeronave: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAircraftDialog(Aircraft aircraft) {
        JTextField modelField = new JTextField(safeText(aircraft.getModel(), ""));
        JTextField yearField = new JTextField(aircraft.getManufYear() != null ? String.valueOf(aircraft.getManufYear()) : "");
        JTextField locationField = new JTextField(safeText(aircraft.getLocation(), ""));
        JTextField fuelField = new JTextField(aircraft.getFuelLevel() != null ? String.valueOf(aircraft.getFuelLevel()) : "0");

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Modelo"));
        form.add(modelField);
        form.add(new JLabel("Ano de fabrico"));
        form.add(yearField);
        form.add(new JLabel("Localizacao"));
        form.add(locationField);
        form.add(new JLabel("Combustivel (%)"));
        form.add(fuelField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Editar " + safeText(aircraft.getRegistration(), "aeronave"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String model = modelField.getText() == null ? "" : modelField.getText().trim();
            Integer year = parseOptionalInt(yearField.getText(), aircraft.getManufYear());
            String location = locationField.getText() == null ? "" : locationField.getText().trim();
            Integer fuel = parseOptionalInt(fuelField.getText(), aircraft.getFuelLevel() != null ? aircraft.getFuelLevel() : 0);

            aircraftController.atualizarAviao(aircraft.getId(), model, year, location, fuel);
            loadData();
            JOptionPane.showMessageDialog(this, "Aeronave atualizada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano ou combustivel invalido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar aeronave: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeStatusDialog(Aircraft aircraft) {
        String[] statusOptions = {"operational", "maintenance", "grounded"};
        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Novo estado para " + safeText(aircraft.getRegistration(), "aeronave"),
                "Atualizar Estado",
                JOptionPane.PLAIN_MESSAGE,
                null,
                statusOptions,
                aircraft.getStatus()
        );
        if (selected == null) {
            return;
        }

        try {
            aircraftController.atualizarStatus(aircraft.getId(), selected);
            loadData();
            JOptionPane.showMessageDialog(this, "Estado atualizado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar estado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAircraft(Aircraft aircraft) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirma eliminacao da aeronave " + safeText(aircraft.getRegistration(), "") + "?",
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            aircraftController.eliminarAviao(aircraft.getId());
            loadData();
            JOptionPane.showMessageDialog(this, "Aeronave eliminada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao eliminar aeronave: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String statusLabel(Aircraft aircraft) {
        return switch (safeLower(aircraft.getStatus())) {
            case "operational" -> "Operacional";
            case "maintenance" -> "Em Manutencao";
            case "grounded" -> "Imobilizado";
            default -> "Sem Estado";
        };
    }

    private double hoursPercent(double hours) {
        return Math.min(100.0, (hours / 2500.0) * 100.0);
    }

    private Color hoursColor(double percent) {
        if (percent > 80) {
            return DANGER;
        }
        if (percent > 60) {
            return WARNING;
        }
        return BLUE;
    }

    private Color fuelColor(int fuel) {
        if (fuel > 60) {
            return SUCCESS;
        }
        if (fuel > 30) {
            return WARNING;
        }
        return DANGER;
    }

    private long daysToNextMaintenance(LocalDate nextMaintenance) {
        if (nextMaintenance == null) {
            return -1;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), nextMaintenance);
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

    private Integer parseOptionalInt(String raw, Integer fallback) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        return Integer.parseInt(raw.trim());
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void styleGhostButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 11));
        button.setForeground(new Color(55, 65, 81));
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.PLAIN, 11));
        button.setForeground(MUTED);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE_DARK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 14, 8, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setFont(new Font("Inter", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 10, 6, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
