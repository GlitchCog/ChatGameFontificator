package com.glitchcog.fontificator.gui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

/**
 * Labeled Input is a text input field with a label
 * 
 * @author Matt Yanos
 */
public class LabeledInput extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JLabel label;

    private JTextField input;

    private boolean isPassword;

    private final static int DEFAULT_FIELD_SIZE = 6;

    public LabeledInput(String label)
    {
        this(label, DEFAULT_FIELD_SIZE);
    }

    public LabeledInput(String label, int size)
    {
        this(label, false, size);
    }

    public LabeledInput(String label, boolean isPassword, int size)
    {
        this(label, isPassword, isPassword ? new JPasswordField(size) : new JTextField(size));
    }

    private LabeledInput(String label, boolean isPassword, JTextField input)
    {
        this.isPassword = isPassword;
        this.label = new JLabel(label);
        this.input = input;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 4, 2, 4), 0, 0);
        add(this.label, gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        add(this.input, gbc);
    }

    public void setEnabled(boolean enabled)
    {
        input.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    public String getLabel()
    {
        return label.getText();
    }

    public String getText()
    {
        if (isPassword)
        {
            return String.valueOf(((JPasswordField) input).getPassword());
        }
        else
        {
            return input.getText();
        }
    }

    public void setText(String text)
    {
        input.setText(text);
    }

    public void addActionListener(ActionListener al)
    {
        input.addActionListener(al);
    }

    public void addDocumentListener(DocumentListener dl)
    {
        input.getDocument().addDocumentListener(dl);
    }

    public void addFocusListener(FocusListener fl)
    {
        input.addFocusListener(fl);
    }

}
