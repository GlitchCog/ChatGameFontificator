package com.glitchcog.fontificator.emoji;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * Holds the URL for an emoji image until it is needed, at which point it load it into memory. This is a combination of
 * every possible version of emoji that could be read from its JSON source, be it Twitch V2, Twitch V3, or FrankerFaceZ.
 * For emoji with multiple images, they will have multiple instances of this LazyLoadEmoji object in the emoji<String,
 * LazyLoadEmoji[]> map in EmojiManager.
 * 
 * @author Matt Yanos
 */
public class LazyLoadEmoji
{
    private static final Logger logger = Logger.getLogger(LazyLoadEmoji.class);

    private final EmojiType type;

    private Image image;

    private URL url;

    private boolean subscriber;

    private String state;

    private int width;

    private int height;

    public LazyLoadEmoji(String url, int width, int height, EmojiType type) throws MalformedURLException
    {
        this.url = new URL(url);
        this.type = type;
        this.width = width;
        this.height = height;
    }

    /**
     * Lazy-loaded image
     * 
     * @return image
     */
    public Image getImage()
    {
        if (image == null)
        {
            try
            {
                image = ImageIO.read(url);
            }
            catch (IOException e)
            {
                logger.trace("Could not load from " + url);
            }
        }
        return image;
    }

    public boolean isSubscriber()
    {
        return subscriber;
    }

    public void setSubscriber(boolean subscriber)
    {
        this.subscriber = subscriber;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public EmojiType getType()
    {
        return type;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }
}
