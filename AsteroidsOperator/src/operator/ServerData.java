/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import java.net.Socket;
import packeges.Address;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import packeges.ServerPacket;

/**
 *
 * @author Tom
 */
public class ServerData extends Observable {

    private Operator operator;

    private Address addressForClient;

    private Socket socket;
    private ServerInputHandler input;
    private ServerOutputHandler output;

    private LinkedList<ClientData> clients;

    private int height;
    private int width;

    public ServerData(Socket socket, Operator operator) {
        this.operator = operator;
        this.socket = socket;

        clients = new LinkedList<>();
        input = new ServerInputHandler(this, operator, socket);
        output = new ServerOutputHandler(this, operator, socket);
    }

    public void initialize(ServerPacket serverPacket) {
        addressForClient = serverPacket.getServerAddress();
        height = serverPacket.getHeight();
        width = serverPacket.getWidth();
        setChanged();
        notifyObservers();
    }

    public ServerInputHandler getInput() {
        return input;
    }

    public ServerOutputHandler getOutput() {
        return output;
    }

    public Address getAddressForClient() {
        return addressForClient;
    }

    public Address getAddressForOperator() {
        return new Address(socket.getInetAddress().getHostAddress(), socket.getLocalPort());
    }
    
    public int getUtility() {
        return clients.size();
    }

    public void addClient(ClientData clientData) {
        clients.add(clientData);
    }

    public void removeClient(ClientData clientData) {
        clients.remove(clientData);
    }

    public void shutdown() {
        System.out.println("Server " + this + " shutting down, not implemented yet");
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return addressForClient + "[" + clients.size() + "] " + height + "x" + width;
    }
}
