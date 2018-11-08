/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import operator.network.ServerInputHandler;
import operator.network.ServerOutputHandler;
import asteroidsoperator.AsteroidsOperator;
import java.io.IOException;
import java.net.Socket;
import server.network.basic.Address;
import java.util.LinkedList;
import java.util.Observable;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import server.network.packets.ServerPacket;
import server.network.packets.ShutdownPacket;

/**
 *
 * @author Tom
 */
public class ServerHandler extends Observable {

    private Operator operator;

    private Address addressForClient;

    private Socket socket;
    private ServerInputHandler input;
    private ServerOutputHandler output;

    private LinkedList<ClientHandler> clients;

    private int height;
    private int width;
    
    private boolean markedShutdown;

    public ServerHandler(Socket socket, Operator operator) {
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Create");
        this.operator = operator;
        this.socket = socket;

        clients = new LinkedList<>();
        output = new ServerOutputHandler(this, operator);
        input = new ServerInputHandler(this, operator);
        
        markedShutdown = false;
    }

    public void initialize(ServerPacket serverPacket) {
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Initialize with {0}", serverPacket);
        addressForClient = serverPacket.getServerAddress();
        height = serverPacket.getHeight();
        width = serverPacket.getWidth();
        setChanged();
        notifyObservers();
    }
    
    public void login() {
        addToOperator();
        addToPanel();
        startReceivingPackets();
    }
    
    public void shutdown() {
        sendShutdownPacket();
        stopReceivingPackets();
        disconnect();
        removeFromPanel();
        removeFromOperator();        
    }
    
    private void sendMarkShutdownPacket() {
        output.sendMarkShutdownPacket();
    }
    
    private void sendShutdownPacket() {
        output.sendShutdownPacket();
    }
    
    private void startReceivingPackets() {
        input.start();
    }
    
    private void stopReceivingPackets() {
        input.stopRunning();
    }
    
    private void addToPanel() {
        addObserver(operator);
    }
    
    private void removeFromPanel() {
        deleteObservers();
    }
    
    private void addToOperator() {
        operator.addServer(this);
    }
    
    private void removeFromOperator() {
        operator.removeServer(this);
    }
    
    public void disconnect() {
        AsteroidsOperator.logger.log(INFO, "[ServerData] Close");
        try {
            socket.close();
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerData] Failed to close socket");
        }
    }

    public Socket getSocket() {
        return socket;
    }
    
    public ServerInputHandler getInput() {
        return input;
    }

    public ServerOutputHandler getOutput() {
        return output;
    }

    public Address getAddressForClient() {
        return addressForClient;
    }

    public Address getAddressForOperator() {
        return new Address(socket.getInetAddress().getHostAddress(), socket.getPort());
    }
    
    public int getUtility() {
        return clients.size();
    }

    public void addClient(ClientHandler clientData) {
        AsteroidsOperator.logger.log(INFO, "[ServerData] Add Client: {0}", clientData.getAddressConnectionServer());
        clients.add(clientData);
        setChanged();
        notifyObservers();
    }

    public void removeClient(ClientHandler clientData) {
        AsteroidsOperator.logger.log(INFO, "[ServerData] Remove Client: {0}", clientData.getAddressConnectionServer());
        clients.remove(clientData);
        setChanged();
        notifyObservers();
    }

    public void markShutdown() {
        markedShutdown = true;
    }
    
    public boolean isMarkedShutdown() {
        return markedShutdown;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return addressForClient + "[" + clients.size() + "] " + height + "x" + width;
    }
}
