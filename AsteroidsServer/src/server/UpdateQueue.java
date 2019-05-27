/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import asteroidsserver.AsteroidsServer;
import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import model.AsteroidsModel;
import model.GameObject;
import model.updates.Update;
import server.network.packets.UpdatePacket;

/**
 *
 * @author Tom
 */
public class UpdateQueue extends Observable {

    AsteroidsModel model;
    ClientHandler clientHandler;
    private LinkedList<LinkedList<Update>> queue;
    private int currentSlotNumber;
    private int slotSize;
    private int length;

    private int amountProcessed;

    public UpdateQueue(ClientHandler clientHandler, AsteroidsModel model) {
        AsteroidsServer.logger.log(FINE, "Create UpdateQueue");
        this.clientHandler = clientHandler;
        this.model = model;

        this.length = 100;
        this.slotSize = 30;
        this.currentSlotNumber = 0;
        this.amountProcessed = 0;

        queue = new LinkedList<>();
        for (int i = 0; i < length; i++) {
            queue.add(new LinkedList<>());
        }
    }

    public synchronized UpdatePacket pop() {
        AsteroidsServer.logger.log(FINE, "Pop UpdateQueue");
        LinkedList<Update> updates = new LinkedList<>();

        try {
            Iterator<Update> it = queue.get(currentSlotNumber).iterator();
            while (it.hasNext()) {
                Update update = it.next();
                update.reset();
                updates.add(update);
                if (update.repeat()) {
                    this.repeat(update);
                }
                it.remove();
                //amountProcessed++;
            }
        } catch (Exception ex) {
            AsteroidsServer.logger.log(SEVERE, "Failed to pop UpdateQueue {0}", ex.getMessage());
        }

        currentSlotNumber = (currentSlotNumber + 1) % length;
        if (currentSlotNumber >= length || currentSlotNumber < 0) {
            AsteroidsServer.logger.log(SEVERE, "Failed UpdateQueue currentSlotNumber {0}", currentSlotNumber);
        }

        amountProcessed++;
        
        if (updates.isEmpty()) {
            return null;
        } else {
            return new UpdatePacket(updates);
        }
    }

    public synchronized void add(Update update) {
        AsteroidsServer.logger.log(FINE, "Add to UpdateQueue");
        if (clientHandler.getSpaceship() == null || update.getGameObject() == null) {
            return;
        }
        if (update.getGameObject().deleteMe()) {
            try {
                queue.get(currentSlotNumber).add(update);
            } catch (Exception ex) {
                AsteroidsServer.logger.log(SEVERE, "Failed to get UpdateQueue {0}", ex.getMessage());
            }
        } else {
            int minimalStepsTillInRange = clientHandler.getSpaceship().getMinimalStepsTillInRange(update.getGameObject());
            int slotNumber = min(minimalStepsTillInRange + 1, length - 1);
            try {
                queue.get((slotNumber + currentSlotNumber) % length).add(update);
            } catch (Exception ex) {
                AsteroidsServer.logger.log(SEVERE, "Failed to get UpdateQueue {0}", ex.getMessage());
            }
        }
    }

    public synchronized void repeat(Update update) {
        AsteroidsServer.logger.log(FINE, "Repeat in UpdateQueue");
        if (clientHandler.getSpaceship() == null || update.getGameObject() == null) {
            return;
        }
        int minimalStepsTillInRange = clientHandler.getSpaceship().getMinimalStepsTillInRange(update.getGameObject());
        int slotNumber = min(minimalStepsTillInRange + 1, length - 1);
        slotNumber = max(slotNumber, update.getUpdateSpeed());
        try {
            queue.get((slotNumber + currentSlotNumber) % length).add(update);
        } catch (Exception ex) {
            AsteroidsServer.logger.log(SEVERE, "Failed to get UpdateQueue {0}", ex.getMessage());
        }
    }

    public synchronized void remove(int id) {
        AsteroidsServer.logger.log(FINE, "Remove from UpdateQueue");
        for (int i = 0; i < length; i++) {
            try {
                Iterator<Update> it = queue.get(i).iterator();
                while (it.hasNext()) {
                    if (it.next().getObjectId() == id) {
                        it.remove();
                    }
                }
            } catch (Exception ex) {
                AsteroidsServer.logger.log(SEVERE, "Failed to remove from UpdateQueue {0}", ex.getMessage());
            }
        }
    }

    public synchronized void initialize() {
        AsteroidsServer.logger.log(FINE, "Initialize UpdateQueue");
        Iterator<GameObject> it = model.getGameObjects().iterator();
        while (it.hasNext()) {
            this.add(it.next().getUpdate());
        }
    }

    public synchronized void empty() {
        AsteroidsServer.logger.log(FINE, "Empty UpdateQueue");
        for (int i = 0; i < length; i++) {
            queue.get(i).clear();
        }
    }

    public synchronized void reset() {
        AsteroidsServer.logger.log(FINE, "Reset UpdateQueue");
        empty();
        initialize();
    }

    public int size() {
        int size = 0;
        for (int i = 0; i < length; i++) {
            size += queue.get(i).size();
        }
        return size;
    }

    public double getAmountProcessed() {
        double tmp = amountProcessed;
        amountProcessed = 0;
        return tmp;
    }
}
