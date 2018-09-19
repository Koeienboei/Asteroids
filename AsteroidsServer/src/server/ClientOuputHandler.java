/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import model.GameObject;
import model.updates.SpaceshipUpdate;
import model.updates.Update;
import packeges.InitPacket;
import packeges.UpdatePacket;
import static server.ClientState.ALIVE;
import static server.ClientState.DEAD;
import static server.ClientState.INITIALIZE;

/**
 *
 * @author Tom
 */
public class ClientOuputHandler extends Thread {
    
    private Server server;
    private ClientData clientData;
    
    private Socket socket;
    private ObjectOutputStream output;
    
    private UpdateQueue updateQueue;
    
    public ClientOuputHandler(ClientData clientData, Server server, Socket socket) {
        this.server = server;
        this.clientData = clientData;
        this.socket = socket;
        
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("[ERROR] Failed to open socket output stream of client: " + clientData);
        }
        
        this.updateQueue = new UpdateQueue(clientData, server.getGame().getModel());
    }
    
    public void send(Object packet) {
        try {
            output.writeObject(packet);
            output.flush();
            output.reset();
        } catch (IOException ex) {
            System.out.println("[ERROR] Failed to send packet to client: " + clientData);
            clientData.logout();
        }
    }
    
    private InitPacket createInitPacket() {
        LinkedList<Update> updates = new LinkedList<>();
        LinkedList<GameObject> gameObjects = server.getGame().getModel().getGameObjects();
        Iterator<GameObject> it = gameObjects.iterator();
        while (it.hasNext()) {
            updates.add(it.next().getUpdate());
        }
        return new InitPacket(updates);
    }
    
    private InitPacket createLastInitPacket() {
        server.getGame().spawnNewSpacehipForClient(clientData);
        return new InitPacket(new SpaceshipUpdate(clientData.getSpaceship()));
    }
    
    public void initialize() {
        send(createInitPacket());
        send(createLastInitPacket());
    }
    
    private synchronized void update() {
        while (clientData.getState() == ALIVE || clientData.getState() == DEAD) {
            UpdatePacket updatePacket = updateQueue.pop();
            if (updatePacket != null) {
                send(updatePacket);
            }
            try {
                this.wait(40);
            } catch (InterruptedException ex) {
                System.out.println("[ERROR] Failed to wait in update function of client: " + clientData);
            }
        }
    }
    
    public void close() {
        try {
            output.close();
        } catch (IOException ex) {
            System.out.println("[ERROR] Failed to close output stream of client: " + clientData);
        }
    }
    
    @Override
    public void run() {
        initialize();
        update();
    }
    
    public UpdateQueue getUpdateQueue() {
        return updateQueue;
    }
    
    public ClientData getClientData() {
        return clientData;
    }
    
}
