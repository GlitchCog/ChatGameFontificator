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
     * Construct an emoji manager object, instantiates the map of maps keyed off of all the possible emoji types
     */
    public EmojiManager()
    {
        allEmoji = new HashMap<EmojiType, TypedEmojiMap>();
        for (EmojiType type : EmojiType.values())
        {
            allEmoji.put(type, new TypedEmojiMap(type));
        }
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
     * Get the emoji, if the configuration allows for that type of emoji
     * 
     * @param testKey
     * @param config
     * @return emoji or null if not found or if configuration prohibits this emoji
     */
    public LazyLoadEmoji[] getEmoji(String testKey, ConfigEmoji config)
    {
        for (EmojiType type : allEmoji.keySet())
        {
            // If config is null, then just assume we want it. Used when caching.
            if (config == null || config.isTypeEnabledAndLoaded(type))
            {
                TypedEmojiMap typedEmoji = allEmoji.get(type);
                LazyLoadEmoji[] value = typedEmoji.getEmoji(testKey, config);
                if (value != null)
                {
                    return value;
                }
            }
        }
        return null;
    }

}
