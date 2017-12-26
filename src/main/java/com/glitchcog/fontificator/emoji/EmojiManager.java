package com.glitchcog.fontificator.emoji;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.emoji.loader.EmojiApiLoader;

/**
 * Handles accessing LazyLoadEmoji objects based on text keys to match emoji regular expressions
 * 
 * @author Matt Yanos
 */
public class EmojiManager
{
    private static final Logger logger = Logger.getLogger(EmojiManager.class);

    private static final String FFZ_REPLACEMENT_EMOTE_URL_BASE = "http://cdn.frankerfacez.com/script/replacements/";

    /**
     * Replacement FFZ emoji, because Twitch's look horrible
     */
    private static final Map<Integer, String> FFZ_REPLACEMENT_EMOTE_URLS = new HashMap<Integer, String>()
    {
        private static final long serialVersionUID = 1L;

        {
            put(15, FFZ_REPLACEMENT_EMOTE_URL_BASE + "15-JKanStyle.png");
            put(16, FFZ_REPLACEMENT_EMOTE_URL_BASE + "16-OptimizePrime.png");
            put(17, FFZ_REPLACEMENT_EMOTE_URL_BASE + "17-StoneLightning.png");
            put(18, FFZ_REPLACEMENT_EMOTE_URL_BASE + "18-TheRinger.png");
            put(19, FFZ_REPLACEMENT_EMOTE_URL_BASE + "19-PazPazowitz.png");
            put(20, FFZ_REPLACEMENT_EMOTE_URL_BASE + "20-EagleEye.png");
            put(21, FFZ_REPLACEMENT_EMOTE_URL_BASE + "21-CougarHunt.png");
            put(22, FFZ_REPLACEMENT_EMOTE_URL_BASE + "22-RedCoat.png");
            put(26, FFZ_REPLACEMENT_EMOTE_URL_BASE + "26-JonCarnage.png");
            put(33, FFZ_REPLACEMENT_EMOTE_URL_BASE + "33-DansGame.png");
            put(26, FFZ_REPLACEMENT_EMOTE_URL_BASE + "26-JonCarnage.png");
            put(27, FFZ_REPLACEMENT_EMOTE_URL_BASE + "27-PicoMause.png");
            put(30, FFZ_REPLACEMENT_EMOTE_URL_BASE + "30-BCWarrior.png");
            put(33, FFZ_REPLACEMENT_EMOTE_URL_BASE + "33-DansGame.png");
            put(36, FFZ_REPLACEMENT_EMOTE_URL_BASE + "36-PJSalt.png");
        }
    };

    /**
     * Holds all the pre-loaded emoji. This is where any pre-loadable emoji go. Basically, it holds anything other than
     * the Twitch emoji, because Twitch's emoji API is stupid and wrong.
     */
    private Map<EmojiType, TypedEmojiMap> preloadedEmoji;

    /**
     * V1 Twitch emotes loaded whenever a loaded on the fly via the emote ID on the IRC post tags' emote ID. These
     * aren't ever lazy loaded because they are only loaded on the fly when used.
     */
    private Map<String, LazyLoadEmoji> emojiById;

    /**
     * Map keyed off of FrankerFaceZ badge IDs that returns a set of users that have that badge
     */
    private Map<Integer, Set<String>> ffzBadgeUsers;

    /**
     * Construct an emoji manager object, instantiates the map of maps keyed off of all the possible emoji types
     */
    public EmojiManager()
    {
        ffzBadgeUsers = new HashMap<Integer, Set<String>>();
        preloadedEmoji = new HashMap<EmojiType, TypedEmojiMap>();
        for (EmojiType type : EmojiType.values())
        {
            preloadedEmoji.put(type, new TypedEmojiMap(type));
        }
        emojiById = new HashMap<String, LazyLoadEmoji>();
    }

    /**
     * Get the map of only the specified type of emoji
     * 
     * @param type
     * @return
     */
    public TypedEmojiMap getEmojiByType(EmojiType type)
    {
        return preloadedEmoji.get(type);
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
     * @return emoji or null if not found
     */
    public LazyLoadEmoji getEmojiWords(EmojiType type, String testKey, ConfigEmoji config)
    {
        return getEmojiWords(new EmojiType[] { type }, testKey, config);
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
    public LazyLoadEmoji getEmojiWords(EmojiType[] types, String testKey, ConfigEmoji config)
    {
        LazyLoadEmoji emoji = null;
        // If config is null, then just assume we want it. Used when caching.
        for (EmojiType type : types)
        {
            if (config == null || config.isTypeEnabledAndLoaded(type))
            {
                TypedEmojiMap typedEmoji = preloadedEmoji.get(type);
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
     * @param emojiId
     * @param word
     * @param emojiConfig
     * @return
     * @throws MalformedURLException
     */
    public LazyLoadEmoji putEmojiById(Integer emojiId, String word, ConfigEmoji emojiConfig) throws MalformedURLException
    {
        logger.trace("Loading unmapped emote from emote ID " + emojiId);
        final String emoteUrl = EmojiApiLoader.getTwitchEmoteV1Url(emojiId);
        LazyLoadEmoji emoji = new LazyLoadEmoji(word, emoteUrl, EmojiType.TWITCH_V1);
        emojiById.put(Integer.toString(emojiId), emoji);
        return emoji;
    }

    /**
     * Get the V1 Twitch emoji by ID. This is only ever a single frame, but it's an array anyhow to match the pre-loaded
     * style. Depending on the emojiConfig, a few of these can be substituted for FrankerFaceZ replacement emotes
     * 
     * @param emojiId
     * @param word
     * @param emojiConfig
     * @return
     */
    public LazyLoadEmoji getEmojiById(Integer emojiId, String word, ConfigEmoji emojiConfig)
    {
        if (emojiConfig != null && emojiConfig.isTwitchEnabled() && emojiConfig.isFfzEnabled() && FFZ_REPLACEMENT_EMOTE_URLS.keySet().contains(emojiId))
        {
            TypedEmojiMap tem = preloadedEmoji.get(EmojiType.FRANKERFACEZ_REPLACEMENT);
            LazyLoadEmoji emoji = tem.getEmoji(getFfzReplacementKey(emojiId), emojiConfig);
            if (emoji == null)
            {
                try
                {
                    logger.trace("Loading replacement FFZ emote for " + word);
                    emoji = new LazyLoadEmoji(word, FFZ_REPLACEMENT_EMOTE_URLS.get(emojiId), EmojiType.FRANKERFACEZ_REPLACEMENT);
                    tem.put(getFfzReplacementKey(emojiId), emoji);
                    return emoji;
                }
                catch (MalformedURLException e)
                {
                    logger.error(e.toString(), e);
                    return emojiById.get(Integer.toString(emojiId));
                }
            }
            else
            {
                return emoji;
            }
        }
        else if (emojiConfig != null && emojiConfig.isTwitchEnabled())
        {
            return emojiById.get(Integer.toString(emojiId));
        }
        else
        {
            return null;
        }
    }

    private static String getFfzReplacementKey(Integer emojiId)
    {
        return "FfzRep" + Integer.toString(emojiId);
    }

    public void setFfzBadgeUsers(Map<Integer, Set<String>> ffzBadgeUsers)
    {
        this.ffzBadgeUsers = ffzBadgeUsers;
    }

    public Map<Integer, Set<String>> getFfzBadgeUsers()
    {
        return ffzBadgeUsers;
    }
}
