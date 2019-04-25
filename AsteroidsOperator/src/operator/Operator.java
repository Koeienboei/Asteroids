/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import java.io.IOException;
import server.network.basic.Address;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import operator.network.ClientConnector;
import operator.network.ServerConnector;
import server.network.packets.ClientStatePacket;
import server.network.packets.ServerPacket;
import static server.ClientState.DEAD;
import static server.ClientState.LOGIN;
import static server.ClientState.LOGOUT;

/**
 *
 * @author tomei
 */
public class Operator extends Observable implements Observer {

    private DockerHandler dockerHandler;
    
    private ClientConnector clientConnector;
    private ServerConnector serverConnector;
    private ConcurrentLinkedQueue<ClientHandler> clients;
    private ConcurrentLinkedQueue<ServerHandler> servers;

    private int serverGameHeight;
    private int serverGameWidth;
    private int serverPortCounter;

    private Monitor monitor;
    private Planner planner;

    private boolean markedShutdown;
    
    private double lastClientSwap;

    public Operator(int rLow, int rHigh, int rMax, int W, int reconfigurationSpeed) {
        AsteroidsOperator.logger.log(FINE, "[Operator] Create");
        dockerHandler = new DockerHandler();
        
        clientConnector = new ClientConnector(this);
        serverConnector = new ServerConnector(this);

        clients = new ConcurrentLinkedQueue<>();
        servers = new ConcurrentLinkedQueue<>();

        serverGameHeight = 1600;
        serverGameWidth = 1600;
        serverPortCounter = 8900;

        monitor = new Monitor(this, rLow, rHigh, rMax, W, reconfigurationSpeed);
        planner = new Planner(this, rLow, rHigh, rMax);

        markedShutdown = false;
        
        lastClientSwap = System.currentTimeMillis();
    }

    public synchronized void start() {
        AsteroidsOperator.logger.log(FINE, "[Operator] Start");
        monitor.start();
        serverConnector.start();
        ServerStarter serverStarter = new ServerStarter();
        startServers(1);
        //serverStarter.start();
        clientConnector.start();
    }

    public void addClient(ClientHandler clientData) {
        AsteroidsOperator.logger.log(FINE, "[Operator] Add Client");
        clients.add(clientData);
        setChanged();
        notifyObservers();
    }

    public void removeClient(ClientHandler clientHandler) {
        AsteroidsOperator.logger.log(FINE, "[Operator] Remove Client: {0}", clientHandler.getAddressConnectionServer());
        clients.remove(clientHandler);
        setChanged();
        notifyObservers();
    }

    public ClientHandler getClientHandler(Address clientAddress) {
        AsteroidsOperator.logger.log(FINE, "[Operator] Get Client: {0}", clientAddress);
        Iterator<ClientHandler> it = clients.iterator();
        while (it.hasNext()) {
            ClientHandler clientHandler = it.next();
            if (clientHandler.getAddressConnectionServer() != null && clientHandler.getAddressConnectionServer().equals(clientAddress)) {
                return clientHandler;
            }
        }
        AsteroidsOperator.logger.log(SEVERE, "[Operator] Failed to get Client: {0}", clientAddress);
        return null;
    }

    public void addServer(ServerHandler serverHandler) {
        AsteroidsOperator.logger.log(INFO, "[Operator] Add Server: {0}", serverHandler.getAddressForOperator());
        servers.add(serverHandler);
        setChanged();
        notifyObservers();
    }

    public void removeServer(ServerHandler serverHandler) {
        AsteroidsOperator.logger.log(INFO, "[Operator] Remove Server: {0}", serverHandler.getAddressForOperator());
        servers.remove(serverHandler);
        setChanged();
        notifyObservers();
        if (markedShutdown && servers.isEmpty()) {
            shutdown();
        }
    }

    public ServerHandler getServer() {
        AsteroidsOperator.logger.log(FINE, "[Operator] Get Server with lowest Utility");
        ServerHandler lowestUtilityServer = null;
        Iterator<ServerHandler> it = servers.iterator();
        while (it.hasNext()) {
            ServerHandler serverData = it.next();
            if (!serverData.isRunning() || serverData.isMarkedShutdown()) {
                continue;
            }
            if (lowestUtilityServer == null) {
                lowestUtilityServer = serverData;
            } else if (serverData.getUtility() < lowestUtilityServer.getUtility()) {
                lowestUtilityServer = serverData;
            }
        }
        return lowestUtilityServer;
    }

    public void startServers(int amount) {
        AsteroidsOperator.logger.log(FINE, "[Operator] Start Servers");
        for (int i=0; i<amount; i++) {
            startServer();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[Operator] Failed to wait after spawning new server");
            }
        }
    }
    
    public void startServer() {
        AsteroidsOperator.logger.log(FINE, "[Operator] Start Server");
        dockerHandler.startServer();
    }

    public void removeServers(int amount) {
        for (int i=0; i<amount; i++) {
            removeServer();
            /*try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[Operator] Failed to wait after spawning new server");
            }*/
        }
    }
    
    public void removeServer() {
        AsteroidsOperator.logger.log(FINE, "[Operator] Remove Server");
        ServerHandler lowestUtilityServer;
        Iterator<ServerHandler> it = servers.iterator();
        if (it.hasNext()) {
            lowestUtilityServer = it.next();
        } else {
            return;
        }
        while (it.hasNext()) {
            ServerHandler serverData = it.next();
            if (serverData.isRunning() && !serverData.isMarkedShutdown() && serverData.getUtility() < lowestUtilityServer.getUtility()) {
                lowestUtilityServer = serverData;
            }
        }
        lowestUtilityServer.markShutdown();
        setChanged();
        notifyObservers();
    }

    public void shutdown() {
        AsteroidsOperator.logger.log(FINE, "[Operator] Shutdown");
        markedShutdown = true;
        monitor.stopRunning();
        clientConnector.stopRunning();
        clientConnector.disconnect();
        serverConnector.stopRunning();
        serverConnector.disconnect();
        Iterator<ServerHandler> its = servers.iterator();
        while (its.hasNext()) {
            its.next().shutdown();
        }
        Iterator<ClientHandler> itc = clients.iterator();
        while (itc.hasNext()) {
            itc.next().logout();
        }
    }

    public double getAverageUtilization() {
        int utilization = 0;
        Iterator<ServerHandler> it = servers.iterator();
        while (it.hasNext()) {
            utilization += it.next().getUtility();
        }
        if (servers.isEmpty()) {
            return 0;
        } else {
            return utilization / servers.size();
        }
    }

    public ConcurrentLinkedQueue<ServerHandler> getServers() {
        return servers;
    }

    public int getServerGameHeight() {
        return serverGameHeight;
    }

    public int getServerGameWidth() {
        return serverGameWidth;
    }

    public ClientConnector getClientConnector() {
        return clientConnector;
    }

    public ServerConnector getServerConnector() {
        return serverConnector;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public Planner getPlanner() {
        return planner;
    }

    public double getLastClientSwap() {
        return System.currentTimeMillis() - lastClientSwap;
    }

    public void setLastClientSwap() {
        this.lastClientSwap = System.currentTimeMillis();
    }

    public DockerHandler getDockerHandler() {
        return dockerHandler;
    }

    @Override
    public void update(Observable o, Object o1) {
        AsteroidsOperator.logger.log(FINE, "[Operator] Observed Update");
        setChanged();
        notifyObservers();
    }

}
