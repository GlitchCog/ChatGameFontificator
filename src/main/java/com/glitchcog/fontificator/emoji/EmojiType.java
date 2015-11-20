package com.glitchcog.fontificator.emoji;

/**
 * Type of emoji or badges
 * 
 * @author Matt Yanos
 */
public enum EmojiType
{
    TWITCH_V2("Twitch Emotes V2", true, false), 
    TWITCH_V3("Twitch Emotes V3", true, false), 
    TWITCH_BADGE("Twitch Badge", false, false), 
    FRANKERFACEZ_CHANNEL("FrankerFaceZ Emotes", false, true), 
    FRANKERFACEZ_GLOBAL("FrankerFaceZ Global Emotes", false, true);

    private boolean twitchEmote;

    private boolean ffzEmote;

    private final String description;

    private EmojiType(String description, boolean twitchEmote, boolean ffzEmote)
    {
        this.description = description;
        this.twitchEmote = twitchEmote;
        this.ffzEmote = ffzEmote;
    }

    public boolean isTwitchEmote()
    {
        return twitchEmote;
    }

    public boolean isFrankerFaceZEmote()
    {
        return ffzEmote;
    }

    public String getDescription()
    {
        return description;
    }
}