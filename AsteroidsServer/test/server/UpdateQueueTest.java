/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import model.AsteroidsModel;
import model.Bullet;
import model.GameObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import packeges.Address;
import packeges.UpdatePacket;

/**
 *
 * @author tomei
 */
public class UpdateQueueTest {
    
    private ClientData clientData;
    private AsteroidsModel model;
    private UpdateQueue updateQueue;
    
    public UpdateQueueTest() {
        clientData = new ClientData(1, new Address("192.168.178.1", 8000));
        model = new AsteroidsModel(800,800);
        updateQueue = new UpdateQueue(clientData, model);
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
     * Test of pop method, of class UpdateQueue.
     */
    @Test
    public void testPop() {
        System.out.println("pop");
        UpdateQueue instance = null;
        UpdatePacket expResult = null;
        UpdatePacket result = instance.pop();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class UpdateQueue.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        AsteroidsModel model = new AsteroidsModel(800,800);
        ClientData clientData = new ClientData(1, new Address("ip", 80));
        GameObject gameObject = new Bullet(0.0,0.0,0.0,0.0,1.0, model);
        UpdateQueue instance = new UpdateQueue(clientData, model);
        instance.add(gameObject);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of initialize method, of class UpdateQueue.
     */
    @Test
    public void testInitialize() {
        System.out.println("initialize");
        UpdateQueue instance = null;
        instance.initialize();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
