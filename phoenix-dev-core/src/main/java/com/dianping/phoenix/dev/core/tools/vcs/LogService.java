package com.dianping.phoenix.dev.core.tools.vcs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class LogService {

    private OutputStream        output;
    private static final int    PROGRESSBAR_LEN = 50;
    private final static String FINISH_ICON     = "\uD83C\uDF7A   ";

    public LogService(OutputStream output) {
        super();
        this.output = output;
    }

    public void log(String message) {
        try {
            message = System.getProperty("line.separator") + message;
            output.write(message.getBytes());
        } catch (IOException e) {
            // do nothing
        }
    }

    public void updateProgressBar(double pcnt, String tips) {
        try {
            PrintStream ps = new PrintStream(output, true, "utf-8");
            int count = (int) (pcnt * PROGRESSBAR_LEN / 100.0);
            ps.write("\r".getBytes());
            if (count == PROGRESSBAR_LEN) {
                ps.print(FINISH_ICON);
            }
            ps.write("[".getBytes());
            for (int i = 0; i < count; i++) {
                ps.write("=".getBytes());
            }
            ps.write(">".getBytes());
            for (int i = count; i < PROGRESSBAR_LEN; i++) {
                ps.write(" ".getBytes());
            }
            ps.write(("] " + tips).getBytes());
        } catch (IOException e) {
        }
    }
}
