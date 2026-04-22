package pt.ipvc.estg.desktop;

import pt.ipvc.estg.desktop.views.LoginFrame;

import javax.swing.*;

/**
 * Ponto de entrada da aplicação Desktop - AeroSchool
 * Inicia o fluxo de autenticação (LoginFrame)
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
