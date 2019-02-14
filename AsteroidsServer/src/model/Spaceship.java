package model;

import asteroidsserver.AsteroidsServer;
import controller.SpaceshipController;
import static java.lang.Math.sqrt;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import static java.util.logging.Level.FINE;
import model.updates.ControllerUpdate;
import model.updates.SpaceshipUpdate;
import model.updates.Update;
import server.ClientHandler;
import static server.ClientState.ALIVE;
import static server.ClientState.DEAD;

/**
 * De klasse die een ruimteschip representeert. De klasse wordt bestuurd door middel van een KeyManager, die doorgeeft op welke knoppen gedrukt wordt.
 */
public class Spaceship extends GameObject {

    private ClientHandler clientHandler;
    private SpaceshipController sc;

    private double direction;

    private int stepsTillFire;

    private boolean alive;
    private int stepsTillAlive;

    public Spaceship(AsteroidsModel model, ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "Create Spaceship");
        this.sc = new SpaceshipController();
        this.model = model;
        this.clientHandler = clientHandler;
        this.dx = 0;
        this.dy = 0;
        this.radius = 15;
        this.direction = 0;
        this.stepsTillFire = 0;
        this.stepsTillAlive = 0;
        this.maxSpeed = 50;
        this.delete = false;
        this.alive = true;
    }

    public Spaceship(SpaceshipUpdate spaceshipUpdate, AsteroidsModel model) {
        AsteroidsServer.logger.log(FINE, "Create Spaceship");
        this.id = spaceshipUpdate.getObjectId();
        this.x = spaceshipUpdate.getX();
        this.y = spaceshipUpdate.getY();
        this.dx = spaceshipUpdate.getDx();
        this.dy = spaceshipUpdate.getDy();
        this.radius = 15;
        this.direction = spaceshipUpdate.getDirection();
        this.alive = spaceshipUpdate.isAlive();
        this.delete = spaceshipUpdate.isDelete();
        this.sc = new SpaceshipController();
        this.model = model;
    }

    public void spawn() {
        AsteroidsServer.logger.log(FINE, "Spawn Spaceship");
        Random random = new Random();
        do {
            x = random.nextInt(model.getWidth());
            y = random.nextInt(model.getHeight());
        } while (collides());
    }

    public void reset() {
        AsteroidsServer.logger.log(FINE, "Reset Spaceship");
        dx = 0;
        dy = 0;
        direction = 0;
        stepsTillFire = 0;
        stepsTillAlive = 0;
        sc.reset();
        //spawn();
        setAlive();
    }

    @Override
    public void nextStep() {
        AsteroidsServer.logger.log(FINE, "Next step Spaceship");
        if (alive) {
            //System.out.println("Speed: (" + dx + "," + dy + ")" + " " + sqrt(dx*dx+dy*dy));
            // Draai het ruimteschip indien nodig
            if (sc.goLeft()) {
                this.direction -= 0.04 * Math.PI;
            }
            if (sc.goRight()) {
                this.direction += 0.04 * Math.PI;
            }

            // Ga vooruit indien nodig
            if (sc.goForward()) {
                this.dx += Math.sin(direction) * 0.4;
                this.dy -= Math.cos(direction) * 0.4;
            }

            // Update de locatie
            this.x = (model.getWidth() + this.x + this.dx) % model.getWidth();
            this.y = (model.getHeight() + this.y + this.dy) % model.getHeight();

            // Pas de weerstand toe (in de ruimte is er niet zoveel luchtweerstand
            // ofzo, maar het maakt het spel wel iets beter te spelen)
            this.dx *= 0.99;
            this.dy *= 0.99;
        }
    }

    public void calculateCollisions() {
        AsteroidsServer.logger.log(FINE, "Calculate collisions Spaceship");
        if (alive && collides()) {
            kill();
        }
    }

    public void calculateReset() {
        AsteroidsServer.logger.log(FINE, "Calculate reset Spaceship");
        if (!alive) {
            if (stepsTillAlive <= 0) {
                reset();
            } else {
                stepsTillAlive--;
            }
        }
    }

    public void calculateFire() {
        AsteroidsServer.logger.log(FINE, "Calculate fire Spaceship");
        this.stepsTillFire = Math.max(0, this.stepsTillFire - 1);
        if (alive == true && sc.fireBullets() && stepsTillFire == 0) {
            Bullet b = new Bullet(this.x, this.y, this.dx, this.dy, this.direction, model);
            model.addGameObject(b);
            stepsTillFire = 25;
        }
    }

    public boolean collides() {
        AsteroidsServer.logger.log(FINE, "Collides Spaceship");
        Collection<Asteroid> asteroids = model.getAsteroids();
        Iterator<Asteroid> ita = asteroids.iterator();
        while (ita.hasNext()) {
            if (this.collides(ita.next())) {
                return true;
            }
        }

        Collection<Bullet> bullets = model.getBullets();
        Iterator<Bullet> itb = bullets.iterator();
        while (itb.hasNext()) {
            if (this.collides(itb.next())) {
                return true;
            }
        }

        return false;
    }

    public void setSpaceshipController(SpaceshipController sc) {
        this.sc = sc;
    }

    public SpaceshipController getSpaceshipController() {
        return sc;
    }

    public void setAlive() {
        AsteroidsServer.logger.log(FINE, "Set alive Spaceship");
        clientHandler.setState(ALIVE);
        alive = true;
    }

    public void kill() {
        AsteroidsServer.logger.log(FINE, "Kill Spaceship");
        clientHandler.setState(DEAD);
        alive = false;
        stepsTillAlive = 75;
    }

    public boolean isAlive() {
        return alive;
    }

    public double getDirection() {
        return this.direction;
    }

    @Override
    public void update(Update update) {
        AsteroidsServer.logger.log(FINE, "Update Spaceship");
        if (update instanceof SpaceshipUpdate) {
            SpaceshipUpdate spaceshipUpdate = (SpaceshipUpdate) update;
            if (spaceshipUpdate.isDelete()) {
                destroy();
                return;
            }
            x = spaceshipUpdate.getX();
            y = spaceshipUpdate.getY();
            dx = spaceshipUpdate.getDx();
            dy = spaceshipUpdate.getDy();
            direction = spaceshipUpdate.getDirection();
            alive = spaceshipUpdate.isAlive();
            return;
        }
        if (update instanceof ControllerUpdate) {
            sc.update((ControllerUpdate) update);
        }
    }

    @Override
    public Update getUpdate() {
        return new SpaceshipUpdate(this);
    }

    @Override
    public String toString() {
        return "Spaceship[" + id + "] p(" + (int) x + "," + (int) y + ") d(" + (int) dx + "," + (int) dy + ")";
    }

}
