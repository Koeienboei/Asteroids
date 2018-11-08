/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import operator.network.ClientInputHandler;
import operator.network.ClientOutputHandler;
import asteroidsoperator.AsteroidsOperator;
import java.io.IOException;
import java.net.Socket;
import server.network.basic.Address;
import java.util.Observable;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import server.ClientState;
import static server.ClientState.DEAD;
import static server.ClientState.LOGIN;
import static server.ClientState.LOGOUT;

/**
 *
 * @author Tom
 */
public class ClientHandler implements Runnable {

    private Operator operator;
    private Address addressConnectionServer;
    private Socket socket;
    private ClientInputHandler input;
    private ClientOutputHandler output;
    private ServerHandler serverHandler;
    private ClientState state;

    public ClientHandler(Socket socket, Operator operator) {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Create");
        this.operator = operator;
        this.socket = socket;
        this.output = new ClientOutputHandler(this);
        this.input = new ClientInputHandler(this);
        this.state = LOGIN;
    }

    @Override
    public void run() {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Start");
        login();
    }
    
    public void login() {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Login");
        addToOperator();
        addToServer(operator.getServer());
        sendServerPacket();
        startReceivingPackets();
    }

    public void logout() {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Logout");
        setState(LOGOUT);
        stopReceivingPackets();
        disconnect();
        removeFromServer();
        removeFromOperator();
    }

    private void addToOperator() {
        operator.addClient(this);
    }
    
    private void addToServer(ServerHandler serverHandler) {
        setServerHandler(serverHandler);
        serverHandler.addClient(this);
    }

    private void sendServerPacket() {
        output.sendServerPacket();
    }

    private void startReceivingPackets() {
        input.start();
    }

    private void stopReceivingPackets() {
        input.stopRunning();
    }

    private void disconnect() {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Close");
        try {
            socket.close();
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[ClientHandler] Failed to close socket (ClientState = [0])", state);
        }
    }

    private void removeFromServer() {
        serverHandler.removeClient(this);
    }

    private void removeFromOperator() {
        operator.removeClient(this);
    }

    public Socket getSocket() {
        return socket;
    }

    public Address getAddressConnectionServer() {
        return addressConnectionServer;
    }

    public void setAddressConnectionServer(Address addressConnectionServer) {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Set Address of connection with Server to: {0}", addressConnectionServer);
        this.addressConnectionServer = addressConnectionServer;
    }

    public ClientInputHandler getInput() {
        return input;
    }

    public ClientOutputHandler getOutput() {
        return output;
    }

    public void setServerHandler(ServerHandler serverHandler) {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Set ServerHandler to: {0}", serverHandler.getAddressForClient());
        this.serverHandler = serverHandler;
    }

    public ServerHandler getServerHandler() {
        return serverHandler;
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        AsteroidsOperator.logger.log(INFO, "[ClientHandler] Change ClientState to: {0}", state);
        this.state = state;
        if (state == LOGOUT) {
            logout();
        }
        if (serverHandler.isMarkedShutdown() && state == DEAD) {
            login();
        }
    }

    @Override
    public String toString() {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

}
