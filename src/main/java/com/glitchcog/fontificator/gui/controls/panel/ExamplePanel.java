package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.bot.MessageType;
import com.glitchcog.fontificator.config.ConfigColor;
import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.config.FontType;
import com.glitchcog.fontificator.config.MessageCasing;
import com.glitchcog.fontificator.config.UsernameCaseResolutionType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.component.ColorButton;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownFont;
import com.glitchcog.fontificator.sprite.SpriteCharacterKey;
import com.glitchcog.fontificator.sprite.SpriteFont;

/**
 * Panel on the Debug Control Panel for creating collages of different presets
 * 
 * @author Matt Yanos
 */
public class ExamplePanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CollagePanel.class);

    // @formatter:off
    private final static String[] PANGRAMS = new String[] {
        "Pack my box with five dozen liquor jugs.", 
        "Jackdaws love my big sphinx of quartz.", 
        "We quickly seized the black axle and just saved it from going past him.", 
        "Six big juicy steaks sizzled in a pan as five workmen left the quarry.", 
        "While making deep excavations we found some quaint bronze jewelry.", 
        "Jaded zombies acted quaintly but kept driving their oxen forward.", 
        "A mad boxer shot a quick, gloved jab to the jaw of his dizzy opponent.", 
        "The job requires extra pluck and zeal from every young wage earner.", 
        "A quart jar of oil mixed with zinc oxide makes a very bright paint.", 
        "Whenever the black fox jumped the squirrel gazed suspiciously.", 
        "We promptly judged antique ivory buckles for the next prize.", 
        "How razorback-jumping frogs can level six piqued gymnasts!", 
        "Crazy Fredericka bought many very exquisite opal jewels.", 
        "Sixty zippers were quickly picked from the woven jute bag.", 
        "Amazingly few discotheques provide jukeboxes.", 
        "Heavy boxes perform quick waltzes and jigs.", 
        "Jinxed wizards pluck ivy from the big quilt.", 
        "Big Fuji waves pitch enzymed kex liquor.", 
        "The quick brown fox jumps over a lazy dog.", 
        "Pack my box with five dozen liquor jugs.", 
        "Jackdaws love my big sphinx of quartz.", 
        "The five boxing wizards jump quickly.", 
        "How quickly daft jumping zebras vex.", 
        "Quick zephyrs blow, vexing daft Jim.", 
        "Sphinx of black quartz, judge my vow.", 
        "Waltz, nymph, for quick jigs vex Bud.", 
        "How vexingly quick daft zebras jump!", 
        "Bright vixens jump; dozy fowl quack."
    };
    // @formatter:on

    public ExamplePanel(ControlPanelBase basePanel)
    {
        build(basePanel);
    }

    private JTextField lineFormatInput;

    private ColorButton fontColorButton;

    private ColorButton bgColorButton;

    private static final String TEXT_FONTNAME = "[FONTNAME]";

    private static final String TEXT_PANGRAM = "[PANGRAM]";

    private static final String TEXT_ASCII = "[ASCII]";

    private void build(ControlPanelBase basePanel)
    {
        fontColorButton = new ColorButton("Font Tint", Color.WHITE, "Color to tint the font in the example image", basePanel);
        bgColorButton = new ColorButton("Backgroud", Color.DARK_GRAY.darker(), "Color to make the background", basePanel);
        lineFormatInput = new JTextField(TEXT_FONTNAME + " " + TEXT_PANGRAM + " " + TEXT_ASCII);
        JButton saveExample = new JButton("Generate/Save Example");
        saveExample.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                generateExampleImage();
            }
        });

        GridBagConstraints gbc = ControlPanelBase.getGbc();
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        add(fontColorButton, gbc);
        gbc.gridx++;
        add(bgColorButton, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.9;
        gbc.gridx++;
        add(lineFormatInput, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.1;
        gbc.gridx++;
        add(saveExample, gbc);
    }

    private static Message getExampleMessage(final String fontFilename, final String format)
    {
        String messageText = format;
        messageText = messageText.replaceAll(Pattern.quote(TEXT_FONTNAME), Matcher.quoteReplacement(ControlPanelFont.getFontGameName(fontFilename)));
        messageText = messageText.replaceAll(Pattern.quote(TEXT_PANGRAM), Matcher.quoteReplacement(PANGRAMS[Math.abs(fontFilename.hashCode()) % PANGRAMS.length]));
        messageText = messageText.replaceAll(Pattern.quote(TEXT_ASCII), Matcher.quoteReplacement(SpriteFont.NORMAL_ASCII_KEY));
        return new Message(MessageType.MANUAL, "", messageText, null);
    }

    private void generateExampleImage()
    {
        Properties emptyProps = new Properties();
        LoadConfigReport ignoreReport = new LoadConfigReport();

        ConfigFont fontConfig = new ConfigFont();
        fontConfig.load(emptyProps, ignoreReport);

        fontConfig.setBorderFilename(ConfigFont.INTERNAL_FILE_PREFIX + "borders/dw3_border.png");
        fontConfig.setFontFilename(ConfigFont.INTERNAL_FILE_PREFIX + "fonts/dw3_font.png");
        fontConfig.setFontType(FontType.FIXED_WIDTH);
        fontConfig.setUnknownChar((char) 127);
        fontConfig.setExtendedCharEnabled(false);
        fontConfig.setCharacterKey(SpriteFont.NORMAL_ASCII_KEY);
        fontConfig.setGridWidth(8);
        fontConfig.setGridHeight(12);
        fontConfig.setFontScale(1);
        fontConfig.setBorderScale(0);
        fontConfig.setBorderInsetX(0);
        fontConfig.setBorderInsetY(0);
        fontConfig.setSpaceWidth(25);
        fontConfig.setBaselineOffset(0);
        final int heightOffset = 6;
        fontConfig.setLineSpacing(heightOffset * 2);
        fontConfig.setCharSpacing(0);

        ConfigColor colorConfig = new ConfigColor();
        colorConfig.load(emptyProps, ignoreReport);

        colorConfig.setBgColor(Color.WHITE);
        colorConfig.setFgColor(Color.WHITE);
        colorConfig.setBorderColor(Color.WHITE);
        colorConfig.setHighlight(Color.GREEN);
        colorConfig.setChromaColor(Color.GREEN);
        colorConfig.setColorUsername(false);
        colorConfig.setColorTimestamp(false);
        colorConfig.setColorMessage(false);
        colorConfig.setColorJoin(false);
        colorConfig.setUseTwitchColors(true);

        ConfigMessage messageConfig = new ConfigMessage();
        messageConfig.load(emptyProps, ignoreReport);

        messageConfig.setJoinMessages(false);
        messageConfig.setShowUsernames(false);
        messageConfig.setShowTimestamps(false);
        messageConfig.setTimeFormat("[HH:mm:ss]");
        messageConfig.setQueueSize(Integer.MAX_VALUE);
        messageConfig.setMessageSpeed(Integer.MAX_VALUE, null);
        messageConfig.setExpirationTime(Integer.MAX_VALUE, null);
        messageConfig.setHideEmptyBorder(false);
        messageConfig.setHideEmptyBackground(false);
        messageConfig.setCaseResolutionType(UsernameCaseResolutionType.NONE);
        messageConfig.setSpecifyCaseAllowed(false);
        messageConfig.setMessageCasing(MessageCasing.MIXED_CASE);

        ConfigEmoji emojiConfig = new ConfigEmoji();
        emojiConfig.load(emptyProps, ignoreReport);
        emojiConfig.setEmojiEnabled(false);

        Collection<DropdownFont> allFonts = ControlPanelFont.getAllFonts();
        SpriteFont font;
        Message exampleMsg;
        int y = 0;
        final int xOffset = 5;
        int height = 0;
        int width = 0;
        for (DropdownFont ddFont : allFonts)
        {
            fontConfig.setFontFilename(ddFont.getFontFilename());
            fontConfig.setFontType(ddFont.getDefaultType());
            fontConfig.setCharSpacing(ddFont.getDefaultType() == FontType.FIXED_WIDTH ? 0 : 1);
            font = new SpriteFont(fontConfig);
            font.updateForConfigChange();
            exampleMsg = getExampleMessage(ddFont.getFontFilename(), lineFormatInput.getText());
            SpriteCharacterKey[] text = exampleMsg.getText(null, messageConfig, emojiConfig);
            height += font.getLineHeightScaled();
            int msgWidth = xOffset * 2;
            for (int c = 0; c < text.length; c++)
            {
                msgWidth += font.getCharacterWidth(null, text[c], emojiConfig);
            }
            width = (int) Math.max(width, msgWidth);
        }

        BufferedImage exampleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D exampleGraphics = (Graphics2D) exampleImage.getGraphics();

        for (DropdownFont ddFont : allFonts)
        {
            fontConfig.setFontFilename(ddFont.getFontFilename());
            fontConfig.setFontType(ddFont.getDefaultType());
            fontConfig.setCharSpacing(ddFont.getDefaultType() == FontType.FIXED_WIDTH ? 0 : 1);
            font = new SpriteFont(fontConfig);
            font.updateForConfigChange();
            exampleMsg = getExampleMessage(ddFont.getFontFilename(), lineFormatInput.getText());
            exampleMsg.setCompletelyDrawn();
            exampleGraphics.setColor(bgColorButton.getColor());
            final int heightScaled = font.getLineHeightScaled();
            exampleGraphics.fillRect(0, y, width, y + heightScaled);
            colorConfig.setFgColor(fontColorButton.getColor());
            font.drawMessage(exampleGraphics, exampleGraphics.getFontMetrics(), exampleMsg, fontColorButton.getColor(), colorConfig, messageConfig, emojiConfig, null, xOffset, (int) (y + heightOffset * fontConfig.getFontScale()), 0, Integer.MAX_VALUE, Integer.MAX_VALUE, false, null, null);
            y += heightScaled;
        }

        JFileChooser exampleImageChooser = new JFileChooser();
        FileFilter pngFileFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
        exampleImageChooser.setFileFilter(pngFileFilter);
        File saveFile = ControlWindow.getTargetSaveFile(exampleImageChooser, "png");
        if (saveFile != null)
        {
            try
            {
                ImageIO.write(exampleImage, "png", saveFile);
            }
            catch (Exception e)
            {
                logger.error("Unable to save example image", e);
            }
        }
    }

}