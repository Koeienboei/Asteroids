/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import static client.ClientState.INITIALIZE;
import static client.ClientState.LOGIN;
import static client.ClientState.PLAYING;
import static java.lang.Math.max;
import java.util.Observable;
import model.AsteroidsModel;

/**
 *
 * @author tomei
 */
public class Game extends Observable implements Runnable {
    
    private Client client;
    private AsteroidsModel model;
    
    public Game(Client client) {
        this.client = client;
    }
    
    @Override
    public void run() {
        long time;
        
        while (runCondition()) {
            time = System.currentTimeMillis();
            nextStep();
            try {
                Thread.sleep(max(0,40 - (System.currentTimeMillis() - time)));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            setChanged();
            notifyObservers();
        }
    }
    
    private void nextStep() {
	model.addNewObjects();
        model.removeOldObjects();
	model.update();
	model.nextStep();
    }
 
    public void initModel(int height, int width) {
        model = new AsteroidsModel(height, width);
    }
    
    private boolean runCondition() {
        return model != null && (client.getClientState() == LOGIN || client.getClientState() == INITIALIZE || client.getClientState() == PLAYING);
    }

    public AsteroidsModel getModel() {
        return model;
    }
    
}
