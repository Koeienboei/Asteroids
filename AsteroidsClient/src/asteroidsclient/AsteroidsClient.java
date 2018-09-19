/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsclient;

import controller.MainFrame;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Tom
 */
public class AsteroidsClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PrintStream out;
        PrintStream err;
        try {
            out = new PrintStream(new FileOutputStream("output.txt"));
            err = new PrintStream(new FileOutputStream("err.txt"));
            System.setOut(out);
            System.setErr(err);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AsteroidsClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        MainFrame mainFrame = new MainFrame();
    }
    
}
