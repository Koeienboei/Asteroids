/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.updates;

import asteroidsserver.AsteroidsServer;
import java.io.Serializable;
import static java.util.logging.Level.FINE;
import model.GameObject;

/**
 *
 * @author Tom
 */
public abstract class Update implements Serializable {
    
    protected transient GameObject gameObject;
    protected transient int updateSpeed;
    protected int objectId;
    
    public Update(GameObject gameObject) {
        AsteroidsServer.logger.log(FINE, "Create Update[{0}]", gameObject.getId());
        this.gameObject = gameObject;
        this.objectId = gameObject.getId();
    }

    public abstract void reset();
    
    public abstract boolean repeat();
    
    public GameObject getGameObject() {
        return gameObject;
    }

    public int getUpdateSpeed() {
        return updateSpeed;
    }
    
    public int getObjectId() {
        return objectId;
    }

}
