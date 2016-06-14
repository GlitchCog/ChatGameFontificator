package com.glitchcog.fontificator.emoji;

/**
 * Type of emoji or badges
 * 
 * @author Matt Yanos
 */
public enum EmojiType
{
    // @formatter:off
    TWITCH_V1("Twitch Emotes V1", EmojiGroup.TWITCH, false, false), 
    TWITCH_V2("Twitch Emotes V2", EmojiGroup.TWITCH, false, false), 
    TWITCH_V3("Twitch Emotes V3", EmojiGroup.TWITCH, false, true), 
    TWITCH_BADGE("Twitch Badge", EmojiGroup.TWITCH, true, false), 
    FRANKERFACEZ_CHANNEL("FrankerFaceZ Emotes", EmojiGroup.FFZ, false, false), 
    FRANKERFACEZ_GLOBAL("FrankerFaceZ Global Emotes", EmojiGroup.FFZ, false, false), 
    FRANKERFACEZ_BADGE("FrankerFaceZ Badge", EmojiGroup.FFZ, true, false),
    FRANKERFACEZ_REPLACEMENT("FrankerFaceZ Replacement Emotes", EmojiGroup.FFZ, false, false), 
    BETTER_TTV_CHANNEL("Better Twitch TV Emotes", EmojiGroup.BTTV, false, false), 
    BETTER_TTV_GLOBAL("Better Twitch TV Global Emotes", EmojiGroup.BTTV, false, false); 
    // @formatter:on

    private final EmojiGroup group;

    private final boolean badge;

    private final boolean loadSetMap;

    private final String description;

    /**
     * These are the types of emoji words to check against for manual messages.
     */
    public static EmojiType[] MANUAL_EMOJI_TYPES = new EmojiType[] { EmojiType.TWITCH_V2, EmojiType.FRANKERFACEZ_CHANNEL, EmojiType.FRANKERFACEZ_GLOBAL, EmojiType.BETTER_TTV_CHANNEL, EmojiType.BETTER_TTV_GLOBAL };

    /**
     * These are the types of emoji words in messages are checked against to include. This is done to prevent things
     * like badges from accidentally being included. For example, FFZ badges are included in their maps using keywords
     * like "bot", which could easily be accidentally inserted into a message.
     */
    public static EmojiType[] THIRD_PARTY_EMOJI_TYPES = new EmojiType[] { EmojiType.FRANKERFACEZ_CHANNEL, EmojiType.FRANKERFACEZ_GLOBAL, EmojiType.BETTER_TTV_CHANNEL, EmojiType.BETTER_TTV_GLOBAL };

    private EmojiType(String description, EmojiGroup group, boolean badge, boolean loadSetMap)
    {
        this.group = group;
        this.description = description;
        this.badge = badge;
        this.loadSetMap = loadSetMap;
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

    public boolean isLoadSetMap()
    {
        return loadSetMap;
    }

    public String getDescription()
    {
        return description;
    }
}