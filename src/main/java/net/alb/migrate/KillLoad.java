package net.alb.migrate;

import net.alb.sysmonitor.ProcInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by mymon_000 on 14-1-20.
 */
public class KillLoad extends MigrateOut {
    private static Logger logger = LoggerFactory.getLogger(KillLoad.class);
    private List<ProcInfo> procInfoList;

    public KillLoad(String threadName,List<ProcInfo> procInfoList) {
        super(threadName);
        this.procInfoList=procInfoList;
    }
    //FIXME ： PID NOT VISIBLE
    @Override
    public void out() {
        if (procInfoList.size() > 0) {
            String killCommand = String.format("kill -9 %d", procInfoList.get(0).getPid());
            logger.info("Kill PID:", procInfoList.get(0).getPid());
            try {
                Process process = Runtime.getRuntime().exec(killCommand);
                process.waitFor();
            }catch (IOException e) {
                logger.warn("Kill pid:{} error,{}.",procInfoList.get(0).getPid(),e.getMessage());
            }catch (InterruptedException e) {
                logger.warn("Process wait error,{}.",e.getMessage());
            }
        }
        logger.debug("No process found!");
    }
}
