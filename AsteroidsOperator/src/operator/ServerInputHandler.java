package operator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import packeges.Address;
import packeges.ClientStatePacket;
import packeges.ServerPacket;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ServerInputHandler extends Thread {

    private ServerData serverData;
    private final Operator operator;
    private ObjectInputStream input;

    public ServerInputHandler(ServerData serverData, Operator operator, Socket socket) {
        this.serverData = serverData;
        this.operator = operator;
        
        try {
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Failed to open socket input stream of server");
        }
    }

    private Object receive() {
        try {
            return input.readObject();
        } catch (Exception ex) {
            System.err.println("Failed to receive packet from server");
        }
        return null;
    }

    @Override
    public void run() {
        while (operator.isRunning()) {
            Object packet = receive();
            if (packet instanceof ServerPacket) {
                serverData.initialize((ServerPacket) packet);
            } else if (packet instanceof ClientStatePacket) {
                operator.changeClientState((ClientStatePacket) packet);
            } 
        }
    }
    
}
