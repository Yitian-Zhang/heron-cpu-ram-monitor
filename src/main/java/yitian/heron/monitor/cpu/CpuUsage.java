package yitian.heron.monitor.cpu;

import org.apache.log4j.Logger;
import yitian.heron.monitor.utils.FileUtils;

import java.io.*;
import java.util.Properties;

public class CpuUsage {
    private static final String CPU_USAGE_DATA_FILE = "/home/yitian/monitor/cpu-usage/cpu-usage-data.txt";
    private static Logger LOG = Logger.getLogger(CpuUsage.class);
    private static CpuUsage instance = new CpuUsage();
    private static String nodeName;

    private CpuUsage() {
    }

    public static CpuUsage getInstance() {
        return instance;
    }

    public void getWorkerNodeName() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("/home/yitian/monitor/monitor-conf.ini"));
        nodeName = properties.getProperty("node-name");
    }

    public float getCpuUsage() throws IOException {
        LOG.info("*********************STARTING MONITOR CPU USAGE*********************");
        // getWorkerNodeName
        getWorkerNodeName();

        float cpuUsage = 0.0f;
        Process processFirst = null;
        Process processSecond = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            String command = "cat /proc/stat";
            long startTime = System.currentTimeMillis();

            // first get cpu time
            processFirst = runtime.exec(command);
            BufferedReader inReader = new BufferedReader(new InputStreamReader(processFirst.getInputStream()));
            String line = null;
            long idleCpuTimeFirst = 0;
            long totalCpuTimeFirst = 0;
            while ((line = inReader.readLine()) != null) {
                if (line.startsWith("cpu")) {
                    line = line.trim();
                    LOG.info("[1] Line is: " + line);

                    String[] temp = line.split("\\s+");
                    idleCpuTimeFirst = Long.parseLong(temp[4]);
                    for (String item : temp) {
                        if (!item.equals("cpu")) {
                            totalCpuTimeFirst += Long.parseLong(item);
                        }
                    }
                    LOG.info("[1] IdleCpuTime: " + idleCpuTimeFirst + ", TotalCpuTime: " + totalCpuTimeFirst);
                    break; // only readed one line in /proc/stat file
                }
            }
            inReader.close();
            processFirst.destroy();

            // sleep a internal time to calculate the cpu usage
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                LOG.error("When sleep, cpu usage has Exception: " + e.getMessage());
                LOG.error(stringWriter.toString());
            }

            // second get cpu time
            long endTime = System.currentTimeMillis();
            processSecond = runtime.exec(command);
            BufferedReader inReader2 = new BufferedReader(new InputStreamReader(processSecond.getInputStream()));
            long idleCputTimeSecond = 0;
            long totalCpuTimeSecond = 0;
            while ((line = inReader2.readLine()) != null) {
                if (line.startsWith("cpu")) {
                    line = line.trim();
                    LOG.info("[2] Line: " + line);
                    String[] temp = line.split("\\s+");
                    idleCputTimeSecond = Long.parseLong(temp[4]);
                    for (String item : temp) {
                        if (!item.equals("cpu")) {
                            totalCpuTimeSecond += Long.parseLong(item);
                        }
                    }
                    LOG.info("[2] IdleCpuTime: " + idleCputTimeSecond + ", totalCpuTime: " + totalCpuTimeSecond);
                    break;
                }
            }

            if (idleCpuTimeFirst != 0 && totalCpuTimeFirst != 0 && idleCputTimeSecond != 0 && totalCpuTimeSecond != 0) {
                cpuUsage = 1 - (float)(idleCputTimeSecond - idleCpuTimeFirst) / (float) (totalCpuTimeSecond - totalCpuTimeFirst);
                LOG.info("Worker Node: [" + nodeName + "] cpu usage in 100 ms is: " + cpuUsage);
            }

            inReader2.close();
            processSecond.destroy();
        } catch (IOException e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            LOG.error("When running cpu usaeg, the Exception happened:" + e.getMessage());
            LOG.error(stringWriter.toString());
        }

        LOG.info("*********************MONITOR CPU USAGE END*********************");
        return cpuUsage;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        while(true){
            System.out.println(CpuUsage.getInstance().getCpuUsage());
            FileUtils.writeToFile(CPU_USAGE_DATA_FILE, nodeName + " : " + CpuUsage.getInstance().getCpuUsage());
            Thread.sleep(5000);
        }
    }

}
