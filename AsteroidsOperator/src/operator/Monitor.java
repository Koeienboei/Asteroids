/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import java.util.Iterator;
import java.util.LinkedList;
import static java.util.logging.Level.FINE;

/**
 *
 * @author tomei
 */
public class Monitor extends Thread {

    private Operator operator;
    private Planner planner;

    private int uLow;
    private int uHigh;
    private int uMax;
    private int movingAverageLength;

    private LinkedList<Double> averageUtilizationList;
    
    private volatile boolean running;

    public Monitor(Operator operator, int uLow, int uHigh, int uMax, int movingAverageLength) {
        AsteroidsOperator.logger.log(FINE, "[Monitor] Create");
        this.operator = operator;
        this.uLow = uLow;
        this.uHigh = uHigh;
        this.uMax = uMax;
        this.movingAverageLength = movingAverageLength;
        this.running = false;
    }

    @Override
    public synchronized void run() {
        AsteroidsOperator.logger.log(FINE, "[Monitor] Start");
        running = true;
        while (running) {
            double averageUtilization = calculateAvarageUtilization();
            if (averageUtilization > uHigh) {
                planner.acquire();
            } else if (averageUtilization < uLow) {
                planner.release();
            }
            try {
                this.wait(1000);
            } catch (InterruptedException ex) {
                
            }
        }
    }

    private void calculateNewState() {
        averageUtilizationList.remove();
        averageUtilizationList.add(operator.getAverageUtilization());
    }
    
    private double calculateAvarageUtilization() {
        double averageUtilization = 0.0;
        Iterator<Double> it = averageUtilizationList.iterator();
        while (it.hasNext()) {
            averageUtilization += it.next();
        }
        return averageUtilization / movingAverageLength;
    }
    
    private void initialize() {
        averageUtilizationList = new LinkedList<>();
        for (int i = 0; i < movingAverageLength; i++) {
            averageUtilizationList.add(0.0);
        }
    }

    public void stopRunning() {
        running = false;
    }
    
}
