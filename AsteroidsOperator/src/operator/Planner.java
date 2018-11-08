/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import asteroidsoperator.AsteroidsOperator;
import static java.util.logging.Level.FINE;

/**
 *
 * @author tomei
 */
public class Planner {
    
    private Operator operator;
    
    public Planner(Operator operator) {
        this.operator = operator;
    }
    
    public void acquire() {
        AsteroidsOperator.logger.log(FINE, "[Planner] Acquire");
        operator.startServer();
    }
    
    public void release() {
        AsteroidsOperator.logger.log(FINE, "[Planner] Release");
        if (operator.getServers().size() > 1) {
            operator.removeServer();
        }
    }
    
}
