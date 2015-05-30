package com.glitchcog.fontificator.config;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * The configuration for the Colors
 * 
 * @author Matt Yanos
 */
public class ConfigColor extends Config
{
    private static final Logger logger = Logger.getLogger(ConfigColor.class);

    private Color bgColor;

    private Color fgColor;

    private Color borderColor;

    private Color highlight;

    private Color chromaColor;

    private List<Color> palette;

    private Boolean colorUsername;

    private Boolean colorTimestamp;

    private Boolean colorMessage;

    private Boolean colorJoin;

    @Override
    public void reset()
    {
        this.bgColor = null;
        this.fgColor = null;
        this.borderColor = null;
        this.highlight = null;
        this.chromaColor = null;
        this.palette = null;
        this.colorUsername = null;
        this.colorTimestamp = null;
        this.colorMessage = null;
        this.colorJoin = null;
    }

    private void validateStrings(List<String> errors, String palStr, String userBool, String timeBool, String msgBool, String joinBool)
    {
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_BG, errors);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_FG, errors);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_BORDER, errors);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_HIGHLIGHT, errors);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_CHROMA_KEY, errors);

        validateBooleanStrings(errors, userBool, timeBool, msgBool, joinBool);

        // An empty palette is allowed
        if (!palStr.trim().isEmpty())
        {
            String[] palColStrs = palStr.split(",");
            for (int i = 0; i < palColStrs.length; i++)
            {
                evaluateColorString(palColStrs[i], errors);
            }
        }
    }

    @Override
    public List<String> load(Properties props, List<String> errors)
    {
        logger.trace("Loading config color via raw properties object");

        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.COLOR_KEYS, errors);

        if (errors.isEmpty())
        {
            final String paletteStr = props.getProperty(FontificatorProperties.KEY_COLOR_PALETTE);

            final String userBool = props.getProperty(FontificatorProperties.KEY_COLOR_USERNAME);
            final String timeBool = props.getProperty(FontificatorProperties.KEY_COLOR_TIMESTAMP);
            final String msgBool = props.getProperty(FontificatorProperties.KEY_COLOR_MESSAGE);
            final String joinBool = props.getProperty(FontificatorProperties.KEY_COLOR_JOIN);

            // Check that the values are valid
            validateStrings(errors, paletteStr, userBool, timeBool, msgBool, joinBool);

            // Fill the values
            if (errors.isEmpty())
            {
                bgColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_BG, errors);
                fgColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_FG, errors);
                borderColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_BORDER, errors);
                highlight = evaluateColorString(props, FontificatorProperties.KEY_COLOR_HIGHLIGHT, errors);
                chromaColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_CHROMA_KEY, errors);

                palette = new ArrayList<Color>();
                String[] palColStrs = paletteStr.split(",");
                for (int i = 0; i < palColStrs.length; i++)
                {
                    Color palAddition = evaluateColorString(palColStrs[i], errors);
                    if (palAddition != null)
                    {
                        palette.add(palAddition);
                    }
                }

                colorUsername = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_USERNAME, errors);
                colorTimestamp = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_TIMESTAMP, errors);
                colorMessage = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_MESSAGE, errors);
                colorJoin = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_JOIN, errors);
            }
        }

        return errors;
    }

    public Color getBgColor()
    {
        return bgColor;
    }

    public void setBgColor(Color bgColor)
    {
        this.bgColor = bgColor;
        props.setProperty(FontificatorProperties.KEY_COLOR_BG, getColorHex(bgColor));
    }

    public Color getFgColor()
    {
        return fgColor;
    }

    public void setFgColor(Color fgColor)
    {
        this.fgColor = fgColor;
        props.setProperty(FontificatorProperties.KEY_COLOR_FG, getColorHex(fgColor));
    }

    public Color getBorderColor()
    {
        return borderColor;
    }

    public void setBorderColor(Color borderColor)
    {
        this.borderColor = borderColor;
        props.setProperty(FontificatorProperties.KEY_COLOR_BORDER, getColorHex(borderColor));
    }

    public Color getHighlight()
    {
        return highlight;
    }

    public void setHighlight(Color highlight)
    {
        this.highlight = highlight;
        props.setProperty(FontificatorProperties.KEY_COLOR_HIGHLIGHT, getColorHex(highlight));
    }

    public Color getChromaColor()
    {
        return chromaColor;
    }

    public void setChromaColor(Color chromaColor)
    {
        this.chromaColor = chromaColor;
        props.setProperty(FontificatorProperties.KEY_COLOR_CHROMA_KEY, getColorHex(chromaColor));
    }

    public List<Color> getPalette()
    {
        return palette;
    }

    public void setPalette(List<Color> palette)
    {
        this.palette = palette;
        String paletteString = "";
        for (int i = 0; i < palette.size(); i++)
        {
            paletteString += (i == 0 ? "" : ",") + getColorHex(palette.get(i));
        }
        props.setProperty(FontificatorProperties.KEY_COLOR_PALETTE, paletteString);
    }

    public Boolean isColorUsername()
    {
        return colorUsername;
    }

    public void setColorUsername(Boolean colorUsername)
    {
        this.colorUsername = colorUsername;
        props.setProperty(FontificatorProperties.KEY_COLOR_USERNAME, Boolean.toString(colorUsername));
    }

    public Boolean isColorTimestamp()
    {
        return colorTimestamp;
    }

    public void setColorTimestamp(Boolean colorTimestamp)
    {
        this.colorTimestamp = colorTimestamp;
        props.setProperty(FontificatorProperties.KEY_COLOR_TIMESTAMP, Boolean.toString(colorTimestamp));
    }

    public Boolean isColorMessage()
    {
        return colorMessage;
    }

    public void setColorMessage(Boolean colorMessage)
    {
        this.colorMessage = colorMessage;
        props.setProperty(FontificatorProperties.KEY_COLOR_MESSAGE, Boolean.toString(colorMessage));
    }

    public Boolean isColorJoin()
    {
        return colorJoin;
    }

    public void setColorJoin(Boolean colorJoin)
    {
        this.colorJoin = colorJoin;
        props.setProperty(FontificatorProperties.KEY_COLOR_JOIN, Boolean.toString(colorJoin));
    }

    public static String getColorHex(Color c)
    {
        return String.format("%06X", (0xFFFFFF & c.getRGB()));
    }
}
