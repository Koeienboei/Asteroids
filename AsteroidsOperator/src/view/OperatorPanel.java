/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import asteroidsoperator.AsteroidsOperator;
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
    
    private JLabel connectionInformation;
    private JTextArea serverData;
    
    private Operator operator;
    
    public OperatorPanel(Operator operator) {
        AsteroidsOperator.logger.log(FINE, "[OperatorPanel] Create");
        this.operator = operator;
        operator.addObserver(this);
        
        connectionInformation = new JLabel("Operator address:  " + operator.getClientConnector().getAddress());
        serverData = new JTextArea("");
        
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(connectionInformation);
        this.add(serverData);
        
        this.setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        AsteroidsOperator.logger.log(FINE, "[OperatorPanel] Observed Update");
        serverData.setText("");
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            serverData.append(it.next().toString() + "\n");
        }
    }
    
}
