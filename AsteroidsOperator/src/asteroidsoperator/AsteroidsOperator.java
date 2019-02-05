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
        
        Operator operator = new Operator();
        MainFrame mainFrame = new MainFrame(operator);
        operator.start();
    }
    
}
