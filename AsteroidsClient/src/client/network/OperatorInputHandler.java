/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network;

import client.Client;
import static client.ClientState.CLOSE;
import server.network.basic.InputHandler;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import server.network.packets.LogoutPacket;
import server.network.packets.Packet;
import server.network.packets.ServerPacket;

/**
 *
 * @author tomei
 */
public class OperatorInputHandler extends Thread {

    private Client client;
    private InputHandler input;

    private volatile boolean running;

    public OperatorInputHandler(Socket socket, Client client) {
        client.logger.log(INFO, "[OperatorInputHandler] Create");
        this.client = client;
        try {
            this.input = new InputHandler(socket);
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[OperatorInputHandler] Failed to set up input stream {0}", ex.getMessage());
        }
        this.running = false;
    }

    public Packet receive() {
        client.logger.log(INFO, "[OperatorInputHandler] Receive");
        Packet packet = null;
        try {
            packet = input.receive();
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[OperatorInputHandler] IOException on receive {0}", ex.getMessage());
        } catch (ClassNotFoundException ex) {
            client.logger.log(SEVERE, "[OperatorInputHandler] ClassNotFoundException on receive {0}", ex.getMessage());
        } catch (ClassCastException ex) {
            client.logger.log(SEVERE, "[OperatorInputHandler] ClassCastException on receive {0}", ex.getMessage());
        }
        return packet;
    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            Packet packet = receive();
            if (packet instanceof ServerPacket) {
                if (client.getClientState() != CLOSE) {
                    client.logoutServer();
                    client.loginServer((ServerPacket) packet);
                }
            }
            if (packet instanceof LogoutPacket) {
                client.logger.log(INFO, "[OperatorInputHandler] Received logout");
                //client.logoutServer();
                client.getOperatorConnector().disconnect();
            }
        }
    }

    public void stopRunning() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

}
