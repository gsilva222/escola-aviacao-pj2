package pt.ipvc.estg.desktop;

import javax.swing.*;

/**
 * Classe principal da aplicação Desktop
 */
public class DesktopApp {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestão Escola de Aviação");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            
            // TODO: Aqui será adicionada a interface gráfica
            JLabel label = new JLabel("Bem-vindo à Gestão da Escola de Aviação!");
            frame.add(label);
            
            frame.setVisible(true);
        });
    }
}
