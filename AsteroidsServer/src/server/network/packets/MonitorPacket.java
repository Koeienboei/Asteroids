/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network.packets;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import static java.util.logging.Level.FINE;
import server.Monitor;

/**
 *
 * @author tomei
 */
public class MonitorPacket extends Packet implements Serializable {
    
    static final long serialVersionUID = 104904873L;
    
    private double responseTime;
    private double utilization;
    private double throughput;
    
    public MonitorPacket(Monitor monitor) {
        AsteroidsServer.logger.log(FINE, "Create MonitorPacket");
        this.responseTime = monitor.getResponseTime();
        this.utilization = monitor.getUtilization();
        this.throughput = monitor.getThroughput();
    }
    
    public MonitorPacket(double R, double X, double U) {
        this.responseTime = R;
        this.throughput = X;
        this.utilization = U;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public void setUtilization(double utilization) {
        this.utilization = utilization;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }
    
    public double getResponseTime() {
        return responseTime;
    }

    public double getUtilization() {
        return utilization;
    }

    public double getThroughput() {
        return throughput;
    }
    
    @Override
    public String toString() {
        return "MonitorPacket(" + responseTime + ", " + utilization + ", " + throughput + ")";
    }
   
}
