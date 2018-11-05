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
import model.updates.Update;

/**
 *
 * @author Tom
 */
public class UpdatePacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 6L;
    
    private LinkedList<Update> updates;
    
    public UpdatePacket(LinkedList<Update> updates) {
        AsteroidsServer.logger.log(FINE, "Create Update(s)Packet");
        this.updates = updates;
    }
    
    public UpdatePacket(Update update) {
        AsteroidsServer.logger.log(FINE, "Create UpdatePacket");
        updates = new LinkedList<Update>();
        updates.add(update);
    }

    public LinkedList<Update> getUpdates() {
        return updates;
    }
    
    public Update getUpdate() {
        return updates.getFirst();
    }
    
    @Override
    public String toString() {
        return "UpdatePacket(" + updates.size() + ")";
    }
    
}
