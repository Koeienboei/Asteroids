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

    private ObjectInputStream input;

    public InputHandler(Socket socket) throws IOException {
        input = new ObjectInputStream(socket.getInputStream());
    }

    public Packet receive() throws IOException, ClassNotFoundException, ClassCastException {
        Object o = input.readObject();
        if (o instanceof Packet) {
            Packet packet = (Packet) o;
            return (Packet) o;
        }
        return null;
    }

}
