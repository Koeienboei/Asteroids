/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operator;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author tomei
 */
public class Monitor implements Observer {

    private Operator operator;
    
    public Monitor(Operator operator) {
        this.operator = operator;
        operator.addObserver(this);
    }
    
    @Override
    public void update(Observable o, Object o1) {
        if (operator.getUtilization() < operator.getUlow()) {
            
        }
    }
    
    
    
}
