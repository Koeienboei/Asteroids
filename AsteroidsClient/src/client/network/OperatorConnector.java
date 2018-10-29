package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import packeges.Address;
import packeges.ServerPacket;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class OperatorHandler extends Thread {

    private Client client;
    private Address operatorAddress;
    private Socket socket;
    private ObjectInputStream input;

    public OperatorHandler(Client client, Address operatorAddress) {
        this.client = client;
        this.operatorAddress = operatorAddress;
    }
    
    public void connect() {
        try {
            System.out.println("Connecting to operator");
            socket = new Socket(operatorAddress.getIp(), operatorAddress.getPort());
            System.out.println("Connected to operator");
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Got inputstream to operator");
        } catch (IOException ex) {
            System.err.println("Failed to connect to operator");
        }
    }

    private Object receive() {
        try {
            return input.readObject();
        } catch (Exception ex) {
            System.err.println("Failed to receive packet from operator");
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Receiving packet");
            Object packet = receive();
            if (packet instanceof ServerPacket) {
                client.initialize((ServerPacket) packet);
            }
        }
    }

    public void close() {
        try {
            input.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Failed to close connection to operator");
        }
    }
    
}
