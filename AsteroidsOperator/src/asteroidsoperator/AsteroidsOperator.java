/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsoperator;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import static java.util.logging.Level.ALL;
import static java.util.logging.Level.INFO;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import view.MainFrame;
import operator.Operator;

/**
 *
 * @author tomei
 */
public class AsteroidsOperator {

    public static final Logger logger = Logger.getGlobal();
    private static FileHandler fh;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try { 
            fh = new FileHandler("OperatorLogs.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
        }
        logger.setLevel(Level.INFO);
        
        int rLow, rHigh, rMax, W, reconfigurationSpeed;
        try {
            rLow = Integer.parseInt(args[0]);
            rHigh = Integer.parseInt(args[1]);
            rMax = Integer.parseInt(args[2]);
            W = Integer.parseInt(args[3]);
            reconfigurationSpeed = Integer.parseInt(args[4]);
        } catch (Exception ex) {
            rLow = 1;
            rHigh = 5;
            rMax = 10;
            W = 3;
            reconfigurationSpeed = 10;
        }
        
        Operator operator = new Operator(rLow, rHigh, rMax, W, reconfigurationSpeed);
        MainFrame mainFrame = new MainFrame(operator);
        operator.start();
    }
    
}
