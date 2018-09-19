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

    private MainFrame mainFrame;
    
    private Game game;
    private Spaceship spaceship;
    private ClientState clientState;

    private OperatorHandler operatorHandler;
    private ServerHandler serverHandler;
    
    private Address operatorAddress;
    private Address serverAddress;
    
    public Client(Address operatorAddress, MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.operatorAddress = operatorAddress;
        
        initialize();
    }
    
    private void initialize() {
        game = new Game(this);
        mainFrame.getGamePanel().setClient(this);
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
        System.out.println("Initializing with server packet: " + serverPacket.getServerAddress() + " (" + serverPacket.getHeight() + "x" + serverPacket.getWidth() + ")");
        serverAddress = serverPacket.getServerAddress();
        serverHandler = new ServerHandler(this, serverAddress);
        
        game.initModel(serverPacket.getHeight(), serverPacket.getWidth());
        mainFrame.getGamePanel().setGame(game);
        
        login();
        serverHandler.getInput().start();
        
        Thread gameThread = new Thread(game);
        gameThread.start();
    }
    
    public void initialize(InitPacket initPacket) {
        if (!initPacket.isLast()) {
            game.getModel().addUpdates(initPacket.getUpdates());
        } else {
            spaceship = new Spaceship(initPacket.getSpaceshipUpdate(), game.getModel());
            game.getModel().addGameObject(spaceship);
            mainFrame.getGamePanel().addKeyListener(spaceship.getSpaceshipController());
            spaceship.getSpaceshipController().addObserver(serverHandler.getOutput());
            this.setState(PLAYING);
        }
    }

    public void update(UpdatePacket updatePacket) {
        //System.out.println(updatePacket);
        game.getModel().addUpdates(updatePacket.getUpdates());
    }

    public ClientState getClientState() {
        return clientState;
    }

    public void setState(ClientState clientState) {
        //System.out.println("Client state is now: " + clientState);
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
