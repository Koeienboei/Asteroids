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
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import server.network.ClientOutputHandler;
import server.network.OperatorConnector;
/**
 *
 * @author tomei
 */
public class Monitor extends Thread implements Observer {
    
    private Server server;
    
    private final OperatorConnector operatorConnector;
    
    private final Game game;
    private final ConcurrentLinkedQueue<ClientHandler> clients;
    
    private final ConcurrentLinkedQueue<Long> responseTimeGame;
    private final ConcurrentLinkedQueue<Long> responseTimeClients;
    
    private volatile boolean running;
    
    public Monitor(Server server) {
        AsteroidsServer.logger.log(FINE, "[Monitor] Create");
        this.server = server;
        
        this.operatorConnector = server.getOperatorConnector();
        
        this.game = server.getGame();
        this.clients = new ConcurrentLinkedQueue<>();
        
        responseTimeGame = new ConcurrentLinkedQueue<>();
        responseTimeClients = new ConcurrentLinkedQueue<>();
        
        running = false;
    }

    @Override
    public void update(Observable o, Object o1) {
        AsteroidsServer.logger.log(FINE, "[Monitor] Update");
        if (o instanceof Game) {
            responseTimeGame.add(game.getCalculationTime());
        }
        if (o instanceof ClientOutputHandler) {
            ClientOutputHandler clientOutputHandler = (ClientOutputHandler) o;
            responseTimeClients.add(clientOutputHandler.getCalculationTime());
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
                Thread.sleep(1000);
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
        double responseTimeGame = getResponseTimeGame();
        double responseTimeClients = /*getResponseTimeClients()*/ 0.0;
        return responseTimeGame > responseTimeClients ? responseTimeGame : responseTimeClients;
    }
    private double getResponseTimeGame() {
        AsteroidsServer.logger.log(FINE, "[Monitor] Get response time game");
        double total = 0.0;
        double responseTime;
        Iterator<Long> it = responseTimeGame.iterator();
        while (it.hasNext()) {
            responseTime = it.next();
            AsteroidsServer.logger.log(FINE, "[Monitor] Response time game: {0}", responseTime);
            total += responseTime;
        }
        return responseTimeGame.isEmpty() ? total : total/responseTimeGame.size();
    }

    private double getResponseTimeClients() {
        AsteroidsServer.logger.log(FINE, "[Monitor] Get response time game clients");
        double total = 0.0;
        double responseTime;
        Iterator<Long> it = responseTimeClients.iterator();
        while (it.hasNext()) {
            responseTime = it.next();
            AsteroidsServer.logger.log(FINE, "[Monitor] Response time client: {0}", responseTime);
            total += responseTime;
        }
        return responseTimeClients.isEmpty() ? total : total/responseTimeClients.size();
    }

    public double getUtilization() {
        return 0.0;
        //return server.getDockerHandler().getContainerCpuUsage();
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
