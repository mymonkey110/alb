package net.alb.sysmonitor;

import net.alb.ALBException.ALBException;
import net.alb.config.Configuration;
import net.alb.util.CsvTool;
import org.hyperic.sigar.SigarException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by mymon_000 on 13-12-30.
 */
public class Collector extends Thread {
    private SystemStatus systemStatus;
    private String procName;
    private List<ProcInfo> procInfoList = new ArrayList<ProcInfo>();
    private ReadWriteLock procInfoListRWLocker = new ReentrantReadWriteLock();
    private CpuMonitor cpuMonitor;
    private MemoryMonitor memoryMonitor;
    private CsvTool csvTool;
    private Configuration configuration;
    private ReadWriteLock rwLocker = new ReentrantReadWriteLock();

    private static Logger logger = LoggerFactory.getLogger(Collector.class);

    public Collector(String threadName) {
        super(threadName);
        systemStatus = new SystemStatus();
    }

    //TODO
    @Override
    public void run() {
        logger.debug("Collector is initializing...");
        try {
            init();
        } catch (ALBException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }
        logger.debug("Collector initialization complete.");
        int timespan = configuration.getMonitorTimespan();
        while (true) {
            saveSysInfo();
            recordSysInfo();
            recordProcInfo();

            try {
                Thread.sleep(timespan * 1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());       //Thread interrupted,probably lead to an accident!
                System.exit(-1);
            }
        }
    }

    private void saveSysInfo() {
        rwLocker.writeLock().lock();

        try {
            cpuMonitor = new CpuMonitor();
            memoryMonitor = new MemoryMonitor();
        } catch (SigarException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }

        systemStatus.setCpuCores(cpuMonitor.getTotalCores());
        systemStatus.setCpuUsage(cpuMonitor.getCpuUsage());
        systemStatus.setMemoryTotal(memoryMonitor.getTotalMemoryWithMB());
        systemStatus.setMemoryUsed(memoryMonitor.getUsedMemoryWithMB());
        systemStatus.setMemoryFree(memoryMonitor.getFreeMemoryWithMB());
        systemStatus.setMemoryUsage(memoryMonitor.getMemoryUsage());

        rwLocker.writeLock().unlock();
    }

    private void recordProcInfo() {
        if (procName == null || procName.isEmpty())
            return;
        List<String> procInfoTempList = new ArrayList<String>();

        try {
            //ps aux | grep -E '\scacvm\s' | awk '{$1=$7=$8=$9=$10=",";;$5/=1024;$6/=1024;;print}' | cut -d " " -f 2,3,4,5,6,11-
            StringBuilder command = new StringBuilder("ps aux | grep -E '\\s+" + procName + "\\s+'");
            command.append(" | awk '{$1=$7=$8=$9=$10=\",\";$5=int($5/1024);$6=int($6/1024);print}' | cut -d \" \" -f 2,3,4,5,6,11-");
            String[] commands=new String[3];
            commands[0]="sh";
            commands[1]="-c";
            commands[2]=command.toString();
            Process process = Runtime.getRuntime().exec(commands);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                procInfoTempList.add(line);
            }
        } catch (IOException e) {
            logger.warn("Get {} information error,{}.", procName, e.getMessage());
            return;
        }
        //Update currentProcInfoList,thread safety
        procInfoListRWLocker.writeLock().lock();
        procInfoList.clear();        //Clear First,make sure it's empty when updating it!
        for (String info : procInfoTempList) {
            String[] index = info.split(" ",6);
            ProcInfo procInfo = new ProcInfo();
            procInfo.setPid(Integer.parseInt(index[0]));
            procInfo.setCpuUsage(Double.parseDouble(index[1]));
            procInfo.setMemoryUsage(Double.parseDouble(index[2]));
            procInfo.setVirtualMemory(Long.parseLong(index[3]));
            procInfo.setPhysicsMemory(Long.parseLong(index[4]));
            procInfo.setCmd(index[5]);
            procInfoList.add(procInfo);
        }
        procInfoListRWLocker.writeLock().unlock();
        //TODO : For Debug
        for (ProcInfo info : procInfoList) {
            logger.debug("PID:{},CPU USAGE:{},MEMORY USAGE:{},Virtual Memory:{},Physics Memory:{}.", info.getPid(),
                    info.getCpuUsage(), info.getMemoryUsage(), info.getVirtualMemory(), info.getPhysicsMemory());
        }
    }

    //Provide a copy of current process information , thread safety
    public List<ProcInfo> getCopyOfProcInfoList() {
        List<ProcInfo> procInfoListCopy = new ArrayList<ProcInfo>();

        procInfoListRWLocker.readLock().lock();
        procInfoListCopy.addAll(procInfoList);
        procInfoListRWLocker.readLock().unlock();

        return procInfoListCopy;
    }

    private void recordSysInfo() {
        try {
            csvTool.writeSysInfo(systemStatus);
        } catch (ALBException e) {
            logger.warn(e.getMessage());        //Warning:Record not working,but we should go on.
        }
    }

    public SystemStatus getSysInfo() {
        rwLocker.readLock().lock();
        SystemStatus currentStatus = new SystemStatus(systemStatus);
        rwLocker.readLock().unlock();
        return currentStatus;
    }

    private void init() throws ALBException {
        try {
            csvTool = CsvTool.getCsvTool();
            configuration = Configuration.getConfiguration();
            procName = configuration.getProcName();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ALBException("initialize tools error!");
        }
    }
}
