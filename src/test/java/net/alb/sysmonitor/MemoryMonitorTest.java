package net.alb.sysmonitor;

/**
 * Created by mymon_000 on 13-12-25.
 */

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class MemoryMonitorTest {
    private static MemoryMonitor mm = null;

    static {
        try {
            mm = new MemoryMonitor();
        } catch (Exception e) {
            fail("Initialize MemoryMonitor Fail!！");
        }
    }

    @Test
    public void testGetTotalMemory() throws Exception {
        assertTrue(mm.getFreeMemory() > 0);
        assertTrue(mm.getUsedMemory() > 0);
        assertTrue(mm.getTotalMemory() >= (mm.getUsedMemory() + mm.getFreeMemory()));
    }

    @Test
    public void testGetFreeMemory() throws Exception {
        assertTrue(mm.getFreeMemory() >= 0);
    }

    @Test
    public void testGetUsedMemory() throws Exception {
        assertTrue(mm.getUsedMemory() > 0);
    }

    @Test
    public void testGetMemoryUsage() throws Exception {
        double usage = mm.getMemoryUsage();
        assertTrue(usage > 0 && usage <= 100);
    }

    @Test
    public void testGetFreeMemoryUsage() throws Exception {
        double usage = mm.getFreeMemoryUsage();
        assertTrue(usage >= 0 && usage <= 100);
    }
}
