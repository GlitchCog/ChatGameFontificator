package com.glitchcog.fontificator.gui.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.config.ConfigChat;
import com.glitchcog.fontificator.config.ConfigColor;
import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.config.FontificatorProperties;
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
    private LinkedList<Message> messages;

    private Set<String> bannedUsers;

    /**
     * Contains the timed task and a reference to this chat to be used to progress rolling out new messages at the
     * appropriate speed
     */
    private MessageProgressor messageProgressor;

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
     * Construct the ChatPanel, which contains the entire visualization of the chat
     * 
     * @throws IOException
     */
    public ChatPanel() throws IOException
    {
        loaded = false;
        lineCount = Integer.MAX_VALUE;
        messages = new LinkedList<Message>();
        bannedUsers = new HashSet<String>();

        messageProgressor = new MessageProgressor(this);
    }

    /**
     * Get whether the configuration has been loaded. Before this is true, no call to any methods that draw should be
     * called because they all rely on the configuration. Once it is set to true, it will remain true- it is only on the
     * initial setup that configuration might be null
     * 
     * @return loaded
     */
    public boolean isLoaded()
    {
        return loaded;
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

        font = new SpriteFont(fontConfig);
        reloadFontFromConfig();
        // This initializes the border
        reloadBorderFromConfig();

        // This indicates that the chat panel is ready to be drawn
        loaded = true;
    }

    @Override
    public void paint(Graphics g)
    {
        if (!isLoaded())
        {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        // Draws the background color and the chroma key border
        drawBackground(g2d);

        // This offset represents how far inward in the x and y directions the messages should be drawn
        Point offset = new Point();

        // If border scale is zero, just skip this. The drawBorder method won't draw a zero scale border, but if these
        // calculations are attempted with a zero scale it will throw a divide by zero exception
        if (fontConfig.getBorderScale() > 0)
        {
            final int gridWidth = getWidth() / border.getSpriteDrawWidth(fontConfig.getBorderScale());
            final int gridHeight = getHeight() / border.getSpriteDrawHeight(fontConfig.getBorderScale());

            final int leftOffset = (getWidth() - gridWidth * border.getSpriteDrawWidth(fontConfig.getBorderScale())) / 2;
            final int topOffset = (getHeight() - gridHeight * border.getSpriteDrawHeight(fontConfig.getBorderScale())) / 2;

            offset = new Point(leftOffset, topOffset);

            drawBorder(g2d, gridWidth, gridHeight, offset, colorConfig.getBorderColor());
        }

        drawChat(g2d, messages, offset);
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
    private void drawBackground(Graphics2D g2d)
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
    private void drawChat(Graphics2D g2d, List<Message> messages, Point offset)
    {
        List<Message> drawMessages = new ArrayList<Message>();

        // Make a copy of the actual cache that only includes the messages that are completely drawn and possibly the
        // one message currently being drawn
        for (Message msg : messages)
        {
            drawMessages.add(msg);
            if (!msg.isCompletelyDrawn())
            {
                break;
            }
        }

        final int lineWrapLength = border == null || fontConfig.getBorderScale() < 1 ? getWidth() : border.getSpriteDrawWidth(fontConfig.getBorderScale()) * (getWidth() / border.getSpriteDrawWidth(fontConfig.getBorderScale()) - 2) - fontConfig.getBorderInsetX() * 2;
        final int leftEdge = offset.x + (border == null || fontConfig.getBorderScale() < 1 ? 0 : border.getSpriteDrawWidth(fontConfig.getBorderScale())) + fontConfig.getBorderInsetX();

        int totalHeight = 0;
        for (Message msg : drawMessages)
        {
            Dimension dim = font.getMessageDimensions(msg, messageConfig, lineWrapLength);
            totalHeight += dim.getHeight();
        }

        // Used to make sure scrolling is good
        int lineHeight = font.getLineHeight();
        lineCount = lineHeight == 0 ? 0 : totalHeight / lineHeight;

        final int borderEdgeThickness = offset.y + (border == null || fontConfig.getBorderScale() < 1 ? 0 : border.getSpriteDrawHeight(fontConfig.getBorderScale())) + fontConfig.getBorderInsetY();

        // This y initially represents the coordinate to start drawing the messages from the top down such that the last
        // message to be displayed will be at the bottom of the chat panel. This variable is also incremented as each
        // message is drawn
        int y = getHeight() - totalHeight - borderEdgeThickness;

        // Draw each message in the drawMessages copy of the cache
        for (Message msg : drawMessages)
        {
            if (bannedUsers.contains(msg.getUsername()))
            {
                continue;
            }

            Color col;
            if (msg.isJoinType())
            {
                col = colorConfig.getHighlight();
            }
            else
            {
                col = colorConfig.getPalette().isEmpty() ? colorConfig.getHighlight() : colorConfig.getPalette().get(Math.abs(msg.getUsername().toLowerCase().hashCode()) % colorConfig.getPalette().size());
            }

            Dimension dim = font.drawMessage(g2d, msg, col, colorConfig, messageConfig, leftEdge, y, borderEdgeThickness, getHeight() - borderEdgeThickness, lineWrapLength);
            y += dim.getHeight();
        }
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
    private void drawBorder(Graphics2D g2d, int gridWidth, int gridHeight, Point offset, Color color)
    {
        final float scale = fontConfig.getBorderScale();

        if (scale < 1)
        {
            return;
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
            }
        }
    }

    /**
     * Add a message to the cache
     * 
     * @param addition
     */
    synchronized public void addMessage(Message addition)
    {
        if (addition.isJoinType() && !messageConfig.showJoinMessages())
        {
            return;
        }

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

        messageProgressor.startMessageClock(messageConfig.getMessageDelay());

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
     * This is added to the window above
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (isLoaded() && chatConfig.isScrollable())
        {
            font.setLineScrollOffset(-e.getWheelRotation(), 0, lineCount);
            repaint();
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
            ChatWindow.popup.handleProblem(errorMessage);
            border = new Sprite();
        }
    }

    /**
     * Used for getting the message configuration for timing the message progression
     * 
     * @return message config
     */
    public ConfigMessage getMessageConfig()
    {
        return messageConfig;
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

    public void banUser(String bannedUser)
    {
        bannedUsers.add(bannedUser);
        repaint();
    }

    public void unbanUser(String bannedUser)
    {
        bannedUsers.remove(bannedUser);
        repaint();
    }

}
