/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

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
        operator.startServer();
    }
    
    public void release() {
        if (operator.getServerDataList().size() > 1) {
            operator.removeServer();
        }
    }
    
}
