package com.glitchcog.fontificator.emoji.loader.twitch;

/**
 * This is the object that the Twitch returns a list of when a call to https://api.twitch.tv/kraken/chat/emoticons is
 * made. This will be converted (Strings cast to Integers to handle the "null" emoticon_set values) into the IdSetLink
 * model for use in the program.
 */
public class TwitchIdSetLink
{
    private String id;

    private String code;

    private String emoticon_set;

    private TwitchIdSetLink(String id, String code, String emoticon_set)
    {
        super();
        this.id = id;
        this.code = code;
        this.emoticon_set = emoticon_set;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getEmoticon_set()
    {
        return emoticon_set;
    }

    public void setEmoticon_set(String emoticon_set)
    {
        this.emoticon_set = emoticon_set;
    }

}
