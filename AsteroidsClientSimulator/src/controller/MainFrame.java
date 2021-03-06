/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import client.Client;
import client.ClientBot;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Iterator;
import java.util.LinkedList;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import view.StartPanel;

/**
 * Deze klasse representeert het hoofdvenster van het programma. Dit venster bestaat uit de menubalk en het GamePanel-object, waarin het model getekend wordt. Ook zitten hier de Action-objecten in.
 *
 * @author Wilco Wijbrandi
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private LinkedList<Client> clients;
    private MainFrame mainFrame;

    private AbstractAction joinAction;

    private JPanel mainPanel;
    private StartPanel startPanel;

    public MainFrame() {
        clients = new LinkedList<>();
        mainFrame = this;

        initActions();
        initGUI();
    }

    private void initGUI() {
        this.setTitle("Connect to server");
        this.setSize(500, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        mainPanel = new JPanel(new CardLayout());
        getContentPane().add(mainPanel);

        startPanel = new StartPanel(this);
        mainPanel.add(startPanel, "start");

        this.setVisible(true);
        this.addWindowListener(new Exit());
    }

    private synchronized void spawnClients(int amount) {
        for (int i = 0; i < amount; i++) {
            spawnClient();
        }
    }

    private synchronized void spawnClient() {
        Client client = new ClientBot(startPanel.getOperatorAddress());
        clients.add(client);
        client.start();
        try {
            this.wait(100);
        } catch (InterruptedException ex) {
        }
    }

    private synchronized void closeClients() {
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            client.logger.log(FINE, "[MainFrame] Window closing");
            client.close();
            it.remove();
            try {
                this.wait(100);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void initActions() {
        this.joinAction = new AbstractAction("Join") {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                spawnClients(startPanel.getAmount());
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
        public synchronized void windowClosing(WindowEvent e) {
            closeClients();
            System.exit(0);
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
