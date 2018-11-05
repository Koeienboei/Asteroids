/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network;

import asteroidsserver.AsteroidsServer;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import server.Server;
import server.network.basic.InputHandler;
import server.network.packets.MarkShutdownPacket;
import server.network.packets.Packet;
import server.network.packets.ShutdownPacket;

/**
 *
 * @author Tom
 */
public class OperatorInputHandler extends Thread {
    
    private Server server;
    private OperatorConnector operatorConnector;
    private InputHandler input;
    private volatile boolean running;
    
    public OperatorInputHandler(OperatorConnector operatorConnector, Server server) {
        AsteroidsServer.logger.log(INFO, "[OperatorInputHandler] Create");
        this.server = server;
        this.operatorConnector = operatorConnector;
        this.input = new InputHandler(operatorConnector.getSocket());
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsServer.logger.log(INFO, "[OperatorInputHandler] Start");
        running = true;
        while (running) {
            Packet packet = input.receive();
            if (packet instanceof MarkShutdownPacket) {
                server.markShutdown();
            }
            if (packet instanceof ShutdownPacket) {
                server.shutdown();
            }
        }
        AsteroidsServer.logger.log(INFO, "[OperatorInputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsServer.logger.log(INFO, "[OperatorInputHandler] Stop running");
        running = false;
    }
    
}
