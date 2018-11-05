/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network;

import asteroidsserver.AsteroidsServer;
import static java.lang.Math.max;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import model.GameObject;
import model.updates.SpaceshipUpdate;
import model.updates.Update;
import server.network.packets.InitPacket;
import server.network.packets.UpdatePacket;
import server.ClientHandler;
import server.Server;
import server.network.basic.OutputHandler;
import server.network.packets.LogoutPacket;

/**
 *
 * @author Tom
 */
public class ClientOutputHandler extends Observable implements Runnable {

    private Server server;
    private ClientHandler clientHandler;

    private OutputHandler output;

    private long calculationTime;
    
    private volatile boolean running;

    public ClientOutputHandler(ClientHandler clientHandler, Server server) {
        AsteroidsServer.logger.log(INFO, "[ClientOutputHandler] Create");
        this.server = server;
        this.clientHandler = clientHandler;

        output = new OutputHandler(clientHandler.getSocket());
        
        this.calculationTime = 0;
        this.running = false;
    }

    public void sendInitPacket() {
        AsteroidsServer.logger.log(INFO, "[ClientOutputHandler] Create InitPacket");
        LinkedList<Update> updates = new LinkedList<>();
        LinkedList<GameObject> gameObjects = server.getGame().getModel().getGameObjects();
        Iterator<GameObject> it = gameObjects.iterator();
        while (it.hasNext()) {
            updates.add(it.next().getUpdate());
        }
        output.send(new InitPacket(updates));
    }

    public void sendSpaceship() {
        AsteroidsServer.logger.log(INFO, "[ClientOutputHandler] Create last InitPacket");
        output.send(new InitPacket(new SpaceshipUpdate(clientHandler.getSpaceship())));
    }

    private synchronized void update() {
        AsteroidsServer.logger.log(INFO, "[ClientOutputHandler] Start updating Client");
        long time;
        while (running) {
            time = System.currentTimeMillis();
            UpdatePacket updatePacket = clientHandler.getUpdateQueue().pop();
            if (updatePacket != null) {
                output.send(updatePacket);
            }
            try {
                calculationTime = System.currentTimeMillis() - time;
                this.wait(max(0, 40 - calculationTime));
            } catch (InterruptedException ex) {
                AsteroidsServer.logger.log(SEVERE, "[ClientOutputHandler] Failed to wait after sending UpdatePacket");
            }
        }
    }

    public void sendLogoutPacket() {
        output.send(new LogoutPacket());
    }
    
    @Override
    public void run() {
        AsteroidsServer.logger.log(INFO, "[ClientOutputHandler] Start");
        running = true;
        update();
        AsteroidsServer.logger.log(INFO, "[ClientOutputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsServer.logger.log(INFO, "[ClientOutputHandler] Stop running");
        running = false;
    }

    public long getCalculationTime() {
        return calculationTime;
    }

}
