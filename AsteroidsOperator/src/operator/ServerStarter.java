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
import java.util.Arrays;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import server.network.basic.Address;

/**
 *
 * @author tomei
 */
public class ServerStarter extends Thread {
    
    private static final boolean useDocker = false;
    
    public ServerStarter() {
        AsteroidsOperator.logger.log(FINE, "[ServerStarter] Create");
    }
    
    private String[] getCommand() {
        if (useDocker) {
            return new String[] {"docker", "run", /*"-it", "--rm",*/ "--net=host", "-P", "server:latest"};
        } else {
            return new String[] {"java", "-jar", "lib/AsteroidsServer.jar", "1600", "1600", "127.0.1.1", "8851"};
        }
    }
    
    @Override
    public void run() {
        AsteroidsOperator.logger.log(INFO, "[ServerStarter] Start");
        String[] command = getCommand();
        System.out.println("Starting new server");
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println("Command: " + String.join(" ", command));
        ProcessBuilder probuilder = new ProcessBuilder(command);
        
        Process process;
        try {
            process = probuilder.start();
            int status = process.waitFor();
            AsteroidsOperator.logger.log(INFO, "[ServerStarter] Exited with status: {0}", status);
        } catch (InterruptedException e) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerStarter] Server process interupted");
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerStarter] Server process failed");
        }
    }
    
}
