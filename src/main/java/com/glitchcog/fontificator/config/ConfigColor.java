package com.glitchcog.fontificator.config;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;

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

    private void validateStrings(LoadConfigReport report, String palStr, String userBool, String timeBool, String msgBool, String joinBool)
    {
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_BG, report);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_FG, report);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_BORDER, report);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_HIGHLIGHT, report);
        evaluateColorString(props, FontificatorProperties.KEY_COLOR_CHROMA_KEY, report);

        validateBooleanStrings(report, userBool, timeBool, msgBool, joinBool);

        // An empty palette is allowed
        if (!palStr.trim().isEmpty())
        {
            String[] palColStrs = palStr.split(",");
            for (int i = 0; i < palColStrs.length; i++)
            {
                evaluateColorString(palColStrs[i], report);
            }
        }
    }

    @Override
    public LoadConfigReport load(Properties props, LoadConfigReport report)
    {
        logger.trace("Loading config color via raw properties object");

        this.props = props;

        reset();

        // Check that the values exist
        baseValidation(props, FontificatorProperties.COLOR_KEYS_WITHOUT_PALETTE, report);

        if (report.isErrorFree())
        {
            final String paletteStr = props.getProperty(FontificatorProperties.KEY_COLOR_PALETTE);

            final String userBool = props.getProperty(FontificatorProperties.KEY_COLOR_USERNAME);
            final String timeBool = props.getProperty(FontificatorProperties.KEY_COLOR_TIMESTAMP);
            final String msgBool = props.getProperty(FontificatorProperties.KEY_COLOR_MESSAGE);
            final String joinBool = props.getProperty(FontificatorProperties.KEY_COLOR_JOIN);

            // Check that the values are valid
            validateStrings(report, paletteStr, userBool, timeBool, msgBool, joinBool);

            // Fill the values
            if (report.isErrorFree())
            {
                bgColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_BG, report);
                fgColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_FG, report);
                borderColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_BORDER, report);
                highlight = evaluateColorString(props, FontificatorProperties.KEY_COLOR_HIGHLIGHT, report);
                chromaColor = evaluateColorString(props, FontificatorProperties.KEY_COLOR_CHROMA_KEY, report);

                palette = new ArrayList<Color>();
                String[] palColStrs = paletteStr.isEmpty() ? new String[] {} : paletteStr.split(",");
                for (int i = 0; i < palColStrs.length; i++)
                {
                    Color palAddition = evaluateColorString(palColStrs[i], report);
                    if (palAddition != null)
                    {
                        palette.add(palAddition);
                    }
                }

                colorUsername = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_USERNAME, report);
                colorTimestamp = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_TIMESTAMP, report);
                colorMessage = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_MESSAGE, report);
                colorJoin = evaluateBooleanString(props, FontificatorProperties.KEY_COLOR_JOIN, report);
            }
        }

        return report;
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
        return palette == null ? new ArrayList<Color>() : palette;
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
