/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network.packets;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import static java.util.logging.Level.FINE;
import model.updates.SpaceshipUpdate;
import model.updates.Update;

/**
 *
 * @author Tom
 */
public class InitPacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 4L;

    private LinkedList<Update> updates;
    private SpaceshipUpdate spaceshipUpdate;
    
    public InitPacket(LinkedList<Update> updates) {
        AsteroidsServer.logger.log(FINE, "Create InitPacket");
        this.updates = updates;
        spaceshipUpdate = null;
    }
    
    public InitPacket(SpaceshipUpdate spaceshipUpdate) {
        AsteroidsServer.logger.log(FINE, "Create last InitPacket");
        this.updates = null;
        this.spaceshipUpdate = spaceshipUpdate;
    }
    
    public boolean isLast() {
        return spaceshipUpdate != null;
    }
    
    public LinkedList<Update> getUpdates() {
        return updates;
    }
    
    public SpaceshipUpdate getSpaceshipUpdate() {
        return spaceshipUpdate;
    }
    
    @Override
    public String toString() {
        return !isLast() ? ("InitPacket(" + updates.size() + ")") : ("LastInitPacket(spaceshipUpdate)");
    }
    
}
