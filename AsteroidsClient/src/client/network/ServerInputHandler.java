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
            System.out.println("1");
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("2");
        } catch (IOException ex) {
            System.err.println("Failed to open socket input stream of server");
        }
        System.out.println("3");
    }

    private Object receive() {
        try {
            //System.out.println("Receiving packet");
            Object packet = input.readObject();
            //System.out.println("Received packet");
            return packet;
        } catch (Exception ex) {
            System.err.println("Failed to receive packet from server");
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        System.out.println("Start server input handler");
        while (true) {
            Object packet = receive();
            if (packet instanceof InitPacket) {
                System.out.println("Received init packet");
                client.initialize((InitPacket) packet);
            } else if (packet instanceof UpdatePacket) {
                //System.out.println("Received update packet");
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
