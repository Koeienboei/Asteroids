/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.Serializable;
import packeges.Address;

/**
 *
 * @author tomei
 */
public class ClientStateChange implements Serializable {
    
    private Address clientAddress;
    private ClientState clientState;
    
    public ClientStateChange(ClientData clientData) {
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
