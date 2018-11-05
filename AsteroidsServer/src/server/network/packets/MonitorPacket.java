/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.network.packets;

import asteroidsserver.AsteroidsServer;
import static java.util.logging.Level.FINE;
import server.Monitor;

/**
 *
 * @author tomei
 */
public class MonitorPacket extends Packet {
    
    private double responseTimeGame;
    private double responseTimeClients;
    private double utilization;
    private double processingRate;
    
    public MonitorPacket(Monitor monitor) {
        AsteroidsServer.logger.log(FINE, "Create MonitorPacket");
        this.responseTimeGame = monitor.getResponseTimeGame();
        this.responseTimeClients = monitor.getResponseTimeClients();
        this.utilization = monitor.getUtilization();
        this.processingRate = monitor.getProcessingRate();
    }

    public double getResponseTimeGame() {
        return responseTimeGame;
    }

    public double getResponseTimeClients() {
        return responseTimeClients;
    }

    public double getUtilization() {
        return utilization;
    }

    public double getProcessingRate() {
        return processingRate;
    }
    
    @Override
    public String toString() {
        return "MonitorPacket(" + responseTimeGame + ", " + responseTimeClients + ", " + utilization + ", " + processingRate + ")";
    }
   
}
