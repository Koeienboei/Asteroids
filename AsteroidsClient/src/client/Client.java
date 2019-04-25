package client;

import client.network.OperatorConnector;
import server.network.basic.Address;
import static client.ClientState.CONNECT;
import static client.ClientState.GET_SERVER;
import client.network.ServerConnector;
import java.io.IOException;
import java.util.logging.FileHandler;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.OFF;
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

    public Client(Address operatorAddress) {
        initializeLogger();
        logger.log(INFO, "[Client] Create");
        operatorConnector = new OperatorConnector(this, operatorAddress);
        serverConnector = new ServerConnector(this);
    }

    private void initializeLogger() {
        loggerId = System.currentTimeMillis();
        logger = Logger.getLogger("Client[" + loggerId + "]Logs");
        /*try {
            loggerFileHandler = new FileHandler("Client[" + loggerId + "]Logs.log");
            logger.addHandler(loggerFileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            loggerFileHandler.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
        }*/
        logger.setLevel(OFF);
    }

    @Override
    public void run() {
        logger.log(INFO, "[Client] Start");
        operatorConnector.connect();
    }

    public abstract void logoutServer();

    public abstract void close();

    public abstract void loginServer(ServerPacket serverPacket);

    public abstract void initialize(InitPacket initPacket);

    public abstract void update(UpdatePacket updatePacket);

    public ClientState getClientState() {
        return clientState;
    }

    public void setState(ClientState clientState) {
        logger.log(INFO, "[Client] Change state to: {0}", clientState);
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
