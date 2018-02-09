package com.glitchcog.fontificator.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.FontType;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelFont;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownBorder;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownFont;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownLabel;

public class AssetIndexLoader
{
    private static final Logger logger = Logger.getLogger(AssetIndexLoader.class);

    private static final String INDEX_FONTS_FILENAME = "index_fonts.txt";

    private static final String INDEX_BORDERS_FILENAME = "index_borders.txt";

    /**
     * Loads all the font index values into a map
     * 
     * @return
     */
    public static Map<DropdownLabel, DropdownFont> loadFonts()
    {
        Map<DropdownLabel, DropdownFont> fontMap = new LinkedHashMap<DropdownLabel, DropdownFont>();
        fontMap.put(ControlPanelFont.CUSTOM_KEY, null);
        String errorLine = null;
        try
        {
            List<String> fontLines = loadIndexFile(INDEX_FONTS_FILENAME);
            for (String line : fontLines)
            {
                errorLine = line;
                if (line.startsWith("#"))
                {
                    continue;
                }
                String[] splitLine = line.split(",");
                final String category = splitLine[0].trim();
                final String name = splitLine[1].trim();
                final String filename = splitLine[2].trim();
                final FontType type = FontType.valueOf(splitLine[3].trim());
                fontMap.put(new DropdownLabel(category, name), new DropdownFont(filename, type));
            }
        }
        catch (Exception e)
        {
            logger.error("Error loading font index" + (errorLine == null ? "" : "\n" + errorLine), e);
        }
        return fontMap;
    }

    /**
     * Load all the border index values into a map
     * 
     * @return
     */
    public static Map<DropdownLabel, DropdownBorder> loadBorders()
    {
        Map<DropdownLabel, DropdownBorder> borderMap = new LinkedHashMap<DropdownLabel, DropdownBorder>();
        borderMap.put(ControlPanelFont.CUSTOM_KEY, null);
        String errorLine = null;
        try
        {
            List<String> borderLines = loadIndexFile(INDEX_BORDERS_FILENAME);
            for (String line : borderLines)
            {
                errorLine = line;
                if (line.startsWith("#"))
                {
                    continue;
                }
                String[] splitLine = line.split(",");
                final String category = splitLine[0].trim();
                final String name = splitLine[1].trim();
                final String filename = splitLine[2].trim();
                final int color = Integer.parseInt(splitLine[3].trim(), 16);
                borderMap.put(new DropdownLabel(category, name), new DropdownBorder(filename, color));
            }
        }
        catch (Exception e)
        {
            logger.error("Error loading border index" + (errorLine == null ? "" : "\n" + errorLine), e);
        }
        return borderMap;
    }

    private static List<String> loadIndexFile(String indexFilename) throws IOException
    {
        InputStream is = AssetIndexLoader.class.getClassLoader().getResourceAsStream(indexFilename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        List<String> lines = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null)
        {
            lines.add(line);
        }
        return lines;
    }

}