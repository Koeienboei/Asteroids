/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network.basic;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import java.util.Objects;
import static java.util.logging.Level.FINE;

/**
 *
 * @author tomei
 */
public class Address implements Serializable {
    
    static final long serialVersionUID = 128L;
    
    private String ip;
    private int port;
    
    public Address(String ip, int port) {
        AsteroidsServer.logger.log(FINE, "Create Address: {0}:{1}", new Object[]{ip, port});
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Address) {
            Address address = (Address) o;
            return ip.equals(address.getIp()) && port == address.getPort();
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return ip == null ? "null" : (ip + ":" + port);
    }
    
}
