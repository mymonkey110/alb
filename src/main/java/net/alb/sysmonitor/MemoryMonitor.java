/**
 * Created by mymon_000 on 13-12-20.
 */
package net.alb.sysmonitor;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class MemoryMonitor {

    private final static long KB = 1024;
    private final static long MB = 1024 * KB;
    private final static long GB = 1024 * MB;

    private Mem mem = null;

    public MemoryMonitor() throws SigarException {
        Sigar sigar = new Sigar();
        mem = sigar.getMem();
    }

    public long getTotalMemory() {
        return mem.getTotal();
    }

    public long getTotalMemoryWithKB() {
        return mem.getTotal() / KB;
    }

    public long getTotalMemoryWithMB() {
        return mem.getTotal() / MB;
    }

    public long getTotalMemoryWithGB() {
        return mem.getTotal() / GB;
    }


    public long getFreeMemory() {
        return mem.getFree();
    }


    public long getFreeMemoryWithKB() {
        return mem.getFree() / KB;
    }


    public long getFreeMemoryWithMB() {
        return mem.getFree() / MB;
    }


    public long getFreeMemoryWithGB() {
        return mem.getFree() / GB;
    }


    public long getUsedMemory() {
        return mem.getUsed();
    }


    public long getUsedMemoryWithKB() {
        return mem.getUsed() / KB;
    }


    public long getUsedMemoryWithMB() {
        return mem.getUsed() / MB;
    }


    public long getUsedMemoryWithGB() {
        return mem.getUsed() / GB;
    }


    public double getMemoryUsage() {
        double result=(double)100*getUsedMemory()/getTotalMemory();
        return Double.parseDouble(String.format("%.2f",result));
    }

    public double getFreeMemoryUsage() {
        double result=(double)100*getFreeMemory()/getTotalMemory();
        return Double.parseDouble(String.format("%.2f",result));
    }
}
