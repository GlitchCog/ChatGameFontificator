package com.glitchcog.fontificator.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.glitchcog.fontificator.FontificatorMain;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;

public class DebugAppender extends WriterAppender implements UncaughtExceptionHandler
{
    private LogBox debugLogBox;

    public DebugAppender(LogBox debugLogBox)
    {
        super(FontificatorMain.LOG_PATTERN_LAYOUT, System.out);
        this.debugLogBox = debugLogBox;
        setName("Debug Logging");
        setThreshold(Level.DEBUG);
    }

    @Override
    public void append(LoggingEvent event)
    {
        debugLogBox.log(this.layout.format(event));
        ThrowableInformation info = event.getThrowableInformation();
        if (info != null && info.getThrowable() != null)
        {
            Throwable t = info.getThrowable();
            debugLogBox.log(throwableToString(t));
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e)
    {
        try
        {
            System.err.println("Exception in thread \"" + t.getName() + "\" ");
            e.printStackTrace(System.err);
            debugLogBox.log("Exception in thread \"" + t.getName() + "\" ");
            debugLogBox.log(throwableToString(e));
        }
        catch (Exception debugException)
        {
            debugException.printStackTrace();
        }
    }

    private static String throwableToString(Throwable e)
    {
        StringWriter stackTraceWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTraceWriter));
        return stackTraceWriter.toString();
    }
}
