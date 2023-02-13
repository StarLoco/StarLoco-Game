package org.starloco.locos.kernel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Logging {
    private static final Logging singleton = new Logging();
    public static boolean USE_LOG = true;
    private ArrayList<Log> logs = new ArrayList<>();

    public static Logging getInstance() {
        return singleton;
    }

    public void initialize() {
        if (!new File("Logs").exists()) new File("Logs/").mkdir();
    }

    public void stop() {
        logs.stream().filter(log -> log.getBuffer() != null).forEach(log -> {
            try {
                log.getBuffer().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        this.logs.clear();
    }

    public void write(String name, String arg0) {
        if(!USE_LOG) return;

        for (Log log : logs) {
            if (log.getName().equals(name)) {
                try {
                    log.write(arg0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        final String date = Calendar.getInstance().get(Calendar.YEAR) + "-"
                + Calendar.getInstance().get(Calendar.MONTH) + "-"
                + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        try {
            this.logs.add(new Log(name, date));
            this.write(name, arg0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Log {
        private final String name;
        private final BufferedWriter buffer;

        public Log(String name, String date) throws IOException {
            this.name = name;

            if (!new File("Logs/" + this.name).exists())
                new File("Logs/" + this.name).mkdir();

            this.buffer = new BufferedWriter(new FileWriter("Logs/" + this.name
                    + "/" + date + ".log", true));
            this.write("Starting logger..");
        }

        public void write(String arg0) throws IOException {
            final int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            min = Calendar.getInstance().get(Calendar.MINUTE),
            sec = Calendar.getInstance().get(Calendar.SECOND);

            final String date = "[" + (hour < 10 ? "0" : "") + hour + " : " + (min < 10 ? "0" : "") + min + " : "
                    + (sec < 10 ? "0" : "") + sec + "] : ";

            this.buffer.write(date + arg0);
            this.buffer.newLine();
            this.buffer.flush();
        }

        public String getName() {
            return name;
        }

        public BufferedWriter getBuffer() {
            return buffer;
        }
    }
}