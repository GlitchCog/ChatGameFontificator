package com.glitchcog.fontificator.gui.controls;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledInput;

/**
 * Pop-up dialog to enter manual messages into
 * 
 * @author Matt Yanos
 */
public class ManualMessageDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    private ControlWindow ctrlWindow;

    private JLabel manualInfo;

    private LabeledInput usernameInput;

    private LabeledInput textInput;

    private JCheckBox retainMessageBox;

    private JButton submitButton;

    private JButton clearButton;

    private static final KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

    public ManualMessageDialog(ControlWindow ctrlWindow)
    {
        super(ctrlWindow);
        ChatWindow.setupHideOnEscape(this);
        this.ctrlWindow = ctrlWindow;
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        build();
        setupSubmitOnEnter();
    }

    public void setupSubmitOnEnter()
    {
        Action aa = new AbstractAction()
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent event)
            {
                submit();
            }
        };
        final String mapKey = "enterPressed";
        JRootPane root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterStroke, mapKey);
        root.getActionMap().put(mapKey, aa);
    }

    private void reset()
    {
        usernameInput.setText("");
        textInput.setText("");
    }

    private void submit()
    {
        List<String> errors = validateInput();
        if (errors.isEmpty())
        {
            ctrlWindow.addManualMessage(usernameInput.getText(), textInput.getText());
            if (!retainMessageBox.isSelected())
            {
                reset();
            }
        }
        else
        {
            ChatWindow.popup.handleProblem(errors);
        }
    }

    private void build()
    {
        setTitle("Manually Post Message to Chat");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        manualInfo = new JLabel("This message will be posted to the visualization only; it will not be sent to the IRC channel.");

        usernameInput = new LabeledInput("Username", 8);
        textInput = new LabeledInput("Message", 32);

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
                else
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

        pack();
        setMinimumSize(getSize());
        setResizable(false);
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
