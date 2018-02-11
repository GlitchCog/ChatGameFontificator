package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.FontType;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.AssetIndexLoader;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.CharacterPicker;
import com.glitchcog.fontificator.gui.component.LabeledInput;
import com.glitchcog.fontificator.gui.component.LabeledSlider;
import com.glitchcog.fontificator.gui.component.combomenu.ComboMenuBar;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownBorder;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownFont;
import com.glitchcog.fontificator.gui.controls.panel.model.DropdownLabel;

/**
 * Control Panel containing all the Font and Border options
 * 
 * @author Matt Yanos
 */
public class ControlPanelFont extends ControlPanelBase
{
    private static final Logger logger = Logger.getLogger(ControlPanelFont.class);

    private static final long serialVersionUID = 1L;

    /**
     * Text of the selection option in the font and border preset dropdown menus to prompt the user to specify her own
     * file
     */
    public static final DropdownLabel CUSTOM_KEY = new DropdownLabel(null, "Custom...");

    private static final Color SCALE_EVEN = Color.BLACK;

    private static final Color SCALE_UNEVEN = new Color(0x661033);

    private static final Map<DropdownLabel, DropdownFont> PRESET_FONT_FILE_MAP = AssetIndexLoader.loadFonts();

    /**
     * A reverse lookup through the map to get the actual name of a game for a font
     * 
     * @param font
     *            filename
     * @return game name
     */
    public static String getFontGameName(String filename)
    {
        for (Entry<DropdownLabel, DropdownFont> e : PRESET_FONT_FILE_MAP.entrySet())
        {
            if (CUSTOM_KEY.equals(e.getKey()))
            {
                continue;
            }

            final String fontFilename = e.getValue().getFontFilename();

            if (fontFilename.equals(filename))
            {
                return e.getKey().getLabel();
            }
        }
        return "Unknown";
    }

    public static List<DropdownFont> getAllFonts()
    {
        List<DropdownFont> allFonts = new ArrayList<DropdownFont>();
        for (DropdownLabel ddfKey : PRESET_FONT_FILE_MAP.keySet())
        {
            if (CUSTOM_KEY.equals(ddfKey))
            {
                continue;
            }
            allFonts.add(PRESET_FONT_FILE_MAP.get(ddfKey));
        }
        return allFonts;
    }

    private static Map<DropdownLabel, DropdownBorder> PRESET_BORDER_FILE_MAP = AssetIndexLoader.loadBorders();

    /**
     * A reverse lookup through the map to get the actual name of a game for a border
     * 
     * @param border
     *            filename
     * @return game name
     */
    public static String getBorderGameName(String filename)
    {
        for (Entry<DropdownLabel, DropdownBorder> e : PRESET_BORDER_FILE_MAP.entrySet())
        {
            if (CUSTOM_KEY.equals(e.getKey()))
            {
                continue;
            }

            final String borderFilename = e.getValue().getBorderFilename();

            if (borderFilename.equals(filename))
            {
                return e.getKey().getLabel();
            }
        }
        return "Unknown";
    }

    private LabeledInput fontFilenameInput;

    private ComboMenuBar fontPresetDropdown;

    private JCheckBox fontTypeCheckbox;

    private LabeledInput borderFilenameInput;

    private ComboMenuBar borderPresetDropdown;

    private LabeledInput gridWidthInput;

    private LabeledInput gridHeightInput;

    private JFileChooser fontPngPicker;

    private JFileChooser borderPngPicker;

    private LabeledSlider fontScaleSlider;

    private LabeledSlider borderScaleSlider;

    private LabeledSlider borderInsetXSlider;

    private LabeledSlider borderInsetYSlider;

    private LabeledInput characterKeyInput;

    private LabeledSlider spaceWidthSlider;

    private LabeledSlider baselineOffsetSlider;

    private LabeledSlider lineSpacingSlider;

    private LabeledSlider charSpacingSlider;

    private LabeledSlider messageSpacingSlider;

    private JButton unknownCharPopupButton;

    private CharacterPicker charPicker;

    private JLabel unknownCharLabel;

    private JCheckBox extendedCharBox;

    private ConfigFont config;

    private ChangeListener sliderListener;

    private ActionListener fontTypeListener;

    private ControlPanelColor colorPanel;

    /**
     * Construct a font control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param logBox
     * @param colorPanel
     *            Used to set the border color tint when the border is changed
     */
    public ControlPanelFont(FontificatorProperties fProps, ChatWindow chatWindow, LogBox logBox, ControlPanelColor colorPanel)
    {
        super("Font/Border", fProps, chatWindow, logBox);

        this.colorPanel = colorPanel;

        fontTypeCheckbox.addActionListener(fontTypeListener);

        fontPngPicker = new JFileChooser();
        borderPngPicker = new JFileChooser();
        FileFilter pngFilter = new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                return f.isDirectory() || (f.getName().toLowerCase().endsWith(".png"));
            }

            @Override
            public String getDescription()
            {
                return "PNG Image (*.png)";
            }
        };
        fontPngPicker.setFileFilter(pngFilter);
        borderPngPicker.setFileFilter(pngFilter);
    }

    @Override
    protected void build()
    {
        sliderListener = new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                JSlider source = (JSlider) e.getSource();
                if (fontScaleSlider.getSlider().equals(source))
                {
                    config.setFontScale(fontScaleSlider.getScaledValue());
                    fontScaleSlider.setValueTextColor(((int) config.getFontScale() == config.getFontScale()) ? SCALE_EVEN : SCALE_UNEVEN);
                }
                else if (borderScaleSlider.getSlider().equals(source))
                {
                    config.setBorderScale(borderScaleSlider.getScaledValue());
                    borderScaleSlider.setValueTextColor(((int) config.getBorderScale() == config.getBorderScale()) ? SCALE_EVEN : SCALE_UNEVEN);
                }
                else if (borderInsetXSlider.getSlider().equals(source))
                {
                    config.setBorderInsetX(borderInsetXSlider.getValue());
                }
                else if (borderInsetYSlider.getSlider().equals(source))
                {
                    config.setBorderInsetY(borderInsetYSlider.getValue());
                }
                else if (spaceWidthSlider.getSlider().equals(source))
                {
                    config.setSpaceWidth(spaceWidthSlider.getValue());
                }
                else if (baselineOffsetSlider.getSlider().equals(source))
                {
                    config.setBaselineOffset(baselineOffsetSlider.getValue());
                }
                else if (lineSpacingSlider.getSlider().equals(source))
                {
                    config.setLineSpacing(lineSpacingSlider.getValue());
                }
                else if (charSpacingSlider.getSlider().equals(source))
                {
                    config.setCharSpacing(charSpacingSlider.getValue());
                }
                else if (messageSpacingSlider.getSlider().equals(source))
                {
                    config.setMessageSpacing(messageSpacingSlider.getValue());
                }
                chat.repaint();
            }
        };

        ActionListener fontAl = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JMenuItem source = (JMenuItem) e.getSource();
                DropdownLabel key = new DropdownLabel(source.getText());
                if (CUSTOM_KEY.equals(key))
                {
                    int selectionResult = fontPngPicker.showDialog(ControlWindow.me, "Select Font PNG");
                    if (selectionResult == JFileChooser.APPROVE_OPTION)
                    {
                        fontFilenameInput.setText(fontPngPicker.getSelectedFile().getAbsolutePath());
                        fontPresetDropdown.setSelectedText(fontPngPicker.getSelectedFile().getName());
                    }
                }
                else
                {
                    fontFilenameInput.setText(PRESET_FONT_FILE_MAP.get(key).getFontFilename());
                    fontTypeCheckbox.setSelected(FontType.VARIABLE_WIDTH.equals(PRESET_FONT_FILE_MAP.get(key).getDefaultType()));
                    spaceWidthSlider.setEnabled(fontTypeCheckbox.isSelected());
                }
                updateFontOrBorder(true);
            }
        };

        ActionListener borderAl = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JMenuItem source = (JMenuItem) e.getSource();
                DropdownLabel key = new DropdownLabel(source.getText());
                if (CUSTOM_KEY.equals(key))
                {
                    int selectionResult = borderPngPicker.showDialog(ControlWindow.me, "Select Border PNG");
                    if (selectionResult == JFileChooser.APPROVE_OPTION)
                    {
                        borderFilenameInput.setText(borderPngPicker.getSelectedFile().getAbsolutePath());
                        borderPresetDropdown.setSelectedText(borderPngPicker.getSelectedFile().getName());
                    }
                }
                else
                {
                    DropdownBorder border = PRESET_BORDER_FILE_MAP.get(key);
                    borderFilenameInput.setText(border.getBorderFilename());
                    colorPanel.setBorderColor(border.getDefaultTint());
                }
                updateFontOrBorder(false);
            }
        };

        fontTypeListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                config.setFontType(fontTypeCheckbox.isSelected() ? FontType.VARIABLE_WIDTH : FontType.FIXED_WIDTH);
                spaceWidthSlider.setEnabled(fontTypeCheckbox.isSelected());
                updateFontOrBorder(true);
            }
        };

        extendedCharBox = new JCheckBox("Display Extended Characters");
        extendedCharBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final boolean ecbSelected = extendedCharBox.isSelected();

                config.setExtendedCharEnabled(ecbSelected);
                unknownCharPopupButton.setEnabled(!ecbSelected);
                unknownCharLabel.setEnabled(!ecbSelected);
                chat.repaint();
            }
        });

        unknownCharLabel = new JLabel("");
        charPicker = new CharacterPicker(ControlWindow.me, fProps.getFontConfig(), unknownCharLabel, chat);

        Map<String, List<String>> fontMenuMap = getMenuMapFromPresets(PRESET_FONT_FILE_MAP.keySet());
        fontMenuMap.put(CUSTOM_KEY.getLabel(), null);
        Map<String, List<String>> borderMenuMap = getMenuMapFromPresets(PRESET_BORDER_FILE_MAP.keySet());
        borderMenuMap.put(CUSTOM_KEY.getLabel(), null);

        fontTypeCheckbox = new JCheckBox("Variable Width Characters");
        fontFilenameInput = new LabeledInput("Font Filename", 32);
        fontPresetDropdown = new ComboMenuBar(fontMenuMap, fontAl);
        borderFilenameInput = new LabeledInput("Border Filename", 32);
        borderPresetDropdown = new ComboMenuBar(borderMenuMap, borderAl);
        gridWidthInput = new LabeledInput("Grid Width", 4);
        gridHeightInput = new LabeledInput("Grid Height", 4);
        fontScaleSlider = new LabeledSlider("Font Size", "x", ConfigFont.MIN_FONT_SCALE, ConfigFont.MAX_FONT_SCALE, ConfigFont.FONT_BORDER_SCALE_GRANULARITY);
        borderScaleSlider = new LabeledSlider("Border Size", "x", ConfigFont.MIN_BORDER_SCALE, ConfigFont.MAX_BORDER_SCALE, ConfigFont.FONT_BORDER_SCALE_GRANULARITY);
        borderInsetXSlider = new LabeledSlider("X", "pixels", ConfigFont.MIN_BORDER_INSET, ConfigFont.MAX_BORDER_INSET);
        borderInsetYSlider = new LabeledSlider("Y", "pixels", ConfigFont.MIN_BORDER_INSET, ConfigFont.MAX_BORDER_INSET);
        characterKeyInput = new LabeledInput("Character Key", 32);
        spaceWidthSlider = new LabeledSlider("Space Width", "%", ConfigFont.MIN_SPACE_WIDTH, ConfigFont.MAX_SPACE_WIDTH);
        baselineOffsetSlider = new LabeledSlider("Baseline Height Offset", "pixels", ConfigFont.MIN_BASELINE_OFFSET, ConfigFont.MAX_BASELINE_OFFSET);
        lineSpacingSlider = new LabeledSlider("Line Spacing", "pixels", ConfigFont.MIN_LINE_SPACING, ConfigFont.MAX_LINE_SPACING);
        charSpacingSlider = new LabeledSlider("Char Spacing", "pixels", ConfigFont.MIN_CHAR_SPACING, ConfigFont.MAX_CHAR_SPACING);
        messageSpacingSlider = new LabeledSlider("Message Spacing", "pixels", ConfigFont.MIN_MESSAGE_SPACING, ConfigFont.MAX_MESSAGE_SPACING);
        unknownCharPopupButton = new JButton("Select Missing Character");

        unknownCharPopupButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                charPicker.setVisible(true);
            }
        });

        fontFilenameInput.setEnabled(false);
        borderFilenameInput.setEnabled(false);

        fontScaleSlider.addChangeListener(sliderListener);
        borderScaleSlider.addChangeListener(sliderListener);
        borderInsetXSlider.addChangeListener(sliderListener);
        borderInsetYSlider.addChangeListener(sliderListener);
        spaceWidthSlider.addChangeListener(sliderListener);
        baselineOffsetSlider.addChangeListener(sliderListener);
        lineSpacingSlider.addChangeListener(sliderListener);
        charSpacingSlider.addChangeListener(sliderListener);
        messageSpacingSlider.addChangeListener(sliderListener);

        JPanel fontPanel = new JPanel(new GridBagLayout());
        JPanel borderPanel = new JPanel(new GridBagLayout());
        JPanel unknownPanel = new JPanel(new GridBagLayout());

        fontPanel.setBorder(new TitledBorder(baseBorder, "Font"));
        borderPanel.setBorder(new TitledBorder(baseBorder, "Border"));
        unknownPanel.setBorder(new TitledBorder(baseBorder, "Extended and Unicode Characters"));

        GridBagConstraints fontGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);
        GridBagConstraints borderGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);
        GridBagConstraints unknownGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);

        // Fields are still used and stored in properties files when saved, but the values are either fixed or meant to
        // be filled by other components
        // add(fontFilenameInput);
        // add(borderFilenameInput);
        // add(gridWidthInput);
        // add(gridHeightInput);
        // add(characterKeyInput);

        fontPanel.add(fontPresetDropdown, fontGbc);
        fontGbc.gridx++;
        // This slider being on the same row as the preset dropdown keeps the combo menu bar from collapsing to no
        // height in the layout
        fontPanel.add(fontScaleSlider, fontGbc);
        fontGbc.gridwidth = 2;
        fontGbc.gridx = 0;
        fontGbc.gridy++;

        fontPanel.add(lineSpacingSlider, fontGbc);
        fontGbc.gridy++;
        fontPanel.add(charSpacingSlider, fontGbc);
        fontGbc.gridy++;
        fontPanel.add(messageSpacingSlider, fontGbc);
        fontGbc.gridy++;

        JPanel variableWidthPanel = new JPanel(new GridBagLayout());
        GridBagConstraints vwpGbc = getGbc();
        vwpGbc.anchor = GridBagConstraints.EAST;
        vwpGbc.weightx = 0.0;
        vwpGbc.fill = GridBagConstraints.NONE;
        variableWidthPanel.add(fontTypeCheckbox, vwpGbc);
        vwpGbc.anchor = GridBagConstraints.WEST;
        vwpGbc.weightx = 1.0;
        vwpGbc.fill = GridBagConstraints.HORIZONTAL;
        vwpGbc.gridx++;
        variableWidthPanel.add(spaceWidthSlider, vwpGbc);
        fontPanel.add(variableWidthPanel, fontGbc);
        fontGbc.gridy++;

        fontPanel.add(baselineOffsetSlider, fontGbc);
        fontGbc.gridy++;

        borderPanel.add(borderPresetDropdown, borderGbc);
        borderGbc.gridx++;
        // This slider being on the same row as the preset dropdown keeps the combo menu bar from collapsing to no
        // height in the layout
        borderPanel.add(borderScaleSlider, borderGbc);
        borderGbc.gridwidth = 2;
        borderGbc.gridx = 0;
        borderGbc.gridy++;

        borderGbc.anchor = GridBagConstraints.CENTER;

        borderPanel.add(new JLabel("Font Insets Off Border"), borderGbc);
        borderGbc.gridy++;
        borderPanel.add(borderInsetXSlider, borderGbc);
        borderGbc.gridy++;
        borderPanel.add(borderInsetYSlider, borderGbc);
        borderGbc.gridy++;

        unknownPanel.add(extendedCharBox, unknownGbc);
        unknownGbc.gridx++;
        unknownPanel.add(unknownCharPopupButton, unknownGbc);
        unknownGbc.gridx++;
        unknownPanel.add(unknownCharLabel, unknownGbc);
        unknownGbc.gridx++;

        JPanel everything = new JPanel(new GridBagLayout());
        GridBagConstraints eGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 10, 10);
        everything.add(fontPanel, eGbc);
        eGbc.gridy++;
        everything.add(borderPanel, eGbc);
        eGbc.gridy++;
        everything.add(unknownPanel, eGbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(everything, gbc);

        // Filler panel
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(new JPanel(), gbc);
    }

    /**
     * Turns a list of keys with a single pipe deliminating the parent menu from the menu item into a map that can be a
     * parameter to create a ComboMenuBar
     * 
     * @param keys
     * @return
     */
    private Map<String, List<String>> getMenuMapFromPresets(Collection<DropdownLabel> keys)
    {
        Map<String, List<String>> menuMap = new LinkedHashMap<String, List<String>>();
        for (DropdownLabel key : keys)
        {
            final String menu = key.getGroup();
            final String item = key.getLabel();
            List<String> items = menuMap.get(menu);
            if (items == null)
            {
                items = new ArrayList<String>();
                menuMap.put(menu, items);
            }
            items.add(item);
        }
        return menuMap;
    }

    private boolean updateFontOrBorder(boolean isFont)
    {
        LoadConfigReport report = new LoadConfigReport();
        if (isFont)
        {
            config.validateFontFile(report, fontFilenameInput.getText());
            config.validateStrings(report, gridWidthInput.getText(), gridHeightInput.getText(), characterKeyInput.getText(), Character.toString(charPicker.getSelectedChar()));
        }
        else
        {
            config.validateBorderFile(report, borderFilenameInput.getText());
        }

        if (report.isErrorFree())
        {
            try
            {
                fillConfigFromInput();
                if (isFont)
                {
                    chat.reloadFontFromConfig();
                }
                else
                {
                    chat.reloadBorderFromConfig();
                }
                chat.repaint();
                return true;
            }
            catch (Exception ex)
            {
                logger.error(ex.toString(), ex);
                ChatWindow.popup.handleProblem("Unable to load " + (isFont ? "font" : "border") + " image", ex);
            }
        }
        else
        {
            ChatWindow.popup.handleProblem(report);
        }
        return false;
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        this.config = fProps.getFontConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        borderFilenameInput.setText(config.getBorderFilename());
        fontFilenameInput.setText(config.getFontFilename());
        gridWidthInput.setText(Integer.toString(config.getGridWidth()));
        gridHeightInput.setText(Integer.toString(config.getGridHeight()));
        fontScaleSlider.setScaledValue(config.getFontScale());
        borderScaleSlider.setScaledValue(config.getBorderScale());
        borderInsetXSlider.setValue(config.getBorderInsetX());
        borderInsetYSlider.setValue(config.getBorderInsetY());
        characterKeyInput.setText(config.getCharacterKey());
        extendedCharBox.setSelected(config.isExtendedCharEnabled());
        charPicker.setSelectedChar(config.getUnknownChar());
        spaceWidthSlider.setValue(config.getSpaceWidth());
        baselineOffsetSlider.setValue(config.getBaselineOffset());
        lineSpacingSlider.setValue(config.getLineSpacing());
        charSpacingSlider.setValue(config.getCharSpacing());
        messageSpacingSlider.setValue(config.getMessageSpacing());
        fontTypeCheckbox.setSelected(FontType.VARIABLE_WIDTH.equals(config.getFontType()));
        spaceWidthSlider.setEnabled(fontTypeCheckbox.isSelected());
        final boolean ecbSelected = extendedCharBox.isSelected();
        unknownCharPopupButton.setEnabled(!ecbSelected);
        unknownCharLabel.setEnabled(!ecbSelected);

        boolean found;

        found = false;
        for (Map.Entry<DropdownLabel, DropdownFont> entry : PRESET_FONT_FILE_MAP.entrySet())
        {
            if (entry.getValue() != null && config.getFontFilename().equals(entry.getValue().getFontFilename()))
            {
                found = true;
                fontPresetDropdown.setSelectedText(entry.getKey().getLabel());
            }
        }
        if (!found)
        {
            String filename = new File(fontFilenameInput.getText()).getName();
            fontPresetDropdown.setSelectedText(filename);
        }

        found = false;
        for (Map.Entry<DropdownLabel, DropdownBorder> entry : PRESET_BORDER_FILE_MAP.entrySet())
        {
            if (entry.getValue() != null && config.getBorderFilename().equals(entry.getValue().getBorderFilename()))
            {
                found = true;
                borderPresetDropdown.setSelectedText(entry.getKey().getLabel());
            }
        }
        if (!found)
        {
            String filename = new File(borderFilenameInput.getText()).getName();
            borderPresetDropdown.setSelectedText(filename);
        }

        // Although the font was already updated from the listener attached the the fontTypeDropdown, it should be done
        // here to make it official. If the font and border aren't updated, they could be out of sync with the input
        // filled from config on preset loads, and it shouldn't be the responsibility of actionlisteners attached to UI
        // components to update the display
        updateFontOrBorder(true);

        // Also, the border must be updated here too.
        updateFontOrBorder(false);
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        LoadConfigReport report = new LoadConfigReport();

        config.validateFontFile(report, fontFilenameInput.getText());
        config.validateBorderFile(report, borderFilenameInput.getText());

        final String widthStr = gridWidthInput.getText();
        final String heightStr = gridHeightInput.getText();
        final String charKey = characterKeyInput.getText();
        final String unknownStr = Character.toString(charPicker.getSelectedChar());

        config.validateStrings(report, widthStr, heightStr, charKey, unknownStr);

        return report;
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setBorderFilename(borderFilenameInput.getText());
        config.setFontFilename(fontFilenameInput.getText());
        config.setFontType(fontTypeCheckbox.isSelected() ? FontType.VARIABLE_WIDTH : FontType.FIXED_WIDTH);
        config.setGridWidth(Integer.parseInt(gridWidthInput.getText()));
        config.setGridHeight(Integer.parseInt(gridHeightInput.getText()));
        config.setFontScale(fontScaleSlider.getScaledValue());
        config.setBorderScale(borderScaleSlider.getScaledValue());
        config.setBorderInsetX(borderInsetXSlider.getValue());
        config.setBorderInsetY(borderInsetYSlider.getValue());
        config.setCharacterKey(characterKeyInput.getText());
        config.setExtendedCharEnabled(extendedCharBox.isSelected());
        config.setUnknownChar(charPicker.getSelectedChar());
        config.setSpaceWidth(spaceWidthSlider.getValue());
        config.setBaselineOffset(baselineOffsetSlider.getValue());
        config.setLineSpacing(lineSpacingSlider.getValue());
        config.setCharSpacing(charSpacingSlider.getValue());
        config.setMessageSpacing(messageSpacingSlider.getValue());
    }

}
