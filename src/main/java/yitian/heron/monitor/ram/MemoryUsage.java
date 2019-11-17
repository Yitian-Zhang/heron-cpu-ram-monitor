package yitian.heron.monitor.ram;

import org.apache.log4j.Logger;
import sun.rmi.log.LogInputStream;
import yitian.heron.monitor.utils.FileUtils;

import java.io.*;
import java.util.Properties;

public class MemoryUsage {
    private static Logger LOG = Logger.getLogger(MemoryUsage.class);
    private static MemoryUsage instance = new MemoryUsage();
    private static final String MEMORY_USAGE_DATA_FILE = "/home/yitian/monitor/memory-usage/memory-usage-data.txt";
    private static String nodeName;

    private MemoryUsage() {}

    public static MemoryUsage getInstance() {
        return instance;
    }

    public void getWorkerNodeName() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("/home/yitian/monitor/monitor-conf.ini"));
        nodeName = properties.getProperty("node-name");
    }

    public float getMemoryUsage() throws IOException {
        LOG.info("**********************STARTING MONITOR MEMORY USAGE**********************");
        // getWorkerNodeName
        getWorkerNodeName();

        float memoryUsage = 0.0f;
        Process process = null;
        Runtime runtime = Runtime.getRuntime();
        BufferedReader inReader = null;

                String command = "cat /proc/meminfo";
        try {
            process = runtime.exec(command);
            inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int count = 0;
            long totalMemory = 0;
            long freeMemory = 0;
            while ((line = inReader.readLine()) != null) {
                LOG.info("Line is: " + line);
                String[] memoryInfo = line.split("\\s+");
                if (memoryInfo[0].startsWith("MemTotal")) {
                    totalMemory = Long.parseLong(memoryInfo[1]);
                }
                if (memoryInfo[0].startsWith("MemFree")) {
                    freeMemory = Long.parseLong(memoryInfo[1]);
                }
                memoryUsage = 1 - (float) freeMemory / (float) totalMemory;
                LOG.info("Worker Node: [" + nodeName + "] memeory usage is: " + memoryUsage);
                if (++count == 2) { // only readed two lines
                    break;
                }
            }
//            inReader.close();
//            process.destroy();
        } catch (IOException e) {
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            LOG.error("Memroy Usage has Exception: " + e.getMessage());
            LOG.error(stringWriter.toString());
        } finally {
            try {
                inReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            process.destroy();
        }
        LOG.info("**********************MONITOR MEMORY USAGE END**********************");
        return memoryUsage;
    }


    public static void main(String[] args) throws InterruptedException, IOException {

        while (true) {
            System.out.println(MemoryUsage.getInstance().getMemoryUsage());
            FileUtils.writeToFile(MEMORY_USAGE_DATA_FILE,  nodeName + " : " + MemoryUsage.getInstance().getMemoryUsage());
            Thread.sleep(5000);
        }
    }

}
