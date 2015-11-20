package com.glitchcog.fontificator.config;

import java.util.Properties;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.emoji.EmojiOperation;
import com.glitchcog.fontificator.emoji.EmojiType;

/**
 * The configuration for the Emoji and Badges
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
     * Whether badges are enabled or not
     */
    private Boolean badgesEnabled;

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
     * The strategy for drawing an emoji when the emoji key word and image URL are loaded, but the image itself is not
     */
    private EmojiLoadingDisplayStragegy displayStrategy;

    /**
     * Whether Twitch emotes are enabled or not
     */
    private Boolean twitchEnabled;

    /**
     * Whether Twitch global emotes are to be cached or not
     */
    private Boolean twitchCacheEnabled;

    /**
     * Whether FrankerFaceZ emotes are enabled or not
     */
    private Boolean ffzEnabled;

    /**
     * Whether FrankerFaceZ emotes are to be cached or not
     */
    private Boolean ffzCacheEnabled;

    /**
     * The loaded Twitch badges channel, or null if none is loaded
     */
    private String twitchBadgesLoadedChannel;

    /**
     * Whether the Twitch emotes have been loaded
     */
    private Boolean twitchLoaded;

    /**
     * Whether Twitch global emotes are cached or not
     */
    private Boolean twitchCached;

    /**
     * The loaded FrankerFaceZ channel, or null if none is loaded
     */
    private String ffzLoadedChannel;

    /**
     * Whether FrankerFaceZ global and channel specific emotes have been cached
     */
    private Boolean ffzCached;

    public ConfigEmoji()
    {
        twitchBadgesLoadedChannel = null;
        twitchLoaded = false;
        ffzLoadedChannel = null;
        twitchCached = false;
        ffzCached = false;
    }

    @Override
    public void reset()
    {
        emojiEnabled = null;
        badgesEnabled = null;
        scaleToLine = null;
        scale = null;
        displayStrategy = null;
        twitchEnabled = null;
        twitchCacheEnabled = null;
        ffzEnabled = null;
        ffzCacheEnabled = null;
    }

    public boolean isEmojiEnabled()
    {
        return emojiEnabled;
    }

    public void setEmojiEnabled(Boolean emojiEnabled)
    {
        this.emojiEnabled = emojiEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_ENABLED, Boolean.toString(emojiEnabled));
    }

    public boolean isBadgesEnabled()
    {
        return badgesEnabled;
    }

    public void setBadgesEnabled(Boolean badgesEnabled)
    {
        this.badgesEnabled = badgesEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_BADGES, Boolean.toString(badgesEnabled));
    }

    public boolean isScaleToLine()
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

    public EmojiLoadingDisplayStragegy getDisplayStrategy()
    {
        return displayStrategy;
    }

    public void setDisplayStrategy(EmojiLoadingDisplayStragegy displayStrategy)
    {
        this.displayStrategy = displayStrategy;
        props.setProperty(FontificatorProperties.KEY_EMOJI_DISPLAY_STRAT, displayStrategy.name());
    }

    public boolean isTwitchEnabled()
    {
        return twitchEnabled;
    }

    public void setTwitchEnabled(Boolean twitchEnabled)
    {
        this.twitchEnabled = twitchEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE, Boolean.toString(twitchEnabled));
    }

    public boolean isTwitchCacheEnabled()
    {
        return twitchCacheEnabled;
    }

    public void setTwitchCacheEnabled(Boolean twitchCacheEnabled)
    {
        this.twitchCacheEnabled = twitchCacheEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_TWITCH_CACHE, Boolean.toString(twitchCacheEnabled));
    }

    public boolean isFfzEnabled()
    {
        return ffzEnabled;
    }

    public void setFfzEnabled(Boolean ffzEnabled)
    {
        this.ffzEnabled = ffzEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_FFZ_ENABLE, Boolean.toString(ffzEnabled));
    }

    public boolean isFfzCacheEnabled()
    {
        return ffzCacheEnabled;
    }

    public void setFfzCacheEnabled(Boolean ffzCacheEnabled)
    {
        this.ffzCacheEnabled = ffzCacheEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_FFZ_CACHE, Boolean.toString(ffzCacheEnabled));
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
            case FRANKERFACEZ_CHANNEL:
                return ffzEnabled != null && ffzEnabled && ffzLoadedChannel != null;
            case TWITCH_V2:
            case TWITCH_V3:
                return twitchEnabled != null && twitchEnabled && twitchLoaded != null && twitchLoaded;
            case TWITCH_BADGE:
                return badgesEnabled != null && badgesEnabled && twitchBadgesLoadedChannel != null;
            default:
                // If it doesn't have a coded EmojiType, then we don't know it
                return false;
            }
        }
    }

    public LoadConfigReport validateStrings(LoadConfigReport report, String enabledBool, String scaleEnabledBool, String scale, String displayStrat, String twitchBool, String twitchCacheBool, String ffzBool, String ffzCacheBool)
    {
        validateBooleanStrings(report, enabledBool, scaleEnabledBool, twitchBool, twitchCacheBool, ffzBool, ffzCacheBool);
        validateIntegerWithLimitString(FontificatorProperties.KEY_EMOJI_SCALE, scale, MIN_SCALE, MAX_SCALE, report);

        return report;
    }

    @Override
    public LoadConfigReport load(Properties props, LoadConfigReport report)
    {
        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.EMOJI_KEYS, report);

        if (report.isErrorFree())
        {
            final String enabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_ENABLED);

            final String scaleEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE);
            final String scaleStr = props.getProperty(FontificatorProperties.KEY_EMOJI_SCALE);
            final String displayStratStr = props.getProperty(FontificatorProperties.KEY_EMOJI_DISPLAY_STRAT);

            final String twitchEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE);
            final String twitchCacheStr = props.getProperty(FontificatorProperties.KEY_EMOJI_TWITCH_CACHE);

            final String ffzEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_FFZ_ENABLE);
            final String ffzCacheStr = props.getProperty(FontificatorProperties.KEY_EMOJI_FFZ_CACHE);

            // Check that the values are valid
            validateStrings(report, enabledStr, scaleEnabledStr, scaleStr, displayStratStr, twitchEnabledStr, twitchCacheStr, ffzEnabledStr, ffzCacheStr);

            // Fill the values
            if (report.isErrorFree())
            {
                emojiEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_ENABLED, report);
                badgesEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_BADGES, report);
                scaleToLine = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE, report);
                scale = evaluateIntegerString(props, FontificatorProperties.KEY_EMOJI_SCALE, report);
                displayStrategy = EmojiLoadingDisplayStragegy.valueOf(displayStratStr);
                twitchEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE, report);
                twitchCacheEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_TWITCH_CACHE, report);
                ffzEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_FFZ_ENABLE, report);
                ffzCacheEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_FFZ_CACHE, report);
            }
        }

        return report;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displayStrategy == null) ? 0 : displayStrategy.hashCode());
        result = prime * result + ((emojiEnabled == null) ? 0 : emojiEnabled.hashCode());
        result = prime * result + ((badgesEnabled == null) ? 0 : badgesEnabled.hashCode());
        result = prime * result + ((ffzEnabled == null) ? 0 : ffzEnabled.hashCode());
        result = prime * result + ((ffzLoadedChannel == null) ? 0 : ffzLoadedChannel.hashCode());
        result = prime * result + ((scale == null) ? 0 : scale.hashCode());
        result = prime * result + ((scaleToLine == null) ? 0 : scaleToLine.hashCode());
        result = prime * result + ((twitchEnabled == null) ? 0 : twitchEnabled.hashCode());
        result = prime * result + ((twitchLoaded == null) ? 0 : twitchLoaded.hashCode());
        result = prime * result + ((twitchBadgesLoadedChannel == null) ? 0 : twitchBadgesLoadedChannel.hashCode());
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
        if (badgesEnabled == null)
        {
            if (other.badgesEnabled != null)
            {
                return false;
            }
        }
        else if (!badgesEnabled.equals(other.badgesEnabled))
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
        if (ffzLoadedChannel == null)
        {
            if (other.ffzLoadedChannel != null)
            {
                return false;
            }
        }
        else if (!ffzLoadedChannel.equals(other.ffzLoadedChannel))
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
        if (twitchBadgesLoadedChannel == null)
        {
            if (other.twitchBadgesLoadedChannel != null)
            {
                return false;
            }
        }
        else if (!twitchBadgesLoadedChannel.equals(other.twitchBadgesLoadedChannel))
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
        this.badgesEnabled = copy.badgesEnabled;
        this.scaleToLine = copy.scaleToLine;
        this.scale = copy.scale;
        this.displayStrategy = copy.displayStrategy;
        this.twitchEnabled = copy.twitchEnabled;
        this.ffzEnabled = copy.ffzEnabled;
        this.twitchLoaded = copy.twitchLoaded;
        this.twitchBadgesLoadedChannel = copy.twitchBadgesLoadedChannel;
        this.ffzLoadedChannel = copy.ffzLoadedChannel;
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
     * Get whether the global Twitch emotes have been cached
     * 
     * @return twitchCached
     */
    public Boolean isTwitchCached()
    {
        return twitchCached;
    }

    /**
     * Set whether the global Twitch emotes have been cached
     * 
     * @param twitchCached
     */
    public void setTwitchCached(Boolean twitchCached)
    {
        this.twitchCached = twitchCached;
    }

    /**
     * Get whether the Twitch badges have been loaded for the specified channel
     * 
     * @return loaded
     */
    public boolean isTwitchBadgesLoaded(String testChannel)
    {
        return twitchBadgesLoadedChannel != null && twitchBadgesLoadedChannel.equals(testChannel);
    }

    /**
     * Set whether the FrankerFaceZ emotes have been loaded
     * 
     * @param ffzLoadedChannel
     *            from which the FrankerFaceZ emotes are loaded
     */
    public void setTwitchBadgesLoaded(String twitchBadgesLoadedChannel)
    {
        this.twitchBadgesLoadedChannel = twitchBadgesLoadedChannel;
    }

    /**
     * Get whether the FrankerFaceZ emotes have been loaded for the specified channel
     * 
     * @return loaded
     */
    public boolean isFfzLoaded(String testChannel)
    {
        return ffzLoadedChannel != null && ffzLoadedChannel.equals(testChannel);
    }

    /**
     * Set whether the FrankerFaceZ emotes have been loaded
     * 
     * @param ffzLoadedChannel
     *            from which the FrankerFaceZ emotes are loaded
     */
    public void setFfzLoaded(String ffzLoadedChannel)
    {
        this.ffzLoadedChannel = ffzLoadedChannel;
    }

    /**
     * Get whether the FrankerFaceZ emotes have been cached
     * 
     * @return ffzCached
     */
    public boolean isFfzCached()
    {
        return ffzCached;
    }

    /**
     * Set whether the FrankerFaceZ emotes have been cached
     * 
     * @param ffzCached
     */
    public void setFfzCached(Boolean ffzCached)
    {
        this.ffzCached = ffzCached;
    }

    /**
     * Mark work as being completed by setting the loaded and cached member variables of this emoji config
     * 
     * @param emojiType
     *            The type of emoji on which the operation was done
     * @param emojiOp
     *            The operation done, loading or caching
     * @param channel
     *            The channel the work was done for, only used for FrankerFazeZ emotes
     */
    public void setWorkCompleted(EmojiType emojiType, EmojiOperation emojiOp, String channel)
    {
        if (emojiType.isTwitchEmote())
        {
            if (EmojiOperation.LOAD == emojiOp)
            {
                this.twitchLoaded = true;
            }
            else if (EmojiOperation.CACHE == emojiOp)
            {
                this.twitchCached = true;
            }
        }
        else if (EmojiType.TWITCH_BADGE.equals(emojiType))
        {
            this.twitchBadgesLoadedChannel = channel;
        }
        else if (emojiType.isFrankerFaceZEmote())
        {
            if (EmojiOperation.LOAD == emojiOp)
            {
                this.ffzLoadedChannel = channel;
            }
            else if (EmojiOperation.CACHE == emojiOp)
            {
                this.ffzCached = true;
            }
        }
    }

    public void resetWorkCompleted()
    {
        this.twitchLoaded = false;
        this.twitchCached = false;
        this.ffzLoadedChannel = null;
        this.ffzCached = null;
    }
}
