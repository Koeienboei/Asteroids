/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import packeges.Address;

/**
 *
 * @author tomei
 */
public class ServerHandler {

    private Address address;

    private Socket socket;
    private ServerInputHandler input;
    private ServerOutputHandler output;

    private Client client;

    public ServerHandler(Client client, Address address) {
        this.address = address;
        this.client = client;
    }

    public void login() {
        System.out.println("Log into server");
        if (address == null) {
            System.err.println("Trying to login without server address");
            System.exit(0);
        }
        try {
            System.out.println("Creating socket for connection with server");
            socket = new Socket(address.getIp(), address.getPort());
            try {
                socket.setTcpNoDelay(true);
            } catch (SocketException ex) {
                System.err.println("Failed to set client socket to no delay");
            }
            System.out.println("Creating output for connection with server");
            output = new ServerOutputHandler(client, socket);
            System.out.println("Creating input for connection with server");
            input = new ServerInputHandler(client, socket);
            System.out.println("Done loggin in");
        } catch (IOException ex) {
            System.err.println("Failed to connect to server");
        }
    }

    public void logout() {
        output.logout();
    }

    public void close() {
        input.close();
        output.close();

        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Failed to close connection to server");
        }
    }

    public ServerOutputHandler getOutput() {
        return output;
    }

    public ServerInputHandler getInput() {
        return input;
    }

}
