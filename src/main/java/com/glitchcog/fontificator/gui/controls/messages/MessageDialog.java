package com.glitchcog.fontificator.gui.controls.messages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JDialog;

import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;
import com.glitchcog.fontificator.gui.controls.panel.MessageCensorPanel;

/**
 * Pop-up dialog to manage messages
 * 
 * @author Matt Yanos
 */
public class MessageDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private MessagePostPanel postPanel;

    private MessageCensorPanel censorPanel;

    public MessageDialog(FontificatorProperties fProps, ChatWindow chatWindow, ControlWindow ctrlWindow, LogBox logBox)
    {
        super(chatWindow);
        ChatWindow.setupHideOnEscape(this);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        build(fProps, chatWindow, ctrlWindow, logBox);
    }

    private void build(FontificatorProperties fProps, ChatWindow chatWindow, ControlWindow ctrlWindow, LogBox logBox)
    {
        setTitle("Message Management");

        this.postPanel = new MessagePostPanel(ctrlWindow);
        this.censorPanel = new MessageCensorPanel(fProps, chatWindow, ctrlWindow, logBox);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = ControlPanelBase.getGbc();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        add(censorPanel, gbc);
        gbc.gridy++;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(postPanel, gbc);
        gbc.gridy++;

        setSize(700, 500);
        setMinimumSize(getSize());
        setSize(700, 700);
    }

    public void showDialog()
    {
        setVisible(true);
    }

    public MessageCensorPanel getCensorPanel()
    {
        return censorPanel;
    }

}
