package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.CourseController;
import pt.ipvc.estg.entities.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Painel de cursos alinhado ao visual dos mockups (cards expansíveis).
 */
public class BOCourses extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);    // #EEF2F7
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0
    private static final Color BLUE_DARK = new Color(13, 71, 161);

    private static final Color[] COURSE_COLORS = new Color[]{
            new Color(21, 101, 192),
            new Color(124, 58, 237),
            new Color(5, 150, 105),
            new Color(217, 119, 6)
    };

    private final CourseController courseController;
    private final JPanel cardsContainer;
    private final Map<Integer, JPanel> detailPanels = new LinkedHashMap<>();
    private Integer expandedCourseId = null;

    public BOCourses() {
        this.courseController = new CourseController();
        setLayout(new BorderLayout(0, 12));
        setBackground(PAGE_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        add(createHeader(), BorderLayout.NORTH);

        cardsContainer = new JPanel();
        cardsContainer.setOpaque(false);
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BG);
        add(scrollPane, BorderLayout.CENTER);

        loadData();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel info = new JLabel("0 cursos disponíveis · EASA Part-FCL Approved");
        info.setForeground(MUTED);
        info.setFont(new Font("Inter", Font.PLAIN, 13));
        info.setName("coursesHeaderInfo");
        panel.add(info, BorderLayout.WEST);

        JButton newBtn = new JButton("Novo Curso");
        newBtn.setFont(new Font("Inter", Font.BOLD, 12));
        newBtn.setForeground(Color.WHITE);
        newBtn.setBackground(BLUE);
        newBtn.setFocusPainted(false);
        newBtn.setBorder(new EmptyBorder(9, 14, 9, 14));
        newBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newBtn.addActionListener(e -> createCourseDialog());
        panel.add(newBtn, BorderLayout.EAST);

        return panel;
    }

    private void loadData() {
        cardsContainer.removeAll();
        detailPanels.clear();

        List<Course> courses = new ArrayList<>(courseController.listarCursos());
        JLabel info = (JLabel) findByName(this, "coursesHeaderInfo");
        if (info != null) {
            info.setText(courses.size() + " cursos disponíveis · EASA Part-FCL Approved");
        }

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            Color accent = COURSE_COLORS[i % COURSE_COLORS.length];
            cardsContainer.add(createCourseCard(course, accent));
            cardsContainer.add(Box.createVerticalStrut(10));
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel createCourseCard(Course course, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JButton header = new JButton();
        header.setLayout(new BorderLayout(14, 0));
        header.setBackground(WHITE);
        header.setFocusPainted(false);
        header.setBorder(new EmptyBorder(14, 14, 14, 14));
        header.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.addActionListener(e -> toggleExpand(course.getId()));

        JPanel icon = new JPanel(new GridBagLayout());
        icon.setPreferredSize(new Dimension(44, 44));
        icon.setBackground(withAlpha(accent, 24));
        JLabel iconText = new JLabel("CU");
        iconText.setForeground(accent);
        iconText.setFont(new Font("Inter", Font.BOLD, 11));
        icon.add(iconText);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        JLabel name = new JLabel(course.getName());
        name.setForeground(TITLE);
        name.setFont(new Font("Inter", Font.BOLD, 14));
        titleRow.add(name);

        JLabel easa = new JLabel("EASA Part-FCL");
        easa.setOpaque(true);
        easa.setBackground(withAlpha(accent, 24));
        easa.setForeground(accent);
        easa.setFont(new Font("Inter", Font.BOLD, 10));
        easa.setBorder(new EmptyBorder(2, 6, 2, 6));
        titleRow.add(easa);

        content.add(titleRow);
        content.add(Box.createVerticalStrut(4));

        JLabel meta = new JLabel(buildMetaLine(course));
        meta.setForeground(MUTED);
        meta.setFont(new Font("Inter", Font.PLAIN, 12));
        content.add(meta);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel modules = new JLabel("3 módulos");
        modules.setForeground(TITLE);
        modules.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel practical = new JLabel("1 prático");
        practical.setForeground(new Color(148, 163, 184));
        practical.setFont(new Font("Inter", Font.PLAIN, 11));
        JLabel chevron = new JLabel("▾");
        chevron.setForeground(new Color(148, 163, 184));
        chevron.setFont(new Font("Dialog", Font.PLAIN, 12));
        modules.setAlignmentX(Component.RIGHT_ALIGNMENT);
        practical.setAlignmentX(Component.RIGHT_ALIGNMENT);
        chevron.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(modules);
        right.add(practical);
        right.add(Box.createVerticalStrut(4));
        right.add(chevron);

        header.add(icon, BorderLayout.WEST);
        header.add(content, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        JPanel details = createDetailsPanel(course);
        details.setVisible(false);
        detailPanels.put(course.getId(), details);
        card.add(details, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDetailsPanel(Course course) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(248, 250, 252));
        wrapper.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JPanel modulesList = new JPanel();
        modulesList.setOpaque(false);
        modulesList.setLayout(new BoxLayout(modulesList, BoxLayout.Y_AXIS));
        modulesList.setBorder(new EmptyBorder(10, 12, 10, 12));

        modulesList.add(createModuleRow("1", "Fundamentos e Regulamentos", "teórico", 20));
        modulesList.add(Box.createVerticalStrut(6));
        modulesList.add(createModuleRow("2", "Navegação e Planeamento", "teórico", 25));
        modulesList.add(Box.createVerticalStrut(6));
        modulesList.add(createModuleRow("3", "Treino Prático de Voo", "prático", Math.max(1, course.getFlightHours() != null ? course.getFlightHours() : 30)));

        wrapper.add(modulesList, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        footer.setBackground(WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        int theoretical = course.getTheoreticalHours() != null ? course.getTheoreticalHours() : 0;
        int practical = course.getFlightHours() != null ? course.getFlightHours() : 0;
        int simulator = Math.max(0, practical / 5);

        footer.add(createStatTag("Teórico: " + theoretical + "h", new Color(219, 234, 254), new Color(29, 78, 216)));
        footer.add(createStatTag("Prático: " + practical + "h", new Color(220, 252, 231), new Color(22, 163, 74)));
        footer.add(createStatTag("Simulador: " + simulator + "h", new Color(243, 232, 255), new Color(124, 58, 237)));

        JButton editBtn = new JButton("Editar");
        editBtn.setFont(new Font("Inter", Font.PLAIN, 11));
        editBtn.addActionListener(e -> editCourseDialog(course));
        footer.add(editBtn);

        JButton delBtn = new JButton("Eliminar");
        delBtn.setFont(new Font("Inter", Font.PLAIN, 11));
        delBtn.addActionListener(e -> deleteCourse(course));
        footer.add(delBtn);

        wrapper.add(footer, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createModuleRow(String index, String name, String type, int hours) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel idx = new JLabel(index, SwingConstants.CENTER);
        idx.setPreferredSize(new Dimension(24, 24));
        idx.setOpaque(true);
        idx.setBackground(new Color(241, 245, 249));
        idx.setForeground(MUTED);
        idx.setFont(new Font("Inter", Font.BOLD, 11));
        row.add(idx, BorderLayout.WEST);

        JLabel title = new JLabel(name);
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 12));
        row.add(title, BorderLayout.CENTER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JLabel h = new JLabel(hours + "h");
        h.setForeground(MUTED);
        h.setFont(new Font("Inter", Font.PLAIN, 11));
        right.add(h);
        right.add(typeBadge(type));
        row.add(right, BorderLayout.EAST);

        return row;
    }

    private JComponent typeBadge(String type) {
        Color bg;
        Color fg;
        String text;
        switch (type) {
            case "prático", "pratico" -> {
                bg = new Color(220, 252, 231);
                fg = new Color(22, 163, 74);
                text = "Prático";
            }
            case "simulador" -> {
                bg = new Color(243, 232, 255);
                fg = new Color(124, 58, 237);
                text = "Simulador";
            }
            default -> {
                bg = new Color(219, 234, 254);
                fg = new Color(29, 78, 216);
                text = "Teórico";
            }
        }
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setFont(new Font("Inter", Font.BOLD, 10));
        label.setBorder(new EmptyBorder(3, 8, 3, 8));
        return label;
    }

    private JComponent createStatTag(String text, Color bg, Color fg) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setFont(new Font("Inter", Font.BOLD, 11));
        label.setBorder(new EmptyBorder(4, 8, 4, 8));
        return label;
    }

    private void toggleExpand(Integer courseId) {
        if (expandedCourseId != null && detailPanels.containsKey(expandedCourseId)) {
            detailPanels.get(expandedCourseId).setVisible(false);
        }
        if (courseId.equals(expandedCourseId)) {
            expandedCourseId = null;
        } else {
            expandedCourseId = courseId;
            JPanel panel = detailPanels.get(courseId);
            if (panel != null) {
                panel.setVisible(true);
            }
        }
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private void createCourseDialog() {
        JTextField name = new JTextField();
        JTextField duration = new JTextField("12 meses");
        JTextField flightHours = new JTextField("45");
        JTextField theoreticalHours = new JTextField("120");
        JTextField price = new JTextField("8500");

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Nome"));
        form.add(name);
        form.add(new JLabel("Duração"));
        form.add(duration);
        form.add(new JLabel("Horas de voo"));
        form.add(flightHours);
        form.add(new JLabel("Horas teóricas"));
        form.add(theoreticalHours);
        form.add(new JLabel("Preço"));
        form.add(price);

        int result = JOptionPane.showConfirmDialog(this, form, "Novo Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                courseController.criarCurso(
                        name.getText().trim(),
                        duration.getText().trim(),
                        Integer.parseInt(flightHours.getText().trim()),
                        Integer.parseInt(theoreticalHours.getText().trim()),
                        Double.parseDouble(price.getText().trim())
                );
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao criar curso: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCourseDialog(Course course) {
        JTextField name = new JTextField(course.getName());
        JTextField duration = new JTextField(course.getDuration() != null ? course.getDuration() : "");
        JTextField flightHours = new JTextField(String.valueOf(course.getFlightHours() != null ? course.getFlightHours() : 0));
        JTextField theoreticalHours = new JTextField(String.valueOf(course.getTheoreticalHours() != null ? course.getTheoreticalHours() : 0));
        JTextField price = new JTextField(String.valueOf(course.getPrice() != null ? course.getPrice() : 0.0));

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Nome"));
        form.add(name);
        form.add(new JLabel("Duração"));
        form.add(duration);
        form.add(new JLabel("Horas de voo"));
        form.add(flightHours);
        form.add(new JLabel("Horas teóricas"));
        form.add(theoreticalHours);
        form.add(new JLabel("Preço"));
        form.add(price);

        int result = JOptionPane.showConfirmDialog(this, form, "Editar Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                courseController.atualizarCurso(
                        course.getId(),
                        name.getText().trim(),
                        duration.getText().trim(),
                        Integer.parseInt(flightHours.getText().trim()),
                        Integer.parseInt(theoreticalHours.getText().trim()),
                        Double.parseDouble(price.getText().trim())
                );
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar curso: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCourse(Course course) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Eliminar curso \"" + course.getName() + "\"?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseController.eliminarCurso(course.getId());
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao eliminar curso: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String buildMetaLine(Course course) {
        int flight = course.getFlightHours() != null ? course.getFlightHours() : 0;
        int theoretical = course.getTheoreticalHours() != null ? course.getTheoreticalHours() : 0;
        int enrolled = course.getEnrolled() != null ? course.getEnrolled() : 0;
        int completed = course.getCompleted() != null ? course.getCompleted() : 0;
        double price = course.getPrice() != null ? course.getPrice() : 0;
        return String.format(
                Locale.ROOT,
                "%s · %dh voo + %dh teórico · %d ativos · %d concluídos · €%,.2f",
                course.getDuration() != null ? course.getDuration() : "N/A",
                flight,
                theoretical,
                enrolled,
                completed,
                price
        );
    }

    private static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    private static Component findByName(Container root, String name) {
        for (Component c : root.getComponents()) {
            if (name.equals(c.getName())) {
                return c;
            }
            if (c instanceof Container child) {
                Component found = findByName(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
