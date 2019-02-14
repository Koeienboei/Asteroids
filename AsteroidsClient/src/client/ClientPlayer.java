package client;

import static client.ClientState.CLOSE;
import static client.ClientState.LOGOUT;
import server.network.basic.Address;
import static client.ClientState.PLAYING;
import controller.MainFrame;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import model.Spaceship;
import server.network.packets.InitPacket;
import server.network.packets.ServerPacket;
import server.network.packets.UpdatePacket;

/**
 * This class represents a Server that can host an Asteroidsgame
 *
 * @author Tom
 */
public class ClientPlayer extends Client {

    private MainFrame mainFrame;
    private Game game;
    
    public ClientPlayer(Address operatorAddress, MainFrame mainFrame) {
        super(operatorAddress);
        logger.log(FINE, "[ClientPlayer] Create");
        this.mainFrame = mainFrame;
        initialize();
    }
    
    private void initialize() {
        logger.log(FINE, "[ClientPlayer] Initialize");
        game = new Game(this);
        mainFrame.getGamePanel().setClient(this);
    }
  
    @Override
    public void loginServer(ServerPacket serverPacket) {
        logger.log(FINE, "[ClientPlayer] Initialize with ServerPacket");
        serverConnector.setServerAddress(serverPacket.getServerAddress());
        
        game.initModel(serverPacket.getHeight(), serverPacket.getWidth());
        mainFrame.getGamePanel().setGame(game);
        Thread gameThread = new Thread(game);
        gameThread.start();
        
        serverConnector.login();
    }
    
    @Override
    public void initialize(InitPacket initPacket) {
        logger.log(FINE, "[ClientPlayer] Initialize with InitPacket");
        if (!initPacket.isLast()) {
            logger.log(FINE, "[ClientPlayer] Not last InitPacket");
            game.getModel().addUpdates(initPacket.getUpdates());
        } else {
            logger.log(FINE, "[ClientPlayer] Last InitPacket");
            spaceship = new Spaceship(initPacket.getSpaceshipUpdate(), game.getModel());
            game.getModel().addGameObject(spaceship);
            mainFrame.getGamePanel().addKeyListener(spaceship.getSpaceshipController());
            spaceship.getSpaceshipController().addObserver(serverConnector.getOutput());
            this.setState(PLAYING);
        }
    }

    @Override
    public void update(UpdatePacket updatePacket) {
        logger.log(FINE, "[ClientPlayer] Update with {0}", updatePacket);
        game.getModel().addUpdates(updatePacket.getUpdates());
    }
    
    @Override
    public void logoutServer() {
        logger.log(FINE, "[ClientPlayer] Logout from Server");
        setState(LOGOUT);
        game.stopRunning();
        serverConnector.logout();
    }
    
    @Override
    public void close() {
        logger.log(INFO, "[Client] Close");
        setState(CLOSE);
        logoutServer();
        operatorConnector.logout();
        System.exit(0);
    }
    
    public Game getGame() {
        return game;
    }
    
}
