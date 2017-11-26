package apps.JmriDemo;

import java.awt.GraphicsEnvironment;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 */
public class JMRIdemoTest {

    @Test
    @Ignore("Causes exception")
    public void testCTor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        JMRIdemo t = new JMRIdemo();
        Assert.assertNotNull("exists", t);
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

    // private final static Logger log = LoggerFactory.getLogger(JMRIdemoTest.class);
}
