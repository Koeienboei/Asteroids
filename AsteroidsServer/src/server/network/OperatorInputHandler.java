/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import packeges.Address;
import packeges.ClientStatePacket;
import static server.ServerState.SHUTDOWN;

/**
 *
 * @author Tom
 */
public class OperatorHandler implements Observer {
    
    private Socket socket;
    private ObjectOutputStream output;
    
    public OperatorHandler(Address operatorAddress) {
        try {
            socket = new Socket(operatorAddress.getIp(), operatorAddress.getPort());
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Failed to connect to operator");
        }
    }
   
    public void send(Object packet) {
        try {
            output.writeObject(packet);
        } catch (IOException ex) {
            System.err.println("Failed to send packet to operator");
        }
    }

    @Override
    public void update(Observable o, Object clientData) {
        send(new ClientStatePacket((ClientData) clientData));
    }
    
    public void close() {
        try {
            output.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Failed to close operator handler");
        }
    }
    
}
