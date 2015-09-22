package com.glitchcog.fontificator.bot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.emoji.LazyLoadEmoji;
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
    private static final String SPACE_BOUNDARY_REGEX = "(?:(?=\\s+)(?<!\\s+)|(?<=\\s+)(?!\\s+))";

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
    public Message(MessageType type, String username, String content)
    {
        this(type, username, new Date(), content);
    }

    /**
     * Construct a message specifying everything
     * 
     * @param type
     * @param username
     * @param timestamp
     * @param content
     */
    public Message(MessageType type, String username, Date timestamp, String content)
    {
        this.type = type;
        this.username = username;
        this.timestamp = timestamp;
        this.content = content;
        this.drawCursor = 0.0f;
        this.lastMessageConfig = new ConfigMessage();
        this.lastEmojiConfig = new ConfigEmoji();
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
        int index = 0;
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
     * since each character is a new albeit small object, so when this should not be done many times a second, rather
     * only if something has changed in the configuration to warrant a re-translation.
     * 
     * @param emojiManager
     * @param messageConfig
     * @param emojiConfig
     * @return spriteCharacterKeys
     */
    private SpriteCharacterKey[] parseIntoText(EmojiManager emojiManager, ConfigMessage messageConfig, ConfigEmoji emojiConfig)
    {
        String rawMessageText = "";
        if (messageConfig.showTimestamps())
        {
            rawMessageText += messageConfig.getTimerFormatter().format(timestamp);
        }
        if (messageConfig.showUsernames())
        {
            if (messageConfig.showTimestamps())
            {
                rawMessageText += TIMESTAMP_USERNAME_SPACER;
            }
            rawMessageText += username;
        }
        if (messageConfig.showUsernames() || messageConfig.showTimestamps())
        {
            rawMessageText += type.getContentBreaker();
        }

        int contentStartIndex = rawMessageText.length();

        rawMessageText += content;

        List<SpriteCharacterKey> keyList = new ArrayList<SpriteCharacterKey>();

        if (emojiConfig.isEmojiEnabled())
        {
            // Just add characters up until the actual message content
            for (int c = 0; c < contentStartIndex; c++)
            {
                keyList.add(new SpriteCharacterKey(rawMessageText.charAt(c)));
            }

            String[] words = content.split(SPACE_BOUNDARY_REGEX);
            LazyLoadEmoji[] emoji = null;
            for (int w = 0; w < words.length; w++)
            {
                emoji = emojiManager.getEmoji(words[w], emojiConfig);
                if (emoji == null)
                {
                    for (int c = 0; c < words[w].length(); c++)
                    {
                        keyList.add(new SpriteCharacterKey(words[w].charAt(c)));
                    }
                }
                else
                {
                    keyList.add(new SpriteCharacterKey(emoji));
                }
            }
        }
        // No emoji, so just chars
        else
        {
            for (int c = 0; c < rawMessageText.length(); c++)
            {
                keyList.add(new SpriteCharacterKey(rawMessageText.charAt(c)));
            }
        }

        return keyList.toArray(new SpriteCharacterKey[keyList.size()]);
    }

}
