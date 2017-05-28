package com.glitchcog.fontificator.emoji;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.glitchcog.fontificator.config.ConfigEmoji;

/**
 * Internally contains two maps for accessing Emoji based on just a word in a message:
 * <ul>
 * <li>one for storing emoji that are keyed off regular expressions (regexMap), and
 * <li>another for storing emoji that are keyed off just a word (normalMap)</li>
 * </ul>
 * This separation is to permit the speedier access of emoji keyed off of words before checking through all the regular
 * expression keys, which takes longer because each key must be accessed and compared as a regular expression.<br />
 * <br />
 * 
 * @author Matt Yanos
 */
public class TypedEmojiMap
{
    private final EmojiType type;

    private Map<String, LazyLoadEmoji> map;

    public TypedEmojiMap(EmojiType type)
    {
        this.type = type;
        map = new HashMap<String, LazyLoadEmoji>();
    }

    /**
     * Get the emoji (for FrankerFaceZ emotes that have an Integer ID)
     * 
     * @param testKey
     * @return emoji or null if not found
     */
    public LazyLoadEmoji getEmoji(Integer testKey)
    {
        return getEmoji(testKey == null ? null : Integer.toString(testKey));
    }

    /**
     * Get the emoji
     * 
     * @param testKey
     * @return emoji or null if not found
     */
    public LazyLoadEmoji getEmoji(String testKey)
    {
        return getEmoji(testKey, null);
    }

    /**
     * Get the emoji, if the configuration allows for that type of emoji
     * 
     * @param testKey
     *            A String key
     * @param config
     * @return emoji or null if not found or if configuration prohibits this emoji
     */
    public LazyLoadEmoji getEmoji(String testKey, ConfigEmoji config)
    {
        if (config != null && !config.isTypeEnabledAndLoaded(type))
        {
            return null;
        }

        LazyLoadEmoji emoji = null;
        if (type.canRegEx())
        {
            for (String regex : map.keySet())
            {
                if (testKey.matches(regex))
                {
                    emoji = map.get(regex);
                    break;
                }
            }
        }
        else
        {
            emoji = map.get(testKey);
        }

        return emoji;
    }

    public LazyLoadEmoji put(String key, LazyLoadEmoji value)
    {
        return map.put(key, value);
    }

    public Collection<String> keySet()
    {
        Set<String> keys = new HashSet<String>(map.keySet().size());
        keys.addAll(map.keySet());
        return keys;
    }

    public Map<String, LazyLoadEmoji> getMap()
    {
        return map;
    }
}
