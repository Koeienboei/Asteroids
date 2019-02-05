/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network;

import asteroidsserver.AsteroidsServer;
import java.util.Observable;
import java.util.Observer;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import server.network.packets.ClientStatePacket;
import server.ClientHandler;
import static server.ClientState.LOGOUT;
import server.Monitor;
import server.Server;
import server.network.basic.OutputHandler;
import server.network.packets.MonitorPacket;
import server.network.packets.ServerPacket;
import server.network.packets.ShutdownPacket;
import server.network.packets.ShutdownSuccesPacket;

/**
 *
 * @author Tom
 */
public class OperatorOutputHandler implements Observer {
    
    private Server server;
    private OperatorConnector operatorConnector;
    private OutputHandler output;
    
    public OperatorOutputHandler(OperatorConnector operatorConnector, Server server) {
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] Create");
        this.server = server;
        this.operatorConnector = operatorConnector;
        output = new OutputHandler(operatorConnector.getSocket());
    }

    public void sendMonitorPacket(Monitor monitor) {
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] Send MonitorPacket");
        MonitorPacket monitorPacket = new MonitorPacket(monitor);
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] Send {0}", monitorPacket);
        output.send(monitorPacket);
    }
    
    public void sendServerPacket() {
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Send ServerPacket");
        output.send(new ServerPacket(server.getClientConnector().getAddress(), server.getGame().getModel().getHeight(), server.getGame().getModel().getWidth()));
    }
    
    public void sendShutdownPacket() {
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] Send ShutdownPacket");
        output.send(new ShutdownPacket());
    }
    
    @Override
    public void update(Observable o, Object o1) {
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] Observed update");
        if (o1 instanceof ClientHandler) {
            ClientStatePacket clientStatePacket = new ClientStatePacket((ClientHandler) o1);
            if (clientStatePacket.getClientState() == LOGOUT) {
                AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Sending {0}", clientStatePacket);
            }
            output.send(clientStatePacket);
        }
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] End of observed update");
    }
    
    public void start() {
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] Start");
        server.addObserver(this);
    }
    
    public void stopRunning() {
        AsteroidsServer.logger.log(FINE, "[OperatorOutputHandler] Stop running");
        server.deleteObserver(this);
    }
    
}
