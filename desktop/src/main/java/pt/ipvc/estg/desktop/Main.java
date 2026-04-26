package pt.ipvc.estg.desktop;

import pt.ipvc.estg.desktop.views.LoginFrame;
import pt.ipvc.estg.dal.mock.MockDataSeeder;

import javax.swing.*;

/**
 * Ponto de entrada da aplicação Desktop - AeroSchool
 * Inicia o fluxo de autenticação (LoginFrame)
 */
public class Main {
    public static void main(String[] args) {
        // Seed de dados mock na inicialização
        MockDataSeeder.seedAllData();
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
