/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import static java.lang.Double.max;
import static java.lang.Double.min;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import static java.util.logging.Level.FINE;
import java.util.logging.Logger;

/**
 *
 * @author tomei
 */
public class Planner {

    private Operator operator;

    double Rlow, Rhigh, Rmax;

    public Planner(Operator operator, double Rlow, double Rhigh, double Rmax) {
        this.operator = operator;
        this.Rlow = Rlow;
        this.Rhigh = Rhigh;
        this.Rmax = Rmax;
    }

    public void acquire(LinkedList<Double> U, double X, double R) {
        AsteroidsOperator.logger.log(FINE, "[Planner] Acquire");
        System.out.println("Acquire");
        Object[] RXU;
        do {

            R = balancedSystemBounds(R, X, U);
            if (R >= (Rhigh + Rlow) / 2) {
                operator.startServer();

                RXU = operator.getMonitor().monitor(5);

                R = (double) RXU[0];
                X = (double) RXU[1];
                U = (LinkedList<Double>) RXU[2];
            }
        } while (R >= (Rhigh + Rlow) / 2);
    }

    public void release(LinkedList<Double> U, double X, double R) {
        AsteroidsOperator.logger.log(FINE, "[Planner] Release");
        System.out.println("Release");
        Object[] RXU;
        while (operator.getServers().size() > 1) {
            operator.removeServer();

            RXU = operator.getMonitor().monitor(5);

            R = (double) RXU[0];
            X = (double) RXU[1];
            U = (LinkedList<Double>) RXU[2];

            R = balancedSystemBounds(R, X, U);

            if (R >= (Rhigh + Rlow) / 2) {
                operator.startServer();
                break;
            }
        }
    }

    private double balancedSystemBounds(double R, double X, LinkedList<Double> U) {
        double Dmax, Dtot, Dave;
        double Xupper, Xlower;
        double Rupper, Rlower;
        double N;
        LinkedList<Double> D;

        N = X * R;
        D = computeD(U, X);

        Dmax = getMax(D);
        Dtot = getTot(D);
        Dave = Dtot / D.size();

        //Xlower = N / (Dtot + (N - 1) * Dmax);
        //Xupper = min(1 / Dmax, N / (Dtot + (N - 1) * Dave));
        Rlower = max(N * Dmax, Dtot + (N - 1) * Dave);
        Rupper = Dtot + (N - 1) * Dmax;

        //X = (Xupper + Xlower) / 2;
        R = (Rupper + Rlower) / 2;

        return R;
    }

    private LinkedList<Double> computeU(double X, LinkedList<Double> D) {
        LinkedList<Double> U = new LinkedList<>();
        Iterator<Double> it = D.iterator();
        while (it.hasNext()) {
            U.add(X * it.next());
        }
        return U;
    }

    private LinkedList<Double> computeD(LinkedList<Double> U, double X) {
        LinkedList<Double> N = new LinkedList<>();
        Iterator<Double> it = U.iterator();
        while (it.hasNext()) {
            N.add(it.next() / X);
        }
        return N;
    }

    private double getMax(LinkedList<Double> list) {
        double max = 0.0;
        double current;
        Iterator<Double> it = list.iterator();
        while (it.hasNext()) {
            current = it.next();
            if (current > max) {
                max = current;
            }
        }
        return max;
    }

    private double getTot(LinkedList<Double> list) {
        double tot = 0.0;
        Iterator<Double> it = list.iterator();
        while (it.hasNext()) {
            tot += it.next();
        }
        return tot;
    }

    private double getAve(LinkedList<Double> list) {
        double ave = 0.0;
        int size = list.size();
        Iterator<Double> it = list.iterator();
        while (it.hasNext()) {
            ave += it.next() / size;
        }
        return ave;
    }

}
