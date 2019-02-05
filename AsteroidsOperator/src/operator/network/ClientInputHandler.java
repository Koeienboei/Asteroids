package operator.network;

import asteroidsoperator.AsteroidsOperator;
import static java.util.logging.Level.FINE;
import operator.ClientHandler;
import server.network.packets.ClientPacket;
import server.network.basic.InputHandler;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ClientInputHandler extends Thread {

    private ClientHandler clientData;
    private InputHandler input;
    private volatile boolean running;

    public ClientInputHandler(ClientHandler clientData) {
        AsteroidsOperator.logger.log(FINE, "[ClientInputHandler] Create");
        this.clientData = clientData;
        this.input = new InputHandler(clientData.getSocket());
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsOperator.logger.log(FINE, "[ClientInputHandler] Start");
        running = true;
        while (running) {
            Object packet = input.receive();
            if (packet instanceof ClientPacket) {
                ClientPacket clientPacket = (ClientPacket) packet;
                clientData.setAddressConnectionServer(clientPacket.getClientAddress());
            }
        }
        AsteroidsOperator.logger.log(FINE, "[ClientInputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsOperator.logger.log(FINE, "[ClientInputHandler] Stop running");
        running = false;
    }

}
