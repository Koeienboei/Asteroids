/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Collection;
import java.util.LinkedList;
import model.updates.AsteroidUpdate;
import model.updates.BulletUpdate;
import model.updates.ControllerUpdate;
import model.updates.SpaceshipUpdate;
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
public class AsteroidsModelTest {
    
    public AsteroidsModelTest() {
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
     * Test of spawnAsteroid method, of class AsteroidsModel.
     */
    @Test
    public void testSpawnAsteroid() {
        System.out.println("spawnAsteroid");
        AsteroidsModel instance = new AsteroidsModel(800,800);
        instance.spawnAsteroid();
        System.out.println(instance);
        instance.spawnAsteroid();
        System.out.println(instance);
        instance.spawnAsteroid();
        System.out.println(instance);
    }

    /**
     * Test of nextStep method, of class AsteroidsModel.
     */
    @Test
    public void testNextStep() {
        System.out.println("nextStep");
        AsteroidsModel instance = new AsteroidsModel(800,800);
        System.out.println(instance);
        instance.nextStep();
        System.out.println(instance);
        instance.spawnAsteroid();
        System.out.println(instance);
        instance.nextStep();
        System.out.println(instance);
        instance.addNewObjects();
        System.out.println(instance);
        instance.addGameObject(new Bullet(0.0,0.0,0.0,0.0,1.0,instance));
        instance.addGameObject(new Spaceship(instance));
        System.out.println(instance);
        instance.addNewObjects();
        System.out.println(instance);
        instance.nextStep();
        System.out.println(instance);
    }

    /**
     * Test of calculateCollisions method, of class AsteroidsModel.
     */
    @Test
    public void testCalculateCollisions() {
        System.out.println("calculateCollisions");
        AsteroidsModel instance = new AsteroidsModel(800,800);
        instance.spawnAsteroid();
        instance.addGameObject(new Bullet(0.0,0.0,0.0,0.0,1.0,instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addNewObjects();
        instance.calculateCollisions();
    }

    /**
     * Test of calculateFireSpaceships method, of class AsteroidsModel.
     */
    @Test
    public void testCalculateFireSpaceships() {
        System.out.println("calculateFireSpaceships");
        AsteroidsModel instance = new AsteroidsModel(800,800);
        instance.spawnAsteroid();
        instance.addGameObject(new Bullet(0.0,0.0,0.0,0.0,1.0,instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addNewObjects();
        instance.calculateFireSpaceships();
    }

    /**
     * Test of calculateResetSpaceships method, of class AsteroidsModel.
     */
    @Test
    public void testCalculateResetSpaceships() {
        System.out.println("calculateResetSpaceships");
        AsteroidsModel instance = new AsteroidsModel(800,800);
        instance.spawnAsteroid();
        instance.addGameObject(new Bullet(0.0,0.0,0.0,0.0,1.0,instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addGameObject(new Spaceship(instance));
        instance.calculateResetSpaceships();
    }

    /**
     * Test of addNewObjects method, of class AsteroidsModel.
     */
    @Test
    public void testAddNewObjects() {
        System.out.println("addNewObjects");
        AsteroidsModel instance = new AsteroidsModel(800,800);
        instance.spawnAsteroid();
        instance.spawnAsteroid();
        instance.addGameObject(new Bullet(0.0,0.0,0.0,0.0,1.0,instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addGameObject(new Spaceship(instance));
        instance.addNewObjects();
        instance.addNewObjects();
    }

    /**
     * Test of addGameObject method, of class AsteroidsModel.
     */
    @Test
    public void testAddGameObject() {
        System.out.println("addGameObject");
        AsteroidsModel instance = new AsteroidsModel(800,800);
        GameObject o1 = new Spaceship(instance);
        instance.addGameObject(o1);
        System.out.println(instance);
        GameObject o2 = new Bullet(0.0,0.0,0.0,0.0,1.0,instance);
        instance.addGameObject(o2);
        System.out.println(instance);
        GameObject o3 = new LargeAsteroid(instance);
        instance.addGameObject(o3);
        System.out.println(instance);
    }

    /**
     * Test of removeSpaceship method, of class AsteroidsModel.
     */
    @Test
    public void testRemoveSpaceship() {
        System.out.println("removeSpaceship");
        int id = 0;
        AsteroidsModel instance = null;
        instance.removeSpaceship(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addUpdate method, of class AsteroidsModel.
     */
    @Test
    public void testAddUpdate() {
        System.out.println("addUpdate");
        Update update = null;
        AsteroidsModel instance = null;
        instance.addUpdate(update);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addUpdates method, of class AsteroidsModel.
     */
    @Test
    public void testAddUpdates() {
        System.out.println("addUpdates");
        LinkedList<Update> updates = null;
        AsteroidsModel instance = null;
        instance.addUpdates(updates);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class AsteroidsModel.
     */
    @Test
    public void testUpdate_0args() {
        System.out.println("update");
        AsteroidsModel instance = null;
        instance.update();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class AsteroidsModel.
     */
    @Test
    public void testUpdate_Update() {
        System.out.println("update");
        Update update = null;
        AsteroidsModel instance = null;
        instance.update(update);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of processAsteroidUpdate method, of class AsteroidsModel.
     */
    @Test
    public void testProcessAsteroidUpdate() {
        System.out.println("processAsteroidUpdate");
        AsteroidUpdate update = null;
        AsteroidsModel instance = null;
        instance.processAsteroidUpdate(update);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of processBulletUpdate method, of class AsteroidsModel.
     */
    @Test
    public void testProcessBulletUpdate() {
        System.out.println("processBulletUpdate");
        BulletUpdate update = null;
        AsteroidsModel instance = null;
        instance.processBulletUpdate(update);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of processSpaceshipUpdate method, of class AsteroidsModel.
     */
    @Test
    public void testProcessSpaceshipUpdate() {
        System.out.println("processSpaceshipUpdate");
        SpaceshipUpdate update = null;
        AsteroidsModel instance = null;
        instance.processSpaceshipUpdate(update);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of processControllerUpdate method, of class AsteroidsModel.
     */
    @Test
    public void testProcessControllerUpdate() {
        System.out.println("processControllerUpdate");
        ControllerUpdate update = null;
        AsteroidsModel instance = null;
        instance.processControllerUpdate(update);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAsteroid method, of class AsteroidsModel.
     */
    @Test
    public void testGetAsteroid() {
        System.out.println("getAsteroid");
        int objectId = 0;
        AsteroidsModel instance = null;
        Asteroid expResult = null;
        Asteroid result = instance.getAsteroid(objectId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBullet method, of class AsteroidsModel.
     */
    @Test
    public void testGetBullet() {
        System.out.println("getBullet");
        int objectId = 0;
        AsteroidsModel instance = null;
        Bullet expResult = null;
        Bullet result = instance.getBullet(objectId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpaceship method, of class AsteroidsModel.
     */
    @Test
    public void testGetSpaceship() {
        System.out.println("getSpaceship");
        int objectId = 0;
        AsteroidsModel instance = null;
        Spaceship expResult = null;
        Spaceship result = instance.getSpaceship(objectId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBullets method, of class AsteroidsModel.
     */
    @Test
    public void testGetBullets() {
        System.out.println("getBullets");
        AsteroidsModel instance = null;
        Collection<Bullet> expResult = null;
        Collection<Bullet> result = instance.getBullets();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAsteroids method, of class AsteroidsModel.
     */
    @Test
    public void testGetAsteroids() {
        System.out.println("getAsteroids");
        AsteroidsModel instance = null;
        Collection<Asteroid> expResult = null;
        Collection<Asteroid> result = instance.getAsteroids();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSpaceships method, of class AsteroidsModel.
     */
    @Test
    public void testGetSpaceships() {
        System.out.println("getSpaceships");
        AsteroidsModel instance = null;
        Collection<Spaceship> expResult = null;
        Collection<Spaceship> result = instance.getSpaceships();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGameObjects method, of class AsteroidsModel.
     */
    @Test
    public void testGetGameObjects() {
        System.out.println("getGameObjects");
        AsteroidsModel instance = null;
        LinkedList<GameObject> expResult = null;
        LinkedList<GameObject> result = instance.getGameObjects();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAddQueue method, of class AsteroidsModel.
     */
    @Test
    public void testGetAddQueue() {
        System.out.println("getAddQueue");
        AsteroidsModel instance = null;
        Collection<GameObject> expResult = null;
        Collection<GameObject> result = instance.getAddQueue();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHeight method, of class AsteroidsModel.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        AsteroidsModel instance = null;
        int expResult = 0;
        int result = instance.getHeight();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWidth method, of class AsteroidsModel.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        AsteroidsModel instance = null;
        int expResult = 0;
        int result = instance.getWidth();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class AsteroidsModel.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        AsteroidsModel instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
