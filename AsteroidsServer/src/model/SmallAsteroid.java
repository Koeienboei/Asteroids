package model;

import asteroidsserver.AsteroidsServer;
import java.util.Collection;
import java.util.Iterator;
import static java.util.logging.Level.FINE;
import model.updates.AsteroidUpdate;

/**
 * Een kleine asteroide.
 *
 * @author Wilco Wijbranid
 */
public class SmallAsteroid extends Asteroid {

    public SmallAsteroid(double x, double y, double dx, double dy, AsteroidsModel model) {
        AsteroidsServer.logger.log(FINE, "Create SmallAsteroid");
        this.model = model;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.radius = 10;
        this.maxSpeed = 13;
    }
    
    public SmallAsteroid(AsteroidUpdate asteroidUpdate, AsteroidsModel model) {
        AsteroidsServer.logger.log(FINE, "Create SmallAsteroid");
        this.id = asteroidUpdate.getObjectId();
        this.model = model;
        this.x = asteroidUpdate.getX();
        this.y = asteroidUpdate.getY();
        this.dx = asteroidUpdate.getDx();
        this.dy = asteroidUpdate.getDy();
        this.radius = 10;
        this.maxSpeed = 13;
        this.delete = asteroidUpdate.isDelete();
    }
    
    @Override
    protected void calculateCollisions() {
        AsteroidsServer.logger.log(FINE, "Calculate collisions SmallAsteroid");
        Collection<Bullet> bullets = model.getBullets();
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet bullet = it.next();
            if (collides(bullet)) {
                // Als de asteroide geraakt wordt door een kogel kunnen de kogel
                // en de asteroide verwijderd worden
                this.destroy();
                bullet.destroy();
            }
        }
    }
    
    @Override
    public String toString() {
        return "SmallAsteroid[" + id + "] p(" + (int)x + "," + (int)y + ") d(" + (int)dx + "," + (int)dy + ")";
    }
    
}
