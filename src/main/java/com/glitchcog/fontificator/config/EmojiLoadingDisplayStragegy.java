package com.glitchcog.fontificator.config;

/**
 * How to handle when an emoji isn't yet loaded
 * 
 * @author Matt Yanos
 */
public enum EmojiLoadingDisplayStragegy
{
    // @formatter:off
    UNKNOWN("Display the specified unknown character"),
    SPACE("Display a space of the expected image size"), 
    NOTHING("Do not display anything"), 
    BOX_FRAME("Display the frame of a box"), 
    BOX_FILL("Display a filled box");
    // @formatter:on

    /**
     * The text to display representing the type in the dropdown menu
     */
    private String label;

    private EmojiLoadingDisplayStragegy(String label)
    {
        this.label = label;
    }

    public String toString()
    {
        return label;
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
}
