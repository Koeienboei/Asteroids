/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import static java.lang.Math.max;
import java.util.Iterator;
import java.util.Observable;
import model.Asteroid;
import model.AsteroidsModel;
import model.Bullet;
import model.GameObject;
import model.Spaceship;
import model.updates.ControllerUpdate;
import model.updates.SpaceshipUpdate;
import model.updates.Update;
import static server.ClientState.ALIVE;
import static server.ClientState.DEAD;

/**
 *
 * @author tomei
 */
public class Game extends Observable implements Runnable {

    private Server server;

    private AsteroidsModel model;

    private int gameObjectIdCounter;
    private int minAmountAsteroids;

    public Game(Server server, int height, int width, int minAmountAsteroids) {
        this.server = server;
        this.model = new AsteroidsModel(height, width);
        this.gameObjectIdCounter = 0;
        this.minAmountAsteroids = minAmountAsteroids;
    }

    @Override
    public void run() {
        System.out.println("Starting game");
        long time;

        while (server.isRunning()) {
            time = System.currentTimeMillis();
            nextStep();
            try {
                System.out.println("Time between nextStep() " + max(0, 40 - (System.currentTimeMillis() - time)));
                Thread.sleep(max(0, 40 - (System.currentTimeMillis() - time)));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            setChanged();
            notifyObservers();
        }
    }

    private void nextStep() {
        this.spawnNewAsteroids();
        this.giveNewObjectsIds();
        this.updateClientQueues();
        model.addNewObjects();
        model.removeOldObjects();
        model.update();
        model.calculateCollisions();
        model.calculateFireSpaceships();
        model.increaseBulletLifeTime();
        model.calculateResetSpaceships();
        model.nextStep();
    }

    private void spawnNewAsteroids() {
        int spawnAmount = minAmountAsteroids - model.getAsteroids().size();
        while (spawnAmount > 0) {
            model.spawnAsteroid();
            spawnAmount--;
        }
    }

    private void giveNewObjectsIds() {
        Iterator<GameObject> it = model.getAddQueue().iterator();
        while (it.hasNext()) {
            GameObject gameObject = it.next();
            if (gameObject.getId() == 0) {
                gameObject.setId(gameObjectIdCounter++);
            }
        }
    }

    public void updateClientQueues() {
        Iterator<GameObject> ito = model.getAddQueue().iterator();
        while (ito.hasNext()) {
            GameObject gameObject = ito.next();
            if (!(gameObject instanceof Spaceship)) {
                updateClientQueues(gameObject.getUpdate());
            }
        }
        Iterator<Asteroid> ita = model.getAsteroids().iterator();
        while (ita.hasNext()) {
            Asteroid asteroid = ita.next();
            if (asteroid.deleteMe()) {
                updateClientQueues(asteroid.getUpdate());
            }
        }
        Iterator<Bullet> itb = model.getBullets().iterator();
        while (itb.hasNext()) {
            Bullet bullet = itb.next();
            if (bullet.deleteMe()) {
                updateClientQueues(bullet.getUpdate());
            }
        }
        Iterator<Spaceship> its = model.getSpaceships().iterator();
        while (its.hasNext()) {
            Spaceship spaceship = its.next();
            if (spaceship.deleteMe()) {
                updateClientQueues(spaceship.getUpdate());
            }
        }
    }

    public void updateClientQueues(Update update) {
        Iterator<ClientData> itc = server.getClients().iterator();
        while (itc.hasNext()) {
            ClientData clientData = itc.next();
            if (clientData.getState() == ALIVE || clientData.getState() == DEAD) {
                if (update instanceof ControllerUpdate) {
                    ControllerUpdate controllerUpdate = (ControllerUpdate) update;
                    if (controllerUpdate.getObjectId() != clientData.getSpaceship().getId()) {
                        clientData.getOutput().getUpdateQueue().add(update);
                    }
                } else {
                    clientData.getOutput().getUpdateQueue().remove(update.getObjectId());
                    clientData.getOutput().getUpdateQueue().add(update);
                }
            }
        }
    }

    public void spawnNewSpacehipForClient(ClientData clientData) {
        Spaceship spaceship = new Spaceship(model, clientData);
        spaceship.setId(gameObjectIdCounter++);
        clientData.setSpaceship(spaceship);
        model.addGameObject(spaceship);
        spaceship.spawn();
        spaceship.setAlive();
    }

    public AsteroidsModel getModel() {
        return model;
    }

}
