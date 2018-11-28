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
import server.Server;

/**
 * This class represents a Packet for Login in
 * @author Tom
 */
public class ServerPacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 2L;
    
    private Address serverAddress;
    private int height, width;
    
    public ServerPacket(Address serverAddress, int height, int width) {
        AsteroidsServer.logger.log(FINE, "Create ServerPacket");
        this.serverAddress = serverAddress;
        this.height = height;
        this.width = width;
    }

    public Address getServerAddress() {
        return serverAddress;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return "ServerPacket(" + serverAddress + ", " + height + ", " + width + ")";
    }
    
}
