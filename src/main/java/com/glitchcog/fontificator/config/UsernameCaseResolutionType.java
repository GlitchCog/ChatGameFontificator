package com.glitchcog.fontificator.config;

/**
 * The way to resolve username casing (capitalization)
 * 
 * @author Matt Yanos
 */
public enum UsernameCaseResolutionType
{
    // @formatter:off
    NONE("Do not modify the capitalization of usernames"), 
    FIRST("Only the first username letter is capitalized"), 
    ALL_LOWERCASE("All username letters are lowercase"), 
    ALL_CAPS("All username letters are capitalized"), 
    LOOKUP("Look up capitalization with call to Twitch API (irc.twitch.tv only)");
    // @formatter:on

    /**
     * The text to display representing the type in the dropdown menu
     */
    private String label;

    private UsernameCaseResolutionType(String label)
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
