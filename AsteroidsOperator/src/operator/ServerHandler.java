/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import operator.network.ServerInputHandler;
import operator.network.ServerOutputHandler;
import asteroidsoperator.AsteroidsOperator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import server.network.basic.Address;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import server.network.packets.MonitorPacket;
import server.network.packets.ServerPacket;
import server.network.packets.ShutdownPacket;

/**
 *
 * @author Tom
 */
public class ServerHandler extends Observable implements Runnable {

    private Operator operator;

    private Address addressForClient;

    private Socket socket;
    private ServerInputHandler input;
    private ServerOutputHandler output;

    private ConcurrentLinkedQueue<ClientHandler> clients;

    private int height;
    private int width;
    
    private boolean markedShutdown;
    
    private MonitorPacketList monitorPacketList;
    private BufferedWriter monitorFileWriter;

    public ServerHandler(Socket socket, Operator operator) {
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Create");
        this.operator = operator;
        this.socket = socket;
        initialize();
    }

    private void initialize() {
        output = new ServerOutputHandler(this, operator);
        input = new ServerInputHandler(this, operator);
        
        try {
            socket.setTcpNoDelay(true);
            //socket.setSoTimeout(40);
        } catch (SocketException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to set socket settings");
        }
        
        clients = new ConcurrentLinkedQueue<>();
        monitorPacketList = new MonitorPacketList(operator.getMonitor().getW());
        try {
            monitorFileWriter = new BufferedWriter(new FileWriter(socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " monitor.txt", true));
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to create file writer");
        }
        
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
    
    @Override
    public void run() {
        login();
    }
    
    public void login() {
        addToOperator();
        addToPanel();
        startReceivingPackets();
    }
    
    public void shutdown() {
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Shutdown {0}", addressForClient);
        sendShutdownPacket();
        /*while (!clients.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to wait for server to shutdown");
            }
        }*/
        stopReceivingPackets();
        disconnect();
        removeFromPanel();
        removeFromOperator();   
        try {
            monitorFileWriter.close();
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to close monitor file");
        }
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
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Disconnect {0}", addressForClient);
        try {
            socket.close();
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to close socket");
        }
    }

    public void addMonitorPacket(MonitorPacket monitorPacket) {
        AsteroidsOperator.logger.log(FINE, "[ServerHandler] Adding {0}", monitorPacket);
        try {
            monitorFileWriter.write(monitorPacket.toString() + "\n");
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ServerHandler] Failed to write to monitor file");
        }
        monitorPacketList.add(monitorPacket);
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
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Server {0} added Client: {1}", new Object[] {addressForClient, clientData.getAddressConnectionServer()});
        clients.add(clientData);
        setChanged();
        notifyObservers();
    }

    public void removeClient(ClientHandler clientData) {
        AsteroidsOperator.logger.log(INFO, "[ServerHandler] Server {0} removed Client: {1}", new Object[] {addressForClient, clientData.getAddressConnectionServer()});
        clients.remove(clientData);
        setChanged();
        notifyObservers();
        if (markedShutdown && clients.isEmpty()) {
            shutdown();
        }
    }

    public void markShutdown() {
        if (clients.isEmpty()) {
            shutdown();
        }
        markedShutdown = true;
    }
    
    public boolean isMarkedShutdown() {
        return markedShutdown;
    }
    
    public boolean isAboveAverage() {
        return getUtility() > operator.getAverageUtilization();
    }
    
    public boolean isRunning() {
        if (input == null || addressForClient == null) {
            return false;
        }
        return input.isRunning();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public MonitorPacketList getMonitorPacketList() {
        return monitorPacketList;
    }    

    @Override
    public String toString() {
        return addressForClient + "  [" + clients.size() + "]" + (markedShutdown ? "Shutting down ..." : "");
    }
}
