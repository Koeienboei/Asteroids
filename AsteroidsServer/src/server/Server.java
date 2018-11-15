package server;

import server.network.ClientConnector;
import asteroidsserver.AsteroidsServer;
import static asteroidsserver.AsteroidsServer.logger;
import controller.MainFrame;
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

    private MainFrame mainFrame;
    private Game game;

    private boolean markedShutdown;

    public Server(MainFrame mainFrame, int height, int width, Address operatorAddress) {
        AsteroidsServer.logger.log(INFO, "[Server] Create");
        this.mainFrame = mainFrame;
        initialize(height, width, operatorAddress);
    }

    private void initialize(int height, int width, Address operatorAddress) {
        AsteroidsServer.logger.log(FINE, "[Server] Initialize");
        game = new Game(this, height, width, 256);

        clients = new ConcurrentLinkedQueue<>();
        clientConnector = new ClientConnector(this);

        operatorConnector = new OperatorConnector(operatorAddress, this);

        markedShutdown = false;
    }

    public void start() {
        AsteroidsServer.logger.log(INFO, "[Server] Start");
        connectToOperator();
        startGame();
        startClientConnector();
    }

    public void shutdown() {
        AsteroidsServer.logger.log(INFO, "[Server] Shutdown");
        logoutClients();;
        disconnectClientConnector();
        disconnectFromOperator();
        stopGame();
        System.exit(0);
    }

    private void startGame() {
        AsteroidsServer.logger.log(INFO, "[Server] Start game");
        Thread gameThread = new Thread(game);
        gameThread.start();
    }

    private void startClientConnector() {
        AsteroidsServer.logger.log(INFO, "[Server] Start receiving logins");
        clientConnector.start();
    }

    private void disconnectClientConnector() {
        AsteroidsServer.logger.log(INFO, "[Server] Disconnect client connector");
        clientConnector.stopRunning();
        clientConnector.disconnect();
    }

    private void connectToOperator() {
        AsteroidsServer.logger.log(INFO, "[Server] Login to operator");
        operatorConnector.start();
    }

    private void disconnectFromOperator() {
        AsteroidsServer.logger.log(INFO, "[Server] Logout from operator");
        operatorConnector.stopRunning();
        operatorConnector.disconnect();
    }

    private void stopGame() {
        AsteroidsServer.logger.log(INFO, "[Server] Stop game");
        game.stopRunning();
    }

    public void addClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(INFO, "[Server] Add Client");
        clients.add(clientHandler);
        setChanged();
        notifyObservers(clientHandler);
    }

    public void removeClient(ClientHandler clientHandler) {
        AsteroidsServer.logger.log(INFO, "[Server] Remove Client");
        clients.remove(clientHandler);
        setChanged();
        notifyObservers();

        if (isMarkedShutdown() && isEmpty()) {
            AsteroidsServer.logger.log(INFO, "[Server] Is marked shutdown and empty");
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
        AsteroidsServer.logger.log(INFO, "[Server] Logout clients");
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

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    @Override
    public void update(Observable o, Object o1) {
        AsteroidsServer.logger.log(INFO, "[Server] Observed update");
        if (o instanceof ClientHandler) {
            ClientHandler clientHandler = (ClientHandler) o;
            if (clientHandler.getState() == ALIVE) {
                game.updateClientQueues(clientHandler.getSpaceship().getUpdate());
            } else if (clientHandler.getState() == DEAD) {
                game.updateClientQueues(clientHandler.getSpaceship().getUpdate());
            }
            setChanged();
            AsteroidsServer.logger.log(INFO, "[Server] Before notify observers ({0})", countObservers());
            notifyObservers(clientHandler);
            AsteroidsServer.logger.log(INFO, "[Server] End of observed update");
        }
    }

}
