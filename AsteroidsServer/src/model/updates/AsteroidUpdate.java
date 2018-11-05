/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.updates;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import static java.util.logging.Level.FINE;
import model.Asteroid;
import model.LargeAsteroid;
import model.MediumAsteroid;
import model.SmallAsteroid;

/**
 *
 * @author Tom
 */
public class AsteroidUpdate extends Update implements Serializable {
    
    static final long serialVersionUID = 1001L;
    
    private transient Asteroid asteroid;
    
    private int type;
    private double x,y;
    private double dx,dy;
    private boolean delete;
    
    public AsteroidUpdate(Asteroid asteroid) {
        super(asteroid);
        AsteroidsServer.logger.log(FINE, "Create AsteroidsUpdate[{0}]", asteroid.getId());
        this.asteroid = asteroid;
        this.updateSpeed = 25;
        this.x = asteroid.getX();
        this.y = asteroid.getY();
        this.dx = asteroid.getDx();
        this.dy = asteroid.getDy();
        this.delete = asteroid.deleteMe();
        if (asteroid instanceof LargeAsteroid) type = 1;
        if (asteroid instanceof MediumAsteroid) type = 2;
        if (asteroid instanceof SmallAsteroid) type = 3;
    }

    @Override
    public void reset() {
        AsteroidsServer.logger.log(FINE, "Reset AsteroidsUpdate[{0}]", asteroid.getId());
        this.x = asteroid.getX();
        this.y = asteroid.getY();
        this.dx = asteroid.getDx();
        this.dy = asteroid.getDy();
        this.delete = asteroid.deleteMe();
    }
    
    @Override
    public boolean repeat() {
        return !delete;
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public boolean isDelete() {
        return delete;
    }

    public int getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "Update[" + objectId + "]: p(" + (int)x + "," + (int)y + ") d(" + (int)dx + "," + (int)dy + ") " + asteroid;
    }
    
}
