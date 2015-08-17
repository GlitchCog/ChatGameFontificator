package com.glitchcog.fontificator.gui.controls.panel;

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

import javax.swing.JButton;
import javax.swing.JComboBox;
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
    private final static DropdownLabel CUSTOM_KEY = new DropdownLabel(null, "Custom...");

    private static final Map<DropdownLabel, DropdownFont> PRESET_FONT_FILE_MAP = new LinkedHashMap<DropdownLabel, DropdownFont>()
    {
        private static final long serialVersionUID = 1L;
        {
            put(CUSTOM_KEY, null);
            put(new DropdownLabel("Chrono", "Chrono Cross"), new DropdownFont("cc_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Chrono", "Chrono Trigger"), new DropdownFont("ct_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Crystalis", "Crystalis"), new DropdownFont("crystalis_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior"), new DropdownFont("dw1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior II"), new DropdownFont("dw2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III"), new DropdownFont("dw3_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III (GBC) Dialog"), new DropdownFont("dw3gbc_dialog_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III (GBC) Fight"), new DropdownFont("dw3gbc_fight_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior IV"), new DropdownFont("dw4_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Earthbound", "Earthbound Zero"), new DropdownFont("eb0_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Earthbound", "Earthbound Zero Bold"), new DropdownFont("eb0_bold_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Earthbound", "Earthbound"), new DropdownFont("eb_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Earthbound", "Earthbound Mr. Saturn"), new DropdownFont("eb_saturn_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Earthbound", "Mother 3"), new DropdownFont("m3_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy"), new DropdownFont("ff1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy VI"), new DropdownFont("ff6_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy VI (Battle)"), new DropdownFont("ff6_battle_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Ghosts and Goblins", "Ghosts n Goblins"), new DropdownFont("gng_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Golden Sun", "Golden Sun"), new DropdownFont("gsun_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Golden Sun", "Golden Sun (Battle)"), new DropdownFont("gsun_battle_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Metroid", "Metroid"), new DropdownFont("metroid_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Pokemon", "Pokemon Red/Blue"), new DropdownFont("pkmnrb_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Pokemon", "Pokemon Fire Red/Leaf Green"), new DropdownFont("pkmnfrlg_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Kunio-kun", "River City Ransom"), new DropdownFont("rcr_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros."), new DropdownFont("smb1_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 2"), new DropdownFont("smb2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3"), new DropdownFont("smb3_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3 HUD"), new DropdownFont("smb3_hud_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3 HUD (Lowercase)"), new DropdownFont("smb3_hud_lowercase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Secret of Evermore", "Secret of Evermore"), new DropdownFont("soe_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Tales", "Tales of Symphonia"), new DropdownFont("tos_font.png", FontType.VARIABLE_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda"), new DropdownFont("loz_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda (Lowercase)"), new DropdownFont("loz_lowercase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "Zelda II"), new DropdownFont("zelda2_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "Zelda II (Lowercase)"), new DropdownFont("zelda2_lowercase_font.png", FontType.FIXED_WIDTH));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: A Link to the Past"), new DropdownFont("lttp_font.png", FontType.VARIABLE_WIDTH));
        }
    };

    private static final Map<DropdownLabel, DropdownBorder> PRESET_BORDER_FILE_MAP = new LinkedHashMap<DropdownLabel, DropdownBorder>()
    {
        private static final long serialVersionUID = 1L;
        {
            put(CUSTOM_KEY, null);
            put(new DropdownLabel("Chrono", "Chrono Cross"), new DropdownBorder("cc_border.png"));
            put(new DropdownLabel("Chrono", "Chrono Trigger"), new DropdownBorder("ct_border.png"));
            put(new DropdownLabel("Cyrstalis", "Crystalis"), new DropdownBorder("crystalis_border.png"));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior"), new DropdownBorder("dw1_border.png"));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior II"), new DropdownBorder("dw2_border.png"));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III"), new DropdownBorder("dw3_border.png"));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior III (GBC)"), new DropdownBorder("dw3gbc_border.png"));
            put(new DropdownLabel("Dragon Warrior", "Dragon Warrior IV"), new DropdownBorder("dw4_border.png"));
            put(new DropdownLabel("Earthbound", "Earthbound Zero"), new DropdownBorder("eb0_border.png"));
            put(new DropdownLabel("Earthbound", "Earthbound Plain"), new DropdownBorder("eb_plain.png"));
            put(new DropdownLabel("Earthbound", "Earthbound Mint"), new DropdownBorder("eb_mint.png"));
            put(new DropdownLabel("Earthbound", "Earthbound Strawberry"), new DropdownBorder("eb_strawberry.png"));
            put(new DropdownLabel("Earthbound", "Earthbound Banana"), new DropdownBorder("eb_banana.png"));
            put(new DropdownLabel("Earthbound", "Earthbound Peanut"), new DropdownBorder("eb_peanut.png"));
            put(new DropdownLabel("Earthbound", "Mother 3"), new DropdownBorder("m3_border.png"));
            put(new DropdownLabel("Final Fantasy", "Final Fantasy"), new DropdownBorder("ff1_border.png"));
            put(new DropdownLabel("Final Fantasy", "Final Fantas VI"), new DropdownBorder("ff6_border.png"));
            put(new DropdownLabel("Golden Sun", "Golden Sun"), new DropdownBorder("gsun_border.png"));
            put(new DropdownLabel("Metroid", "Metroid"), new DropdownBorder("metroid_border.png"));
            put(new DropdownLabel("Metroid", "Metroid Pipes"), new DropdownBorder("metroid_pipe_border.png"));
            put(new DropdownLabel("Metroid", "Metroid Mother Brain Glass"), new DropdownBorder("metroid_glass_border.png"));
            put(new DropdownLabel("Pokemon", "Pokemon Red/Blue"), new DropdownBorder("pkmnrb_border.png"));
            put(new DropdownLabel("Pokemon", "Pokemon Fire Red/Leaf Green"), new DropdownBorder("pkmnfrlg_border.png"));
            put(new DropdownLabel("Kunio-kun", "River City Ransom"), new DropdownBorder("rcr_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. Brick"), new DropdownBorder("smb1_brick_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. Cloud"), new DropdownBorder("smb1_cloud_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. Rock"), new DropdownBorder("smb1_rock_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. Seabed"), new DropdownBorder("smb1_seabed_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. Stone"), new DropdownBorder("smb1_stone_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. Empty Block"), new DropdownBorder("smb1_used_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. 2 Pause"), new DropdownBorder("smb2_pause_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3 HUD"), new DropdownBorder("smb3_hud_border.png"));
            put(new DropdownLabel("Mario", "Super Mario Bros. 3 Letter"), new DropdownBorder("smb3_letter_border.png"));
            put(new DropdownLabel("Secret of Evermore", "Secret of Evermore"), new DropdownBorder("soe_border.png"));
            put(new DropdownLabel("Tales", "Tales of Symphonia B"), new DropdownBorder("tos_b_border.png"));
            put(new DropdownLabel("Tales", "Tales of Symphonia C"), new DropdownBorder("tos_c_border.png"));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Bush"), new DropdownBorder("loz_bush_border.png"));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Rock"), new DropdownBorder("loz_rock_border.png"));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Dungeon"), new DropdownBorder("loz_dungeon_border.png"));
            put(new DropdownLabel("Zelda", "The Legend of Zelda Story"), new DropdownBorder("loz_story_border.png"));
            put(new DropdownLabel("Zelda", "Zelda II"), new DropdownBorder("zelda2_border.png"));
            put(new DropdownLabel("Zelda", "The Legend of Zelda: A Link to the Past"), new DropdownBorder("lttp_border.png"));
        }
    };

    private LabeledInput fontFilenameInput;

    private ComboMenuBar fontPresetDropdown;

    private JComboBox<FontType> fontTypeDropdown;

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

    private LabeledSlider lineSpacingSlider;

    private LabeledSlider charSpacingSlider;

    private JButton unknownCharPopupButton;

    private CharacterPicker charPicker;

    private JLabel unknownCharLabel;

    private ConfigFont config;

    private ChangeListener sliderListener;

    private ActionListener dropdownListener;

    /**
     * Construct a font control panel
     * 
     * @param fProps
     * @param chatWindow
     */
    public ControlPanelFont(FontificatorProperties fProps, ChatWindow chatWindow)
    {
        super("Font/Border", fProps, chatWindow);

        fontTypeDropdown.addActionListener(dropdownListener);

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
                    config.setFontScale(fontScaleSlider.getValue());
                }
                else if (borderScaleSlider.getSlider().equals(source))
                {
                    config.setBorderScale(borderScaleSlider.getValue());
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
                else if (lineSpacingSlider.getSlider().equals(source))
                {
                    config.setLineSpacing(lineSpacingSlider.getValue());
                }
                else if (charSpacingSlider.getSlider().equals(source))
                {
                    config.setCharSpacing(charSpacingSlider.getValue());
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
                    fontTypeDropdown.setSelectedItem(PRESET_FONT_FILE_MAP.get(key).getDefaultType());
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
                    borderFilenameInput.setText(PRESET_BORDER_FILE_MAP.get(key).getBorderFilename());
                }
                updateFontOrBorder(false);
            }
        };

        dropdownListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                config.setFontType((FontType) fontTypeDropdown.getSelectedItem());
                spaceWidthSlider.setEnabled(FontType.VARIABLE_WIDTH.equals(config.getFontType()));
                updateFontOrBorder(true);
            }
        };

        unknownCharLabel = new JLabel("Missing Character Substitute: ");
        charPicker = new CharacterPicker(ControlWindow.me, fProps.getFontConfig(), unknownCharLabel, chat);

        Map<String, List<String>> fontMenuMap = getMenuMapFromPresets(PRESET_FONT_FILE_MAP.keySet());
        fontMenuMap.put(CUSTOM_KEY.getLabel(), null);
        Map<String, List<String>> borderMenuMap = getMenuMapFromPresets(PRESET_BORDER_FILE_MAP.keySet());
        borderMenuMap.put(CUSTOM_KEY.getLabel(), null);

        fontTypeDropdown = new JComboBox<FontType>(FontType.values());
        fontFilenameInput = new LabeledInput("Font Filename", 32);
        fontPresetDropdown = new ComboMenuBar(fontMenuMap, fontAl);
        borderFilenameInput = new LabeledInput("Border Filename", 32);
        borderPresetDropdown = new ComboMenuBar(borderMenuMap, borderAl);
        gridWidthInput = new LabeledInput("Grid Width", 4);
        gridHeightInput = new LabeledInput("Grid Height", 4);
        fontScaleSlider = new LabeledSlider("Font Scale", "x", ConfigFont.MIN_FONT_SCALE, ConfigFont.MAX_FONT_SCALE);
        borderScaleSlider = new LabeledSlider("Border Scale", "x", ConfigFont.MIN_BORDER_SCALE, ConfigFont.MAX_BORDER_SCALE);
        borderInsetXSlider = new LabeledSlider("X", "pixels", ConfigFont.MIN_BORDER_INSET, ConfigFont.MAX_BORDER_INSET);
        borderInsetYSlider = new LabeledSlider("Y", "pixels", ConfigFont.MIN_BORDER_INSET, ConfigFont.MAX_BORDER_INSET);
        characterKeyInput = new LabeledInput("Character Key", 32);
        spaceWidthSlider = new LabeledSlider("Variable Width Space", "%", ConfigFont.MIN_SPACE_WIDTH, ConfigFont.MAX_SPACE_WIDTH);
        lineSpacingSlider = new LabeledSlider("Line Spacing", "pixels", ConfigFont.MIN_LINE_SPACING, ConfigFont.MAX_LINE_SPACING);
        charSpacingSlider = new LabeledSlider("Char Spacing", "pixels", ConfigFont.MIN_CHAR_SPACING, ConfigFont.MAX_LINE_SPACING);
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
        lineSpacingSlider.addChangeListener(sliderListener);
        charSpacingSlider.addChangeListener(sliderListener);

        JPanel fontPanel = new JPanel(new GridBagLayout());
        JPanel borderPanel = new JPanel(new GridBagLayout());
        JPanel unknownPanel = new JPanel(new GridBagLayout());

        fontPanel.setBorder(new TitledBorder(baseBorder, "Font"));
        borderPanel.setBorder(new TitledBorder(baseBorder, "Border"));
        unknownPanel.setBorder(new TitledBorder(baseBorder, "Missing Characters"));

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

        fontPanel.add(fontTypeDropdown, fontGbc);
        fontGbc.gridy++;
        fontPanel.add(lineSpacingSlider, fontGbc);
        fontGbc.gridy++;
        fontPanel.add(charSpacingSlider, fontGbc);
        fontGbc.gridy++;
        fontPanel.add(spaceWidthSlider, fontGbc);
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

        unknownPanel.add(unknownCharLabel, unknownGbc);
        unknownGbc.gridx++;
        unknownPanel.add(unknownCharPopupButton, unknownGbc);
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
        List<String> errors = new ArrayList<String>();
        if (isFont)
        {
            config.validateFontFile(errors, fontFilenameInput.getText());
            config.validateStrings(errors, gridWidthInput.getText(), gridHeightInput.getText(), characterKeyInput.getText(), Character.toString(charPicker.getSelectedChar()));
        }
        else
        {
            config.validateBorderFile(errors, borderFilenameInput.getText());
        }

        if (errors.isEmpty())
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
            ChatWindow.popup.handleProblem(errors);
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
        fontScaleSlider.setValue(config.getFontScale());
        borderScaleSlider.setValue(config.getBorderScale());
        borderInsetXSlider.setValue(config.getBorderInsetX());
        borderInsetYSlider.setValue(config.getBorderInsetY());
        characterKeyInput.setText(config.getCharacterKey());
        charPicker.setSelectedChar(config.getUnknownChar());
        spaceWidthSlider.setValue(config.getSpaceWidth());
        lineSpacingSlider.setValue(config.getLineSpacing());
        charSpacingSlider.setValue(config.getCharSpacing());
        fontTypeDropdown.setSelectedItem(config.getFontType());

        spaceWidthSlider.setEnabled(FontType.VARIABLE_WIDTH.equals(config.getFontType()));

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
        // filled from config on preset loads, and it shouldn't be the resposibility of actionlisteners attached to UI
        // components to update the display
        updateFontOrBorder(true);

        // Also, the border must be updated here too.
        updateFontOrBorder(false);
    }

    @Override
    protected List<String> validateInput()
    {
        List<String> errors = new ArrayList<String>();

        config.validateFontFile(errors, fontFilenameInput.getText());
        config.validateBorderFile(errors, borderFilenameInput.getText());

        final String widthStr = gridWidthInput.getText();
        final String heightStr = gridHeightInput.getText();
        final String charKey = characterKeyInput.getText();
        final String unknownStr = Character.toString(charPicker.getSelectedChar());

        config.validateStrings(errors, widthStr, heightStr, charKey, unknownStr);

        return errors;
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setBorderFilename(borderFilenameInput.getText());
        config.setFontFilename(fontFilenameInput.getText());
        config.setFontType((FontType) fontTypeDropdown.getSelectedItem());
        config.setGridWidth(Integer.parseInt(gridWidthInput.getText()));
        config.setGridHeight(Integer.parseInt(gridHeightInput.getText()));
        config.setFontScale(fontScaleSlider.getValue());
        config.setBorderScale(borderScaleSlider.getValue());
        config.setBorderInsetX(borderInsetXSlider.getValue());
        config.setBorderInsetY(borderInsetYSlider.getValue());
        config.setCharacterKey(characterKeyInput.getText());
        config.setUnknownChar(charPicker.getSelectedChar());
        config.setSpaceWidth(spaceWidthSlider.getValue());
        config.setLineSpacing(lineSpacingSlider.getValue());
        config.setCharSpacing(charSpacingSlider.getValue());
    }

}
