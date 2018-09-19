/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import server.Server;
import view.ServerPanel;

/**
 * Deze klasse representeert het hoofdvenster van het programma. Dit venster
 * bestaat uit de menubalk en het GamePanel-object, waarin het model getekend
 * wordt. Ook zitten hier de Action-objecten in.
 *
 * @author Wilco Wijbrandi
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private Server server;
    
    private JPanel serverPanel;

    public MainFrame(Server server) {
        this.server = server;
        
        initGUI();
        
        serverPanel = new ServerPanel(server);
        this.add(serverPanel);
        
        // Maak het venster zichtbaar
        this.setVisible(true);
        System.out.println("Done with mainframe");
    }

    /**
     * Initialize the GUI.
     */
    private void initGUI() {
        this.setTitle("Asteroids Server");
        this.setSize(800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        this.addWindowListener(new Exit());
    }
    
    private class Exit implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            server.close();
        }

        @Override
        public void windowClosed(WindowEvent e) {
        }

        @Override
        public void windowIconified(WindowEvent e) {
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
        }
        
    }
    
}
