/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server.network.packets;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import static java.util.logging.Level.FINE;
import server.network.basic.Address;

/**
 * This class represents a Packet for Login in
 * @author Tom
 */
public class ShutdownSuccesPacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 101L;

    public ShutdownSuccesPacket() {
        AsteroidsServer.logger.log(FINE, "[ShutdownSuccesPacket] Create");
    }
    
    @Override
    public String toString() {
        return "ShutdownSuccesPacket()";
    }
    
}
