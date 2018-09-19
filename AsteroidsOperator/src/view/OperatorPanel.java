/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import operator.Operator;
import operator.ServerData;

/**
 *
 * @author Tom
 */
public class OperatorPanel extends JPanel implements Observer {
    
    private JLabel connectionInformation;
    private JTextArea serverData;
    
    private Operator operator;
    
    public OperatorPanel(Operator operator) {
        this.operator = operator;
        operator.addObserver(this);
        
        connectionInformation = new JLabel("Operator address:  " + operator.getClientHandler().getAddress());
        serverData = new JTextArea("");
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(connectionInformation);
        this.add(serverData);
        
        this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        serverData.setText("");
        Collection<ServerData> servers = operator.getServerDataList();
        Iterator<ServerData> it = servers.iterator();
        while (it.hasNext()) {
            ServerData server = it.next();
            serverData.append(server.toString() + "\n");
        }
    }
    
}
