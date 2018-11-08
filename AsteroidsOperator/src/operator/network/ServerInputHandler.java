package operator.network;

import asteroidsoperator.AsteroidsOperator;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import operator.ClientHandler;
import operator.Operator;
import operator.ServerHandler;
import server.network.basic.InputHandler;
import server.network.packets.ClientStatePacket;
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
        this.input = new InputHandler(serverHandler.getSocket());
        this.running = false;
    }

    @Override
    public void run() {
        AsteroidsOperator.logger.log(FINE, "[ServerInputHandler] Start");
        running = true;
        while (running) {
            Packet packet = input.receive();
            if (packet instanceof ServerPacket) {
                serverHandler.initialize((ServerPacket) packet);
            } else if (packet instanceof ClientStatePacket) {
                ClientStatePacket clientStatePacket = (ClientStatePacket) packet;
                ClientHandler clientHandler = operator.getClientHandler(clientStatePacket.getClientAddress());
                if (clientHandler != null) {
                    clientHandler.setState(clientStatePacket.getClientState());
                }
            } else if (packet instanceof ShutdownPacket) {
                serverHandler.shutdown();
            }
        }
        AsteroidsOperator.logger.log(INFO, "[ServerInputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsOperator.logger.log(INFO, "[ServerInputHandler] Stop running");
        running = false;
    }
    
}
