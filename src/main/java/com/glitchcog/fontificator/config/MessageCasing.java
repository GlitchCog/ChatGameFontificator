package com.glitchcog.fontificator.config;

/**
 * Whether to leave casing alone, or to force upper or lower case
 * 
 * @author Matt Yanos
 */
public enum MessageCasing
{
    // @formatter:off
    MIXED_CASE("Mixed casing (No modification)"), 
    UPPERCASE("Uppercase (Caps Lock)"), 
    LOWERCASE("Lowercase (No capital letters)"); 
    // @formatter:on

    private final String label;

    private MessageCasing(String label)
    {
        this.label = label;
    }

    public static boolean contains(String name)
    {
        for (int i = 0; i < values().length; i++)
        {
            if (values()[i].name().equals(name))
            {
                return true;
            }
        }
        return false;
    }

    public String toString()
    {
        return label;
    }
}
