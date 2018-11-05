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
import java.net.Socket;
import java.util.logging.Level;
import server.Server;
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
    
    public void login() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Login");
        connect();
        sendServerPacket();
    }

    private void sendServerPacket() {
        output.sendServerPacket();
    }
    
    public void sendShutdownPacket() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Send ShutdownPacket");
        output.sendShutdownPacket();
    }    
    
    public void logout() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Logout");
        stopReceivingPackets();
        stopSendingPackets();
        disconnect();
    }
    
    private void connect() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Connect");
        try {
            socket = new Socket(operatorAddress.getIp(), operatorAddress.getPort());
            output = new OperatorOutputHandler(this, server);
            input = new OperatorInputHandler(this, server);
        } catch (IOException ex) {
            AsteroidsServer.logger.log(SEVERE, "[OperatorData] Failed to connect to Operator");
        }
    }
    
    private void stopReceivingPackets() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Stop receiving packets");
        input.stopRunning();
    }
    
    private void stopSendingPackets() {
        AsteroidsServer.logger.log(INFO, "[OperatorConnector] Stop sending packets");
        output.stopRunning();
    }
    
    private void disconnect() {
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

}
