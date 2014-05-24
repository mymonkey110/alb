package net.alb.core;

/**
 * Created by mymon_000 on 14-1-14.
 */

import net.alb.sysmonitor.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Name:LL Selection Algorithm
 * Select a least load host to migrate out load according to last neighbor status
 */
public class LeastLoad implements Decider {
    private static Logger logger = LoggerFactory.getLogger(LeastLoad.class);

    private Map<String, SystemStatus> neighborStatusMap = null;

    public LeastLoad(Map<String, SystemStatus> neighborStatusMap) {
        this.neighborStatusMap = new HashMap<String, SystemStatus>(neighborStatusMap);
    }

    //TODO: Implement Least Load Algorithm
    public List<String> decide() {
        List<String> recommendIps = new ArrayList<String>();
        List<Map.Entry<String, SystemStatus>> mappingList = new ArrayList<Map.Entry<String, SystemStatus>>(neighborStatusMap.entrySet());

        Collections.sort(mappingList, new Comparator<Map.Entry<String, SystemStatus>>() {
            @Override
            public int compare(Map.Entry<String, SystemStatus> first, Map.Entry<String, SystemStatus> second) {
                SystemStatus firstSystemStatus = first.getValue();
                SystemStatus secondSystemStatus = second.getValue();

                long firstFP = (long) (firstSystemStatus.getCpuCores() * (1 - firstSystemStatus.getCpuUsage() / 100) * firstSystemStatus.getMemoryFree());
                long secondFP = (long) (secondSystemStatus.getCpuCores() * (1 - secondSystemStatus.getCpuUsage() / 100) * secondSystemStatus.getMemoryFree());

                return firstFP < secondFP ? 1 : -1;
            }
        });

        for(Map.Entry<String,SystemStatus> entry : mappingList) {
            recommendIps.add(entry.getKey());
        }

        return recommendIps;
    }
}
