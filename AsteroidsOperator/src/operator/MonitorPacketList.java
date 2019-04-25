/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import server.network.packets.MonitorPacket;

/**
 *
 * @author tomei
 */
public class MonitorPacketList {
    
    private ConcurrentLinkedQueue<MonitorPacket> list;
    private int W;
    
    public MonitorPacketList(int W) {
        list = new ConcurrentLinkedQueue<>();
        for (int i=0; i<W; i++) {
            list.add(new MonitorPacket(0,0,0));
        }
        this.W = W;
    }
    
    public void add(MonitorPacket monitorPacket) {
        list.remove();
        list.add(monitorPacket);
    }
    
    public double getAverageResponseTime() {
        double total = 0;
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            total += it.next().getResponseTime();
        }
        return total / W;
    }
    
    public double getAverageThroughput() {
        double total = 0;
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            total += it.next().getThroughput();
        }
        return total / W;
    }
    
    public double getAverageUtilization() {
        double total = 0;
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            total += it.next().getUtilization();
        }
        return total / W;
    }
    
    public MonitorPacket getCurrent() {
        return list.peek();
    }
    
    public MonitorPacket getAverage() {
        MonitorPacket average = new MonitorPacket(0,0,0);
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            MonitorPacket next = it.next();
            average.setResponseTime(average.getResponseTime() + next.getResponseTime()/W);
            average.setThroughput(average.getThroughput() + next.getThroughput()/W);
            average.setUtilization(average.getUtilization() + next.getUtilization()/W);
        }
        return average;
    }
    
}
