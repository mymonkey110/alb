package net.alb.migrate;

import net.alb.analysis.Checker;
import net.alb.sysmonitor.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mymon_000 on 14-2-12.
 */
public class VMMigrateIn extends MigrateIn {
    private static Logger logger = LoggerFactory.getLogger(VMMigrateIn.class);

    public VMMigrateIn(String threadName) {
        super(threadName);
    }

    @Override
    public void in() {
        ServerSocket serverSocket = null;
        Checker checker = null;
        try {
            logger.info("VMMigrateIn is initializing...");
            serverSocket = new ServerSocket(8000);
            serverSocket.setReuseAddress(true);
            checker = Checker.getChecker();
            logger.info("VMMigrateIn initialize complete.");
        } catch (Exception e) {
            logger.error("Starting VMMigrateIn error,{}.", e.getMessage());
            System.exit(-1);
        }

        while (true) {
            try {
                Socket conn = serverSocket.accept();
                logger.debug("Server:{} request migration.", conn.getRemoteSocketAddress());
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                PrintWriter pw = new PrintWriter(conn.getOutputStream(), true);
                String request = in.readLine();
                logger.debug("Remote request:{}", request);
                if (request == null) {
                    conn.close();
                    continue;
                }

                String[] tmp = request.split("\\|");
                double cpuUsage = Double.parseDouble(tmp[0]);
                long memoryUsed = Long.parseLong(tmp[1]);
                String cmdLine = tmp[2];
                logger.debug("ProcInfo:CpuUsage-{},MemoryUsed-{}MB", cpuUsage, memoryUsed);

                SystemStatus fakeStatus = checker.getCurrentStatus();
                if (fakeStatus == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                    fakeStatus = checker.getCurrentStatus();
                    if (fakeStatus == null) {
                        logger.debug("Get current status error");
                        System.exit(-1);
                    }
                }

                logger.debug("FakeStatus cpuUsage:{},memoryUsed:{}MB,memoryFree:{}MB,memoryUsage:{}.", fakeStatus.getCpuUsage(), fakeStatus.getMemoryUsed(),
                        fakeStatus.getMemoryFree(), fakeStatus.getMemoryUsage());
                double newCpuUsage=fakeStatus.getCpuUsage() + cpuUsage / fakeStatus.getCpuCores();
                fakeStatus.setCpuUsage(Double.parseDouble(String.format("%.2f",newCpuUsage)));
                fakeStatus.setMemoryUsed(fakeStatus.getMemoryUsed() + memoryUsed);
                fakeStatus.setMemoryFree(fakeStatus.getMemoryFree() - memoryUsed);
                double newMemoryUsage=fakeStatus.getMemoryUsed() / fakeStatus.getMemoryTotal();
                fakeStatus.setMemoryUsage(Double.parseDouble(String.format("%.2f",newMemoryUsage)));
                logger.debug("FakeStatus cpuUsage:{},memoryUsed:{}MB,memoryFree:{}MB,memoryUsage:{}.", fakeStatus.getCpuUsage(), fakeStatus.getMemoryUsed(),
                        fakeStatus.getMemoryFree(), fakeStatus.getMemoryUsage());
                if (checker.outThreshold(fakeStatus)) {
                    logger.debug("Estimate out threshold,deny migrate in.");
                    pw.println("deny");
                    conn.close();
                    continue;
                }
                String startKVM = String.format("setsid %s &", cmdLine);
                logger.debug("StartKVM:{}", startKVM);
                String[] startCmdLine = new String[3];
                startCmdLine[0] = "sh";
                startCmdLine[1] = "-c";
                startCmdLine[2] = startKVM;
                Runtime.getRuntime().exec(startCmdLine);
                pw.println("ok");
                conn.close();
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }
}
