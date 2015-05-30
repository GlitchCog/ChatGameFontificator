package com.glitchcog.fontificator.gui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Labeled Slider is a JSlider with a label and a unit label
 * 
 * @author Matt Yanos
 */
public class LabeledSlider extends JPanel
{
    private static final long serialVersionUID = 1L;

    protected JSlider slider;

    private JLabel label;

    private String unitLabelStr;

    private JLabel unitLabel;

    private int maxValueDigits;

    public LabeledSlider(String labelStr, String unitLabelStr, int min, int max)
    {
        this(labelStr, unitLabelStr, min, max, Math.max(Integer.toString(min).length(), Integer.toString(max).length()));
    }

    public LabeledSlider(String labelStr, String unitLabelStr, int min, int max, int maxValueDigits)
    {
        this(labelStr, unitLabelStr, min, max, min, maxValueDigits);
    }

    public LabeledSlider(String labelStr, String unitLabelStr, int min, int max, int value, int maxValueDigits)
    {
        this.unitLabelStr = unitLabelStr;
        this.slider = new JSlider(JSlider.HORIZONTAL, min, max, value);
        this.label = new JLabel(labelStr);
        this.unitLabel = new JLabel(slider.getValue() + " " + unitLabelStr);
        this.maxValueDigits = maxValueDigits;
        setUnitLabel();
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                setUnitLabel();
            }
        });
        addComponents();
    }

    protected void addComponents()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        add(label, gbc);

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx++;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(slider, gbc);
        gbc.fill = GridBagConstraints.NONE;

        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx++;

        add(unitLabel, gbc);
    }

    public void setEnabled(boolean enabled)
    {
        slider.setEnabled(enabled);
    }

    public int getValue()
    {
        return slider.getValue();
    }

    public String getValueString()
    {
        return Integer.toString(getValue());
    }

    protected void setUnitLabel()
    {
        unitLabel.setText("<html><nobr><tt><b>" + padValue(getValueString()) + " " + unitLabelStr + "</b></tt></nobr></html>");
    }

    private String padValue(String valStr)
    {
        while (valStr.length() < maxValueDigits)
        {
            valStr = " " + valStr;
        }
        valStr = valStr.replaceAll(" ", "&nbsp;");
        return valStr;
    }

    public void addChangeListener(ChangeListener cl)
    {
        slider.addChangeListener(cl);
    }

    public void setValue(int value)
    {
        slider.setValue(value);
    }

    public JSlider getSlider()
    {
        return slider;
    }
}
