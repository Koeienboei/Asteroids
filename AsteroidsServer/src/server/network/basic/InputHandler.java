/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network.basic;

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

    private Logger logger = Logger.getGlobal();
    
    private ObjectInputStream input;

    public InputHandler(Socket socket) {
        logger.log(Level.FINE, "[InputHandler] Create");
        try {
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "[InputHandler] Failed to create ObjectInputStream");
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
            logger.log(Level.WARNING, "[InputHandler] Failed to receive packet");
        }
        return null;
    }

}
