package com.glitchcog.fontificator.sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.bot.MessageType;
import com.glitchcog.fontificator.config.ConfigColor;
import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.config.FontType;
import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.emoji.LazyLoadEmoji;

/**
 * A font that is drawn with a sprite
 * 
 * @author Matt Yanos
 */
public class SpriteFont
{
    private static final Logger logger = Logger.getLogger(SpriteFont.class);

    public static final String NORMAL_ASCII_KEY = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~" + (char) 127;

    /**
     * The number of pixels between each individual badge and between badges and the username;
     */
    public static final int BADGE_MINIMUM_SPACING_PIXELS = 3;

    protected SpriteCache sprites;

    protected Map<Integer, Rectangle> characterBounds;

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
        this.characterBounds = new HashMap<Integer, Rectangle>();
        this.sprites = new SpriteCache(config);
    }

    /**
     * Get the dimensions of an emoji image
     * 
     * @param img
     * @param emojiConfig
     * @return
     */
    private int[] getEmojiDimensions(LazyLoadEmoji emoji, ConfigEmoji emojiConfig)
    {
        Image img = emoji.getImage(emojiConfig.isAnimationEnabled());

        int iw;
        int ih;

        if (img == null)
        {
            switch (emojiConfig.getDisplayStrategy())
            {
            case SPACE:
            case BOX_FILL:
            case BOX_FRAME:
                iw = emoji.getWidth();
                ih = emoji.getHeight();
                break;
            case UNKNOWN:
                // Do not use the emoji scaling below because it's a character, not an emoji
                // We can pass a null in for the FontMetrics, because we know the unknown character falls within the
                // non-extended range
                return new int[] { getCharacterWidth(null, new SpriteCharacterKey(config.getUnknownChar()), emojiConfig), 1 };
            case NOTHING:
            default:
                iw = 0;
                ih = 1;
                break;
            }
        }
        else
        {
            iw = emoji.getWidth();
            ih = emoji.getHeight();
        }

        float h;
        float w;

        float eScale = emoji.getType().isBadge() ? (emojiConfig.getBadgeScale() / 100.0f) : (emojiConfig.getEmojiScale() / 100.0f);
        if ((emoji.getType().isBadge() && emojiConfig.isBadgeScaleToLine()) || (!emoji.getType().isBadge() && emojiConfig.isEmojiScaleToLine()))
        {
            final float emojiScaleRatio = eScale * getLineHeightScaled() / (float) ih;
            h = ih * emojiScaleRatio;
            w = iw * h / ih;
        }
        else
        {
            w = eScale * iw;
            h = eScale * ih;
        }

        return new int[] { (int) w, (int) h };
    }

    private char[] fontMetricCharArray = new char[1];

    /**
     * Return how wide a character is in pixels- must take scale into consideration scale
     * 
     * @param c
     * @return character width
     */
    private int getCharacterWidth(FontMetrics fontMetrics, SpriteCharacterKey c, ConfigEmoji emojiConfig)
    {
        if (c.isChar())
        {
            int baseWidth;

            // Extended characters are enabled
            if (c.isExtended())
            {
                if (config.isExtendedCharEnabled())
                {
                    // Return string width of extended char
                    //fontMetricCharArray[0] = c.getChar();
                    baseWidth = fontMetrics.charWidth(c.getCodepoint());
                    // Don't include scale in this calculation, because it's already built into the font size
                    return (int) (baseWidth + config.getCharSpacing() * config.getFontScale());
                }
                // The extended character should be replaced with the unknown character
                else
                {
                    baseWidth = getCharacterBounds(config.getUnknownChar()).width;
                }
            }
            // It's a normal character
            else
            {
                // Character
                baseWidth = getCharacterBounds(c.getChar()).width;
            }
            return (int) ((baseWidth + config.getCharSpacing()) * config.getFontScale());
        }
        else
        {
            // Emoji
            int[] eDim = getEmojiDimensions(c.getEmoji(), emojiConfig);
            final int charSpacing = (int) (config.getCharSpacing() * config.getFontScale());
            final int extraSpacing = (c.getEmoji().getType().isBadge() ? Math.max(charSpacing, (int) (BADGE_MINIMUM_SPACING_PIXELS * config.getFontScale())) : charSpacing);
            return eDim[0] + extraSpacing;
        }

    }

    public void calculateCharacterDimensions()
    {
        // Start from scratch
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
            characterBounds.put((int)c, bounds);
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
                    characterBounds.put((int)ckey, new Rectangle(x + charWidth / 4, y, charWidth / 2, charHeight));
                }
                // For all other characters, or for spaces that have some non transparent pixels, use the calculated
                // bounds
                else
                {
                    characterBounds.put((int)ckey, bounds);
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
    public Rectangle getCharacterBounds(int c)
    {
        if (!config.getCharacterKey().contains(new String(Character.toChars(c))))
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
     * Get the distance in pixels from the top of one line of text to the top of the next line of text, scaled
     * 
     * @return lineHeightScaled
     */
    public int getLineHeightScaled()
    {
        return (int) (getLineHeight() * config.getFontScale());
    }

    /**
     * Get the distance in pixels from the top of one line of text to the top of the next line of text, unscaled
     * 
     * @return lineHeight
     */
    public int getLineHeight()
    {
        return getFontHeight() + config.getLineSpacing();
    }

    public int getFontHeight()
    {
        return sprites.getSprite(config).pixelHeight;
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
    public void incrementLineScrollOffset(int delta, int min, int max)
    {
        final int test = lineScrollOffset + delta;
        if (test >= min && test < max)
        {
            lineScrollOffset = test;
        }
        else
        {
            lineScrollOffset = Math.max(lineScrollOffset, min);
            lineScrollOffset = Math.min(lineScrollOffset, max);
        }
    }

    /**
     * Do a mock drawing of the string to determine the dimensions of the bounding box that would surround the drawn
     * message
     * 
     * @param message
     * @param messageConfig
     * @param emojiConfig
     * @param emojiManager
     * @param lineWrapLength
     * @return The size of the bounding box of the drawn message
     */
    public Dimension getMessageDimensions(Message message, FontMetrics fontMetrics, ConfigMessage messageConfig, ConfigEmoji emojiConfig, EmojiManager emojiManager, int lineWrapLength)
    {
        return drawMessage(null, fontMetrics, message, null, null, messageConfig, emojiConfig, emojiManager, 0, 0, 0, 0, lineWrapLength, false, null, null);
    }

    /**
     * @param g2d
     *            The graphics object upon which to draw
     * @param fontMetrics
     *            The actual font metrics of the JPanel drawing this SpriteFont, used to draw extended characters
     * @param msg
     *            The message to draw
     * @param userColor
     *            The color unique to the sender of the message being drawn
     * @param colorConfig
     *            The configuration for how to color messages
     * @param messageConfig
     *            The configuration for how to draw messages
     * @param emojiConfig
     *            The configuration for how to handle emoji
     * @param emojiManager
     *            The manager for accessing emoji images
     * @param x_init
     *            The left edge x coordinate to start drawing from
     * @param y_init
     *            The top edge y coordinate to start drawing from (probably up in negative space above the graphics
     *            object)
     * @param topLimit
     *            When to start drawing lines as y increases, because many will be off screen or under the top order
     * @param botLimit
     *            Only draw up to this edge
     * @param lineWrapLength
     *            How long to let the text go to the right before going to a new line
     * @param debug
     *            Whether to draw debugging boxes
     * @param debugColor
     *            The color to draw debugging boxes
     * @param emojiObserver
     *            Used to update animated GIF BTTV emotes
     * @return The size of the bounding box of the drawn message
     */
    public Dimension drawMessage(Graphics2D g2d, FontMetrics fontMetrics, Message msg, Color userColor, ConfigColor colorConfig, ConfigMessage messageConfig, ConfigEmoji emojiConfig, EmojiManager emojiManager, int x_init, int y_init, int topLimit, int botLimit, int lineWrapLength, boolean debug, Color debugColor, ImageObserver emojiObserver)
    {
        if (msg.isJoinType() && !messageConfig.showJoinMessages())
        {
            return new Dimension();
        }

        SpriteCharacterKey[] text = msg.getText(emojiManager, messageConfig, emojiConfig);

        int maxCharWidth = 0;
        for (int c = 0; c < text.length; c++)
        {
            maxCharWidth = Math.max(maxCharWidth, getCharacterWidth(fontMetrics, text[c], emojiConfig));
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

        y += lineScrollOffset * getLineHeightScaled();

        int maxWidth = 0;
        int width = 0;
        int height = getLineHeightScaled();

        boolean forcedBreak = false;

        Color color = Color.WHITE;

        // Go through each character in the text
        for (int ci = 0; ci < text.length; ci++)
        {
            if (colorConfig != null)
            {
                color = getFontColor(msg, ci, messageConfig, colorConfig, userColor);
            }

            // If the character is a line return, go to the next line
            if (LINE_BREAKS.contains(String.valueOf(text[ci].getChar())))
            {
                x = x_init;
                maxWidth = Math.max(maxWidth, width);
                width = 0;
                y += getLineHeightScaled();
                if (ci < msg.getDrawCursor())
                {
                    height += getLineHeightScaled();
                }
            }
            // If it's not a line return, look forward into the text to find if
            // the next word fits
            else if (WORD_BREAKS.contains(String.valueOf(text[ci].getChar())))
            {
                int charWidth = getCharacterWidth(fontMetrics, text[ci], emojiConfig);
                x += charWidth;
                width += charWidth;
                forcedBreak = false;
            }
            else
            {
                int currentWordPixelWidth = 0;
                for (int nwc = 0; nwc < text.length - ci && !WORD_BREAKS.contains(String.valueOf(text[ci + nwc].getChar())); nwc++)
                {
                    currentWordPixelWidth += getCharacterWidth(fontMetrics, text[ci + nwc], emojiConfig);
                }
                int distanceAlreadyFilled = x - x_init;

                // The next word fits
                if (distanceAlreadyFilled + currentWordPixelWidth < lineWrapLength)
                {
                    if (g2d != null && y >= topLimit && y < botLimit && ci < msg.getDrawCursor())
                    {
                        drawCharacter(g2d, fontMetrics, text[ci], x, y, emojiConfig, color, debug, debugColor, emojiObserver);
                    }
                    int charWidth = getCharacterWidth(fontMetrics, text[ci], emojiConfig);
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
                    y += getLineHeightScaled();
                    if (ci < msg.getDrawCursor())
                    {
                        height += getLineHeightScaled();
                    }
                    if (g2d != null && y >= topLimit && y < botLimit && ci < msg.getDrawCursor())
                    {
                        drawCharacter(g2d, fontMetrics, text[ci], x, y, emojiConfig, color, debug, debugColor, emojiObserver);
                    }
                    int charWidth = getCharacterWidth(fontMetrics, text[ci], emojiConfig);
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
                    int charWidth = getCharacterWidth(fontMetrics, text[ci], emojiConfig);
                    if (charWidth > remainderOfTheLine)
                    {
                        x = x_init;
                        maxWidth = Math.max(maxWidth, width);
                        width = 0;
                        y += getLineHeightScaled();
                        if (ci < msg.getDrawCursor())
                        {
                            height += getLineHeightScaled();
                        }
                    }

                    if (g2d != null && y >= topLimit && y < botLimit && ci < msg.getDrawCursor())
                    {
                        drawCharacter(g2d, fontMetrics, text[ci], x, y, emojiConfig, color, debug, debugColor, emojiObserver);
                    }
                    x += charWidth;
                    width += charWidth;
                }
            }

        }
        return new Dimension(maxWidth, height);
    }

    private void drawCharacter(Graphics2D g2d, FontMetrics fontMetrics, SpriteCharacterKey sck, int x, int y, ConfigEmoji emojiConfig, Color color, boolean debug, Color debugColor, ImageObserver emojiObserver)
    {
        final int drawX = x + config.getCharSpacing() / 2;
        int drawY = y;

        if (sck.isChar())
        {
            final boolean validNormalChar = !sck.isExtended() && characterBounds.containsKey(sck.getCodepoint());
            final boolean drawUnknownChar = !validNormalChar && !config.isExtendedCharEnabled();

            if (drawUnknownChar)
            {
                sck = new SpriteCharacterKey(config.getUnknownChar());
            }

            if (validNormalChar || drawUnknownChar)
            {
                Rectangle bounds = characterBounds.get(sck.getCodepoint());
                sprites.getSprite(config).draw(g2d, drawX, drawY, bounds.width, bounds.height, bounds, config.getFontScale(), color);
                if (debug)
                {
                    g2d.setColor(debugColor);
                    g2d.drawRect(drawX, drawY, (int) (bounds.width * config.getFontScale()), (int) (bounds.height * config.getFontScale()));
                }
            }
            // The character is invalid, and drawing the unknown char is not selected, so draw the extended characters
            else
            {
                g2d.setColor(color);
                g2d.drawString(sck.toString(), drawX, drawY + (fontMetrics.getHeight() - fontMetrics.getDescent()) - config.getBaselineOffset() * config.getFontScale());
            }
        }
        else
        {
            int[] eDim = getEmojiDimensions(sck.getEmoji(), emojiConfig);
            // yOffset is to center the emoji on the line
            int yOffset = (int) (sprites.getSprite(config).getSpriteDrawHeight(config.getFontScale()) / 2 - config.getBaselineOffset() * config.getFontScale()) - (sck.isBadge() ? emojiConfig.getBadgeHeightOffset() : 0);
            drawY += yOffset - eDim[1] / 2;
            Image eImage = sck.getEmoji().getImage(emojiConfig.isAnimationEnabled());
            if (eImage == null)
            {
                // If the image is null, then it's not loaded, so do the backup display strategy
                g2d.setColor(color);
                switch (emojiConfig.getDisplayStrategy())
                {
                case BOX_FILL:
                    g2d.fillRect(drawX, drawY, eDim[0] + 1, eDim[1] + 1);
                    break;
                case BOX_FRAME:
                    g2d.drawRect(drawX, drawY, eDim[0], eDim[1]);
                    break;
                case UNKNOWN:
                    drawCharacter(g2d, fontMetrics, new SpriteCharacterKey(config.getUnknownChar()), x, y, emojiConfig, color, debug, debugColor, emojiObserver);
                    break;
                case SPACE:
                case NOTHING:
                default:
                    break;
                }
            }
            else
            {
                // Draw a color square background for the emoji (for FrankerFaceZ badges)
                if (sck.getEmoji().isColoringRequired())
                {
                    g2d.setColor(sck.getEmojiBgColor());
                    g2d.fillRect(drawX, drawY, eDim[0] + 1, eDim[1] + 1);
                }
                // Draw the emoji image
                g2d.drawImage(eImage, drawX, drawY, eDim[0], eDim[1], emojiObserver);
            }
        }
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
