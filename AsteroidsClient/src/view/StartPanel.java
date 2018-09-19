package view;

import packeges.Address;
import controller.MainFrame;
import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/* In this panel the menu is created. */
@SuppressWarnings("serial")
public class StartPanel extends JPanel {

    private JLabel ipLabel;
    private JLabel portLabel;
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;


    public StartPanel(MainFrame mainFrame) {
        this.setBackground(Color.WHITE);
        
        ipLabel = new JLabel("ip:");
        this.add(ipLabel);
        ipField = new JTextField(20);
        this.add(ipField);
        portLabel = new JLabel("port:");
        this.add(portLabel);
        portField = new JTextField(6);
        this.add(portField);
        
        // Add a button for a singleplayer game
        connectButton = new JButton("Connect");
        connectButton.addActionListener(mainFrame.getJoinAction());
        this.add(connectButton);
        
        this.setVisible(true);
        
        try {
            ipField.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            ipField.setText("");
        }
        portField.setText("8850");
    }
    
    private String getOperatorIp() {
        return ipField.getText();
    }
    
    private int getOperatorPort() {
        return Integer.parseInt(portField.getText());
    }
    
    public Address getOperatorAddress() {
        return new Address(getOperatorIp(), getOperatorPort());
    }
}
