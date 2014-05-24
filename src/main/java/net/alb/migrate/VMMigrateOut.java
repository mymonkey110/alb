package net.alb.migrate;

import net.alb.core.Decider;
import net.alb.core.LeastLoad;
import net.alb.sysmonitor.ProcInfo;
import net.alb.sysmonitor.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by mymon_000 on 14-2-12.
 */
public class VMMigrateOut extends MigrateOut {
    private List<ProcInfo> procInfoList = null;
    private Map<String, SystemStatus> neigborStatusMap = null;

    private static Logger logger = LoggerFactory.getLogger(VMMigrateOut.class);

    public VMMigrateOut(String threadName, List<ProcInfo> procInfoList, Map<String, SystemStatus> neighborStatusMap) {
        super(threadName);
        this.procInfoList = procInfoList;
        this.neigborStatusMap = neighborStatusMap;
    }

    //TODO Implement the whole process of KVM Migration
    @Override
    public void out() {
        if (procInfoList == null || procInfoList.isEmpty()) {
            logger.debug("No process found,cancel migrate.");
            return;
        }

        Decider decider = new LeastLoad(neigborStatusMap);
        boolean flag = false;
        List<String> recommendIps = decider.decide();
        logger.debug("RecommendIps:{}.", recommendIps);
        for (String dstIp : recommendIps) {
            logger.debug("Trying to migrate to {}.", dstIp);
            if (notifyDst(procInfoList.get(0), dstIp)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            try {
                logger.debug("Kill Source KVM:{}.", procInfoList.get(0).getPid());
                Runtime.getRuntime().exec("kill -9 " + procInfoList.get(0).getPid());
            } catch (IOException e) {
                logger.debug("Kill source KVM error:{}.", e.getMessage());
            }
        }
    }

    public boolean notifyDst(ProcInfo procInfo, String dstIp) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(dstIp, 8000), 3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            //String kvmCmd=procInfo.getCmd();
            //String imgName = kvmCmd.substring(kvmCmd.indexOf("id=") + 3, kvmCmd.indexOf(',', kvmCmd.indexOf("id=")));
            String request=String.format("%.2f|%d|%s",procInfo.getCpuUsage(),procInfo.getPhysicsMemory(),procInfo.getCmd());
            logger.debug("Request:{}",request);
            pw.println(request);
            String reply = in.readLine();
            socket.close();
            if (reply != null && reply.equals("ok"))
                return true;
        } catch (IOException e) {
            logger.info("Connect to {} error,{}.", dstIp, e.getMessage());
        }
        return false;
    }

    //FIXME Here exist bug
    @Deprecated
    public boolean liveMigrate(String kvmCmd, String dstIp) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(dstIp, 8000), 3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String imgName = kvmCmd.substring(kvmCmd.indexOf("id=") + 3, kvmCmd.indexOf(',', kvmCmd.indexOf("id=")));
            StringBuilder stringBuilder = new StringBuilder(imgName);
            stringBuilder.append("|");
            stringBuilder.append(kvmCmd);
            pw.println(stringBuilder.toString());

            String reply = in.readLine();
            logger.debug("Server:{} reply:{}.", dstIp, reply);
            if (reply.equals("deny")) {
                logger.info("{} deny migrate in.", dstIp);
                return false;
            } else {
                int migratePort = Integer.parseInt(reply);
                String[] migrateCmd = new String[3];
                migrateCmd[0] = "sh";
                migrateCmd[1] = "-c";
                migrateCmd[2] = String.format("echo \"migrate -b tcp:%s:%d\"|nc -U /var/cloudos/conf/net/%s", dstIp, migratePort, imgName);
                logger.debug("MigrateOut:{}.", migrateCmd[2]);
                Process process = Runtime.getRuntime().exec(migrateCmd);
                process.waitFor();
            }
            socket.close();
        } catch (Exception e) {
            logger.info("Connect to {} failed,error:{}.", dstIp, e.getMessage());
            return false;
        }
        return true;
    }
}
