package com.glitchcog.fontificator.emoji.loader.twitch;

/**
 * Twitch badge model returned when accessing API at https://api.twitch.tv/kraken/chat/[user]/badges
 */
public class TwitchBadges
{
    private String svg;

    private String alpha;

    private String image;

    public String getSvg()
    {
        return svg;
    }

    public void setSvg(String svg)
    {
        this.svg = svg;
    }

    public String getAlpha()
    {
        return alpha;
    }

    public void setAlpha(String alpha)
    {
        this.alpha = alpha;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }
}
