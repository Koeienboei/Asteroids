package controller;

import asteroidsserver.AsteroidsServer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.Observable;
import static java.util.logging.Level.FINE;
import model.updates.ControllerUpdate;

/**
 * Dit is de klasse SpaceshipController. Java werkt standaard met event driven
 * programming voor het indrukken van toetsen. Dit is helaas niet zo handig bij
 * dit programma. Daarom hebben we een aparte klasse SpaceshipController
 * gemaakt, die ten alle tijde kan vertellen of de knop omhoog, links, rechts of
 * de spatiebalk ingedrukt is.
 *
 * @author Wilco Wijbrandi
 */
public class SpaceshipController extends Observable implements KeyListener, Serializable {

    private boolean up;
    private boolean left;
    private boolean right;
    private boolean space;

    public SpaceshipController() {
        AsteroidsServer.logger.log(FINE, "Create Controller");
        up = false;
        left = false;
        right = false;
        space = false;
    }

    public void reset() {
        AsteroidsServer.logger.log(FINE, "Reset Controller");
        up = false;
        left = false;
        right = false;
        space = false;
    }
    
    public boolean goForward() {
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
    
    public void update(ControllerUpdate update) {
        AsteroidsServer.logger.log(FINE, "Update Controller");
        up = update.goUp();
        right = update.goRight();
        left = update.goLeft();
        space = update.fireBullets();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        AsteroidsServer.logger.log(FINE, "Key pressed");
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.up = true;
                break;
            case KeyEvent.VK_LEFT:
                this.left = true;
                break;
            case KeyEvent.VK_RIGHT:
                this.right = true;
                break;
            case KeyEvent.VK_SPACE:
                this.space = true;
        }
        // Notify obeservers
        setChanged();
        notifyObservers();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        AsteroidsServer.logger.log(FINE, "Key released");
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                this.up = false;
                break;
            case KeyEvent.VK_LEFT:
                this.left = false;
                break;
            case KeyEvent.VK_RIGHT:
                this.right = false;
                break;
            case KeyEvent.VK_SPACE:
                this.space = false;
        }
        // Notify obeservers
        setChanged();
        notifyObservers();
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }

    @Override
    public String toString() {
        return "Controller: (" + up + "," + left + "," + right + "," + space + ")";
    }
    
}
