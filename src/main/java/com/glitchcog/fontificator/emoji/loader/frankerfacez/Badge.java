package com.glitchcog.fontificator.emoji.loader.frankerfacez;

import java.awt.Color;

import org.apache.log4j.Logger;

/**
 * Badge model returned by the FrankerFaceZ badge API
 */
public class Badge
{
    private static final Logger logger = Logger.getLogger(Badge.class);

    private String alphaImage;

    private String color;

    private String css;

    private Integer id;

    private String image;

    private String name;

    private String replaces;

    private Integer slot;

    private String title;

    public String getAlphaImage()
    {
        return alphaImage;
    }

    public void setAlphaImage(String alphaImage)
    {
        this.alphaImage = alphaImage;
    }

    public String getColor()
    {
        return color;
    }

    private static final String[] HEX_TEXT = new String[] { "#", "0x" };

    public Color getColorParsed()
    {
        try
        {
            color = color.trim();
            for (int i = 0; i < HEX_TEXT.length; i++)
            {
                if (color.startsWith(HEX_TEXT[i]))
                {
                    color = color.substring(HEX_TEXT[i].length());
                }
            }
            Integer i = Integer.parseInt(color, 16);
            return new Color(i);
        }
        catch (Exception e)
        {
            logger.debug("Unable to parse Color from \"" + color + "\"", e);
            return null;
        }
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public String getCss()
    {
        return css;
    }

    public void setCss(String css)
    {
        this.css = css;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getReplaces()
    {
        return replaces;
    }

    public void setReplaces(String replaces)
    {
        this.replaces = replaces;
    }

    public Integer getSlot()
    {
        return slot;
    }

    public void setSlot(Integer slot)
    {
        this.slot = slot;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}