/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import server.network.basic.Address;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
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

    private ClientConnector clientConnector;
    private ServerConnector serverConnector;
    private ConcurrentLinkedQueue<ClientHandler> clients;
    private ConcurrentLinkedQueue<ServerHandler> servers;

    private int serverGameHeight;
    private int serverGameWidth;

    private Monitor monitor;
    private Planner planner;

    private boolean markedShutdown;

    public Operator() {
        AsteroidsOperator.logger.log(INFO, "[Operator] Create");
        clientConnector = new ClientConnector(this);
        serverConnector = new ServerConnector(this);

        clients = new ConcurrentLinkedQueue<>();
        servers = new ConcurrentLinkedQueue<>();

        serverGameHeight = 6400;
        serverGameWidth = 6400;

        monitor = new Monitor(this, 4, 48, 64, 5);
        planner = new Planner(this);

        markedShutdown = false;
    }

    public void start() {
        AsteroidsOperator.logger.log(INFO, "[Operator] Start");
        serverConnector.start();
        startServer();
        clientConnector.start();
    }

    public void addClient(ClientHandler clientData) {
        AsteroidsOperator.logger.log(INFO, "[Operator] Add Client");
        clients.add(clientData);
        setChanged();
        notifyObservers();
    }

    public void removeClient(ClientHandler clientHandler) {
        AsteroidsOperator.logger.log(INFO, "[Operator] Remove Client: {0}", clientHandler.getAddressConnectionServer());
        clients.remove(clientHandler);
        setChanged();
        notifyObservers();
    }

    public ClientHandler getClientHandler(Address clientAddress) {
        AsteroidsOperator.logger.log(INFO, "[Operator] Get Client: {0}", clientAddress);
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
        AsteroidsOperator.logger.log(INFO, "[Operator] Get Server with lowest Utility");
        ServerHandler lowestUtilityServer = null;
        Iterator<ServerHandler> it = servers.iterator();
        while (it.hasNext()) {
            ServerHandler serverData = it.next();
            if (serverData.isMarkedShutdown()) {
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

    public void startServer() {
        AsteroidsOperator.logger.log(INFO, "[Operator] Start Server");
        ServerStarter serverStarter = new ServerStarter(servers.size(), serverGameHeight, serverGameWidth, serverConnector.getAddress());
        serverStarter.start();
    }

    public void removeServer() {
        AsteroidsOperator.logger.log(INFO, "[Operator] Remove Server");
        ServerHandler lowestUtilityServer;
        Iterator<ServerHandler> it = servers.iterator();
        if (it.hasNext()) {
            lowestUtilityServer = it.next();
        } else {
            return;
        }
        while (it.hasNext()) {
            ServerHandler serverData = it.next();
            if (serverData.getUtility() < lowestUtilityServer.getUtility()) {
                lowestUtilityServer = serverData;
            }
        }
        lowestUtilityServer.markShutdown();
        setChanged();
        notifyObservers();
    }

    public void shutdown() {
        AsteroidsOperator.logger.log(INFO, "[Operator] Shutdown");
        markedShutdown = true;
        clientConnector.stopRunning();
        clientConnector.disconnect();
        serverConnector.stopRunning();
        serverConnector.disconnect();
        Iterator<ServerHandler> it = servers.iterator();
        while (it.hasNext()) {
            it.next().shutdown();
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

    @Override
    public void update(Observable o, Object o1) {
        AsteroidsOperator.logger.log(FINE, "[Operator] Observed Update");
        setChanged();
        notifyObservers();
    }

}
