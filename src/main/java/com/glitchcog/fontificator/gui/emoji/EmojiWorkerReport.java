package com.glitchcog.fontificator.gui.emoji;

/**
 * A simple class to house multiple data types for the intermediate results
 * 
 * @author Matt Yanos
 */
public class EmojiWorkerReport
{
    /**
     * The human readable message describing the work
     */
    private final String message;

    /**
     * The percent complete, 0 to 100
     */
    private final int percentComplete;

    /**
     * Indicates an error has occurred
     */
    private final boolean error;

    /**
     * Indicates the work has been canceled
     */
    private final boolean canceled;

    public EmojiWorkerReport(String message, int percentComplete, boolean error, boolean canceled)
    {
        this.message = message;
        this.percentComplete = percentComplete;
        this.error = error;
        this.canceled = canceled;
    }

    /**
     * Get the human readable message describing the work
     * 
     * @return message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Get the percent complete, 0 to 100
     * 
     * @return percentCompete
     */
    public int getPercentComplete()
    {
        return percentComplete;
    }

    /**
     * Get the percent text formatted with leading spaces and a percent sign
     * 
     * @return percentText
     */
    public String getPercentText()
    {
        String ps = Integer.toString(percentComplete);
        while (ps.length() < 3)
        {
            ps = " " + ps;
        }
        return ps + "%";
    }

    public boolean isComplete()
    {
        return percentComplete == 100 && !isError() && !isCanceled();
    }

    public boolean isError()
    {
        return error;
    }

    public boolean isCanceled()
    {
        return canceled;
    }
}
