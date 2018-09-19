package model;

import java.io.Serializable;
import java.nio.ByteBuffer;
import model.updates.BulletUpdate;
import model.updates.Update;
import server.ClientData;

/**
 * Deze klasse representeert een kogel. Een kogel heeft altijd een aantal frames
 * dat hij blijft leven. Als hij langer dan dit aantal frames in het spel zit
 * wordt hij gewoon verwijderd, omdat kogels anders door zouden blijven vliegen
 * totdat ze tegen een asteroide aankomen.
 *
 * @author Wilco Wijbrandi
 */
public class Bullet extends GameObject {

    private double direction;
    private int stepsToLive;

    public Bullet(double x, double y, double dx, double dy, double direction, AsteroidsModel model) {
        this.stepsToLive = 60;
        this.dx = dx + Math.sin(direction) * 16;
        this.dy = dy - Math.cos(direction) * 16;
        this.x = (model.getWidth() + x + this.dx*2) % model.getWidth();
        this.y = (model.getHeight() + y + this.dy*2) % model.getHeight();   
        this.direction = direction;
        this.radius = 0;
        this.maxSpeed = 50;
        this.delete = false;
        this.model = model;
    }
    
    public Bullet(BulletUpdate bulletUpdate, AsteroidsModel model) {
        this.id = bulletUpdate.getObjectId();
        this.model = model;
        this.dx = bulletUpdate.getDx();
        this.dy = bulletUpdate.getDy();
        this.x = bulletUpdate.getX();
        this.y = bulletUpdate.getY();
        this.direction = bulletUpdate.getDirection();
        this.radius = 0;
        this.maxSpeed = 50;
        this.delete = bulletUpdate.isDelete();
        
    }
    
    @Override
    public void nextStep() {
        this.x = (model.getWidth() + this.x + this.dx) % model.getWidth();
        this.y = (model.getHeight() + this.y + this.dy) % model.getHeight();
    }

    public void increaseLifeTime() {
        stepsToLive--;
    }
    
    public double getDirection() {
        return direction;
    }

    public int getStepsToLive() {
        return stepsToLive;
    }
    
    @Override
    public void update(Update update) {
        if (update instanceof BulletUpdate) {
            BulletUpdate bulletUpdate = (BulletUpdate) update;
            if (bulletUpdate.isDelete()) {
                destroy();
                return;
            }
            x = bulletUpdate.getX();
            y = bulletUpdate.getY();
            dx = bulletUpdate.getDx();
            dy = bulletUpdate.getDy();
        }
    }
    
    @Override
    public Update getUpdate() {
        return new BulletUpdate(this);
    }

    @Override
    public String toString() {
        return "Bullet[" + id + "] p(" + (int)x + "," + (int)y + ") d(" + (int)dx + "," + (int)dy + ")";
    }
    
}
