package com.glitchcog.fontificator.emoji;

import java.awt.Color;

/**
 * Type of FrankerFaceZ badge
 * 
 * @author Matt Yanos
 */
public enum FfzBadgeType
{
    /**
     * Custom moderator badge that comes as a member of the Room object of the FFZ API JSON
     */
    MODERATOR("ffzmod", new Color(0x34AE0A), null),

    /**
     * Gear badge for bots
     */
    BOT("ffzbot", new Color(0x34AE0A), "https://cdn.frankerfacez.com/script/boticon.png"),

    /**
     * "FFZ Supporter" (Upsidedown faceless dog)
     */
    SUPPORTER("ffzdev", new Color(0x755000), "https://cdn.frankerfacez.com/script/devicon.png"),

    /**
     * Unused? (Upsidedown dog)
     */
    DONOR("ffzdonor", new Color(0x755000), "https://cdn.frankerfacez.com/script/donoricon.png");

    private final String key;

    private final Color color;

    private final String url;

    private FfzBadgeType(String key, Color color, String url)
    {
        this.key = key;
        this.color = color;
        this.url = url;
    }

    public String getKey()
    {
        return key;
    }

    public Color getColor()
    {
        return color;
    }

    public String getUrl()
    {
        return url;
    }
}