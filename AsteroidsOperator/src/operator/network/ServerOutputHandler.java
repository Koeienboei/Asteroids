/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator.network;

import asteroidsoperator.AsteroidsOperator;
import operator.Operator;
import operator.ServerHandler;
import static java.util.logging.Level.FINE;
import server.network.basic.OutputHandler;
import server.network.packets.MarkShutdownPacket;
import server.network.packets.ShutdownPacket;

/**
 *
 * @author Tom
 */
public class ServerOutputHandler extends Thread {
    
    private ServerHandler serverData;
    private Operator operator;
    private OutputHandler output;
    
    public ServerOutputHandler(ServerHandler serverData, Operator operator) {
        AsteroidsOperator.logger.log(FINE, "[ServerOutputHandler] Create");
        this.serverData = serverData;
        this.operator = operator;
        this.output = new OutputHandler(serverData.getSocket());
    }
    
    public void sendMarkShutdownPacket() {
        output.send(new MarkShutdownPacket());
    }
    
    public void sendShutdownPacket() {
        AsteroidsOperator.logger.log(FINE, "[ServerOutputHandler] Send shutdown packet");
        output.send(new ShutdownPacket());
    }
    
}
