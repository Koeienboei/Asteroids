/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network.basic;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.network.packets.Packet;

/**
 *
 * @author tomei
 */
public class OutputHandler {

    private Logger logger = Logger.getGlobal();
    private ObjectOutputStream output;

    public OutputHandler(Socket socket) {
        logger.log(Level.INFO, "[OutputHandler] Create with socket {0} {1}", new Object[] {socket == null, socket.getLocalPort()});
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "[OutputHandler] Failed to create ObjectOutputStream {0}", ex.getMessage());
        }
    }

    public void send(Packet packet) {
        logger.log(Level.FINE, "[OutputHandler] Send packet: {0}", packet);
        boolean sent = false;
        int timesTried = 0;
        while (!sent && timesTried < 10) {
            try {
                timesTried++;
                output.writeObject(packet);
                output.flush();
                output.reset();
                sent = true;
            } catch (IOException ex) {
                logger.log(Level.WARNING, "[OutputHandler] Failed({0}) to send packet: {1}", new Object[]{timesTried, packet});
            }
        }
    }

}
