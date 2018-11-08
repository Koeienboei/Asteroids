/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network;

import client.Client;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import static java.util.logging.Level.FINE;
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
        client.logger.log(FINE, "[ServerConnector] Create");
        this.client = client;
        this.initialize();
    }

    private void initialize() {
        client.logger.log(FINE, "[ServerConnector] Initialize");
        serverAddress = null;
        socket = new Socket();
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Failed to set socket to no delay (ClientState = {0})", client.getClientState());
        }
    }
    
    public void login() {
        client.logger.log(FINE, "[ServerConnector] Login to Server");
        if (serverAddress == null) {
            client.logger.log(SEVERE, "[ServerConnector] Trying to login to Server without a Server Address (ClientState = {0})", client.getClientState());
            System.exit(0);
        }
        try {
            socket.connect(new InetSocketAddress(serverAddress.getIp(), serverAddress.getPort()));

            client.getOperatorConnector().getOutput().send(new ClientPacket(new Address(socket.getLocalAddress().getHostAddress(), socket.getLocalPort())));

            output = new ServerOutputHandler(client, socket);
            input = new ServerInputHandler(client, socket);
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Failed to set up connection with Server(ClientState = {0})", client.getClientState());
        }
    }

    public void sendLogoutPacket() {
        output.sendLogoutPacket();
    }
    
    public void logout() {
        if (output != null) {
            output.stopRunning();
        }
        if (input != null) {
            input.stopRunning();
        }
    }
    
    public void close() {
        client.logger.log(FINE, "[ServerConnector] Close");
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
        client.logger.log(FINE, "[ServerConnector] Set Server Address to: {0}", serverAddress);
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
