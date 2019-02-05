/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network;

import client.Client;
import client.network.basic.OutputHandler;
import java.net.Socket;
import static java.util.logging.Level.INFO;
import server.network.basic.Address;
import server.network.packets.ClientPacket;

/**
 *
 * @author tomei
 */
public class OperatorOutputHandler {
    
    private Client client;
    private OutputHandler output;
    
    public OperatorOutputHandler(Socket socket, Client client) {
        client.logger.log(INFO, "[OperatorOutputHandler] Create");
        this.output = new OutputHandler(socket, client);
        this.client = client;
    }
    
    public void sendClientPacket(Socket socket) {
        ClientPacket clientPacket = new ClientPacket(new Address(socket.getLocalAddress().getHostAddress(), socket.getLocalPort()));
        client.logger.log(INFO, "[OperatorOutputHandler] Send {0}", clientPacket);
        output.send(clientPacket);
    }    
    
}
