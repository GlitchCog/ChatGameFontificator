package com.glitchcog.fontificator.bot;

import java.util.Date;

import com.glitchcog.fontificator.config.ConfigMessage;

/**
 * Anything posted to an IRC channel
 * 
 * @author Matt Yanos
 */
public class Message
{
    /**
     * The username of the poster, or the username of the user who joined, if the message is a join message
     */
    private final String username;

    /**
     * A time stamp of when the message was created
     */
    private final Date timestamp;

    /**
     * The text content of the message
     */
    private final String content;

    /**
     * The type of the message
     */
    private final MessageType type;

    /**
     * Whether the message is completely drawn or not. Because the fully displayed message can change, like if the
     * option to show the time stamp is selected on the fly, once any configuration of the message is fully displayed,
     * it should remain fully displayed, even though the draw cursor won't be at the end of the now longer message text
     */
    private boolean completelyDrawn;

    /**
     * The text that goes between a time stamp and a username
     */
    private static final String TIMESTAMP_USERNAME_SPACER = " ";

    /**
     * The maximum possible value of an int cast into a float, used to max out the character count if the message speed
     * is maxed out
     */
    private static final float MAX_INT_AS_FLOAT = (float) Integer.MAX_VALUE;

    /**
     * How much of the message should be drawn
     */
    private float drawCursor;

    /**
     * Construct a message specifying the type, username and content, but set the time stamp to the current time
     * 
     * @param type
     * @param username
     * @param content
     */
    public Message(MessageType type, String username, String content)
    {
        this(type, username, new Date(), content);
    }

    /**
     * Construct a message specifying everything
     * 
     * @param type
     * @param username
     * @param timestamp
     * @param content
     */
    public Message(MessageType type, String username, Date timestamp, String content)
    {
        this.type = type;
        this.username = username;
        this.timestamp = timestamp;
        this.content = content;
        this.drawCursor = 0.0f;
    }

    /**
     * Get whether the message is a join type message
     * 
     * @return joinType
     */
    public boolean isJoinType()
    {
        return MessageType.JOIN.equals(type);
    }

    /**
     * Get the type of the message
     * 
     * @return type
     */
    public MessageType getType()
    {
        return type;
    }

    /**
     * Get the username of the poster, or the username of the user who joined, if the message is a join message
     * 
     * @return username
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * Increment the draw cursor based on the message text as defined by the specified messageConfig and its message
     * speed setting
     * 
     * @param messageConfig
     */
    public void incrementDrawCursor(ConfigMessage messageConfig)
    {
        float characterCount = 1.0f;

        // If the message speed is zero, then it's max, so draw everything
        if (messageConfig.getMessageSpeed() < 1)
        {
            characterCount = MAX_INT_AS_FLOAT;
        }
        // If this is true, then multiple characters need to be drawn per update
        else if (1000L / messageConfig.getMessageSpeed() < ConfigMessage.SHORTEST_DELAY)
        {
            characterCount = (float) messageConfig.getMessageSpeed() * (float) ConfigMessage.SHORTEST_DELAY / 1000.0f;
        }

        drawCursor += characterCount;

        // Whether the message is completely drawn yet needs to be calculated
        // and set as a member variable once true so that configuration changes
        // that modify the length of the message do not re-trigger drawing bits
        // of past messages
        if (drawCursor >= getText(messageConfig).length())
        {
            completelyDrawn = true;
        }
    }

    /**
     * Get the draw cursor, or the max draw cursor value if the message is completely drawn
     * 
     * @return drawCursor
     */
    public float getDrawCursor()
    {
        return completelyDrawn ? MAX_INT_AS_FLOAT : drawCursor;
    }

    /**
     * Get whether the message is completely drawn
     * 
     * @return completelyDrawn
     */
    public boolean isCompletelyDrawn()
    {
        return completelyDrawn;
    }

    /**
     * Get the time stamp
     * 
     * @return timestamp
     */
    public Date getTimestamp()
    {
        return timestamp;
    }

    /**
     * Get the string representation of the time stamp using the
     * 
     * @param messageConfig
     * @return timestampString
     */
    public String getTimestampString(ConfigMessage messageConfig)
    {
        return messageConfig.getTimerFormatter().format(timestamp);
    }

    /**
     * Get the content of the message
     * 
     * @return content
     */
    public String getContent()
    {
        return content;
    }

    @Override
    public String toString()
    {
        return username + ": " + content;
    }

    /**
     * Get the index right after the username based on the specified messageConfig
     * 
     * @param messageConfig
     * @return
     */
    public int getIndexTimestamp(ConfigMessage messageConfig)
    {
        return messageConfig.showTimestamps() ? getTimestampString(messageConfig).length() : 0;
    }

    /**
     * Get the index right after the username based on the specified messageConfig
     * 
     * @param messageConfig
     * @return username index
     */
    public int getIndexUsername(ConfigMessage messageConfig)
    {
        int index = 0;
        if (messageConfig.showTimestamps())
        {
            index += getIndexTimestamp(messageConfig);
        }
        if (messageConfig.showUsernames())
        {
            if (messageConfig.showTimestamps())
            {
                index += TIMESTAMP_USERNAME_SPACER.length();
            }
            index += username.length();
        }
        return index;
    }

    /**
     * Get the full text of the message to display based on the specified messageConfig
     * 
     * @param messageConfig
     * @return message text
     */
    public String getText(ConfigMessage messageConfig)
    {
        String text = "";
        if (messageConfig.showTimestamps())
        {
            text += messageConfig.getTimerFormatter().format(timestamp);
        }
        if (messageConfig.showUsernames())
        {
            if (messageConfig.showTimestamps())
            {
                text += TIMESTAMP_USERNAME_SPACER;
            }
            text += username;
        }
        if (messageConfig.showUsernames() || messageConfig.showTimestamps())
        {
            text += type.getContentBreaker();
        }
        text += content;
        return text;
    }

}
