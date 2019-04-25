/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import asteroidsoperator.AsteroidsOperator;
import java.awt.Font;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import static java.util.logging.Level.FINE;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import operator.Operator;
import operator.ServerHandler;

/**
 *
 * @author Tom
 */
public class OperatorPanel extends JPanel implements Observer {
    
    private JLabel addressForClients;
    private JLabel addressForServers;
    private JTextArea serverData;
    
    private Operator operator;
    
    public OperatorPanel(Operator operator) {
        AsteroidsOperator.logger.log(FINE, "[OperatorPanel] Create");
        this.operator = operator;
        operator.addObserver(this);
        
        addressForClients = new JLabel("Address for clients:  " + operator.getClientConnector().getAddress());
        addressForServers = new JLabel("Address for servers: " + operator.getServerConnector().getAddress());
        serverData = new JTextArea("");
        addressForClients.setFont(new Font("Courier New", Font.PLAIN, 12));
        addressForServers.setFont(new Font("Courier New", Font.PLAIN, 12));
        serverData.setFont(new Font("Courier New", Font.PLAIN, 12));
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(addressForClients);
        this.add(addressForServers);
        this.add(serverData);
        
        this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        AsteroidsOperator.logger.log(FINE, "[OperatorPanel] Observed Update");
        String text = "";
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            text += it.next().toString() + "\n";
            
        }
        serverData.setText(text);
    }
    
}
