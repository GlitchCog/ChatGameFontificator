package com.glitchcog.fontificator.config;

import java.awt.Color;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.emoji.EmojiJob;
import com.glitchcog.fontificator.emoji.EmojiOperation;
import com.glitchcog.fontificator.emoji.EmojiType;

/**
 * The configuration for the Emoji and Badges
 * 
 * @author Matt Yanos
 */
public class ConfigEmoji extends Config
{
    private static final Logger logger = Logger.getLogger(ConfigEmoji.class);

    public static final int MIN_SCALE = 10;
    public static final int MAX_SCALE = 500;

    public static final int MIN_BADGE_OFFSET = -32;
    public static final int MAX_BADGE_OFFSET = 64;

    /**
     * The green of moderator badges, used for FrankerFaceZ badge coloration
     */
    public static final Color MOD_BADGE_COLOR = new Color(0x34AE0A);

    /**
     * Whether all emoji are enabled or not
     */
    private Boolean emojiEnabled;

    /**
     * Whether Twitch badges are enabled or not
     */
    private Boolean twitchBadgesEnabled;

    /**
     * Whether FrankerFaceZ badges are enabled or not
     */
    private Boolean ffzBadgesEnabled;

    /**
     * Whether the emoji scale value is used to modify the size of the emoji relative to the line height, or the
     * original emoji image size
     */
    private Boolean emojiScaleToLine;

    /**
     * Whether the badge scale value is used to modify the size of the badge relative to the line height, or the
     * original badge image size
     */
    private Boolean badgeScaleToLine;

    /**
     * This is the number of pixels to offset the badges vertically to accommodate the fact that sprite fonts don't have
     * a baseline to determine where the vertical center of the text is appropriately
     */
    private Integer badgeHeightOffset;

    /**
     * The scale value to be applied to the emoji
     */
    private Integer emojiScale;

    /**
     * The scale value to be applied to the badge
     */
    private Integer badgeScale;

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
     * The loaded FrankerFaceZ badges channel, or null if none is loaded
     */
    private String ffzBadgesLoadedChannel;

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
     * Whether the FrankerFaceZ global emotes are loaded
     */
    private Boolean ffzGlobalLoaded;

    /**
     * Whether FrankerFaceZ global and channel specific emotes have been cached
     */
    private Boolean ffzCached;

    /**
     * Whether BetterTTV emotes are enabled or not
     */
    private Boolean bttvEnabled;

    /**
     * Whether BetterTTV emotes are to be cached or not
     */
    private Boolean bttvCacheEnabled;

    /**
     * The loaded BetterTTV channel, or null if none is loaded
     */
    private String bttvLoadedChannel;

    /**
     * Whether the BetterTTV global emotes are loaded
     */
    private Boolean bttvGlobalLoaded;

    /**
     * Whether BetterTTV global and channel specific emotes have been cached
     */
    private Boolean bttvCached;

    public ConfigEmoji()
    {
        resetWorkCompleted();
    }

    @Override
    public void reset()
    {
        emojiEnabled = null;
        twitchBadgesEnabled = null;
        ffzBadgesEnabled = null;
        emojiScaleToLine = null;
        badgeScaleToLine = null;
        badgeHeightOffset = null;
        emojiScale = null;
        badgeScale = null;
        displayStrategy = null;
        twitchEnabled = null;
        twitchCacheEnabled = null;
        ffzEnabled = null;
        ffzCacheEnabled = null;
        bttvEnabled = null;
        bttvCacheEnabled = null;
        bttvLoadedChannel = null;
        bttvGlobalLoaded = null;
        bttvCached = null;
    }

    public boolean isEmojiEnabled()
    {
        return emojiEnabled != null && emojiEnabled;
    }

    public void setEmojiEnabled(Boolean emojiEnabled)
    {
        this.emojiEnabled = emojiEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_ENABLED, Boolean.toString(emojiEnabled));
    }

    public boolean isTwitchBadgesEnabled()
    {
        return twitchBadgesEnabled != null && twitchBadgesEnabled;
    }

    public void setTwitchBadgesEnabled(Boolean twitchBadgesEnabled)
    {
        this.twitchBadgesEnabled = twitchBadgesEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_TWITCH_BADGES, Boolean.toString(twitchBadgesEnabled));
    }

    public boolean isFfzBadgesEnabled()
    {
        return ffzBadgesEnabled != null && ffzBadgesEnabled;
    }

    public void setFfzBadgesEnabled(Boolean ffzBadgesEnabled)
    {
        this.ffzBadgesEnabled = ffzBadgesEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_FFZ_BADGES, Boolean.toString(ffzBadgesEnabled));
    }

    /**
     * Get whether badges of any type enabled
     * 
     * @return ffz or twitch badges enabled
     */
    public boolean isAnyBadgesEnabled()
    {
        return isFfzBadgesEnabled() || isTwitchBadgesEnabled();
    }

    public boolean isEmojiScaleToLine()
    {
        return emojiScaleToLine != null && emojiScaleToLine;
    }

    public void setEmojiScaleToLine(Boolean emojiScaleToLine)
    {
        this.emojiScaleToLine = emojiScaleToLine;
        props.setProperty(FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE, Boolean.toString(emojiScaleToLine));
    }

    public boolean isBadgeScaleToLine()
    {
        return badgeScaleToLine != null && badgeScaleToLine;
    }

    public void setBadgeScaleToLine(Boolean badgeScaleToLine)
    {
        this.badgeScaleToLine = badgeScaleToLine;
        props.setProperty(FontificatorProperties.KEY_EMOJI_BADGE_SCALE_TO_LINE, Boolean.toString(badgeScaleToLine));
    }

    public int getBadgeHeightOffset()
    {
        return badgeHeightOffset;
    }

    public void setBadgeHeightOffset(int badgeHeightOffset)
    {
        this.badgeHeightOffset = badgeHeightOffset;
        props.setProperty(FontificatorProperties.KEY_EMOJI_BADGE_HEIGHT_OFFSET, Integer.toString(badgeHeightOffset));
    }

    public Integer getEmojiScale()
    {
        return emojiScale;
    }

    public void setEmojiScale(Integer emojiScale)
    {
        this.emojiScale = emojiScale;
        props.setProperty(FontificatorProperties.KEY_EMOJI_SCALE, Integer.toString(emojiScale));
    }

    public Integer getBadgeScale()
    {
        return badgeScale;
    }

    public void setBadgeScale(Integer badgeScale)
    {
        this.badgeScale = badgeScale;
        props.setProperty(FontificatorProperties.KEY_EMOJI_BADGE_SCALE, Integer.toString(badgeScale));
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
        return twitchEnabled != null && twitchEnabled;
    }

    public void setTwitchEnabled(Boolean twitchEnabled)
    {
        this.twitchEnabled = twitchEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE, Boolean.toString(twitchEnabled));
    }

    public boolean isTwitchCacheEnabled()
    {
        return twitchCacheEnabled != null && twitchCacheEnabled;
    }

    public void setTwitchCacheEnabled(Boolean twitchCacheEnabled)
    {
        this.twitchCacheEnabled = twitchCacheEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_TWITCH_CACHE, Boolean.toString(twitchCacheEnabled));
    }

    public boolean isFfzEnabled()
    {
        return ffzEnabled != null && ffzEnabled;
    }

    public void setFfzEnabled(Boolean ffzEnabled)
    {
        this.ffzEnabled = ffzEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_FFZ_ENABLE, Boolean.toString(ffzEnabled));
    }

    public boolean isFfzCacheEnabled()
    {
        return ffzCacheEnabled != null && ffzCacheEnabled;
    }

    public void setFfzCacheEnabled(Boolean ffzCacheEnabled)
    {
        this.ffzCacheEnabled = ffzCacheEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_FFZ_CACHE, Boolean.toString(ffzCacheEnabled));
    }

    public Boolean isBttvEnabled()
    {
        return bttvEnabled != null && bttvEnabled;
    }

    public Boolean isBttvCacheEnabled()
    {
        return bttvCacheEnabled != null && bttvCacheEnabled;
    }

    public void setBttvCacheEnabled(Boolean bttvCacheEnabled)
    {
        this.bttvCacheEnabled = bttvCacheEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_BTTV_CACHE, Boolean.toString(bttvCacheEnabled));
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
            case FRANKERFACEZ_GLOBAL:
                return ffzEnabled != null && ffzEnabled && ffzGlobalLoaded != null && ffzGlobalLoaded;
            case FRANKERFACEZ_BADGE:
                return ffzBadgesEnabled != null && ffzBadgesEnabled && ffzBadgesLoadedChannel != null;
            case BETTER_TTV_CHANNEL:
                return bttvEnabled != null && bttvEnabled && bttvLoadedChannel != null;
            case BETTER_TTV_GLOBAL:
                return bttvEnabled != null && bttvEnabled && bttvGlobalLoaded != null && bttvGlobalLoaded;
            // case TWITCH_V2:
            // case TWITCH_V3:
            //     // Only V2 and V3. Chat V1 doesn't use the normal EmojiTypeMap, so it doesn't need to be checked. They're loaded on the fly.
            //     return ControlPanelEmoji.TWITCH_EMOTE_VERSION.equals(type) && twitchEnabled != null && twitchEnabled && twitchLoaded != null && twitchLoaded;
            case TWITCH_BADGE:
                return twitchBadgesEnabled != null && twitchBadgesEnabled && twitchBadgesLoadedChannel != null;
            default:
                // If it doesn't have a coded EmojiType, then we don't know it
                return false;
            }
        }
    }

    public LoadConfigReport validateStrings(LoadConfigReport report, String enabledBool, String badgeTwitchBool, String badgeFfzBool, String scaleEnabledBool, String scaleBadgeEnabledBool, String badgeHeightOffsetStr, String scale, String scaleBadge, String displayStrat, String twitchBool, String twitchCacheBool, String ffzBool, String ffzCacheBool, String bttvBool, String bttvCacheBool)
    {
        validateBooleanStrings(report, enabledBool, badgeTwitchBool, badgeFfzBool, scaleEnabledBool, scaleBadgeEnabledBool, twitchBool, twitchCacheBool, ffzBool, ffzCacheBool, bttvBool, bttvCacheBool);
        validateIntegerWithLimitString(FontificatorProperties.KEY_EMOJI_SCALE, scale, MIN_SCALE, MAX_SCALE, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_EMOJI_BADGE_SCALE, scaleBadge, MIN_SCALE, MAX_SCALE, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_EMOJI_BADGE_HEIGHT_OFFSET, badgeHeightOffsetStr, MIN_BADGE_OFFSET, MAX_BADGE_OFFSET, report);
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

            final String twitchBadgeStr = props.getProperty(FontificatorProperties.KEY_EMOJI_TWITCH_BADGES);
            final String ffzBadgeStr = props.getProperty(FontificatorProperties.KEY_EMOJI_FFZ_BADGES);

            final String scaleEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE);
            final String scaleBadgeEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_BADGE_SCALE_TO_LINE);
            final String badgeHeightOffsetStr = props.getProperty(FontificatorProperties.KEY_EMOJI_BADGE_HEIGHT_OFFSET);
            final String scaleStr = props.getProperty(FontificatorProperties.KEY_EMOJI_SCALE);
            final String scaleBadgeStr = props.getProperty(FontificatorProperties.KEY_EMOJI_BADGE_SCALE);
            final String displayStratStr = props.getProperty(FontificatorProperties.KEY_EMOJI_DISPLAY_STRAT);

            final String twitchEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE);
            final String twitchCacheStr = props.getProperty(FontificatorProperties.KEY_EMOJI_TWITCH_CACHE);

            final String ffzEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_FFZ_ENABLE);
            final String ffzCacheStr = props.getProperty(FontificatorProperties.KEY_EMOJI_FFZ_CACHE);

            final String bttvEnabledStr = props.getProperty(FontificatorProperties.KEY_EMOJI_BTTV_ENABLE);
            final String bttvCacheStr = props.getProperty(FontificatorProperties.KEY_EMOJI_BTTV_CACHE);

            // Check that the values are valid
            validateStrings(report, enabledStr, twitchBadgeStr, ffzBadgeStr, scaleEnabledStr, scaleBadgeEnabledStr, badgeHeightOffsetStr, scaleStr, scaleBadgeStr, displayStratStr, twitchEnabledStr, twitchCacheStr, ffzEnabledStr, ffzCacheStr, bttvEnabledStr, bttvCacheStr);

            // Fill the values
            if (report.isErrorFree())
            {
                emojiEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_ENABLED, report);
                twitchBadgesEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_TWITCH_BADGES, report);
                ffzBadgesEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_FFZ_BADGES, report);
                emojiScaleToLine = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_SCALE_TO_LINE, report);
                badgeScaleToLine = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_BADGE_SCALE_TO_LINE, report);
                badgeHeightOffset = evaluateIntegerString(props, FontificatorProperties.KEY_EMOJI_BADGE_HEIGHT_OFFSET, report);
                emojiScale = evaluateIntegerString(props, FontificatorProperties.KEY_EMOJI_SCALE, report);
                badgeScale = evaluateIntegerString(props, FontificatorProperties.KEY_EMOJI_BADGE_SCALE, report);
                displayStrategy = EmojiLoadingDisplayStragegy.valueOf(displayStratStr);
                twitchEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_TWITCH_ENABLE, report);
                twitchCacheEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_TWITCH_CACHE, report);
                ffzEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_FFZ_ENABLE, report);
                ffzCacheEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_FFZ_CACHE, report);
                bttvEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_BTTV_ENABLE, report);
                bttvCacheEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_EMOJI_BTTV_CACHE, report);
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
        result = prime * result + ((twitchBadgesEnabled == null) ? 0 : twitchBadgesEnabled.hashCode());
        result = prime * result + ((ffzBadgesEnabled == null) ? 0 : ffzBadgesEnabled.hashCode());
        result = prime * result + ((ffzEnabled == null) ? 0 : ffzEnabled.hashCode());
        result = prime * result + ((ffzLoadedChannel == null) ? 0 : ffzLoadedChannel.hashCode());
        result = prime * result + ((ffzGlobalLoaded == null) ? 0 : ffzGlobalLoaded.hashCode());
        result = prime * result + ((bttvEnabled == null) ? 0 : bttvEnabled.hashCode());
        result = prime * result + ((bttvLoadedChannel == null) ? 0 : bttvLoadedChannel.hashCode());
        result = prime * result + ((bttvGlobalLoaded == null) ? 0 : bttvGlobalLoaded.hashCode());
        result = prime * result + ((emojiScale == null) ? 0 : emojiScale.hashCode());
        result = prime * result + ((emojiScaleToLine == null) ? 0 : emojiScaleToLine.hashCode());
        result = prime * result + ((badgeScale == null) ? 0 : badgeScale.hashCode());
        result = prime * result + ((badgeScaleToLine == null) ? 0 : badgeScaleToLine.hashCode());
        result = prime * result + ((badgeHeightOffset == null) ? 0 : badgeHeightOffset.hashCode());
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
        if (twitchBadgesEnabled == null)
        {
            if (other.twitchBadgesEnabled != null)
            {
                return false;
            }
        }
        else if (!twitchBadgesEnabled.equals(other.twitchBadgesEnabled))
        {
            return false;
        }
        if (ffzBadgesEnabled == null)
        {
            if (other.ffzBadgesEnabled != null)
            {
                return false;
            }
        }
        else if (!ffzBadgesEnabled.equals(other.ffzBadgesEnabled))
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
        if (ffzGlobalLoaded == null)
        {
            if (other.ffzGlobalLoaded != null)
            {
                return false;
            }
        }
        else if (!ffzGlobalLoaded.equals(other.ffzGlobalLoaded))
        {
            return false;
        }
        if (bttvEnabled == null)
        {
            if (other.bttvEnabled != null)
            {
                return false;
            }
        }
        else if (!bttvEnabled.equals(other.bttvEnabled))
        {
            return false;
        }
        if (bttvLoadedChannel == null)
        {
            if (other.bttvLoadedChannel != null)
            {
                return false;
            }
        }
        else if (!bttvLoadedChannel.equals(other.bttvLoadedChannel))
        {
            return false;
        }
        if (bttvGlobalLoaded == null)
        {
            if (other.bttvGlobalLoaded != null)
            {
                return false;
            }
        }
        else if (!bttvGlobalLoaded.equals(other.bttvGlobalLoaded))
        {
            return false;
        }
        if (emojiScale == null)
        {
            if (other.emojiScale != null)
            {
                return false;
            }
        }
        else if (!emojiScale.equals(other.emojiScale))
        {
            return false;
        }
        if (badgeScale == null)
        {
            if (other.badgeScale != null)
            {
                return false;
            }
        }
        else if (!badgeScale.equals(other.badgeScale))
        {
            return false;
        }
        if (emojiScaleToLine == null)
        {
            if (other.emojiScaleToLine != null)
            {
                return false;
            }
        }
        else if (!emojiScaleToLine.equals(other.emojiScaleToLine))
        {
            return false;
        }
        if (badgeScaleToLine == null)
        {
            if (other.badgeScaleToLine != null)
            {
                return false;
            }
        }
        else if (!badgeScaleToLine.equals(other.badgeScaleToLine))
        {
            return false;
        }
        if (badgeHeightOffset == null)
        {
            if (other.badgeHeightOffset != null)
            {
                return false;
            }
        }
        else if (!badgeHeightOffset.equals(other.badgeHeightOffset))
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
        this.twitchBadgesEnabled = copy.twitchBadgesEnabled;
        this.ffzBadgesEnabled = copy.ffzBadgesEnabled;
        this.emojiScaleToLine = copy.emojiScaleToLine;
        this.emojiScale = copy.emojiScale;
        this.badgeScaleToLine = copy.badgeScaleToLine;
        this.badgeHeightOffset = copy.badgeHeightOffset;
        this.badgeScale = copy.badgeScale;
        this.displayStrategy = copy.displayStrategy;
        this.twitchEnabled = copy.twitchEnabled;
        this.ffzEnabled = copy.ffzEnabled;
        this.twitchLoaded = copy.twitchLoaded;
        this.twitchBadgesLoadedChannel = copy.twitchBadgesLoadedChannel;
        this.ffzLoadedChannel = copy.ffzLoadedChannel;
        this.ffzGlobalLoaded = copy.ffzGlobalLoaded;
        this.bttvEnabled = copy.bttvEnabled;
        this.bttvCacheEnabled = copy.bttvCacheEnabled;
        this.bttvLoadedChannel = copy.bttvLoadedChannel;
        this.bttvGlobalLoaded = copy.bttvGlobalLoaded;
        this.bttvCached = copy.bttvCached;
    }

    /**
     * Get whether the Twitch emotes have been loaded
     * 
     * @return twitchLoaded
     */
    public Boolean isTwitchLoaded()
    {
        return twitchLoaded != null && twitchLoaded;
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
        return twitchCached != null && twitchCached;
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
     * Get whether the FrankerFaceZ badges have been loaded for the specified channel
     * 
     * @return loaded
     */
    public boolean isFfzBadgesLoaded(String testChannel)
    {
        return ffzBadgesLoadedChannel != null && ffzBadgesLoadedChannel.equals(testChannel);
    }

    /**
     * Set whether the Twitch badges have been loaded
     * 
     * @param twitchLoadedChannel
     *            from which the Twitch emotes are loaded
     */
    public void setTwitchBadgesLoaded(String twitchBadgesLoadedChannel)
    {
        this.twitchBadgesLoadedChannel = twitchBadgesLoadedChannel;
    }

    /**
     * Set whether the FrankerFaceZ badges have been loaded
     * 
     * @param ffzLoadedChannel
     *            from which the FrankerFaceZ emotes are loaded
     */
    public void setFfzBadgesLoaded(String ffzBadgesLoadedChannel)
    {
        this.ffzBadgesLoadedChannel = ffzBadgesLoadedChannel;
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
     * Get whether the global FrankerFaceZ emotes have been loaded
     * 
     * @return ffzGlobalLoaded
     */
    public Boolean isFfzGlobalLoaded()
    {
        return ffzGlobalLoaded != null && ffzGlobalLoaded;
    }

    /**
     * Set whether the global FrankerFaceZ emotes have been loaded
     * 
     * @param ffzGlobalLoaded
     */
    public void setFfzGlobalLoaded(Boolean ffzGlobalLoaded)
    {
        this.ffzGlobalLoaded = ffzGlobalLoaded;
    }

    /**
     * Get whether the FrankerFaceZ emotes have been cached
     * 
     * @return ffzCached
     */
    public boolean isFfzCached()
    {
        return ffzCached != null && ffzCached;
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

    public void setBttvEnabled(Boolean bttvEnabled)
    {
        this.bttvEnabled = bttvEnabled;
        props.setProperty(FontificatorProperties.KEY_EMOJI_BTTV_ENABLE, Boolean.toString(bttvEnabled));
    }

    public String getBttvLoadedChannel()
    {
        return bttvLoadedChannel;
    }

    public void setBttvLoadedChannel(String bttvLoadedChannel)
    {
        this.bttvLoadedChannel = bttvLoadedChannel;
    }

    public void setBttvGlobalLoaded(Boolean bttvGlobalLoaded)
    {
        this.bttvGlobalLoaded = bttvGlobalLoaded;
    }

    public boolean isBttvLoaded(String testChannel)
    {
        return bttvLoadedChannel != null && bttvLoadedChannel.equals(testChannel);
    }

    public void setBttfLoaded(String bttvLoadedChannel)
    {
        this.bttvLoadedChannel = bttvLoadedChannel;
    }

    public Boolean isBttvGlobalLoaded()
    {
        return bttvGlobalLoaded != null && bttvGlobalLoaded;
    }

    public void setBttfGlobalLoaded(Boolean bttvGlobalLoaded)
    {
        this.bttvGlobalLoaded = bttvGlobalLoaded;
    }

    public boolean isBttvCached()
    {
        return bttvCached != null && bttvCached;
    }

    public void setBttvCached(Boolean bttvCached)
    {
        this.bttvCached = bttvCached;
    }

    /**
     * Mark work as being completed by setting the loaded and cached member variables of this emoji config
     * 
     * @param job
     *            Contains the type, op, and channel of the job completed
     */
    public void setWorkCompleted(EmojiJob job)
    {
        logger.trace(job.toString() + " completed");

        final EmojiType emojiType = job.getType();
        final EmojiOperation emojiOp = job.getOp();

        if (EmojiType.TWITCH_BADGE.equals(emojiType))
        {
            this.twitchBadgesLoadedChannel = job.getChannel();
        }
        else if (EmojiType.FRANKERFACEZ_BADGE.equals(emojiType))
        {
            this.ffzBadgesLoadedChannel = job.getChannel();
        }
        else if (emojiType.isTwitchEmote()) // This would also include TWITCH_BADGE, but it's already checked above
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
        else if (emojiType.isFrankerFaceZEmote())
        {
            if (EmojiOperation.LOAD == emojiOp)
            {
                if (emojiType == EmojiType.FRANKERFACEZ_CHANNEL)
                {
                    this.ffzLoadedChannel = job.getChannel();
                }
                else if (emojiType == EmojiType.FRANKERFACEZ_GLOBAL)
                {
                    this.ffzGlobalLoaded = true;
                }
            }
            else if (EmojiOperation.CACHE == emojiOp)
            {
                this.ffzCached = true;
            }
        }
        else if (emojiType.isBetterTtvEmote())
        {
            if (EmojiOperation.LOAD == emojiOp)
            {
                if (emojiType == EmojiType.BETTER_TTV_CHANNEL)
                {
                    this.bttvLoadedChannel = job.getChannel();
                }
                else if (emojiType == EmojiType.BETTER_TTV_GLOBAL)
                {
                    this.bttvGlobalLoaded = true;
                }
            }
            else if (EmojiOperation.CACHE == emojiOp)
            {
                this.bttvCached = true;
            }
        }
    }

    /**
     * Reset the flags indicating any completed work. This enables a reloading of everything. It does not clear out the
     * previously loaded or cached data, just enables the system to reload or recache it.
     */
    public void resetWorkCompleted()
    {
        this.twitchLoaded = false;
        this.twitchCached = false;
        this.twitchBadgesLoadedChannel = null;
        this.ffzBadgesLoadedChannel = null;
        this.ffzLoadedChannel = null;
        this.ffzGlobalLoaded = false;
        this.ffzCached = null;
        this.bttvLoadedChannel = null;
        this.bttvGlobalLoaded = false;
        this.bttvCached = null;
    }

    /**
     * Get whether any work been done to load or cache any emoji or badges
     * 
     * @return whether work was done
     */
    public boolean isAnyWorkDone()
    {
        // @formatter:off
        return twitchBadgesLoadedChannel != null || isTwitchLoaded() || isTwitchCached() || 
               ffzBadgesLoadedChannel != null || ffzLoadedChannel != null || isFfzGlobalLoaded() || isFfzCached() || 
               bttvLoadedChannel != null || isBttvGlobalLoaded() || isBttvCached();
        // @formatter:on
    }

}
