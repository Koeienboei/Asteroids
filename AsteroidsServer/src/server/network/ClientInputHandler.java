/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network;

import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
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
        AsteroidsServer.logger.log(FINE, "Create ClientInputHandler");
        this.server = server;
        this.clientHandler = clientHandler;

        try {
            input = new InputHandler(clientHandler.getSocket());
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[ClientInputHandler] Failed to set up input stream {0}", ex.getMessage());
        }
        
        this.running = false;
    }

    private void update() {
        AsteroidsServer.logger.log(FINE, "[ClientInputHandler] Start receiving updates from Client");
        while (running) {
            Packet packet = null;
            try {
                packet = input.receive();
            } catch (IOException ex) {
                AsteroidsServer.logger.log(SEVERE, "[ClientInputHandler] IOException on receive from {0}, {1}", new Object[] {clientHandler.getAddress(), ex.getMessage()});
            } catch (ClassNotFoundException ex) {
                AsteroidsServer.logger.log(SEVERE, "[ClientInputHandler] ClassNotFoundException on receive from {0}, {1}", new Object[] {clientHandler.getAddress(), ex.getMessage()});
            } catch (ClassCastException ex) {
                AsteroidsServer.logger.log(SEVERE, "[ClientInputHandler] ClassCastException on receive from {0}, {1}", new Object[] {clientHandler.getAddress(), ex.getMessage()});
            }
            if (packet instanceof UpdatePacket && clientHandler.getClientState() == ALIVE) {
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
        AsteroidsServer.logger.log(FINE, "Start ClientInputHandler");
        running = true;
        update();
        AsteroidsServer.logger.log(FINE, "[ClientInputHandler] End of run function");
    }
    
    public void stopRunning() {
        AsteroidsServer.logger.log(FINE, "[ClientInputHandler] Stop running");
        running = false;
    }

}
