package operator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import packeges.Address;
import packeges.ServerPacket;
import static server.ClientState.LOGIN;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ClientHandler extends Thread {

    private final Operator operator;
    private ServerSocket serverSocket;

    public ClientHandler(Operator operator) {
        this.operator = operator;
        System.out.println("ClientHandler");
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 0));
        } catch (IOException ex) {
            System.err.println("Failed te create server socket");
        }
        System.out.println("ClientHandler " + getAddress());
    }
    
    private void send(Object packet, ObjectOutputStream output) {
        try {
            output.writeObject(packet);
        } catch (IOException ex) {
            System.err.println("Failed to send packet to client");
        }
    }
        
    @Override
    public void run() {
        while (operator.isRunning()) {
            System.out.println("Waiting for new client connection");
            try {
                ClientData clientData = new ClientData(serverSocket.accept());
                operator.addClient(clientData);
                ServerData serverData = operator.getServer();
                System.out.println("Chosen server for client: " + serverData);
                clientData.setServerData(serverData);
                clientData.setState(LOGIN);
                send(new ServerPacket(serverData.getAddressForClient(), serverData.getHeight(), serverData.getWidth()), clientData.getOutput());
                System.out.println("Send server packet to client");
            } catch (IOException ex) {
                System.err.println("Failed to connect with client");
            }
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Failed to close client handler");
        }
    }
    
    public Address getAddress() {
        return new Address(serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
    }
    
}
