package com.glitchcog.fontificator.gui.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.config.ConfigCensor;
import com.glitchcog.fontificator.config.ConfigChat;
import com.glitchcog.fontificator.config.ConfigColor;
import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.gui.chat.clock.MessageExpirer;
import com.glitchcog.fontificator.gui.chat.clock.MessageProgressor;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelDebug;
import com.glitchcog.fontificator.gui.controls.panel.MessageCensorPanel;
import com.glitchcog.fontificator.sprite.Sprite;
import com.glitchcog.fontificator.sprite.SpriteFont;

/**
 * This panel contains the entire visualization of the chat, so it handles all the drawing. It also handles scrolling
 * through the chat.
 * 
 * @author Matt Yanos
 */
public class ChatPanel extends JPanel implements MouseWheelListener
{
    private static final Logger logger = Logger.getLogger(ChatPanel.class);

    private static final long serialVersionUID = 1L;

    /**
     * The cache of messages to be displayed
     */
    private ConcurrentLinkedQueue<Message> messages;

    private MessageCensorPanel censor;

    /**
     * Contains the timed task and a reference to this chat to be used to progress rolling out new messages at the
     * appropriate speed
     */
    private MessageProgressor messageProgressor;

    /**
     * Contains the timed task and a reference to this chat to be used to refresh periodically in case any messages have
     * been expired
     */
    private MessageExpirer messageExpirer;

    /**
     * The sprite used to draw the border around the chat, to be displayed if the border scale is greater than zero
     */
    private Sprite border;

    /**
     * The number of lines for all the messages in the chat buffer. This is not the number of messages, but the number
     * of lines the messages will take up once drawn.
     */
    private int lineCount;

    /**
     * The number of lines that fit on the screen between the top border and bottom border.
     */
    private int onScreenLineCount;

    /**
     * Configuration for the font and the border
     */
    private ConfigFont fontConfig;

    /**
     * Configuration for the chat, meaning whether scrolling is enabled and whether and how to draw the chroma key
     * border
     */
    private ConfigChat chatConfig;

    /**
     * Configuration for which colors to use to draw the chat
     */
    private ConfigColor colorConfig;

    /**
     * Configuration for how to draw the messages, what parts of the messages to display, the rate to display new
     * messages, the format for the timestamps, and the queue size
     */
    private ConfigMessage messageConfig;

    /**
     * Configuration for whether to include emoji in the messages
     */
    private ConfigEmoji emojiConfig;

    /**
     * Configuration for how to censor messages
     */
    private ConfigCensor censorConfig;

    /**
     * The control panel for the debug options. They aren't saved, so a reference to the control panel itself is
     * sufficient
     */
    private ControlPanelDebug debugSettings;

    /**
     * The font used to draw the chat messages
     */
    private SpriteFont font;

    /**
     * This indicates whether the configuration has been loaded. Before this is true, no call to any methods that draw
     * should be called because they all rely on the configuration. Once it is set to true, it will remain true- it is
     * only on the initial setup that configuration might be null
     */
    private boolean loaded;

    /**
     * Manages emoji loading, caching, and access
     */
    private EmojiManager emojiManager;

    /**
     * Construct the ChatPanel, which contains the entire visualization of the chat
     * 
     * @param censor
     *            The message popup dialog from the Control Window to be updated when a message is posted so the censor
     *            list is current
     * @throws IOException
     */
    public ChatPanel() throws IOException
    {
        loaded = false;
        lineCount = Integer.MAX_VALUE;
        onScreenLineCount = 0;
        messages = new ConcurrentLinkedQueue<Message>();

        emojiManager = new EmojiManager();
        messageProgressor = new MessageProgressor(this);
        messageExpirer = new MessageExpirer(this);
    }

    /**
     * Get whether the configuration and message dialog have been loaded. Before this is true, no call to any methods
     * that draw should be called because they all rely on the configuration and the link to the message dialog to be
     * updated so censorship rules can be assessed. Once it returns true, it will remain true- it is only on the initial
     * setup that configuration and the message dialog might be null.
     * 
     * @return loaded
     */
    public boolean isLoaded()
    {
        return loaded && censor != null;
    }

    /**
     * Set the configuration references from the properties object. This method instantiates the font and border. Once
     * this method is complete, loaded is set to true.
     * 
     * @param fProps
     *            The properties from which to get the configuration references
     * @throws IOException
     *             If there are any issues loading the files specified for the font and border
     */
    public void setConfig(FontificatorProperties fProps) throws IOException
    {
        logger.trace("Setting chat panel config via fontificator properties object");
        this.fontConfig = fProps.getFontConfig();
        this.chatConfig = fProps.getChatConfig();
        this.colorConfig = fProps.getColorConfig();
        this.messageConfig = fProps.getMessageConfig();
        this.emojiConfig = fProps.getEmojiConfig();
        this.censorConfig = fProps.getCensorConfig();

        font = new SpriteFont(fontConfig);
        reloadFontFromConfig();
        // This initializes the border
        reloadBorderFromConfig();

        // This indicates that the chat panel is ready to be drawn
        loaded = true;
    }

    /**
     * Set the Control Panel for debugging on this chat panel
     * 
     * @param debugSettings
     *            The control panel for debug settings
     */
    public void setDebugSettings(ControlPanelDebug debugSettings)
    {
        this.debugSettings = debugSettings;
    }

    public void setMessageCensor(MessageCensorPanel censor)
    {
        this.censor = censor;
    }

    @Override
    public void paint(Graphics g)
    {
        if (!isLoaded())
        {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        if (chatConfig.isAntiAlias())
        {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        // Fits in the line height.
        boolean stillFits = true;
        int fontSize = 0;
        while (stillFits)
        {
            fontSize++;
            g2d.setFont(new Font(g2d.getFont().getName(), Font.PLAIN, fontSize));
            stillFits = (font.getFontHeight() - fontConfig.getBaselineOffset()) * fontConfig.getFontScale() > g2d.getFontMetrics().getStringBounds("A", 0, 1, g2d).getHeight();
        }

        logger.trace("Calulated font size: " + fontSize);

        List<Message> drawMessages = new ArrayList<Message>();

        // Make a copy of the actual cache that only includes the messages that are completely drawn and possibly the
        // one message currently being drawn
        long drawTime = System.currentTimeMillis();
        for (Message msg : messages)
        {
            final boolean censored = censorConfig.isCensorshipEnabled() && msg.isCensored();
            final boolean expired = messageConfig.isMessageExpirable() && msg.getAge(drawTime) > messageConfig.getExpirationTime();
            if (!censored && !expired)
            {
                drawMessages.add(msg);
            }
            if (!censored && !expired && !msg.isCompletelyDrawn())
            {
                // No need to check any further messages because this is the one currently being rolled out
                break;
            }
        }

        // Draws the background color and the chroma key border
        if (messageConfig.isHideEmptyBackground() && drawMessages.isEmpty())
        {
            // If the messages are empty and the background should be hidden, draw the chroma color regardless of whether it's enabled
            g2d.setColor(colorConfig.getChromaColor());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        else
        {
            drawBackgroundAndChroma(g2d);
        }

        // This offset represents how far inward in the x and y directions the messages should be drawn
        Point offset = new Point();

        // If border scale is zero, just skip this. The drawBorder method won't draw a zero scale border, but if these
        // calculations are attempted with a zero scale it will throw a divide by zero exception
        // Also check if no messages are visible whether the border should be hidden
        if (fontConfig.getBorderScale() > 0.0f && !(messageConfig.isHideEmptyBorder() && drawMessages.isEmpty()))
        {
            final int gridWidth = getWidth() / border.getSpriteDrawWidth(fontConfig.getBorderScale());
            final int gridHeight = getHeight() / border.getSpriteDrawHeight(fontConfig.getBorderScale());

            final int leftOffset = (getWidth() - gridWidth * border.getSpriteDrawWidth(fontConfig.getBorderScale())) / 2;
            final int topOffset = (getHeight() - gridHeight * border.getSpriteDrawHeight(fontConfig.getBorderScale())) / 2;

            offset = new Point(leftOffset, topOffset);

            drawBorder(g2d, gridWidth, gridHeight, offset, colorConfig.getBorderColor(), debugSettings.isDrawBorderGrid(), debugSettings.getBorderGridColor());
        }

        drawChat(g2d, drawMessages, offset, debugSettings.isDrawTextGrid(), debugSettings.getTextGridColor());
    }

    /**
     * Draws a test grid for debugging purposes
     * 
     * @param g
     * @param one
     * @param two
     * @param x
     * @param y
     * @param width
     * @param height
     * @param squareSize
     */
    protected void drawGrid(Graphics g, Color one, Color two, int x, int y, int width, int height, int squareSize)
    {
        for (int r = 0; r < height / squareSize + 1; r++)
        {
            for (int c = 0; c < width / squareSize + 1; c++)
            {
                boolean colorOne = r % 2 == 1 && c % 2 == 0 || r % 2 == 0 && c % 2 == 1;
                g.setColor(colorOne ? one : two);
                g.fillRect(x + c * squareSize, y + r * squareSize, squareSize, squareSize);
            }
        }
    }

    /**
     * Draw the background and the chroma key border
     * 
     * @param g2d
     */
    private void drawBackgroundAndChroma(Graphics2D g2d)
    {
        if (chatConfig.isChromaEnabled())
        {
            g2d.setColor(chatConfig.isChromaInvert() ? colorConfig.getBgColor() : colorConfig.getChromaColor());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(chatConfig.isChromaInvert() ? colorConfig.getChromaColor() : colorConfig.getBgColor());
            Rectangle border = chatConfig.getChromaBorder();
            g2d.fillRoundRect(Math.min(getWidth(), border.x), Math.min(getHeight(), border.y), Math.max(0, getWidth() - border.width - border.x), Math.max(0, getHeight() - border.height - border.y), chatConfig.getChromaCornerRadius(), chatConfig.getChromaCornerRadius());
        }
        else
        {
            // Just draw the background
            g2d.setColor(colorConfig.getBgColor());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Draw the words in the messages in the chat
     * 
     * @param g2d
     * @param messages
     * @param offset
     */
    private void drawChat(Graphics2D g2d, List<Message> drawMessages, Point offset, boolean debug, Color debugColor)
    {
        final int lineWrapLength = (border == null || fontConfig.getBorderScale() <= 0.0f ? getWidth() : border.getSpriteDrawWidth(fontConfig.getBorderScale()) * (getWidth() / border.getSpriteDrawWidth(fontConfig.getBorderScale()) - 2)) - fontConfig.getBorderInsetX() * 2;
        final int leftEdge = offset.x + (border == null || fontConfig.getBorderScale() <= 0.0f ? 0 : border.getSpriteDrawWidth(fontConfig.getBorderScale())) + fontConfig.getBorderInsetX();

        // totalHeight is the height of all the messages
        int totalHeight = 0;
        for (Message msg : drawMessages)
        {
            Dimension dim = font.getMessageDimensions(msg, g2d.getFontMetrics(), messageConfig, emojiConfig, emojiManager, lineWrapLength);
            totalHeight += dim.getHeight();
        }

        // Used for scrolling
        int lineHeight = font.getLineHeightScaled();
        if (lineHeight == 0)
        {
            lineHeight = 1;
        }
        lineCount = lineHeight == 0 ? 0 : totalHeight / lineHeight;

        // borderEdgeThickness is the y-inset on the top plus the height of the top part of the border
        final int borderEdgeThickness = offset.y + (border == null || fontConfig.getBorderScale() < ConfigFont.FONT_BORDER_SCALE_GRANULARITY ? 0 : border.getSpriteDrawHeight(fontConfig.getBorderScale())) + fontConfig.getBorderInsetY();

        final int drawableVerticalRange = getHeight() - borderEdgeThickness * 2;

        // Used for scrolling when chat scrolls normally and starts from the top, or when chat scrolls reverse and starts from the bottom
        onScreenLineCount = drawableVerticalRange / lineHeight;

        // y is where the drawing begins
        int y;
        if (chatConfig.isChatFromBottom())
        {
            if (chatConfig.isReverseScrolling())
            {
                if (totalHeight > drawableVerticalRange)
                {
                    y = borderEdgeThickness;
                }
                else
                {
                    y = getHeight() - totalHeight - borderEdgeThickness;
                }
            }
            else
            {
                y = getHeight() - totalHeight - borderEdgeThickness;
            }
        }
        // else chat from top
        else
        {
            if (chatConfig.isReverseScrolling())
            {
                y = borderEdgeThickness;
            }
            else
            {
                if (totalHeight > drawableVerticalRange)
                {
                    // Not all the messages fit in the given space range, so start drawing up out of bounds at a negative y. This uses just the top borderEdgeThickness's height, not both top and bottom
                    y = (getHeight() - borderEdgeThickness) - totalHeight;
                }
                // If the total height of all the messages is less than or equal to the total height
                else
                {
                    // Just set the y to start drawing to the borderEdgeThickness because it should be fixed to the top when there's enough room for everything
                    y = borderEdgeThickness;
                }
            }
        }

        final int botLimit;
        if (chatConfig.isReverseScrolling() && totalHeight > drawableVerticalRange)
        {
            botLimit = getHeight() - borderEdgeThickness - font.getLineHeightScaled();
        }
        else
        {
            botLimit = getHeight() - borderEdgeThickness;
        }

        // Draw each message in the drawMessages copy of the cache
        for (int i = 0; i < drawMessages.size(); i++)
        {
            int msgIndex = chatConfig.isReverseScrolling() ? drawMessages.size() - i - 1 : i;
            Message msg = drawMessages.get(msgIndex);
            Color col = getUsernameColor(colorConfig, msg);
            // The call to drawMessage in SpriteFont will determine whether to draw each character based on whether it is located at a position appropriate to be drawn on
            Dimension dim = font.drawMessage(g2d, g2d.getFontMetrics(), msg, col, colorConfig, messageConfig, emojiConfig, emojiManager, leftEdge, y, borderEdgeThickness, botLimit, lineWrapLength, debug, debugColor, this);
            y += dim.getHeight();
        }
    }

    private static Color getUsernameColor(ConfigColor colorConfig, Message msg)
    {
        Color col;
        if (msg.isJoinType())
        {
            col = colorConfig.getHighlight();
        }
        else if (colorConfig.isUseTwitchColors() && msg.getPrivmsg().getColor() != null)
        {
            col = msg.getPrivmsg().getColor();
        }
        else
        {
            col = colorConfig.getPalette().isEmpty() ? colorConfig.getHighlight() : colorConfig.getPalette().get(Math.abs(msg.getUsername().toLowerCase().hashCode()) % colorConfig.getPalette().size());
        }
        return col;
    }

    /**
     * Draw the border
     * 
     * @param g2d
     * @param gridWidth
     * @param gridHeight
     * @param offset
     * @param color
     */
    private void drawBorder(Graphics2D g2d, int gridWidth, int gridHeight, Point offset, Color color, boolean debug, Color debugColor)
    {
        final float scale = fontConfig.getBorderScale();

        if (scale <= 0.0f)
        {
            return;
        }

        if (debug)
        {
            g2d.setColor(debugColor);
        }

        for (int r = 0; r < gridHeight; r++)
        {
            for (int c = 0; c < gridWidth; c++)
            {
                int pixelX = c * border.getSpriteDrawWidth(scale) + offset.x;
                int pixelY = r * border.getSpriteDrawHeight(scale) + offset.y;

                if (r == 0) // Top row
                {
                    if (c == 0) // Top left
                    {
                        border.draw(g2d, pixelX, pixelY, 0, scale, color);
                    }
                    else if (c == gridWidth - 1) // Top right
                    {
                        border.draw(g2d, pixelX, pixelY, 2, scale, color);
                    }
                    else
                    // Top middle
                    {
                        border.draw(g2d, pixelX, pixelY, 1, scale, color);
                    }
                }
                else if (r == gridHeight - 1) // Bottom row
                {
                    if (c == 0) // Bottom left
                    {
                        border.draw(g2d, pixelX, pixelY, 6, scale, color);
                    }
                    else if (c == gridWidth - 1) // Bottom right
                    {
                        border.draw(g2d, pixelX, pixelY, 8, scale, color);
                    }
                    else
                    // Bottom middle
                    {
                        border.draw(g2d, pixelX, pixelY, 7, scale, color);
                    }
                }
                else
                // Middle
                {
                    if (c == 0) // Middle left
                    {
                        border.draw(g2d, pixelX, pixelY, 3, scale, color);
                    }
                    else if (c == gridWidth - 1) // Middle right
                    {
                        border.draw(g2d, pixelX, pixelY, 5, scale, color);
                    }
                    else
                    // Middle middle
                    {
                        border.draw(g2d, pixelX, pixelY, 4, scale, color);
                    }
                }
                if (debug)
                {
                    g2d.drawRect(pixelX, pixelY, (int) (border.getSpriteWidth() * scale), (int) (border.getSpriteHeight() * scale));
                }
            }
        }
    }

    /**
     * Add a message to the cache, and call method to process any censorship
     * 
     * @param addition
     */
    synchronized public void addMessage(Message addition)
    {
        if (addition.isJoinType() && !messageConfig.showJoinMessages())
        {
            return;
        }

        censor.checkCensor(addition);

        // Note that for a moment here, the size of messages can exceed the specified queueSize in the message config,
        // so if another thread is accessing this, be sure to take that into consideration
        messages.add(addition);
        int remCount = Math.max(0, messages.size() - messageConfig.getQueueSize());

        Iterator<Message> iter = messages.iterator();
        while (iter.hasNext() && remCount > 0)
        {
            iter.next();
            iter.remove();
            remCount--;
        }

        initMessageRollout();
        if (censor.isVisible())
        {
            censor.updateManualTable();
        }

        repaint();
    }

    /**
     * Delete all messages from the queue to clear the chat
     */
    synchronized public void clearChat()
    {
        messages.clear();
        repaint();
    }

    /**
     * Reset the scroll offset to zero
     */
    public void resetScrollOffset()
    {
        if (isLoaded())
        {
            font.setLineScrollOffset(0);
        }
    }

    /**
     * Refresh the scroll offset in case of resize (only needed when chat starts at top)
     * 
     * @param positiveDirection
     *            Whether the direction is up or down
     * @param lines
     *            how many lines to scroll
     */
    public void incrementScrollOffset(boolean positiveDirection, int lines)
    {
        if (isLoaded())
        {
            final int dir = positiveDirection ? lines : -lines;
            if (chatConfig.isChatFromBottom())
            {
                font.incrementLineScrollOffset(dir, 0, lineCount);
            }
            else
            {
                final boolean screenIsOverflowing = lineCount >= onScreenLineCount;
                if (screenIsOverflowing)
                {
                    font.incrementLineScrollOffset(dir, -onScreenLineCount + 1, lineCount - onScreenLineCount + 1);
                }
                else
                {
                    font.incrementLineScrollOffset(dir, lineCount == 0 ? 0 : -lineCount + 1, 1);
                }
            }
            repaint();
        }
    }

    /**
     * This is added to the window above
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (isLoaded() && chatConfig.isScrollable())
        {
            incrementScrollOffset(e.getWheelRotation() < 0, 1);
        }
    }

    /**
     * Get the message cache. This is used by the timer task that resides in the MessageProgressor object to increment
     * the drawing of the messages
     * 
     * @return messages
     */
    synchronized public Message[] getMessages()
    {
        return messages.toArray(new Message[messages.size()]);
    }

    /**
     * Get the actual queue of messages
     * 
     * @return messages
     */
    public ConcurrentLinkedQueue<Message> getMessageQueue()
    {
        return messages;
    }

    /**
     * Updates the font when there are changes to the configuration
     * 
     * @throws IOException
     *             If there is a problem using a file
     */
    public void reloadFontFromConfig() throws IOException
    {
        font.updateForConfigChange();
    }

    /**
     * Updates the border when there are changes to the configuration
     * 
     * @throws IOException
     */
    public void reloadBorderFromConfig() throws IOException
    {
        try
        {
            border = new Sprite(fontConfig.getBorderFilename(), 3, 3);
        }
        catch (Exception e)
        {
            final String errorMessage = "Unable to load border sprite " + (fontConfig == null ? "for null font configuration" : "for border filename " + fontConfig.getBorderFilename());
            logger.error(errorMessage, e);
            border = new Sprite();
        }
    }

    /**
     * Used for getting the message configuration for formatting the message and timing the message progression
     * 
     * @return message config
     */
    public ConfigMessage getMessageConfig()
    {
        return messageConfig;
    }

    /**
     * Used for getting the emoji configuration for formatting the message
     * 
     * @return emoji config
     */
    public ConfigEmoji getEmojiConfig()
    {
        return emojiConfig;
    }

    /**
     * Get the message progressor
     * 
     * @return messageProgressor
     */
    public MessageProgressor getMessageProgressor()
    {
        return messageProgressor;
    }

    public MessageExpirer getMessageExpirer()
    {
        return messageExpirer;
    }

    public void banUser(String bannedUser)
    {
        censor.addBan(bannedUser);
        repaint();
    }

    public void unbanUser(String bannedUser)
    {
        censor.removeBan(bannedUser);
        repaint();
    }

    public EmojiManager getEmojiManager()
    {
        return emojiManager;
    }

    public void initExpirationTimer()
    {
        if (messageConfig.isMessageExpirable())
        {
            messageExpirer.startClock();
        }
    }

    /**
     * Attempt to restart the message rollout, called whenever some messages might be reintroduced to the drawMessage
     * after the message rollout is completed, by being uncensored for example. This call relies on the fact that the
     * messageProgression will halt again if all the messages are complete already.
     */
    public void initMessageRollout()
    {
        messageProgressor.startClock(messageConfig.getMessageDelay());
    }

    public boolean isCensorshipEnabled()
    {
        return censorConfig.isCensorshipEnabled();
    }

}
