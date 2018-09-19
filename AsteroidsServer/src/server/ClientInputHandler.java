/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import packeges.UpdatePacket;
import static server.ClientState.ALIVE;
import static server.ClientState.DEAD;
import static server.ClientState.INITIALIZE;

/**
 *
 * @author Tom
 */
public class ClientInputHandler extends Thread {
    
    private Server server;
    private ClientData clientData;
    
    private Socket socket;
    private ObjectInputStream input;
    
    public ClientInputHandler(ClientData clientData, Server server, Socket socket) {
        this.server = server;
        this.clientData = clientData;
        this.socket = socket;
        
        try {
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("[ERROR] Failed to open socket input stream of client: " + clientData);
        }
    }
    
    private Object receive() {
        try {
            return input.readObject();
        } catch (Exception ex) {
            System.out.println("[ERROR] Failed to receive packet from client: " + clientData);
            clientData.logout();
            
        }
        return null;
    }
    
    private void update() {
        while (clientData.getState() == INITIALIZE || clientData.getState() == ALIVE || clientData.getState() == DEAD) {
            Object packet = receive();
            if (packet instanceof UpdatePacket && clientData.getState() == ALIVE) {
                UpdatePacket updatePacket = (UpdatePacket) packet;
                server.getGame().getModel().addUpdate(updatePacket.getUpdate());
                server.getGame().updateClientQueues(updatePacket.getUpdate());
            }
        }
    }
    
    public void close() {
        try {
            input.close();
        } catch (IOException ex) {
            System.out.println("[ERROR] Failed to close client input: " + clientData);
        }
    }
    
    @Override
    public void run() {
        update();
    }
    
    public ClientData getClientData() {
        return clientData;
    }
    
}
