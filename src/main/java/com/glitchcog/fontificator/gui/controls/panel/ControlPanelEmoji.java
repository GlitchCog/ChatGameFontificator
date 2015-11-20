package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.glitchcog.fontificator.bot.ChatViewerBot;
import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.config.EmojiLoadingDisplayStragegy;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.emoji.EmojiOperation;
import com.glitchcog.fontificator.emoji.EmojiType;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledSlider;
import com.glitchcog.fontificator.gui.emoji.EmojiLoadProgressPanel;
import com.glitchcog.fontificator.gui.emoji.EmojiWorker;
import com.glitchcog.fontificator.gui.emoji.EmojiWorkerReport;

/**
 * Sub-control panel to configure emoji and badges in the chat view
 * 
 * @author Matt Yanos
 */
public class ControlPanelEmoji extends ControlPanelBase
{
    private static final long serialVersionUID = 1L;

    /**
     * This is the version of the Twitch emote API to use
     */
    public static final EmojiType TWITCH_EMOTE_VERSION = EmojiType.TWITCH_V3;

    /**
     * The bar to indicate progress as emotes load from the APIs
     */
    private EmojiLoadProgressPanel progressPanel;

    /**
     * Whether to use any emoji at all
     */
    private JCheckBox enableAll;

    /**
     * Whether to display user badges
     */
    private JCheckBox enableTwitchBadges;

    /**
     * Whether emoji should be scaled to match line height
     */
    private JCheckBox emojiScaleToLineHeight;

    /**
     * How much to scale the emoji relative to line height
     */
    private LabeledSlider emojiScale;

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
     * Whether to cache global Twitch emotes
     */
    private JCheckBox cacheTwitch;

    /**
     * Whether to enable FrankerFaceZ emotes
     */
    private JCheckBox enableFrankerFaceZ;

    /**
     * Whether to cache global and channel specific FrankerFazeZ emotes
     */
    private JCheckBox cacheFrankerFaceZ;

    /**
     * The emoji config object that bridges the UI to the properties file
     */
    private ConfigEmoji config;

    /**
     * Panel to house general emoji options
     */
    private JPanel scaleAndDisplayPanel;

    /**
     * Panel to house Twitch emote options
     */
    private JPanel twitchPanel;

    /**
     * Panel to house FrankerFaceZ options
     */
    private JPanel frankerPanel;

    /**
     * A reference to the IRC bot to check whether the user is already connected when they check to load or cache an
     * emote type
     */
    private final ChatViewerBot bot;

    /**
     * Construct an emoji control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param bot
     * @param logBox
     */
    public ControlPanelEmoji(FontificatorProperties fProps, ChatWindow chatWindow, ChatViewerBot bot, LogBox logBox)
    {
        super("Emoji", fProps, chatWindow, logBox);
        this.progressPanel.setEmojiConfig(config);
        this.bot = bot;
    }

    private void resolveEnables()
    {
        final boolean all = enableAll.isSelected();
        final boolean badges = enableTwitchBadges.isSelected();

        scaleAndDisplayPanel.setEnabled(all || badges);
        emojiScaleToLineHeight.setEnabled(all || badges);
        emojiScale.setEnabled(all || badges);
        emojiLoadingDisplayStratLabel.setEnabled(all || badges);
        emojiLoadingDisplayStrat.setEnabled(all || badges);

        twitchPanel.setEnabled(all);
        enableTwitch.setEnabled(all);
        cacheTwitch.setEnabled(all && enableTwitch.isSelected());

        frankerPanel.setEnabled(all);
        enableFrankerFaceZ.setEnabled(all);
        cacheFrankerFaceZ.setEnabled(all && enableFrankerFaceZ.isSelected());
    }

    @Override
    protected void build()
    {
        enableAll = new JCheckBox("Enable Emoji");
        enableTwitchBadges = new JCheckBox("Enable Twitch Badges");

        progressPanel = new EmojiLoadProgressPanel(chat);

        emojiScaleToLineHeight = new JCheckBox("Scale to Line Height");
        emojiScale = new LabeledSlider("Emoji Scale", "%", ConfigEmoji.MIN_SCALE, ConfigEmoji.MAX_SCALE, 100, 3);
        emojiLoadingDisplayStratLabel = new JLabel("Emoji Loading Display Strategy:");
        emojiLoadingDisplayStrat = new JComboBox<EmojiLoadingDisplayStragegy>(EmojiLoadingDisplayStragegy.values());

        enableTwitch = new JCheckBox("Enable Twitch Emotes");
        cacheTwitch = new JCheckBox("Cache Global Twitch Emotes");

        enableFrankerFaceZ = new JCheckBox("Enable FrankerFaceZ Emotes");
        cacheFrankerFaceZ = new JCheckBox("Cache FrankerFaceZ Emotes");

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

        // This action listener is for loading emotes and badges whenever a checkbox is checked while connected
        ActionListener cbal = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JCheckBox source = (JCheckBox) e.getSource();
                if (bot.isConnected())
                {

                    Set<EmojiType> types = new HashSet<EmojiType>();
                    Set<EmojiOperation> ops = new HashSet<EmojiOperation>();

                    final boolean clickAll = enableAll.equals(source) && enableAll.isSelected();
                    final boolean clickTwitchLoad = !config.isTwitchLoaded() && (clickAll || enableTwitch.equals(source)) && enableTwitch.isSelected();
                    final boolean clickTwitchCache = !config.isTwitchCached() && (clickAll || cacheTwitch.equals(source)) && cacheTwitch.isSelected();
                    final boolean clickFfzLoad = !config.isFfzLoaded(getConnectChannel()) && (clickAll || enableFrankerFaceZ.equals(source)) && enableFrankerFaceZ.isSelected();
                    final boolean clickFfzCache = !config.isFfzCached() && (clickAll || cacheFrankerFaceZ.equals(source)) && cacheFrankerFaceZ.isSelected();

                    // Badges is independent of enableAll
                    final boolean clickBadges = !config.isTwitchBadgesLoaded(getConnectChannel()) && enableTwitchBadges.equals(source) && enableTwitchBadges.isSelected();

                    if (clickTwitchLoad)
                    {
                        types.add(TWITCH_EMOTE_VERSION);
                        ops.add(EmojiOperation.LOAD);
                    }

                    if (clickTwitchCache)
                    {
                        types.add(TWITCH_EMOTE_VERSION);
                        ops.add(EmojiOperation.CACHE);
                    }

                    if (clickFfzLoad)
                    {
                        types.add(EmojiType.FRANKERFACEZ_CHANNEL);
                        types.add(EmojiType.FRANKERFACEZ_GLOBAL);
                        ops.add(EmojiOperation.LOAD);
                    }

                    if (clickFfzCache)
                    {
                        types.add(EmojiType.FRANKERFACEZ_CHANNEL);
                        types.add(EmojiType.FRANKERFACEZ_GLOBAL);
                        ops.add(EmojiOperation.CACHE);
                    }

                    if (clickBadges)
                    {
                        types.add(EmojiType.TWITCH_BADGE);
                        ops.add(EmojiOperation.LOAD);
                    }

                    loadEmojiWork(types, ops);
                    runEmojiWork();
                }

                config.setEmojiEnabled(enableAll.isSelected());
                config.setBadgesEnabled(enableTwitchBadges.isSelected());
                config.setTwitchEnabled(enableTwitch.isSelected());
                config.setTwitchCacheEnabled(cacheTwitch.isSelected());
                config.setFfzEnabled(enableFrankerFaceZ.isSelected());
                config.setFfzCacheEnabled(cacheFrankerFaceZ.isSelected());
                config.setScaleToLine(emojiScaleToLineHeight.isSelected());
                resolveEnables();

                chat.repaint();
            }
        };

        enableAll.addActionListener(cbal);
        enableTwitchBadges.addActionListener(cbal);
        emojiScaleToLineHeight.addActionListener(cbal);
        enableTwitch.addActionListener(cbal);
        enableFrankerFaceZ.addActionListener(cbal);

        JPanel allEnabledPanel = new JPanel(new GridBagLayout());
        GridBagConstraints allGbc = getGbc();
        allGbc.anchor = GridBagConstraints.CENTER;
        allGbc.fill = GridBagConstraints.NONE;
        allGbc.weightx = 0.5;
        allEnabledPanel.add(enableAll, allGbc);
        allGbc.gridx++;
        allGbc.weightx = 0.5;
        allEnabledPanel.add(enableTwitchBadges, allGbc);
        allGbc.gridwidth = 2;
        allGbc.gridy++;

        scaleAndDisplayPanel = new JPanel(new GridBagLayout());
        scaleAndDisplayPanel.setBorder(baseBorder);
        GridBagConstraints scaleAndDisplayGbc = getGbc();
        scaleAndDisplayGbc.anchor = GridBagConstraints.CENTER;
        scaleAndDisplayGbc.fill = GridBagConstraints.HORIZONTAL;
        scaleAndDisplayGbc.weightx = 1.0;
        scaleAndDisplayPanel.add(emojiScale, scaleAndDisplayGbc);
        scaleAndDisplayGbc.gridx++;
        scaleAndDisplayGbc.fill = GridBagConstraints.NONE;
        scaleAndDisplayGbc.weightx = 0.0;
        scaleAndDisplayPanel.add(emojiScaleToLineHeight, scaleAndDisplayGbc);
        scaleAndDisplayGbc.gridx = 0;
        scaleAndDisplayGbc.gridy++;
        scaleAndDisplayGbc.gridwidth = 2;
        scaleAndDisplayGbc.weightx = 1.0;
        scaleAndDisplayGbc.fill = GridBagConstraints.HORIZONTAL;
        scaleAndDisplayPanel.add(new JSeparator(SwingConstants.HORIZONTAL), scaleAndDisplayGbc);
        scaleAndDisplayGbc.gridy++;
        JPanel bottomOfScaleDisplayPanel = new JPanel();
        bottomOfScaleDisplayPanel.add(emojiLoadingDisplayStratLabel);
        bottomOfScaleDisplayPanel.add(emojiLoadingDisplayStrat);
        scaleAndDisplayPanel.add(bottomOfScaleDisplayPanel, scaleAndDisplayGbc);

        twitchPanel = new JPanel(new GridBagLayout());
        twitchPanel.setBorder(BorderFactory.createTitledBorder(baseBorder, "Twitch Emotes"));
        GridBagConstraints twitchGbc = getGbc();
        twitchPanel.add(enableTwitch, twitchGbc);
        twitchGbc.gridy++;
        twitchPanel.add(cacheTwitch, twitchGbc);
        twitchGbc.gridy++;

        frankerPanel = new JPanel(new GridBagLayout());
        frankerPanel.setBorder(BorderFactory.createTitledBorder(baseBorder, "FrankerFaceZ Emotes"));
        GridBagConstraints frankerGbc = getGbc();
        frankerPanel.add(enableFrankerFaceZ, frankerGbc);
        frankerGbc.gridy++;
        frankerPanel.add(cacheFrankerFaceZ, frankerGbc);
        frankerGbc.gridy++;

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 2;

        add(allEnabledPanel, gbc);
        gbc.gridy++;
        add(scaleAndDisplayPanel, gbc);
        gbc.gridy++;

        gbc.gridwidth = 1;
        add(twitchPanel, gbc);
        gbc.gridx++;

        add(frankerPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(progressPanel, gbc);
        gbc.gridy++;
    }

    /**
     * Load emoji based on what's already happened and what is checked. This is called by the IRC control panel when a
     * connection is first made to the IRC channel.
     */
    public void loadEmojiWork()
    {
        if (enableAll.isSelected() || enableTwitchBadges.isSelected())
        {
            Set<EmojiType> types = new HashSet<EmojiType>();
            Set<EmojiOperation> ops = new HashSet<EmojiOperation>();

            if (enableAll.isSelected())
            {
                final boolean workTwitchLoad = !config.isTwitchLoaded() && enableTwitch.isSelected();
                final boolean workTwitchCache = !config.isTwitchCached() && cacheTwitch.isSelected();
                final boolean workFfzLoad = !config.isFfzLoaded(getConnectChannel()) && enableFrankerFaceZ.isSelected();
                final boolean workFfzCache = !config.isFfzCached() && cacheFrankerFaceZ.isSelected();

                if (workTwitchLoad)
                {
                    types.add(TWITCH_EMOTE_VERSION);
                    ops.add(EmojiOperation.LOAD);
                }

                if (workTwitchCache)
                {
                    types.add(TWITCH_EMOTE_VERSION);
                    ops.add(EmojiOperation.CACHE);
                }

                if (workFfzLoad)
                {
                    types.add(EmojiType.FRANKERFACEZ_CHANNEL);
                    types.add(EmojiType.FRANKERFACEZ_GLOBAL);
                    ops.add(EmojiOperation.LOAD);
                }

                if (workFfzCache)
                {
                    types.add(EmojiType.FRANKERFACEZ_CHANNEL);
                    types.add(EmojiType.FRANKERFACEZ_GLOBAL);
                    ops.add(EmojiOperation.CACHE);
                }
            }

            if (enableTwitchBadges.isSelected())
            {
                types.add(EmojiType.TWITCH_BADGE);
                ops.add(EmojiOperation.LOAD);
            }

            loadEmojiWork(types, ops);
        }
    }

    /**
     * Queue the work of a specified operation (load or cache) on a specified type of emote (Twitch or FrankerFaceZ).
     * Call runEmoteWork to run the loaded work in series.
     * 
     * @param types
     * @param ops
     */
    public void loadEmojiWork(Collection<EmojiType> types, Collection<EmojiOperation> ops)
    {
        for (EmojiOperation op : ops)
        {
            for (EmojiType type : types)
            {
                loadEmojiWork(type, op);
            }
        }
    }

    /**
     * Queue the work of a specified operation (load or cache) on a specified type of emote (Twitch or FrankerFaceZ).
     * Call runEmoteWork to run the loaded work in series.
     * 
     * @param type
     * @param op
     */
    public void loadEmojiWork(EmojiType type, EmojiOperation op)
    {
        final String channel = getConnectChannel();

        final String reportMessage = (op == EmojiOperation.LOAD ? "Load" : "Cache") + " " + type.getDescription() + " Emotes";
        EmojiWorkerReport initialReport = new EmojiWorkerReport(reportMessage, 0);

        // A SwingWorkers can only be run once because... reasons. So each call to do work must be on a freshly
        // instantiated worker object.
        EmojiWorker worker = new EmojiWorker(chat.getEmojiManager(), progressPanel, channel, type, op, logBox, initialReport);

        progressPanel.addWorkToQueue(worker);
    }

    /**
     * To be called after loadEmoteWork has set up the tasks
     */
    public void runEmojiWork()
    {
        progressPanel.initiateWork();
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
        this.emojiScaleToLineHeight.setSelected(config.isScaleToLine());
        this.emojiScale.setValue(config.getScale());
        this.emojiLoadingDisplayStrat.setSelectedItem(config.getDisplayStrategy());
        this.enableTwitch.setSelected(config.isTwitchEnabled());
        this.cacheTwitch.setSelected(config.isTwitchCacheEnabled());
        this.enableFrankerFaceZ.setSelected(config.isFfzEnabled());
        this.cacheFrankerFaceZ.setSelected(config.isFfzCacheEnabled());

        resolveEnables();
    }

    private String getConnectChannel()
    {
        return fProps.getIrcConfig().getChannelNoHash();
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        return new LoadConfigReport();
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setEmojiEnabled(enableAll.isSelected());
        config.setScaleToLine(emojiScaleToLineHeight.isSelected());
        config.setScale(emojiScale.getValue());
        config.setDisplayStrategy((EmojiLoadingDisplayStragegy) emojiLoadingDisplayStrat.getSelectedItem());
        config.setTwitchEnabled(enableTwitch.isSelected());
        config.setTwitchCacheEnabled(cacheTwitch.isSelected());
        config.setFfzEnabled(enableFrankerFaceZ.isSelected());
        config.setFfzCacheEnabled(cacheFrankerFaceZ.isSelected());
    }

}
