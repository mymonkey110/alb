package net.alb.util;

import net.alb.ALBException.ALBException;
import net.alb.sysmonitor.SystemStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mymon_000 on 13-12-27.
 */
public class CsvTool {
    //private PrintWriter printWriter;
    private FileWriter fileWriter = null;
    public static String filePath = null;
    public static boolean append = true;

    private static CsvTool instance = null;

    private CsvTool() throws IOException {
        File file = new File(filePath);
        fileWriter = new FileWriter(file, append);
        if (file.length() == 0) {
            fileWriter.write("TIME,CPU CORES,CPU USAGE,MEMORY TOTAL,MEMORY USED,MEMORY FREE,MEMORY USAGE\r\n");
            fileWriter.flush();
        }
    }

    public void writeSysInfo(SystemStatus systemStatus) throws ALBException {
        Date date=new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder(simpleDateFormat.format(date));
        sb.append(","+systemStatus.getCpuCores()+","+systemStatus.getCpuUsage());
        sb.append(","+systemStatus.getMemoryTotal()+","+systemStatus.getMemoryUsed());
        sb.append(","+systemStatus.getMemoryFree()+","+systemStatus.getMemoryUsage()+"\r\n");

        try {
            fileWriter.write(sb.toString());
            fileWriter.flush();
        } catch (IOException e) {
            throw new ALBException(e.getMessage());
        }
    }

    public static void setCsvTool(String filePath, boolean append) throws ALBException {
        CsvTool.filePath = filePath;
        CsvTool.append = append;
    }

    //FIXME Not Thread Safety
    public static CsvTool getCsvTool() throws ALBException {
        if (instance == null) {
            if (filePath.isEmpty())
                throw new ALBException("Csv file path not set!");
            try {
                instance = new CsvTool();
            } catch (IOException e) {
                throw new ALBException(e.getMessage());
            }
        }
        return instance;
    }
}
