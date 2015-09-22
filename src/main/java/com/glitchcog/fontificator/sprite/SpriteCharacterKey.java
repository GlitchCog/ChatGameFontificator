package com.glitchcog.fontificator.sprite;

import com.glitchcog.fontificator.emoji.LazyLoadEmoji;

/**
 * Replacement for char that can also represent an emoji. One or the other member variable is to be set.
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
     * The emoji this character represents
     */
    private LazyLoadEmoji[] emoji;

    /**
     * Construct as a character
     * 
     * @param character
     */
    public SpriteCharacterKey(char character)
    {
        this(character, null);
    }

    /**
     * Construct as an emoji
     * 
     * @param emoji
     */
    public SpriteCharacterKey(LazyLoadEmoji[] emoji)
    {
        this((char) 127, emoji);
    }

    /**
     * Private constructor to ensure that one or the other parameter is marked as unused
     * 
     * @param character
     * @param emoji
     */
    private SpriteCharacterKey(char character, LazyLoadEmoji[] emoji)
    {
        this.character = character;
        this.emoji = emoji;
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
        // Sometimes multiple frames represent frames of animation, other times it's the subscriber versions of globals
        // For now, just return the first emoji
        return emoji[0];
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
        return !isChar();
    }

    @Override
    public String toString()
    {
        return isEmoji() ? "[E]" : Character.toString(getChar());
    }
}
