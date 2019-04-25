/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network;

import server.network.OperatorInputHandler;
import server.network.OperatorOutputHandler;
import asteroidsserver.AsteroidsServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import server.Server;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import server.network.basic.Address;

/**
 *
 * @author Tom
 */
public class OperatorConnector {
    
    private Server server;
    
    private Address operatorAddress;
    
    private Socket socket;
    private OperatorInputHandler input;
    private OperatorOutputHandler output;
    
    public OperatorConnector(Address operatorAddress, Server server) {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Create");
        this.server = server;
        this.operatorAddress = operatorAddress;
    }
    
    public void start() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Login");
        connect();
        sendServerPacket();
        startSendingPackets();
        startReceivingPackets();
    }

    private void sendServerPacket() {
        output.sendServerPacket();
    }
    
    public void sendShutdownPacket() {
        AsteroidsServer.logger.log(FINE, "[OperatorConnector] Send ShutdownPacket");
        output.sendShutdownPacket();
    }    
    
    public void stopRunning() {
        AsteroidsServer.logger.log(FINE, "[OperatorConnector] Logout");
        stopReceivingPackets();
        stopSendingPackets();
    }
    
    private void connect() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Connect");
        System.out.println("Connect to operator (" + operatorAddress + ")");
        try {
            socket = new Socket(operatorAddress.getIp(), operatorAddress.getPort(), InetAddress.getLocalHost(), 0);
            output = new OperatorOutputHandler(this, server);
            input = new OperatorInputHandler(this, server);
            socket.setTcpNoDelay(true);
            //socket.setSoTimeout(40);
            System.out.println("Connect to operator succes");
        } catch (IOException ex) {
            System.out.println("Failed to connect to operator " + ex.getMessage());
            AsteroidsServer.logger.log(SEVERE, "[OperatorData] Failed to connect to Operator");
        }
    }
    
    private void startReceivingPackets() {
        AsteroidsServer.logger.log(FINE, "[OperatorConnector] Start receiving packets");
        input.start();
    }
    
    private void stopReceivingPackets() {
        AsteroidsServer.logger.log(FINE, "[OperatorConnector] Stop receiving packets");
        input.stopRunning();
    }
    
    private void startSendingPackets() {
        AsteroidsServer.logger.log(FINE, "[OperatorConnector] Start sending packets");
        output.start();
    }
    
    private void stopSendingPackets() {
        AsteroidsServer.logger.log(FINE, "[OperatorConnector] Stop sending packets");
        output.stopRunning();
    }
    
    public void disconnect() {
        AsteroidsServer.logger.log(FINE, "[OperatorData] Disconnect");
        try {
            socket.close();
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[OperatorData] Failed to close socket");
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public OperatorInputHandler getInput() {
        return input;
    }

    public OperatorOutputHandler getOutput() {
        return output;
    }

    public Address getAddress() {
        return new Address(socket.getLocalAddress().getHostAddress(), socket.getLocalPort());
    }
    
}
