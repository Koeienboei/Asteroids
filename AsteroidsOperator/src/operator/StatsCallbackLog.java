/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import server.network.packets.MonitorPacket;

/**
 *
 * @author tomei
 */
public class StatsCallbackLog extends ResultCallbackTemplate<StatsCallbackLog, Statistics> {

    private ConcurrentLinkedQueue<Double> cpuPercentList;
    
    public StatsCallbackLog() {
        this.cpuPercentList = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void onNext(Statistics stats) {
        try {
            //System.out.println("Cpu percent: " + calculateCPUPercent(stats));
        } catch (NullPointerException ex) {}
        if (stats != null) {
            try {
                this.cpuPercentList.add(calculateCPUPercent(stats));
            } catch (NullPointerException ex) {}
        }
    }

    public double calculateCPUPercent(Statistics statistics) throws NullPointerException {
        //System.out.println("Cpu total: " + statistics.getCpuStats().getCpuUsage().getTotalUsage() + " PreCpu total: " + statistics.getPreCpuStats().getCpuUsage().getTotalUsage());
        double cpuDelta = statistics.getCpuStats().getCpuUsage().getTotalUsage() - statistics.getPreCpuStats().getCpuUsage().getTotalUsage();
        //System.out.println("Cpu delta: " + cpuDelta);
        //System.out.println("System usage: " + statistics.getCpuStats().getSystemCpuUsage() + " PreSystem usage: " + statistics.getPreCpuStats().getSystemCpuUsage());
        double systemDelta = statistics.getCpuStats().getSystemCpuUsage() - statistics.getPreCpuStats().getSystemCpuUsage();
        //System.out.println("System delta: " + systemDelta);
        double cpuPercent = cpuDelta/systemDelta;
        //System.out.println("Cpu percent: " + cpuPercent);
        return cpuPercent;
    }    

    public double getCpuPercent() {
        double average = 0.0;
        int size = cpuPercentList.size();
        Iterator<Double> it = cpuPercentList.iterator();
        while (it.hasNext()) {
            average = it.next() / size;
        }
        cpuPercentList.clear();
        return average;
    }

}
