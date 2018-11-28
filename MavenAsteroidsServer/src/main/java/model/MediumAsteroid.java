package model;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import static java.util.logging.Level.FINE;
import model.updates.AsteroidUpdate;
import model.updates.Update;

/**
 * Een medium asteroide.
 *
 * @author Wilco Wijbrandi
 */
public class MediumAsteroid extends Asteroid {

    public MediumAsteroid(double x, double y, double dx, double dy, AsteroidsModel model) {
        AsteroidsServer.logger.log(FINE, "Create MediumAsteroid");
        this.model = model;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = 20;
        this.maxSpeed = 9;
        this.delete = false;
    }
    
    public MediumAsteroid(AsteroidUpdate asteroidUpdate, AsteroidsModel model) {
        AsteroidsServer.logger.log(FINE, "Create MediumAsteroid");
        this.id = asteroidUpdate.getObjectId();
        this.model = model;
        this.x = asteroidUpdate.getX();
        this.y = asteroidUpdate.getY();
        this.dx = asteroidUpdate.getDx();
        this.dy = asteroidUpdate.getDy();
        this.radius = 20;
        this.maxSpeed = 9;
        this.delete = asteroidUpdate.isDelete();
    }
    
    @Override
    protected void calculateCollisions() {
        AsteroidsServer.logger.log(FINE, "Calculate collisions MediumAsteroid");
        Collection<Bullet> bullets = model.getBullets();
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            // Raakt deze asteroide een kogel aan?
            if (collides(bullet)) {
		// Maak twee kleine asteroiden, die 1.5 keer zo snel gaan en in
                // tegenovergestelde richtingen van elkaar gaan
                SmallAsteroid a = new SmallAsteroid(x, y, -dx * 1.5, dy * 1.5, model);
                model.addGameObject(a);
                SmallAsteroid b = new SmallAsteroid(x, y, dx * 1.5, -dy * 1.5, model);
                model.addGameObject(b);
                // Verwijder deze asteroide
                this.destroy();
                // Verwijder de kogel
                bullet.destroy();
            }
        }

    }
    
    @Override
    public String toString() {
        return "MediumAsteroid[" + id + "] p(" + (int)x + "," + (int)y + ") d(" + (int)dx + "," + (int)dy + ")";
    }
    
}
