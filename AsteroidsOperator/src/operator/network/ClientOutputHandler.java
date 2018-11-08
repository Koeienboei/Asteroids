/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator.network;

import asteroidsoperator.AsteroidsOperator;
import operator.ClientHandler;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import server.network.basic.OutputHandler;
import server.network.packets.ServerPacket;

/**
 *
 * @author Tom
 */
public class ClientOutputHandler {
    
    private ClientHandler clientData;
    private OutputHandler output;
    
    public ClientOutputHandler(ClientHandler clientData) {
        AsteroidsOperator.logger.log(FINE, "[ClientOutputHandler] Create");
        this.clientData = clientData;
        this.output = new OutputHandler(clientData.getSocket());
    }

    public void sendServerPacket() {
        if (clientData.getServerHandler() != null) {
            output.send(new ServerPacket(clientData.getServerHandler().getAddressForClient(), clientData.getServerHandler().getHeight(), clientData.getServerHandler().getWidth()));
        } else {
            AsteroidsOperator.logger.log(SEVERE, "Failed sending ServerPacket to client, no server data");
        }
    }    
    
}
