package com.glitchcog.fontificator.emoji;

import java.util.HashMap;
import java.util.Map;

import com.glitchcog.fontificator.config.ConfigEmoji;

/**
 * Handles accessing LazyLoadEmoji objects based on text keys to match emoji regular expressions
 * 
 * @author Matt Yanos
 */
public class EmojiManager
{
    /**
     * Holds all the loaded emoji
     */
    private Map<EmojiType, TypedEmojiMap> allEmoji;

    /**
     * Holds the same loaded emoji references, but accessible via emoji ID key, used for Twitch V3 emote access when the
     * Twitch emote ID is in the information prepended to each message
     */
    private Map<Integer, LazyLoadEmoji[]> emojiById;

    /**
     * Construct an emoji manager object, instantiates the map of maps keyed off of all the possible emoji types
     */
    public EmojiManager()
    {
        allEmoji = new HashMap<EmojiType, TypedEmojiMap>();
        for (EmojiType type : EmojiType.values())
        {
            allEmoji.put(type, new TypedEmojiMap(type));
        }
        emojiById = new HashMap<Integer, LazyLoadEmoji[]>();
    }

    /**
     * Get the map of only the specified type of emoji
     * 
     * @param type
     * @return
     */
    public TypedEmojiMap getEmojiByType(EmojiType type)
    {
        return allEmoji.get(type);
    }

    /**
     * Get the emoji
     * 
     * @param testKey
     * @return emoji or null if not found
     */
    public LazyLoadEmoji[] getEmoji(String testKey)
    {
        return getEmoji(testKey, null);
    }

    /**
     * Get the emoji of any loaded type, where the testKey is the typed word indicating the emoji to display, if the
     * configuration allows for that type of emoji
     * 
     * @param testKey
     *            The word keying the emoji
     * @param config
     *            The emoji configuration
     * @return emoji or null if not found or if configuration prohibits this emoji
     */
    public LazyLoadEmoji[] getEmoji(String testKey, ConfigEmoji config)
    {
        return getEmoji(EmojiType.values(), testKey, config);
    }

    /**
     * Get an emoji, of the given type, where the testKey is the typed word indicating the emoji to display, if the
     * configuration allows for that type of emoji
     * 
     * @param type
     *            Type of emoji to get
     * @param testKey
     *            The word keying the emoji
     * @param config
     *            The emoji configuration
     * @return
     */
    public LazyLoadEmoji[] getEmoji(EmojiType type, String testKey, ConfigEmoji config)
    {
        return getEmoji(new EmojiType[] { type }, testKey, config);
    }

    /**
     * Get an emoji, of the given types, where the testKey is the typed word indicating the emoji to display, if the
     * configuration allows for that type of emoji
     * 
     * @param types
     *            Types of emoji to get
     * @param testKey
     *            The word keying the emoji
     * @param config
     *            The emoji configuration
     * @return emoji or null if it's not found
     */
    public LazyLoadEmoji[] getEmoji(EmojiType[] types, String testKey, ConfigEmoji config)
    {
        LazyLoadEmoji[] emoji = null;
        // If config is null, then just assume we want it. Used when caching.
        for (EmojiType type : types)
        {
            if (config == null || config.isTypeEnabledAndLoaded(type))
            {
                TypedEmojiMap typedEmoji = allEmoji.get(type);
                if (typedEmoji != null)
                {
                    emoji = typedEmoji.getEmoji(testKey, config);
                    if (emoji != null)
                    {
                        return emoji;
                    }
                }
            }
        }
        return emoji;
    }

    /**
     * Add to the a map of all the Twitch V3 emoji keyed by emote ID, which is the number given in the prepended Twitch
     * IRC message tags
     * 
     * @return emojiById
     */
    public LazyLoadEmoji[] putEmojiById(Integer id, LazyLoadEmoji[] emoji)
    {
        return emojiById.put(id, emoji);
    }

    /**
     * Get the emoji, if the configuration allows for that type of emoji, based on the emote ID. This is the method to
     * call if you have the prepended data from a Twitch message that includes the Twitch emote set ID and the indicies
     * of the word indicating an emoji.
     * 
     * @param setId
     *            the key (note, null is a valid key, indicating the global Twitch emotes set)
     * @return TypedEmojiMap
     */
    public LazyLoadEmoji[] getEmojiById(Integer emojiId, ConfigEmoji config)
    {
        if (config != null && !config.isTypeEnabledAndLoaded(EmojiType.TWITCH_V3))
        {
            return null;
        }
        return emojiById.get(emojiId);
    }

}
