package model;

import java.io.Serializable;
import model.updates.AsteroidUpdate;
import model.updates.Update;
import server.ClientData;

/**
 * Dit is een abstracte klasse Asteroid. Er zijn drie soorten asteroiden: large,
 * medium en small.
 * 
 * @author Wilco Wijbrandi
 */
public abstract class Asteroid extends GameObject {

    public Asteroid() {
    }

    protected abstract void calculateCollisions();

    @Override
    public void nextStep() {
	x = (model.getWidth() + x + dx) % model.getWidth();
	y = (model.getHeight() + y + dy) % model.getHeight();
    }
    
    @Override
    public void update(Update update) {
        if (update instanceof AsteroidUpdate) {
            AsteroidUpdate asteroidUpdate = (AsteroidUpdate) update;
            if (asteroidUpdate.isDelete()) {
                destroy();
                return;
            }
            x = asteroidUpdate.getX();
            y = asteroidUpdate.getY();
            dx = asteroidUpdate.getDx();
            dy = asteroidUpdate.getDy();
        }
    }
    
    @Override
    public Update getUpdate() {
        return new AsteroidUpdate(this);
    }
    
}
