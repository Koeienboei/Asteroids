/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsserver;

import controller.MainFrame;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import packeges.Address;
import server.Server;

/**
 *
 * @author Tom
 */
public class AsteroidsServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int height = Integer.parseInt(args[0]);
        int width = Integer.parseInt(args[1]);
        
        String operatorIp = args[2];
        int operatorPort = Integer.parseInt(args[3]);
        
        System.out.println("Starting server: (" + height + "," + width + ") operator address " + operatorIp + ":" + operatorPort);
        Server server = new Server(height, width, new Address(operatorIp, operatorPort));
        System.out.println("Starting mainframe");
        MainFrame mainFrame = new MainFrame(server);
    }
    
}
