package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.glitchcog.fontificator.config.ConfigColor;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.ColorButton;
import com.glitchcog.fontificator.gui.component.palette.Palette;

/**
 * Control Panel containing all the color options
 * 
 * @author Matt Yanos
 */
public class ControlPanelColor extends ControlPanelBase
{
    private static final long serialVersionUID = 1L;

    /**
     * Background color button
     */
    private ColorButton bgColorButton;

    /**
     * Foreground color tint button
     */
    private ColorButton fgColorButton;

    /**
     * Border color tint button
     */
    private ColorButton borderColorButton;

    /**
     * Highlight color tint button
     */
    private ColorButton highlightButton;

    /**
     * Chroma border color button
     */
    private ColorButton chromaColorButton;

    /**
     * The palette UI object that lets you specify a collection of colors that are used for distinctly coloring usernames and messages, depending on the chat's configuration
     */
    private Palette palette;

    /**
     * Checkbox to indicate whether Twitch username colors should override the palette selection, when those colors are provided
     */
    private JCheckBox useTwitchBox;

    /**
     * Checkbox to indicate whether to color join messages
     */
    private JCheckBox joinBox;

    /**
     * Checkbox to indicate whether to color usernames
     */
    private JCheckBox usernameBox;

    /**
     * Checkbox to indicate whether to color timestamps
     */
    private JCheckBox timestampBox;

    /**
     * Checkbox to indicate whether to color the message text
     */
    private JCheckBox messageBox;

    /**
     * The color config object that bridges the UI to the properties file
     */
    private ConfigColor config;

    /**
     * Construct a color control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param logBox
     */
    public ControlPanelColor(FontificatorProperties fProps, ChatWindow chatWindow, LogBox logBox)
    {
        super("Color", fProps, chatWindow, logBox);
    }

    @Override
    protected void build()
    {
        useTwitchBox = new JCheckBox("Override Palette with Twitch Username Colors when Available");
        joinBox = new JCheckBox("Color Join Messages");
        usernameBox = new JCheckBox("Color Usernames");
        timestampBox = new JCheckBox("Color Timestamps");
        messageBox = new JCheckBox("Color Messages");

        ActionListener boxListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JCheckBox source = (JCheckBox) e.getSource();
                if (useTwitchBox.equals(source))
                {
                    config.setUseTwitchColors(source.isSelected());
                }
                else if (joinBox.equals(source))
                {
                    config.setColorJoin(source.isSelected());
                }
                else if (usernameBox.equals(source))
                {
                    config.setColorUsername(source.isSelected());
                }
                else if (timestampBox.equals(source))
                {
                    config.setColorTimestamp(source.isSelected());
                }
                else if (messageBox.equals(source))
                {
                    config.setColorMessage(source.isSelected());
                }
                chat.repaint();
            }
        };

        useTwitchBox.addActionListener(boxListener);
        joinBox.addActionListener(boxListener);
        usernameBox.addActionListener(boxListener);
        timestampBox.addActionListener(boxListener);
        messageBox.addActionListener(boxListener);

        bgColorButton = new ColorButton("Background", Color.BLACK, "Set the color of the background of the chat", this);
        chromaColorButton = new ColorButton("Chroma Key", Color.GREEN, "Set the color for the chroma key border region", this);

        fgColorButton = new ColorButton("Text Tint", Color.WHITE, "Set the color of the text of the chat", this);
        borderColorButton = new ColorButton("Border Tint", Color.WHITE, "Set the color of the border of the chat", this);
        highlightButton = new ColorButton("Highlight Tint", Color.YELLOW, "Set the default color used to highlight the text of the chat", this);

        palette = new Palette(Color.BLACK, this);

        JPanel topColorPanel = new JPanel(new GridLayout(2, 1));
        JPanel botOptionsPanel = new JPanel(new GridBagLayout());
        JPanel palettePanel = new JPanel(new GridBagLayout());
        topColorPanel.setBorder(new TitledBorder(baseBorder, "Color Selections", TitledBorder.CENTER, TitledBorder.TOP));
        palettePanel.setBorder(new TitledBorder(baseBorder, "Palette of Colors drawn from, per Username", TitledBorder.CENTER, TitledBorder.TOP));
        botOptionsPanel.setBorder(new TitledBorder(baseBorder, "Color Options", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel colorPanelA = new JPanel(new GridLayout(1, 2));
        colorPanelA.add(bgColorButton);
        colorPanelA.add(chromaColorButton);

        JPanel colorPanelB = new JPanel(new GridLayout(1, 3));
        colorPanelB.add(fgColorButton);
        colorPanelB.add(highlightButton);
        colorPanelB.add(borderColorButton);

        topColorPanel.add(colorPanelA);
        topColorPanel.add(colorPanelB);

        GridBagConstraints ppGbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, NO_INSETS, 0, 0);
        palettePanel.add(palette, ppGbc);
        ppGbc.gridy++;
        palettePanel.add(useTwitchBox, ppGbc);

        GridBagConstraints optionsGbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);

        botOptionsPanel.add(joinBox, optionsGbc);
        optionsGbc.gridx++;
        botOptionsPanel.add(usernameBox, optionsGbc);
        optionsGbc.gridx = 0;
        optionsGbc.gridy++;
        botOptionsPanel.add(timestampBox, optionsGbc);
        optionsGbc.gridx++;
        botOptionsPanel.add(messageBox, optionsGbc);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(topColorPanel, gbc);
        gbc.gridy++;
        add(palettePanel, gbc);
        gbc.gridy++;
        add(botOptionsPanel, gbc);

        // Filler panel
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc);
        gbc.gridy++;
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        config = fProps.getColorConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        bgColorButton.setColor(config.getBgColor());
        fgColorButton.setColor(config.getFgColor());
        borderColorButton.setColor(config.getBorderColor());
        highlightButton.setColor(config.getHighlight());
        chromaColorButton.setColor(config.getChromaColor());
        palette.reset();
        for (Color col : config.getPalette())
        {
            palette.addColor(col);
        }
        palette.refreshComponents();

        useTwitchBox.setSelected(config.isUseTwitchColors());
        joinBox.setSelected(config.isColorJoin());
        usernameBox.setSelected(config.isColorUsername());
        messageBox.setSelected(config.isColorMessage());
        timestampBox.setSelected(config.isColorTimestamp());

        // Update the control panel palette too
        palette.setBgColor(config.getBgColor());
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        // Nothing to check because there are no string input fields
        return new LoadConfigReport();
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setBgColor(bgColorButton.getColor());
        config.setFgColor(fgColorButton.getColor());
        config.setBorderColor(borderColorButton.getColor());
        config.setHighlight(highlightButton.getColor());
        config.setChromaColor(chromaColorButton.getColor());
        config.setPalette(palette.getColors());
        config.setUseTwitchColors(useTwitchBox.isSelected());
        config.setColorJoin(joinBox.isSelected());
        config.setColorUsername(usernameBox.isSelected());
        config.setColorMessage(messageBox.isSelected());
        config.setColorTimestamp(timestampBox.isSelected());
        palette.setBgColor(bgColorButton.getColor());
    }

}
