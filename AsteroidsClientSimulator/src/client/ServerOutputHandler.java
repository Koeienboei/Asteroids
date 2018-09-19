package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.updates.ControllerUpdate;
import packeges.LogoutPacket;
import packeges.UpdatePacket;

/**
 * This class represents an OutputHandler for the Server to send packages
 *
 * @author Tom
 */
public class ServerOutputHandler extends Thread implements Observer {

    private final Client client;
    private ObjectOutputStream output;

    public ServerOutputHandler(Client client, Socket socket) {
        this.client = client;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Failed to open socket output stream of server");
        }
    }
    
    private void send(Object packet) {
        try {
            output.writeObject(packet);
        } catch (IOException ex) {
            System.err.println("Failed to send packet to server");
        }
    }
    
    public void logout() {
        send(new LogoutPacket());
    }
    
    public void close() {
        try {
            output.close();
        } catch (IOException ex) {
            System.err.println("Failed to close output stream of server");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        ControllerUpdate controllerUpdate = new ControllerUpdate(client.getSpaceship());
        send(new UpdatePacket(controllerUpdate));
    }
}
