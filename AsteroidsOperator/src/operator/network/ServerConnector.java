package operator.network;

import asteroidsoperator.AsteroidsOperator;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import server.network.basic.Address;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import operator.Operator;
import operator.ServerHandler;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ServerConnector extends Thread {

    private final Operator operator;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public ServerConnector(Operator operator) {
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Create");
        this.operator = operator;
        
        try {
            //serverSocket = new ServerSocket();
            serverSocket = new ServerSocket(0, 100, InetAddress.getLocalHost());
            //serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 0));
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to create ServerSocket");
        }
        
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Start with ServerSocket at {0}", getAddress());
        running = true;
        while (running) {
            try {
                ServerHandler serverHandler = new ServerHandler(serverSocket.accept(), operator);
                serverHandler.login();
            } catch (IOException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to connect with Server");
            }
        }
    }

    public void stopRunning() {
        running = false;
    }
    
    public void disconnect() {
        AsteroidsOperator.logger.log(FINE, "[ServerHandler] Close");
        try {
            serverSocket.close();
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to close ServerSocket");
        }
    }
    
    public Address getAddress() {
        return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }
    
}
