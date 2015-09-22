package com.glitchcog.fontificator.emoji;

public enum EmojiType
{
    TWITCH_V2("Twitch Emotes V2"), TWITCH_V3("Twitch Emotes V3"), FRANKERFACEZ("FrankerFaceZ Emotes");

    private final String description;

    private EmojiType(String description)
    {
        this.description = description;
    }

    public boolean isTwitch()
    {
        return this == TWITCH_V2 || this == TWITCH_V3;
    }

    public String getDescription()
    {
        return description;
    }
}