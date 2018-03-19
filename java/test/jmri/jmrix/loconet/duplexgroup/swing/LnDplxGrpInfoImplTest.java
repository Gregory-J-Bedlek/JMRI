package jmri.jmrix.loconet.duplexgroup.swing;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test simple functioning of LnDplxGrpInfoImpl
 *
 * @author	Paul Bender Copyright (C) 2016
 */
public class LnDplxGrpInfoImplTest {
    
    @Test
    public void testCtor() {
        jmri.jmrix.loconet.LocoNetSystemConnectionMemo memo = new jmri.jmrix.loconet.LocoNetSystemConnectionMemo();
        LnDplxGrpInfoImpl action = new LnDplxGrpInfoImpl(memo);
        Assert.assertNotNull("exists", action);
        memo.dispose();
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {        JUnitUtil.tearDown();    }
}
