package client.network;

import asteroidsclient.AsteroidsClient;
import client.Client;
import static client.ClientState.CLOSE;
import static client.ClientState.INITIALIZE;
import static client.ClientState.LOGOUT;
import client.network.basic.InputHandler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import server.network.basic.Address;
import server.network.packets.InitPacket;
import server.network.packets.LogoutPacket;
import server.network.packets.Packet;
import server.network.packets.UpdatePacket;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ServerInputHandler extends Thread {

    private Client client;
    private InputHandler input;
    private volatile boolean running;

    public ServerInputHandler(Client client, Socket socket) {
        client.logger.log(FINE, "[ServerInputHandler] Create");
        this.client = client;
        this.input = new InputHandler(socket, client);
        this.running = false;
    }
    
    @Override
    public void run() {
        client.logger.log(FINE, "[ServerInputHandler] Start");
        running = true;
        while (running) {
            Object packet = input.receive();
            if (packet instanceof InitPacket) {
                client.initialize((InitPacket) packet);
            } else if (packet instanceof UpdatePacket) {
                client.update((UpdatePacket) packet);
            } else if (packet instanceof LogoutPacket) {
                client.close();
            }
        }
    }
    
    public void stopRunning() {
        running = false;
    }
    
}
