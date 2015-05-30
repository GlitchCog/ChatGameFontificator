package com.glitchcog.fontificator.gui.controls.panel.model;

import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.FontType;

/**
 * Simple class to hosue the font filename, generate the full filename with constants, and to house the default type
 * 
 * @author Matt Yanos
 */
public class DropdownFont
{
    private final String fontFilename;

    private final FontType defaultType;

    public DropdownFont(String fontFilename, FontType defaultType)
    {
        this.fontFilename = fontFilename;
        this.defaultType = defaultType;
    }

    public String getFontFilename()
    {
        return ConfigFont.INTERNAL_FILE_PREFIX + ConfigFont.INTERNAL_FONT_DIR + fontFilename;
    }

    public FontType getDefaultType()
    {
        return defaultType;
    }

}
