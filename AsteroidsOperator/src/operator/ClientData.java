/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import packeges.Address;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.ClientState;

/**
 *
 * @author Tom
 */
public class ClientData extends Observable {

    private Address address;
    private Socket socket;
    private ObjectOutputStream output;
    private ServerData serverData;
    private ClientState state;

    public ClientData(Socket socket) {
        this.address = new Address(socket.getInetAddress().toString(), socket.getPort());
        this.socket = socket;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Failed to open socket output stream of server");
        }
    }

    public void close() {
        try {
            output.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Failed to close client");
        }
    }
    
    public Address getAddress() {
        return address;
    }

    public ObjectOutputStream getOutput() {
        return output;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }

    public String toString() {
        return getAddress().toString();
    }

}
