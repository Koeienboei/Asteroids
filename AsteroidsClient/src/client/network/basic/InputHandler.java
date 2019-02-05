/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.network.basic;

import client.Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import java.util.logging.Logger;
import server.network.packets.Packet;

/**
 *
 * @author tomei
 */
public class InputHandler {

    private Logger logger;
    
    private ObjectInputStream input;

    public InputHandler(Socket socket, Client client) {
        this.logger = client.logger;
        logger.log(Level.FINE, "[InputHandler] Create");
        try {
            logger.log(Level.FINE, "[InputHandler] port ({0})", socket.getLocalPort());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "[InputHandler] Failed to create ObjectInputStream ({0})", ex.getMessage());
        }
    }

    public Packet receive() {
        logger.log(Level.FINE, "[InputHandler] Receive packet");
        try {
            Object o = input.readObject();
            if (o instanceof Packet) {
                Packet packet = (Packet) o;
                logger.log(Level.FINE, "[InputHandler] Received packet: {0}", packet);
                return (Packet) o;
            }
        } catch (SocketTimeoutException ex) {
            logger.log(FINE, "[InputHandler] Socket timeout");
        } catch (IOException | ClassNotFoundException ex) {
            logger.log(Level.FINE, "[InputHandler] Failed to receive packet");
        }
        return null;
    }

}
