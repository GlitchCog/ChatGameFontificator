package com.glitchcog.fontificator.emoji;

/**
 * Type of emoji or badges
 * 
 * @author Matt Yanos
 */
public enum EmojiType
{
    // @formatter:off
    TWITCH_V1("Twitch Emotes from Chat V1", EmojiGroup.TWITCH, false),
 
    TWITCH_BADGE("Twitch Badge", EmojiGroup.TWITCH, true), 
    FRANKERFACEZ_CHANNEL("FrankerFaceZ Emotes", EmojiGroup.FFZ, false), 
    FRANKERFACEZ_GLOBAL("FrankerFaceZ Global Emotes", EmojiGroup.FFZ, false), 
    FRANKERFACEZ_BADGE("FrankerFaceZ Badge", EmojiGroup.FFZ, true),
    FRANKERFACEZ_REPLACEMENT("FrankerFaceZ Replacement Emotes", EmojiGroup.FFZ, false),  
    BETTER_TTV_CHANNEL("Better Twitch TV Emotes", EmojiGroup.BTTV, false), 
    BETTER_TTV_GLOBAL("Better Twitch TV Global Emotes", EmojiGroup.BTTV, false), 
    TWITTER_EMOJI("Twitter Unicode Emotes", EmojiGroup.UNICODE, false);
    // @formatter:on

    private final EmojiGroup group;

    private final boolean badge;

    private final String description;

    /**
     * These are the types of emoji words to check against for manual messages.
     */
    public static EmojiType[] MANUAL_EMOJI_TYPES = new EmojiType[] { EmojiType.FRANKERFACEZ_CHANNEL, EmojiType.FRANKERFACEZ_GLOBAL, EmojiType.BETTER_TTV_CHANNEL, EmojiType.BETTER_TTV_GLOBAL, EmojiType.TWITTER_EMOJI };

    /**
     * These are the types of emoji words in messages are checked against to include. This is done to prevent things
     * like badges from accidentally being included. For example, FFZ badges are included in their maps using keywords
     * like "bot", which could easily be accidentally inserted into a message.
     */
    public static EmojiType[] THIRD_PARTY_EMOJI_TYPES = new EmojiType[] { EmojiType.FRANKERFACEZ_CHANNEL, EmojiType.FRANKERFACEZ_GLOBAL, EmojiType.BETTER_TTV_CHANNEL, EmojiType.BETTER_TTV_GLOBAL, EmojiType.TWITTER_EMOJI };

    private EmojiType(String description, EmojiGroup group, boolean badge)
    {
        this.group = group;
        this.description = description;
        this.badge = badge;
    }

    public boolean isTwitchEmote()
    {
        return group == EmojiGroup.TWITCH;
    }

    public boolean isFrankerFaceZEmote()
    {
        return group == EmojiGroup.FFZ;
    }

    public boolean isBetterTtvEmote()
    {
        return group == EmojiGroup.BTTV;
    }

    public boolean isBadge()
    {
        return badge;
    }

    public String getDescription()
    {
        return description;
    }
}