package com.glitchcog.fontificator.bot;

/**
 * A Twitch Emote ID and the indices for the keyword in the message
 * 
 * @author Matt Yanos
 */
public class EmoteAndIndices
{
    /**
     * The emote ID of the Twitch emote, from prepended IRC tags that are received with each Twitch post
     */
    private final Integer emoteId;

    /**
     * The starting index in the message of the keyword of this emote
     */
    private final int begin;

    /**
     * The ending index in the message of the keyword of this emote
     */
    private final int end;

    /**
     * @param setId
     *            The set ID of the Twitch version 3 emote for this emote. (This value is null for the global Twitch
     *            emotes)
     * @param begin
     *            The starting index in the message of the keyword of this emote
     * @param twitchEndIndex
     *            The ending index in the message of the keyword of this emote. Twitch defines this as the last index
     *            that represents the emote, which is 1 less than it should be for a Java substring, which is why it is
     *            set with +1.
     */
    public EmoteAndIndices(Integer setId, int begin, int twitchEndIndex)
    {
        this.emoteId = setId;
        this.begin = begin;
        this.end = twitchEndIndex + 1;
    }

    /**
     * Get the emote ID of the Twitch emote, from prepended IRC tags that are received with each Twitch post
     * 
     * @return emoteId
     */
    public Integer getEmoteId()
    {
        return emoteId;
    }

    /**
     * Get the starting index in the message of the keyword of this emote
     * 
     * @return begin
     */
    public int getBegin()
    {
        return begin;
    }

    /**
     * Get the ending index in the message of the keyword of this emote
     * 
     * @return end
     */
    public int getEnd()
    {
        return end;
    }

    @Override
    public String toString()
    {
        return emoteId + ":" + begin + "-" + end;
    }
}
