package client.network;

import client.Client;
import client.network.basic.InputHandler;
import java.net.Socket;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import server.network.packets.InitPacket;
import server.network.packets.LogoutPacket;
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
        client.logger.log(INFO, "[ServerInputHandler] Create");
        this.client = client;
        this.input = new InputHandler(socket, client);
        this.running = false;
    }
    
    @Override
    public void run() {
        client.logger.log(INFO, "[ServerInputHandler] Start");
        running = true;
        while (running) {
            Object packet = input.receive();
            if (packet instanceof InitPacket) {
                client.logger.log(FINE, "[ServerInputHandler] Received InitPacket");
                client.initialize((InitPacket) packet);
            } else if (packet instanceof UpdatePacket) {
                client.logger.log(FINE, "[ServerInputHandler] Received UpdatePacket");
                client.update((UpdatePacket) packet);
            } else if (packet instanceof LogoutPacket) {
                client.logger.log(INFO, "[ServerInputHandler] Received LogoutPacket");
                client.close();
            }
        }
    }
    
    public void stopRunning() {
        client.logger.log(INFO, "[ServerInputHandler] Stop running");
        running = false;
    }
    
}
