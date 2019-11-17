package yitian.heron.monitor.utils;

import java.io.*;
import java.text.SimpleDateFormat;

public class FileUtils {

    /**
     * Write content to file
     *
     * @param filename filename path
     * @param content  writing content
     * @return ture or false
     */
    public static boolean writeToFile(String filename, String content) {
        BufferedWriter out = null;
        try {
            File file = new File(filename);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdir();
                file.createNewFile();
            }
            System.out.println("Starting write file : " + filename + " : " + content);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write("[" + getCurrentTime() + "] " + content + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * From storm beachmark
     * @param parent file path
     * @param name filename
     * @return PrintWriter
     */
    public static PrintWriter createFileWriter(String parent, String name) {
        try {
            final File dir = new File(parent);
            if (dir.exists() || dir.mkdirs()) {
                final File file = new File(name);
                file.createNewFile();
                final PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
                return writer;
            } else {
                throw new RuntimeException("fail to create parent directory " + parent);
            }
        } catch (IOException e) {
            throw new RuntimeException("No such file or directory " + name, e);
        }
    }

    /**
     * get current time for write content to file
     * @return time string
     */
    private static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = df.format(System.currentTimeMillis());
        return currentTime;
    }

    public static void main(String[] args) {
        String filename = "/home/yitian/logs/latency/aurora/latency-monitor.txt";
//        String filename = "C:\\Users\\Administrator\\Desktop\\heron latency\\text.txt";
        FileUtils.writeToFile(filename, "content");
        FileUtils.writeToFile(filename, "add this content");

    }
}
