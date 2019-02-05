package operator.network;

import asteroidsoperator.AsteroidsOperator;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import static java.util.logging.Level.FINE;
import server.network.basic.Address;
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
        AsteroidsOperator.logger.log(FINE, "[ServerConnector] Create");
        this.operator = operator;
        
        try {
            serverSocket = new ServerSocket(8851, 100, InetAddress.getLocalHost());
            //serverSocket.setSoTimeout(40);
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerConnector] Failed to create ServerSocket");
        }
        
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsOperator.logger.log(FINE, "[ServerConnector] Start with ServerSocket at {0}", getAddress());
        running = true;
        while (running) {
            try {
                AsteroidsOperator.logger.log(FINE, "[ServerConnector] Waiting for connection");
                ServerHandler serverHandler = new ServerHandler(serverSocket.accept(), operator);
                Thread serverHandlerThread = new Thread(serverHandler);
                serverHandlerThread.start();
            } catch (SocketTimeoutException ex) {
                AsteroidsOperator.logger.log(FINE, "[ServerConnector] Socket timeout");
            } catch (IOException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ServerConnector] Failed to connect with Server");
            }
        }
    }

    public void stopRunning() {
        AsteroidsOperator.logger.log(FINE, "[ServerConnector] Stop running");
        running = false;
    }
    
    public void disconnect() {
        AsteroidsOperator.logger.log(FINE, "[ServerConnector] Close");
        try {
            serverSocket.close();
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerConnector] Failed to close ServerSocket");
        }
    }
    
    public Address getAddress() {
        return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }
    
}
