package client.network;

import client.Client;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import server.network.basic.Address;
import server.network.packets.Packet;
import server.network.packets.ServerPacket;

/**
 * This class represents an InputHandler for the Server to receive packages
 *
 * @author Tom
 */
public class OperatorConnector extends Thread {

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
            //socket.setSoTimeout(40);
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[OperatorConnector] Failed to set up connection with Operator");
        }
    }

    public void disconnect() {
        client.logger.log(INFO, "[OperatorConnector] Disconnect");
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

}
