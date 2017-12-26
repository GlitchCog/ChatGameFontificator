package com.glitchcog.fontificator.emoji;

/**
 * The source group for a type of emoji. This enum indicates the company or group that is the source for the emoji type,
 * which further divides the emoji to its type indicating things like version, whether it's a badge, global, associated
 * with a specific channel, or subscriber-only, etc...
 * 
 * @author Matt Yanos
 */
public enum EmojiGroup
{
    TWITCH("Twitch"), FFZ("FrankerFaceZ"), BTTV("Better TTV"), UNICODE("Unicode");

    private final String description;

    private EmojiGroup(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return description;
    }
}
