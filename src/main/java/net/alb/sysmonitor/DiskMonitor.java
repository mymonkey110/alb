package net.alb.sysmonitor;

import java.io.File;

public class DiskMonitor {

    private final static long KB = 1024;
    private final static long MB = 1024 * KB;
    private final static long GB = 1024 * MB;
    private final static long TB = 1024 * GB;

    private File[] roots = null;

    public DiskMonitor() {
        roots = File.listRoots();
    }

    public long getTotalSpace() {
        long totalSpace = 0;
        for (File file : roots) {
            totalSpace += file.getTotalSpace();
        }

        return totalSpace;
    }

    public long getTotalSpaceWithKB() {
        long totalSpace = 0;
        for (File file : roots) {
            totalSpace += file.getTotalSpace();
        }

        return totalSpace / KB;
    }

    public long getTotalSpaceWithMB() {
        long totalSpace = 0;
        for (File file : roots) {
            totalSpace += file.getTotalSpace();
        }

        return totalSpace / MB;
    }

    public long getTotalSpaceWithGB() {
        long totalSpace = 0;
        for (File file : roots) {
            totalSpace += file.getTotalSpace();
        }

        return totalSpace / GB;
    }

    public long getTotalSpaceWithTB() {
        long totalSpace = 0;
        for (File file : roots) {
            totalSpace += file.getTotalSpace();
        }

        return totalSpace / TB;
    }

    public long getTotalUsedSpace() {
        long totalUsedSpace = 0;
        for (File file : roots) {
            totalUsedSpace += (file.getTotalSpace() - file.getFreeSpace());
        }

        return totalUsedSpace;
    }

    public long getTotalUsedSpaceWithKB() {
        long totalUsedSpace = 0;
        for (File file : roots) {
            totalUsedSpace += (file.getTotalSpace() - file.getFreeSpace());
        }

        return totalUsedSpace / KB;
    }

    public long getTotalUsedSpaceWithMB() {
        long totalUsedSpace = 0;
        for (File file : roots) {
            totalUsedSpace += (file.getTotalSpace() - file.getFreeSpace());
        }

        return totalUsedSpace / MB;
    }

    public long getTotalUsedSpaceWithGB() {
        long totalUsedSpace = 0;
        for (File file : roots) {
            totalUsedSpace += (file.getTotalSpace() - file.getFreeSpace());
        }

        return totalUsedSpace / GB;
    }

    public long getTotalUsedSpaceWithTB() {
        long totalUsedSpace = 0;
        for (File file : roots) {
            totalUsedSpace += (file.getTotalSpace() - file.getFreeSpace());
        }

        return totalUsedSpace / TB;
    }

    public long getTotalFreeSpace() {
        long totalFreeSpace = 0;
        for (File file : roots) {
            totalFreeSpace += file.getFreeSpace();
        }

        return totalFreeSpace;
    }

    public long getTotalFreeSpaceWithKB() {
        long totalFreeSpace = 0;
        for (File file : roots) {
            totalFreeSpace += file.getFreeSpace();
        }

        return totalFreeSpace / KB;
    }

    public long getTotalFreeSpaceWithMB() {
        long totalFreeSpace = 0;
        for (File file : roots) {
            totalFreeSpace += file.getFreeSpace();
        }

        return totalFreeSpace / MB;
    }

    public long getTotalFreeSpaceWithGB() {
        long totalFreeSpace = 0;
        for (File file : roots) {
            totalFreeSpace += file.getFreeSpace();
        }

        return totalFreeSpace / GB;
    }

    public long getTotalFreeSpaceWithTB() {
        long totalFreeSpace = 0;
        for (File file : roots) {
            totalFreeSpace += file.getFreeSpace();
        }

        return totalFreeSpace / TB;
    }

    public double getDiskUsage() {
        return Double.parseDouble(String.format("%.2f", 100 * (double) getTotalUsedSpace() / getTotalSpace()));
    }


    public double getDiskFreeUsage() {
        return Double.parseDouble(String.format("%.2f", 100 * (double) getTotalFreeSpace() / getTotalSpace()));
    }

}
