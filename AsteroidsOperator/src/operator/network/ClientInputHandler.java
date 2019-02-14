package operator.network;

import asteroidsoperator.AsteroidsOperator;
import java.io.IOException;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import operator.ClientHandler;
import server.network.packets.ClientPacket;
import server.network.basic.InputHandler;
import server.network.packets.LogoutPacket;
import server.network.packets.Packet;

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
        try {
            this.input = new InputHandler(clientData.getSocket());
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ClientInputHandler] Failed to set up input stream {0}", ex.getMessage());
        }
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsOperator.logger.log(FINE, "[ClientInputHandler] Start");
        running = true;
        while (running) {
            Packet packet = null;
            try {
                packet = input.receive();
            } catch (IOException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ClientInputHandler] IOException on receive from {0}, {1}", new Object[] {clientData.getAddressConnectionServer(), ex.getMessage()});
            } catch (ClassNotFoundException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ClientInputHandler] ClassNotFoundException on receive from {0}, {1}", new Object[] {clientData.getAddressConnectionServer(), ex.getMessage()});
            } catch (ClassCastException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ClientInputHandler] ClassCastException on receive from {0}, {1}", new Object[] {clientData.getAddressConnectionServer(), ex.getMessage()});
            }
            if (packet instanceof ClientPacket) {
                ClientPacket clientPacket = (ClientPacket) packet;
                clientData.setAddressConnectionServer(clientPacket.getClientAddress());
            }
            if (packet instanceof LogoutPacket) {
                clientData.logout();
            }
        }
        AsteroidsOperator.logger.log(FINE, "[ClientInputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsOperator.logger.log(FINE, "[ClientInputHandler] Stop running");
        running = false;
    }

}
