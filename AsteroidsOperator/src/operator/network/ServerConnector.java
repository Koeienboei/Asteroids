package operator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import packeges.Address;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ServerHandler extends Thread {

    private final Operator operator;
    private ServerSocket serverSocket;

    public ServerHandler(Operator operator) {
        this.operator = operator;
        
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 0));
        } catch (IOException ex) {
            System.err.println("Failed te create server socket");
        }
    }
    
    @Override
    public void run() {
        while (operator.isRunning()) {
            try {
                Socket socket = serverSocket.accept();
                operator.addServer(new ServerData(socket, operator));
            } catch (IOException ex) {
                System.err.println("Failed to connect with server");
            }
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Failed to close server handler");
        }
    }
    
    public Address getAddress() {
        return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }
    
}
