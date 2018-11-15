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
        this.serverAddress = null;
        this.socket = null;
    }

    private void initialize() {
        client.logger.log(FINE, "[ServerConnector] Initialize");
        client.getOperatorConnector().getOutput().send(new ClientPacket(new Address(socket.getLocalAddress().getHostAddress(), socket.getLocalPort())));

        try {
            socket.setTcpNoDelay(true);
            //socket.setSoTimeout(40);
        } catch (SocketException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Failed to set socket settings (ClientState = {0})", client.getClientState());
        }

        output = new ServerOutputHandler(client, socket);
        input = new ServerInputHandler(client, socket);
    }

    public void login() {
        client.logger.log(FINE, "[ServerConnector] Login to Server");
        connect();
        initialize();
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
        disconnect();
    }

    private void connect() {
        try {
            socket = new Socket(serverAddress.getIp(), serverAddress.getPort(), InetAddress.getLocalHost(), 8950);
        } catch (NullPointerException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Trying to login to Server without a Server Address (ClientState = {0})", client.getClientState());
        } catch (IOException ex) {
            client.logger.log(SEVERE, "[ServerConnector] Failed to set up connection with Server(ClientState = {0})", client.getClientState());
        }

    }

    public void disconnect() {
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
