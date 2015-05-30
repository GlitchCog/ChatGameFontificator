package com.glitchcog.fontificator.config;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * The configuration for the Font and Border
 * 
 * @author Matt Yanos
 */
public class ConfigFont extends Config
{
    public static final String INTERNAL_FILE_PREFIX = "preset://";
    public static final String INTERNAL_FONT_DIR = "fonts/";
    public static final String INTERNAL_BORDER_DIR = "borders/";

    public static final int MIN_FONT_SCALE = 1;
    public static final int MAX_FONT_SCALE = 8;

    public static final int MIN_BORDER_SCALE = 0;
    public static final int MAX_BORDER_SCALE = 8;

    public static final int MIN_BORDER_INSET = -256;
    public static final int MAX_BORDER_INSET = 256;

    public static final int MIN_SPACE_WIDTH = 0;
    public static final int MAX_SPACE_WIDTH = 250;

    public static final int MIN_LINE_SPACING = -16;
    public static final int MAX_LINE_SPACING = 32;

    public static final int MIN_CHAR_SPACING = -16;
    public static final int MAX_CHAR_SPACING = 32;

    private String fontFilename;

    private String borderFilename;

    private int gridWidth;

    private int gridHeight;

    private int fontScale;

    private int borderScale;

    private int borderInsetX;

    private int borderInsetY;

    private int spaceWidth;

    private String characterKey;

    private char unknownChar;

    private int lineSpacing;

    private int charSpacing;

    private FontType fontType;

    /**
     * Default constructor for creating an empty config to fill with load method
     */
    public ConfigFont()
    {
    }

    @Override
    public void reset()
    {
        fontFilename = null;
        borderFilename = null;
        fontType = null;
        spaceWidth = 0;
        gridWidth = 0;
        gridHeight = 0;
        fontScale = 1;
        borderScale = 1;
        borderInsetX = 0;
        borderInsetY = 0;
        characterKey = null;
        unknownChar = '\0';
        lineSpacing = 0;
        charSpacing = 0;
    }

    public void validateFontFile(List<String> errors, String fontFilename)
    {
        if (fontFilename.startsWith(INTERNAL_FILE_PREFIX))
        {
            final String plainFilename = fontFilename.substring(INTERNAL_FILE_PREFIX.length());
            if (getClass().getClassLoader().getResource(plainFilename) == null)
            {
                errors.add("Preset font " + plainFilename + " not found");
            }
        }
        else if (!new File(fontFilename).exists())
        {
            errors.add("Unable to find font PNG file \"" + fontFilename + "\"");
        }
    }

    public void validateBorderFile(List<String> errors, String borderFilename)
    {
        if (borderFilename.startsWith(INTERNAL_FILE_PREFIX))
        {
            final String plainFilename = borderFilename.substring(INTERNAL_FILE_PREFIX.length());
            if (getClass().getClassLoader().getResource(plainFilename) == null)
            {
                errors.add("Preset border " + plainFilename + " not found");
            }
        }
        else if (!new File(borderFilename).exists())
        {
            errors.add("Unable to find border PNG file \"" + borderFilename + "\"");
        }
    }


    public void validateStrings(List<String> errors, String widthStr, String heightStr, String charKey, String unknownCharStr)
    {
        if (unknownCharStr.length() != 1)
        {
            errors.add("Unknown character value must be a single character");
        }

        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_GRID_WIDTH, widthStr, 1, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_GRID_HEIGHT, heightStr, 1, errors);

        if (errors.isEmpty())
        {
            int w = Integer.parseInt(widthStr);
            int h = Integer.parseInt(heightStr);
            if (w > 0 && w > 0)
            {
                if (w * h != charKey.length())
                {
                    errors.add("Character key length (" + characterKey.length() + ") must match the number of characters in the font image (" + w + " x " + h + " = " + (w * h) + ")");
                }
            }

            if (!charKey.contains(unknownCharStr))
            {
                errors.add("The value for " + FontificatorProperties.KEY_FONT_UNKNOWN_CHAR + " (" + unknownCharStr + ") must also be in the value for " + FontificatorProperties.KEY_FONT_CHARACTERS);
            }
        }
    }

    public void validateStrings(List<String> errors, 
                                String fontFilenameStr, 
                                String borderFilenameStr, 
                                String widthStr, 
                                String heightStr, 
                                String charKey, 
                                String unknownCharStr, 
                                String scaleStr, 
                                String borderScaleStr, 
                                String borderInsetXStr, 
                                String borderInsetYStr, 
                                String spaceWidthStr, 
                                String lineStr, 
                                String charStr, 
                                String fontTypeStr)
    {
        validateStrings(errors, widthStr, heightStr, charKey, unknownCharStr);

        if (fontFilenameStr.isEmpty())
        {
            errors.add("A font filename is required");
        }

        if (borderFilenameStr.isEmpty())
        {
            errors.add("A border filename is required");
        }

        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SCALE, scaleStr, MIN_FONT_SCALE, MAX_FONT_SCALE, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_BORDER_SCALE, borderScaleStr, MIN_BORDER_SCALE, MAX_BORDER_SCALE, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_BORDER_INSET_X, borderInsetXStr, MIN_BORDER_INSET, MAX_BORDER_INSET, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_BORDER_INSET_Y, borderInsetYStr, MIN_BORDER_INSET, MAX_BORDER_INSET, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SPACE_WIDTH, spaceWidthStr, MIN_SPACE_WIDTH, MAX_SPACE_WIDTH, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SPACING_LINE, lineStr, MIN_LINE_SPACING, MAX_LINE_SPACING, errors);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SPACING_CHAR, charStr, MIN_CHAR_SPACING, MAX_CHAR_SPACING, errors);

        if (!FontType.contains(fontTypeStr))
        {
            errors.add("Value of key \"" + FontificatorProperties.KEY_FONT_TYPE + "\" is invalid.");
        }
    }

    @Override
    public List<String> load(Properties props, List<String> errors)
    {
        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.FONT_KEYS, errors);

        if (errors.isEmpty())
        {
            final String gridWidthStr = props.getProperty(FontificatorProperties.KEY_FONT_GRID_WIDTH);
            final String gridHeightStr = props.getProperty(FontificatorProperties.KEY_FONT_GRID_HEIGHT);
            final String scaleStr = props.getProperty(FontificatorProperties.KEY_FONT_SCALE);
            final String borderScaleStr = props.getProperty(FontificatorProperties.KEY_FONT_BORDER_SCALE);
            final String borderInsetXStr = props.getProperty(FontificatorProperties.KEY_FONT_BORDER_INSET_X);
            final String borderInsetYStr = props.getProperty(FontificatorProperties.KEY_FONT_BORDER_INSET_Y);
            final String spaceWidthStr = props.getProperty(FontificatorProperties.KEY_FONT_SPACE_WIDTH);
            final String unknownCharStr = props.getProperty(FontificatorProperties.KEY_FONT_UNKNOWN_CHAR);
            final String lineSpacingStr = props.getProperty(FontificatorProperties.KEY_FONT_SPACING_LINE);
            final String charSpacingStr = props.getProperty(FontificatorProperties.KEY_FONT_SPACING_CHAR);
            final String fontTypeStr = props.getProperty(FontificatorProperties.KEY_FONT_TYPE);
            final String charKeyStr = props.getProperty(FontificatorProperties.KEY_FONT_CHARACTERS);

            final String borderFilenameStr = props.getProperty(FontificatorProperties.KEY_FONT_FILE_BORDER);
            final String fontFilenameStr = props.getProperty(FontificatorProperties.KEY_FONT_FILE_FONT);

            // Check that the values are valid
            validateStrings(errors,  fontFilenameStr, borderFilenameStr, gridWidthStr, gridHeightStr, charKeyStr, unknownCharStr, scaleStr, borderScaleStr, borderInsetXStr, borderInsetYStr, spaceWidthStr, lineSpacingStr, charSpacingStr, fontTypeStr);

            // Fill the values
            if (errors.isEmpty())
            {
                this.borderFilename = props.getProperty(FontificatorProperties.KEY_FONT_FILE_BORDER);
                this.fontFilename = props.getProperty(FontificatorProperties.KEY_FONT_FILE_FONT);
                this.fontType = FontType.valueOf(props.getProperty(FontificatorProperties.KEY_FONT_TYPE));
                this.unknownChar = unknownCharStr.charAt(0);
                this.characterKey = props.getProperty(FontificatorProperties.KEY_FONT_CHARACTERS);
                this.gridWidth = Integer.parseInt(gridWidthStr);
                this.gridHeight = Integer.parseInt(gridHeightStr);
                this.fontScale = Integer.parseInt(scaleStr);
                this.borderScale = Integer.parseInt(borderScaleStr);
                this.borderInsetX = Integer.parseInt(borderInsetXStr);
                this.borderInsetY = Integer.parseInt(borderInsetYStr);
                this.spaceWidth = Integer.parseInt(spaceWidthStr);
                this.lineSpacing = Integer.parseInt(lineSpacingStr);
                this.charSpacing = Integer.parseInt(charSpacingStr);
            }
        }

        return errors;
    }

    public String getFontFilename()
    {
        return fontFilename;
    }

    public void setFontFilename(String fontFilename)
    {
        this.fontFilename = fontFilename;
        props.setProperty(FontificatorProperties.KEY_FONT_FILE_FONT, fontFilename);
    }

    public String getBorderFilename()
    {
        return borderFilename;
    }

    public void setBorderFilename(String borderFilename)
    {
        this.borderFilename = borderFilename;
        props.setProperty(FontificatorProperties.KEY_FONT_FILE_BORDER, borderFilename);
    }

    public int getGridWidth()
    {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth)
    {
        this.gridWidth = gridWidth;
        props.setProperty(FontificatorProperties.KEY_FONT_GRID_WIDTH, Integer.toString(gridWidth));
    }

    public int getGridHeight()
    {
        return gridHeight;
    }

    public void setGridHeight(int gridHeight)
    {
        this.gridHeight = gridHeight;
        props.setProperty(FontificatorProperties.KEY_FONT_GRID_HEIGHT, Integer.toString(gridHeight));
    }

    public int getFontScale()
    {
        return fontScale;
    }

    public void setFontScale(int fontScale)
    {
        this.fontScale = fontScale;
        props.setProperty(FontificatorProperties.KEY_FONT_SCALE, Integer.toString(fontScale));
    }

    public int getBorderScale()
    {
        return borderScale;
    }

    public void setBorderScale(int borderScale)
    {
        this.borderScale = borderScale;
        props.setProperty(FontificatorProperties.KEY_FONT_BORDER_SCALE, Integer.toString(borderScale));
    }

    public int getBorderInsetX()
    {
        return borderInsetX;
    }

    public void setBorderInsetX(int borderInsetX)
    {
        this.borderInsetX = borderInsetX;
        props.setProperty(FontificatorProperties.KEY_FONT_BORDER_INSET_X, Integer.toString(borderInsetX));
    }

    public int getBorderInsetY()
    {
        return borderInsetY;
    }

    public void setBorderInsetY(int borderInsetY)
    {
        this.borderInsetY = borderInsetY;
        props.setProperty(FontificatorProperties.KEY_FONT_BORDER_INSET_Y, Integer.toString(borderInsetY));
    }

    public int getSpaceWidth()
    {
        return spaceWidth;
    }

    public void setSpaceWidth(int spaceWidth)
    {
        this.spaceWidth = spaceWidth;
        props.setProperty(FontificatorProperties.KEY_FONT_SPACE_WIDTH, Integer.toString(spaceWidth));
    }

    public String getCharacterKey()
    {
        return characterKey;
    }

    public void setCharacterKey(String characterKey)
    {
        this.characterKey = characterKey;
        props.setProperty(FontificatorProperties.KEY_FONT_CHARACTERS, characterKey);
    }

    public char getUnknownChar()
    {
        return unknownChar;
    }

    public void setUnknownChar(char unknownChar)
    {
        this.unknownChar = unknownChar;
        props.setProperty(FontificatorProperties.KEY_FONT_UNKNOWN_CHAR, Character.toString(unknownChar));
    }

    public int getLineSpacing()
    {
        return lineSpacing;
    }

    public void setLineSpacing(int lineSpacing)
    {
        this.lineSpacing = lineSpacing;
        props.setProperty(FontificatorProperties.KEY_FONT_SPACING_LINE, Integer.toString(lineSpacing));
    }

    public int getCharSpacing()
    {
        return charSpacing;
    }

    public void setCharSpacing(int charSpacing)
    {
        this.charSpacing = charSpacing;
        props.setProperty(FontificatorProperties.KEY_FONT_SPACING_CHAR, Integer.toString(charSpacing));
    }

    public FontType getFontType()
    {
        return fontType;
    }

    public void setFontType(FontType fontType)
    {
        this.fontType = fontType;
        props.setProperty(FontificatorProperties.KEY_FONT_TYPE, fontType.name());
    }

}
