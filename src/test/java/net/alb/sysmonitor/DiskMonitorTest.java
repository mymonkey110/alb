/**
 * Created by mymon_000 on 13-12-25.
 */

package net.alb.sysmonitor;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DiskMonitorTest {
    private static DiskMonitor dm = new DiskMonitor();

    @Ignore
    public void testGetTotalSpace() throws Exception {
        assertTrue(dm.getTotalFreeSpace() > 0);
        assertTrue(dm.getTotalUsedSpace() > 0);
        assertTrue(dm.getTotalSpace() >= (dm.getTotalFreeSpace() + dm.getTotalUsedSpace()));
    }

    @Ignore
    public void testGetTotalUsedSpace() throws Exception {
        assertTrue(dm.getTotalUsedSpace() > 0);
    }

    @Ignore
    public void testGetTotalFreeSpace() throws Exception {
        assertTrue(dm.getTotalFreeSpace() > 0);
    }

    @Ignore
    public void testGetDiskUsage() throws Exception {
        assertTrue(dm.getDiskUsage() > 0 && dm.getDiskUsage() <= 100);
    }

    @Ignore
    public void testGetDiskFreeUsage() throws Exception {
        assertTrue(dm.getDiskFreeUsage() > 0 && dm.getDiskFreeUsage() <= 100);
    }
}
