package operator.network;

import asteroidsoperator.AsteroidsOperator;
import java.io.IOException;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import operator.ClientHandler;
import operator.Operator;
import operator.ServerHandler;
import static server.ClientState.ALIVE;
import static server.ClientState.DEAD;
import static server.ClientState.LOGOUT;
import server.network.basic.InputHandler;
import server.network.packets.ClientStatePacket;
import server.network.packets.MonitorPacket;
import server.network.packets.Packet;
import server.network.packets.ServerPacket;
import server.network.packets.ShutdownPacket;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class ServerInputHandler extends Thread {

    private ServerHandler serverHandler;
    private final Operator operator;
    private InputHandler input;
    private volatile boolean running;

    public ServerInputHandler(ServerHandler serverHandler, Operator operator) {
        AsteroidsOperator.logger.log(FINE, "[ServerInputHandler] Create");
        this.serverHandler = serverHandler;
        this.operator = operator;
        try {
            this.input = new InputHandler(serverHandler.getSocket());
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerInputHandler] Failed to set up input stream {0}", ex.getMessage());
        }
        this.running = false;
    }

    @Override
    public void run() {
        AsteroidsOperator.logger.log(INFO, "[ServerInputHandler] Start");
        running = true;
        while (running) {
            Packet packet = null;
            try {
                packet = input.receive();
            } catch (IOException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ServerInputHandler] IOException on receive from {0}, {1}", new Object[]{serverHandler.getAddressForClient(), ex.getMessage()});
            } catch (ClassNotFoundException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ServerInputHandler] ClassNotFoundException on receive from {0}, {1}", new Object[]{serverHandler.getAddressForClient(), ex.getMessage()});
            } catch (ClassCastException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ServerInputHandler] ClassCastException on receive from {0}, {1}", new Object[]{serverHandler.getAddressForClient(), ex.getMessage()});
            }
            if (packet instanceof ServerPacket) {
                AsteroidsOperator.logger.log(FINE, "[ServerInputHandler] Received ServerPacket");
                serverHandler.initialize((ServerPacket) packet);
            } else if (packet instanceof ClientStatePacket) {
                AsteroidsOperator.logger.log(FINE, "[ServerInputHandler] Received ClientStatePacket");
                ClientStatePacket clientStatePacket = (ClientStatePacket) packet;
                ClientHandler clientHandler = operator.getClientHandler(clientStatePacket.getClientAddress());
                if (clientHandler != null) {
                    clientHandler.setState(clientStatePacket.getClientState());
                    if (clientStatePacket.getClientState() == LOGOUT) {
                        AsteroidsOperator.logger.log(FINE, "[ServerInputHandler] Received {0}", clientStatePacket);
                        serverHandler.removeClient(clientHandler);
                    }/* else if (serverHandler.isMarkedShutdown() && clientStatePacket.getClientState() == ALIVE) {
                        clientHandler.swap();
                    }*/ else if (serverHandler.isAboveAverage() && clientStatePacket.getClientState() == ALIVE) {
                        clientHandler.swap();
                    }
                }
            } else if (packet instanceof ShutdownPacket) {
                AsteroidsOperator.logger.log(FINE, "[ServerInputHandler] Received ShutdownPacket");
                serverHandler.shutdown();
            } else if (packet instanceof MonitorPacket) {
                serverHandler.addMonitorPacket((MonitorPacket) packet);
            }
        }
        AsteroidsOperator.logger.log(FINE, "[ServerInputHandler] End of run function");
    }

    public void stopRunning() {
        AsteroidsOperator.logger.log(INFO, "[ServerInputHandler] Stop running {0}", serverHandler.getAddressForClient());
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

}
