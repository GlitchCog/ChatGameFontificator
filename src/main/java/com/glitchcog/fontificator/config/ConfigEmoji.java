package com.glitchcog.fontificator.config;

import java.util.Properties;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.emoji.EmojiType;

/**
 * The configuration for the Emoji
 * 
 * @author Matt Yanos
 */
public class ConfigEmoji extends Config
{
    public static final int MIN_SCALE = 10;
    public static final int MAX_SCALE = 500;

    /**
     * Whether all emoji are enabled or not
     */
    private Boolean emojiEnabled;

    /**
     * Whether the emoji scale value is used to modify the size of the emoji relative to the line height, or the
     * original emoji image size
     */
    private Boolean scaleToLine;

    /**
     * The scale value to be applied to the emoji
     */
    private Integer scale;

    /**
     * The channel from which to load emoji
     */
    private String channel;

    /**
     * Whether to use the IRC control tab's connection channel value from which to load emojis
     */
    private Boolean connectChannel;

    /**
     * The strategy for drawing an emoji when the emoji key word and image URL are loaded, but the image itself is not
     */
    private EmojiLoadingDisplayStragegy displayStrategy;

    /**
     * Whether Twitch emotes are enabled or not
     */
    private Boolean twitchEnabled;

    /**
     * Whether Twitch emotes marked as subscriber only are disabled or not
     */
    private Boolean twitchDisableSubscriber;

    /**
     * Whether FrankerFaceZ emotes are enabled or not
     */
    private Boolean ffzEnabled;

    /**
     * Whether the Twitch emotes have been loaded
     */
    private Boolean twitchLoaded;

    /**
     * Whether the FrankenFaceZ emotes have been loaded
     */
    private Boolean ffzLoaded;

    public ConfigEmoji()
    {
        twitchLoaded = false;
        ffzLoaded = false;
    }

    @Override
    public void reset()
    {
        emojiEnabled = null;
        scaleToLine = null;
        scale = null;
        channel = null;
        connectChannel = null;
        displayStrategy = null;
        twitchEnabled = null;
        twitchDisableSubscriber = null;
        ffzEnabled = null;
    }

    public Boolean isEmojiEnabled()
    {
        return emojiEnabled;
    }

    public void setEmojiEnabled(Boolean emojiEnabled)
    {
        this.emojiEnabled = emojiEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_ENABLED, Boolean.toString(emojiEnabled));
    }

    public Boolean isScaleToLine()
    {
        return scaleToLine;
    }

    public void setScaleToLine(Boolean scaleToLine)
    {
        this.scaleToLine = scaleToLine;
        props.setProperty(FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE, Boolean.toString(scaleToLine));
    }

    public Integer getScale()
    {
        return scale;
    }

    public void setScale(Integer scale)
    {
        this.scale = scale;
        props.setProperty(FontificatorProperties.KEY_EMOJI_SCALE, Integer.toString(scale));
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        if (channel != null && channel.startsWith("#"))
        {
            channel = channel.substring(1);
        }
        this.channel = channel;
        props.setProperty(FontificatorProperties.KEY_EMOJI_CHANNEL, channel == null ? "" : channel);
    }

    public Boolean isConnectChannel()
    {
        return connectChannel;
    }

    public void setConnectChannel(Boolean connectChannel)
    {
        this.connectChannel = connectChannel;
        props.setProperty(FontificatorProperties.KEY_EMOJI_CONNECT_CHANNEL, Boolean.toString(connectChannel));
    }

    public EmojiLoadingDisplayStragegy getDisplayStrategy()
    {
        return displayStrategy;
    }

    public void setDisplayStrategy(EmojiLoadingDisplayStragegy displayStrategy)
    {
        this.displayStrategy = displayStrategy;
        props.setProperty(FontificatorProperties.KEY_EMOJI_DISPLAY_STRAT, displayStrategy.name());
    }

    public Boolean isTwitchEnabled()
    {
        return twitchEnabled;
    }

    public void setTwitchEnabled(Boolean twitchEnabled)
    {
        this.twitchEnabled = twitchEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE, Boolean.toString(twitchEnabled));
    }

    public Boolean isTwitchSubscriberDisable()
    {
        return twitchDisableSubscriber;
    }

    public void setTwitchDisableSubscriber(Boolean twitchDisableSubscriber)
    {
        this.twitchDisableSubscriber = twitchDisableSubscriber;
        props.setProperty(FontificatorProperties.KEY_EMOJI_TWITCH_SUBSCRIBER, Boolean.toString(twitchDisableSubscriber));
    }

    public Boolean isFfzEnabled()
    {
        return ffzEnabled;
    }

    public void setFfzEnabled(Boolean ffzEnabled)
    {
        this.ffzEnabled = ffzEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_FFZ_ENABLE, Boolean.toString(ffzEnabled));
    }

    /**
     * Get whether the type of emoji is enabled and loaded
     * 
     * @param type
     * @return enabled
     */
    public boolean isTypeEnabledAndLoaded(EmojiType type)
    {
        if (type == null)
        {
            return false;
        }
        else
        {
            switch (type)
            {
            case FRANKERFACEZ:
                return ffzEnabled != null && ffzEnabled && ffzLoaded != null && ffzLoaded;
            case TWITCH_V2:
            case TWITCH_V3:
                return twitchEnabled != null && twitchEnabled && twitchLoaded != null && twitchLoaded;
            default:
                // If it doesn't have a coded EmojiType, then we don't know it
                return false;
            }
        }
    }

    public LoadConfigReport validateStrings(LoadConfigReport report, String enabledBool, String scaleEnabledBool, String scale, String channel, String connectChanBool, String displayStrat, String twitchBool, String twitchSubsBool, String ffzBool)
    {
        validateBooleanStrings(report, enabledBool, scaleEnabledBool, connectChanBool, twitchBool, twitchSubsBool, ffzBool);
        validateIntegerWithLimitString(FontificatorProperties.KEY_EMOJI_SCALE, scale, MIN_SCALE, MAX_SCALE, report);

        return report;
    }

    @Override
    public LoadConfigReport load(Properties props, LoadConfigReport report)
    {
        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.EMOJI_KEYS_WITHOUT_CHANNEL, report);

        if (report.isErrorFree())
        {
            final String enabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_ENABLED);

            final String scaleEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE);
            final String scaleStr = props.getProperty(FontificatorProperties.KEY_EMOJI_SCALE);
            final String channelStr = props.getProperty(FontificatorProperties.KEY_EMOJI_CHANNEL);
            final String connectChannelStr = props.getProperty(FontificatorProperties.KEY_EMOJI_CONNECT_CHANNEL);
            final String displayStratStr = props.getProperty(FontificatorProperties.KEY_EMOJI_DISPLAY_STRAT);

            final String twitchEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE);
            final String twitchSubsDisabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_TWITCH_SUBSCRIBER);

            final String ffzEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_FFZ_ENABLE);

            // Check that the values are valid
            validateStrings(report, enabledStr, scaleEnabledStr, scaleStr, channelStr, connectChannelStr, displayStratStr, twitchEnabledStr, twitchSubsDisabledStr, ffzEnabledStr);

            // Fill the values
            if (report.isErrorFree())
            {
                emojiEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_ENABLED, report);
                scaleToLine = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE, report);
                scale = evaluateIntegerString(props, FontificatorProperties.KEY_EMOJI_SCALE, report);
                channel = channelStr;
                connectChannel = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_CONNECT_CHANNEL, report);
                displayStrategy = EmojiLoadingDisplayStragegy.valueOf(displayStratStr);
                twitchEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE, report);
                twitchDisableSubscriber = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_TWITCH_SUBSCRIBER, report);
                ffzEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_FFZ_ENABLE, report);
            }
        }

        return report;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((connectChannel == null) ? 0 : connectChannel.hashCode());
        result = prime * result + ((displayStrategy == null) ? 0 : displayStrategy.hashCode());
        result = prime * result + ((emojiEnabled == null) ? 0 : emojiEnabled.hashCode());
        result = prime * result + ((ffzEnabled == null) ? 0 : ffzEnabled.hashCode());
        result = prime * result + ((ffzLoaded == null) ? 0 : ffzLoaded.hashCode());
        result = prime * result + ((scale == null) ? 0 : scale.hashCode());
        result = prime * result + ((scaleToLine == null) ? 0 : scaleToLine.hashCode());
        result = prime * result + ((twitchDisableSubscriber == null) ? 0 : twitchDisableSubscriber.hashCode());
        result = prime * result + ((twitchEnabled == null) ? 0 : twitchEnabled.hashCode());
        result = prime * result + ((twitchLoaded == null) ? 0 : twitchLoaded.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ConfigEmoji other = (ConfigEmoji) obj;
        if (channel == null)
        {
            if (other.channel != null)
            {
                return false;
            }
        }
        else if (!channel.equals(other.channel))
        {
            return false;
        }
        if (connectChannel == null)
        {
            if (other.connectChannel != null)
            {
                return false;
            }
        }
        else if (!connectChannel.equals(other.connectChannel))
        {
            return false;
        }
        if (displayStrategy != other.displayStrategy)
        {
            return false;
        }
        if (emojiEnabled == null)
        {
            if (other.emojiEnabled != null)
            {
                return false;
            }
        }
        else if (!emojiEnabled.equals(other.emojiEnabled))
        {
            return false;
        }
        if (ffzEnabled == null)
        {
            if (other.ffzEnabled != null)
            {
                return false;
            }
        }
        else if (!ffzEnabled.equals(other.ffzEnabled))
        {
            return false;
        }
        if (ffzLoaded == null)
        {
            if (other.ffzLoaded != null)
            {
                return false;
            }
        }
        else if (!ffzLoaded.equals(other.ffzLoaded))
        {
            return false;
        }
        if (scale == null)
        {
            if (other.scale != null)
            {
                return false;
            }
        }
        else if (!scale.equals(other.scale))
        {
            return false;
        }
        if (scaleToLine == null)
        {
            if (other.scaleToLine != null)
            {
                return false;
            }
        }
        else if (!scaleToLine.equals(other.scaleToLine))
        {
            return false;
        }
        if (twitchDisableSubscriber == null)
        {
            if (other.twitchDisableSubscriber != null)
            {
                return false;
            }
        }
        else if (!twitchDisableSubscriber.equals(other.twitchDisableSubscriber))
        {
            return false;
        }
        if (twitchEnabled == null)
        {
            if (other.twitchEnabled != null)
            {
                return false;
            }
        }
        else if (!twitchEnabled.equals(other.twitchEnabled))
        {
            return false;
        }
        if (twitchLoaded == null)
        {
            if (other.twitchLoaded != null)
            {
                return false;
            }
        }
        else if (!twitchLoaded.equals(other.twitchLoaded))
        {
            return false;
        }
        return true;
    }

    /**
     * Perform a deep copy of the emoji config, used to compare against the previous one used to generated the string of
     * characters and emojis that are stored in a Message object
     * 
     * @param copy
     */
    public void deepCopy(ConfigEmoji copy)
    {
        this.emojiEnabled = copy.emojiEnabled;
        this.scaleToLine = copy.scaleToLine;
        this.scale = copy.scale;
        this.channel = copy.channel;
        this.connectChannel = copy.connectChannel;
        this.displayStrategy = copy.displayStrategy;
        this.twitchEnabled = copy.twitchEnabled;
        this.twitchDisableSubscriber = copy.twitchDisableSubscriber;
        this.ffzEnabled = copy.ffzEnabled;
        this.twitchLoaded = copy.twitchLoaded;
        this.ffzLoaded = copy.ffzLoaded;
    }

    /**
     * Get whether the Twitch emotes have been loaded
     * 
     * @return twitchLoaded
     */
    public Boolean isTwitchLoaded()
    {
        return twitchLoaded;
    }

    /**
     * Set whether the Twitch emotes have been loaded
     * 
     * @param twitchLoaded
     */
    public void setTwitchLoaded(Boolean twitchLoaded)
    {
        this.twitchLoaded = twitchLoaded;
    }

    /**
     * Get whether the FrankenFaceZ emotes have been loaded
     * 
     * @return ffzLoaded
     */
    public Boolean isFfzLoaded()
    {
        return ffzLoaded;
    }

    /**
     * Set whether the FrankenFaceZ emotes have been loaded
     * 
     * @param ffzLoaded
     */
    public void setFfzLoaded(Boolean ffzLoaded)
    {
        this.ffzLoaded = ffzLoaded;
    }

}
