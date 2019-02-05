/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import asteroidsserver.AsteroidsServer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.lang.management.ManagementFactory;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import server.network.OperatorConnector;
/**
 *
 * @author tomei
 */
public class Monitor extends Thread implements Observer {
    
    private final OperatorConnector operatorConnector;
    
    private final Game game;
    private final LinkedList<ClientHandler> clients;
    
    private final LinkedList<Long> responseTimeGame;
    private final LinkedList<Long> responseTimeClients;
    
    private volatile boolean running;
    
    public Monitor(OperatorConnector operatorConnector, Game game) {
        AsteroidsServer.logger.log(FINE, "[Monitor] Create");
        this.operatorConnector = operatorConnector;
        
        this.game = game;
        this.clients = new LinkedList<>();
        
        responseTimeGame = new LinkedList<>();
        responseTimeClients = new LinkedList<>();
        
        running = false;
    }

    @Override
    public void update(Observable o, Object o1) {
        AsteroidsServer.logger.log(FINE, "[Monitor] Update");
        if (game.hasChanged()) {
            responseTimeGame.add(game.getCalculationTime());
        }
        if (o1 instanceof ClientHandler) {
            ClientHandler clientHandler = (ClientHandler) o1;
            if (clientHandler.getOutput().hasChanged()) {
                responseTimeClients.add(clientHandler.getOutput().getCalculationTime());
            }
        }
    }
    
    public void addClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "[Monitor] Add Client");
        clients.add(clientHandler);
        clientHandler.getOutput().addObserver(this);
    }
    
    public void removeClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "[Monitor] Remove Client");
        clients.remove(clientHandler);
        clientHandler.getOutput().deleteObserver(this);
    }
    
    @Override
    public synchronized void run() {
        AsteroidsServer.logger.log(FINE, "[Monitor] Start");
        running = true;
        while (running) {
            operatorConnector.getOutput().sendMonitorPacket(this);
            reset();
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                AsteroidsServer.logger.log(SEVERE, "[Monitor] Failed to wait");
            }
        }
    }

    private void reset() {
        AsteroidsServer.logger.log(FINE, "[Monitor] Reset");
        responseTimeGame.clear();
        responseTimeClients.clear();
    }
    
    public double getResponseTime() {
        return getResponseTimeGame();
    }
    private double getResponseTimeGame() {
        double total = 0.0;
        Iterator<Long> it = responseTimeGame.iterator();
        while (it.hasNext()) {
            total += it.next();
        }
        return responseTimeGame.isEmpty() ? total : total/responseTimeGame.size();
    }

    private double getResponseTimeClients() {
        double max = 0;
        double next;
        Iterator<Long> it = responseTimeClients.iterator();
        while (it.hasNext()) {
            next = it.next();
            if (max < next) {
                max = next;
            }
        }
        return max;
    }

    public double getUtilization() {
        return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }

    public double getThroughput() {        
        double total = 0;
        Iterator<ClientHandler> it = clients.iterator();
        while (it.hasNext()) {
            total += it.next().getUpdateQueue().getAmountProcessed();
        }
        return total;
    }
    
}
