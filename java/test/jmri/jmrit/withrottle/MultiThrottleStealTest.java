package jmri.jmrit.withrottle;

import java.beans.PropertyChangeEvent;
//import jmri.InstanceManager;
//import jmri.NamedBeanHandleManager;
import jmri.util.JUnitUtil;
import org.junit.*;

/**
 * Test stealing behavior of MultiThrottle
 *
 * @author	Paul Bender Copyright (C) 2018
 */
public class MultiThrottleStealTest {
        
   private ControllerInterfaceScaffold cis = null; 
   private ThrottleControllerListenerScaffold tcls = null;
   private MultiThrottle throttle = null;
    
    @Test
    public void testSetAndReleaseLongAddressWithSteal(){
       // set the address
       throttle.handleMessage("+L1234<;>L1234");
       Assert.assertFalse("Address Found",tcls.hasAddressBeenFound());
       // the throttle manager send a steal request, which triggers a message 
       // from the controller to the device 
       Assert.assertEquals("outgoing message after throttle request", "MASL1234<;>L1234",cis.getLastPacket() );
       // the device then confirms the steal.
       throttle.handleMessage("SL1234<;>L1234");
       // and the sequence continues as normal.
       Assert.assertTrue("Address Found",tcls.hasAddressBeenFound());
       // then release it.
       throttle.handleMessage("-L1234<;>r");
       Assert.assertEquals("outgoing yymessage after throttle release", "MA-L1234<;>",cis.getLastPacket() );
       Assert.assertTrue("Address Released",tcls.hasAddressBeenReleased());
    }

    @Test
    public void testRefuseOneStealOne() {
       // set the address
       throttle.handleMessage("+L1234<;>L1234");
       Assert.assertFalse("Address Found",tcls.hasAddressBeenFound());
       // the throttle manager send a steal request, which triggers a message 
       // from the controller to the device 
       Assert.assertEquals("outgoing message after throttle request", "MASL1234<;>L1234",cis.getLastPacket() );
       // to refuse the steal, we have to send a different address
       throttle.handleMessage("+L4321<;>L4321");
       Assert.assertFalse("Address Found",tcls.hasAddressBeenFound());
       // from the controller to the device 
       Assert.assertEquals("outgoing message after throttle request", "MASL4321<;>L4321",cis.getLastPacket() );
       // the device then confirms the steal.
       throttle.handleMessage("SL4321<;>L4321");
       // and the sequence continues as normal.
       Assert.assertTrue("Address Found",tcls.hasAddressBeenFound());
       // then release it.
       throttle.handleMessage("-L4321<;>r");
       Assert.assertTrue("Address Released",tcls.hasAddressBeenReleased());
       Assert.assertEquals("outgoing yymessage after throttle release", "MA-L4321<;>",cis.getLastPacket() );
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
        // these tests use the StealingThrottleManager.
        jmri.ThrottleManager m = new jmri.managers.StealingThrottleManager();
        jmri.InstanceManager.setThrottleManager(m);
        cis = new ControllerInterfaceScaffold();
        tcls = new ThrottleControllerListenerScaffold();
        throttle = new MultiThrottle('A',tcls,cis);
    }
    
    @After
    public void tearDown() {
        cis = null;
        tcls = null;
        throttle = null;
        JUnitUtil.tearDown();
    }
}
