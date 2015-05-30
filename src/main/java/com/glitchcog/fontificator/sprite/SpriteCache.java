package com.glitchcog.fontificator.sprite;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.gui.chat.ChatWindow;

/**
 * Contains a map that is a cache of sprites keyed off of font configurations
 * 
 * @author Matt Yanos
 */
public class SpriteCache
{
    private static final Logger logger = Logger.getLogger(SpriteCache.class);

    private Map<String, Sprite> cache;

    public SpriteCache(ConfigFont fontConfig)
    {
        this();
        getSprite(fontConfig);
    }

    public SpriteCache()
    {
        cache = new HashMap<String, Sprite>();
        cache.put(null, new Sprite()); // An empty sprite
    }

    public Sprite getSprite(ConfigFont fontConfig)
    {
        if (!cache.containsKey(fontConfig.getFontFilename()))
        {
            try
            {
                Sprite sprite = new Sprite(fontConfig.getFontFilename(), fontConfig.getGridWidth(), fontConfig.getGridHeight());
                cache.put(fontConfig.getFontFilename(), sprite);
            }
            catch (Exception e)
            {
                logger.error(e.toString(), e);
                ChatWindow.popup.handleProblem("Unable to load sprite from file " + fontConfig.getFontFilename(), e);
                cache.put(fontConfig.getFontFilename(), cache.get(null));
            }
        }

        if (cache.containsKey(fontConfig.getFontFilename()))
        {
            Sprite sprite = cache.get(fontConfig.getFontFilename());
            if (sprite != null)
            {
                sprite.setGridDimensions(fontConfig);
                return sprite;
            }
        }

        logger.error("SpriteCache returning empty default Sprite");
        return cache.get(null);
    }
}
