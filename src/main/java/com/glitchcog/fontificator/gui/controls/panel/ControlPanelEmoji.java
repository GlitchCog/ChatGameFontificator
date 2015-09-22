package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.EmojiLoadingDisplayStragegy;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.emoji.EmojiOperation;
import com.glitchcog.fontificator.emoji.EmojiType;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledInput;
import com.glitchcog.fontificator.gui.component.LabeledSlider;
import com.glitchcog.fontificator.gui.emoji.EmojiLoadProgressDialog;
import com.glitchcog.fontificator.gui.emoji.EmojiWorker;
import com.glitchcog.fontificator.gui.emoji.EmojiWorkerReport;

/**
 * Sub-control panel to configure emoji in the chat view
 * 
 * @author Matt Yanos
 */
public class ControlPanelEmoji extends ControlPanelBase
{
    private static final Logger logger = Logger.getLogger(ControlPanelEmoji.class);

    private static final long serialVersionUID = 1L;

    /**
     * This is the version of the Twitch emote API to use. Version 3 is supposed to be coming soon, but it is dumb and
     * broken.
     */
    public static final EmojiType TWITCH_EMOTE_VERSION = EmojiType.TWITCH_V2;

    /**
     * The bar to indicate progress as emotes load from the APIs
     */
    private EmojiLoadProgressDialog progressPopup;

    /**
     * Whether to use any emoji at all
     */
    private JCheckBox enableAll;

    /**
     * Whether emoji should be scaled to match line height
     */
    private JCheckBox scaleToLineHeight;

    /**
     * How much to scale the emoji relative to line height
     */
    private LabeledSlider emojiScale;

    /**
     * Whether to use the connected channel to select the FrankerFaceZ emotes
     */
    private JCheckBox useConnectChannel;

    /**
     * Text field for specifying a custom channel for loading FrankerFazeZ emotes
     */
    private LabeledInput channelInput;

    /**
     * Label for menu for how to deal with emote images that are not yet loaded
     */
    private JLabel emojiLoadingDisplayStratLabel;

    /**
     * Specifies how to deal with emote images that are not yet loaded
     */
    private JComboBox<EmojiLoadingDisplayStragegy> emojiLoadingDisplayStrat;

    /**
     * Whether to enable Twitch emotes
     */
    private JCheckBox enableTwitch;

    /**
     * Button to load Twitch emotes from the API
     */
    private JButton loadTwitchEmotes;

    /**
     * Button to cache images for common twitch emote images
     */
    private JButton cacheGlobalTwitchImages;

    /**
     * Disable Twitch subscriber emotes, use only global and robot emotes
     */
    private JCheckBox disableSubscriber;

    /**
     * Whether to enable FrankerFaceZ emotes
     */
    private JCheckBox enableFrankerFaceZ;

    /**
     * Button to load FrankerFaceZ emotes from the API
     */
    private JButton loadFrankerFaceZEmotes;

    /**
     * Button to cache images for FrankerFaceZ emote images
     */
    private JButton cacheFrankerFaceZImages;

    /**
     * The emoji config object that bridges the UI to the properties file
     */
    private ConfigEmoji config;

    /**
     * Panel to house general emoji options
     */
    private JPanel emojiPanel;

    /**
     * Panel to house Twitch emote options
     */
    private JPanel twitchPanel;

    /**
     * Panel to house FrankerFaceZ options
     */
    private JPanel frankerPanel;

    /**
     * Used to send reference to emoji worker
     */
    private ControlPanelEmoji thisControlPanel;

    /**
     * Construct an emoji control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param logBox
     */
    public ControlPanelEmoji(FontificatorProperties fProps, ChatWindow chatWindow, LogBox logBox)
    {
        super("Emoji", fProps, chatWindow, logBox);
    }

    private void resolveEnables()
    {
        final boolean all = enableAll.isSelected();

        emojiPanel.setEnabled(all);
        twitchPanel.setEnabled(all);
        frankerPanel.setEnabled(all);

        scaleToLineHeight.setEnabled(all);
        emojiScale.setEnabled(all);
        useConnectChannel.setEnabled(all);
        channelInput.setEnabled(all);
        emojiLoadingDisplayStratLabel.setEnabled(all);
        emojiLoadingDisplayStrat.setEnabled(all);
        channelInput.setEnabled(all && !useConnectChannel.isSelected());

        enableTwitch.setEnabled(all);
        disableSubscriber.setEnabled(all && enableTwitch.isSelected());
        loadTwitchEmotes.setEnabled(all && enableTwitch.isSelected());
        cacheGlobalTwitchImages.setEnabled(all && enableTwitch.isSelected() && config.isTwitchLoaded());

        enableFrankerFaceZ.setEnabled(all);
        loadFrankerFaceZEmotes.setEnabled(all && enableFrankerFaceZ.isSelected());
        cacheFrankerFaceZImages.setEnabled(all && enableFrankerFaceZ.isSelected() && config.isFfzLoaded());

        if (config.isConnectChannel())
        {
            // This won't pick up connection tab channel changes that haven't been saved by connecting
            final String cc = getConnectChannel();
            channelInput.setText(cc);
            config.setChannel(cc);
        }
    }

    @Override
    protected void build()
    {
        thisControlPanel = this;

        enableAll = new JCheckBox("Enable Emoji");

        progressPopup = new EmojiLoadProgressDialog(chat);

        scaleToLineHeight = new JCheckBox("Scale to Line Height");
        emojiScale = new LabeledSlider("Scale", "%", ConfigEmoji.MIN_SCALE, ConfigEmoji.MAX_SCALE, 100, 3);
        emojiLoadingDisplayStratLabel = new JLabel("Emoji Loading Display Strategy:");
        emojiLoadingDisplayStrat = new JComboBox<EmojiLoadingDisplayStragegy>(EmojiLoadingDisplayStragegy.values());
        useConnectChannel = new JCheckBox("Use Connection Channel for Emoji Loading");
        channelInput = new LabeledInput("Channel", 13);

        enableTwitch = new JCheckBox("Enable Twitch Emotes");
        disableSubscriber = new JCheckBox("Disable Subscriber Emotes");
        loadTwitchEmotes = new JButton("Load Twitch Emotes");
        cacheGlobalTwitchImages = new JButton("Cache Global Twitch Emotes");

        enableFrankerFaceZ = new JCheckBox("Enable FrankerFaceZ Emotes");
        loadFrankerFaceZEmotes = new JButton("Load FrankerFaceZ Emotes");
        cacheFrankerFaceZImages = new JButton("Cache FrankerFaceZ Emotes");

        emojiScale.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                config.setScale(emojiScale.getValue());
                chat.repaint();
            }
        });

        emojiLoadingDisplayStrat.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                config.setDisplayStrategy((EmojiLoadingDisplayStragegy) emojiLoadingDisplayStrat.getSelectedItem());
                chat.repaint();
            }
        });

        ActionListener cbal = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                config.setEmojiEnabled(enableAll.isSelected());
                config.setTwitchEnabled(enableTwitch.isSelected());
                config.setTwitchDisableSubscriber(disableSubscriber.isSelected());
                config.setFfzEnabled(enableFrankerFaceZ.isSelected());
                config.setConnectChannel(useConnectChannel.isSelected());
                config.setScaleToLine(scaleToLineHeight.isSelected());
                resolveEnables();
                chat.repaint();
            }
        };

        enableAll.addActionListener(cbal);
        scaleToLineHeight.addActionListener(cbal);
        enableTwitch.addActionListener(cbal);
        disableSubscriber.addActionListener(cbal);
        enableFrankerFaceZ.addActionListener(cbal);
        useConnectChannel.addActionListener(cbal);

        ActionListener bal = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();
                final String channel = channelInput.getText();

                if (channel == null || channel.trim().isEmpty())
                {
                    ChatWindow.popup.handleProblem("Channel is unspecified. Please enter " + (useConnectChannel.isSelected() ? "it on the Connection tab" : "a custom emoji channel") + ".");
                    return;
                }

                EmojiType type = null;
                EmojiOperation op = null;

                if (loadTwitchEmotes.equals(source))
                {
                    type = TWITCH_EMOTE_VERSION;
                    op = EmojiOperation.LOAD;
                }
                else if (cacheGlobalTwitchImages.equals(source))
                {
                    type = TWITCH_EMOTE_VERSION;
                    op = EmojiOperation.CACHE;
                }
                else if (loadFrankerFaceZEmotes.equals(source))
                {
                    type = EmojiType.FRANKERFACEZ;
                    op = EmojiOperation.LOAD;
                }
                else if (cacheFrankerFaceZImages.equals(source))
                {
                    type = EmojiType.FRANKERFACEZ;
                    op = EmojiOperation.CACHE;
                }

                workEmotes(type, op, true);
            }
        };

        loadTwitchEmotes.addActionListener(bal);
        cacheGlobalTwitchImages.addActionListener(bal);
        loadFrankerFaceZEmotes.addActionListener(bal);
        cacheFrankerFaceZImages.addActionListener(bal);

        cacheGlobalTwitchImages.setEnabled(false);
        cacheFrankerFaceZImages.setEnabled(false);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.gridwidth = 2;
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints topGbc = getGbc();
        topGbc.anchor = GridBagConstraints.WEST;
        topGbc.weightx = 1.0;
        topPanel.add(enableAll, topGbc);
        add(topPanel, gbc);
        gbc.gridy++;

        emojiPanel = new JPanel(new GridBagLayout());

        JPanel emojiSubPanel = new JPanel(new GridBagLayout());
        JPanel emojiSubPanelTop = new JPanel(new GridBagLayout());
        emojiPanel.setBorder(baseBorder);
        GridBagConstraints emojiGbc = getGbc();
        GridBagConstraints separatorGbc = getGbc();
        emojiGbc.anchor = GridBagConstraints.EAST;
        emojiGbc.gridwidth = 1;

        emojiSubPanelTop.add(emojiScale, emojiGbc);
        emojiGbc.anchor = GridBagConstraints.WEST;
        emojiGbc.gridx++;

        emojiSubPanelTop.add(scaleToLineHeight, emojiGbc);
        emojiGbc.gridy++;
        emojiGbc.gridx = 0;
        emojiGbc.anchor = GridBagConstraints.CENTER;
        emojiGbc.gridy = 0;

        emojiGbc.gridwidth = 2;
        emojiSubPanel.add(emojiSubPanelTop, emojiGbc);
        emojiGbc.gridwidth = 1;
        emojiGbc.gridy++;

        separatorGbc.weightx = 1.0;
        separatorGbc.gridwidth = 2;
        separatorGbc.fill = GridBagConstraints.HORIZONTAL;
        separatorGbc.gridy = emojiGbc.gridy;
        emojiSubPanel.add(new JSeparator(SwingConstants.HORIZONTAL), separatorGbc);

        emojiGbc.gridy++;
        emojiSubPanel.add(channelInput, emojiGbc);
        emojiGbc.gridx++;
        emojiSubPanel.add(useConnectChannel, emojiGbc);
        emojiGbc.gridy++;

        separatorGbc.gridy = emojiGbc.gridy;
        emojiSubPanel.add(new JSeparator(SwingConstants.HORIZONTAL), separatorGbc);

        emojiGbc.gridx = 0;
        emojiGbc.gridwidth = 2;
        emojiGbc.gridy = 0;
        emojiGbc.anchor = GridBagConstraints.CENTER;

        emojiGbc.gridy++;
        emojiPanel.add(emojiSubPanel, emojiGbc);
        emojiGbc.gridy++;
        emojiGbc.gridwidth = 1;

        emojiPanel.add(emojiLoadingDisplayStratLabel, emojiGbc);
        emojiGbc.gridx++;
        emojiPanel.add(emojiLoadingDisplayStrat, emojiGbc);
        emojiGbc.gridy++;
        emojiGbc.gridx = 0;

        gbc.gridwidth = 2;
        add(emojiPanel, gbc);
        gbc.gridy++;

        twitchPanel = new JPanel(new GridBagLayout());
        twitchPanel.setBorder(BorderFactory.createTitledBorder(baseBorder, "Twitch Emotes"));
        GridBagConstraints twitchGbc = getGbc();
        twitchPanel.add(enableTwitch, twitchGbc);
        twitchGbc.gridy++;
        twitchPanel.add(loadTwitchEmotes, twitchGbc);
        twitchGbc.gridy++;
        twitchPanel.add(cacheGlobalTwitchImages, twitchGbc);
        twitchGbc.gridy++;
        twitchPanel.add(disableSubscriber, twitchGbc);
        twitchGbc.gridy++;

        gbc.gridwidth = 1;
        add(twitchPanel, gbc);
        gbc.gridx++;

        frankerPanel = new JPanel(new GridBagLayout());
        frankerPanel.setBorder(BorderFactory.createTitledBorder(baseBorder, "FrankerFaceZ Emotes"));
        GridBagConstraints frankerGbc = getGbc();
        frankerPanel.add(enableFrankerFaceZ, frankerGbc);
        frankerGbc.gridy++;
        frankerPanel.add(loadFrankerFaceZEmotes, frankerGbc);
        frankerGbc.gridy++;
        frankerPanel.add(cacheFrankerFaceZImages, frankerGbc);
        frankerGbc.gridy++;

        add(frankerPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        // Filler panel
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JPanel(), gbc); // filler panel
        gbc.gridy++;
    }

    /**
     * @param type
     * @param op
     * @param holdPopupOpen
     *            Whether to close the popup automatically after the operation is complete
     */
    public void workEmotes(EmojiType type, EmojiOperation op, boolean holdPopupOpen)
    {
        final String channel = getConnectChannel();

        // A SwingWorkers can only be run once because... reasons. So each call to do work must be on a freshly
        // instantiated worker object.
        EmojiWorker worker = new EmojiWorker(chat.getEmojiManager(), progressPopup, channel, type, op, thisControlPanel, logBox, holdPopupOpen);

        try
        {
            final String dialogTitle = (op == EmojiOperation.LOAD ? "Load" : "Cache") + " " + (type == TWITCH_EMOTE_VERSION ? "Twitch" : "FrankerFaceZ") + " Emotes";
            worker.execute();
            progressPopup.showDialog(dialogTitle, worker, new EmojiWorkerReport(dialogTitle, 0));
        }
        catch (Exception ex)
        {
            String errorMessage = "Unable to load " + (type == TWITCH_EMOTE_VERSION ? "Twitch" : "FrankerFaceZ") + " emotes for " + channel;
            logger.error(errorMessage, ex);
            ChatWindow.popup.handleProblem(errorMessage);
        }
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        config = fProps.getEmojiConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        this.enableAll.setSelected(config.isEmojiEnabled());
        this.scaleToLineHeight.setSelected(config.isScaleToLine());
        this.emojiScale.setValue(config.getScale());
        this.useConnectChannel.setSelected(config.isConnectChannel());
        this.channelInput.setText(useConnectChannel.isSelected() ? getConnectChannel() : config.getChannel());
        this.emojiLoadingDisplayStrat.setSelectedItem(config.getDisplayStrategy());
        this.enableTwitch.setSelected(config.isTwitchEnabled());
        this.loadTwitchEmotes.setSelected(config.isTwitchEnabled());
        this.disableSubscriber.setSelected(config.isTwitchSubscriberDisable());
        this.enableFrankerFaceZ.setSelected(config.isFfzEnabled());

        resolveEnables();
    }

    private String getConnectChannel()
    {
        return fProps.getIrcConfig().getChannelNoHash();
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        LoadConfigReport report = new LoadConfigReport();

        if (useConnectChannel.isSelected())
        {
            channelInput.setText(getConnectChannel());
        }

        if (channelInput.getText().isEmpty())
        {
            report.addError("An input value for Channel is required", LoadConfigErrorType.MISSING_VALUE);
        }

        return report;
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setEmojiEnabled(enableAll.isSelected());
        config.setScaleToLine(scaleToLineHeight.isSelected());
        config.setScale(emojiScale.getValue());
        config.setChannel(channelInput.getText());
        config.setConnectChannel(useConnectChannel.isSelected());
        config.setDisplayStrategy((EmojiLoadingDisplayStragegy) emojiLoadingDisplayStrat.getSelectedItem());
        config.setTwitchEnabled(enableTwitch.isSelected());
        config.setTwitchDisableSubscriber(disableSubscriber.isSelected());
        config.setFfzEnabled(enableFrankerFaceZ.isSelected());
    }

    public void enableCache(EmojiType type)
    {
        switch (type)
        {
        case FRANKERFACEZ:
            loadFrankerFaceZEmotes.setText("Reload FrankerFaceZ Emotes");
            cacheFrankerFaceZImages.setEnabled(true);
            config.setFfzLoaded(true);
            break;
        case TWITCH_V2:
        case TWITCH_V3:
            loadTwitchEmotes.setText("Reload Twitch Emotes");
            cacheGlobalTwitchImages.setEnabled(true);
            config.setTwitchLoaded(true);
            break;
        default:
            break;
        }
    }

    public void disableCache(EmojiType type)
    {
        switch (type)
        {
        case FRANKERFACEZ:
            cacheFrankerFaceZImages.setEnabled(false);
            break;
        case TWITCH_V2:
        case TWITCH_V3:
            cacheGlobalTwitchImages.setEnabled(false);
            break;
        default:
            break;
        }
    }
}
