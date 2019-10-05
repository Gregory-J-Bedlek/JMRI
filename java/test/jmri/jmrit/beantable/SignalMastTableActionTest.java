package jmri.jmrit.beantable;

import jmri.SignalMast;
import jmri.util.JUnitUtil;
import jmri.util.junit.annotations.*;
import org.junit.*;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class SignalMastTableActionTest extends AbstractTableActionBase<SignalMast> {

    @Test
    public void testCTor() {
        Assert.assertNotNull("exists", a);
    }

    @Override
    public String getTableFrameName() {
        return Bundle.getMessage("TitleSignalMastTable");
    }

    @Override
    @Test
    public void testGetClassDescription() {
        Assert.assertEquals("SignalMast Table Action class description", "Signal Mast Table", a.getClassDescription());
    }

    /**
     * Check the return value of includeAddButton. The table generated by this
     * action includes an Add Button.
     */
    @Override
    @Test
    public void testIncludeAddButton() {
        Assert.assertTrue("Default include add button", a.includeAddButton());
    }

    @Override
    public String getAddFrameName(){
        return Bundle.getMessage("TitleAddSignalMast");
    }

    @Test
    @Ignore("Signal Mast create frame does not have a hardware address")
    @ToDo("Re-write parent class test to use the right name")
    @Override
    public void testAddThroughDialog() {
    }

    @Test
    @Ignore("Signal Mast create frame does not have a hardware address")
    @ToDo("Re-write parent class test to use the right name")
    @Override
    public void testEditButton() {
    }

    // The minimal setup for log4J
    @Override
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.resetProfileManager();
        helpTarget = "package.jmri.jmrit.beantable.SignalMastTable"; 
        a = new SignalMastTableAction();
    }

    @Override
    @After
    public void tearDown() {
        a = null;
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(SignalMastTableActionTest.class);
}
