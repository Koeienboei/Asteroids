/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server.network.packets;

import server.network.basic.Address;
import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import static java.util.logging.Level.FINE;
import server.ClientHandler;
import server.ClientState;

/**
 * This class represents a Packet for Login in
 * @author Tom
 */
public class ClientStatePacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 7L;
    
    private Address clientAddress;
    private ClientState clientState;
    
    public ClientStatePacket(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "Create ClientStatePacket: {0}, {1}", new Object[]{clientHandler.getAddress(), clientHandler.getState()});
        this.clientAddress = clientHandler.getAddress();
        this.clientState = clientHandler.getState();
    }

    public Address getClientAddress() {
        return clientAddress;
    }

    public ClientState getClientState() {
        return clientState;
    }
    
    @Override
    public String toString() {
        return "ClientStatePacket(" + clientAddress + ", " + clientState + ")";
    }
    
}
