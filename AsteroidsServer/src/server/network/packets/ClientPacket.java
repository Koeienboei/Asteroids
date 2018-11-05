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

/**
 * This class represents a Packet for Login in
 * @author Tom
 */
public class ClientPacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 2L;
    
    private Address clientAddress;
    
    public ClientPacket(Address clientAddress) {
        AsteroidsServer.logger.log(FINE, "Create ClientPacket: {0}", clientAddress);
        this.clientAddress = clientAddress;
    }

    public Address getClientAddress() {
        return clientAddress;
    }

    @Override
    public String toString() {
        return "ClientPacket(" + clientAddress + ")";
    }
    
}
