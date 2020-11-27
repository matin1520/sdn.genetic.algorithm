package com.csi6900;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class Logger
{
    static java.util.logging.Logger logger;

    private Logger()
    {
        logger = java.util.logging.Logger.getLogger("MyLoggerName");
        logger.setUseParentHandlers(false);
        try
        {
            var directoryName = "logs";
            var directory = new File(directoryName);
            if (!directory.exists())
                directory.mkdir();

            var fh = new FileHandler("logs/sdn.log");

            var formatter = new SimpleFormatter()
            {
                private static final String format = "%1$tF %1$tT - %2$s - %3$s.%4$s || %5$s%n";

                @Override
                public synchronized String format(LogRecord lr)
                {
                    return String.format(
                            format,
                            new Date(lr.getMillis()),
                            lr.getLevel().getLocalizedName(),
                            lr.getSourceClassName(),
                            lr.getSourceMethodName(),
                            lr.getMessage()
                    );
                }
            };

            fh.setFormatter(formatter);
            logger.addHandler(fh);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static java.util.logging.Logger getLogger()
    {
        if(logger == null)
            new Logger();

        return logger;
    }

    private static void log(Level level, String msg)
    {
        var stackTraceElements = Thread.currentThread().getStackTrace();
        var className = stackTraceElements[3].getClassName();
        var methodName = stackTraceElements[3].getMethodName();

        getLogger().logp(level, className, methodName, msg);
    }

    public static void Info(String msg) { log(Level.INFO, msg); }

    public static void Warn(String msg) { log(Level.WARNING, msg); }

    public static void Error(String msg) { log(Level.SEVERE, msg); }

}
