package com.dianping.maven.plugin.tools.console;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;

public class ConsoleIO {

    private static final String ENCODING             = "utf-8";
    private final static int    DEFAULT_COLUMN_WIDTH = 30;
    private final static String ICON                 = "\uD83C\uDF7A   ";

    private String format(List<String> values, int column, int columnWidth) {
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        int idx = 0;
        for (String v : values) {
            sb.append(StringUtils.rightPad(idx + ":" + v, columnWidth));
            if (idx % column == column - 1) {
                sb.append(newLine);
            } else {
                sb.append("\t");
            }
            idx++;
        }
        return sb.toString();
    }

    public List<String> choose(List<String> availableValues, int column, String prompt, PrintStream out, Reader in)
            throws IOException {
        return choose(availableValues, column, prompt, out, in, DEFAULT_COLUMN_WIDTH);
    }

    public List<String> choose(List<String> availableValues, int column, String prompt, PrintStream out, Reader in,
            int columnWidth) throws IOException {
        lineMessage(prompt, true);
        lineMessage(format(availableValues, column, columnWidth).trim(), false);

        List<String> choosedValues = new ArrayList<String>();

        char[] buffer = new char[1024];
        int size = in.read(buffer);
        while (size > 0 && (buffer[size - 1] == '\n' || buffer[size - 1] == '\r')) {
            size--;
        }
        if (size > 0) {
            String input = new String(buffer, 0, size);
            String[] idxes = input.split(",");
            for (int i = 0; i < idxes.length; i++) {
                int idx = -1;
                try {
                    idx = Integer.parseInt(idxes[i].trim());
                } catch (Exception e) {
                    // ignore it
                }
                if (idx >= 0 && idx < availableValues.size()) {
                    choosedValues.add(availableValues.get(idx));
                }
            }
        }

        return choosedValues;
    }

    public List<String> choice(List<String> availableValues, int column, String prompt) throws IOException {
        return choice(availableValues, column, prompt, DEFAULT_COLUMN_WIDTH);
    }

    public List<String> choice(List<String> availableValues, int column, String prompt, int columnWidth)
            throws IOException {
        return choose(availableValues, column, prompt, new PrintStream(System.out, true, ENCODING),
                new InputStreamReader(System.in), columnWidth);
    }

    public void message(String msg) throws IOException {
        message(msg, false);
    }

    public void message(String msg, boolean withIcon) throws IOException {
        PrintStream ps = new PrintStream(System.out, true, ENCODING);
        if (withIcon) {
            ps.print(ICON + msg);
        } else {
            ps.print(msg);
        }
    }

    public void lineMessage(String msg) throws IOException {
        lineMessage(msg, false);
    }

    public void lineMessage(String msg, boolean withIcon) throws IOException {
        PrintStream ps = new PrintStream(System.out, true, ENCODING);
        message(msg, withIcon);
        ps.println();
    }

    public void newLine() throws IOException {
        PrintStream ps = new PrintStream(System.out, true, ENCODING);
        ps.println();
    }

    public String readInput(String defaultValue) {
        Scanner cin = new Scanner(System.in);
        String input = cin.nextLine();
        if (defaultValue == null) {
            return input;
        } else {
            if (StringUtils.isBlank(input)) {
                return defaultValue;
            } else {
                return input;
            }
        }
    }

    public String readInput() {
        return readInput(null);
    }
}
