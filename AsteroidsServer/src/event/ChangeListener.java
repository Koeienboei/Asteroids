/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author tomei
 */
public interface ChangeListener {
  
  
    
  public void changeEventReceived(ChangeEvent event);
  

  
}
