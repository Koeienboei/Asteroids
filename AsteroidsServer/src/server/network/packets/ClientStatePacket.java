/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package packeges;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import server.ClientData;
import server.ClientState;
import server.ClientStateChange;

/**
 * This class represents a Packet for Login in
 * @author Tom
 */
public class ClientStatePacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 7L;
    
    private Address clientAddress;
    private ClientState clientState;
    
    public ClientStatePacket(ClientData clientData) {
        this.clientAddress = clientData.getAddress();
        this.clientState = clientData.getState();
    }

    public Address getClientAddress() {
        return clientAddress;
    }

    public ClientState getClientState() {
        return clientState;
    }

}
