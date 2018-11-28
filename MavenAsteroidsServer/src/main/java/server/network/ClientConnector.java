package server.network;

import server.network.basic.Address;
import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
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
            serverSocket = new ServerSocket(8900, 100, InetAddress.getLocalHost());
            serverSocket.setSoTimeout(40);
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[ClientConnector] Failed to create ServerSocket");
        }
        
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsServer.logger.log(INFO, "[ClientConnector] Start with ServerSocket at {0}", getAddress());
        this.running = true;
        while (running) {
            try {
                AsteroidsServer.logger.log(INFO, "[ClientConnector] Waiting for incomming connection");
                Socket socket = serverSocket.accept();
                AsteroidsServer.logger.log(INFO, "[ClientConnector] Accepted s{0} to c{1}", new Object[]{new Address(socket.getLocalAddress().getHostAddress(), socket.getLocalPort()), new Address(socket.getInetAddress().getHostAddress(), socket.getPort())});
                System.out.println("Accepted client connection with address " + new Address(socket.getInetAddress().getHostAddress(), socket.getPort()));
                ClientHandler clientHandler = new ClientHandler(socket, server);
                Thread clientHandlerThread = new Thread(clientHandler);
                clientHandlerThread.start();
            } catch (SocketTimeoutException ex) {
                AsteroidsServer.logger.log(FINE, "[ClientConnector] Socket timeout");
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
