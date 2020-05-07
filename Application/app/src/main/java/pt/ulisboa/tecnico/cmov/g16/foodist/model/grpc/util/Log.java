package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util;

import java.text.MessageFormat;

public class Log {

    public static void appendLogs(StringBuffer logs, String msg, Object... params) {
        if (params.length > 0) {
            logs.append(MessageFormat.format(msg, params));
        } else {
            logs.append(msg);
        }
        logs.append("\n");
    }
}
