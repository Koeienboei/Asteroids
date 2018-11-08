/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import asteroidsclient.AsteroidsClient;
import static client.ClientState.INITIALIZE;
import static client.ClientState.LOGIN;
import static client.ClientState.PLAYING;
import static java.lang.Math.max;
import java.util.Observable;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import model.AsteroidsModel;

/**
 *
 * @author tomei
 */
public class Game extends Observable implements Runnable {
    
    private Client client;
    private AsteroidsModel model;
    private volatile boolean running;
    
    public Game(Client client) {
        client.logger.log(FINE, "[Game] Create");
        this.client = client;
        this.running = false;
    }
    
    @Override
    public void run() {
        client.logger.log(FINE, "[Game] Start");
        running = true;
        long time;
        
        while (running) {
            time = System.currentTimeMillis();
            nextStep();
            try {
                client.logger.log(FINE, "[Game] Wait for {0}", max(0,40 - (System.currentTimeMillis() - time)));
                Thread.sleep(max(0,40 - (System.currentTimeMillis() - time)));
            } catch (InterruptedException ex) {
                client.logger.log(SEVERE, "[Game] Failed to wait");
            }
            setChanged();
            notifyObservers();
        }
    }
    
    private void nextStep() {
        client.logger.log(FINE, "[Game] Next step");
	model.addNewObjects();
        model.removeOldObjects();
	model.update();
	model.nextStep();
    }
 
    public void initModel(int height, int width) {
        client.logger.log(FINE, "[Game] Initialize Model");
        model = new AsteroidsModel(height, width);
    }
    
    public void stopRunning() {
        running = false;
    }

    public AsteroidsModel getModel() {
        return model;
    }
    
}
