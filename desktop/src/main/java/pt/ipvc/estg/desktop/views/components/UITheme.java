package pt.ipvc.estg.desktop.views.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.JTableHeader;
import java.awt.*;

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
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(resolveFont(button.getFont()));
        if (button.getBackground() == null) {
            button.setBackground(WHITE);
        }
        if (button.isContentAreaFilled()) {
            button.setContentAreaFilled(false);
            button.setOpaque(false);
        }
        Border current = button.getBorder();
        if (current == null || current instanceof LineBorder || current instanceof EmptyBorder) {
            button.setBorder(new CompoundBorder(
                    new RoundedFillBorder(button.getBackground(), BORDER, 1, 16),
                    new EmptyBorder(8, 12, 8, 12)
            ));
        } else if (current instanceof CompoundBorder compoundBorder
                && compoundBorder.getOutsideBorder() instanceof LineBorder lineBorder) {
            button.setBorder(new CompoundBorder(
                    new RoundedFillBorder(button.getBackground(), lineBorder.getLineColor(), lineBorder.getThickness(), 16),
                    compoundBorder.getInsideBorder()
            ));
        }
    }

    private static void polishTextField(JTextField textField) {
        textField.setFont(new Font("Inter", Font.PLAIN, 12));
        textField.setBackground(ROW_HOVER);
        textField.setForeground(TITLE);
        textField.setCaretColor(BLUE);
        textField.setOpaque(false);
        textField.setBorder(new CompoundBorder(
                new RoundedFillBorder(ROW_HOVER, BORDER, 1, 16),
                new EmptyBorder(8, 10, 8, 10)
        ));
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, Math.max(38, textField.getPreferredSize().height)));
    }

    private static void polishTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Inter", Font.PLAIN, 12));
        textArea.setBackground(ROW_HOVER);
        textArea.setForeground(TITLE);
        textArea.setCaretColor(BLUE);
        textArea.setBorder(new EmptyBorder(8, 10, 8, 10));
    }

    private static void polishComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Inter", Font.PLAIN, 12));
        comboBox.setBackground(WHITE);
        comboBox.setForeground(TITLE);
        comboBox.setOpaque(false);
        comboBox.setBorder(new CompoundBorder(
                new RoundedFillBorder(WHITE, BORDER, 1, 16),
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
