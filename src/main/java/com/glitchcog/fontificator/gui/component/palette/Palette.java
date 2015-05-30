package com.glitchcog.fontificator.gui.component.palette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;

/**
 * A Palette is a component for taking user input for a series of colors. It has add and remove buttons and a swatch
 * panel for displaying all the colors. This is the main component of this package.
 * 
 * @author Matt Yanos
 */
public class Palette extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JLabel label;

    private JButton buttonAdd;

    private JButton buttonRem;

    private SwatchPanel swatchPanel;

    private JScrollPane paletteScrollPane;

    private final ControlPanelBase control;

    public Palette(Color bgColor, ControlPanelBase controlPanel)
    {
        this(null, bgColor, controlPanel);
    }

    public Palette(final String label, Color bgColor, ControlPanelBase controlPanel)
    {
        super(new GridBagLayout());

        this.control = controlPanel;

        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);

        this.buttonAdd = new JButton("+");
        this.buttonRem = new JButton("-");
        if (label != null)
        {
            this.label = new JLabel(label);
        }
        this.swatchPanel = new SwatchPanel(bgColor);

        gbc.weightx = 0.0;

        if (label != null)
        {
            gbc.gridheight = 2;
            add(this.label, gbc);
            gbc.gridx++;
        }
        gbc.gridheight = 1;
        add(this.buttonAdd, gbc);
        gbc.gridy++;
        add(this.buttonRem, gbc);
        gbc.gridy = 0;
        gbc.gridx++;
        gbc.gridheight = 2;

        paletteScrollPane = new JScrollPane(this.swatchPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        paletteScrollPane.setPreferredSize(new Dimension(512, getHeight()));

        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.EAST;
        add(paletteScrollPane, gbc);
        gbc.gridx++;

        ActionListener al = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();

                if (source == buttonAdd)
                {
                    Color c = JColorChooser.showDialog(ControlWindow.me, "Add Color to Palette" + (label == null ? "" : " for " + label), swatchPanel.isEmpty() ? null : swatchPanel.getColors().get(swatchPanel.getCount() - 1));
                    if (c != null)
                    {
                        addColor(c);
                        control.update();
                        validate();
                        repaint();
                    }
                }
                else if (source == buttonRem)
                {
                    swatchPanel.removeSelectedColors();
                    control.update();
                    validate();
                    repaint();
                }

            }
        };

        this.buttonAdd.addActionListener(al);
        this.buttonRem.addActionListener(al);

    }

    public List<Color> getColors()
    {
        return swatchPanel.getColors();
    }

    public String getLabel()
    {
        return label.getText();
    }

    public void setBgColor(Color bgColor)
    {
        swatchPanel.setBackground(bgColor);
    }

    public void addColor(Color col)
    {
        swatchPanel.addColor(col);
    }

    public void reset()
    {
        swatchPanel.clear();
    }

    public void refreshComponents()
    {
        swatchPanel.validate();
        swatchPanel.repaint();
    }

}
