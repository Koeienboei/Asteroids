/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network;

import client.Client;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import server.network.basic.Address;
import server.network.packets.ClientPacket;

/**
 *
 * @author tomei
 */
public class ServerConnector {

    private Address serverAddress;

    private Socket socket;
    private ServerInputHandler input;
    private ServerOutputHandler output;

    private Client client;

    public ServerConnector(Client client) {
        client.logger.log(INFO, "[ServerConnector] Create");
        this.client = client;
        this.serverAddress = null;
        this.socket = null;
    }

    private void initialize() {
        client.logger.log(INFO, "[ServerConnector] Initialize");
        
        client.getOperatorConnector().getOutput().sendClientPacket(socket);

        output = new ServerOutputHandler(client, socket);
        input = new ServerInputHandler(client, socket);
        
        try {
            socket.setTcpNoDelay(true);
            //socket.setSoTimeout(40);
        } catch (SocketException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Failed to set socket settings ({0})", ex.getMessage());
        }
    }

    public void login() {
        client.logger.log(INFO, "[ServerConnector] Login to Server");
        connect();
        initialize();
    }

    public void sendLogoutPacket() {
        client.logger.log(INFO, "[ServerConnector] Send LogoutPacket {0}", new Address(socket.getLocalAddress().getHostAddress(), socket.getLocalPort()));
        output.sendLogoutPacket();
    }

    public void logout() {
        client.logger.log(INFO, "[ServerConnector] Logout");
        if (output != null) {
            output.stopRunning();
        }
        if (input != null) {
            input.stopRunning();
        }
        disconnect();
    }

    private void connect() {
        client.logger.log(INFO, "[ServerConnector] Connect");
        try {
            socket = new Socket();
            socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
            socket.connect(new InetSocketAddress(serverAddress.getIp(), serverAddress.getPort()));
        } catch (NullPointerException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Trying to login to Server without a Server Address (ClientState = {0})", client.getClientState());
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Failed to set up connection with Server ({0})", ex.getMessage());
        }

    }

    public void disconnect() {
        client.logger.log(INFO, "[ServerConnector] Disconnect");
        try {
            socket.close();
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Failed to close socket (ClientState = {0})", client.getClientState());
        }
    }

    public Address getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(Address serverAddress) {
        client.logger.log(INFO, "[ServerConnector] Set Server Address to: {0}", serverAddress);
        this.serverAddress = serverAddress;
    }

    public boolean isLoggedIn() {
        return serverAddress != null;
    }

    public ServerOutputHandler getOutput() {
        return output;
    }

    public ServerInputHandler getInput() {
        return input;
    }

}
