package pt.ipvc.estg.desktop.views.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

public final class UITheme {

    public static final Color PAGE_BG = new Color(238, 242, 247);
    public static final Color FRONT_BG = new Color(240, 246, 255);
    public static final Color WHITE = Color.WHITE;
    public static final Color TITLE = new Color(15, 35, 68);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color SOFT = new Color(148, 163, 184);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Color BLUE = new Color(21, 101, 192);
    public static final Color BLUE_DARK = new Color(13, 71, 161);
    public static final Color ROW_HOVER = new Color(248, 250, 252);
    public static final Color SELECTED = new Color(239, 246, 255);

    private UITheme() {
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Keep the platform default if the system look and feel is not available.
        }

        Font base = new Font("Inter", Font.PLAIN, 12);
        UIManager.put("Label.font", base);
        UIManager.put("Button.font", base.deriveFont(Font.BOLD, 12f));
        UIManager.put("TextField.font", base);
        UIManager.put("TextArea.font", base);
        UIManager.put("ComboBox.font", base);
        UIManager.put("Table.font", base);
        UIManager.put("TableHeader.font", base.deriveFont(Font.BOLD, 11f));
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("Panel.background", PAGE_BG);
        UIManager.put("Table.selectionBackground", SELECTED);
        UIManager.put("Table.selectionForeground", TITLE);
        UIManager.put("TextField.caretForeground", BLUE);
    }

    public static void polishTree(Component component) {
        if (component == null) {
            return;
        }

        polishComponent(component);

        if (component instanceof JScrollPane scrollPane) {
            polishScrollPane(scrollPane);
        }
        if (component instanceof JTable table) {
            polishTable(table);
        }
        if (component instanceof JButton button) {
            polishButton(button);
        }
        if (component instanceof JTextField textField) {
            polishTextField(textField);
        }
        if (component instanceof JTextArea textArea) {
            polishTextArea(textArea);
        }
        if (component instanceof JComboBox<?> comboBox) {
            polishComboBox(comboBox);
        }
        if (component instanceof JProgressBar progressBar) {
            polishProgressBar(progressBar);
        }

        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                polishTree(child);
            }
        }
    }

    public static Border cardBorder() {
        return new CompoundBorder(
                new RoundedFillBorder(WHITE, BORDER, 1, 18),
                new EmptyBorder(12, 12, 12, 12)
        );
    }

    public static Border controlBorder() {
        return new CompoundBorder(
                new RoundedFillBorder(ROW_HOVER, BORDER, 1, 16),
                new EmptyBorder(8, 10, 8, 10)
        );
    }

    private static void polishComponent(Component component) {
        if (component instanceof JComponent jComponent) {
            jComponent.setFont(resolveFont(jComponent.getFont()));

            if (jComponent instanceof JPanel panel) {
                Border border = panel.getBorder();
                if (panel.isOpaque() && WHITE.equals(panel.getBackground()) && !isInNavigationShell(panel)) {
                    if (border instanceof LineBorder lineBorder) {
                        panel.setOpaque(false);
                        panel.setBorder(new RoundedFillBorder(WHITE, lineBorder.getLineColor(), lineBorder.getThickness(), 18));
                    } else if (border instanceof CompoundBorder compoundBorder
                            && compoundBorder.getOutsideBorder() instanceof LineBorder lineBorder) {
                        panel.setOpaque(false);
                        panel.setBorder(new CompoundBorder(
                                new RoundedFillBorder(WHITE, lineBorder.getLineColor(), lineBorder.getThickness(), 18),
                                compoundBorder.getInsideBorder()
                        ));
                    }
                }
            }
        }
    }

    private static Font resolveFont(Font current) {
        if (current == null) {
            return new Font("Inter", Font.PLAIN, 12);
        }
        if ("Dialog".equals(current.getFamily()) || "Arial".equals(current.getFamily())) {
            return new Font("Inter", current.getStyle(), current.getSize());
        }
        return current;
    }

    private static boolean isInNavigationShell(Component component) {
        Component current = component;
        while (current != null) {
            String className = current.getClass().getName();
            if (className.endsWith(".Sidebar") || className.endsWith(".TopBar")) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private static void polishScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(true);
        if (scrollPane.getViewport().getView() instanceof JComponent view && view.getBackground() != null) {
            scrollPane.getViewport().setBackground(view.getBackground());
        } else {
            scrollPane.getViewport().setBackground(PAGE_BG);
        }
        polishScrollBar(scrollPane.getVerticalScrollBar());
        polishScrollBar(scrollPane.getHorizontalScrollBar());
    }

    private static void polishScrollBar(JScrollBar scrollBar) {
        if (scrollBar == null) {
            return;
        }
        scrollBar.setUnitIncrement(18);
        scrollBar.setBlockIncrement(90);
        scrollBar.setPreferredSize(scrollBar.getOrientation() == Adjustable.VERTICAL
                ? new Dimension(10, 0)
                : new Dimension(0, 10));
        scrollBar.setBorder(BorderFactory.createEmptyBorder());
        scrollBar.setUI(new ModernScrollBarUI());
    }

    private static void polishTable(JTable table) {
        table.setFont(new Font("Inter", Font.PLAIN, 12));
        table.setRowHeight(Math.max(table.getRowHeight(), 46));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(SELECTED);
        table.setSelectionForeground(TITLE);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFocusable(false);
        table.setFillsViewportHeight(true);
        table.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setReorderingAllowed(false);
            header.setBackground(ROW_HOVER);
            header.setForeground(MUTED);
            header.setFont(new Font("Inter", Font.BOLD, 11));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        }
    }

    private static void polishButton(JButton button) {
        // Navigation/topbar buttons paint themselves and must stay untouched.
        if (isInNavigationShell(button) || isInternalControl(button)) {
            return;
        }

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setRolloverEnabled(true);

        Color foreground = button.getForeground();
        Color background = button.getBackground() != null ? button.getBackground() : WHITE;
        Font font = resolveFont(button.getFont());
        Insets padding = extractPadding(button.getBorder(), new Insets(9, 16, 9, 16));

        // A custom UI paints the rounded background *before* the text, so the
        // label is never covered (unlike a fill border, which paints last).
        button.setUI(new RoundedButtonUI());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFont(font);
        button.setBorder(new EmptyBorder(padding));
    }

    private static Insets extractPadding(Border border, Insets fallback) {
        if (border instanceof EmptyBorder emptyBorder) {
            return emptyBorder.getBorderInsets();
        }
        if (border instanceof CompoundBorder compoundBorder
                && compoundBorder.getInsideBorder() instanceof EmptyBorder emptyBorder) {
            return emptyBorder.getBorderInsets();
        }
        return fallback;
    }

    private static boolean isInternalControl(Component component) {
        Component current = component.getParent();
        while (current != null) {
            if (current instanceof JComboBox<?> || current instanceof JScrollBar || current instanceof JSpinner) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private static void polishTextField(JTextField textField) {
        if (isInNavigationShell(textField) || isInternalControl(textField)) {
            return;
        }
        textField.setFont(new Font("Inter", Font.PLAIN, 12));
        textField.setBackground(ROW_HOVER);
        textField.setForeground(TITLE);
        textField.setCaretColor(BLUE);
        textField.setOpaque(true);
        // The component fills its own background (opaque); the border only rounds
        // the corners, so the typed text stays fully visible.
        textField.setBorder(new CompoundBorder(
                new RoundedOutlineBorder(BORDER, 1, 16),
                new EmptyBorder(8, 10, 8, 10)
        ));
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, Math.max(38, textField.getPreferredSize().height)));
    }

    private static void polishTextArea(JTextArea textArea) {
        if (isInNavigationShell(textArea)) {
            return;
        }
        textArea.setFont(new Font("Inter", Font.PLAIN, 12));
        textArea.setBackground(ROW_HOVER);
        textArea.setForeground(TITLE);
        textArea.setCaretColor(BLUE);
        textArea.setOpaque(true);
        textArea.setBorder(new EmptyBorder(8, 10, 8, 10));
    }

    private static void polishComboBox(JComboBox<?> comboBox) {
        if (isInNavigationShell(comboBox)) {
            return;
        }
        comboBox.setFont(new Font("Inter", Font.PLAIN, 12));
        comboBox.setBackground(WHITE);
        comboBox.setForeground(TITLE);
        comboBox.setOpaque(true);
        comboBox.setBorder(new CompoundBorder(
                new RoundedOutlineBorder(BORDER, 1, 16),
                new EmptyBorder(7, 10, 7, 10)
        ));
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, Math.max(38, comboBox.getPreferredSize().height)));
    }

    private static void polishProgressBar(JProgressBar progressBar) {
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(226, 232, 240));
        if (progressBar.getForeground() == null) {
            progressBar.setForeground(BLUE);
        }
    }

    private static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(184, 198, 216);
            trackColor = new Color(244, 247, 251);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(trackColor);
            g2d.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, 10, 10);
            g2d.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (!scrollbar.isEnabled() || thumbBounds.isEmpty()) {
                return;
            }
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(isDragging ? new Color(126, 148, 176) : thumbColor);
            g2d.fillRoundRect(
                    thumbBounds.x + 2,
                    thumbBounds.y + 2,
                    thumbBounds.width - 4,
                    thumbBounds.height - 4,
                    10,
                    10
            );
            g2d.dispose();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }

    private static Color darken(Color color, float factor) {
        return new Color(
                Math.max(0, Math.round(color.getRed() * (1 - factor))),
                Math.max(0, Math.round(color.getGreen() * (1 - factor))),
                Math.max(0, Math.round(color.getBlue() * (1 - factor)))
        );
    }

    private static Color blend(Color from, Color to, float ratio) {
        return new Color(
                Math.round(from.getRed() + (to.getRed() - from.getRed()) * ratio),
                Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * ratio),
                Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * ratio)
        );
    }

    private static boolean isLight(Color color) {
        double luminance = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
        return luminance > 200;
    }

    /**
     * Paints a flat, rounded button background before the label, so the text is
     * always drawn on top (instead of being covered by a fill border).
     */
    private static class RoundedButtonUI extends BasicButtonUI {
        private static final int ARC = 16;

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Color base = button.getBackground() != null ? button.getBackground() : WHITE;
            ButtonModel model = button.getModel();

            Color fill = base;
            if (!button.isEnabled()) {
                fill = blend(base, WHITE, 0.5f);
            } else if (model.isPressed()) {
                fill = darken(base, 0.12f);
            } else if (model.isRollover()) {
                fill = darken(base, 0.05f);
            }

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(fill);
            g2d.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, ARC, ARC);
            g2d.setColor(isLight(base) ? BORDER : darken(base, 0.10f));
            g2d.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, ARC, ARC);
            g2d.dispose();

            super.paint(g, c);
        }
    }

    /**
     * Rounds the corners of an opaque component by repainting the four corner
     * slivers with the parent background, then strokes a rounded outline.
     * Unlike {@link RoundedFillBorder}, it never paints over the component's
     * own content (text, combo value, etc.).
     */
    private static class RoundedOutlineBorder extends LineBorder {
        private final int radius;

        RoundedOutlineBorder(Color color, int thickness, int radius) {
            super(color, thickness, true);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color corner = WHITE;
            Container parent = c.getParent();
            if (parent != null && parent.getBackground() != null) {
                corner = parent.getBackground();
            }

            Area area = new Area(new Rectangle(x, y, width, height));
            area.subtract(new Area(new RoundRectangle2D.Float(x, y, width - 1f, height - 1f, radius, radius)));
            g2d.setColor(corner);
            g2d.fill(area);

            g2d.setColor(lineColor);
            for (int i = 0; i < thickness; i++) {
                g2d.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, radius, radius);
            }
            g2d.dispose();
        }
    }

    private static class RoundedLineBorder extends LineBorder {
        private final int radius;

        RoundedLineBorder(Color color, int thickness, int radius) {
            super(color, thickness, true);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(lineColor);
            for (int i = 0; i < thickness; i++) {
                g2d.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, radius, radius);
            }
            g2d.dispose();
        }
    }

    private static class RoundedFillBorder extends LineBorder {
        private final Color fillColor;
        private final int radius;

        RoundedFillBorder(Color fillColor, Color borderColor, int thickness, int radius) {
            super(borderColor, thickness, true);
            this.fillColor = fillColor;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = c.getBackground() != null ? c.getBackground() : fillColor;
            g2d.setColor(bg);
            g2d.fillRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.setColor(lineColor);
            for (int i = 0; i < thickness; i++) {
                g2d.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, radius, radius);
            }
            g2d.dispose();
        }
    }
}
