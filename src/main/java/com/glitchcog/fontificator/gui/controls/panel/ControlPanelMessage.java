package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.glitchcog.fontificator.bot.ChatViewerBot;
import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.UsernameCaseResolutionType;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledInput;
import com.glitchcog.fontificator.gui.component.LabeledSlider;

/**
 * Contains all the options for how to format the messages that are displayed in the chat panel- things like whether and
 * how the time stamp is displayed on the line
 * 
 * @author Matt Yanos
 */
public class ControlPanelMessage extends ControlPanelBase
{
    private static final long serialVersionUID = 1L;

    private JCheckBox usernamesBox;

    private JCheckBox timestampsBox;

    private LabeledInput timeFormatInput;

    private JButton timeFormatUpdateButton;

    private JCheckBox joinMessagesBox;

    private LabeledSlider messageSpeedSlider;

    private LabeledSlider queueSizeSlider;

    private ConfigMessage config;

    private JButton clearChatButton;

    private JComboBox<UsernameCaseResolutionType> caseTypeDropdown;

    private JCheckBox specifyCaseBox;

    public ControlPanelMessage(FontificatorProperties fProps, ChatWindow chatWindow, ChatViewerBot bot)
    {
        super("Message", fProps, chatWindow);
        bot.setMessageConfig(config);
    }

    @Override
    protected void build()
    {
        usernamesBox = new JCheckBox("Show Usernames");
        joinMessagesBox = new JCheckBox("Show Joins");
        timestampsBox = new JCheckBox("Show Timestamps");
        timeFormatInput = new LabeledInput(null, 9);
        timeFormatUpdateButton = new JButton("Update Time Format");
        queueSizeSlider = new LabeledSlider("Message Queue Size", "messages", ConfigMessage.MIN_QUEUE_SIZE, ConfigMessage.MAX_QUEUE_SIZE);

        final String maxLabel = "MAX";
        messageSpeedSlider = new LabeledSlider("Message Speed", "char/sec", ConfigMessage.MIN_MESSAGE_SPEED, ConfigMessage.MAX_MESSAGE_SPEED, maxLabel.length())
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String getValueString()
            {
                if (getValue() == slider.getMaximum())
                {
                    return maxLabel;
                }
                else
                {
                    return super.getValueString();
                }
            }
        };
        caseTypeDropdown = new JComboBox<UsernameCaseResolutionType>(UsernameCaseResolutionType.values());
        specifyCaseBox = new JCheckBox("Permit users to specify their own username case in posts");

        DocumentListener docListener = new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                toggleEnableds();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                toggleEnableds();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                toggleEnableds();
            }
        };

        timeFormatInput.addDocumentListener(docListener);

        caseTypeDropdown.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                UsernameCaseResolutionType type = (UsernameCaseResolutionType) caseTypeDropdown.getSelectedItem();
                boolean changed = config.getCaseResolutionType() != type;
                config.setCaseResolutionType(type);
                if (changed)
                {
                    chatWindow.clearUsernameCases();
                }
            }
        });

        ActionListener boxListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JCheckBox source = (JCheckBox) e.getSource();
                if (usernamesBox.equals(source))
                {
                    config.setShowUsernames(source.isSelected());
                }
                else if (joinMessagesBox.equals(source))
                {
                    config.setJoinMessages(source.isSelected());
                }
                else if (timestampsBox.equals(source))
                {
                    config.setShowTimestamps(source.isSelected());
                    toggleEnableds();
                }
                else if (specifyCaseBox.equals(source))
                {
                    config.setSpecifyCaseAllowed(specifyCaseBox.isSelected());
                    chatWindow.clearUsernameCases();
                }
                chat.repaint();
            }
        };

        usernamesBox.addActionListener(boxListener);
        joinMessagesBox.addActionListener(boxListener);
        timestampsBox.addActionListener(boxListener);
        specifyCaseBox.addActionListener(boxListener);

        timeFormatUpdateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                List<String> errors = new ArrayList<String>();
                config.validateTimeFormat(errors, timeFormatInput.getText());
                if (errors.isEmpty())
                {
                    config.setTimeFormat(timeFormatInput.getText());
                    toggleEnableds();
                    chat.repaint();
                }
                else
                {
                    ChatWindow.popup.handleProblem(errors);
                }
            }
        });

        ChangeListener cl = new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting())
                {
                    if (queueSizeSlider.getSlider().equals(source))
                    {
                        config.setQueueSize(queueSizeSlider.getValue());
                    }
                    else if (messageSpeedSlider.getSlider().equals(source))
                    {
                        config.setMessageSpeed(messageSpeedSlider.getValue(), chat.getMessageProgressor());
                    }
                }
            }
        };

        messageSpeedSlider.addChangeListener(cl);
        queueSizeSlider.addChangeListener(cl);

        clearChatButton = new JButton("Clear Chat");
        clearChatButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                chat.clearChat();
                chat.repaint();
            }
        });

        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;

        JPanel topOptions = new JPanel(new GridLayout(2, 1));
        topOptions.setBorder(new TitledBorder(baseBorder, "Message Format Options", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel optionsA = new JPanel(new GridBagLayout());
        JPanel optionsB = new JPanel(new GridBagLayout());

        GridBagConstraints tfGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);
        JPanel timeFormatPanel = new JPanel(new GridBagLayout());
        timeFormatPanel.add(timeFormatInput, tfGbc);
        tfGbc.gridx++;
        timeFormatPanel.add(timeFormatUpdateButton, tfGbc);

        GridBagConstraints aGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);
        optionsA.add(usernamesBox, aGbc);
        aGbc.gridx++;
        aGbc.anchor = GridBagConstraints.NORTH;
        optionsA.add(timestampsBox, aGbc);
        aGbc.gridy++;
        aGbc.gridx = 0;
        aGbc.anchor = GridBagConstraints.WEST;
        optionsA.add(joinMessagesBox, aGbc);
        aGbc.gridx++;
        aGbc.anchor = GridBagConstraints.EAST;
        optionsA.add(timeFormatPanel, aGbc);

        GridBagConstraints bGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);
        optionsB.add(messageSpeedSlider, bGbc);
        bGbc.gridy++;
        optionsB.add(queueSizeSlider, bGbc);

        topOptions.add(optionsA);
        topOptions.add(optionsB);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(topOptions, gbc);
        gbc.gridy++;

        JPanel botOptions = new JPanel(new GridLayout(3, 1));
        botOptions.setBorder(new TitledBorder(baseBorder, "Username Options", TitledBorder.CENTER, TitledBorder.TOP));

        botOptions.add(new JLabel("Default Method for Handling Username Casing"));
        botOptions.add(caseTypeDropdown);
        botOptions.add(specifyCaseBox);

        add(botOptions, gbc);

        // Filler panel
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc);
        gbc.gridy++;
    }

    private void toggleEnableds()
    {
        timeFormatInput.setEnabled(config.showTimestamps());
        boolean tfModified = false;
        try
        {
            DateFormat df = new SimpleDateFormat(timeFormatInput.getText());
            df.format(new Date());
            tfModified = !timeFormatInput.getText().equals(config.getTimeFormat());
        }
        catch (Exception e)
        {
            tfModified = false;
        }
        timeFormatUpdateButton.setEnabled(config.showTimestamps() && tfModified);
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        this.config = fProps.getMessageConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        usernamesBox.setSelected(config.showUsernames());
        timestampsBox.setSelected(config.showTimestamps());
        timeFormatInput.setText(config.getTimeFormat());
        // Set the timeformatter here by sending it back
        config.setTimeFormat(timeFormatInput.getText());
        toggleEnableds();
        joinMessagesBox.setSelected(config.showJoinMessages());
        queueSizeSlider.setValue(config.getQueueSize());
        messageSpeedSlider.setValue(config.getMessageSpeed());
        caseTypeDropdown.setSelectedItem(config.getCaseResolutionType());
        specifyCaseBox.setSelected(config.isSpecifyCaseAllowed());
    }

    @Override
    protected List<String> validateInput()
    {
        List<String> errors = new ArrayList<String>();
        config.validateStrings(errors, timeFormatInput.getText(), Integer.toString(queueSizeSlider.getValue()), Integer.toString(messageSpeedSlider.getValue()));
        return errors;
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setShowUsernames(usernamesBox.isSelected());
        config.setJoinMessages(joinMessagesBox.isSelected());
        config.setShowTimestamps(timestampsBox.isSelected());
        config.setTimeFormat(timeFormatInput.getText());
        toggleEnableds();
        config.setQueueSize(queueSizeSlider.getValue());
        config.setMessageSpeed(messageSpeedSlider.getValue(), chat.getMessageProgressor());
        config.setCaseResolutionType((UsernameCaseResolutionType) caseTypeDropdown.getSelectedItem());
        config.setSpecifyCaseAllowed(specifyCaseBox.isSelected());
    }

}
