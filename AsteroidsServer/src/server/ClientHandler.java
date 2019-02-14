/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import server.network.ClientOutputHandler;
import server.network.ClientInputHandler;
import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import server.network.basic.Address;
import java.util.Observable;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import model.Spaceship;
import static server.ClientState.ALIVE;
import static server.ClientState.INITIALIZE;
import static server.ClientState.LOGIN;
import static server.ClientState.LOGOUT;

/**
 *
 * @author Tom
 */
public class ClientHandler extends Observable implements Runnable {

    private Spaceship spaceship;
    private ClientState clientState;

    private Socket socket;
    private ClientInputHandler input;
    private ClientOutputHandler output;

    private UpdateQueue updateQueue;

    private Server server;

    public ClientHandler(Socket socket, Server server) {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Create with address s{0} to c{1}", new Object[]{new Address(socket.getLocalAddress().getHostAddress(), socket.getLocalPort()), new Address(socket.getInetAddress().getHostAddress(), socket.getPort())});
        this.socket = socket;
        this.server = server;
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Before initialize");
        this.initialize();
    }
    
    private void initialize() {
        clientState = INITIALIZE;
        
        try {
            socket.setTcpNoDelay(true);
            //socket.setSoTimeout(40);
        } catch (SocketException ex) {
            AsteroidsServer.logger.log(SEVERE, "[ClientHandler] Failed to set socket settings");
        }
        
        output = new ClientOutputHandler(this, server);
        input = new ClientInputHandler(this, server);
        
        updateQueue = new UpdateQueue(this, server.getGame().getModel());
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Initialized");
    }

    @Override
    public void run() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Start");
        login();
    }

    public void login() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Login {0}", getAddress());
        System.out.println("Client logs in: " + getAddress());
        addToServer();
        sendInitPacket();
        addToGame();
        sendSpaceship();
        initializeUpdateQueue();
        addToMonitor();
        startSendingUpdates();
        startReceivingPackets();
        clientState = ALIVE;
    }
    
    public void logout() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Logout {0}", getAddress());
        System.out.println("Client logs out: " + getAddress());
        setState(LOGOUT);
        sendLogoutPacket();
        stopSendingUpdates();
        stopReceivingPackets();
        disconnect();
        removeFromMonitor();
        removeFromGame();
        removeFromServer();
    }

    private void sendInitPacket() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Send InitPacket");
        output.sendInitPacket();
    }

    private void sendSpaceship() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Send Spaceship");
        output.sendSpaceship();
    }

    private void sendLogoutPacket() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Send Logout {0}", getAddress());
        output.sendLogoutPacket();
    }

    private void initializeUpdateQueue() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Init UpdateQueue");
        updateQueue.initialize();
    }

    private void startSendingUpdates() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Start sending updates");
        Thread outputThread = new Thread(output);
        outputThread.start();
    }

    private void stopSendingUpdates() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Stop sending updates {0}", getAddress());
        output.stopRunning();
    }

    private void startReceivingPackets() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Start receiving packets");
        input.start();
    }

    private void stopReceivingPackets() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Stop receiving packets {0}", getAddress());
        input.stopRunning();
    }

    private void disconnect() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Disconnect {0}", getAddress());
        try {
            socket.close();
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[ClientHandler] Failed to close connection");
        }
    }

    private void addToGame() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Add to game");
        server.getGame().spawnNewSpacehipForClient(this);
    }

    private void removeFromGame() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] Remove from game {0}", getAddress());
        spaceship.destroy();
    }

    private void addToServer() {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Add to server");
        server.addClient(this);
        addObserver(server);
    }

    private void removeFromServer() {
        AsteroidsServer.logger.log(INFO, "[ClientHandler] remove from server {0}", getAddress());
        server.removeClient(this);
        deleteObserver(server);
    }
    
    private void addToMonitor() {
        server.getMonitor().addClient(this);
    }
    
    private void removeFromMonitor() {
        server.getMonitor().removeClient(this);
    }

    public Address getAddress() {
        return new Address(socket.getInetAddress().getHostAddress(), socket.getPort());
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }

    public Spaceship getSpaceship() {
        return spaceship;
    }

    public ClientInputHandler getInput() {
        return input;
    }

    public ClientOutputHandler getOutput() {
        return output;
    }

    public ClientState getClientState() {
        return clientState;
    }

    public void setState(ClientState clientState) {
        AsteroidsServer.logger.log(FINE, "[ClientHandler] Set ClientState to: {0}", clientState);
        this.clientState = clientState;
        setChanged();
        notifyObservers();
        AsteroidsServer.logger.log(FINE, "[ClientHandler] End of set ClientState to: {0}", clientState);
    }

    public Socket getSocket() {
        return socket;
    }

    public UpdateQueue getUpdateQueue() {
        return updateQueue;
    }

    @Override
    public String toString() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " " + clientState;
    }

}
