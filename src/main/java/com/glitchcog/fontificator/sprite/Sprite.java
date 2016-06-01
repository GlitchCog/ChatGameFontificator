package com.glitchcog.fontificator.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigFont;

/**
 * A sprite that has a grid containing different frames to be displayed.
 * 
 * @author Matt Yanos
 */
public class Sprite
{
    private static final Logger logger = Logger.getLogger(Sprite.class);

    private BufferedImage img;

    private Map<Color, BufferedImage> coloredImgs;

    /**
     * The number of frames that make up the width of the image grid
     */
    protected int gridWidth;

    /**
     * The number of frames that make up the height of the image grid
     */
    protected int gridHeight;

    /**
     * The pixel width of a single character
     */
    protected int pixelWidth;

    /**
     * The pixel height of a single character
     */
    protected int pixelHeight;

    private ShortLookupTable swapTable;

    private BufferedImageOp swapOp;

    /**
     * An empty sprite to use if an image file won't load
     */
    public Sprite()
    {
        logger.trace("Sprite default construstor");
        this.gridWidth = 8;
        this.pixelWidth = 8;
        this.pixelHeight = 8;
        img = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        coloredImgs = new HashMap<Color, BufferedImage>();
        setupSwap();
    }

    /**
     * Construct a fixed-width sprite font
     * 
     * @param filename
     *            Name of the image file containing the font grid
     * @param gridWidth
     *            The number of characters that make up the width of the image grid
     * @param gridHeight
     *            The number of characters that make up the height of the image grid
     * @throws Exception
     */
    public Sprite(String filename, int gridWidth, int gridHeight) throws Exception
    {
        this(filename);
        logger.trace("Sprite grid specified: (" + gridWidth + "x" + gridHeight + ")");
        setGridDimensions(gridWidth, gridHeight);
        setupSwap();
        addToColorCache(Color.WHITE);
    }

    /**
     * Construct a sprite
     * 
     * @param fontSpriteFilename
     *            Name of the image file containing the font grid
     */
    public Sprite(String fontSpriteFilename) throws IOException
    {
        logger.trace("Loading sprite from " + fontSpriteFilename);

        coloredImgs = new HashMap<Color, BufferedImage>();

        setImage(fontSpriteFilename);

        setupSwap();
    }

    private void setupSwap()
    {
        short[][] lookupArray = new short[4][256];
        for (int i = 0; i < lookupArray.length; i++)
        {
            for (short c = 0; c < lookupArray[i].length; c++)
            {
                lookupArray[i][c] = c;
            }
        }
        swapTable = new ShortLookupTable(0, lookupArray);
        swapOp = new LookupOp(swapTable, null);
    }

    private boolean setImage(String filename) throws IOException
    {
        logger.trace("Setting image for " + filename);
        if (filename.startsWith(ConfigFont.INTERNAL_FILE_PREFIX))
        {
            final String classPathUrlStr = filename.substring(ConfigFont.INTERNAL_FILE_PREFIX.length());
            URL url = getClass().getClassLoader().getResource(classPathUrlStr);
            logger.trace("Sprite URL: " + url);
            // Assume that the preset images are all of the correct type (TYPE_4BYTE_ABGR)
            img = ImageIO.read(url);
        }
        else
        {
            File file = new File(filename);
            BufferedImage testImg = ImageIO.read(file);

            if (testImg.getType() != BufferedImage.TYPE_4BYTE_ABGR)
            {
                // Convert
                img = new BufferedImage(testImg.getWidth(), testImg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                img.getGraphics().drawImage(testImg, 0, 0, null);
                img.getGraphics().dispose();
            }
            else
            {
                img = testImg;
            }
        }
        if (img == null)
        {
            logger.error("Unable to load image " + filename);
            return false;
        }
        return true;
    }

    public BufferedImage getImage()
    {
        return img;
    }

    public void setGridDimensions(ConfigFont config)
    {
        setGridDimensions(config.getGridWidth(), config.getGridHeight());
    }

    private void setGridDimensions(int gridWidth, int gridHeight)
    {
        setGridWidth(gridWidth);
        setGridHeight(gridHeight);
    }

    /**
     * Sets the number of frames that make up the width of the image grid and calculates the pixel width of a frame
     * accordingly
     * 
     * @param gridWidth
     */
    private void setGridWidth(int gridWidth)
    {
        if (gridWidth < 1)
        {
            logger.error("Grid width must be at least 1");
            gridWidth = 1;
        }
        this.gridWidth = gridWidth;
        this.pixelWidth = img.getWidth(null) / gridWidth;
    }

    /**
     * Sets the number of frames that make up the height of the image grid and calculates the pixel height of a frame
     * accordingly
     * 
     * @param gridHeight
     */
    public void setGridHeight(int gridHeight)
    {
        if (gridHeight < 1)
        {
            logger.error("Grid height must be at least 1");
            gridHeight = 1;
        }
        this.gridHeight = gridHeight;
        this.pixelHeight = img.getHeight(null) / gridHeight;
    }

    /**
     * The pixel width of a specific frame
     * 
     * @return pixelWidth
     */
    public int getSpriteWidth()
    {
        return pixelWidth;
    }

    /**
     * The pixel height of a specific frame
     * 
     * @return pixelHeight
     */
    public int getSpriteHeight()
    {
        return pixelHeight;
    }

    /**
     * The pixel width of a frame as it appears on the screen
     * 
     * @return drawWidth
     */
    public int getSpriteDrawWidth(float scale)
    {
        return (int) (pixelWidth * scale);
    }

    /**
     * The pixel height of a frame as it appears on the screen
     * 
     * @return drawHeight
     */
    public int getSpriteDrawHeight(float scale)
    {
        return (int) (pixelHeight * scale);
    }

    private static BufferedImage copyImage(BufferedImage bi)
    {
        ColorModel cm = bi.getColorModel();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
    }

    private BufferedImage addToColorCache(Color drawColor)
    {
        for (short i = 0; i < 256; i++)
        {
            swapTable.getTable()[0][i] = (short) ((i / 255.0f) * drawColor.getRed());
            swapTable.getTable()[1][i] = (short) ((i / 255.0f) * drawColor.getGreen());
            swapTable.getTable()[2][i] = (short) ((i / 255.0f) * drawColor.getBlue());
        }
        BufferedImage coloredImg = copyImage(img);
        coloredImg = swapOp.filter(img, coloredImg);

        coloredImgs.put(drawColor, coloredImg);

        return coloredImg;
    }

    public void draw(Graphics2D g2d, int x, int y, int frame, float scale, Color color)
    {
        int sourceX = (frame % gridWidth) * pixelWidth;
        int sourceY = (frame / gridWidth) * pixelHeight;

        BufferedImage drawImg = coloredImgs.get(color);
        if (drawImg == null)
        {
            drawImg = addToColorCache(color);
            coloredImgs.put(color, drawImg);
        }
        g2d.drawImage(drawImg, x, y, x + (int) (pixelWidth * scale), y + (int) (pixelHeight * scale), sourceX, sourceY, sourceX + pixelWidth, sourceY + pixelHeight, null);

        // Crops the image before anti-aliasing is applied, so pixels on the edges of cropping lines don't bleed over, but it's slow!
        // final BufferedImage bi = drawImg.getSubimage(sourceX, sourceY, pixelWidth, pixelHeight);
        // g2d.drawImage(bi, x, y, (int) (pixelWidth * scale), (int) (pixelHeight * scale), null);
    }

    /**
     * @param g2d
     * @param x
     * @param y
     * @param source
     * @param scale
     */
    public void draw(Graphics2D g2d, int x, int y, Rectangle source, float scale, Color color)
    {
        draw(g2d, x, y, pixelWidth, pixelHeight, source, scale, color);
    }

    public void draw(Graphics2D g2d, int x, int y, int w, int h, Rectangle source, float scale, Color color)
    {
        BufferedImage drawImg = coloredImgs.get(color);
        if (drawImg == null)
        {
            drawImg = addToColorCache(color);
            coloredImgs.put(color, drawImg);
        }
        g2d.drawImage(drawImg, x, y, x + (int) (w * scale), y + (int) (h * scale), source.x, source.y, source.x + source.width, source.y + source.height, null);

        // Crops the image before anti-aliasing is applied, so pixels on the edges of cropping lines don't bleed over, but it's slow!
        // final BufferedImage bi = drawImg.getSubimage(source.x, source.y, source.width, source.height);
        // g2d.drawImage(bi, x, y, (int) (w * scale), (int) (h * scale), null);
    }

}
