package com.glitchcog.fontificator.emoji;

/**
 * Type of emoji or badges
 * 
 * @author Matt Yanos
 */
public enum EmojiType
{
    // @formatter:off
    TWITCH_V1("Twitch Emotes V1", true, false, false, false), 
    TWITCH_V2("Twitch Emotes V2", true, false, false, false), 
    TWITCH_V3("Twitch Emotes V3", true, false, false, true), 
    TWITCH_BADGE("Twitch Badge", false, false, true, false), 
    FRANKERFACEZ_CHANNEL("FrankerFaceZ Emotes", false, true, false, false), 
    FRANKERFACEZ_GLOBAL("FrankerFaceZ Global Emotes", false, true, false, false), 
    FRANKERFACEZ_REPLACEMENT("FrankerFaceZ Replacement Emotes", false, true, false, false);
    // @formatter:on

    private final boolean badge;

    private final boolean twitchEmote;

    private final boolean ffzEmote;

    private final boolean loadSetMap;

    private final String description;

    private EmojiType(String description, boolean twitchEmote, boolean ffzEmote, boolean badge, boolean loadSetMap)
    {
        this.description = description;
        this.twitchEmote = twitchEmote;
        this.badge = badge;
        this.ffzEmote = ffzEmote;
        this.loadSetMap = loadSetMap;
    }

    public boolean isTwitchEmote()
    {
        return twitchEmote;
    }

    public boolean isFrankerFaceZEmote()
    {
        return ffzEmote;
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