package client;

import packeges.Address;
import static client.ClientState.CLOSE;
import static client.ClientState.GET_SERVER;
import static client.ClientState.LOGIN;
import static client.ClientState.LOGOUT;
import static client.ClientState.PLAYING;
import controller.MainFrame;
import model.Spaceship;
import packeges.InitPacket;
import packeges.ServerPacket;
import packeges.UpdatePacket;

/**
 * This class represents a Server that can host an Asteroidsgame
 *
 * @author Tom
 */
public class Client {
    
    private Game game;
    private Spaceship spaceship;
    private ClientState clientState;

    private OperatorHandler operatorHandler;
    private ServerHandler serverHandler;
    
    private Address operatorAddress;
    private Address serverAddress;
    
    public Client(Address operatorAddress) {
        this.operatorAddress = operatorAddress;
        
        initialize();
    }
    
    private void initialize() {
        game = new Game(this);
        operatorHandler = new OperatorHandler(this, operatorAddress);
    }
    
    public void start() {
        getServer();
        operatorHandler.start();
    }
    
    public void getServer() {
        setState(GET_SERVER);
        operatorHandler.connect();
    }
    
    public void login() {
        setState(LOGIN);
        serverHandler.login();
    }
    
    public void logout() {
        setState(LOGOUT);
        serverHandler.logout();
    }
    
    public void close() {
        setState(CLOSE);
        serverHandler.close();
        operatorHandler.close();
    }
  
    public void initialize(ServerPacket serverPacket) {
        serverAddress = serverPacket.getServerAddress();
        serverHandler = new ServerHandler(this, serverAddress);
        
        game.initModel(serverPacket.getHeight(), serverPacket.getWidth());
        
        login();
        serverHandler.getInput().start();
    }
    
    public void initialize(InitPacket initPacket) {
        if (initPacket.isLast()) {
            spaceship = new Spaceship(initPacket.getSpaceshipUpdate(), game.getModel());
            game.getModel().addGameObject(spaceship);
            spaceship.getSpaceshipController().addObserver(serverHandler.getOutput());
            this.setState(PLAYING);
            Thread controllerThread = new Thread(spaceship.getSpaceshipController());
            controllerThread.start();
        }
    }

    public void update(UpdatePacket updatePacket) {
        
    }

    public ClientState getClientState() {
        return clientState;
    }

    public void setState(ClientState clientState) {
        this.clientState = clientState;
    }

    public Address getOperatorAddress() {
        return operatorAddress;
    }

    public Address getServerAddress() {
        return serverAddress;
    }
    
    public Spaceship getSpaceship() {
        return spaceship;
    }

    public Game getGame() {
        return game;
    }
    
}
