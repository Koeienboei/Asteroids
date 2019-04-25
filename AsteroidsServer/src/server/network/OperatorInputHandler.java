/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network;

import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
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
        AsteroidsServer.logger.log(FINE, "[OperatorInputHandler] Create");
        this.server = server;
        this.operatorConnector = operatorConnector;
        try {
            this.input = new InputHandler(operatorConnector.getSocket());
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[OperatorInputHandler] Failed to set up input stream {0}", ex.getMessage());
        }
        this.running = false;
    }
    
    @Override
    public void run() {
        AsteroidsServer.logger.log(FINE, "[OperatorInputHandler] Start");
        running = true;
        while (running) {
            AsteroidsServer.logger.log(FINE, "[OperatorInputHandler] Receive packet");
            Packet packet = null;
            try {
                packet = input.receive();
            } catch (IOException ex) {
                AsteroidsServer.logger.log(SEVERE, "[OperatorInputHandler] IOException on receive {0}", ex.getMessage());
            } catch (ClassNotFoundException ex) {
                AsteroidsServer.logger.log(SEVERE, "[OperatorInputHandler] ClassNotFoundException on receive {0}", ex.getMessage());
            } catch (ClassCastException ex) {
                AsteroidsServer.logger.log(SEVERE, "[OperatorInputHandler] ClassCastException on receive {0}", ex.getMessage());
            }
            if (packet instanceof MarkShutdownPacket) {
                AsteroidsServer.logger.log(FINE, "[OperatorInputHandler] Mark for shutdown packet received");
                server.markShutdown();
            }
            if (packet instanceof ShutdownPacket) {
                AsteroidsServer.logger.log(FINE, "[OperatorInputHandler] Shutdown packet received");
                server.shutdown();
            }
        }
        AsteroidsServer.logger.log(FINE, "[OperatorInputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsServer.logger.log(FINE, "[OperatorInputHandler] Stop running");
        running = false;
    }
    
}
