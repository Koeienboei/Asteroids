/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network;

import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import java.io.ObjectInputStream;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import server.ClientHandler;
import server.network.packets.LogoutPacket;
import server.network.packets.Packet;
import server.network.packets.UpdatePacket;
import server.Server;
import static server.ClientState.ALIVE;
import server.network.basic.InputHandler;

/**
 *
 * @author Tom
 */
public class ClientInputHandler extends Thread {

    private Server server;
    private ClientHandler clientHandler;

    private InputHandler input;
    private volatile boolean running;

    public ClientInputHandler(ClientHandler clientHandler, Server server) {
        AsteroidsServer.logger.log(INFO, "Create ClientInputHandler");
        this.server = server;
        this.clientHandler = clientHandler;

        input = new InputHandler(clientHandler.getSocket());
        
        this.running = false;
    }

    private void update() {
        AsteroidsServer.logger.log(INFO, "Start receiving updates from Client");
        while (running) {
            Packet packet = input.receive();
            if (packet instanceof UpdatePacket && clientHandler.getState() == ALIVE) {
                UpdatePacket updatePacket = (UpdatePacket) packet;
                server.getGame().getModel().addUpdate(updatePacket.getUpdate());
                server.getGame().updateClientQueues(updatePacket.getUpdate());
            }
            if (packet instanceof LogoutPacket) {
                clientHandler.logout();
            }
        }
    }

    @Override
    public void run() {
        AsteroidsServer.logger.log(INFO, "Start ClientInputHandler");
        running = true;
        update();
        AsteroidsServer.logger.log(INFO, "[ClientInputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsServer.logger.log(INFO, "[ClientInputHandler] Stop running");
        running = false;
    }

}
