package server;

import server.network.ClientConnector;
import asteroidsserver.AsteroidsServer;
import server.network.basic.Address;
import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static server.ClientState.ALIVE;
import static server.ClientState.DEAD;
import server.network.OperatorConnector;

/**
 * This class represents a Server that can host an Asteroidsgame
 *
 * @author Tom
 */
public class Server extends Observable implements Observer {
    
    private ClientConnector clientConnector;
    private ConcurrentLinkedQueue<ClientHandler> clients;

    private OperatorConnector operatorConnector;

    private Game game;

    private Monitor monitor;
    
    private boolean markedShutdown;

    public Server(int height, int width, int amountAsteroids, Address operatorAddress) {
        AsteroidsServer.logger.log(INFO, "[Server] Create");
        initialize(height, width, amountAsteroids, operatorAddress);
    }

    private void initialize(int height, int width, int amountAsteroids, Address operatorAddress) {
        AsteroidsServer.logger.log(INFO, "[Server] Initialize");
        game = new Game(this, height, width, amountAsteroids);

        clients = new ConcurrentLinkedQueue<>();
        clientConnector = new ClientConnector(this);

        operatorConnector = new OperatorConnector(operatorAddress, this);
        
        monitor = new Monitor(this);
        game.addObserver(monitor);

        markedShutdown = false;
    }

    public void start() {
        AsteroidsServer.logger.log(INFO, "[Server] Start");
        connectToOperator();
        startGame();
        startClientConnector();
        monitor.start();
        System.out.println("Address for clients: " + clientConnector.getAddress());
        System.out.println("Address for operator: " + operatorConnector.getAddress());
    }

    public void shutdown() {
        AsteroidsServer.logger.log(FINE, "[Server] Shutdown");
        logoutClients();
        disconnectClientConnector();
        disconnectFromOperator();
        stopGame();
        System.exit(0);
    }

    private void startGame() {
        AsteroidsServer.logger.log(FINE, "[Server] Start game");
        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    private void startClientConnector() {
        AsteroidsServer.logger.log(FINE, "[Server] Start receiving logins");
        clientConnector.start();
    }

    private void disconnectClientConnector() {
        AsteroidsServer.logger.log(FINE, "[Server] Disconnect client connector");
        clientConnector.stopRunning();
        clientConnector.disconnect();
    }

    private void connectToOperator() {
        AsteroidsServer.logger.log(INFO, "[Server] Login to operator");
        operatorConnector.start();
    }

    private void disconnectFromOperator() {
        AsteroidsServer.logger.log(FINE, "[Server] Logout from operator");
        operatorConnector.stopRunning();
        operatorConnector.disconnect();
    }

    private void stopGame() {
        AsteroidsServer.logger.log(FINE, "[Server] Stop game");
        game.stopRunning();
    }

    public void addClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "[Server] Add Client");
        clients.add(clientHandler);
        setChanged();
        notifyObservers(clientHandler);
    }

    public void removeClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(FINE, "[Server] Remove Client");
        clients.remove(clientHandler);
        setChanged();
        notifyObservers();

        if (isMarkedShutdown() && isEmpty()) {
            AsteroidsServer.logger.log(FINE, "[Server] Is marked shutdown and empty");
            operatorConnector.sendShutdownPacket();
        }
    }

    public ClientHandler getClientHandler(Address clientAddress) {
        AsteroidsServer.logger.log(FINE, "[Server] Get ClientHandler");
        Iterator<ClientHandler> it = clients.iterator();
        while (it.hasNext()) {
            ClientHandler clientHandler = it.next();
            if (clientHandler.getAddress().equals(clientAddress)) {
                return clientHandler;
            }
        }
        AsteroidsServer.logger.log(SEVERE, "[Server] Failed to find ClientHandler");
        return null;
    }

    public void logoutClients() {
        AsteroidsServer.logger.log(FINE, "[Server] Logout clients");
        Iterator<ClientHandler> it = clients.iterator();
        while (it.hasNext()) {
            it.next().logout();
        }
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    public ClientConnector getClientConnector() {
        return clientConnector;
    }

    public OperatorConnector getOperatorConnector() {
        return operatorConnector;
    }

    public void markShutdown() {
        markedShutdown = true;
    }

    public boolean isMarkedShutdown() {
        return markedShutdown;
    }

    public int getOccupation() {
        return clients.size();
    }

    public Collection<ClientHandler> getClients() {
        return clients;
    }

    public Game getGame() {
        return game;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public void update(Observable o, Object o1) {
        AsteroidsServer.logger.log(FINE, "[Server] Observed update");
        if (o instanceof ClientHandler) {
            ClientHandler clientHandler = (ClientHandler) o;
            if (clientHandler.getClientState() == ALIVE) {
                game.updateClientQueues(clientHandler.getSpaceship().getUpdate());
            } else if (clientHandler.getClientState() == DEAD) {
                game.updateClientQueues(clientHandler.getSpaceship().getUpdate());
            }
            setChanged();
            AsteroidsServer.logger.log(FINE, "[Server] Before notify observers ({0})", countObservers());
            notifyObservers(clientHandler);
            AsteroidsServer.logger.log(FINE, "[Server] End of observed update");
        }
    }

}
