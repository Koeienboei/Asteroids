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
import static java.util.logging.Level.INFO;
import server.network.packets.ClientStatePacket;
import server.ClientHandler;
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
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Create");
        this.server = server;
        this.operatorConnector = operatorConnector;
        output = new OutputHandler(operatorConnector.getSocket());
    }

    public void sendMonitorPacket(Monitor monitor) {
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Send MonitorPacket");
        output.send(new MonitorPacket(monitor));
    }
    
    public void sendServerPacket() {
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Send ServerPacket");
        output.send(new ServerPacket(server.getClientConnector().getAddress(), server.getGame().getModel().getHeight(), server.getGame().getModel().getWidth()));
    }
    
    public void sendShutdownPacket() {
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Send ShutdownPacket");
        output.send(new ShutdownPacket());
    }
    
    @Override
    public void update(Observable o, Object o1) {
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Observed update");
        if (o1 instanceof ClientHandler) {
            output.send(new ClientStatePacket((ClientHandler) o1));
        }
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] End of observed update");
    }
    
    public void start() {
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Start");
        server.addObserver(this);
    }
    
    public void stopRunning() {
        AsteroidsServer.logger.log(INFO, "[OperatorOutputHandler] Stop running");
        server.deleteObserver(this);
    }
    
}
