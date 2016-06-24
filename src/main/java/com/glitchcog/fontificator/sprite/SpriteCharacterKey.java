package com.glitchcog.fontificator.sprite;

import java.awt.Color;

import com.glitchcog.fontificator.emoji.LazyLoadEmoji;

/**
 * Replacement for char that can also represent an emoji or a badge. One or the other member variable is to be set.
 * 
 * @author Matt Yanos
 */
public class SpriteCharacterKey
{
    /**
     * The character this represents
     */
    private char character;

    /**
     * Whether the character falls outside of the inclusive ASCII range 32-127
     */
    private boolean extended;

    /**
     * The emoji this character represents
     */
    private LazyLoadEmoji emoji;

    /**
     * Background color override for emoji, for handling variable FFZ badge colors (bot is default gray, but changes to
     * green if the bot is also a moderator)
     */
    private Color emojiBgColorOverride;

    /**
     * Whether the emoji set is a badge image
     */
    private boolean badge;

    /**
     * Construct as a character
     * 
     * @param character
     */
    public SpriteCharacterKey(char character)
    {
        this(character, null, false);
    }

    /**
     * Construct as an emoji
     * 
     * @param emoji
     */
    public SpriteCharacterKey(LazyLoadEmoji emoji, boolean badge)
    {
        this((char) 127, emoji, badge);
    }

    /**
     * Private constructor to ensure that one or the other parameter is marked as unused
     * 
     * @param character
     * @param emoji
     * @param badge
     *            Only used for emoji
     */
    private SpriteCharacterKey(char character, LazyLoadEmoji emoji, boolean badge)
    {
        this.character = character;
        this.extended = !SpriteFont.NORMAL_ASCII_KEY.contains(Character.toString(this.character));
        this.emoji = emoji;
        this.badge = badge;
    }

    /**
     * Get the character, assuming this represents a character
     * 
     * @return char
     */
    public char getChar()
    {
        return character;
    }

    /**
     * Get the emoji, assuming this represents an emoji
     * 
     * @return emoji
     */
    public LazyLoadEmoji getEmoji()
    {
        return emoji;
    }

    /**
     * Whether this sprite character is a char, not an emoji
     * 
     * @return isChar
     */
    public boolean isChar()
    {
        return emoji == null;
    }

    /**
     * Whether this sprite character is an emoji, not a char
     * 
     * @return isNotChar
     */
    public boolean isEmoji()
    {
        return !isChar() && !badge;
    }

    /**
     * Whether this sprite character is a badge emoji, not a char
     * 
     * @return
     */
    public boolean isBadge()
    {
        return !isChar() & badge;
    }

    @Override
    public String toString()
    {
        return isEmoji() ? "[E]" : Character.toString(getChar());
    }

    /**
     * @return
     */
    public boolean isExtended()
    {
        return extended;
    }

    public Color getEmojiBgColor()
    {
        if (emojiBgColorOverride != null)
        {
            return emojiBgColorOverride;
        }

        return emoji == null ? null : emoji.getBgColor();
    }

    public void setEmojiBgColorOverride(Color emojiColorOverride)
    {
        this.emojiBgColorOverride = emojiColorOverride;
    }

}
