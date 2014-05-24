package net.alb.sysmonitor;

import java.io.Serializable;

/**
 * Created by mymon_000 on 14-1-7.
 */
public class SystemStatus implements Serializable {
    private int cpuCores;
    private double cpuUsage;
    private long memoryTotal;
    private long memoryUsed;
    private long memoryFree;
    private double memoryUsage;

    public SystemStatus() {

    }

    public SystemStatus(SystemStatus other) {
        this.cpuCores=other.getCpuCores();
        this.cpuUsage=other.getCpuUsage();
        this.memoryTotal=other.getMemoryTotal();
        this.memoryUsed=other.getMemoryUsed();
        this.memoryFree=other.getMemoryFree();
        this.memoryUsage=other.getMemoryUsage();
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getMemoryTotal() {
        return memoryTotal;
    }

    public void setMemoryTotal(long memoryTotal) {
        this.memoryTotal = memoryTotal;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public long getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(long memoryFree) {
        this.memoryFree = memoryFree;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String summarize() {
        return String.format("CpuCores:%d\tCpuUsage:%.2f%%\tMemoryTotal:%dMB\tMemoryUsed:%dMB\tMemoryFree:%dMB\tMemoryUsage:%.2f%%",
                cpuCores,cpuUsage,memoryTotal,memoryUsed,memoryFree,memoryUsage);
    }
}
