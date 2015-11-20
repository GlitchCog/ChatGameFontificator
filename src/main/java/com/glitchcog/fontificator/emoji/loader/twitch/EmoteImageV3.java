package com.glitchcog.fontificator.emoji.loader.twitch;

/**
 * Maps to the V3 JSON data for Twitch emote images
 * 
 * @author Matt Yanos
 */
public class EmoteImageV3
{
    /**
     * The pixel width of the Twitch emote image
     */
    private int width;

    /**
     * The pixel height of the Twitch emote image
     */
    private int height;

    /**
     * The URL of the Twitch emote image
     */
    private String url;

    /**
     * The emoticon set the image belongs to. (This information would make more sense to store with the regex, but
     * Twitch did not do that)
     */
    private Integer emoticon_set;

    /**
     * Get the pixel width of the Twitch emote image
     * 
     * @return width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Set the pixel width of the Twitch emote image
     * 
     * @param width
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * Get the pixel height of the Twitch emote image
     * 
     * @return height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Set the pixel height of the Twitch emote image
     * 
     * @param height
     */
    public void setHeight(int height)
    {
        this.height = height;
    }

    /**
     * Get the URL of the Twitch emote image
     * 
     * @return url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Set the URL of the Tiwtch emote image
     * 
     * @param url
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Get the emoticon set the image belongs to
     * 
     * @return emoticon_set
     */
    public Integer getEmoticon_set()
    {
        return emoticon_set;
    }

    /**
     * Set the emoticon set the image belongs to
     * 
     * @param emoticon_set
     */
    public void setEmoticon_set(Integer emoticon_set)
    {
        this.emoticon_set = emoticon_set;
    }
}
