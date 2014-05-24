package net.alb.config;

import net.alb.ALBException.ALBException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mymon_000 on 13-12-25.
 */
public class Configuration {
    private List<String> ips = new ArrayList<String>();
    //private List<String> processNameList = new ArrayList<String>();
    private String csvPath = null;
    private boolean csvAppend = true;
    private int monitorTimespan = 0;
    private int commTimespan = 0;
    private double maxCpuUsage;
    private double maxMemoryUsage;
    private long maxMemoryUsed;
    private long minMemoryFree;
    private String procName=null;
    private String logConfigPath = null;
    private Document document = null;
    private static Configuration instance = null;
    private static Logger logger = LoggerFactory.getLogger(Configuration.class);

    private Configuration() throws DocumentException {
        logger.info("Loading Configuration...");
        SAXReader saxReader = new SAXReader();
        document = saxReader.read(new File("alb.xml"));
    }

    private Configuration(String confPath) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        document = saxReader.read(new File(confPath));
    }

    //FIXME : Not Thread Safety
    public static Configuration getConfiguration(String configPath) throws ALBException {
        if (instance == null) {
            try {
                instance = new Configuration(configPath);
            } catch (DocumentException e) {
                throw new ALBException("Load Configuration File Error!");
            }
        }
        return instance;
    }
    //FIXME : Not Thread Safety
    public static Configuration getConfiguration() throws ALBException {
        if (instance == null) {
            try {
                instance = new Configuration();
            } catch (DocumentException e) {
                throw new ALBException("Load Configuration File Error!");
            }
        }
        return instance;
    }

    //FIXME There is some logical error while handling the configuration file
    public void init() {

        List servers = document.selectNodes("/application/clusters/cluster[@name='local']/server");
        if (servers != null) {
            for (Object it : servers.toArray()) {
                ips.add(((Element) it).getTextTrim());
            }
        } else {
            logger.warn("No server found!");
        }
        logger.info("Neighbor server:{}.",ips);

        Node commNode = document.selectSingleNode("/application/communication");
        if (commNode != null) {
            commTimespan = Integer.parseInt(commNode.valueOf("timespan").trim());
            if (commTimespan <= 0) {
                logger.warn("timespan of communication <= 0,use default value:60 seconds.");
                commTimespan = 60;
            }
        } else {
            logger.warn("Communication node not found");
        }
        logger.info("Communication timespan:{}.",commTimespan);

        Node procNode = document.selectSingleNode("/application/process/name");
        if (procNode != null) {
            procName = procNode.getText().trim();
        } else {
            logger.info("No monitor process name found.");
        }
        logger.info("Process name:{}.",procName);

        Node thresholdNode = document.selectSingleNode("/application/threshold");
        if (thresholdNode != null) {
            maxCpuUsage = Double.parseDouble(thresholdNode.valueOf("maxCpuUsage"));
            if (maxCpuUsage <= 0 || maxCpuUsage > 100) {
                logger.warn("maxCpuUsage error,use default:0.8.");
                maxCpuUsage = 0.8;
            }
            logger.info("MaxCpuUsage:{}.",maxCpuUsage);

            maxMemoryUsage = Double.parseDouble(thresholdNode.valueOf("maxMemoryUsage"));
            if (maxMemoryUsage <= 0 || maxMemoryUsage > 100) {
                logger.warn("maxMemoryUsage error,use default:0.8");
                maxMemoryUsage = 0.8;
            }
            logger.info("MaxMemoryUsage:{}.",maxMemoryUsage);

            maxMemoryUsed = Long.parseLong(thresholdNode.valueOf("maxMemoryUsed"));
            if (maxMemoryUsed <= 0) {
                maxMemoryUsed = -1;
            }
            logger.info("MaxMemoryUsed:{}.",maxMemoryUsed);

            minMemoryFree = Long.parseLong(thresholdNode.valueOf("minMemoryFree"));
            if (minMemoryFree <= 0) {
                minMemoryFree = -1;
            }
            logger.info("MinMemoryFree:{}.",minMemoryFree);
        }

        Node logNode = document.selectSingleNode("/application/log");
        if (logNode != null) {
            logConfigPath = logNode.valueOf("configuration").trim();
            if (logConfigPath.isEmpty()) {
                logger.warn("Log configuration file error,use default:./");
                logConfigPath = "./";
            }
        } else {
            logger.warn("Log node not found.");
        }
        logger.info("Log configuration file path:{}.",logConfigPath);

        Node csvNode = document.selectSingleNode("/application/monitor");
        if (csvNode != null) {
            csvPath = csvNode.valueOf("csvpath").trim();
            if (csvPath.isEmpty()) {
                csvPath = "monitor.csv";
                logger.warn("CSV file path not configured,use default:monitor.csv.");
            }
            logger.info("Csv path:{}.",csvPath);

            csvAppend = Boolean.parseBoolean(csvNode.valueOf("append").trim());
            if (!csvAppend)
                logger.warn("csv file append is false,overwrite old file if it exist.");
            logger.info("Csv append:{}.",csvAppend);

            monitorTimespan = Integer.parseInt(csvNode.valueOf("timespan").trim());
            if (monitorTimespan <= 0) {
                logger.warn("timespan of collect system information <= 0, use timespan:60 seconds.");
                monitorTimespan = 60;
            }
            logger.info("Monitor Timespan:{}.",monitorTimespan);
        } else {
            logger.warn("Monitor node not found!");
        }

    }

    public List<String> getIps() {
        return ips;
    }

    public boolean isCsvAppend() {
        return csvAppend;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public int getMonitorTimespan() {
        return monitorTimespan;
    }

    public int getCommTimespan() {
        return commTimespan;
    }

    public String getLogConfigPath() {
        return logConfigPath;
    }

    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public double getMaxMemoryUsage() {
        return maxMemoryUsage;
    }

    public long getMaxMemoryUsed() {
        return maxMemoryUsed;
    }

    public long getMinMemoryFree() {
        return minMemoryFree;
    }

    public String getProcName() {
        return procName;
    }

    @Deprecated
    public String summarize() {
        return String.format("Found Servers:%s,commTimespan=%d,csvpath=%s,csvappend=%s,monitorTimespan=%d.",
                ips, commTimespan, csvPath, csvAppend, monitorTimespan);
    }
}
