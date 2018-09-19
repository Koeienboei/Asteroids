/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import model.AsteroidsModel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import packeges.Address;

/**
 *
 * @author tomei
 */
public class ServerTest {
    
    private Server server;
    
    public ServerTest() {
        server = new Server(8501, 800, 800, new Address("192.168.178.1", 8500));
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
     * Test of updateClientQueues method, of class Server.
     */
    @Test
    public void testUpdateClientQueues() {
        System.out.println("updateClientQueues");
        Server instance = null;
        instance.updateClientQueues();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of close method, of class Server.
     */
    @Test
    public void testClose() {
        System.out.println("close");
        Server instance = null;
        instance.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addClient method, of class Server.
     */
    @Test
    public void testAddClient() {
        System.out.println("addClient");
        
        server.addClient(1, new Address("192.168.178.1", 9001));
        server.addClient(2, new Address("192.168.178.2", 9002));
        server.addClient(3, new Address("192.168.178.3", 9003));
        
        System.out.println("server.getClientData(1): " + server.getClientData(1));
        System.out.println("server.getClientData(new Address(\"192.168.178.1\", 9001)): " + server.getClientData(new Address("192.168.178.1", 9001)));
        
        System.out.println("server.getClientData(2): " + server.getClientData(2));
        System.out.println("server.getClientData(new Address(\"192.168.178.2\", 9002)): " + server.getClientData(new Address("192.168.178.2", 9002)));
        
        System.out.println("server.getClientData(1): " + server.getClientData(3));
        System.out.println("server.getClientData(new Address(\"192.168.178.3\", 9003)): " + server.getClientData(new Address("192.168.178.3", 9003)));
        
        System.out.println("server.getClientData(0): " + server.getClientData(0));
        System.out.println("server.getClientData(4): " + server.getClientData(4));
        System.out.println("server.getClientData(-4): " + server.getClientData(-4));
        
        System.out.println("server.getClientData(new Address(\"192.168.178.1\", 9002)): " + server.getClientData(new Address("192.168.178.1", 9002)));
        System.out.println("server.getClientData(new Address(\"192.168.178.2\", 9001)): " + server.getClientData(new Address("192.168.178.2", 9001)));
        System.out.println("server.getClientData(new Address(\"192.168.178.4\", 9000)): " + server.getClientData(new Address("192.168.178.4", 9000)));
        
        Collection<ClientData> clientDataList = server.getClientDataList();
        
        System.out.println("ClientDataList:");
        Iterator<ClientData> it = clientDataList.iterator();
        while(it.hasNext()) {
            System.out.println("it.next: " + it.next());
        }
    }

    /**
     * Test of removeClient method, of class Server.
     */
    @Test
    public void testRemoveClient() {
        System.out.println("removeClient");
        int id = 0;
        Server instance = null;
        instance.removeClient(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClientData method, of class Server.
     */
    @Test
    public void testGetClientData() {
        System.out.println("getClientData");
        int id = 0;
        Server instance = null;
        ClientData expResult = null;
        ClientData result = instance.getClientData(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClientHandler method, of class Server.
     */
    @Test
    public void testGetClientHandler() {
        System.out.println("getClientHandler");
        int id = 0;
        Server instance = null;
        ClientHandler expResult = null;
        ClientHandler result = instance.getClientHandler(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOperatorHandler method, of class Server.
     */
    @Test
    public void testGetOperatorHandler() {
        System.out.println("getOperatorHandler");
        Server instance = null;
        OperatorHandler expResult = null;
        OperatorHandler result = instance.getOperatorHandler();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOccupation method, of class Server.
     */
    @Test
    public void testGetOccupation() {
        System.out.println("getOccupation");
        Server instance = null;
        int expResult = 0;
        int result = instance.getOccupation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClientDataList method, of class Server.
     */
    @Test
    public void testGetClientDataList() {
        System.out.println("getClientDataList");
        Server instance = null;
        Collection<ClientData> expResult = null;
        Collection<ClientData> result = instance.getClientDataList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClientHandlerList method, of class Server.
     */
    @Test
    public void testGetClientHandlerList() {
        System.out.println("getClientHandlerList");
        Server instance = null;
        Collection<ClientHandler> expResult = null;
        Collection<ClientHandler> result = instance.getClientHandlerList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getServerAddress method, of class Server.
     */
    @Test
    public void testGetServerAddress() {
        System.out.println("getServerAddress");
        Server instance = null;
        Address expResult = null;
        Address result = instance.getServerAddress();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOperatorAddress method, of class Server.
     */
    @Test
    public void testGetOperatorAddress() {
        System.out.println("getOperatorAddress");
        Server instance = null;
        Address expResult = null;
        Address result = instance.getOperatorAddress();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModel method, of class Server.
     */
    @Test
    public void testGetModel() {
        System.out.println("getModel");
        Server instance = null;
        AsteroidsModel expResult = null;
        AsteroidsModel result = instance.getModel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isRunning method, of class Server.
     */
    @Test
    public void testIsRunning() {
        System.out.println("isRunning");
        Server instance = null;
        boolean expResult = false;
        boolean result = instance.isRunning();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class Server.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        Observable o = null;
        Object clientData = null;
        Server instance = null;
        instance.update(o, clientData);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
