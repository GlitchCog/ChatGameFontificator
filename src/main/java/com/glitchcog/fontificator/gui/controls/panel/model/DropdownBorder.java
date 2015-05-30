package com.glitchcog.fontificator.gui.controls.panel.model;

import com.glitchcog.fontificator.config.ConfigFont;

/**
 * Simple class to house and generate the border filename using the constants
 * 
 * @author Matt Yanos
 */
public class DropdownBorder
{
    private final String borderFilename;

    public DropdownBorder(String borderFilename)
    {
        this.borderFilename = borderFilename;
    }

    public String getBorderFilename()
    {
        return ConfigFont.INTERNAL_FILE_PREFIX + ConfigFont.INTERNAL_BORDER_DIR + borderFilename;
    }

}
