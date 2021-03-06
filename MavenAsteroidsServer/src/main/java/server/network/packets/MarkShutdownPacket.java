/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server.network.packets;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import static java.util.logging.Level.FINE;

/**
 * This class represents a Packet for Login in
 * @author Tom
 */
public class MarkShutdownPacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 1034L;
    
    public MarkShutdownPacket() {
        AsteroidsServer.logger.log(FINE, "[MarkShutdownPacket] Create");
    }
    
    @Override
    public String toString() {
        return "MarkShutdownPacket()";
    }
   
}
