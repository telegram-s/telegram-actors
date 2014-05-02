package org.telegram.actors.log;

/**
 * Created by ex3ndr on 03.04.14.
 */
public class Logger {

    private static LogInterface logInterface;

    public static void registerInterface(LogInterface logInterface) {
        Logger.logInterface = logInterface;
    }

    public static void w(String tag, String message) {
        if (logInterface != null) {
            logInterface.w(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (logInterface != null) {
            logInterface.d(tag, message);
        }
    }

    public static void e(String tag, Throwable t) {
        if (logInterface != null) {
            logInterface.e(tag, t);
        }
    }
}
