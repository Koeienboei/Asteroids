/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import asteroidsserver.AsteroidsServer;
import static java.lang.Math.max;
import java.util.Iterator;
import java.util.Observable;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
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
    
    private long calculationTime;
    
    private volatile boolean running;

    public Game(Server server, int height, int width, int minAmountAsteroids) {
        AsteroidsServer.logger.log(FINE, "[Game] Create");
        this.server = server;
        this.model = new AsteroidsModel(height, width);
        this.gameObjectIdCounter = 0;
        this.minAmountAsteroids = minAmountAsteroids;
        this.calculationTime = 0;
        this.running = false;
    }

    @Override
    public void run() {
        AsteroidsServer.logger.log(FINE, "[Game] Start");
        running = true;
        long time;

        while (running) {
            time = System.currentTimeMillis();
            nextStep();
            try {
                calculationTime = System.currentTimeMillis() - time;
                setChanged();
                notifyObservers();
                Thread.sleep(max(0, 40 - calculationTime));
            } catch (InterruptedException ex) {
                AsteroidsServer.logger.log(SEVERE, "Failed to wait after next step in Game");
            }
            setChanged();
            notifyObservers();
        }
    }

    private void nextStep() {
        AsteroidsServer.logger.log(FINE, "[Game] Next step in Game");
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
        AsteroidsServer.logger.log(FINE, "Spawn new Asteroids");
        int spawnAmount = minAmountAsteroids - model.getAsteroids().size();
        while (spawnAmount > 0) {
            model.spawnAsteroid();
            spawnAmount--;
        }
    }

    private void giveNewObjectsIds() {
        AsteroidsServer.logger.log(FINE, "Give new objects ids");
        Iterator<GameObject> it = model.getAddQueue().iterator();
        while (it.hasNext()) {
            GameObject gameObject = it.next();
            if (gameObject.getId() == 0) {
                gameObject.setId(gameObjectIdCounter++);
            }
        }
    }

    public void updateClientQueues() {
        AsteroidsServer.logger.log(FINE, "[Game] Update ClientQueues ClientOutputHandler");
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
        AsteroidsServer.logger.log(FINE, "[Game] Update ClientQueues with one Update #c{0}", server.getClients().size());
        Iterator<ClientHandler> itc = server.getClients().iterator();
        while (itc.hasNext()) {
            ClientHandler clientHandler = itc.next();
            if (clientHandler.getClientState() == ALIVE || clientHandler.getClientState() == DEAD) {
                if (update instanceof ControllerUpdate) {
                    ControllerUpdate controllerUpdate = (ControllerUpdate) update;
                    if (controllerUpdate.getObjectId() != clientHandler.getSpaceship().getId()) {
                        clientHandler.getUpdateQueue().add(update);
                    }
                } else {
                    clientHandler.getUpdateQueue().remove(update.getObjectId());
                    clientHandler.getUpdateQueue().add(update);
                }
            }
        }
    }

    public void spawnNewSpacehipForClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "Spawn new Spaceship for Client");
        Spaceship spaceship = new Spaceship(model, clientHandler);
        spaceship.setId(gameObjectIdCounter++);
        clientHandler.setSpaceship(spaceship);
        model.addGameObject(spaceship);
        spaceship.spawn();
        spaceship.setAlive();
    }

    public void stopRunning() {
        AsteroidsServer.logger.log(FINE, "[Game] Stop running");
        running = false;
    }
    
    public AsteroidsModel getModel() {
        return model;
    }

    public long getCalculationTime() {
        return calculationTime;
    }
    
}
