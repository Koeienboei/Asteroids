/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import packeges.Address;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Spaceship;
import static server.ClientState.INITIALIZE;
import static server.ClientState.LOGOUT;

/**
 *
 * @author Tom
 */
public class ClientData extends Observable {
    
    private Address address;
    private Spaceship spaceship;
    private ClientState state;
    
    private Socket socket;
    private ClientInputHandler input;
    private ClientOuputHandler output;
    
    private Server server;
    
    public ClientData(Socket socket, Server server) {
        this.address = new Address(socket.getInetAddress().toString(), socket.getPort());
        this.socket = socket;
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException ex) {
            System.err.println("Failed to set client socket to no delay");
        }
        this.server = server;
        
        input = new ClientInputHandler(this, server, socket);
        output = new ClientOuputHandler(this, server, socket);
        
        this.state = INITIALIZE;
    }
    
    public void logout() {
        setState(LOGOUT);
        deleteObservers();
        spaceship.destroy();
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Failed to close socket of client: " + this);
        }
        server.removeClient(address);
    }

    public Address getAddress() {
        return address;
    }

    public void setSpaceship(Spaceship spaceship) {
        this.spaceship = spaceship;
    }
    
    public Spaceship getSpaceship() {
        return spaceship;
    }

    public ClientInputHandler getInput() {
        return input;
    }

    public ClientOuputHandler getOutput() {
        return output;
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
        setChanged();
        notifyObservers(this);
    }
    
    public void setStateWithoutNotify(ClientState state) {
        this.state = state;
    }
    
    @Override
    public String toString() {
        return address.getIp() + ":" + address.getPort() + " " + state;
    }
    
}
