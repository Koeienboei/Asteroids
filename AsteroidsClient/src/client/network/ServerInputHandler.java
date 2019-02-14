package client.network;

import client.Client;
import server.network.basic.InputHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
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
        client.logger.log(INFO, "[ServerInputHandler] Create");
        this.client = client;
        try {
            this.input = new InputHandler(socket);
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[ServerInputHandler] Failed to set up input stream {0}", ex.getMessage());
        }
        this.running = false;
    }

    @Override
    public void run() {
        client.logger.log(INFO, "[ServerInputHandler] Start");
        running = true;
        while (running) {
            Packet packet = null;
            try {
                packet = input.receive();
            } catch (IOException ex) {
                client.logger.log(SEVERE, "[ServerInputHandler] IOException on receive from {0}, {1}", new Object[] {client.getServerConnector().getServerAddress(), ex.getMessage()});
            } catch (ClassNotFoundException ex) {
                client.logger.log(SEVERE, "[ServerInputHandler] ClassNotFoundException on receive from {0}, {1}", new Object[] {client.getServerConnector().getServerAddress(), ex.getMessage()});
            } catch (ClassCastException ex) {
                client.logger.log(SEVERE, "[ServerInputHandler] ClassCastException on receive from {0}, {1}", new Object[] {client.getServerConnector().getServerAddress(), ex.getMessage()});
            }
            if (packet instanceof InitPacket) {
                client.logger.log(FINE, "[ServerInputHandler] Received InitPacket");
                client.initialize((InitPacket) packet);
            } else if (packet instanceof UpdatePacket) {
                client.logger.log(FINE, "[ServerInputHandler] Received UpdatePacket");
                client.update((UpdatePacket) packet);
            } else if (packet instanceof LogoutPacket) {
                client.logger.log(INFO, "[ServerInputHandler] Received LogoutPacket");
                client.getServerConnector().disconnect();
            }
        }
    }

    public void stopRunning() {
        client.logger.log(INFO, "[ServerInputHandler] Stop running");
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

}
