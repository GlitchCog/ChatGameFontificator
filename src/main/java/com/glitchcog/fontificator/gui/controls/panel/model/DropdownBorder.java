package com.glitchcog.fontificator.gui.controls.panel.model;

import java.awt.Color;

import com.glitchcog.fontificator.config.ConfigFont;

/**
 * Simple class to house and generate the border filename using the constants and house a default tint color
 * 
 * @author Matt Yanos
 */
public class DropdownBorder
{
    private final String borderFilename;

    /**
     * The default color to use to tint this background
     */
    private final Color defaultTint;

    public DropdownBorder(String borderFilename, int defaultTint)
    {
        this(borderFilename, new Color(defaultTint));
    }

    public DropdownBorder(String borderFilename, Color defaultTint)
    {
        this.borderFilename = borderFilename;
        this.defaultTint = defaultTint;
    }

    public String getBorderFilename()
    {
        return ConfigFont.INTERNAL_FILE_PREFIX + ConfigFont.INTERNAL_BORDER_DIR + borderFilename;
    }

    public Color getDefaultTint()
    {
        return defaultTint;
    }
}
