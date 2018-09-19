package model;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.sqrt;
import model.updates.Update;

/**
 * GameObject is een abstracte klasse waar iedere klasse die in ruimte zweeft
 * van erft. Een GameObject heeft een locatie en een snelheid.
 * 
 * @author Wilco Wijbrandi
 */
public abstract class GameObject {
    
    protected AsteroidsModel model;
    
    protected int id;
    
    protected double x;
    protected double y;
    protected double dx;
    protected double dy;
    
    protected int radius;
    
    protected boolean delete;
    
    protected int maxSpeed;
    
    public GameObject() {
    }
    
    abstract public void nextStep();

    public int getRadius() {
        return radius;
    }

    public int getId() {
        return id;
    }

    public double getX() {
	return this.x;
    }

    public double getY() {
	return this.y;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }   

    public void setId(int id) {
        this.id = id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }  

    public boolean deleteMe() {
        return delete;
    }
    
    public void destroy() {
        this.delete = true;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public boolean collides(GameObject o) {
	double x = Math.abs(this.x - o.getX());
	double y = Math.abs(this.y - o.getY());
	double distance = Math.sqrt(x * x + y * y);
	return distance < this.getRadius() + o.getRadius();
    }
    
    public int getMinimalStepsTillInRange(GameObject gameObject) {
        return (int) (max(0,(distance(gameObject)-600)) / (maxSpeed+gameObject.getMaxSpeed()));
    }
    
    private double xDistance(GameObject gameObject) {
        double xDistance = abs(x - gameObject.getX());
        if (xDistance > model.getWidth() / 2) {
            xDistance = model.getWidth() - xDistance;
        }
        return xDistance;
    }
    
    private double yDistance(GameObject gameObject) {
        double yDistance = abs(y - gameObject.getY());
        if (yDistance > model.getHeight() / 2) {
            yDistance = model.getHeight() - yDistance;
        }
        return yDistance;
    }
    
    public double distance(GameObject gameObject) {
        double xDistance = xDistance(gameObject);
        double yDistance = yDistance(gameObject);
        return sqrt(xDistance*xDistance + yDistance*yDistance);
    }
        
    public abstract void update(Update update);

    public abstract Update getUpdate();
    
}
