package com.glitchcog.fontificator.bot;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * The state of the user that is prepended to the message from Twitch. This reference is the same one that's stored
     * in the ChatViewerBot, so it is possible to update that object and see the effects on this message
     */
    private final TwitchPrivmsg privmsg;

    /**
     * Whether this message is censored, meaning not displayed at all in the chat
     */
    private boolean censored;

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
    private final String username;

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
     * @param username
     *            The username of whomever posted this message
     * @param content
     *            The text of the message
     * @param privmsg
     *            The Twitch Privmsg object, will not be null
     */
    public Message(MessageType type, String username, String content, TwitchPrivmsg privmsg)
    {
        this(type, username, new Date(), content, privmsg);
    }

    /**
     * Construct a message specifying everything
     * 
     * @param type
     *            The type of this message
     * @param username
     *            The username of whomever posted this message
     * @param timestamp
     *            When the message was posted (local time)
     * @param content
     *            The text of the message
     * @param privmsg
     *            The Twitch Privmsg object, will not be null
     */
    public Message(MessageType type, String username, Date timestamp, String content, TwitchPrivmsg privmsg)
    {
        this.type = type;
        this.username = username;
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
        return username;
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
        return username + ": " + content;
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
     * Get the index right after the username based on the specified messageConfig
     * 
     * @param messageConfig
     * @return username index
     */
    public int getIndexUsername(ConfigMessage messageConfig)
    {
        int index = badges.size();
        if (messageConfig.showTimestamps())
        {
            index += getIndexTimestamp(messageConfig);
        }
        if (messageConfig.showUsernames())
        {
            if (messageConfig.showTimestamps())
            {
                index += TIMESTAMP_USERNAME_SPACER.length();
            }
            index += username.length();
        }
        return index;
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
            for (int c = 0; c < timeStampStr.length(); c++)
            {
                keyList.add(new SpriteCharacterKey(timeStampStr.charAt(c)));
            }
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
                    if (users.contains(username.toLowerCase()))
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
                for (int c = 0; c < TIMESTAMP_USERNAME_SPACER.length(); c++)
                {
                    keyList.add(new SpriteCharacterKey(TIMESTAMP_USERNAME_SPACER.charAt(c)));
                }
            }
            String casedUsername = applyCasing(username, messageConfig.getMessageCasing());
            for (int c = 0; c < casedUsername.length(); c++)
            {
                keyList.add(new SpriteCharacterKey(casedUsername.charAt(c)));
            }
        }
        if (messageConfig.showUsernames() || messageConfig.showTimestamps() || (emojiConfig.isAnyBadgesEnabled() && badges != null && !badges.isEmpty()))
        {
            for (int c = 0; c < type.getContentBreaker().length(); c++)
            {
                keyList.add(new SpriteCharacterKey(type.getContentBreaker().charAt(c)));
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
            String casedContent = applyCasing(content, messageConfig.getMessageCasing());
            for (int c = 0; c < casedContent.length(); c++)
            {
                keyList.add(new SpriteCharacterKey(casedContent.charAt(c)));
            }
        }

        // Return the list as an array, to be kept until configuration is modified requiring a reprocessing
        return keyList.toArray(new SpriteCharacterKey[keyList.size()]);
    }

    public static String[] codePointSpaceSplit(String content)
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

        String[] words = content.split(SPACE_BOUNDARY_REGEX);

        int charIndex = 0;

        LazyLoadEmoji emoji = null;
        for (int w = 0; w < words.length; w++)
        {
            EmoteAndIndices eai = emotes.get(charIndex);
            if (eai != null && emojiConfig.isTwitchEnabled())
            {
                // This catches subscriber emotes and any non-global emotes
                emoji = emojiManager.getEmojiById(eai.getEmoteId(), words[w], emojiConfig);
                if (emoji == null)
                {
                    // The already loaded Twitch V1 emoji map doesn't this emote ID yet, so add it
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
                    emoji = emojiManager.getEmoji(EmojiType.MANUAL_EMOJI_TYPES, words[w], emojiConfig);
                }
                // Only check 3rd party emotes
                else
                {
                    emoji = emojiManager.getEmoji(EmojiType.THIRD_PARTY_EMOJI_TYPES, words[w], emojiConfig);
                }
            }

            if (emoji == null)
            {
                String casedWord = applyCasing(words[w], casing);
                // Done checking for all sorts of emoji types, so it's just a word. Set the characters.
                for (int c = 0; c < casedWord.length(); c++)
                {
                    keyList.add(new SpriteCharacterKey(casedWord.charAt(c)));
                }
            }
            else
            {
                keyList.add(new SpriteCharacterKey(emoji, false));
            }

            // Increment the charIndex by the current word's length
            charIndex += words[w].length();
        }
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

    public void setCensored(boolean censored)
    {
        if (censored)
        {
            drawCursor = Integer.MAX_VALUE;
            completedTime = System.currentTimeMillis();
        }
        this.censored = censored;
    }

    public void resetCensorship(boolean overrideManual)
    {
        if (!manualCensorship || overrideManual)
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

}
