/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packeges;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import model.updates.Update;

/**
 *
 * @author Tom
 */
public class UpdatePacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 6L;
    
    private LinkedList<Update> updates;
    
    public UpdatePacket(LinkedList<Update> updates) {
        this.updates = updates;
    }
    
    public UpdatePacket(Update update) {
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
        String s = "Update packet:\n";
        Iterator<Update> it = updates.iterator();
        while (it.hasNext()) {
            s += it.next() + "\n";
        }
        return s;
    }
    
}
