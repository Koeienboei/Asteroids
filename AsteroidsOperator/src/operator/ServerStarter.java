/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import server.network.basic.Address;

/**
 *
 * @author tomei
 */
public class ServerStarter extends Thread {
    
    private String asteroidsServerPath = "C:\\Users\\tomei\\Dropbox\\Bachelor project\\Version 2.4\\AsteroidsServer\\dist\\AsteroidsServer.jar";
    private String asteroidsServerOutputPath = "C:\\Users\\tomei\\Dropbox\\Bachelor project\\Version 2.4\\AsteroidsServer\\dist\\";
    
    private int serverNumber;
    private int height;
    private int width;
    private Address serverHandlerAddress;
    
    public ServerStarter(int serverNumber, int height, int width, Address serverHandlerAddress) {
        AsteroidsOperator.logger.log(FINE, "[ServerStarter] Create");
        this.serverNumber = serverNumber;
        this.height = height;
        this.width = width;
        this.serverHandlerAddress = serverHandlerAddress;
    }
    
    @Override
    public void run() {
        AsteroidsOperator.logger.log(FINE, "[ServerStarter] Start");
        String[] command = {"java", "-jar", asteroidsServerPath , Integer.toString(height), Integer.toString(width), serverHandlerAddress.getIp(), Integer.toString(serverHandlerAddress.getPort())};
        ProcessBuilder probuilder = new ProcessBuilder(command);

        Process process;
        try {
            process = probuilder.start();
            int status = process.waitFor();
            AsteroidsOperator.logger.log(FINE, "[ServerStarter] Exited with status: {0}", status);
        } catch (InterruptedException e) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerStarter] Server process interupted");
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerStarter] Server process failed");
        }
    }
    
}
