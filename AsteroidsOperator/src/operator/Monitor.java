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
        int C;
        int Cnew;
        Object[] RXU;
        double R, X;
        LinkedList<Double> U;
        running = true;
        while (running) {
            C = operator.getServers().size();

            RXU = monitor(W);
            R = (double)RXU[0];
            X = (double)RXU[1];
            U = (LinkedList<Double>)RXU[2];

            if (R > rHigh) {
                AsteroidsOperator.logger.log(INFO, "[Monitor] Acquire");
                operator.getPlanner().acquire(U, X, R);
            } else if (R < rLow) {
                AsteroidsOperator.logger.log(INFO, "[Monitor] Release");
                operator.getPlanner().release(U, X, R);
            }
            
            Cnew = operator.getServers().size();
            
            writeToFile(getCurrentResponseTime(), R, Cnew - C);
        }
    }

    public Object[] monitor(int seconds) {
        double X, R;
        LinkedList<Double> U;
        
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            it.next().getMonitorPacketList().reset();
        }
        
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
        }
        
        R = getAverageResponseTime();
        X = getAverageThroughput();
        U = getAverageUtilization();
        
        return new Object[]{R,X,U};
    }
    
    private void writeToFile(double currentResponseTime, double averageResponseTime, int amount) {
        try {
            String s = System.currentTimeMillis() + ": " + currentResponseTime + " " + averageResponseTime + " " + (amount > 0 ? ("+" + amount) : amount);
            System.out.println(s);
            monitorFileWriter.write(s);
            monitorFileWriter.flush();
        } catch (IOException ex) {
        }
    }

    public LinkedList<Double> getD() {
        LinkedList<Double> D = new LinkedList<>();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            MonitorPacket serverAverage = it.next().getMonitorPacketList().getAverage();
            D.add(serverAverage.getUtilization() / serverAverage.getThroughput());
        }
        return D;
    }

    public double getDmax() {
        double Dmax = 0.0;
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            MonitorPacket serverAverage = it.next().getMonitorPacketList().getAverage();
            if (Dmax < serverAverage.getUtilization() / serverAverage.getThroughput()) {
                Dmax = serverAverage.getUtilization() / serverAverage.getThroughput();
            }
        }
        return Dmax;
    }

    public double getDtotal() {
        double Dtotal = 0.0;
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            MonitorPacket serverAverage = it.next().getMonitorPacketList().getAverage();
            Dtotal += serverAverage.getUtilization() / serverAverage.getThroughput();
        }
        return Dtotal;
    }

    public double getDaverage() {
        double Daverage = 0.0;
        int size = operator.getServers().size();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            MonitorPacket serverAverage = it.next().getMonitorPacketList().getAverage();
            Daverage += (serverAverage.getUtilization() / serverAverage.getThroughput()) / size;
        }
        return Daverage;
    }

    public double getAverageResponseTime() {
        double averageResponseTime = 0.0;
        int amountServers = operator.getServers().size();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            double serverAverageResponseTime = it.next().getMonitorPacketList().getAverageResponseTime();
            averageResponseTime += serverAverageResponseTime / amountServers;
        }
        return averageResponseTime;
    }

    private double getCurrentResponseTime() {
        double currentResponseTime = 0.0;
        int amountServers = operator.getServers().size();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            double serverCurrentResponseTime = it.next().getMonitorPacketList().getCurrentResponseTime();
            currentResponseTime += serverCurrentResponseTime / amountServers;
        }
        return currentResponseTime;
    }

    public double getAverageThroughput() {
        double averageThroughput = 0.0;
        int amountServers = operator.getServers().size();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            double serverAverageThroughput = it.next().getMonitorPacketList().getAverageThroughput();
            averageThroughput += serverAverageThroughput / amountServers;
        }
        return averageThroughput;
    }

    public LinkedList<Double> getAverageUtilization() {
        LinkedList<Double> averageUtilization = new LinkedList<>();
        Iterator<ServerHandler> it = operator.getServers().iterator();
        while (it.hasNext()) {
            averageUtilization.add(it.next().getMonitorPacketList().getAverageUtilization());
        }
        return averageUtilization;
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
