/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.CardLayout;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import model.AsteroidsModel;
import server.ClientData;
import server.Server;

/**
 *
 * @author Tom
 */
public class ServerPanel extends JPanel implements Observer {
    
    private JLabel connectionInformation;
    private JLabel occupation;
    private JTextArea clientData;
    
    private Server server;
    
    public ServerPanel(Server server) {
        this.server = server;
        server.addObserver(this);
        
        connectionInformation = new JLabel("Server address:  " + server.getInputHandler().getAddress());
        occupation = new JLabel("Occupation:  " + server.getOccupation());
        clientData = new JTextArea("");
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(connectionInformation);
        this.add(occupation);
        this.add(clientData);
        
        this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        occupation.setText("Occupation:  " + server.getOccupation());
        
        clientData.setText("");
        Collection<ClientData> clients = server.getClients();
        Iterator<ClientData> it = clients.iterator();
        while (it.hasNext()) {
            ClientData client = it.next();
            clientData.append(client.toString() + "\n");
        }
    }
    
}
