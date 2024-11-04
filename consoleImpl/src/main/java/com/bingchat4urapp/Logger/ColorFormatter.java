package com.bingchat4urapp.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ColorFormatter extends Formatter {

    private static final String INFO_COLOR = "\u001B[37m"; // White
    private static final String WARNING_COLOR = "\u001B[33m"; // Dark Yellow
    private static final String ERROR_COLOR = "\u001B[31m"; // Dark Red
    private static final String RESET_COLOR = "\u001B[0m";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm]");

    @Override
    public String format(LogRecord record) {
        String levelColor;
        switch (record.getLevel().getName()) {
            case "WARNING":
                levelColor = WARNING_COLOR;
                break;
            case "SEVERE":
                levelColor = ERROR_COLOR;
                break;
            case "INFO":
                levelColor = INFO_COLOR;
                break;
            default:
                levelColor = RESET_COLOR;
                break;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(levelColor)
          .append(dateFormat.format(new Date(record.getMillis())))
          .append(" ")
          .append(record.getLevel().getName())
          .append(" - ")
          .append(record.getSourceClassName())
          .append(".")
          .append(record.getSourceMethodName())
          .append(" - ")
          .append(formatMessage(record))
          .append(RESET_COLOR)
          .append("\n");

        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            sb.append(ERROR_COLOR).append(sw.toString()).append(RESET_COLOR);
        }

        return sb.toString();
    }
}
