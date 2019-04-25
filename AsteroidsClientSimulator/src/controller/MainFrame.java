/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import client.Client;
import client.ClientBot;
import static client.ClientState.CLOSE;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import server.network.basic.Address;
import view.StartPanel;

/**
 * Deze klasse representeert het hoofdvenster van het programma. Dit venster bestaat uit de menubalk en het GamePanel-object, waarin het model getekend wordt. Ook zitten hier de Action-objecten in.
 *
 * @author Wilco Wijbrandi
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements Runnable {

    private LinkedList<Client> clients;
    private MainFrame mainFrame;

    private LinkedList<Integer> amountClientsOverTime;

    private AbstractAction joinAction;

    private JPanel mainPanel;
    private StartPanel startPanel;

    public MainFrame(LinkedList<Integer> amountClientsOverTime) {
        clients = new LinkedList<>();
        mainFrame = this;
        this.amountClientsOverTime = amountClientsOverTime;
        initActions();
        //initGUI();
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

    @Override
    public void run() {
        Iterator<Integer> it = amountClientsOverTime.iterator();
        int next;
        int current = 0;
        int difference;
        while (it.hasNext()) {
            next = it.next();
            difference = Math.abs(next - current);
            if (next < current) {
                System.out.println("Closing " + difference + " clients");
                closeClients(difference);
            } else {
                System.out.println("Spawning " + difference + " clients");
                spawnClients(difference);
            }
            current = next;
            if (10000 - difference * 250 > 0) {
                try {
                    Thread.sleep(10000 - difference * 250);
                } catch (InterruptedException ex) {
                }
            }
        }
        closeClients();
    }

    private void spawnClients(int amount) {
        for (int i = 0; i < amount; i++) {
            spawnClient();
        }
    }

    private void spawnClient() {
        Client client;
        try {
            client = new ClientBot(/*startPanel.getOperatorAddress()*/new Address(InetAddress.getLocalHost().getHostAddress(), 8850));
            clients.add(client);
            client.start();
        } catch (UnknownHostException ex) {
        }

        try {
            Thread.sleep(250);
        } catch (InterruptedException ex) {
        }
    }

    private void closeClients() {
        Iterator<Client> it = clients.iterator();
        while (it.hasNext()) {
            Client client = it.next();
            client.logger.log(INFO, "[MainFrame] Window closing");
            client.close();
        }
    }

    private void closeClients(int amount) {
        for (int i = 0; i < amount; i++) {
            closeClient();
        }
    }

    private void closeClient() {
        if (!clients.isEmpty()) {
            clients.getFirst().close();
            clients.remove();
            try {
                Thread.sleep(250);
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
        public void windowClosing(WindowEvent e) {
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
