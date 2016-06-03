package com.glitchcog.fontificator.emoji.loader.betterttv;

/**
 * Better TTV Emote object returned by the Better TTV API
 */
public class BttvEmote
{
    private String id;

    private String channel;

    private String code;

    private String imageType;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
    }
}
