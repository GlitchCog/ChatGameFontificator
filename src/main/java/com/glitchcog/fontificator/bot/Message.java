package com.glitchcog.fontificator.bot;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.config.MessageCasing;
import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.emoji.EmojiType;
import com.glitchcog.fontificator.emoji.LazyLoadEmoji;
import com.glitchcog.fontificator.emoji.TypedEmojiMap;
import com.glitchcog.fontificator.sprite.SpriteCharacterKey;

/**
 * Anything posted to an IRC channel
 * 
 * @author Matt Yanos
 */
public class Message
{
    private static final Logger logger = Logger.getLogger(Message.class);

    private static final long UNCOMPLETED_TIME = 0L;

    /**
     * A regex for checking for emoji keys in the text. Used in a String.split to divide the message into an array of
     * words and the spaces between them.
     */
    public static final String SPACE_BOUNDARY_REGEX = "(?:(?=\\s+)(?<!\\s+)|(?<=\\s+)(?!\\s+))";

    /**
     * Why doesn't this catch 🤖 (U+0x1f916)?
     */
    private static final String TWITTER_EMOJI_REGEX = "((([\uD83C\uDF00-\uD83D\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]|[\u2700-\u27BF])[\\x{1F3FB}-\\x{1F3FF}]?))";
    private static final Pattern TWITTER_EMOJI_PATTERN = Pattern.compile(TWITTER_EMOJI_REGEX);

    /**
     * The state of the user that is prepended to the message from Twitch. This reference is the same one that's stored
     * in the ChatViewerBot, so it is possible to update that object and see the effects on this message
     */
    private final TwitchPrivmsg privmsg;

    /**
     * Whether this message is censored, meaning not displayed at all in the chat
     */
    private boolean censored;

    /**
     * Whether a censored message was purged from the chat
     */
    private boolean purged;

    /**
     * The reason for the censorship
     */
    private String censoredReason;

    /**
     * Whether this message has been manually censored or uncensored
     */
    private boolean manualCensorship;

    /**
     * The username of the poster, or the username of the user who joined, if the message is a join message
     */
    private final String rawUsername;

    /**
     * A time stamp of when the message was created
     */
    private final Date timestamp;

    /**
     * The text content of the message
     */
    private final String content;

    /**
     * The type of the message
     */
    private final MessageType type;

    /**
     * The badges to draw, the size of which is used to keep track of the position of the username, which is used for
     * coloring. This value is calculated when the text is parsed into SpriteCharacterKeys and will be null if all
     * badges are switched off.
     */
    private Map<String, LazyLoadEmoji> badges;

    /**
     * The String of the message put into text
     */
    private SpriteCharacterKey[] text;

    /**
     * The text that goes between a time stamp and a username
     */
    private static final String TIMESTAMP_USERNAME_SPACER = " ";

    /**
     * The maximum possible value of an int cast into a float, used to max out the character count if the message speed
     * is maxed out
     */
    private static final float MAX_INT_AS_FLOAT = (float) Integer.MAX_VALUE;

    /**
     * How much of the message should be drawn
     */
    private float drawCursor;

    /**
     * Keep track of the configuration the last time this message was parsed, so if no configuration has changed (check
     * using ConfigMessage.equals), then there's no need to re-parse it
     */
    private ConfigMessage lastMessageConfig;

    /**
     * Keep track of the configuration the last time this message was parsed, so if no configuration has changed (check
     * using ConfigEmoji.equals), then there's no need to re-parse it
     */
    private ConfigEmoji lastEmojiConfig;

    /**
     * The moment in time the message was completely drawn
     */
    private long completedTime;

    /**
     * Construct a message specifying the type, username and content, but set the time stamp to the current local time
     * 
     * @param type
     *            The type of this message
     * @param rawUsername
     *            The username of whomever posted this message
     * @param content
     *            The text of the message
     * @param privmsg
     *            The Twitch Privmsg object, will not be null
     */
    public Message(MessageType type, String rawUsername, String content, TwitchPrivmsg privmsg)
    {
        this(type, rawUsername, new Date(), content, privmsg);
    }

    /**
     * Construct a message specifying everything
     * 
     * @param type
     *            The type of this message
     * @param rawUsername
     *            The username of whomever posted this message
     * @param timestamp
     *            When the message was posted (local time)
     * @param content
     *            The text of the message
     * @param privmsg
     *            The Twitch Privmsg object, will not be null
     */
    public Message(MessageType type, String rawUsername, Date timestamp, String content, TwitchPrivmsg privmsg)
    {
        this.type = type;
        this.rawUsername = rawUsername;
        this.timestamp = timestamp;
        this.content = content;
        this.drawCursor = 0.0f;
        this.lastMessageConfig = new ConfigMessage();
        this.lastEmojiConfig = new ConfigEmoji();
        this.privmsg = privmsg;
        this.completedTime = UNCOMPLETED_TIME;
    }

    /**
     * Get whether the message is a join type message
     * 
     * @return joinType
     */
    public boolean isJoinType()
    {
        return MessageType.JOIN.equals(type);
    }

    /**
     * Get the type of the message
     * 
     * @return type
     */
    public MessageType getType()
    {
        return type;
    }

    /**
     * Get the username of the poster, or the username of the user who joined, if the message is a join message
     * 
     * @return username
     */
    public String getUsername()
    {
        return rawUsername;
    }

    /**
     * Increment the draw cursor based on the message text as defined by the specified messageConfig and its message
     * speed setting
     * 
     * @param messageConfig
     * @param emojiConfig
     */
    public void incrementDrawCursor(EmojiManager emojiManager, ConfigMessage messageConfig, ConfigEmoji emojiConfig)
    {
        float characterCount = 1.0f;

        // If the message speed is zero, then it's max, so draw everything
        if (messageConfig.getMessageSpeed() < 1)
        {
            characterCount = MAX_INT_AS_FLOAT;
        }
        // If this is true, then multiple characters need to be drawn per update
        else if (1000L / messageConfig.getMessageSpeed() < ConfigMessage.SHORTEST_DELAY)
        {
            characterCount = (float) messageConfig.getMessageSpeed() * (float) ConfigMessage.SHORTEST_DELAY / 1000.0f;
        }

        drawCursor += characterCount;

        // Whether the message is completely drawn yet needs to be calculated
        // and set as a member variable once true so that configuration changes
        // that modify the length of the message do not re-trigger drawing bits
        // of past messages
        if (drawCursor >= getMessageLength(emojiManager, messageConfig, emojiConfig))
        {
            completedTime = System.currentTimeMillis();
        }
    }

    /**
     * Get message length in characters. Emoji are considered single characters.
     * 
     * @param emojiManager
     * @param messageConfig
     * @param emojiConfig
     * @return length
     */
    public int getMessageLength(EmojiManager emojiManager, ConfigMessage messageConfig, ConfigEmoji emojiConfig)
    {
        return getText(emojiManager, messageConfig, emojiConfig).length;
    }

    /**
     * Get the draw cursor, or the max draw cursor value if the message is completely drawn
     * 
     * @return drawCursor
     */
    public float getDrawCursor()
    {
        return isCompletelyDrawn() ? MAX_INT_AS_FLOAT : drawCursor;
    }

    /**
     * Get whether the message is completely drawn or not. Because the fully displayed message can change, like if the
     * option to show the time stamp is selected on the fly, once any configuration of the message is fully displayed,
     * it should remain fully displayed, even though the draw cursor won't be at the end of the now longer message text.
     * 
     * @return completelyDrawn
     */
    public boolean isCompletelyDrawn()
    {
        return completedTime != 0;
    }

    public void setCompletelyDrawn()
    {
        drawCursor = Integer.MAX_VALUE;
        completedTime = System.currentTimeMillis();
    }

    /**
     * Get the time stamp
     * 
     * @return timestamp
     */
    public Date getTimestamp()
    {
        return timestamp;
    }

    /**
     * Get the string representation of the time stamp using the
     * 
     * @param messageConfig
     * @return timestampString
     */
    public String getTimestampString(ConfigMessage messageConfig)
    {
        return messageConfig.getTimerFormatter().format(timestamp);
    }

    /**
     * Get the content of the message
     * 
     * @return content
     */
    public String getContent()
    {
        return content;
    }

    @Override
    public String toString()
    {
        return rawUsername + ": " + content;
    }

    /**
     * Get the index right after the username based on the specified messageConfig
     * 
     * @param messageConfig
     * @return
     */
    public int getIndexTimestamp(ConfigMessage messageConfig)
    {
        return messageConfig.showTimestamps() ? getTimestampString(messageConfig).length() : 0;
    }

    /**
     * Get the indexes for when the username starts and right after the username based on the specified messageConfig
     * 
     * @param messageConfig
     * @return username index
     */
    public int[] getIndexUsername(ConfigMessage messageConfig)
    {
        int start = badges == null ? 0 : badges.size();
        int end;
        if (messageConfig.showTimestamps())
        {
            start += getIndexTimestamp(messageConfig);
        }
        if (messageConfig.showUsernames())
        {
            if (messageConfig.showTimestamps())
            {
                start += TIMESTAMP_USERNAME_SPACER.length();
            }
            final String usernameFormat = messageConfig.getUsernameFormat();
            if (usernameFormat.indexOf(ConfigMessage.USERNAME_REPLACE) > 0)
            {
                final int distanceIntoFormat = usernameFormat.indexOf(ConfigMessage.USERNAME_REPLACE);
                start += distanceIntoFormat;
            }
            end = start;
            end += rawUsername.length();
            return new int[] { start, end };
        }
        else
        {
            return new int[] { start, start };
        }

    }

    /**
     * Get the full text of the message to display based on the specified messageConfig
     * 
     * @param emojiManager
     * @param messageConfig
     * @param emojiConfig
     * @return message text
     */
    public SpriteCharacterKey[] getText(EmojiManager emojiManager, ConfigMessage messageConfig, ConfigEmoji emojiConfig)
    {
        if (text != null && !isConfigChanged(messageConfig, emojiConfig))
        {
            return text;
        }
        else
        {
            text = parseIntoText(emojiManager, messageConfig, emojiConfig);
            this.lastMessageConfig.deepCopy(messageConfig);
            this.lastEmojiConfig.deepCopy(emojiConfig);
            return text;
        }
    }

    /**
     * Get whether the configuration has changed
     * 
     * @param messageConfig
     * @param emojiConfig
     * @return changed
     */
    private boolean isConfigChanged(ConfigMessage messageConfig, ConfigEmoji emojiConfig)
    {
        final boolean messageConfigChanged = !messageConfig.equals(lastMessageConfig);
        final boolean emojiConfigChanged = !emojiConfig.equals(lastEmojiConfig);
        return messageConfigChanged || emojiConfigChanged;
    }

    /**
     * Compile the array of SpriteCharacterKeys using the specified configuration. This can be a bit memory intensive
     * since each character is a new albeit small object, so this should not be done many times a second, rather only if
     * something has changed in the configuration to warrant a re-translation.
     * 
     * @param emojiManager
     * @param messageConfig
     * @param emojiConfig
     * @return spriteCharacterKeys
     */
    private SpriteCharacterKey[] parseIntoText(EmojiManager emojiManager, ConfigMessage messageConfig, ConfigEmoji emojiConfig)
    {
        List<SpriteCharacterKey> keyList = new ArrayList<SpriteCharacterKey>();

        if (messageConfig.showTimestamps())
        {
            String timeStampStr = messageConfig.getTimerFormatter().format(timestamp);
            timeStampStr = applyCasing(timeStampStr, messageConfig.getMessageCasing());
            keyList.addAll(toSpriteArray(timeStampStr));
        }

        // Add badges to be placed right before the username
        if (emojiConfig.isAnyBadgesEnabled())
        {
            // LinkedHashMap to preserve original insert order
            badges = new LinkedHashMap<String, LazyLoadEmoji>();

            final boolean userIsModerator = privmsg.getUserType() == UserType.MOD;

            // Bank to pull Twitch badges from
            TypedEmojiMap twitchBadgeBank = emojiManager.getEmojiByType(EmojiType.TWITCH_BADGE);
            // Bank to pull FrankerFaceZ badges from
            TypedEmojiMap ffzBadgeBank = emojiManager.getEmojiByType(EmojiType.FRANKERFACEZ_BADGE);

            // Get the badge for the type of user, if the usertype has a badge (but not if it's an ffzBot)
            if (privmsg.getUserType() != null && privmsg.getUserType() != UserType.NONE)
            {
                LazyLoadEmoji testBadge;
                // FFZ badges are enabled, the user is a moderator, and the custom FFZ moderator badge exists
                if (emojiConfig.isFfzBadgesEnabled() && userIsModerator && ffzBadgeBank.getEmoji(UserType.MOD.getKey()) != null)
                {
                    badges.put(UserType.MOD.getKey(), ffzBadgeBank.getEmoji(UserType.MOD.getKey()));
                }
                else if (emojiConfig.isTwitchBadgesEnabled() && (testBadge = twitchBadgeBank.getEmoji(privmsg.getUserType().getKey())) != null)
                {
                    badges.put(privmsg.getUserType().getKey(), testBadge);
                }
            }

            LazyLoadEmoji replacementBadge = null;

            if (emojiConfig.isFfzBadgesEnabled())
            {
                Map<Integer, Set<String>> ffzBadgeUsers = emojiManager.getFfzBadgeUsers();
                for (Integer ffzBadgeType : ffzBadgeUsers.keySet())
                {
                    final String ffzBadgeKey = ffzBadgeType == null ? null : Integer.toString(ffzBadgeType);
                    Set<String> users = ffzBadgeUsers.get(ffzBadgeType);
                    if (users.contains(rawUsername.toLowerCase()))
                    {
                        LazyLoadEmoji ffzBadge = ffzBadgeBank.getEmoji(ffzBadgeType);
                        if (ffzBadge.isReplacement() && badges.containsKey(ffzBadge.getReplaces()))
                        {
                            badges.put(ffzBadge.getReplaces(), ffzBadge);
                            if (userIsModerator && true)
                            {
                                replacementBadge = ffzBadge;
                            }
                        }
                        else
                        {
                            badges.put(ffzBadgeKey, ffzBadge);
                        }
                    }
                }
            }

            // Optional subscriber badge
            if (emojiConfig.isTwitchBadgesEnabled())
            {
                final String subStr = "subscriber";
                if (privmsg.isSubscriber() && twitchBadgeBank.getEmoji(subStr) != null)
                {
                    badges.put(subStr, twitchBadgeBank.getEmoji(subStr));
                }
            }

            // Optional turbo badge
            final String turboStr = "turbo";
            if (emojiConfig.isTwitchBadgesEnabled() && privmsg.isTurbo() && twitchBadgeBank.getEmoji(turboStr) != null)
            {
                badges.put(turboStr, twitchBadgeBank.getEmoji(turboStr));
            }

            final String primeStr = "prime";
            if (emojiConfig.isTwitchBadgesEnabled() && privmsg.isPrime() && twitchBadgeBank.getEmoji(primeStr) != null)
            {
                badges.put(primeStr, twitchBadgeBank.getEmoji(primeStr));
            }

            // Add each badges map item onto the sprite character key list
            for (LazyLoadEmoji lle : badges.values())
            {
                SpriteCharacterKey sck = new SpriteCharacterKey(lle, true);
                if (userIsModerator && lle == replacementBadge)
                {
                    sck.setEmojiBgColorOverride(ConfigEmoji.MOD_BADGE_COLOR);
                }
                keyList.add(sck);
            }
        }

        if (messageConfig.showUsernames())
        {
            if (messageConfig.showTimestamps())
            {
                keyList.addAll(toSpriteArray(TIMESTAMP_USERNAME_SPACER));
            }
            String casedUsername = getFormattedUsername(messageConfig);
            keyList.addAll(toSpriteArray(casedUsername));
        }
        if (messageConfig.showUsernames() || messageConfig.showTimestamps() || (emojiConfig.isAnyBadgesEnabled() && badges != null && !badges.isEmpty()))
        {
            if (type == MessageType.NORMAL || type == MessageType.MANUAL)
            {
                keyList.addAll(toSpriteArray(messageConfig.getContentBreaker()));
            }
            else
            {
                keyList.addAll(toSpriteArray(type.getContentBreaker()));
            }
        }

        // Parse out the emoji, if enabled
        if (emojiConfig.isEmojiEnabled())
        {
            processEmoji(content, privmsg, keyList, emojiManager, emojiConfig, MessageType.MANUAL.equals(type), messageConfig.getMessageCasing());
        }
        // Configured for no emoji, so just chars
        else
        {
            keyList.addAll(toSpriteArray(applyCasing(content, messageConfig.getMessageCasing())));
        }

        // Return the list as an array, to be kept until configuration is modified requiring a reprocessing
        return keyList.toArray(new SpriteCharacterKey[keyList.size()]);
    }

    private static String[] codePointSpaceSplit(String content)
    {
        List<String> words = new ArrayList<String>();
        if (!content.isEmpty())
        {
            int breakIndex = 0;
            int c = 0;
            boolean currentlyInWord = !Character.isWhitespace(content.codePointAt(c));
            while (c < content.length())
            {
                final int codePoint = content.codePointAt(c);
                final boolean isWhitespace = Character.isWhitespace(codePoint);
                final int charSize = Character.charCount(codePoint);

                if ((currentlyInWord && isWhitespace) || (!currentlyInWord && !isWhitespace))
                {
                    words.add(content.substring(breakIndex, c));
                    breakIndex = c;
                    currentlyInWord = !currentlyInWord;
                }

                c += charSize;
            }
            words.add(content.substring(breakIndex, content.length()));
        }
        return words.toArray(new String[words.size()]);
    }

    /**
     * Convert the content of the message into the appropriate emoji. Add those emoji and the remaining characters
     * between them to the specified keyList array.
     * 
     * @param content
     * @param privmsg
     * @param keyList
     *            The array to add the emoji and remaining characters to
     * @param emojiManager
     * @param emojiConfig
     * @param isManualMessage
     */
    private static void processEmoji(String content, TwitchPrivmsg privmsg, List<SpriteCharacterKey> keyList, EmojiManager emojiManager, ConfigEmoji emojiConfig, boolean isManualMessage, MessageCasing casing)
    {
        Map<Integer, EmoteAndIndices> emotes = privmsg.getEmotes();

        String[] words = codePointSpaceSplit(content); // content.split(SPACE_BOUNDARY_REGEX);

        int codeIndex = 0;

        LazyLoadEmoji emoji = null;
        for (int w = 0; w < words.length; w++)
        {
            EmoteAndIndices eai = emotes.get(codeIndex);
            if (eai != null && emojiConfig.isTwitchEnabled())
            {
                // This catches subscriber emotes and any non-global emotes
                emoji = emojiManager.getEmojiById(eai.getEmoteId(), words[w], emojiConfig);
                if (emoji == null)
                {
                    // The already loaded Twitch V1 emoji map doesn't have this emote ID yet, so add it
                    try
                    {
                        emoji = emojiManager.putEmojiById(eai.getEmoteId(), words[w], emojiConfig);
                    }
                    catch (MalformedURLException e)
                    {
                        logger.error("Unable to load emote for emote ID " + eai.getEmoteId(), e);
                    }
                }
            }
            // At this point, only 3rd party emoji should be a possibility for this word (with the exception of
            // manual messages)
            else
            {
                // This is the manual message exception
                if (isManualMessage)
                {
                    // As a known bug here, all manual messages will have access to all Twitch emotes, regardless of
                    // subscriber status
                    emoji = emojiManager.getEmojiWords(EmojiType.MANUAL_EMOJI_TYPES, words[w], emojiConfig);
                }
                // Only check 3rd party emotes
                else
                {
                    emoji = emojiManager.getEmojiWords(EmojiType.THIRD_PARTY_EMOJI_TYPES, words[w], emojiConfig);
                }
            }

            // If a word-emoji has not yet been found and Twitter single-character emoji are enabled, then process them
            // within the word
            if (emoji == null)
            {
                Matcher matcher = TWITTER_EMOJI_PATTERN.matcher(words[w]);
                if (emojiConfig.isTwitterEnabled() && matcher.find())
                {
                    matcher.reset();

                    String twitterEmojiUrl = null;
                    int i = 0;
                    while (matcher.find())
                    {
                        String rawCode = matcher.group(2);
                        String iconId = toCodePoint(rawCode.indexOf('\u200D') < 0 ? rawCode.replace("\uFE0F", "") : rawCode);
                        twitterEmojiUrl = "https://twemoji.maxcdn.com/2/" + "72x72" + "/" + iconId + ".png";
                        LazyLoadEmoji lle = emojiManager.getEmojiByType(EmojiType.TWITTER_EMOJI).getEmoji(iconId);
                        if (lle == null)
                        {
                            try
                            {
                                lle = new LazyLoadEmoji(iconId, twitterEmojiUrl, EmojiType.TWITTER_EMOJI);
                                emojiManager.getEmojiByType(EmojiType.TWITTER_EMOJI).put(iconId, lle);
                            }
                            catch (MalformedURLException e)
                            {
                                logger.error("Couldn't parse emoji URL: " + twitterEmojiUrl, e);
                            }
                        }
                        final String wordBit = words[w].substring(i, matcher.start());

                        keyList.addAll(toSpriteArray(applyCasing(wordBit, casing)));
                        if (lle != null)
                        {
                            keyList.add(new SpriteCharacterKey(lle, false));
                        }
                        i = matcher.end();
                    }
                    final String wordBit = words[w].substring(i, words[w].length());
                    keyList.addAll(toSpriteArray(applyCasing(wordBit, casing)));
                }
                else
                {
                    keyList.addAll(toSpriteArray(applyCasing(words[w], casing)));
                }
            }

            if (emoji != null)
            {
                keyList.add(new SpriteCharacterKey(emoji, false));
            }

            // Increment the codeIndex by the current word's code point count
            // Emoji indexes are in code points, but java is counting in fixed 16-bit units
            codeIndex += words[w].codePointCount(0, words[w].length());
        }
    }

    /**
     * From https://gist.github.com/heyarny/71c246f2f7fa4d9d10904fb9d5b1fa1d
     * 
     * @param unicodeSurrogates
     * @param sep
     * @return codePoint
     */
    private static String toCodePoint(String unicodeSurrogates)
    {
        ArrayList<String> r = new ArrayList<String>();
        int c = 0, p = 0, i = 0;
        while (i < unicodeSurrogates.length())
        {
            c = unicodeSurrogates.charAt(i++);
            if (p != 0)
            {
                r.add(Integer.toString((0x10000 + ((p - 0xD800) << 10) + (c - 0xDC00)), 16));
                p = 0;
            }
            else if (0xD800 <= c && c <= 0xDBFF)
            {
                p = c;
            }
            else
            {
                r.add(Integer.toString(c, 16));
            }
        }

        String output = "";
        for (String str : r)
        {
            output += (output.isEmpty() ? "" : "-") + str;
        }

        return output;
    }

    public String getCensoredReason()
    {
        return censoredReason;
    }

    public void setCensoredReason(String censoredReason)
    {
        this.censoredReason = censoredReason;
    }

    public boolean isCensored()
    {
        return censored;
    }

    public void setCensored(boolean censored, boolean isCensorshipEnabled)
    {
        // Only end the draw cursor if the message is both censored AND if censorship is actually enabled,
        // otherwise the message will insta-draw because it would be censored if censorship were enabled
        if (censored && isCensorshipEnabled)
        {
            setCompletelyDrawn();
        }
        this.censored = censored;
    }

    public void setPurged(boolean purged)
    {
        this.purged = purged;
    }

    public void resetCensorship(boolean overrideManual)
    {
        if (!purged && (!manualCensorship || overrideManual))
        {
            manualCensorship = false;
            censored = false;
            censoredReason = null;
        }
    }

    public boolean isManualCensorship()
    {
        return manualCensorship;
    }

    public void setManualCensorship(boolean manualCensorship)
    {
        this.manualCensorship = manualCensorship;
        this.censoredReason = "MANUAL";
    }

    public int getUserPostCount()
    {
        return privmsg.getPostCount();
    }

    public TwitchPrivmsg getPrivmsg()
    {
        return privmsg;
    }

    /**
     * @return age of the message since it was completely drawn, or zero if it isn't yet completed in seconds
     */
    public long getAge(long currentTime)
    {
        return completedTime == UNCOMPLETED_TIME ? 0L : (currentTime - completedTime) / 1000L;
    }

    private static String applyCasing(String str, MessageCasing casing)
    {
        if (casing == null)
        {
            return null;
        }

        switch (casing)
        {
        case LOWERCASE:
            return str.toLowerCase();
        case UPPERCASE:
            return str.toUpperCase();
        case MIXED_CASE:
        default:
            return str;
        }
    }

    /**
     * Convert the string to a list of SpriteCharacterKeys by codepoint.
     *
     * @param str
     * @returns A List of SpriteCharacterKeys
     */
    private static List<SpriteCharacterKey> toSpriteArray(String str)
    {
        // TODO java 1.8: We could use CharSequence and call codePoints method
        List<SpriteCharacterKey> keyList = new ArrayList<SpriteCharacterKey>();

        int i = 0;
        while (i < str.length())
        {
            final int codepoint = str.codePointAt(i);
            keyList.add(new SpriteCharacterKey(codepoint));
            i += Character.charCount(codepoint);
        }

        return keyList;
    }

    private String getFormattedUsername(ConfigMessage messageConfig)
    {
        final String usernameFormat = messageConfig.getUsernameFormat();
        final String casedUsername = applyCasing(rawUsername, messageConfig.getMessageCasing());
        final String formattedUsername = usernameFormat.replaceAll(ConfigMessage.USERNAME_REPLACE, casedUsername);
        return formattedUsername;
    }
}
