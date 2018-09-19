/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packeges;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
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
        this.updates = updates;
        spaceshipUpdate = null;
    }
    
    public InitPacket(SpaceshipUpdate spaceshipUpdate) {
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
    
    public LinkedList<Integer> getObjectIds() {
        LinkedList<Integer> objectIds = new LinkedList<>();
        if (!isLast()) {
            Iterator<Update> it = updates.iterator();
            while (it.hasNext()) {
                objectIds.add(it.next().getObjectId());
            }
        } else {
            objectIds.add(spaceshipUpdate.getObjectId());
        }
        return objectIds;
    }
}
