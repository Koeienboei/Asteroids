/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Tom
 */
public class ServerOutputHandler extends Thread {
    
    private ServerData serverData;
    private Operator operator;
    private ObjectOutputStream output;
    
    public ServerOutputHandler(ServerData serverData, Operator operator, Socket socket) {
        this.serverData = serverData;
        this.operator = operator;
        
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.err.println("Failed to open socket output stream of server");
        }
    }
    
    private void send(Object packet) {
        try {
            output.writeObject(packet);
        } catch (IOException ex) {
            System.err.println("Failed to send packet to server");
        }
    }
    
    public void close() {
        try {
            output.close();
        } catch (IOException ex) {
            System.err.println("Failed to close output stream of server");
        }
    }
    
}
