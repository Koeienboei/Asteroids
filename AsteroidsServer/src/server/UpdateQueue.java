/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.AsteroidsModel;
import model.GameObject;
import model.updates.Update;
import packeges.UpdatePacket;

/**
 *
 * @author Tom
 */
public class UpdateQueue {
    
    AsteroidsModel model;
    ClientData clientData;
    private List<LinkedList<Update>> queue;
    private int currentSlotNumber;
    private int slotSize;
    private int length;
    
    public UpdateQueue(ClientData clientData, AsteroidsModel model) {
        this.clientData = clientData;
        this.model = model;
        
        this.length = 500;
        this.slotSize = 100;
        this.currentSlotNumber = 0;
        
        queue = new LinkedList<LinkedList<Update>>();
        for (int i=0; i<length; i++) {
            queue.add(new LinkedList<Update>());
        }
    }

    public synchronized UpdatePacket pop() {
        LinkedList<Update> updates = new LinkedList<>();
        
        Iterator<Update> it = queue.get(currentSlotNumber).iterator();
        while (it.hasNext()) {
            Update update = it.next();
            update.reset();
            updates.add(update);
            if (update.repeat()) {
                this.repeat(update);
            }
            it.remove();
        }
        
        currentSlotNumber = (currentSlotNumber+1) % length;
        if (updates.isEmpty()) {
            return null;
        } else {
            return new UpdatePacket(updates);
        }
    }
    
    public synchronized void add(Update update) {
        if (clientData.getSpaceship() == null || update.getGameObject() == null) {
            return;
        }
        int minimalStepsTillInRange = clientData.getSpaceship().getMinimalStepsTillInRange(update.getGameObject());
        int slotNumber = min(minimalStepsTillInRange + 1, length-1);
        queue.get((slotNumber+currentSlotNumber) % length).add(update);
    }
    
    public synchronized void repeat(Update update) {
        int minimalStepsTillInRange = clientData.getSpaceship().getMinimalStepsTillInRange(update.getGameObject());
        int slotNumber = min(minimalStepsTillInRange + 1, length-1);
        slotNumber = max(slotNumber, update.getUpdateSpeed());
        queue.get((slotNumber+currentSlotNumber) % length).add(update);
    }
    
    public synchronized void remove(int id) {
        for (int i=0; i<length; i++) {
            Iterator<Update> it = queue.get(i).iterator();
            while (it.hasNext()) {
                if (it.next().getObjectId() == id) {
                    it.remove();
                }
            }
        }
    }
    
    public synchronized void initialize() {
        Iterator<GameObject> it = model.getGameObjects().iterator();
        while (it.hasNext()) {
            this.add(it.next().getUpdate());
        }
    }
    
    public synchronized void empty() {
        for (int i=0; i<length; i++) {
            queue.get(i).clear();
        }
    }
    
    public synchronized void reset() {
        empty();
        initialize();
    }
    
}
