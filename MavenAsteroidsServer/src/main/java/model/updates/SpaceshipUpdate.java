/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.updates;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import static java.util.logging.Level.FINE;
import model.Spaceship;

/**
 *
 * @author Tom
 */
public class SpaceshipUpdate extends Update implements Serializable {
    
    static final long serialVersionUID = 1005L;
    
    private transient Spaceship spaceship;
    
    private double x,y;
    private double dx,dy;
    private boolean delete;
    private double direction;
    private boolean alive;
    
    public SpaceshipUpdate(Spaceship spaceship) {
        super(spaceship);
        AsteroidsServer.logger.log(FINE, "Create SpaceshipUpdate[{0}]", spaceship.getId());
        this.spaceship = spaceship;
        this.updateSpeed = 1;
        this.x = spaceship.getX();
        this.y = spaceship.getY();
        this.dx = spaceship.getDx();
        this.dy = spaceship.getDy();
        this.delete = spaceship.deleteMe();
        this.direction = spaceship.getDirection();
        this.alive = spaceship.isAlive();
    }

    @Override
    public void reset() {
        AsteroidsServer.logger.log(FINE, "Reset SpaceshipUpdate[{0}]", spaceship.getId());
        this.x = spaceship.getX();
        this.y = spaceship.getY();
        this.dx = spaceship.getDx();
        this.dy = spaceship.getDy();
        this.delete = spaceship.deleteMe();
        this.direction = spaceship.getDirection();
        this.alive = spaceship.isAlive();
    }
    
    @Override
    public boolean repeat() {
        return !delete && alive;
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

    public double getDirection() {
        return direction;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public String toString() {
        return "Update[" + objectId + "]: p(" + (int)x + "," + (int)y + ") d(" + (int)dx + "," + (int)dy + ") " + spaceship;
    }    
    
}
