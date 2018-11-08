/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import asteroidsclient.AsteroidsClient;
import client.Client;
import client.ClientPlayer;
import static client.ClientState.INITIALIZE;
import static client.ClientState.PLAYING;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import static java.util.logging.Level.FINE;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static server.ClientState.DEAD;
import view.GamePanel;
import view.GamePanel;
import view.StartPanel;
import view.StartPanel;

/**
 * Deze klasse representeert het hoofdvenster van het programma. Dit venster
 * bestaat uit de menubalk en het GamePanel-object, waarin het model getekend
 * wordt. Ook zitten hier de Action-objecten in.
 *
 * @author Wilco Wijbrandi
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private Client client;
    private MainFrame mainFrame;

    private AbstractAction joinAction;

    private JPanel mainPanel;
    private StartPanel startPanel;
    private GamePanel gamePanel;

    public MainFrame() {
        client = null;
        mainFrame = this;

        initActions();
        initGUI();
    }

    private void initGUI() {
        this.setTitle("Connect to server");
        this.setSize(400, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        mainPanel = new JPanel(new CardLayout());
        getContentPane().add(mainPanel);

        startPanel = new StartPanel(this);
        mainPanel.add(startPanel, "start");

        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, "game");

        this.setVisible(true);
        this.addWindowListener(new Exit());
    }

    private void initActions() {
        this.joinAction = new AbstractAction("Join") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                mainFrame.setSize(800, 800);
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "game");
                gamePanel.requestFocus();
                client = new ClientPlayer(startPanel.getOperatorAddress(), mainFrame);
                client.start();
            }
        };
    }

    public AbstractAction getJoinAction() {
        return joinAction;
    }

    private class Exit implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            if (client != null) {
                client.logger.log(FINE, "[MainFrame] Window closing");
                client.getServerConnector().sendLogoutPacket();
            }
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

    public GamePanel getGamePanel() {
        return gamePanel;
    }

}
