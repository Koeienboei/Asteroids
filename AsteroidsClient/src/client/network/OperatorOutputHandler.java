/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network;

import client.Client;
import client.network.basic.OutputHandler;
import java.net.Socket;
import static java.util.logging.Level.FINE;
import server.network.packets.Packet;

/**
 *
 * @author tomei
 */
public class OperatorOutputHandler {
    
    private OutputHandler output;
    
    public OperatorOutputHandler(Socket socket, Client client) {
        client.logger.log(FINE, "[OperatorOutputHandler] Create");
        this.output = new OutputHandler(socket, client);
    }
    
    public void send(Packet packet) {
        output.send(packet);
    }    
    
}
