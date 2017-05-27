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
    private int codepoint;

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

    public SpriteCharacterKey(int codepoint)
    {
        this(codepoint, null, false);
    }

    /**
     * Construct as an emoji
     * 
     * @param emoji
     */
    public SpriteCharacterKey(LazyLoadEmoji emoji, boolean badge)
    {
        this(127, emoji, badge);
    }

    /**
     * Private constructor to ensure that one or the other parameter is marked as unused
     * 
     * @param codepoint
     * @param emoji
     * @param badge
     *            Only used for emoji
     */
    private SpriteCharacterKey(int codepoint, LazyLoadEmoji emoji, boolean badge)
    {
        this.codepoint = codepoint;
        /* not-great: Take code point and toss surrogate pair, if present, to check if in ascii set */
        this.extended = !SpriteFont.NORMAL_ASCII_KEY.contains(Character.toString(Character.toChars(this.codepoint)[0]));
        this.emoji = emoji;
        this.badge = badge;
    }

    /**
     * Get the character as a char, assuming this represents a character instead of emoji
     * 
     * @return char
     */
    public char getChar()
    {
        // Not a pretty compromise.  Basically get the broken half a code point when trying to getChar() something that
        // isn't in the BMP.  This is mostly OK.
        // Ideally this would throw an exception if !Character.isBmpCodePoint(this.codepoint)
        return Character.toChars(this.codepoint)[0];
    }

    /**
     * Get the character as a codepoint, assuming this represents a character instead of emoji
     *
     * @return int
     */
    public int getCodepoint()
    {
        return this.codepoint;
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
     * Whether this sprite character is a character, not an emoji
     * 
     * @return isChar
     */
    public boolean isChar()
    {
        return emoji == null;
    }

    /**
     * Whether this sprite character is an emoji, not a character
     * 
     * @return isNotChar
     */
    public boolean isEmoji()
    {
        return !isChar() && !badge;
    }

    /**
     * Whether this sprite character is a badge emoji, not a character
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
        return isEmoji() ? "[E]" : new String(Character.toChars(this.codepoint));
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
