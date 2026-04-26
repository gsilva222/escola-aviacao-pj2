package pt.ipvc.estg.desktop;

import pt.ipvc.estg.dal.mock.MockDataSeeder;
import pt.ipvc.estg.desktop.views.LandingFrame;
import pt.ipvc.estg.desktop.views.components.UITheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        UITheme.install();
        MockDataSeeder.seedAllData();

        SwingUtilities.invokeLater(() -> {
            LandingFrame landingFrame = new LandingFrame();
            landingFrame.setVisible(true);
        });
    }
}
