package com.glitchcog.fontificator.emoji;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Handles the few Twitch emotes that have opaque backgrounds
 * 
 * @author Matt Yanos
 */
public class EmojiOpacityHandler
{
    private static final Logger logger = Logger.getLogger(EmojiOpacityHandler.class);

    /**
     * A few Twitch face-style emotes have blatantly opaque backgrounds. The words representing those emotes are the
     * keys in this map, and the values they link to are the thresholds of simple Euclidean distance between the
     * upper-left-most pixel's color and the pixel being tested to determine if it should be swapped out for a
     * transparent pixel.
     */
    public static final Map<String, Double> TWITCH_EMOTES_WITH_OPAQUE_BACKGROUNDS = new HashMap<String, Double>()
    {
        private static final long serialVersionUID = 1L;

        {
            put("Kappa", 0.0);
            put("StoneLightning", 0.21);
            put("TheRinger", 0.11);
            put("EagleEye", 0.12);
            put("CougarHunt", 0.085);
            put("RedCoat", 0.12);
        }
    };

    /**
     * Fix Kappa if its still not transparent
     * 
     * @param emojiType
     * @param imageType
     * @param identifier
     * @return
     */
    public static boolean isCandidateForModification(EmojiType emojiType, int imageType, String identifier)
    {
        final boolean emojiTypeQualifies = emojiType.isTwitchEmote();

        // @formatter:off
        // EagleEye is a TYPE_4BYTE_ABGR type image, but it still has an opaque background
        // final boolean imageTypeQualifies = imageType != BufferedImage.TYPE_INT_ARGB && 
        //                                    imageType != BufferedImage.TYPE_4BYTE_ABGR && 
        //                                    imageType != BufferedImage.TYPE_4BYTE_ABGR_PRE && 
        //                                    imageType != BufferedImage.TYPE_INT_ARGB_PRE;
        // @formatter:on

        boolean specificEmojiQualifies = false;
        for (String key : TWITCH_EMOTES_WITH_OPAQUE_BACKGROUNDS.keySet())
        {
            if (key.equals(identifier))
            {
                specificEmojiQualifies = true;
                break;
            }
        }

        return emojiTypeQualifies && specificEmojiQualifies;
    }

    /**
     * Takes the pixel in the upper left corner of the image from Twitch and changes all instances of that pixel to a
     * transparent pixel in the returned custom transparency image
     * 
     * @param identifier
     * @param imageFromTwitch
     * @return customTransparencyImage
     */
    public static Image fixOpaqueEmote(String identifier, Image imageFromTwitch)
    {
        logger.trace("Doing custom background transparancy for " + identifier + " emote");

        // Draw the loaded image onto a buffered image of type ARGB
        BufferedImage customTransparencyImage = new BufferedImage(imageFromTwitch.getWidth(null), imageFromTwitch.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics big = customTransparencyImage.getGraphics();
        big.drawImage(imageFromTwitch, 0, 0, null);

        // Get the int values of the pixel to swap out (the background color for Kappa V1 sizes 2.0 and
        // 3.0), and the transparent pixel to swap in
        final int pixelThatShouldBeTransparent = customTransparencyImage.getRGB(0, 0);
        // The second parameter on this Color object constructor is very important for the pixel to actually
        // be transparent
        final int transparentPixel = new Color(0, true).getRGB();

        // Go through the pixels in the buffered image and switch out the background color for the
        // transparent color
        for (int y = 0; y < customTransparencyImage.getHeight(); y++)
        {
            for (int x = 0; x < customTransparencyImage.getWidth(); x++)
            {
                if (getDifferenceBetweenColors(customTransparencyImage.getRGB(x, y), pixelThatShouldBeTransparent) <= TWITCH_EMOTES_WITH_OPAQUE_BACKGROUNDS.get(identifier))
                {
                    customTransparencyImage.setRGB(x, y, transparentPixel);
                }
            }
        }

        return customTransparencyImage;
    }

    /**
     * Just a quick and dirty Euclidean distance between two colors
     * 
     * @param a
     * @param b
     * @return percent different
     */
    private static double getDifferenceBetweenColors(int a, int b)
    {
        Color ca = new Color(a);
        Color cb = new Color(b);

        final double rDiff = ca.getRed() - cb.getRed();
        final double gDiff = ca.getGreen() - cb.getGreen();
        final double bDiff = ca.getBlue() - cb.getBlue();
        final double diff = Math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff);
        final double percentDiff = diff / Math.sqrt(255 * 255 + 255 * 255 + 255 * 255);

        return percentDiff;
    }
}
