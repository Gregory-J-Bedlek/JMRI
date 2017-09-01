package jmri.jmrit.operations.trains.excel;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017	
 */
public class SetupExcelProgramSwitchListFrameActionTest {

    @Test
    public void testCTor() {
        SetupExcelProgramSwitchListFrameAction t = new SetupExcelProgramSwitchListFrameAction("test action");
        Assert.assertNotNull("exists",t);
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(SetupExcelProgramSwitchListFrameActionTest.class.getName());

}