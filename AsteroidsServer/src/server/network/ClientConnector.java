package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import packeges.Address;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class InputHandler extends Thread {

    private final Server server;
    private ServerSocket serverSocket;

    public InputHandler(Server server) {
        this.server = server;
        
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 0));
        } catch (IOException ex) {
            System.out.println("[ERROR] Failed to create server socket");
        }
    }
    
    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                Socket socket = serverSocket.accept();
                server.addClient(socket);
            } catch (IOException ex) {
                System.out.println("[ERROR] Failed to connect with client");
            }
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.out.println("[ERROR] Failed to close input handler");
        }
    }
    
    public Address getAddress() {
        return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }
    
}
