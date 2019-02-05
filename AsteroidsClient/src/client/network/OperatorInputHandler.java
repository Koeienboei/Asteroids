/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network;

import client.Client;
import client.network.basic.InputHandler;
import java.net.Socket;
import static java.util.logging.Level.INFO;
import server.network.packets.ServerPacket;

/**
 *
 * @author tomei
 */
public class OperatorInputHandler {

    private Client client;
    private InputHandler input;

    public OperatorInputHandler(Socket socket, Client client) {
        client.logger.log(INFO, "[OperatorInputHandler] Create");
        this.client = client;
        this.input = new InputHandler(socket, client);
    }

    public ServerPacket getServer() {
        client.logger.log(INFO, "[OperatorInputHandler] Get server");
        Object packet = null;
        do {
            packet = input.receive();
        } while (!(packet instanceof ServerPacket));
        return (ServerPacket) packet;
    }

}
