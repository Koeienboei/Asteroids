/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.updates;

import java.io.Serializable;
import model.Spaceship;

/**
 *
 * @author Tom
 */
public class ControllerUpdate extends Update implements Serializable {
    
    static final long serialVersionUID = 1003L;
    
    private transient Spaceship spaceship;
    
    private boolean up;
    private boolean left;
    private boolean right;
    private boolean space;
    
    public ControllerUpdate(Spaceship spaceship) {
        super(spaceship);
        this.spaceship = spaceship;
        this.updateSpeed = 0;
        this.up = spaceship.getSpaceshipController().goForward();
        this.left = spaceship.getSpaceshipController().goLeft();
        this.right = spaceship.getSpaceshipController().goRight();
        this.space = spaceship.getSpaceshipController().fireBullets();
    }

    @Override
    public void reset() {
        this.up = spaceship.getSpaceshipController().goForward();
        this.left = spaceship.getSpaceshipController().goLeft();
        this.right = spaceship.getSpaceshipController().goRight();
        this.space = spaceship.getSpaceshipController().fireBullets();
    }
    
    @Override
    public boolean repeat() {
        return false;
    }
    
    public boolean goUp() {
        return up;
    }

    public boolean goLeft() {
        return left;
    }

    public boolean goRight() {
        return right;
    }

    public boolean fireBullets() {
        return space;
    }
    
    @Override
    public String toString() {
        return "Update[" + objectId + "]: (" + up + "," + left + "," + right + "," + space + ") " + (spaceship!=null ? spaceship.getSpaceshipController() : "null");
    }
    
}
