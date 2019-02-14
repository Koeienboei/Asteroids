package client.network;

import client.Client;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import server.network.basic.Address;
import server.network.packets.Packet;
import server.network.packets.ServerPacket;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class OperatorConnector {

    private Client client;
    private Socket socket;
    private Address operatorAddress;
    private OperatorInputHandler input;
    private OperatorOutputHandler output;

    public OperatorConnector(Client client, Address operatorAddress) {
        client.logger.log(INFO, "[OperatorConnector] Create");
        this.client = client;
        this.socket = null;
        this.operatorAddress = operatorAddress;
    }

    public void connect() {
        client.logger.log(INFO, "[OperatorConnector] Connect");
        try {
            socket = new Socket(operatorAddress.getIp(), operatorAddress.getPort(), InetAddress.getLocalHost(), 0);

            output = new OperatorOutputHandler(socket, client);
            input = new OperatorInputHandler(socket, client);

            socket.setTcpNoDelay(true);
            
            input.start();
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[OperatorConnector] Failed to set up connection with Operator");
        }
    }

    public void logout() {
        client.logger.log(INFO, "[OperatorConnector] Logout");
        if (isLoggedIn()) {
            output.sendLogoutPacket();
            while (isLoggedIn()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    client.logger.log(SEVERE, "[OperatorConnector] Failed to wait for logging out of operator");
                }
            }
        }
        
    }
    
    public void disconnect() {
        client.logger.log(INFO, "[OperatorConnector] Disconnect");
        if (input != null) {
            input.stopRunning();
        }
        try {
            socket.close();
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[OperatorConnector] Failed to close socket");
        }
    }

    public OperatorInputHandler getInput() {
        return input;
    }

    public OperatorOutputHandler getOutput() {
        return output;
    }

    public void sendLogoutPacket() {
        output.sendLogoutPacket();
    }

    public boolean isLoggedIn() {
        return input.isRunning();
    }
}
