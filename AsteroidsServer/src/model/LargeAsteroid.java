package model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import model.updates.AsteroidUpdate;
import model.updates.Update;

/**
 * Een grote asteroide.
 *
 * @author Wilco Wijbrandi
 */
public class LargeAsteroid extends Asteroid {

    public LargeAsteroid(AsteroidsModel model) {
        this.model = model;
        this.radius = 40;
        this.maxSpeed = 6;
        this.delete = false;
        spawn();
    }
    
    public LargeAsteroid(AsteroidUpdate asteroidUpdate, AsteroidsModel model) {
        this.model = model;
        this.radius = 40;
        this.maxSpeed = 6;
        this.id = asteroidUpdate.getObjectId();
        this.x = asteroidUpdate.getX();
        this.y = asteroidUpdate.getY();
        this.dx = asteroidUpdate.getDx();
        this.dy = asteroidUpdate.getDy();
        this.delete = asteroidUpdate.isDelete();
        
    }
    
    private void spawn() {    
        Random random = new Random();
        do {
            x = random.nextInt(model.getWidth());
            y = random.nextInt(model.getHeight());
        } while (collides());

        // Generates a random velocity
        dx = random.nextDouble() * 8 - 4;
        dy = random.nextDouble() * 8 - 4;
    }
    
    @Override
    protected void calculateCollisions() {
        Collection<Bullet> bullets = model.getBullets();
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            // Raakt deze asteroide een kogel aan?
            if (collides(bullet)) {
		// Maak twee medium asteroiden, die 1.5 keer zo snel gaan en in
                // tegenovergestelde richtingen van elkaar gaan
                MediumAsteroid a = new MediumAsteroid(x, y, -dx * 1.5,
                        dy * 1.5, model);
                model.addGameObject(a);
                a = new MediumAsteroid(x, y, dx * 1.5, -dy * 1.5, model);
                model.addGameObject(a);
                // Verwijder deze asteroide
                this.destroy();
                // Verwijder de kogel
                bullet.destroy();
            }
        }
    }
    
    public boolean collides() {
        Collection<Spaceship> spaceships = model.getSpaceships();
        Iterator<Spaceship> it = spaceships.iterator();
        while (it.hasNext()) {
            Spaceship spaceship = it.next();
            if (collides(spaceship)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "LargeAsteroid[" + id + "] p(" + (int)x + "," + (int)y + ") d(" + (int)dx + "," + (int)dy + ")";
    }
    
}
