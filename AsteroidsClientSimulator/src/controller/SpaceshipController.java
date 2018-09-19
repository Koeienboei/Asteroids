package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.Observable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class SpaceshipController extends Observable implements Runnable {

    private boolean up;
    private boolean left;
    private boolean right;
    private boolean space;

    public SpaceshipController() {
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
        up = update.goUp();
        right = update.goRight();
        left = update.goLeft();
        space = update.fireBullets();
    }

    private synchronized void calculateRandomStates() {
        int button;
        int waitTime;
        Random random = new Random();

        while (true) {
            button = random.nextInt(8);
            waitTime = random.nextInt(1000);

            switch (button) {
                case 0:
                    up = true;
                    break;
                case 1:
                    right = true;
                    break;
                case 2:
                    left = true;
                    break;
                case 3:
                    space = true;
                    break;
                case 4:
                    up = false;
                    break;
                case 5:
                    right = false;
                    break;
                case 6:
                    left = false;
                    break;
                case 7:
                    space = false;
                    break;
            }

            try {
                this.wait(waitTime);
            } catch (InterruptedException ex) {
                System.out.println("[ERROR] Failed to wait in controller");
            }
            
            setChanged();
            notifyObservers();
        }
    }

    @Override
    public void run() {
        calculateRandomStates();
    }
}


