package net.alb;

import net.alb.ALBException.ALBException;
import net.alb.analysis.Checker;
import net.alb.comm.CommManager;
import net.alb.config.Configuration;
import net.alb.config.Loader;
import net.alb.migrate.*;
import net.alb.sysmonitor.Collector;
import net.alb.sysmonitor.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by mymon_000 on 13-12-30.
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("Starting...");
        Loader loader = new Loader();
        loader.init();
        Map<String,SystemStatus> neighborStatusMap;

        Collector collector = new Collector("Collector");
        collector.start();
        CommManager commManager = new CommManager("Communicator");
        commManager.start();
        logger.info("ALB initialization complete.");

        Checker checker=null;
        MigrateOut migrateOut=null;
        MigrateIn migrateIn=new VMMigrateIn("VMMigrateIn");
        migrateIn.start();

        int timespan=0;
        try{
            Configuration configuration=Configuration.getConfiguration();
            timespan=configuration.getMonitorTimespan();
            checker=Checker.getChecker();
        }catch (ALBException e) {
            logger.error(e.getMessage());
            System.exit(-1);
        }


        //TODO Add Migration,Selection Algorithm
        while(true) {
            SystemStatus currentStatus=collector.getSysInfo();
            commManager.setCurrentStatus(currentStatus);
            checker.setCurrentStatus(currentStatus);
            logger.info("Current Status:{}.",currentStatus.summarize());
            try{
                Thread.sleep(timespan*1000);
            }catch (InterruptedException e) {
                logger.error(e.getMessage());
                System.exit(-1);
            }
            neighborStatusMap=commManager.getNeighborStatusMap();
            for(Map.Entry<String,SystemStatus> entry:neighborStatusMap.entrySet()) {
                logger.debug("Server:{} Status:{}",entry.getKey(),entry.getValue().summarize());
            }

            if(checker.outThreshold()) {
                logger.debug("OutThreshold!");
                if(migrateOut==null || !migrateOut.isAlive()) {
                    migrateOut=new VMMigrateOut("VMMigrateOut",collector.getCopyOfProcInfoList(),commManager.getNeighborStatusMap());
                    migrateOut.start();
                } else {
                    logger.debug("Migration is on going!");
                }
            }
        }
    }
}
