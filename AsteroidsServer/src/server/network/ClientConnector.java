package server.network;

import server.network.basic.Address;
import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import server.ClientHandler;
 import server.Server;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ClientConnector extends Thread {

    private final Server server;
    private ServerSocket serverSocket;
    
    private volatile boolean running;

    public ClientConnector(Server server) {
        AsteroidsServer.logger.log(INFO, "[ClientConnector] Create");
        this.server = server;
        
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 0));
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[ClientConnector] Failed to create ServerSocket");
        }
        
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsServer.logger.log(INFO, "[ClientConnector] Start");
        this.running = true;
        while (running) {
            try {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept(), server);
                Thread clientHandlerThread = new Thread(clientHandler);
                clientHandlerThread.start();
            } catch (IOException ex) {
                AsteroidsServer.logger.log(SEVERE, "[ClientConnector] Failed to connect to Client");
            }
        }
    }

    public void stopRunning() {
        AsteroidsServer.logger.log(INFO, "[ClientConnector] Stop running");
        running = false;
    }
    
    public void disconnect() {
        AsteroidsServer.logger.log(INFO, "[ClientConnector] Close");
        try {
            serverSocket.close();
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[ClientConnector] Failed to close ServerSocket");
        }
    }
    
    public Address getAddress() {
        return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }
    
}
