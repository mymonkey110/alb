package net.alb.analysis;

import net.alb.ALBException.ALBException;
import net.alb.config.Configuration;
import net.alb.sysmonitor.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mymon_000 on 14-1-14.
 */
public class Checker {
    private double maxCpuUsage;
    private double maxMemoryUsage;
    private long maxMemoryUsed;
    private long minMemoryFree;
    private SystemStatus currentStatus;

    private static Logger logger= LoggerFactory.getLogger(Checker.class);
    private static Checker instance=null;

    private Checker() throws ALBException {
        logger.info("Checker is initializing...");
        Configuration configuration=Configuration.getConfiguration();
        maxCpuUsage=configuration.getMaxCpuUsage();
        maxMemoryUsage=configuration.getMaxMemoryUsage();
        maxMemoryUsed=configuration.getMaxMemoryUsed();
        minMemoryFree=configuration.getMinMemoryFree();
        logger.info("Checker initialize complete.");
    }

    //Thread Safety
    public static Checker getChecker() throws ALBException{
        byte[] lock=new byte[0];
        synchronized (lock) {
            if(instance==null)
                instance=new Checker();
        }
        return instance;
    }

    public void setCurrentStatus(SystemStatus status) {
        this.currentStatus=status;
    }

    public SystemStatus getCurrentStatus() {
        return new SystemStatus(currentStatus);
    }
    //Analysis current self status is out of threshold
    public boolean outThreshold() {
        if(currentStatus.getCpuUsage()>maxCpuUsage) {
            logger.debug("CpuUsage OutThreshold,current CpuUsage:{},maxCpuUsage:{}.",currentStatus.getCpuUsage(),maxCpuUsage);
            return true;
        }

        if(currentStatus.getMemoryUsage()>maxMemoryUsage*100) {
            logger.debug("MemoryUsage OutThreshold,current MemoryUsage:{},maxMemoryUsage:{}.",currentStatus.getMemoryUsage(),maxMemoryUsage);
            return true;
        }

        if(currentStatus.getMemoryUsed()>maxMemoryUsed) {
            logger.debug("MemoryUsed OutThreshold,current MemoryUsed:{} MB,maxMemoryUsed:{} MB.",currentStatus.getMemoryUsed(),maxMemoryUsed);
            return true;
        }

        if(currentStatus.getMemoryFree()<minMemoryFree) {
            logger.debug("MemoryFree OutThreshold,current MemoryFree:{} MB,minMemoryFree:{} MB.",currentStatus.getMemoryFree(),minMemoryFree);
            return true;
        }

        return false;
    }
    //Analysis the provided status is out of threshold
    public boolean outThreshold(SystemStatus status) {
        if(status.getCpuUsage()>maxCpuUsage) {
            logger.debug("CpuUsage OutThreshold,provided CpuUsage:{},maxCpuUsage:{}.",status.getCpuUsage(),maxCpuUsage);
            return true;
        }

        if(status.getMemoryUsage()>maxMemoryUsage*100) {
            logger.debug("MemoryUsage OutThreshold,provided MemoryUsage:{},maxMemoryUsage:{}.",status.getMemoryUsage(),maxMemoryUsage);
            return true;
        }

        if(status.getMemoryUsed()>maxMemoryUsed) {
            logger.debug("MemoryUsed OutThreshold,provided MemoryUsed:{} MB,maxMemoryUsed:{} MB.",status.getMemoryUsed(),maxMemoryUsed);
            return true;
        }

        if(status.getMemoryFree()<minMemoryFree) {
            logger.debug("MemoryFree OutThreshold,provided MemoryFree:{} MB,minMemoryFree:{} MB.",status.getMemoryFree(),minMemoryFree);
            return true;
        }

        return false;
    }
}
