package server;

import packeges.Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;
import packeges.ServerPacket;
import static server.ClientState.ALIVE;
import static server.ClientState.DEAD;

/**
 * This class represents a Server that can host an Asteroidsgame
 *
 * @author Tom
 */
public class Server extends Observable implements Observer {

    private InputHandler inputHandler;

    private Address operatorAddress;
    private OperatorHandler operatorHandler;

    private ConcurrentLinkedQueue<ClientData> clients;

    private Game game;

    private volatile boolean running;

    public Server(int height, int width, Address operatorAddress) {
        this.operatorAddress = operatorAddress;
        initialize(height, width);
        start();
    }

    private void initialize(int height, int width) {
        game = new Game(this, height, width, 256);
        clients = new ConcurrentLinkedQueue<>();
        inputHandler = new InputHandler(this);
        
        operatorHandler = new OperatorHandler(operatorAddress);
        operatorHandler.send(new ServerPacket(inputHandler.getAddress(), height, width));
        this.addObserver(operatorHandler);
        
        running = false;
    }

    private void start() {
        running = true;
        Thread gameThread = new Thread(game);
        gameThread.start();
        inputHandler.start();
    }

    private String initServerIp() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            System.out.println("Failed to get local ip");
        }
        return ip;
    }

    public void close() {
        running = false;
        inputHandler.close();
        operatorHandler.close();
    }

    public void addClient(Socket socket) {
        ClientData clientData = new ClientData(socket, this);
        clients.add(clientData);
        clientData.addObserver(this);
        clientData.getOutput().start();
        clientData.getInput().start();
        setChanged();
        notifyObservers(clientData);
    }

    public void removeClient(Address clientAddress) {
        Iterator<ClientData> it = clients.iterator();
        while (it.hasNext()) {
            ClientData clientData = it.next();
            if (clientData.getAddress()== clientAddress) {
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

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public OperatorHandler getOperatorHandler() {
        return operatorHandler;
    }

    public int getOccupation() {
        return clients.size();
    }

    public Collection<ClientData> getClients() {
        return clients;
    }

    public Address getOperatorAddress() {
        return operatorAddress;
    }

    public Game getGame() {
        return game;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void update(Observable o, Object clientData) {
        if (clientData instanceof ClientData) {
            ClientData cd = (ClientData) clientData;
            if (cd.getState() == ALIVE) {
                cd.getOutput().getUpdateQueue().reset();
                game.updateClientQueues(cd.getSpaceship().getUpdate());
            } else if (cd.getState() == DEAD) {
                game.updateClientQueues(cd.getSpaceship().getUpdate());
            }
        }
        setChanged();
        notifyObservers(clientData);
    }

}
