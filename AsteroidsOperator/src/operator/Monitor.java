/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import server.network.packets.MonitorPacket;

/**
 *
 * @author tomei
 */
public class Monitor extends Thread {

    private Operator operator;
    private Planner planner;

    private int rLow;
    private int rHigh;
    private int rMax;
    private int W;
    
    private volatile boolean running;

    public Monitor(Operator operator, int rLow, int rHigh, int rMax, int W) {
        AsteroidsOperator.logger.log(FINE, "[Monitor] Create");
        this.operator = operator;
        this.rLow = rLow;
        this.rHigh = rHigh;
        this.rMax = rMax;
        this.W = W;
        this.running = false;
    }

    @Override
    public synchronized void run() {
        AsteroidsOperator.logger.log(FINE, "[Monitor] Start");
        MonitorPacket average;
        double R, X, U;
        running = true;
        while (running) {
            average = calculateAverage();
            AsteroidsOperator.logger.log(INFO, "[Monitor] Average: {0}", average);
            R = average.getResponseTime();
            X = average.getThroughput();
            U = average.getUtilization();
            if (R > rHigh) {
                //planner.acquire(R, X, U);
            } else if (R < rLow) {
                //planner.release(R, X, U);
            }
            try {
                this.wait(1000);
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
    private MonitorPacket calculateAverage() {
        MonitorPacket average = new MonitorPacket(0,0,0);
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            MonitorPacket serverAverage = it.next().getMonitorPacketList().getAverage();
            average.setResponseTime(average.getResponseTime() + serverAverage.getResponseTime()/operator.getServers().size());
            average.setThroughput(average.getThroughput() + serverAverage.getThroughput()/operator.getServers().size());
            average.setUtilization(average.getUtilization() + serverAverage.getUtilization()/operator.getServers().size());
        }
        return average;
    }

    public int getW() {
        return W;
    }

    public void stopRunning() {
        running = false;
    }
    
}
