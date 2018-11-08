/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package view;

import asteroidsoperator.AsteroidsOperator;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import javax.swing.JFrame;
import javax.swing.JPanel;
import operator.Operator;
import view.OperatorPanel;

/**
 * Deze klasse representeert het hoofdvenster van het programma. Dit venster
 * bestaat uit de menubalk en het GamePanel-object, waarin het model getekend
 * wordt. Ook zitten hier de Action-objecten in.
 *
 * @author Wilco Wijbrandi
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private Operator operator;
    
    private JPanel operatorPanel;

    public MainFrame(Operator operator) {
        AsteroidsOperator.logger.log(FINE, "[MainFrame] Create");
        this.operator = operator;
        
        initGUI();
        
        operatorPanel = new OperatorPanel(operator);
        this.add(operatorPanel);
        
        // Maak het venster zichtbaar
        this.setVisible(true);
    }

    /**
     * Initialize the GUI.
     */
    private void initGUI() {
        AsteroidsOperator.logger.log(FINE, "[MainFrame] Initialize GUI");
        this.setTitle("Asteroids Operator");
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
            AsteroidsOperator.logger.log(FINE, "[MainFrame] Window closing");
            operator.shutdown();
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
