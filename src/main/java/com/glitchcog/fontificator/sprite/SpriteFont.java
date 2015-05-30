package com.glitchcog.fontificator.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.bot.MessageType;
import com.glitchcog.fontificator.config.ConfigColor;
import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.config.FontType;

/**
 * A font that is drawn with a sprite
 * 
 * @author Matt Yanos
 */
public class SpriteFont
{
    private static final Logger logger = Logger.getLogger(SpriteFont.class);

    public static final String NORMAL_ASCII_KEY = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~" + (char) 127;

    protected SpriteCache sprites;

    protected Map<Character, Rectangle> characterBounds;

    /**
     * Characters that can be line breaks for wrapping to the next line
     */
    protected static final String WORD_BREAKS = " \n\r\t";

    /**
     * Characters that indicate a return to the start of the next line
     */
    protected static final String LINE_BREAKS = "\n";

    /**
     * Shift the messages down this many lines
     */
    protected int lineScrollOffset;

    protected ConfigFont config;

    public SpriteFont(ConfigFont config)
    {
        logger.trace("Creating sprite font using config font filename " + (config == null ? "null" : config.getFontFilename()));
        this.config = config;
        this.characterBounds = new HashMap<Character, Rectangle>();
        this.sprites = new SpriteCache(config);
    }

    /**
     * Return how wide a character is- must take scale into consideration
     * 
     * @param c
     * @return character width
     */
    public int getCharacterWidth(char c)
    {
        return (getCharacterBounds(c).width + config.getCharSpacing()) * config.getFontScale();
    }

    public void calculateCharacterDimensions()
    {
        // Start from stratch
        characterBounds.clear();

        // For fixed width, just put the same sized box for all characters. The
        // only difference is the location on the sprite grid
        switch (config.getFontType())
        {
        case FIXED_WIDTH:
            calculateFixedCharacterDimensions();
            break;
        case VARIABLE_WIDTH:
            calculateVariableCharacterDimensions();
            break;
        default:
            logger.error("Unknown font type: " + config.getFontType());
            break;
        }
    }

    private void calculateFixedCharacterDimensions()
    {
        final int spriteWidth = sprites.getSprite(config).getSpriteWidth();
        final int spriteHeight = sprites.getSprite(config).getSpriteHeight();

        for (int i = 0; i < config.getCharacterKey().length(); i++)
        {
            final char c = config.getCharacterKey().charAt(i);
            final int index = config.getCharacterKey().indexOf(c);
            final int gridX = index % config.getGridWidth();
            final int gridY = index / config.getGridWidth();
            Rectangle bounds = new Rectangle();
            bounds.setLocation(gridX * spriteWidth, gridY * spriteHeight);
            bounds.setSize(spriteWidth, spriteHeight);
            characterBounds.put(c, bounds);
        }
    }

    /**
     * Go through each character and determine how wide it is based on the non transparent pixels
     */
    private void calculateVariableCharacterDimensions()
    {
        logger.trace("Calculating character dimensions");
        characterBounds.clear();

        Sprite sprite = sprites.getSprite(config);

        final int charWidth = sprite.getSpriteWidth();
        final int charHeight = sprite.getSpriteHeight();
        final int wholeWidth = sprite.getImage().getWidth();
        final int wholeHeight = sprite.getImage().getHeight();

        final String key = config.getCharacterKey();

        int[] pixelData = new int[wholeWidth * wholeHeight];
        pixelData = sprite.getImage().getRGB(0, 0, wholeWidth, wholeHeight, pixelData, 0, wholeWidth);
        int letterIndex = 0;
        // For each grid row
        for (int y = 0; y < wholeHeight; y += charHeight)
        {
            // For each grid col
            for (int x = 0; x < wholeWidth; x += charWidth)
            {
                final char ckey = key.charAt(letterIndex);

                boolean leftEdgeFound = false;
                int leftEdge = 0;
                int rightEdge = 0;

                // Go through each column in the letter cell
                for (int localX = 0; localX < charWidth; localX++)
                {
                    int absoluteX = localX + x;
                    // Go down the whole column looking for opaque pixels
                    boolean opaquePixelFound = false;
                    for (int absoluteY = y; absoluteY < y + charHeight && !opaquePixelFound; absoluteY++)
                    {
                        // If the alpha component of the pixel RGB integer
                        // is greater than zero, then the pixel counts
                        // towards the width of the character
                        opaquePixelFound = ((pixelData[absoluteX + absoluteY * wholeWidth] >> 24) & 0xff) > 0;
                    }

                    if (opaquePixelFound)
                    {
                        // Set left only the first time an opaque pixel is
                        // found
                        if (!leftEdgeFound)
                        {
                            leftEdge = localX;
                            leftEdgeFound = true;
                        }
                        // Keep setting the right each time to expand the
                        // width
                        rightEdge = localX;
                    }
                }

                // Add one pixel for the space between characters, and take
                // the
                // minimum of the calculated width and the max charWidth
                // just in case

                rightEdge++;

                final int letterWidth = Math.min(charWidth, rightEdge - leftEdge);

                Rectangle bounds = new Rectangle(x + leftEdge, y, letterWidth, charHeight);

                // If the character is a space and the bounds calculated to be nothing, meaning there were no
                // opaque pixels found, then make it a default quarter of the sprite width
                if (!leftEdgeFound)
                {
                    // Then just use a quarter of the character width
                    characterBounds.put(ckey, new Rectangle(x + charWidth / 4, y, charWidth / 2, charHeight));
                }
                // For all other characters, or for spaces that have some non transparent pixels, use the calculated
                // bounds
                else
                {
                    characterBounds.put(ckey, bounds);
                }

                letterIndex++;
            }
        }
    }

    /**
     * Get the bounding box for the character in the sprite font image (does not use scale at all)
     * 
     * @param c
     * @return bounding box
     */
    public Rectangle getCharacterBounds(char c)
    {
        if (!config.getCharacterKey().contains(Character.toString(c)))
        {
            c = config.getUnknownChar();
        }

        if (FontType.VARIABLE_WIDTH.equals(config.getFontType()) && c == ' ')
        {
            Rectangle spaceBounds = characterBounds.get(c);
            spaceBounds.width = (int) (sprites.getSprite(config).getSpriteWidth() * (config.getSpaceWidth() / 100.0f));
        }

        return characterBounds.get(c);
    }

    /**
     * Should be called if the ConfigFont object is updated
     */
    public void updateForConfigChange()
    {
        calculateCharacterDimensions();
    }

    /**
     * Get the distance in pixels from the top of one line of text to the top of the next line of text
     */
    public int getLineHeight()
    {
        return (int) ((sprites.getSprite(config).pixelHeight + config.getLineSpacing()) * config.getFontScale());
    }

    public int getLineScrollOffset()
    {
        return lineScrollOffset;
    }

    public void setLineScrollOffset(int lineScrollOffset)
    {
        this.lineScrollOffset = lineScrollOffset;
    }

    /**
     * Change the line scroll offset by the specified amount if it falls within or up/down to the specified min and max
     * values
     * 
     * @param delta
     *            The amount to try to change the line scroll offset
     * @param min
     *            The smallest the line scroll offset should be
     * @param max
     *            The largest the line scroll offset should be
     */
    public void setLineScrollOffset(int delta, int min, int max)
    {
        final int test = lineScrollOffset + delta;
        if (test >= min && test < max)
        {
            lineScrollOffset = test;
        }
    }

    /**
     * Do a mock drawing of the string to determine the dimensions of the bounding box that would surround the drawn
     * message
     * 
     * @param message
     * @param messageConfig
     * @param lineWrapLength
     * @return
     */
    public Dimension getMessageDimensions(Message message, ConfigMessage messageConfig, int lineWrapLength)
    {
        return drawMessage(null, message, null, null, messageConfig, 0, 0, 0, 0, lineWrapLength);
    }

    /**
     * @param g2d
     *            The graphics object upon which to draw
     * @param msg
     *            The message to draw
     * @param userColor
     *            The color unique to the sender of the message being drawn
     * @param colorConfig
     *            The configuration for how to color messages
     * @param messageConfig
     *            The configuration for how to draw messages
     * @param x_init
     *            The left edge x coordinate to start drawing from
     * @param y_init
     *            The top edge y coordinate to start drawing from (probably up in negative space above the graphics
     *            object)
     * @param edgeThickness
     *            When to start drawing lines as y increases, because many will be off screen or under the top order
     * @param bottomEdgeY
     *            Only draw up to this edge
     * @param lineWrapLength
     *            How long to let the text go to the right before going to a new line
     * @return The size of the bounding box of the drawn message
     */
    public Dimension drawMessage(Graphics2D g2d, Message msg, Color userColor, ConfigColor colorConfig, ConfigMessage messageConfig, int x_init, int y_init, int edgeThickness, int bottomEdgeY, int lineWrapLength)
    {
        if (msg.isJoinType() && !messageConfig.showJoinMessages())
        {
            return new Dimension();
        }

        String text = msg.getText(messageConfig);

        int maxCharWidth = 0;
        for (int c = 0; c < text.length(); c++)
        {
            maxCharWidth = Math.max(maxCharWidth, getCharacterWidth(text.charAt(c)));
        }
        if (maxCharWidth > lineWrapLength)
        {
            return new Dimension();
        }

        // Because the letters are set back by this amount to divide up the
        // spacing between their left and right sides
        x_init -= config.getCharSpacing() / 2;

        int x = x_init;
        int y = y_init;

        y += lineScrollOffset * getLineHeight();

        int maxWidth = 0;
        int width = 0;
        int height = getLineHeight();

        boolean forcedBreak = false;

        Color color = Color.WHITE;

        // Go through each character in the text
        for (int c = 0; c < text.length(); c++)
        {
            if (colorConfig != null)
            {
                color = getFontColor(msg, c, messageConfig, colorConfig, userColor);
            }

            // If the character is a line return, go to the next line
            if (LINE_BREAKS.contains(String.valueOf(text.charAt(c))))
            {
                x = x_init;
                maxWidth = Math.max(maxWidth, width);
                width = 0;
                y += getLineHeight();
                if (c < msg.getDrawCursor())
                {
                    height += getLineHeight();
                }
            }
            // If it's not a line return, look forward into the text to find if
            // the next word fits
            else if (WORD_BREAKS.contains(String.valueOf(text.charAt(c))))
            {
                int charWidth = getCharacterWidth(text.charAt(c));
                x += charWidth;
                width += charWidth;
                forcedBreak = false;
            }
            else
            {
                final String currentWord = text.substring(c);
                int currentWordPixelWidth = 0;
                for (int nwc = 0; nwc < currentWord.length() && !WORD_BREAKS.contains(String.valueOf(currentWord.charAt(nwc))); nwc++)
                {
                    currentWordPixelWidth += getCharacterWidth(text.charAt(c + nwc));
                }

                int distanceAlreadyFilled = x - x_init;

                // The next word fits
                if (distanceAlreadyFilled + currentWordPixelWidth < lineWrapLength)
                {
                    if (g2d != null && y >= edgeThickness && y < bottomEdgeY && c < msg.getDrawCursor())
                    {
                        drawCharacter(g2d, text.charAt(c), x, y, color);
                    }
                    int charWidth = getCharacterWidth(text.charAt(c));
                    x += charWidth;
                    width += charWidth;
                }
                // The next word doesn't fit, but it doesn't exceed the length
                // of a full line, so hit return
                else if (!forcedBreak && currentWordPixelWidth < lineWrapLength)
                {
                    x = x_init;
                    maxWidth = Math.max(maxWidth, width);
                    width = 0;
                    y += getLineHeight();
                    if (c < msg.getDrawCursor())
                    {
                        height += getLineHeight();
                    }
                    if (g2d != null && y >= edgeThickness && y < bottomEdgeY && c < msg.getDrawCursor())
                    {
                        drawCharacter(g2d, text.charAt(c), x, y, color);
                    }
                    int charWidth = getCharacterWidth(text.charAt(c));
                    x += charWidth;
                    width += charWidth;
                }
                // The next word doesn't even fit on its own line, so it needs a
                // forced break at the end of the line
                else
                {
                    forcedBreak = true;
                    distanceAlreadyFilled = x - x_init;
                    final int remainderOfTheLine = lineWrapLength - distanceAlreadyFilled;
                    int charWidth = getCharacterWidth(text.charAt(c));
                    if (charWidth > remainderOfTheLine)
                    {
                        x = x_init;
                        maxWidth = Math.max(maxWidth, width);
                        width = 0;
                        y += getLineHeight();
                        if (c < msg.getDrawCursor())
                        {
                            height += getLineHeight();
                        }
                    }

                    if (g2d != null && y >= edgeThickness && y < bottomEdgeY && c < msg.getDrawCursor())
                    {
                        drawCharacter(g2d, text.charAt(c), x, y, color);
                    }
                    x += charWidth;
                    width += charWidth;
                }
            }

        }
        return new Dimension(maxWidth, height);
    }

    private void drawCharacter(Graphics2D g2d, char c, int x, int y, Color color)
    {
        if (!characterBounds.containsKey(c))
        {
            c = config.getUnknownChar();
        }
        Rectangle bounds = characterBounds.get(c);
        sprites.getSprite(config).draw(g2d, x + config.getCharSpacing() / 2, y, bounds.width, bounds.height, bounds, config.getFontScale(), color);
    }

    /**
     * If the change to the next character requires a change to the color of the text, this method will set the
     * appropriate color
     * 
     * @param msg
     * @param c
     * @param messageConfig
     * @param colorConfig
     * @param userColor
     */
    public Color getFontColor(Message msg, int c, ConfigMessage messageConfig, ConfigColor colorConfig, Color userColor)
    {
        boolean timestampIndexEncountered = messageConfig.showTimestamps() && c < msg.getIndexTimestamp(messageConfig);

        if (msg.isJoinType())
        {
            if (timestampIndexEncountered)
            {
                return colorConfig.isColorJoin() && colorConfig.isColorTimestamp() ? colorConfig.getHighlight() : colorConfig.getFgColor();
            }
            else
            {
                return colorConfig.isColorJoin() ? colorConfig.getHighlight() : colorConfig.getFgColor();
            }
        }
        else
        {
            if (timestampIndexEncountered)
            {
                return colorConfig.isColorTimestamp() ? userColor : colorConfig.getFgColor();
            }
            else if (messageConfig.showUsernames() && c < msg.getIndexUsername(messageConfig))
            {
                return colorConfig.isColorUsername() ? userColor : colorConfig.getFgColor();
            }
            else if (colorConfig.isColorMessage() || MessageType.ACTION.equals(msg.getType()))
            {
                return userColor;
            }
            else
            {
                return colorConfig.getFgColor();
            }
        }
    }

}
