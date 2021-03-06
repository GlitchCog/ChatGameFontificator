package com.glitchcog.fontificator.config;

import java.io.File;
import java.util.Properties;

import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;

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

    /**
     * The size of a step in the scale of a font or border on the sliders in ControlPanelFont
     */
    public static final float FONT_BORDER_SCALE_GRANULARITY = 0.25f;

    public static final float MIN_FONT_SCALE = 0.25f;
    public static final int MAX_FONT_SCALE = (int) (8 / FONT_BORDER_SCALE_GRANULARITY);

    public static final int MIN_BORDER_SCALE = 0;
    public static final int MAX_BORDER_SCALE = (int) (8 / FONT_BORDER_SCALE_GRANULARITY);

    public static final int MIN_BORDER_INSET = -256;
    public static final int MAX_BORDER_INSET = 256;

    public static final int MIN_SPACE_WIDTH = 0;
    public static final int MAX_SPACE_WIDTH = 250;

    public static final int MIN_BASELINE_OFFSET = -32;
    public static final int MAX_BASELINE_OFFSET = 64;

    public static final int MIN_LINE_SPACING = -16;
    public static final int MAX_LINE_SPACING = 32;

    public static final int MIN_CHAR_SPACING = -16;
    public static final int MAX_CHAR_SPACING = 32;

    public static final int MIN_MESSAGE_SPACING = 0;
    public static final int MAX_MESSAGE_SPACING = 128;

    private String fontFilename;

    private String borderFilename;

    private int gridWidth;

    private int gridHeight;

    private float fontScale;

    private float borderScale;

    private int borderInsetX;

    private int borderInsetY;

    private int spaceWidth;

    private int baselineOffset;

    private String characterKey;

    private char unknownChar;

    private Boolean extendedCharEnabled;

    private int lineSpacing;

    private int charSpacing;

    private int messageSpacing;

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
        baselineOffset = 0;
        gridWidth = 0;
        gridHeight = 0;
        fontScale = 1.0f;
        borderScale = 1.0f;
        borderInsetX = 0;
        borderInsetY = 0;
        characterKey = null;
        unknownChar = '\0';
        extendedCharEnabled = null;
        lineSpacing = 0;
        charSpacing = 0;
        messageSpacing = 0;
    }

    public void validateFontFile(LoadConfigReport report, String fontFilename)
    {
        if (fontFilename.startsWith(INTERNAL_FILE_PREFIX))
        {
            final String plainFilename = fontFilename.substring(INTERNAL_FILE_PREFIX.length());
            if (getClass().getClassLoader().getResource(plainFilename) == null)
            {
                report.addError("Preset font " + plainFilename + " not found", LoadConfigErrorType.FILE_NOT_FOUND);
            }
        }
        else if (!new File(fontFilename).exists())
        {
            report.addError("Unable to find font PNG file \"" + fontFilename + "\"", LoadConfigErrorType.FILE_NOT_FOUND);
        }
    }

    public void validateBorderFile(LoadConfigReport report, String borderFilename)
    {
        if (borderFilename.startsWith(INTERNAL_FILE_PREFIX))
        {
            final String plainFilename = borderFilename.substring(INTERNAL_FILE_PREFIX.length());
            if (getClass().getClassLoader().getResource(plainFilename) == null)
            {
                report.addError("Preset border " + plainFilename + " not found", LoadConfigErrorType.FILE_NOT_FOUND);
            }
        }
        else if (!new File(borderFilename).exists())
        {
            report.addError("Unable to find border PNG file \"" + borderFilename + "\"", LoadConfigErrorType.FILE_NOT_FOUND);
        }
    }

    public void validateStrings(LoadConfigReport report, String widthStr, String heightStr, String charKey, String unknownCharStr)
    {
        if (unknownCharStr.length() != 1)
        {
            report.addError("Unknown character value must be a single character", LoadConfigErrorType.PARSE_ERROR_CHAR);
        }

        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_GRID_WIDTH, widthStr, 1, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_GRID_HEIGHT, heightStr, 1, report);

        if (report.isErrorFree())
        {
            int w = Integer.parseInt(widthStr);
            int h = Integer.parseInt(heightStr);
            if (w > 0 && w > 0)
            {
                if (w * h != charKey.length())
                {
                    report.addError("Character key length (" + charKey.length() + ") must match the number of characters in the font image (" + w + " x " + h + " = " + (w * h) + ")", LoadConfigErrorType.VALUE_OUT_OF_RANGE);
                }
            }

            if (!charKey.contains(unknownCharStr))
            {
                report.addError("The value for " + FontificatorProperties.KEY_FONT_UNKNOWN_CHAR + " (" + unknownCharStr + ") must also be in the value for " + FontificatorProperties.KEY_FONT_CHARACTERS, LoadConfigErrorType.VALUE_OUT_OF_RANGE);
            }
        }
    }

    public void validateStrings(LoadConfigReport report, String fontFilenameStr, String borderFilenameStr, String widthStr, String heightStr, String charKey, String unknownCharStr, String extendedCharStr, String scaleStr, String borderScaleStr, String borderInsetXStr, String borderInsetYStr, String spaceWidthStr, String baselineStr, String lineStr, String charStr, String msgStr, String fontTypeStr)
    {
        validateStrings(report, widthStr, heightStr, charKey, unknownCharStr);

        if (fontFilenameStr.isEmpty())
        {
            report.addError("A font filename is required", LoadConfigErrorType.MISSING_VALUE);
        }

        if (borderFilenameStr.isEmpty())
        {
            report.addError("A border filename is required", LoadConfigErrorType.MISSING_VALUE);
        }

        validateBooleanStrings(report, extendedCharStr);

        validateFloatWithLimitString(FontificatorProperties.KEY_FONT_SCALE, scaleStr, MIN_FONT_SCALE, MAX_FONT_SCALE, report);
        validateFloatWithLimitString(FontificatorProperties.KEY_FONT_BORDER_SCALE, borderScaleStr, MIN_BORDER_SCALE, MAX_BORDER_SCALE, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_BORDER_INSET_X, borderInsetXStr, MIN_BORDER_INSET, MAX_BORDER_INSET, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_BORDER_INSET_Y, borderInsetYStr, MIN_BORDER_INSET, MAX_BORDER_INSET, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SPACE_WIDTH, spaceWidthStr, MIN_SPACE_WIDTH, MAX_SPACE_WIDTH, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_BASELINE_OFFSET, baselineStr, MIN_BASELINE_OFFSET, MAX_BASELINE_OFFSET, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SPACING_LINE, lineStr, MIN_LINE_SPACING, MAX_LINE_SPACING, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SPACING_CHAR, charStr, MIN_CHAR_SPACING, MAX_CHAR_SPACING, report);
        validateIntegerWithLimitString(FontificatorProperties.KEY_FONT_SPACING_MESSAGE, msgStr, MIN_MESSAGE_SPACING, MAX_MESSAGE_SPACING, report);

        if (!FontType.contains(fontTypeStr))
        {
            report.addError("Value of key \"" + FontificatorProperties.KEY_FONT_TYPE + "\" is invalid.", LoadConfigErrorType.PARSE_ERROR_ENUM);
        }
    }

    @Override
    public LoadConfigReport load(Properties props, LoadConfigReport report)
    {
        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.FONT_KEYS, report);

        if (report.isErrorFree())
        {
            final String gridWidthStr = props.getProperty(FontificatorProperties.KEY_FONT_GRID_WIDTH);
            final String gridHeightStr = props.getProperty(FontificatorProperties.KEY_FONT_GRID_HEIGHT);
            final String scaleStr = props.getProperty(FontificatorProperties.KEY_FONT_SCALE);
            final String borderScaleStr = props.getProperty(FontificatorProperties.KEY_FONT_BORDER_SCALE);
            final String borderInsetXStr = props.getProperty(FontificatorProperties.KEY_FONT_BORDER_INSET_X);
            final String borderInsetYStr = props.getProperty(FontificatorProperties.KEY_FONT_BORDER_INSET_Y);
            final String spaceWidthStr = props.getProperty(FontificatorProperties.KEY_FONT_SPACE_WIDTH);
            final String baselineStr = props.getProperty(FontificatorProperties.KEY_FONT_BASELINE_OFFSET);
            final String unknownCharStr = props.getProperty(FontificatorProperties.KEY_FONT_UNKNOWN_CHAR);
            final String extendedCharStr = props.getProperty(FontificatorProperties.KEY_FONT_EXTENDED_CHAR);
            final String lineSpacingStr = props.getProperty(FontificatorProperties.KEY_FONT_SPACING_LINE);
            final String charSpacingStr = props.getProperty(FontificatorProperties.KEY_FONT_SPACING_CHAR);
            final String messageSpacingStr = props.getProperty(FontificatorProperties.KEY_FONT_SPACING_MESSAGE);
            final String fontTypeStr = props.getProperty(FontificatorProperties.KEY_FONT_TYPE);
            final String charKeyStr = props.getProperty(FontificatorProperties.KEY_FONT_CHARACTERS);

            final String borderFilenameStr = props.getProperty(FontificatorProperties.KEY_FONT_FILE_BORDER);
            final String fontFilenameStr = props.getProperty(FontificatorProperties.KEY_FONT_FILE_FONT);

            // Check that the values are valid
            validateStrings(report, fontFilenameStr, borderFilenameStr, gridWidthStr, gridHeightStr, charKeyStr, unknownCharStr, extendedCharStr, scaleStr, borderScaleStr, borderInsetXStr, borderInsetYStr, spaceWidthStr, baselineStr, lineSpacingStr, charSpacingStr, messageSpacingStr, fontTypeStr);

            // Fill the values
            if (report.isErrorFree())
            {
                this.borderFilename = props.getProperty(FontificatorProperties.KEY_FONT_FILE_BORDER);
                this.fontFilename = props.getProperty(FontificatorProperties.KEY_FONT_FILE_FONT);
                this.fontType = FontType.valueOf(props.getProperty(FontificatorProperties.KEY_FONT_TYPE));
                this.unknownChar = unknownCharStr.charAt(0);
                this.extendedCharEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_FONT_EXTENDED_CHAR, report);
                this.characterKey = props.getProperty(FontificatorProperties.KEY_FONT_CHARACTERS);
                this.gridWidth = Integer.parseInt(gridWidthStr);
                this.gridHeight = Integer.parseInt(gridHeightStr);
                this.fontScale = Float.parseFloat(scaleStr);
                this.borderScale = Float.parseFloat(borderScaleStr);
                this.borderInsetX = Integer.parseInt(borderInsetXStr);
                this.borderInsetY = Integer.parseInt(borderInsetYStr);
                this.spaceWidth = Integer.parseInt(spaceWidthStr);
                this.baselineOffset = Integer.parseInt(baselineStr);
                this.lineSpacing = Integer.parseInt(lineSpacingStr);
                this.charSpacing = Integer.parseInt(charSpacingStr);
                this.messageSpacing = Integer.parseInt(messageSpacingStr);
            }
        }

        return report;
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

    public float getFontScale()
    {
        return fontScale;
    }

    public void setFontScale(float fontScale)
    {
        this.fontScale = fontScale;
        props.setProperty(FontificatorProperties.KEY_FONT_SCALE, Float.toString(fontScale));
    }

    public float getBorderScale()
    {
        return borderScale;
    }

    public void setBorderScale(float borderScale)
    {
        this.borderScale = borderScale;
        props.setProperty(FontificatorProperties.KEY_FONT_BORDER_SCALE, Float.toString(borderScale));
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

    public int getBaselineOffset()
    {
        return baselineOffset;
    }

    public void setBaselineOffset(int baselineOffset)
    {
        this.baselineOffset = baselineOffset;
        props.getProperty(FontificatorProperties.KEY_FONT_BASELINE_OFFSET, Integer.toString(baselineOffset));
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

    public boolean isExtendedCharEnabled()
    {
        return extendedCharEnabled;
    }

    public void setExtendedCharEnabled(boolean extendedCharEnabled)
    {
        this.extendedCharEnabled = extendedCharEnabled;
        props.setProperty(FontificatorProperties.KEY_FONT_EXTENDED_CHAR, Boolean.toString(extendedCharEnabled));
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

    public int getMessageSpacing()
    {
        return messageSpacing;
    }

    public void setMessageSpacing(int messageSpacing)
    {
        this.messageSpacing = messageSpacing;
        props.setProperty(FontificatorProperties.KEY_FONT_SPACING_MESSAGE, Integer.toString(messageSpacing));
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
