package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.glitchcog.fontificator.config.MessageCasing;
import com.glitchcog.fontificator.config.UsernameCaseResolutionType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
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

    /**
     * Checkbox to indicate whether to display the username with the message
     */
    private JCheckBox usernamesBox;

    /**
     * Checkbox to indicate whether to display the timestamp with the message
     */
    private JCheckBox timestampsBox;

    /**
     * Input for specifying the format pattern of the username
     */
    private LabeledInput usernameFormatInput;

    /**
     * Input for specifying how the username/timestamp is separated from the message content
     */
    private LabeledInput contentBreakerInput;

    /**
     * Input for specifying the format pattern of the timestamp
     */
    private LabeledInput timeFormatInput;

    /**
     * Button to apply any changes to the username format pattern
     */
    private JButton usernameFormatUpdateButton;

    /**
     * Button to reset the username format pattern, in case the user changes it and forgets the text of the %user%
     * variable
     */
    private JButton formatRestButton;

    /**
     * Button to apply any changes to the content breaker pattern
     */
    private JButton contentBreakerUpdateButton;

    /**
     * Button to apply any changes to the time format pattern
     */
    private JButton timeFormatUpdateButton;

    /**
     * Checkbox to indicate whether join messages should be both collected and displayed. If this is unchecked, join
     * messages will not be stored in the message cache, so only join messages that are received while this is checked
     * will be toggled. Join messages that come in while this is unchecked can never be displayed.
     */
    private JCheckBox joinMessagesBox;

    /**
     * Slider to specify the size of the message queue
     */
    private LabeledSlider queueSizeSlider;

    /**
     * Slider to indicate how fast the messages should be rolled out onto the chat display
     */
    private LabeledSlider messageSpeedSlider;

    /**
     * Slider to indicate whether and how long messages take to expire
     */
    private LabeledSlider expirationTimeSlider;

    /**
     * Checkbox to indicate whether no border should be drawn when there are no visible messages to display in it
     */
    private JCheckBox hideEmptyBorder;

    /**
     * Checkbox to indicate whether no background should be drawn when there are no visible messages to display on it
     */
    private JCheckBox hideEmptyBackground;

    /**
     * The message config object that bridges the UI to the properties file
     */
    private ConfigMessage config;

    /**
     * Dropdown menu to specify the choices for username capitalization
     */
    private JComboBox<UsernameCaseResolutionType> caseTypeDropdown;

    /**
     * Checkbox to indicate whether users will be able to specify their own username casing if they type their own
     * username into the chat
     */
    private JCheckBox specifyCaseBox;

    /**
     * Dropdown menu to specify the choices for enforcing upper or lower case on the entire message
     */
    private JComboBox<MessageCasing> messageCasingDropdown;

    /**
     * Construct a message control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param bot
     * @param logBox
     */
    public ControlPanelMessage(FontificatorProperties fProps, ChatWindow chatWindow, ChatViewerBot bot, LogBox logBox)
    {
        super("Message", fProps, chatWindow, logBox);
        bot.setMessageConfig(config);
    }

    @Override
    protected void build()
    {
        usernamesBox = new JCheckBox("Show Usernames");
        joinMessagesBox = new JCheckBox("Show Joins");
        timestampsBox = new JCheckBox("Show Timestamps");
        usernameFormatInput = new LabeledInput(null, 9);
        contentBreakerInput = new LabeledInput(null, 9);
        timeFormatInput = new LabeledInput(null, 9);
        usernameFormatUpdateButton = new JButton("Update Username Format");
        formatRestButton = new JButton("Reset");
        contentBreakerUpdateButton = new JButton("Update Content Breaker Format");
        timeFormatUpdateButton = new JButton("Update Time Format");
        queueSizeSlider = new LabeledSlider("Message Queue Size", "messages", ConfigMessage.MIN_QUEUE_SIZE, ConfigMessage.MAX_QUEUE_SIZE);

        formatRestButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                usernameFormatInput.setText(ConfigMessage.USERNAME_REPLACE);
                contentBreakerInput.setText(ConfigMessage.DEFAULT_CONTENT_BREAKER);
                toggleEnableds();
            }
        });

        final String maxSpeedLabel = "MAX";
        messageSpeedSlider = new LabeledSlider("Message Speed", "char/sec", ConfigMessage.MIN_MESSAGE_SPEED, ConfigMessage.MAX_MESSAGE_SPEED, maxSpeedLabel.length())
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String getValueString()
            {
                if (getValue() == slider.getMaximum())
                {
                    return maxSpeedLabel;
                }
                else
                {
                    return super.getValueString();
                }
            }
        };

        final String minExpirationLabel = "NEVER";
        expirationTimeSlider = new LabeledSlider("Hide Messages After ", "sec", ConfigMessage.MIN_MESSAGE_EXPIRATION, ConfigMessage.MAX_MESSAGE_EXPIRATION, minExpirationLabel.length())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected String getUnitLabelStr()
            {
                if (getValue() == slider.getMinimum())
                {
                    return padValue("", super.getUnitLabelStr().length());
                }
                else
                {
                    return super.getUnitLabelStr();
                }
            }

            @Override
            public String getValueString()
            {
                if (getValue() == slider.getMinimum())
                {
                    return minExpirationLabel;
                }
                else
                {
                    return super.getValueString();
                }
            }
        };

        JLabel hideLabel = new JLabel("When No Messages Are Displayed: ");
        hideEmptyBorder = new JCheckBox("Hide Border");
        hideEmptyBackground = new JCheckBox("Hide Background");

        caseTypeDropdown = new JComboBox<UsernameCaseResolutionType>(UsernameCaseResolutionType.values());
        specifyCaseBox = new JCheckBox("Permit users to specify their own username case in posts");
        messageCasingDropdown = new JComboBox<MessageCasing>(MessageCasing.values());

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

        usernameFormatInput.addDocumentListener(docListener);
        timeFormatInput.addDocumentListener(docListener);
        contentBreakerInput.addDocumentListener(docListener);

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

        messageCasingDropdown.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                config.setMessageCasing((MessageCasing) messageCasingDropdown.getSelectedItem());
                chat.repaint();
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
                    toggleEnableds();
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
                else if (hideEmptyBorder.equals(source))
                {
                    config.setHideEmptyBorder(hideEmptyBorder.isSelected());
                }
                else if (hideEmptyBackground.equals(source))
                {
                    config.setHideEmptyBackground(hideEmptyBackground.isSelected());
                }
                chat.repaint();
            }
        };

        usernamesBox.addActionListener(boxListener);
        joinMessagesBox.addActionListener(boxListener);
        timestampsBox.addActionListener(boxListener);
        specifyCaseBox.addActionListener(boxListener);
        hideEmptyBorder.addActionListener(boxListener);
        hideEmptyBackground.addActionListener(boxListener);

        ActionListener updateButtonListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (e.getSource() == timeFormatUpdateButton)
                {
                    LoadConfigReport report = new LoadConfigReport();
                    config.validateTimeFormat(report, timeFormatInput.getText());
                    if (report.isErrorFree())
                    {
                        config.setTimeFormat(timeFormatInput.getText());
                        toggleEnableds();
                        chat.repaint();
                    }
                    else
                    {
                        ChatWindow.popup.handleProblem(report);
                    }
                }
                else if (e.getSource() == usernameFormatUpdateButton)
                {
                    config.setUsernameFormat(usernameFormatInput.getText());
                    toggleEnableds();
                    chat.repaint();
                }
                else if (e.getSource() == contentBreakerUpdateButton)
                {
                    config.setContentBreaker(contentBreakerInput.getText());
                    toggleEnableds();
                    chat.repaint();
                }
            }
        };

        usernameFormatUpdateButton.addActionListener(updateButtonListener);
        timeFormatUpdateButton.addActionListener(updateButtonListener);
        contentBreakerUpdateButton.addActionListener(updateButtonListener);

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
                    else if (expirationTimeSlider.getSlider().equals(source))
                    {
                        config.setExpirationTime(expirationTimeSlider.getValue(), chat.getMessageExpirer());
                        chat.repaint();
                    }
                }
            }
        };

        messageSpeedSlider.addChangeListener(cl);
        expirationTimeSlider.addChangeListener(cl);
        queueSizeSlider.addChangeListener(cl);

        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;

        JPanel topOptions = new JPanel(new GridBagLayout());
        topOptions.setBorder(new TitledBorder(baseBorder, "Message Format Options", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel optionsA = new JPanel(new GridBagLayout());
        JPanel optionsB = new JPanel(new GridBagLayout());

        GridBagConstraints tfGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);
        JPanel formatPanel = new JPanel(new GridBagLayout());

        formatPanel.add(usernameFormatInput, tfGbc);
        tfGbc.gridx++;
        formatPanel.add(usernameFormatUpdateButton, tfGbc);
        tfGbc.gridx++;
        formatPanel.add(formatRestButton, tfGbc);
        tfGbc.gridx = 0;
        tfGbc.gridwidth = 2;
        tfGbc.gridy++;
        tfGbc.gridx = 0;
        formatPanel.add(contentBreakerInput, tfGbc);
        tfGbc.gridx++;
        formatPanel.add(contentBreakerUpdateButton, tfGbc);
        tfGbc.gridy++;
        tfGbc.gridx = 0;
        formatPanel.add(timeFormatInput, tfGbc);
        tfGbc.gridx++;
        formatPanel.add(timeFormatUpdateButton, tfGbc);

        GridBagConstraints aGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);
        optionsA.add(usernamesBox, aGbc);
        aGbc.gridy++;
        aGbc.anchor = GridBagConstraints.WEST;
        optionsA.add(timestampsBox, aGbc);
        aGbc.gridy++;
        optionsA.add(joinMessagesBox, aGbc);
        aGbc.gridy = 0;

        aGbc.gridx++;
        aGbc.gridheight = 3;
        optionsA.add(formatPanel, aGbc);

        GridBagConstraints bGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);
        bGbc.gridwidth = 3;
        bGbc.fill = GridBagConstraints.HORIZONTAL;
        optionsB.add(queueSizeSlider, bGbc);
        bGbc.gridy++;
        optionsB.add(messageSpeedSlider, bGbc);
        bGbc.gridy++;
        optionsB.add(expirationTimeSlider, bGbc);
        bGbc.gridy++;
        bGbc.fill = GridBagConstraints.NONE;
        bGbc.weightx = 0.333;
        bGbc.gridwidth = 1;
        bGbc.anchor = GridBagConstraints.EAST;
        optionsB.add(hideLabel, bGbc);
        bGbc.gridx++;
        bGbc.anchor = GridBagConstraints.CENTER;
        optionsB.add(hideEmptyBorder, bGbc);
        bGbc.gridx++;
        bGbc.anchor = GridBagConstraints.WEST;
        optionsB.add(hideEmptyBackground, bGbc);
        bGbc.gridx = 0;
        bGbc.gridwidth = 3;
        bGbc.gridy++;
        bGbc.anchor = GridBagConstraints.CENTER;
        bGbc.weightx = 1.0;

        GridBagConstraints topOpGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0);
        topOpGbc.anchor = GridBagConstraints.SOUTH;
        topOptions.add(optionsA, topOpGbc);
        topOpGbc.gridy++;
        topOpGbc.anchor = GridBagConstraints.NORTH;
        topOptions.add(optionsB, topOpGbc);
        topOpGbc.gridy++;

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(topOptions, gbc);
        gbc.gridy++;

        JPanel usernameOptions = new JPanel(new GridLayout(3, 1));
        usernameOptions.setBorder(new TitledBorder(baseBorder, "Username Options", TitledBorder.CENTER, TitledBorder.TOP));

        usernameOptions.add(new JLabel("Default Method for Handling Username Casing"));
        usernameOptions.add(caseTypeDropdown);
        usernameOptions.add(specifyCaseBox);

        JPanel casingOptions = new JPanel();
        casingOptions.setBorder(new TitledBorder(baseBorder, "Message Casing Options", TitledBorder.CENTER, TitledBorder.TOP));
        casingOptions.add(new JLabel("Force uppercase or lowercase: "));
        casingOptions.add(messageCasingDropdown);

        add(usernameOptions, gbc);
        gbc.gridy++;

        add(casingOptions, gbc);
        gbc.gridy++;

        // Filler panel
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc);
        gbc.gridy++;
    }

    /**
     * Toggle UI enabled states based on user input
     */
    private void toggleEnableds()
    {
        usernameFormatInput.setEnabled(config.showUsernames());
        timeFormatInput.setEnabled(config.showTimestamps());
        // Username Formatter
        boolean ufModified = false;
        // Timestamp Formatter
        boolean tfModified = false;
        // Content Breaker
        boolean cbModified = false;
        try
        {
            tfModified = !timeFormatInput.getText().equals(config.getTimeFormat());
            ufModified = !usernameFormatInput.getText().equals(config.getUsernameFormat());
            cbModified = !contentBreakerInput.getText().equals(config.getContentBreaker());
            DateFormat df = new SimpleDateFormat(timeFormatInput.getText());
            df.format(new Date());
        }
        catch (Exception e)
        {
            tfModified = false;
        }
        usernameFormatUpdateButton.setEnabled(ufModified);
        timeFormatUpdateButton.setEnabled(config.showTimestamps() && tfModified);
        contentBreakerUpdateButton.setEnabled(cbModified);
        formatRestButton.setEnabled(!config.getUsernameFormat().equals(ConfigMessage.USERNAME_REPLACE) || !config.getContentBreaker().equals(ConfigMessage.DEFAULT_CONTENT_BREAKER));
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
        usernameFormatInput.setText(config.getUsernameFormat());
        timeFormatInput.setText(config.getTimeFormat());
        contentBreakerInput.setText(config.getContentBreaker());
        // Set the formatters here by sending them back
        config.setUsernameFormat(usernameFormatInput.getText());
        config.setTimeFormat(timeFormatInput.getText());
        config.setContentBreaker(contentBreakerInput.getText());
        toggleEnableds();
        joinMessagesBox.setSelected(config.showJoinMessages());
        queueSizeSlider.setValue(config.getQueueSize());
        messageSpeedSlider.setValue(config.getMessageSpeed());
        expirationTimeSlider.setValue(config.getExpirationTime());
        hideEmptyBorder.setSelected(config.isHideEmptyBorder());
        hideEmptyBackground.setSelected(config.isHideEmptyBackground());
        caseTypeDropdown.setSelectedItem(config.getCaseResolutionType());
        specifyCaseBox.setSelected(config.isSpecifyCaseAllowed());
        messageCasingDropdown.setSelectedItem(config.getMessageCasing());
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        LoadConfigReport report = new LoadConfigReport();
        config.validateStrings(report, timeFormatInput.getText(), Integer.toString(queueSizeSlider.getValue()), Integer.toString(messageSpeedSlider.getValue()), Integer.toString(expirationTimeSlider.getValue()));
        return report;
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setShowUsernames(usernamesBox.isSelected());
        config.setJoinMessages(joinMessagesBox.isSelected());
        config.setShowTimestamps(timestampsBox.isSelected());
        config.setUsernameFormat(usernameFormatInput.getText());
        config.setTimeFormat(timeFormatInput.getText());
        config.setContentBreaker(contentBreakerInput.getText());
        toggleEnableds();
        config.setQueueSize(queueSizeSlider.getValue());
        config.setMessageSpeed(messageSpeedSlider.getValue(), chat.getMessageProgressor());
        config.setExpirationTime(expirationTimeSlider.getValue(), chat.getMessageExpirer());
        config.setHideEmptyBorder(hideEmptyBorder.isSelected());
        config.setHideEmptyBackground(hideEmptyBackground.isSelected());
        config.setCaseResolutionType((UsernameCaseResolutionType) caseTypeDropdown.getSelectedItem());
        config.setSpecifyCaseAllowed(specifyCaseBox.isSelected());
        config.setMessageCasing((MessageCasing) messageCasingDropdown.getSelectedItem());
    }

}
