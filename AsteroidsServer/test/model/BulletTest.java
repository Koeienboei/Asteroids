/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import model.updates.BulletUpdate;
import model.updates.Update;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tomei
 */
public class BulletTest {
    
    public BulletTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of nextStep method, of class Bullet.
     */
    @Test
    public void testNextStep() {
        System.out.println("nextStep");
        AsteroidsModel model = new AsteroidsModel(800,800);
        Bullet instance = new Bullet(0.0,0.0,0.0,0.0,1.0, model);
        System.out.println(instance);
        instance.nextStep();
        System.out.println(instance);
    }

    /**
     * Test of getDirection method, of class Bullet.
     */
    @Test
    public void testGetDirection() {
        System.out.println("getDirection");
        Bullet instance = null;
        double expResult = 0.0;
        double result = instance.getDirection();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStepsToLive method, of class Bullet.
     */
    @Test
    public void testGetStepsToLive() {
        System.out.println("getStepsToLive");
        Bullet instance = null;
        int expResult = 0;
        int result = instance.getStepsToLive();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class Bullet.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        AsteroidsModel model = new AsteroidsModel(800,800);
        Bullet updateBullet = new Bullet(1.0,1.0,1.0,1.0,0.5, model);
        System.out.println(updateBullet);
        Update update = new BulletUpdate(updateBullet);
        Bullet instance = new Bullet(0.0,0.0,0.0,0.0,1.0, model);
        System.out.println(instance);
        instance.update(update);
        System.out.println(instance);
    }

    /**
     * Test of getUpdate method, of class Bullet.
     */
    @Test
    public void testGetUpdate() {
        System.out.println("getUpdate");
        AsteroidsModel model = new AsteroidsModel(800,800);
        Bullet instance = new Bullet(0.0,0.0,0.0,0.0,1.0, model);
        Update expResult = new BulletUpdate(new Bullet(0.0,0.0,0.0,0.0,1.0, model));
        Update result = instance.getUpdate();
        assertEquals(expResult, result);
    }
    
}
