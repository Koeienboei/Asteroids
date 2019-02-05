package client;

import asteroidsclient.AsteroidsClient;
import static client.ClientState.CLOSE;
import server.network.basic.Address;
import static client.ClientState.GET_SERVER;
import static client.ClientState.LOGIN;
import static client.ClientState.LOGOUT;
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
public class ClientBot extends Client {
    
    public ClientBot(Address operatorAddress) {
        super(operatorAddress);
        logger.log(FINE, "[ClientBot] Create");
    }
  
    @Override
    public void initialize(ServerPacket serverPacket) {
        logger.log(FINE, "[ClientBot] Initialize with ServerPacket");
        serverConnector.setServerAddress(serverPacket.getServerAddress());
    }
    
    @Override
    public void initialize(InitPacket initPacket) {
        logger.log(FINE, "[ClientBot] Initialize with InitPacket");
        if (initPacket.isLast()) {
            logger.log(FINE, "[ClientBot] Last InitPacket");
            spaceship = new Spaceship(initPacket.getSpaceshipUpdate(), null);
            spaceship.getSpaceshipController().addObserver(serverConnector.getOutput());
            this.setState(PLAYING);
            Thread controllerThread = new Thread(spaceship.getSpaceshipController());
            controllerThread.start();
        }
    }

    @Override
    public void update(UpdatePacket updatePacket) {}
    
    @Override
    public void logoutServer() {
        logger.log(FINE, "[ClientBot] Logout from Server");
        setState(LOGOUT);
        if (spaceship != null) {
            spaceship.getSpaceshipController().stopRunning();
        }
        serverConnector.logout();
    }
    
    @Override
    public void close() {
        logger.log(INFO, "[Client] Close");
        stopRunning();
        serverConnector.logout();
        operatorConnector.disconnect();
        setState(CLOSE);
    }    
}
