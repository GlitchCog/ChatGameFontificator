package com.glitchcog.fontificator.bot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.ConfigMessage;
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
     * The number of badges to draw, to keep track of the position of the username, which is used for coloring. This
     * value is calculated when the text is parsed into SpriteCharacterKeys and will be zero if badges are switched off.
     */
    private int badgeCount;

    /**
     * Whether the message is completely drawn or not. Because the fully displayed message can change, like if the
     * option to show the time stamp is selected on the fly, once any configuration of the message is fully displayed,
     * it should remain fully displayed, even though the draw cursor won't be at the end of the now longer message text
     */
    private boolean completelyDrawn;

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
     * Construct a message specifying the type, username and content, but set the time stamp to the current time
     * 
     * @param type
     * @param username
     * @param content
     */
    public Message(MessageType type, String username, String content, TwitchPrivmsg privmsg)
    {
        this(type, username, new Date(), content, privmsg);
    }

    /**
     * Construct a message specifying everything
     * 
     * @param type
     * @param username
     * @param timestamp
     * @param content
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
            completelyDrawn = true;
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
        return completelyDrawn ? MAX_INT_AS_FLOAT : drawCursor;
    }

    /**
     * Get whether the message is completely drawn
     * 
     * @return completelyDrawn
     */
    public boolean isCompletelyDrawn()
    {
        return completelyDrawn;
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
        int index = badgeCount;
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
            final String timeStampStr = messageConfig.getTimerFormatter().format(timestamp);
            for (int c = 0; c < timeStampStr.length(); c++)
            {
                keyList.add(new SpriteCharacterKey(timeStampStr.charAt(c)));
            }
        }

        badgeCount = 0;
        // Add badges to be placed right before the username
        if (emojiConfig.isBadgesEnabled())
        {
            // Bank to pull badges from
            TypedEmojiMap badgeBank = emojiManager.getEmojiByType(EmojiType.TWITCH_BADGE);

            // Get the badge for the type of user, if the usertype has a badge
            if (privmsg.getUserType() != null && privmsg.getUserType() != UserType.NONE)
            {
                LazyLoadEmoji[] testBadge = null;
                if ((testBadge = badgeBank.getEmoji(privmsg.getUserType().getKey())) != null)
                {
                    keyList.add(new SpriteCharacterKey(testBadge, true));
                    badgeCount++;
                }
            }

            // Optional subscriber badge
            final String subStr = "subscriber";
            if (privmsg.isSubscriber() && badgeBank.getEmoji(subStr) != null)
            {
                keyList.add(new SpriteCharacterKey(badgeBank.getEmoji(subStr), true));
                badgeCount++;
            }

            // Optional turbo badge
            final String turboStr = "turbo";
            if (privmsg.isTurbo() && badgeBank.getEmoji(turboStr) != null)
            {
                keyList.add(new SpriteCharacterKey(badgeBank.getEmoji(turboStr), true));
                badgeCount++;
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
            for (int c = 0; c < username.length(); c++)
            {
                keyList.add(new SpriteCharacterKey(username.charAt(c)));
            }
        }
        if (messageConfig.showUsernames() || messageConfig.showTimestamps() || emojiConfig.isBadgesEnabled())
        {
            for (int c = 0; c < type.getContentBreaker().length(); c++)
            {
                keyList.add(new SpriteCharacterKey(type.getContentBreaker().charAt(c)));
            }
        }

        if (emojiConfig.isEmojiEnabled())
        {
            Map<Integer, EmoteAndIndices> emotes = privmsg.getEmotes();

            String[] words = content.split(SPACE_BOUNDARY_REGEX);

            int charIndex = 0;

            LazyLoadEmoji[] emoji = null;
            for (int w = 0; w < words.length; w++)
            {
                EmoteAndIndices eai = emotes.get(charIndex);
                if (eai != null)
                {
                    emoji = emojiManager.getEmojiById(eai.getEmoteId(), emojiConfig);

                    // There is an emoji here, but we couldn't find it in the emoteId map, so just use the map
                    if (emoji == null)
                    {
                        emoji = emojiManager.getEmoji(EmojiType.TWITCH_V3, words[w], emojiConfig);
                    }
                }
                // At this point, only 3rd party emoji should be a possibility for this word (with the exception of
                // manual messages)
                else
                {
                    // This is the manual message exception
                    if (MessageType.MANUAL.equals(type))
                    {
                        // As a bug here, all manual messages will have access to all Twitch emotes, regardless of
                        // subscriber status
                        emoji = emojiManager.getEmoji(words[w], emojiConfig);
                    }
                    // Only check 3rd party emotes
                    else
                    {
                        emoji = emojiManager.getEmoji(new EmojiType[] { EmojiType.FRANKERFACEZ_CHANNEL, EmojiType.FRANKERFACEZ_GLOBAL }, words[w], emojiConfig);
                    }
                }

                if (emoji == null)
                {
                    // Done checking for all sorts of emoji types, so it's just a word. Set the characters.
                    for (int c = 0; c < words[w].length(); c++)
                    {
                        keyList.add(new SpriteCharacterKey(words[w].charAt(c)));
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
        // Configured for no emoji, so just chars
        else
        {
            for (int c = 0; c < content.length(); c++)
            {
                keyList.add(new SpriteCharacterKey(content.charAt(c)));
            }
        }

        return keyList.toArray(new SpriteCharacterKey[keyList.size()]);
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
}
