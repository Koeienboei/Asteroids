/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Double.max;
import static java.lang.Double.min;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import server.network.packets.MonitorPacket;

/**
 *
 * @author tomei
 */
public class Monitor extends Thread {

    private Operator operator;

    private int rLow;
    private int rHigh;
    private int rMax;
    private int W;
    private int reconfigurationSpeed;

    private volatile boolean running;

    private BufferedWriter monitorFileWriter;

    public Monitor(Operator operator, int rLow, int rHigh, int rMax, int W, int reconfigurationSpeed) {
        AsteroidsOperator.logger.log(FINE, "[Monitor] Create");
        this.operator = operator;
        this.rLow = rLow;
        this.rHigh = rHigh;
        this.rMax = rMax;
        this.W = W;
        this.reconfigurationSpeed = reconfigurationSpeed;
        this.running = false;
        try {
            monitorFileWriter = new BufferedWriter(new FileWriter("Operator monitor.txt", true));
        } catch (IOException ex) {
            AsteroidsOperator.logger.log(SEVERE, "[Monitor] Failed to create file writer");
        }
    }

    @Override
    public synchronized void run() {
        AsteroidsOperator.logger.log(INFO, "[Monitor] Start");
        MonitorPacket current;
        MonitorPacket average;
        int C = operator.getServers().size();
        int Cnew = C;
        double R, X, U;
        running = true;
        while (running) {
            C = operator.getServers().size();
            Cnew = C;
            current = getCurrent();
            average = getAverage();
            AsteroidsOperator.logger.log(INFO, "[Monitor] Average: {0}", average);
            //System.out.println(average);
            R = average.getResponseTime();
            X = average.getThroughput();
            U = average.getUtilization();

            if (R > rHigh) {
                AsteroidsOperator.logger.log(INFO, "[Monitor] Acquire");
                Cnew = operator.getPlanner().acquire(C, U, X, R);
            } else if (R < rLow) {
                AsteroidsOperator.logger.log(INFO, "[Monitor] Release");
                Cnew = operator.getPlanner().release(C, U, X, R);
            }

            if (C < Cnew) {
                System.out.println("Start " + (Cnew - C) + " servers");
                operator.startServers(Cnew - C);
            }
            if (C > Cnew) {
                System.out.println("Remove " + (C - Cnew) + " servers");
                operator.removeServers(C - Cnew);
            }
            
            writeToFile(current, average, Cnew - C);
            
            try {
                Thread.sleep(reconfigurationSpeed * 1000);
            } catch (InterruptedException ex) {

            }
        }
    }

    private void writeToFile(MonitorPacket current, MonitorPacket average, int amount) {
        try {
            String s = System.currentTimeMillis() + ": " + current.getResponseTime() + " " + average.getResponseTime() + " " + (amount > 0 ? ("+" + amount) : amount);
            System.out.println(s);
            monitorFileWriter.write(s);
            monitorFileWriter.flush();
        } catch (IOException ex) {
        }
    }

    private MonitorPacket getCurrent() {
        MonitorPacket current = new MonitorPacket(0, 0, 0);
        int amountServers = operator.getServers().size();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            MonitorPacket serverCurrent = it.next().getMonitorPacketList().getCurrent();
            current.setResponseTime(current.getResponseTime() + serverCurrent.getResponseTime() / amountServers);
            current.setThroughput(current.getThroughput() + serverCurrent.getThroughput() / amountServers);
            current.setUtilization(current.getUtilization() + serverCurrent.getUtilization() / amountServers);
        }
        return current;
    }

    private MonitorPacket getAverage() {
        MonitorPacket average = new MonitorPacket(0, 0, 0);
        int amountServers = operator.getServers().size();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            MonitorPacket serverAverage = it.next().getMonitorPacketList().getAverage();
            average.setResponseTime(average.getResponseTime() + serverAverage.getResponseTime() / amountServers);
            average.setThroughput(average.getThroughput() + serverAverage.getThroughput() / amountServers);
            average.setUtilization(average.getUtilization() + serverAverage.getUtilization() / amountServers);
        }
        return average;
    }

    public int getW() {
        return W;
    }

    public void stopRunning() {
        running = false;
        try {
            monitorFileWriter.close();
        } catch (IOException ex) {
        }
    }

}
