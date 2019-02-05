/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsserver;

import controller.MainFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import static java.util.logging.Level.ALL;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.OFF;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import server.network.basic.Address;
import server.Server;

/**
 *
 * @author Tom
 */
public class AsteroidsServer {

    public static final Logger logger = Logger.getGlobal();
    private static FileHandler fh;
    private static long loggerId;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            loggerId = System.currentTimeMillis();
            fh = new FileHandler("ServerLogs[" + loggerId + "].log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
        }
        logger.setLevel(INFO);

        try {
            PrintStream error = new PrintStream(new FileOutputStream("error.txt"));
            System.setErr(error);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AsteroidsServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.log(SEVERE, "Uncaught exception: ", e);
            }
        ;
        });
        
        int height = Integer.parseInt(args[0]);
        int width = Integer.parseInt(args[1]);

        String operatorIp = args[2];
        int operatorPort = Integer.parseInt(args[3]);

        Server server = new Server(height, width, new Address(operatorIp, operatorPort));

        server.start();
    }

}
