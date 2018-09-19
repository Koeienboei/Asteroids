/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import packeges.Address;

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
        this.serverNumber = serverNumber;
        this.height = height;
        this.width = width;
        this.serverHandlerAddress = serverHandlerAddress;
    }
    
    @Override
    public void run() {
        String[] command = {"java", "-jar", asteroidsServerPath , Integer.toString(height), Integer.toString(width), serverHandlerAddress.getIp(), Integer.toString(serverHandlerAddress.getPort())};
        ProcessBuilder probuilder = new ProcessBuilder(command);

        Process process;
        try {
            System.out.println("Creating new output file for server");
            File outputFile = new File(asteroidsServerOutputPath + "output" + serverNumber + ".txt");
            File errFile = new File(asteroidsServerOutputPath + "err" + serverNumber + ".txt");
            
            probuilder.redirectOutput(outputFile);
            probuilder.redirectError(errFile);
            process = probuilder.start();
            
            int status = process.waitFor();
            System.out.println("Exited with status: " + status);
        } catch (IOException ex) {
            Logger.getLogger(Operator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
