package net.alb.sysmonitor;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by mymon_000 on 13-12-25.
 */
public class CpuMonitorTest {
    private static CpuMonitor cm = null;

    static {
        try {
            cm = new CpuMonitor();
        } catch (Exception e) {
            fail("CpuMonitor Init Failed!");
        }
    }

    @Test
    public void testGetTotalCores() throws Exception {
        assertTrue(cm.getTotalCores() > 0);
    }


    @Test
    public void testGetCpuUsage() throws Exception {
        double usage = cm.getCpuUsage();
        assertTrue(usage >= 0 && usage <= 100);
    }

    @Test
    public void testGetCpuIdelUsage() throws Exception {
        double usage = cm.getCpuIdelUsage();
        assertTrue(usage >= 0 && usage <= 100);
    }
}
