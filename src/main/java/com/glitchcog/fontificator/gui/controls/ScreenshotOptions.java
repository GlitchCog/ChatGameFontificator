package com.glitchcog.fontificator.gui.controls;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;

/**
 * Options for the JFileChooser for saving a screenshot
 * 
 * @author Matt Yanos
 */
public class ScreenshotOptions extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * Checkbox to determine whether metadata should be saved in the PNG image file. This includes the name of the video
     * game use for the font and border, a title stating the image is a screenshot, and identification of this program
     * as the creating software
     */
    private JCheckBox metadata;

    /**
     * Checkbox to determine whether the chroma key color should be made transparent in the PNG image file.
     */
    private JCheckBox chromaTransparency;

    public ScreenshotOptions()
    {
        chromaTransparency = new JCheckBox("Chroma Transparency");
        chromaTransparency.setSelected(true);

        metadata = new JCheckBox("Save Metadata");
        metadata.setSelected(true);

        setLayout(new GridBagLayout());

        setBorder(new TitledBorder(ControlPanelBase.getBaseBorder(), "Screenshot Options"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridy = 0;

        add(chromaTransparency, gbc);
        gbc.gridy++;

        add(metadata, gbc);
        gbc.gridy++;

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        add(new JPanel(), gbc);
        gbc.gridy++;
    }

    /**
     * Get whether metadata should be saved in the PNG image file. This includes the name of the video game use for the
     * font and border, a title stating the image is a screenshot, and identification of this program as the creating
     * software
     * 
     * @return whether metadata should be saved
     */
    public boolean isMetadataEnabled()
    {
        return metadata.isSelected();
    }

    /**
     * Get whether the chroma key color should be made transparent in the PNG image file.
     * 
     * @return whether the chroma color should be made transparent
     */
    public boolean isTransparencyEnabled()
    {
        return chromaTransparency.isSelected();
    }
}
