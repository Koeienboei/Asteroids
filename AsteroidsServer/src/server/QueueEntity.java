/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import model.updates.Update;
import packeges.Packet;

/**
 *
 * @author tomei
 */
public class QueueEntity {
    
    private Packet packet;
    private ClientData clientData;
    
    public QueueEntity(Packet packet, ClientData clientData) {
        this.packet = packet;
        this.clientData = clientData;
    }

    public Packet getPacket() {
        return packet;
    }

    public ClientData getClientData() {
        return clientData;
    }
    
}
