package com.glitchcog.fontificator.config;

/**
 * The type of font
 * 
 * @author Matt Yanos
 */
public enum FontType
{
    FIXED_WIDTH("Fixed-width"), VARIABLE_WIDTH("Variable-width");

    /**
     * The text to display representing the type in the dropdown menu
     */
    private final String label;

    private FontType(String label)
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

    public String getLabel()
    {
        return label;
    }

    public static FontType getByLabel(String label)
    {
        for (FontType type : values())
        {
            if (type.getLabel().equals(label))
            {
                return type;
            }
        }
        return null;
    }

    public String toString()
    {
        return getLabel();
    }
}
