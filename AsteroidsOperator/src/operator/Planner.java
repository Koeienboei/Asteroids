/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.util.logging.Level.FINE;

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

    public int acquire(int C, double U, double X, double R) {
        AsteroidsOperator.logger.log(FINE, "[Planner] Acquire");
        System.out.println("Acquire");
        double N;
        double D;
        double[] UR;
        do {
            N = X * R;
            D = C * (U / X);
            UR = balancedSystemBounds(C, D, N);
            U = UR[0];
            R = UR[1];
            if (R >= (Rhigh + Rlow) / 2) {
                C++;
            }
        } while (R >= (Rhigh + Rlow) / 2);
        return C;
    }

    public int release(int C, double U, double X, double R) {
        AsteroidsOperator.logger.log(FINE, "[Planner] Release");
        System.out.println("Release");
        double N;
        double D;
        double[] UR;
        while (C > 1) {
            C--;
            N = X * R;
            D = C * (U / X);
            UR = balancedSystemBounds(C, D, N);
            U = UR[0];
            R = UR[1];
            if (R >= (Rhigh + Rlow) / 2) {
                C++;
                break;
            }
        }
        return C;
    }

    private double[] balancedSystemBounds(int C, double D, double N) {
        System.out.println("BSB(" + C + "," + D + "," + N + ")");
        double Xupper, Xlower, Rupper, Rlower, U, R, X;
        Xlower = N / (D + (N - 1) * D);
        Xupper = min(1 / D, N / (D + (N - 1) * D));
        Rlower = max(N * D, D + (N - 1) * D);
        Rupper = D + (N - 1) * D;
        X = (Xupper + Xlower) / 2;
        R = (Rupper + Rlower) / 2;
        U = X * D;
        System.out.println("Xlower = " + Xlower);
        System.out.println("Xupper = " + Xupper);
        System.out.println("Rlower = " + Rlower);
        System.out.println("Rupper = " + Rupper);
        System.out.println("X = " + X);
        System.out.println("R = " + R);
        System.out.println("U = " + U);
        return new double[]{U, R};
    }

}
