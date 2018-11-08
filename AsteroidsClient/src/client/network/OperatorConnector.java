package client.network;

import client.Client;
import java.io.IOException;
import java.net.Socket;
import static java.util.logging.Level.FINE;
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
        client.logger.log(FINE, "[OperatorConnector] Create");
        this.client = client;
        this.socket = null;
        this.operatorAddress = operatorAddress;
    }

    public void connect() {
        client.logger.log(FINE, "[OperatorConnector] Login to Operator");
        try {
            socket = new Socket(operatorAddress.getIp(), operatorAddress.getPort());
            output = new OperatorOutputHandler(socket, client);
            input = new OperatorInputHandler(socket, client);
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[OperatorConnector] Failed to set up connection with Operator");
        }
    }

    public void disconnect() {
        client.logger.log(FINE, "[OperatorConnector] Close");
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
