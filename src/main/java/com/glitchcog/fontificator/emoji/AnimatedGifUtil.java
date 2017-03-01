package com.glitchcog.fontificator.emoji;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * Utility class for loading animated GIFs, largely just to deal with (ditto) so it displays as if it were in a browser
 * 
 * @author Matt Yanos
 */
public class AnimatedGifUtil
{
    private static final Logger logger = Logger.getLogger(AnimatedGifUtil.class);

    public static final String GIF_EXTENSION = "gif";

    public static final String GRAPHIC_CTRL_EXT = "GraphicControlExtension";
    public static final String IMAGE_DESCRIPTOR = "ImageDescriptor";

    public static final String ATTRIBUTE_DELAY_TIME = "delayTime";

    public static final String ATTRIBUTE_DISPOSAL_METHOD = "disposalMethod";

    public static final int MIN_ANI_GIF_DELAY = 10;

    public static final String DISPOSE_NONE = "none";
    public static final String DISPOSE_UNSPECIFIED = "undefinedDisposalMethod4";
    public static final String DISPOSE_DO_NOT_DISPOSE = "doNotDispose";
    public static final String DISPOSE_RESTORE_TO_BGCOLOR = "restoreToBackgroundColor";
    public static final String DISPOSE_RESTORE_TO_PREVIOUS = "restoreToPrevious";

    public static final String IMAGE_LEFT = "imageLeftPosition";
    public static final String IMAGE_TOP = "imageTopPosition";
    public static final String IMAGE_WIDTH = "imageWidth";
    public static final String IMAGE_HEIGHT = "imageHeight";

    public static Image loadAnimatedGif(final URL url)
    {
        return new ImageIcon(url).getImage();
    }

    /**
     * Fix (ditto) for Java's animated GIF interpretation
     * 
     * Adapted from http://stackoverflow.com/questions/26801433/fix-frame-rate-of-animated-gif-in-java#answer-26829534
     * 
     * @param url
     *            The URL for the animated GIF to be loaded
     * @param dim
     *            The dimension object to be filled by the width and height of the loaded animated GIF
     * @return The loaded animated GIF
     * @throws Exception
     */
    public static Image loadDittoAnimatedGif(final URL url, Dimension dim)
    {
        final Image dimImage = new ImageIcon(url).getImage();

        Image image = null;
        try
        {
            ImageReader gifReader = ImageIO.getImageReadersByFormatName(GIF_EXTENSION).next();
            InputStream imageStream = url.openStream();
            gifReader.setInput(ImageIO.createImageInputStream(imageStream));
            IIOMetadata imageMetaData = gifReader.getImageMetadata(0);
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
            final int frameCount = gifReader.getNumImages(true);

            ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
            ImageOutputStream ios = ImageIO.createImageOutputStream(baoStream);
            ImageWriter writer = ImageIO.getImageWriter(gifReader);
            writer.setOutput(ios);
            writer.prepareWriteSequence(null);

            final int imgWidth = dimImage.getWidth(null);
            final int imgHeight = dimImage.getHeight(null);
            dim.setSize(imgWidth, imgHeight);

            for (int i = 0; i < frameCount; i++)
            {
                // This read method takes into account the frame's top and left coordinates
                BufferedImage frame = gifReader.read(i);
                IIOMetadataNode nodes = (IIOMetadataNode) gifReader.getImageMetadata(i).getAsTree(metaFormatName);

                for (int j = 0; j < nodes.getLength(); j++)
                {
                    IIOMetadataNode node = (IIOMetadataNode) nodes.item(j);
                    if (GRAPHIC_CTRL_EXT.equalsIgnoreCase(node.getNodeName()))
                    {
                        int delay = Integer.parseInt(node.getAttribute(ATTRIBUTE_DELAY_TIME));
                        // Minimum delay for browsers, which delay animated GIFs much more than would be to spec
                        if (delay < MIN_ANI_GIF_DELAY)
                        {
                            node.setAttribute(ATTRIBUTE_DELAY_TIME, Integer.toString(MIN_ANI_GIF_DELAY));
                        }
                        // Java's interpretation of restore to previous doesn't seem to work correctly, 
                        // at least not for BTTV's (ditto)
                        if (node.getAttribute(ATTRIBUTE_DISPOSAL_METHOD).equals(DISPOSE_RESTORE_TO_PREVIOUS))
                        {
                            node.setAttribute(ATTRIBUTE_DISPOSAL_METHOD, DISPOSE_RESTORE_TO_BGCOLOR);
                        }
                    }
                }

                IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(frame), null);
                metadata.setFromTree(metadata.getNativeMetadataFormatName(), nodes);

                // This modified frame is necessary to get the correct image placement, width, and height in the final GIF
                BufferedImage frameMod = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics big = frameMod.getGraphics();
                big.drawImage(frame, 0, 0, null);

                IIOImage fixedFrame = new IIOImage(frameMod, null, metadata);
                writer.writeToSequence(fixedFrame, writer.getDefaultWriteParam());
            }
            writer.endWriteSequence();

            if (ios != null)
            {
                ios.close();
            }

            image = Toolkit.getDefaultToolkit().createImage(baoStream.toByteArray());
        }
        catch (Exception e)
        {
            // If anything goes wrong, just load it normally
            logger.error("Error loading animated GIF (ditto) from " + url, e);
            image = new ImageIcon(url).getImage();
            dim.setSize(image.getWidth(null), image.getHeight(null));
        }

        return image;
    }

}
