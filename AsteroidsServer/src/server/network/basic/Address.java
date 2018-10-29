/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packeges;

import java.io.Serializable;

/**
 *
 * @author tomei
 */
public class Address implements Serializable {
    
    static final long serialVersionUID = 128L;
    
    private String ip;
    private int port;
    
    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
    
    public boolean equals(Address address) {
        return ip.equals(address.getIp()) && port == address.getPort();
    }
    
    @Override
    public String toString() {
        return ip + ":" + port;
    }
    
}
