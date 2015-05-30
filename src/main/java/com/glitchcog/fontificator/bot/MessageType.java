package com.glitchcog.fontificator.bot;

/**
 * Message Type
 * 
 * @author Matt Yanos
 */
public enum MessageType
{
    // @formatter:off
    JOIN(false, " "), 
    ACTION(true, " "), 
    NORMAL(true, ": "), 
    MANUAL(false, ": ");
    // @formatter:on

    /**
     * Whether this type of message should be used to lookup the capitalization of a username
     */
    private final boolean parsableUsername;

    /**
     * The text to place in between the username and the content of the message
     */
    private final String contentBreaker;

    /**
     * Construct message type
     * 
     * @param parsableUsername
     */
    private MessageType(boolean parsableUsername, String contentBreaker)
    {
        this.parsableUsername = parsableUsername;
        this.contentBreaker = contentBreaker;
    }

    /**
     * Get whether this type of message should be used to lookup the capitalization of a username
     * 
     * @return parsableUsername
     */
    public boolean containsParsableUsername()
    {
        return parsableUsername;
    }

    /**
     * Get the text to place in between the username and the content of the message
     * 
     * @return contentBreaker
     */
    public String getContentBreaker()
    {
        return contentBreaker;
    }
}
