package com.glitchcog.fontificator.emoji.loader.twitch;

/**
 * This is the object that the Twitch emote API V2 returns a list of
 */
public class TwitchEmoteV2
{
    private int width;

    private int height;

    private String regex;

    private String state;

    private boolean subscriber_only;

    private String url;

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public String getRegex()
    {
        return regex;
    }

    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public boolean isSubscriber_only()
    {
        return subscriber_only;
    }

    public void setSubscriber_only(boolean subscriber_only)
    {
        this.subscriber_only = subscriber_only;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

}
