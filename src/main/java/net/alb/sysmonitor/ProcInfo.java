package net.alb.sysmonitor;

/**
 * Created by mymon_000 on 14-1-15.
 */
public class ProcInfo {
    //Process ID,for debug
    private int pid;
    //Process command line
    private String cmd;
    //Process's CPU usage,refer to "top"
    private double cpuUsage;
    //Process occupy the memory percent,refer to "top"
    private double memoryUsage;
    //Process's virtual memory size,MB
    private long virtualMemory;
    //Process's physics memory size,MB
    private long physicsMemory;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public long getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(long virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public long getPhysicsMemory() {
        return physicsMemory;
    }

    public void setPhysicsMemory(long physicsMemory) {
        this.physicsMemory = physicsMemory;
    }
}
