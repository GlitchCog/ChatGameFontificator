package com.glitchcog.fontificator.gui.component;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.gui.chat.ChatPanel;

/**
 * Character Picker is a GUI pop-up component for picking a character
 * 
 * @author Matt Yanos
 */
public class CharacterPicker extends JDialog
{
    private static final long serialVersionUID = 1L;

    private JToggleButton[] charButtons;

    private ButtonGroup buttonGroup;

    /**
     * Config to update when new value is selected
     */
    private ConfigFont config;

    /**
     * Label to update when new value is selected
     */
    private JLabel selectionLabel;

    private String baseLabelText;

    /**
     * Chat to call repaint when a new value is selected
     */
    private ChatPanel chat;

    public CharacterPicker(JDialog parent, ConfigFont config, JLabel selectionLabel, ChatPanel chat)
    {
        super(parent, true);
        this.selectionLabel = selectionLabel;
        this.baseLabelText = selectionLabel.getText();
        this.config = config;
        this.chat = chat;
        build();
    }

    private void build()
    {
        setLayout(new GridLayout(12, 8));
        setTitle("Character Picker");

        ActionListener al = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JToggleButton tb = (JToggleButton) e.getSource();
                setSelectedChar(tb.getText().charAt(0));
                setVisible(false);
            }
        };

        buttonGroup = new ButtonGroup()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void setSelected(ButtonModel model, boolean selected)
            {
                if (selected)
                {
                    super.setSelected(model, selected);
                }
                else
                {
                    clearSelection();
                }
            }
        };

        charButtons = new JToggleButton[96];
        for (int i = 0; i < charButtons.length; i++)
        {
            charButtons[i] = new JToggleButton(Character.toString((char) (i + 32)));
            charButtons[i].addActionListener(al);
            buttonGroup.add(charButtons[i]);
            add(charButtons[i]);
        }

        pack();
        setResizable(false);
    }

    public void setSelectedChar(char selectedChar)
    {
        config.setUnknownChar(selectedChar);
        charButtons[selectedChar - 32].setSelected(true);
        String label = "'" + Character.toString(selectedChar) + "' (ASCII " + (int)selectedChar + ")";
        selectionLabel.setText(baseLabelText + label);
        chat.repaint();
    }

    public char getSelectedChar()
    {
        return config.getUnknownChar();
    }
}
