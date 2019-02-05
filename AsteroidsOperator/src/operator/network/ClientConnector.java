package operator.network;

import asteroidsoperator.AsteroidsOperator;
import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import server.network.basic.Address;
import operator.ClientHandler;
import operator.Operator;
import server.network.packets.ServerPacket;
import static server.ClientState.LOGIN;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ClientConnector extends Thread {

    private final Operator operator;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public ClientConnector(Operator operator) {
        AsteroidsOperator.logger.log(FINE, "[ClientConnector] Create");
        this.operator = operator;
        try {
            serverSocket = new ServerSocket(8850, 1000, InetAddress.getLocalHost());
            serverSocket.setSoTimeout(40);
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ClientConnector] Failed to create ServerSocket");
        }
        this.running = false;
    }
        
    @Override
    public void run() {
        AsteroidsOperator.logger.log(FINE, "[ClientConnector] Start with ServerSocket at {0}", getAddress());
        running = true;
        while (running) {
            try {
                AsteroidsOperator.logger.log(FINE, "[ClientConnector] Waiting for connection");
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), operator);
                Thread clientHandlerThread = new Thread(clientHandler);
                clientHandlerThread.start();
            } catch (SocketTimeoutException ex) {
                AsteroidsOperator.logger.log(FINE, "[ClientConnector] Socket timeout");
            } catch (IOException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ClientConnector] Failed to set up connection");
            }
        }
    }

    public void stopRunning() {
        AsteroidsOperator.logger.log(FINE, "[ClientConnector] Stop running");
        running = false;
    }
    
    public void disconnect() {
        AsteroidsOperator.logger.log(FINE, "[ClientConnector] Close");
        try {
            serverSocket.close();
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ClientConnector] Failed to close ServerSocket");
        }
    }
    
    public Address getAddress() {
        return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }
    
}
