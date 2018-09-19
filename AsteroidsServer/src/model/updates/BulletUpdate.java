/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.updates;

import java.io.Serializable;
import model.Bullet;

/**
 *
 * @author Tom
 */
public class BulletUpdate extends Update implements Serializable {
    
    static final long serialVersionUID = 1002L;
    
    private transient Bullet bullet;
    
    private double x,y;
    private double dx,dy;
    private boolean delete;
    private double direction;
    
    public BulletUpdate(Bullet bullet) {
        super(bullet);
        this.bullet = bullet;
        this.updateSpeed = 0;
        this.x = bullet.getX();
        this.y = bullet.getY();
        this.dx = bullet.getDx();
        this.dy = bullet.getDy();
        this.delete = bullet.deleteMe();
        this.direction = bullet.getDirection();
    }
    
    @Override
    public void reset() {
        this.x = bullet.getX();
        this.y = bullet.getY();
        this.dx = bullet.getDx();
        this.dy = bullet.getDy();
        this.delete = bullet.deleteMe();
        this.direction = bullet.getDirection();
    }

    @Override
    public boolean repeat() {
        return false;
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
    
    @Override
    public String toString() {
        return "Update[" + objectId + "]: p(" + (int)x + "," + (int)y + ") d(" + (int)dx + "," + (int)dy + ") " + bullet;
    }
    
}
