package apps.util.issuereporter;

import jmri.util.JUnitUtil;

import org.junit.jupiter.api.*;
import org.junit.Assert;

/**
 * Minimal test skeleton for EnhancementRequest class
 */
public class EnhancementRequestTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("EnhancementRequest constructor", new EnhancementRequest("title", "body"));
    }

    @BeforeEach
    public void setUp() {
        JUnitUtil.setUp();
    }

    @AfterEach
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}

