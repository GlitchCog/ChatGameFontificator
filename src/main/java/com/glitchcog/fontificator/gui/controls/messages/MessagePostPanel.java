package com.glitchcog.fontificator.gui.controls.messages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledInput;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;

/**
 * Panel for manually posting messages to the chat window
 * 
 * @author Matt Yanos
 */
public class MessagePostPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JLabel manualInfo;

    private LabeledInput usernameInput;

    private LabeledInput textInput;

    private JCheckBox retainMessageBox;

    private JButton submitButton;

    private JButton clearButton;

    private ControlWindow ctrlWindow;

    public MessagePostPanel(ControlWindow ctrlWindow)
    {
        this.ctrlWindow = ctrlWindow;
        build();
    }

    private void build()
    {
        setBorder(new TitledBorder(ControlPanelBase.getBaseBorder(), "Manually Post Message to Chat", TitledBorder.CENTER, TitledBorder.TOP));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        manualInfo = new JLabel("This message will be posted to the visualization only; it will not be sent to the IRC channel.");

        usernameInput = new LabeledInput("Username", 8);
        textInput = new LabeledInput("Message", 32);

        textInput.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                submit();
            }
        });

        retainMessageBox = new JCheckBox("Retain message input after post");

        submitButton = new JButton("Post");
        clearButton = new JButton("Clear");

        ActionListener al = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();
                if (submitButton.equals(source))
                {
                    submit();
                }
                else if (clearButton.equals(source))
                {
                    reset();
                }
            }
        };

        submitButton.addActionListener(al);
        clearButton.addActionListener(al);

        add(manualInfo, gbc);
        gbc.gridy++;

        gbc.gridwidth = 1;

        add(usernameInput, gbc);
        gbc.gridx++;
        add(textInput, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridwidth = 0;
        gbc.anchor = GridBagConstraints.WEST;
        buttonPanel.add(retainMessageBox, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        buttonPanel.add(submitButton, gbc);
        gbc.gridx++;
        buttonPanel.add(clearButton, gbc);
    }

    private void reset()
    {
        usernameInput.setText("");
        textInput.setText("");
    }

    public void submit()
    {
        List<String> report = validateInput();
        if (report.isEmpty())
        {
            ctrlWindow.addManualMessage(usernameInput.getText(), textInput.getText());
            if (!retainMessageBox.isSelected())
            {
                reset();
            }
        }
        else
        {
            ChatWindow.popup.handleProblem(report);
        }
    }

    private List<String> validateInput()
    {
        List<String> errors = new ArrayList<String>();

        if (usernameInput.getText().isEmpty())
        {
            errors.add("Please enter a username under which to post");
        }

        if (textInput.getText().isEmpty())
        {
            errors.add("Please enter a message to post");
        }

        return errors;
    }

}
