package model;

import controller.SpaceshipController;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import model.updates.AsteroidUpdate;
import model.updates.BulletUpdate;
import model.updates.ControllerUpdate;
import model.updates.SpaceshipUpdate;
import model.updates.Update;

/**
 * This class represents the model of the MVC-pattern. An AsteroidsModel
 * consists of a spaceship, a list of asteroids and a list of bullets.
 *
 * @author Wilco Wijbrandi
 */
public class AsteroidsModel {

    private int height;
    private int width;

    private ConcurrentLinkedQueue<Bullet> bullets;
    private ConcurrentLinkedQueue<Asteroid> asteroids;
    private ConcurrentLinkedQueue<Spaceship> spaceships;

    private ConcurrentLinkedQueue<GameObject> addQueue;
    private ConcurrentLinkedQueue<Update> updateQueue;

    public AsteroidsModel(int height, int width) {
        this.height = height;
        this.width = width;

        initialize();
    }

    private void initialize() {
        bullets = new ConcurrentLinkedQueue();
        asteroids = new ConcurrentLinkedQueue();
        spaceships = new ConcurrentLinkedQueue();

        addQueue = new ConcurrentLinkedQueue();
        updateQueue = new ConcurrentLinkedQueue();
    }

    public void spawnAsteroid() {
        addGameObject(new LargeAsteroid(this));
    }

    public void nextStep() {
        Iterator<Spaceship> its = spaceships.iterator();
        while (its.hasNext()) {
            its.next().nextStep();
        }

        Iterator<Bullet> itb = bullets.iterator();
        while (itb.hasNext()) {
            itb.next().nextStep();
        }

        Iterator<Asteroid> ita = asteroids.iterator();
        while (ita.hasNext()) {
            ita.next().nextStep();
        }
    }

    public void calculateCollisions() {
        Iterator<Spaceship> its = spaceships.iterator();
        while (its.hasNext()) {
            its.next().calculateCollisions();
        }

        Iterator<Asteroid> ita = asteroids.iterator();
        while (ita.hasNext()) {
            ita.next().calculateCollisions();
        }
    }

    public void calculateFireSpaceships() {
        Iterator<Spaceship> its = spaceships.iterator();
        while (its.hasNext()) {
            its.next().calculateFire();
        }
    }

    public void calculateResetSpaceships() {
        Iterator<Spaceship> its = spaceships.iterator();
        while (its.hasNext()) {
            its.next().calculateReset();
        }
    }

    public void increaseBulletLifeTime() {
        Iterator<Bullet> itb = bullets.iterator();
        while (itb.hasNext()) {
            itb.next().increaseLifeTime();
        }
    }

    public void addNewObjects() {
        Iterator<GameObject> it = addQueue.iterator();
        while (it.hasNext()) {
            GameObject gameObject = it.next();
            if (gameObject instanceof Asteroid) {
                asteroids.add((Asteroid) gameObject);
            } else if (gameObject instanceof Bullet) {
                bullets.add((Bullet) gameObject);
            } else if (gameObject instanceof Spaceship) {
                spaceships.add((Spaceship) gameObject);
            }
            it.remove();
        }
    }

    public void addGameObject(GameObject o) {
        addQueue.add(o);
    }

    public void removeOldObjects() {
        Iterator<Spaceship> its = spaceships.iterator();
        while (its.hasNext()) {
            if (its.next().deleteMe()) {
                its.remove();
            }
        }

        Iterator<Bullet> itb = bullets.iterator();
        while (itb.hasNext()) {
            if (itb.next().deleteMe()) {
                itb.remove();
            }
        }

        Iterator<Asteroid> ita = asteroids.iterator();
        while (ita.hasNext()) {
            if (ita.next().deleteMe()) {
                ita.remove();
            }
        }
    }

    public void removeSpaceship(int id) {
        Iterator<Spaceship> it = spaceships.iterator();
        while (it.hasNext()) {
            Spaceship spaceship = it.next();
            if (spaceship.getId() == id) {
                spaceship.destroy();
            }
        }
    }

    public synchronized void addUpdate(Update update) {
        updateQueue.add(update);
    }

    public synchronized void addUpdates(LinkedList<Update> updates) {
        updateQueue.addAll(updates);
    }

    public synchronized void update() {
        Iterator<Update> it = updateQueue.iterator();
        while (it.hasNext()) {
            update(it.next());
            it.remove();
        }
    }

    public void update(Update update) {
        if (update instanceof AsteroidUpdate) {
            processAsteroidUpdate((AsteroidUpdate) update);
        }
        if (update instanceof BulletUpdate) {
            processBulletUpdate((BulletUpdate) update);
        }
        if (update instanceof SpaceshipUpdate) {
            processSpaceshipUpdate((SpaceshipUpdate) update);
        }
        if (update instanceof ControllerUpdate) {
            processControllerUpdate((ControllerUpdate) update);
        }
    }

    public void processAsteroidUpdate(AsteroidUpdate update) {
        Asteroid asteroid = getAsteroid(update.getObjectId());
        if (asteroid != null) {
            asteroid.update(update);
        } else if (!update.isDelete()) {
            switch (update.getType()) {
                case 1:
                    addGameObject(new LargeAsteroid(update, this));
                    break;
                case 2:
                    addGameObject(new MediumAsteroid(update, this));
                    break;
                case 3:
                    addGameObject(new SmallAsteroid(update, this));
                    break;
            }
        }
    }

    public void processBulletUpdate(BulletUpdate update) {
        Bullet bullet = getBullet(update.getObjectId());
        if (bullet != null) {
            bullet.update(update);
        } else if (!update.isDelete()) {
            addGameObject(new Bullet(update, this));
        }
    }

    public void processSpaceshipUpdate(SpaceshipUpdate update) {
        Spaceship spaceship = getSpaceship(update.getObjectId());
        if (spaceship != null) {
            spaceship.update(update);
        } else if (!update.isDelete()) {
            addGameObject(new Spaceship(update, this));
        }
    }

    public void processControllerUpdate(ControllerUpdate update) {
        SpaceshipController spaceshipController = getSpaceship(update.getObjectId()).getSpaceshipController();
        if (spaceshipController != null) {
            spaceshipController.update(update);
        }
    }

    public Asteroid getAsteroid(int objectId) {
        Iterator<Asteroid> it = asteroids.iterator();
        while (it.hasNext()) {
            Asteroid asteroid = it.next();
            if (asteroid.getId() == objectId) {
                return asteroid;
            }
        }
        return null;
    }

    public Bullet getBullet(int objectId) {
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            if (bullet.getId() == objectId) {
                return bullet;
            }
        }
        return null;
    }

    public Spaceship getSpaceship(int objectId) {
        Iterator<Spaceship> it = spaceships.iterator();
        while (it.hasNext()) {
            Spaceship spaceship = it.next();
            if (spaceship.getId() == objectId) {
                return spaceship;
            }
        }
        return null;
    }

    public Collection<Bullet> getBullets() {
        return bullets;
    }

    public Collection<Asteroid> getAsteroids() {
        return asteroids;
    }

    public Collection<Spaceship> getSpaceships() {
        return spaceships;
    }

    public LinkedList<GameObject> getGameObjects() {
        LinkedList<GameObject> gameObjects = new LinkedList<GameObject>();

        Iterator<Asteroid> ita = asteroids.iterator();
        while (ita.hasNext()) {
            gameObjects.add(ita.next());
        }
        Iterator<Bullet> itb = bullets.iterator();
        while (itb.hasNext()) {
            gameObjects.add(itb.next());
        }
        Iterator<Spaceship> its = spaceships.iterator();
        while (its.hasNext()) {
            gameObjects.add(its.next());
        }
        Iterator<GameObject> itq = addQueue.iterator();
        while (itq.hasNext()) {
            gameObjects.add(itq.next());
        }

        return gameObjects;
    }

    public Collection<GameObject> getAddQueue() {
        return addQueue;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        String string = "Model(" + width + "," + height + "):\n";
        string += "#asteroids: " + asteroids.size() + "\n";
        Iterator<Asteroid> ita = asteroids.iterator();
        while (ita.hasNext()) {
            Asteroid asteroid = ita.next();
            string += "Asteroid[" + asteroid.getId() + "] location(" + asteroid.getX() + "," + asteroid.getY() + ")\n";
        }
        string += "#bullets: " + bullets.size() + "\n";
        Iterator<Bullet> itb = bullets.iterator();
        while (itb.hasNext()) {
            Bullet bullet = itb.next();
            string += "Bullet[" + bullet.getId() + "] location(" + bullet.getX() + "," + bullet.getY() + ")\n";
        }
        string += "#spaceships: " + spaceships.size() + "\n";
        Iterator<Spaceship> its = spaceships.iterator();
        while (its.hasNext()) {
            Spaceship spaceship = its.next();
            string += "Spaceship[" + spaceship.getId() + "] location(" + spaceship.getX() + "," + spaceship.getY() + ")\n";
        }
        string += "#queue: " + addQueue.size() + "\n";
        Iterator<GameObject> itq = addQueue.iterator();
        while (itq.hasNext()) {
            GameObject gameObject = itq.next();
            string += "Queue[" + gameObject.getId() + "] location(" + gameObject.getX() + "," + gameObject.getY() + ")\n";
        }
        return string;
    }

}
