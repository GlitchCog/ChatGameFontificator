package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.config.ConfigCensor;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledSlider;
import com.glitchcog.fontificator.gui.component.ListInput;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.messages.MessageCheckList;

/**
 * Panel for configuring posted message censorship rules
 * 
 * @author Matt Yanos
 */
public class MessageCensorPanel extends ControlPanelBase
{
    private static final Logger logger = Logger.getLogger(MessageCensorPanel.class);

    private static final long serialVersionUID = 1L;

    private JCheckBox enableCensorshipBox;

    private JCheckBox purgeOnTwitchBanBox;

    private JCheckBox censorAllUrlsBox;

    private JCheckBox censorFirstPostUrlsBox;

    private JCheckBox censorUnknownCharsBox;

    private LabeledSlider unknownCharSlider;

    private ListInput userWhitelist;

    private ListInput userBlacklist;

    private ListInput bannedWordList;

    private MessageCheckList messageList;

    private ConfigCensor config;

    final String URL_REGEX = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    /**
     * Construct a chat control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param ctrlWindow
     * @param logBox
     */
    public MessageCensorPanel(FontificatorProperties fProps, ChatWindow chatWindow, ControlWindow ctrlWindow, LogBox logBox)
    {
        super("Chat Window", fProps, chatWindow, logBox);
    }

    @Override
    protected void build()
    {
        setBorder(new TitledBorder(ControlPanelBase.getBaseBorder(), "Message Censorship Configuration", TitledBorder.CENTER, TitledBorder.TOP));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = ControlPanelBase.getGbc();

        enableCensorshipBox = new JCheckBox("Enable message censoring");
        purgeOnTwitchBanBox = new JCheckBox("Purge messages on Twitch ban/timeout");
        censorAllUrlsBox = new JCheckBox("Censor all messages containing URLs");
        censorFirstPostUrlsBox = new JCheckBox("Censor messages containing URLs in a user's initial post");
        censorUnknownCharsBox = new JCheckBox("Censor messages containing a specified percentage of extended characters:");

        final String minLabel = "> 0";
        // @formatter:off
        unknownCharSlider = new LabeledSlider("", "%", ConfigCensor.MIN_UNKNOWN_CHAR_PCT, ConfigCensor.MAX_UNKNOWN_CHAR_PCT, 
        Math.max(Math.max(Integer.toString(ConfigCensor.MIN_UNKNOWN_CHAR_PCT).length(), Integer.toString(ConfigCensor.MAX_UNKNOWN_CHAR_PCT).length()), minLabel.length()))
        // @formatter:on
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String getValueString()
            {
                if (getValue() == slider.getMinimum())
                {
                    return minLabel;
                }
                else
                {
                    return super.getValueString();
                }
            }
        };

        ActionListener bl = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateConfig();
                chat.repaint();
                chat.initMessageRollout();
            }
        };

        userWhitelist = new ListInput("User Whitelist", "Exempt from all censorship", this, bl);
        userBlacklist = new ListInput("User Blacklist", "Every message censored", this, bl);
        bannedWordList = new ListInput("Banned Words", "Containing censors message", this, bl);

        enableCensorshipBox.addActionListener(bl);
        purgeOnTwitchBanBox.addActionListener(bl);
        censorAllUrlsBox.addActionListener(bl);
        censorFirstPostUrlsBox.addActionListener(bl);
        censorUnknownCharsBox.addActionListener(bl);

        unknownCharSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                config.setUnknownCharPercentage(unknownCharSlider.getValue());
            }
        });

        messageList = new MessageCheckList(chat, this);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 3;
        add(new JLabel("Censorship rules affect the visualization only; they don't influence the visibility of IRC posts."), gbc);
        gbc.gridy++;
        add(enableCensorshipBox, gbc);
        gbc.gridy++;
        add(purgeOnTwitchBanBox, gbc);
        gbc.gridy++;
        add(censorAllUrlsBox, gbc);
        gbc.gridy++;
        add(censorFirstPostUrlsBox, gbc);
        gbc.gridy++;
        JPanel unknownPanel = new JPanel(new GridBagLayout());
        GridBagConstraints uGbc = getGbc();
        uGbc.anchor = GridBagConstraints.WEST;
        uGbc.weightx = 1.0;
        uGbc.fill = GridBagConstraints.NONE;
        unknownPanel.add(censorUnknownCharsBox, uGbc);
        uGbc.gridy++;
        uGbc.anchor = GridBagConstraints.EAST;
        uGbc.weightx = 1.0;
        uGbc.weighty = 1.0;
        uGbc.fill = GridBagConstraints.BOTH;
        unknownPanel.add(unknownCharSlider, uGbc);
        add(unknownPanel, gbc);
        gbc.gridy++;

        gbc.weightx = 0.33;
        gbc.gridwidth = 1;
        add(userWhitelist, gbc);
        gbc.gridx++;
        add(userBlacklist, gbc);
        gbc.gridx++;
        add(bannedWordList, gbc);
        gbc.gridx++;
        gbc.gridy++;

        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(messageList, gbc);
        gbc.gridy++;
    }

    public void addMessage(Message msg)
    {
        messageList.addMessage(msg);
    }

    public void updateManualTable()
    {
        messageList.revalidateTable();
    }

    public void addBan(String bannedUser)
    {
        userBlacklist.addItem(bannedUser);
        recheckCensorship();
    }

    public void removeBan(String bannedUser)
    {
        userBlacklist.removeItem(bannedUser);
        recheckCensorship();
    }

    /**
     * Used to purge messages from chat whenever a user is timed-out or banned by a Twitch moderator
     * 
     * @param username
     * @param reason
     */
    public void purgeMessagesForUser(final String username, String reason)
    {
        if (!config.isCensorshipEnabled() || !config.isPurgeOnTwitchBan() || username == null)
        {
            return;
        }
        else if (reason == null)
        {
            reason = "TWITCH PURGE";
        }

        for (Message msg : chat.getMessages())
        {
            if (msg.getUsername() != null && username.toLowerCase().equals(msg.getUsername().toLowerCase()))
            {
                msg.setCensoredReason(reason);
                msg.setCensored(true, config.isCensorshipEnabled());
                msg.setPurged(true);
            }
        }
        refreshListAndMessages();
    }

    public void undoPurge()
    {
        for (Message msg : chat.getMessages())
        {
            msg.setPurged(false);
            msg.resetCensorship(false);
        }
        refreshListAndMessages();
    }

    public void recheckCensorship()
    {
        recheckCensorship(false);
    }

    public void recheckCensorship(boolean overrideManual)
    {
        for (Message msg : chat.getMessages())
        {
            msg.resetCensorship(overrideManual);
            checkCensor(msg);
        }
        refreshListAndMessages();
    }

    private void refreshListAndMessages()
    {
        messageList.revalidateTable();
        chat.initMessageRollout();
        chat.repaint();
    }

    public void checkCensor(Message msg)
    {
        // If this individual message has been manually censored or uncensored, just return
        if (msg.isManualCensorship())
        {
            return;
        }
        // Check for user on whitelist
        if (userWhitelist.contains(msg.getUsername()))
        {
            msg.setCensoredReason("USER WHITELIST");
            return;
        }
        // Check for user on blacklist
        if (userBlacklist.contains(msg.getUsername()))
        {
            msg.setCensoredReason("USER BLACKLIST");
            msg.setCensored(true, chat.isCensorshipEnabled());
            return;
        }
        // Check message contents for banned words
        String banned = containsBannedWord(msg.getContent());
        if (banned != null)
        {
            msg.setCensoredReason("BANNED WORD: \"" + banned + "\"");
            msg.setCensored(true, enableCensorshipBox.isSelected());
            return;
        }

        // Check for URL censorship rules, if a URL is present in the message
        final boolean containsUrl = URL_PATTERN.matcher(msg.getContent()).find();
        if (containsUrl)
        {
            // If all URLs are censored, then censor the message
            if (censorAllUrlsBox.isSelected())
            {
                msg.setCensored(true, enableCensorshipBox.isSelected());
                msg.setCensoredReason("URL");
                return;
            }
            // If only the first URLs are censored, then check the user post count to censor
            else if (msg.getUserPostCount() < 2 && censorFirstPostUrlsBox.isSelected())
            {
                msg.setCensored(true, enableCensorshipBox.isSelected());
                msg.setCensoredReason("1ST POST URL");
                return;
            }
        }

        final float percentUnknownChars = 100 * getPercentUnknownChars(msg.getContent());
        if (censorUnknownCharsBox.isSelected() && percentUnknownChars > 0.0f && percentUnknownChars >= unknownCharSlider.getValue())
        {
            msg.setCensored(true, enableCensorshipBox.isSelected());
            msg.setCensoredReason("UNKNOWN CHARACTERS");
            return;
        }
    }

    private String containsBannedWord(String text)
    {
        String[] words = text.split("[^\\w']+");
        for (String word : words)
        {
            if (!word.trim().isEmpty() && bannedWordList.contains(word))
            {
                return word;
            }
        }
        return null;
    }

    private float getPercentUnknownChars(String text)
    {
        if (text == null || text.isEmpty())
        {
            return 0.0f;
        }

        int count = 0;
        for (int c = 0; c < text.length(); c++)
        {
            if (text.charAt(c) < 32 || text.charAt(c) > 127)
            {
                count++;
            }
        }
        return ((float) count) / ((float) text.length());
    }

    private void toggleEnableds()
    {
        final boolean all = enableCensorshipBox.isSelected();
        purgeOnTwitchBanBox.setEnabled(all);
        censorAllUrlsBox.setEnabled(all);
        censorFirstPostUrlsBox.setEnabled(all && !censorAllUrlsBox.isSelected());
        censorUnknownCharsBox.setEnabled(all);
        unknownCharSlider.setEnabled(all && censorUnknownCharsBox.isSelected());
        userWhitelist.setEnabled(all);
        userBlacklist.setEnabled(all);
        bannedWordList.setEnabled(all);
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        this.config = fProps.getCensorConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        enableCensorshipBox.setSelected(config.isCensorshipEnabled());
        purgeOnTwitchBanBox.setSelected(config.isPurgeOnTwitchBan());
        censorAllUrlsBox.setSelected(config.isCensorAllUrls());
        censorFirstPostUrlsBox.setSelected(config.isCensorFirstUrls());
        censorUnknownCharsBox.setSelected(config.isCensorUnknownChars());
        unknownCharSlider.setValue(config.getUnknownCharPercentage());
        userWhitelist.setList(config.getUserWhitelist());
        userBlacklist.setList(config.getUserBlacklist());
        bannedWordList.setList(config.getBannedWords());
        toggleEnableds();
        userWhitelist.revalidate();
        userBlacklist.revalidate();
        bannedWordList.revalidate();
        repaint();
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        return new LoadConfigReport();
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setCensorshipEnabled(enableCensorshipBox.isSelected());
        config.setPurgeOnTwitchBan(purgeOnTwitchBanBox.isSelected());
        config.setCensorAllUrls(censorAllUrlsBox.isSelected());
        config.setCensorFirstUrls(censorFirstPostUrlsBox.isSelected());
        config.setCensorUnknownChars(censorUnknownCharsBox.isSelected());
        config.setUnknownCharPercentage(unknownCharSlider.getValue());
        config.setUserWhitelist(userWhitelist.getList());
        config.setUserBlacklist(userBlacklist.getList());
        config.setBannedWords(bannedWordList.getList());
        toggleEnableds();
    }

    /**
     * This work is also handled by the Config object's setters, but for censorship it is done here as well. This may be
     * redundant, but it was left in in case some of it is not.
     */
    public void updateConfig()
    {
        try
        {
            fillConfigFromInput();
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_ENABLED, Boolean.toString(config.isCensorshipEnabled()));
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_PURGE_ON_TWITCH_BAN, Boolean.toString(config.isPurgeOnTwitchBan()));
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_URL, Boolean.toString(config.isCensorAllUrls()));
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_FIRST_URL, Boolean.toString(config.isCensorFirstUrls()));
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_UNKNOWN_CHARS, Boolean.toString(config.isCensorUnknownChars()));
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_UNKNOWN_CHARS_PERCENT, Integer.toString(config.getUnknownCharPercentage()));
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_WHITE, config.getUserWhiteListString());
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_BLACK, config.getUserBlackListString());
            fProps.setProperty(FontificatorProperties.KEY_CENSOR_BANNED, config.getBannedWordsString());
        }
        catch (Exception e)
        {
            logger.error("Unable to update config from censorship UI input", e);
        }
    }

}
