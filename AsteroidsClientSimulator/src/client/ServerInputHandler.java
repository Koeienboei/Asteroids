package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import packeges.Address;
import packeges.InitPacket;
import packeges.UpdatePacket;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ServerInputHandler extends Thread {

    private Client client;
    private ObjectInputStream input;

    public ServerInputHandler(Client client, Socket socket) {
        this.client = client;
        try {
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Failed to open socket input stream of server");
        }
    }

    private Object receive() {
        try {
            Object packet = input.readObject();
            return packet;
        } catch (Exception ex) {
            System.err.println("Failed to receive packet from server");
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            Object packet = receive();
            if (packet instanceof InitPacket) {
                client.initialize((InitPacket) packet);
            } else if (packet instanceof UpdatePacket) {
                client.update((UpdatePacket) packet);
            }
        }
    }

    public void close() {
        try {
            input.close();
        } catch (IOException ex) {
            System.err.println("Failed to close input stream of server");
        }
    }
    
}
