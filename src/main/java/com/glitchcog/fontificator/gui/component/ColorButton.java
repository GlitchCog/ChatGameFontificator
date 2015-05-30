package com.glitchcog.fontificator.gui.component;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;

/**
 * Color Button is a GUI component containing a button that lets a user select a color from a JColorChooser pop-up, and
 * then changes the button color to display the selection
 * 
 * @author Matt Yanos
 */
public class ColorButton extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * The text label for identifying the color button component
     */
    private JLabel label;

    /**
     * The button that displays the color
     */
    private JButton button;

    /**
     * A reference to the control panel this button sits on, so its update method to use the newly selected colors can
     * be called
     */
    private final ControlPanelBase control;

    public ColorButton(final String label, Color value, final String explanation, ControlPanelBase controlPanel)
    {
        super();
        this.control = controlPanel;
        this.button = new JButton();
        this.button.setToolTipText("");
        this.label = new JLabel(label);
        setColor(value == null ? Color.BLACK : value);
        button.setBackground(value);

        // So the button color shows up on Mac OS
        button.setOpaque(true);

        add(this.label);
        add(this.button);

        this.button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Color color = JColorChooser.showDialog(ControlWindow.me, "Select Color" + (label == null ? "" : " for " + label), getColor());
                if (color != null)
                {
                    setColor(color);
                    control.update();
                }
            }
        });
    }

    /**
     * Get the selected color
     * 
     * @return color
     */
    public Color getColor()
    {
        return button.getBackground();
    }

    /**
     * Set the selected color
     * 
     * @param value
     */
    public void setColor(Color value)
    {
        button.setBackground(value);
    }

    /**
     * Get the label text
     * 
     * @return labelText
     */
    public String getLabel()
    {
        return label.getText();
    }

}
