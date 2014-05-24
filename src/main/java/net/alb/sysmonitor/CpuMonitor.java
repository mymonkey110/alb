/**
 * Created by mymon_000 on 13-12-21.
 */
package net.alb.sysmonitor;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class CpuMonitor {
    private CpuInfo cpuInfo = null;
    private CpuPerc cpuPerc = null;

    public CpuMonitor() throws SigarException {
        Sigar sigar = new Sigar();
        cpuInfo = sigar.getCpuInfoList()[0];
        cpuPerc = sigar.getCpuPerc();
    }

    /**
     * ��ȡCPU�ܺ���,�߼��������
     */
    public int getTotalCores() {
        return cpuInfo.getTotalCores();
    }

    /**
     * ��ȡCPU������
     */
    public String getVendor() {
        return cpuInfo.getVendor();
    }

    /**
     * ��ȡCPU��ϵ��
     */
    public String getModel() {
        return cpuInfo.getModel();
    }

    /**
     * ��ȡCPU��ʹ���ʣ��ٷֱȣ�
     */
    public double getCpuUsage() {
        return Double.parseDouble(String.format("%.2f", 100 * cpuPerc.getCombined()));
    }

    /**
     * ��ȡCPU�Ŀ����ʣ��ٷֱȣ�
     */
    public double getCpuIdelUsage() {
        return Double.parseDouble(String.format("%.2f", 100 * cpuPerc.getIdle()));
    }

    /**
     * CPU��Ϣͳ��
     */
    public String summarize() {
        StringBuilder sb = new StringBuilder();
        sb.append("CPU�����̣�" + getVendor() + "\n");
        sb.append("CPU�ͺţ�" + getModel() + "\n");
        sb.append("CPU�����߼�����" + getTotalCores() + "\n");
        sb.append("��ǰCPUʹ���ʣ�" + getCpuUsage() + "%\n");
        return sb.toString();
    }
}
