package com.glitchcog.fontificator.emoji.loader.twitch;

/**
 * This is the object that the Twitch emote API V3 returns a list of when a call to
 * https://api.twitch.tv/kraken/chat/emoticons is made. This class is unused, but exists if Twitch ever decides to turn
 * off the V2 emote API, which is a much better API than V3 because it's channel specific.
 * 
 * @author Matt Yanos
 */
public class TwitchEmoteV3
{
    /**
     * The regular expression that when matched is to have the emote image substituted in place of the matching text
     */
    private String regex;

    /**
     * The images that make up the emote
     */
    private EmoteImageV3[] images;

    /**
     * Get the regular expression that when matched is to have the emote image substituted in place of the matching
     * text.
     * 
     * @return regex
     */
    public String getRegex()
    {
        return regex;
    }

    /**
     * Set the regular expression that when matched is to have the emote image substituted in place of the matching text
     * 
     * @param regex
     */
    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    /**
     * Get the images that make up the emote
     * 
     * @return images
     */
    public EmoteImageV3[] getImages()
    {
        return images;
    }

    /**
     * Set the images that make up the emote
     * 
     * @param images
     */
    public void setImages(EmoteImageV3[] images)
    {
        this.images = images;
    }

    @Override
    public String toString()
    {
        String output = regex;
        if (images == null)
        {
            output += " [null emote images]";
        }
        else if (images.length == 0)
        {
            output += " [no emote images found]";
        }
        else
        {
            int repImageIndex = 0;
            output += " " + images.length + " image" + (images.length == 1 ? "" : "s") + " set: " + images[repImageIndex].getEmoticon_set() + " [" + images[repImageIndex].getWidth() + "x" + images[repImageIndex].getHeight() + "] " + images[repImageIndex].getUrl();
        }
        return output;
    }
}
