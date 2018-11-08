package client.network;

import asteroidsclient.AsteroidsClient;
import client.Client;
import static client.ClientState.CLOSE;
import client.network.basic.OutputHandler;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import model.updates.ControllerUpdate;
import server.network.packets.LogoutPacket;
import server.network.packets.Packet;
import server.network.packets.UpdatePacket;

/**
 * This class represents an OutputHandler for the Server to send packages
 *
 * @author Tom
 */
public class ServerOutputHandler extends Thread implements Observer {

    private final Client client;
    private OutputHandler output;

    public ServerOutputHandler(Client client, Socket socket) {
        client.logger.log(FINE, "[ServerOutputHandler] Create");
        this.client = client;
        this.output = new OutputHandler(socket, client);
    }
    
    public void sendLogoutPacket() {
        output.send(new LogoutPacket());
    }
    
    @Override
    public void update(Observable o, Object arg) {
        client.logger.log(FINE, "[ServerOutputHandler] Observed update");
        output.send(new UpdatePacket(new ControllerUpdate(client.getSpaceship())));
    }
    
    public void stopRunning() {
        client.getSpaceship().getSpaceshipController().deleteObservers();
    }
}
