package com.glitchcog.fontificator.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.glitchcog.fontificator.gui.chat.MessageProgressor;

/**
 * The configuration for how to display the messages
 * 
 * @author Matt Yanos
 */
public class ConfigMessage extends Config
{
    /**
     * This is the shortest a delay should be for updating the message. 67 ms translates to approximately 15 updates per
     * second
     */
    public static final long SHORTEST_DELAY = 67L;

    public static final int MIN_QUEUE_SIZE = 1;
    public static final int MAX_QUEUE_SIZE = 256;

    public static final int MIN_MESSAGE_SPEED = 1;
    public static final int MAX_MESSAGE_SPEED = 121;

    private static final long MIN_MESSAGE_DELAY = 1L;

    /**
     * Whether to display join messages
     */
    private Boolean joinMessages;

    /**
     * Whether to show the username of the message poster
     */
    private Boolean usernames;

    /**
     * Whether to show the timestamps of the message
     */
    private Boolean timestamps;

    /**
     * The datetime pattern for the timestamps
     */
    private String timeFormat;

    /**
     * The formatter for timestamps
     */
    private DateFormat timeFormatter;

    /**
     * The number of messages to keep to draw and scroll back through
     */
    private Integer queueSize;

    /**
     * The rate at which messages are written in characters per second
     */
    private Integer messageSpeed;

    private UsernameCaseResolutionType caseResolutionType;

    private Boolean specifyCaseAllowed;

    @Override
    public void reset()
    {
        this.joinMessages = null;
        this.usernames = null;
        this.timestamps = null;
        this.timeFormat = null;
        this.queueSize = null;
        this.messageSpeed = null;
        this.caseResolutionType = null;
        this.specifyCaseAllowed = null;
    }

    public List<String> validateTimeFormat(List<String> errors, String timeFormatStr)
    {
        try
        {
            DateFormat df = new SimpleDateFormat(timeFormatStr);
            df.format(new Date());
        }
        catch (Exception e)
        {
            errors.add("The value \"" + timeFormatStr + "\" of key " + FontificatorProperties.KEY_MESSAGE_TIMEFORMAT + " could not be used to parse a date");
        }
        return errors;
    }

    public List<String> validateStrings(List<String> errors, String timeFormatStr, String queueSizeStr, String messageSpeedStr)
    {
        validateTimeFormat(errors, timeFormatStr);

        validateIntegerWithLimitString(FontificatorProperties.KEY_MESSAGE_QUEUE_SIZE, queueSizeStr, MIN_QUEUE_SIZE, MAX_QUEUE_SIZE, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_MESSAGE_SPEED, messageSpeedStr, MIN_MESSAGE_SPEED, MAX_MESSAGE_SPEED, errors);

        return errors;
    }

    public List<String> validateStrings(List<String> errors, String timeFormatStr, String queueSizeStr, String messageSpeedStr, String caseTypeStr, String joinBool, String userBool, String timestampBool, String specifyCaseBool)
    {
        validateStrings(errors, timeFormatStr, queueSizeStr, messageSpeedStr);

        validateBooleanStrings(errors, joinBool, userBool, timestampBool, specifyCaseBool);

        if (!UsernameCaseResolutionType.contains(caseTypeStr))
        {
            errors.add("Value of key \"" + FontificatorProperties.KEY_MESSAGE_CASE_TYPE + "\" is invalid.");
        }

        return errors;
    }

    @Override
    public List<String> load(Properties props, List<String> errors)
    {
        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.MESSAGE_KEYS, errors);

        if (errors.isEmpty())
        {
            // Check that the values are valid
            final String tfString = props.getProperty(FontificatorProperties.KEY_MESSAGE_TIMEFORMAT);
            final String quSizeStr = props.getProperty(FontificatorProperties.KEY_MESSAGE_QUEUE_SIZE);
            final String msgSpeedStr = props.getProperty(FontificatorProperties.KEY_MESSAGE_SPEED);
            final String caseTpStr = props.getProperty(FontificatorProperties.KEY_MESSAGE_CASE_TYPE);
            final String joinBool = props.getProperty(FontificatorProperties.KEY_MESSAGE_JOIN);
            final String userBool = props.getProperty(FontificatorProperties.KEY_MESSAGE_USERNAME);
            final String timestampBool = props.getProperty(FontificatorProperties.KEY_MESSAGE_TIMESTAMP);
            final String specifyCaseBool = props.getProperty(FontificatorProperties.KEY_MESSAGE_CASE_SPECIFY);
            validateStrings(errors, tfString, quSizeStr, msgSpeedStr, caseTpStr, joinBool, userBool, timestampBool, specifyCaseBool);

            // Fill the values
            if (errors.isEmpty())
            {
                this.joinMessages = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_JOIN, errors);
                this.usernames = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_USERNAME, errors);
                this.timestamps = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_TIMESTAMP, errors);
                this.timeFormat = props.getProperty(FontificatorProperties.KEY_MESSAGE_TIMEFORMAT);
                this.queueSize = evaluateIntegerString(props, FontificatorProperties.KEY_MESSAGE_QUEUE_SIZE, errors);
                this.messageSpeed = evaluateIntegerString(props, FontificatorProperties.KEY_MESSAGE_SPEED, errors);
                this.caseResolutionType = UsernameCaseResolutionType.valueOf(props.getProperty(FontificatorProperties.KEY_MESSAGE_CASE_TYPE));
                this.specifyCaseAllowed = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_CASE_SPECIFY, errors);
            }
        }

        return errors;
    }

    /**
     * Get whether to display join messages
     */
    public boolean showJoinMessages()
    {
        return joinMessages;
    }

    public void setJoinMessages(boolean joinMessages)
    {
        this.joinMessages = joinMessages;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_JOIN, Boolean.toString(joinMessages));
    }

    /**
     * Get whether to show the username of the message poster
     */
    public boolean showUsernames()
    {
        return usernames;
    }

    public void setShowUsernames(boolean usernames)
    {
        this.usernames = usernames;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_USERNAME, Boolean.toString(usernames));
    }

    /**
     * Get whether to show the timestamps of the message
     */
    public boolean showTimestamps()
    {
        return timestamps;
    }

    public void setShowTimestamps(boolean timestamps)
    {
        this.timestamps = timestamps;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_TIMESTAMP, Boolean.toString(timestamps));
    }

    /**
     * Get the datetime pattern for the timestamps
     */
    public String getTimeFormat()
    {
        return timeFormat;
    }

    public DateFormat getTimerFormatter()
    {
        return timeFormatter;
    }

    public void setTimeFormat(String timeFormat)
    {
        this.timeFormat = timeFormat;
        this.timeFormatter = new SimpleDateFormat(timeFormat);
        props.setProperty(FontificatorProperties.KEY_MESSAGE_TIMEFORMAT, timeFormat);
    }

    /**
     * Get the number of messages to keep to draw and scroll back through
     */
    public int getQueueSize()
    {
        return queueSize;
    }

    public void setQueueSize(int queueSize)
    {
        this.queueSize = queueSize;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_QUEUE_SIZE, Integer.toString(queueSize));
    }

    /**
     * Get the rate at which messages are written in characters per second
     * 
     * @return messageSpeed
     */
    public int getMessageSpeed()
    {
        return messageSpeed == null ? 0 : messageSpeed;
    }

    public long getMessageDelay()
    {
        return this.messageSpeed <= 0 || this.messageSpeed >= MAX_MESSAGE_SPEED ? MIN_MESSAGE_DELAY : 1000L / this.messageSpeed;
    }

    /**
     * Set the message speed and update the message progressor timer to match the newly set speed
     * 
     * @param messageSpeed
     * @param progressor
     */
    public void setMessageSpeed(int messageSpeed, MessageProgressor progressor)
    {
        this.messageSpeed = messageSpeed;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_SPEED, Integer.toString(messageSpeed));
        if (progressor != null)
        {
            progressor.refreshTimer(getMessageDelay());
        }
    }

    public UsernameCaseResolutionType getCaseResolutionType()
    {
        return caseResolutionType;
    }

    public void setCaseResolutionType(UsernameCaseResolutionType caseResolutionType)
    {
        this.caseResolutionType = caseResolutionType;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_CASE_TYPE, caseResolutionType.name());
    }

    public Boolean isSpecifyCaseAllowed()
    {
        return specifyCaseAllowed;
    }

    public void setSpecifyCaseAllowed(Boolean specifyCaseAllowed)
    {
        this.specifyCaseAllowed = specifyCaseAllowed;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_CASE_SPECIFY, Boolean.toString(specifyCaseAllowed));
    }

}
