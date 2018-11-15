package client;

import client.network.OperatorConnector;
import server.network.basic.Address;
import static client.ClientState.CLOSE;
import static client.ClientState.CONNECT;
import static client.ClientState.GET_SERVER;
import static client.ClientState.INITIALIZE;
import static client.ClientState.LOGIN;
import static client.ClientState.LOGOUT;
import client.network.ServerConnector;
import java.io.IOException;
import java.util.logging.FileHandler;
import static java.util.logging.Level.ALL;
import static java.util.logging.Level.FINE;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import model.Spaceship;
import server.network.packets.InitPacket;
import server.network.packets.ServerPacket;
import server.network.packets.UpdatePacket;

/**
 * This class represents a Server that can host an Asteroidsgame
 *
 * @author Tom
 */
public abstract class Client extends Thread {

    private long loggerId;
    public Logger logger;
    private FileHandler loggerFileHandler;

    protected Spaceship spaceship;
    protected volatile ClientState clientState;
    
    protected OperatorConnector operatorConnector;
    protected ServerConnector serverConnector;
    
    protected volatile boolean running;

    public Client(Address operatorAddress) {
        initializeLogger();
        logger.log(FINE, "[Client] Create");
        operatorConnector = new OperatorConnector(this, operatorAddress);
        serverConnector = new ServerConnector(this);
        running = false;
    }

    private void initializeLogger() {
        loggerId = System.currentTimeMillis();
        logger = Logger.getLogger("Client[" + loggerId + "]Logs");
        try {
            loggerFileHandler = new FileHandler("C:\\Users\\tomei\\Dropbox\\Bachelor project\\Version 2.4\\Client[" + loggerId + "]Logs.log");
            logger.addHandler(loggerFileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            loggerFileHandler.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
        }
        logger.setLevel(ALL);
    }

    @Override
    public void run() {
        logger.log(FINE, "[Client] Start");
        running = true;
        setState(CONNECT);
        operatorConnector.connect();
        setState(GET_SERVER);
        while(running) {
            ServerPacket serverPacket = operatorConnector.getInput().getServer();
            if (serverConnector.isLoggedIn()) {
                setState(LOGOUT);
                serverConnector.logout();
            }
            setState(INITIALIZE);
            initialize(serverPacket);
            setState(LOGIN);
            serverConnector.login();
            setState(INITIALIZE);
            serverConnector.getInput().start();
        }
    }

    public void stopRunning() {
        running = false;
    }
    
    public abstract void logoutServer();

    public void close() {
        logger.log(FINE, "[Client] Close");
        setState(CLOSE);
        this.stopRunning();
        serverConnector.logout();
        operatorConnector.disconnect();
        System.exit(0);
    }

    public abstract void initialize(ServerPacket serverPacket);

    public abstract void initialize(InitPacket initPacket);

    public abstract void update(UpdatePacket updatePacket);

    public ClientState getClientState() {
        return clientState;
    }

    public void setState(ClientState clientState) {
        logger.log(FINE, "[Client] Change state to: {0}", clientState);
        this.clientState = clientState;
    }

    public Spaceship getSpaceship() {
        return spaceship;
    }

    public OperatorConnector getOperatorConnector() {
        return operatorConnector;
    }

    public ServerConnector getServerConnector() {
        return serverConnector;
    }

}
