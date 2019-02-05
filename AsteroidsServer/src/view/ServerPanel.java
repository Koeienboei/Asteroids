/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import asteroidsserver.AsteroidsServer;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import server.ClientHandler;
import server.Server;

/**
 *
 * @author Tom
 */
public class ServerPanel extends JPanel implements Observer {
    
    private JLabel connectionInformation;
    private JLabel occupation;
    private JTextArea textAreaClients;
    
    private Server server;
    private ConcurrentLinkedQueue<ClientHandler> clients;
    
    public ServerPanel() {
        AsteroidsServer.logger.log(FINE, "[ServerPanel] Create");
        this.clients = new ConcurrentLinkedQueue<>();
        initGUI();
    }
    
    private void initGUI() {
        connectionInformation = new JLabel();
        occupation = new JLabel();
        textAreaClients = new JTextArea();
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(connectionInformation);
        this.add(occupation);
        this.add(textAreaClients);
        
        this.setVisible(true);
    }
    
    private void displayServerInformation() {
        if (server != null) {
            connectionInformation.setText("Server address:  " + server.getClientConnector().getAddress());
            occupation.setText("Occupation:  " + server.getOccupation());
        }
    }
    
    private void displayClientInformation() {
        textAreaClients.setText("");
        Iterator<ClientHandler> it = clients.iterator();
        while (it.hasNext()) {
            ClientHandler clientHandler = it.next();
            textAreaClients.append(clientHandler.toString() + "\n");
        }
    }

    public void addClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "[ServerPanel] Add client");
        clients.add(clientHandler);
    }
    
    public void removeClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "[ServerPanel] Remove client");
        clients.remove(clientHandler);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        AsteroidsServer.logger.log(FINE, "[ServerPanel] Observed update");
        displayServerInformation();
        displayClientInformation();
        AsteroidsServer.logger.log(FINE, "[ServerPanel] End of observed update");
    }
    
    public void setServer(Server server) {
        AsteroidsServer.logger.log(FINE, "[ServerPanel] Set server");
        this.server = server;
        server.addObserver(this);
        displayServerInformation();
    }
    
}
