/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import packeges.Address;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import packeges.ClientStatePacket;

/**
 *
 * @author tomei
 */
public class Operator extends Observable implements Observer {

    private ClientHandler clientHandler;
    private ServerHandler serverHandler;
    private Collection<ClientData> clients;
    private Collection<ServerData> servers;

    private volatile boolean running;

    private int ulow;
    private int uhigh;

    private int serverGameHeight;
    private int serverGameWidth;

    private Monitor monitor;
    private Planner planner;

    public Operator() {
        clientHandler = new ClientHandler(this);
        serverHandler = new ServerHandler(this);

        clients = Collections.synchronizedList(new LinkedList<ClientData>());
        servers = Collections.synchronizedList(new LinkedList<ServerData>());

        ulow = 30;
        uhigh = 300;
        serverGameHeight = 6400;
        serverGameWidth = 6400;

        monitor = new Monitor(this);
        planner = new Planner(this);
    }

    public void start() {
        running = true;
        serverHandler.start();
        startServer();
        clientHandler.start();
    }

    public void addClient(ClientData clientData) {
        clients.add(clientData);
        System.out.println("Adding client: " + clientData);
        setChanged();
        notifyObservers();
    }
    
    public void removeClient(Address clientAddress) {
        Iterator<ClientData> it = clients.iterator();
        while (it.hasNext()) {
            ClientData clientData = it.next();
            if (clientData.getAddress().equals(clientAddress)) {
                it.remove();
                setChanged();
                notifyObservers();
            }
        }
    }

    public ClientData getClientData(Address clientAddress) {
        Iterator<ClientData> it = clients.iterator();
        while (it.hasNext()) {
            ClientData clientData = it.next();
            if (clientData.getAddress().equals(clientAddress)) {
                return clientData;
            }
        }
        return null;
    }

    public void changeClientState(ClientStatePacket clientStatePacket) {
        /*ClientData clientData = getClientData(clientStatePacket.getClientAddress());
        if (clientData == null) {
            System.err.println("Trying to change client state that does not exist");
        }
        if (clientStatePacket.getClientState() == LOGOUT) {
            clientData.close();
            removeClient(clientStatePacket.getClientAddress());
        } else {
            clientData.setState(clientStatePacket.getClientState());
        }
        setChanged();
        notifyObservers();*/
    }

    public void addServer(ServerData serverData) {
        servers.add(serverData);
        serverData.addObserver(this);
        serverData.getInput().start();
        setChanged();
        notifyObservers();
    }
    
    public void removeServer(Address serverAddress) {
        Iterator<ServerData> it = servers.iterator();
        while (it.hasNext()) {
            ServerData serverData = it.next();
            if (serverData.getAddressForOperator().equals(serverAddress)) {
                it.remove();
                setChanged();
                notifyObservers();
            }
        }
    }
    
    public ServerData getServer() {
        ServerData lowestUtilityServer = null;
        Iterator<ServerData> it = servers.iterator();
        if (it.hasNext()) {
            lowestUtilityServer = it.next();
        }
        while (it.hasNext()) {
            ServerData server = it.next();
            if (server.getUtility() < lowestUtilityServer.getUtility()) {
                lowestUtilityServer = server;
            }
        }
        return lowestUtilityServer;
    }

    public void startServer() {
        ServerStarter serverStarter = new ServerStarter(servers.size(), serverGameHeight, serverGameWidth, serverHandler.getAddress());
        serverStarter.start();
    }

    public void removeServer() {
        ServerData lowestUtilityServer;
        Iterator<ServerData> it = servers.iterator();
        if (it.hasNext()) {
            lowestUtilityServer = it.next();
        } else {
            return;
        }
        while (it.hasNext()) {
            ServerData serverData = it.next();
            if (serverData.getUtility() < lowestUtilityServer.getUtility()) {
                lowestUtilityServer = serverData;
            }
        }
        lowestUtilityServer.shutdown();
        setChanged();
        notifyObservers();
    }

    public void close() {
        running = false;
        clientHandler.close();
        serverHandler.close();
        System.exit(0);
    }

    public Collection<ServerData> getServerDataList() {
        return servers;
    }

    public boolean isRunning() {
        return running;
    }

    public float getUtilization() {
        int utilization = 0;
        Iterator<ServerData> it = servers.iterator();
        while (it.hasNext()) {
            utilization = +it.next().getUtility();
        }
        if (servers.isEmpty()) {
            return 0;
        } else {
            return utilization / servers.size();
        }
    }

    public int getServerGameHeight() {
        return serverGameHeight;
    }

    public int getServerGameWidth() {
        return serverGameWidth;
    }

    public int getUlow() {
        return ulow;
    }

    public int getUhigh() {
        return uhigh;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    @Override
    public void update(Observable o, Object o1) {
        setChanged();
        notifyObservers();
    }

}
