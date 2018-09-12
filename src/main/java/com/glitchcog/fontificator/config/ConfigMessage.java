package com.glitchcog.fontificator.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.chat.clock.MessageExpirer;
import com.glitchcog.fontificator.gui.chat.clock.MessageProgressor;

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
    public static final int MAX_QUEUE_SIZE = 5000;

    public static final int MIN_MESSAGE_SPEED = 1;
    public static final int MAX_MESSAGE_SPEED = 121;

    public static final int MIN_MESSAGE_EXPIRATION = 0;
    public static final int MAX_MESSAGE_EXPIRATION = 720;

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

    /**
     * The age a message can reach prior to its expiration, or zero if message expiration is turned off
     */
    private Integer expirationTime;

    /**
     * Whether the border should be hidden if there are no messages to display
     */
    private Boolean hideEmptyBorder;

    /**
     * Whether the background should be hidden if there are no messages to display
     */
    private Boolean hideEmptyBackground;

    /**
     * The method for resolving the capitalization of usernames
     */
    private UsernameCaseResolutionType caseResolutionType;

    /**
     * Whether the users are permitted to specify their own name casing by typing their own username anywhere in a
     * message
     */
    private Boolean specifyCaseAllowed;

    /**
     * Whether to leave casing alone in the message, or force all alphabetic characters to uppercase or lowercase
     */
    private MessageCasing messageCasing;

    @Override
    public void reset()
    {
        this.joinMessages = null;
        this.usernames = null;
        this.timestamps = null;
        this.timeFormat = null;
        this.queueSize = null;
        this.messageSpeed = null;
        this.expirationTime = null;
        this.hideEmptyBorder = null;
        this.hideEmptyBackground = null;
        this.caseResolutionType = null;
        this.specifyCaseAllowed = null;
        this.messageCasing = null;
    }

    public LoadConfigReport validateTimeFormat(LoadConfigReport report, String timeFormatStr)
    {
        try
        {
            DateFormat df = new SimpleDateFormat(timeFormatStr);
            df.format(new Date());
        }
        catch (Exception e)
        {
            report.addError("The value \"" + timeFormatStr + "\" of key " + FontificatorProperties.KEY_MESSAGE_TIMEFORMAT + " could not be used to parse a date", LoadConfigErrorType.PARSE_ERROR_STRING);
        }
        return report;
    }

    public LoadConfigReport validateStrings(LoadConfigReport report, String timeFormatStr, String queueSizeStr, String messageSpeedStr, String expirationTimeStr)
    {
        validateTimeFormat(report, timeFormatStr);

        validateIntegerWithLimitString(FontificatorProperties.KEY_MESSAGE_QUEUE_SIZE, queueSizeStr, MIN_QUEUE_SIZE, MAX_QUEUE_SIZE, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_MESSAGE_SPEED, messageSpeedStr, MIN_MESSAGE_SPEED, MAX_MESSAGE_SPEED, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_MESSAGE_EXPIRATION_TIME, expirationTimeStr, MIN_MESSAGE_EXPIRATION, MAX_MESSAGE_EXPIRATION, report);

        return report;
    }

    public LoadConfigReport validateStrings(LoadConfigReport report, String timeFormatStr, String queueSizeStr, String messageSpeedStr, String expirationTimerStr, String hideEmptyBorderBool, String hideEmptyBgBool, String caseTypeStr, String joinBool, String userBool, String timestampBool, String specifyCaseBool, String msgCasingStr)
    {
        validateStrings(report, timeFormatStr, queueSizeStr, messageSpeedStr, expirationTimerStr);

        validateBooleanStrings(report, joinBool, userBool, timestampBool, specifyCaseBool, hideEmptyBorderBool, hideEmptyBgBool);

        if (!UsernameCaseResolutionType.contains(caseTypeStr))
        {
            report.addError("Value of key \"" + FontificatorProperties.KEY_MESSAGE_CASE_TYPE + "\" is invalid.", LoadConfigErrorType.PARSE_ERROR_ENUM);
        }

        if (!MessageCasing.contains(msgCasingStr))
        {
            report.addError("Value of key \"" + FontificatorProperties.KEY_MESSAGE_CASING + "\" is invalid.", LoadConfigErrorType.PARSE_ERROR_ENUM);
        }

        return report;
    }

    @Override
    public LoadConfigReport load(Properties props, LoadConfigReport report)
    {
        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.MESSAGE_KEYS, report);

        if (report.isErrorFree())
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
            final String msgCaseStr = props.getProperty(FontificatorProperties.KEY_MESSAGE_CASING);
            final String expTimerStr = props.getProperty(FontificatorProperties.KEY_MESSAGE_EXPIRATION_TIME);
            final String hideEmptyBorderStr = props.getProperty(FontificatorProperties.KEY_MESSAGE_HIDE_EMPTY_BORDER);
            final String hideEmptyBgStr = props.getProperty(FontificatorProperties.KEY_MESSAGE_HIDE_EMPTY_BACKGROUND);
            validateStrings(report, tfString, quSizeStr, msgSpeedStr, expTimerStr, hideEmptyBorderStr, hideEmptyBgStr, caseTpStr, joinBool, userBool, timestampBool, specifyCaseBool, msgCaseStr);

            // Fill the values
            if (report.isErrorFree())
            {
                this.joinMessages = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_JOIN, report);
                this.usernames = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_USERNAME, report);
                this.timestamps = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_TIMESTAMP, report);
                this.timeFormat = tfString;
                this.queueSize = evaluateIntegerString(props, FontificatorProperties.KEY_MESSAGE_QUEUE_SIZE, report);
                this.messageSpeed = evaluateIntegerString(props, FontificatorProperties.KEY_MESSAGE_SPEED, report);
                this.expirationTime = evaluateIntegerString(props, FontificatorProperties.KEY_MESSAGE_EXPIRATION_TIME, report);
                this.hideEmptyBorder = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_HIDE_EMPTY_BORDER, report);
                this.hideEmptyBackground = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_HIDE_EMPTY_BACKGROUND, report);
                this.caseResolutionType = UsernameCaseResolutionType.valueOf(caseTpStr);
                this.specifyCaseAllowed = evaluateBooleanString(props, FontificatorProperties.KEY_MESSAGE_CASE_SPECIFY, report);
                this.messageCasing = MessageCasing.valueOf(msgCaseStr);
            }
        }

        return report;
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

    public Integer getExpirationTime()
    {
        return expirationTime;
    }

    public void setExpirationTime(Integer expirationTime, MessageExpirer expirer)
    {
        this.expirationTime = expirationTime;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_EXPIRATION_TIME, Integer.toString(expirationTime));
        if (expirer != null)
        {
            if (expirationTime == MIN_MESSAGE_EXPIRATION)
            {
                expirer.cancelLatest();
            }
            else
            {
                expirer.startClock();
            }
        }
    }

    public boolean isMessageExpirable()
    {
        return expirationTime != null && expirationTime != 0;
    }

    public boolean isHideEmptyBorder()
    {
        return hideEmptyBorder;
    }

    public void setHideEmptyBorder(boolean hideEmptyBorder)
    {
        this.hideEmptyBorder = hideEmptyBorder;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_HIDE_EMPTY_BORDER, Boolean.toString(hideEmptyBorder));
    }

    public boolean isHideEmptyBackground()
    {
        return hideEmptyBackground;
    }

    public void setHideEmptyBackground(boolean hideEmptyBackground)
    {
        this.hideEmptyBackground = hideEmptyBackground;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_HIDE_EMPTY_BACKGROUND, Boolean.toString(hideEmptyBackground));
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

    public MessageCasing getMessageCasing()
    {
        return messageCasing;
    }

    public void setMessageCasing(MessageCasing messageCasing)
    {
        this.messageCasing = messageCasing;
        props.setProperty(FontificatorProperties.KEY_MESSAGE_CASING, messageCasing.name());
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((joinMessages == null) ? 0 : joinMessages.hashCode());
        result = prime * result + ((timeFormat == null) ? 0 : timeFormat.hashCode());
        result = prime * result + ((timestamps == null) ? 0 : timestamps.hashCode());
        result = prime * result + ((usernames == null) ? 0 : usernames.hashCode());
        result = prime * result + ((messageCasing == null) ? 0 : messageCasing.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfigMessage other = (ConfigMessage) obj;
        if (messageCasing != other.messageCasing)
            return false;
        if (joinMessages == null)
        {
            if (other.joinMessages != null)
                return false;
        }
        else if (!joinMessages.equals(other.joinMessages))
            return false;
        if (timeFormat == null)
        {
            if (other.timeFormat != null)
                return false;
        }
        else if (!timeFormat.equals(other.timeFormat))
            return false;
        if (timestamps == null)
        {
            if (other.timestamps != null)
                return false;
        }
        else if (!timestamps.equals(other.timestamps))
            return false;
        if (usernames == null)
        {
            if (other.usernames != null)
                return false;
        }
        else if (!usernames.equals(other.usernames))
            return false;
        return true;
    }

    /**
     * Perform a deep copy of the message config, used to compare against the previous one used to generated the string
     * of characters and emojis that are stored in a Message object
     * 
     * @param copy
     */
    public void deepCopy(ConfigMessage copy)
    {
        this.joinMessages = copy.joinMessages;
        this.timeFormat = copy.timeFormat;
        this.timestamps = copy.timestamps;
        this.usernames = copy.usernames;
        this.messageCasing = copy.messageCasing;
    }

}
