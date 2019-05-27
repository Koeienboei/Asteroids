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

    public MonitorPacketList(int W) {
        list = new ConcurrentLinkedQueue<>();
    }

    public void add(MonitorPacket monitorPacket) {
        list.add(monitorPacket);
    }

    public double getAverageResponseTime() {
        double total = 0;
        int size = list.size();
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            total += it.next().getResponseTime();
        }
        return total / size;
    }

    public double getAverageThroughput() {
        double total = 0;
        int size = list.size();
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            total += it.next().getThroughput();
        }
        return total / size;
    }

    public double getAverageUtilization() {
        double total = 0;
        int size = list.size();
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            total += it.next().getUtilization();
        }
        return total / size;
    }

    public MonitorPacket getCurrent() {
        return list.peek();
    }

    public double getCurrentResponseTime() {
        return list.peek().getResponseTime();
    }

    public void reset() {
        list.clear();
    }

    public MonitorPacket getAverage() {
        MonitorPacket average = new MonitorPacket(0, 0, 0);
        int size = list.size();
        Iterator<MonitorPacket> it = list.iterator();
        while (it.hasNext()) {
            MonitorPacket next = it.next();
            average.setResponseTime(average.getResponseTime() + next.getResponseTime() / size);
            average.setThroughput(average.getThroughput() + next.getThroughput() / size);
            average.setUtilization(average.getUtilization() + next.getUtilization() / size);
        }
        return average;
    }

}
