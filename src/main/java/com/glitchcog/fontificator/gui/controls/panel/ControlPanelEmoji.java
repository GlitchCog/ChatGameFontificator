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
import com.glitchcog.fontificator.emoji.EmojiJob;
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
     * This is the version of the Twitch emote API to use. Using V2 just for global Twitch emotes for manual messages.
     */
    public static final EmojiType TWITCH_EMOTE_VERSION = EmojiType.TWITCH_V2;

    /**
     * The bar to indicate progress as emotes load from the APIs
     */
    private EmojiLoadProgressPanel progressPanel;

    /**
     * Whether to use any emoji at all
     */
    private JCheckBox enableAll;

    /**
     * Whether to display Twitch user badges
     */
    private JCheckBox enableTwitchBadges;

    /**
     * Whether to display FrankerFaceZ badges
     */
    private JCheckBox enableFfzBadges;

    /**
     * Whether emoji should be scaled to match line height or just scaled against their actual size
     */
    private JCheckBox emojiScaleToLineHeight;

    /**
     * How much to scale the emoji
     */
    private LabeledSlider emojiScale;

    /**
     * Whether badges should be scaled to match line height or just scaled against their actual size
     */
    private JCheckBox badgeScaleToLineHeight;

    /**
     * How much to scale the badges
     */
    private LabeledSlider badgeScale;

    /**
     * How much to offset the badge vertically, needed because the middle line of a font image is much lower than the
     * middle line calculated from the font baseline to the top
     */
    private LabeledSlider badgeHeightOffset;

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
     * Whether to enable BetterTTV emotes
     */
    private JCheckBox enableBetterTtv;

    /**
     * Whether to cache global and channel specific BetterTTV emotes
     */
    private JCheckBox cacheBetterTtv;

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
     * Panel to house BetterTTV options
     */
    private JPanel betterPanel;

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
        final boolean badges = enableTwitchBadges.isSelected() | enableFfzBadges.isSelected();

        scaleAndDisplayPanel.setEnabled(all || badges);
        emojiScaleToLineHeight.setEnabled(all);
        badgeScaleToLineHeight.setEnabled(badges);
        emojiScale.setEnabled(all);
        badgeScale.setEnabled(badges);
        badgeHeightOffset.setEnabled(badges);
        emojiLoadingDisplayStratLabel.setEnabled(all || badges);
        emojiLoadingDisplayStrat.setEnabled(all || badges);

        twitchPanel.setEnabled(all);
        enableTwitch.setEnabled(all);
        cacheTwitch.setEnabled(all && enableTwitch.isSelected());

        frankerPanel.setEnabled(all);
        enableFrankerFaceZ.setEnabled(all);
        cacheFrankerFaceZ.setEnabled(all && enableFrankerFaceZ.isSelected());

        betterPanel.setEnabled(all);
        enableBetterTtv.setEnabled(all);
        cacheBetterTtv.setEnabled(all && enableBetterTtv.isSelected());

        progressPanel.handleButtonEnables();
    }

    @Override
    protected void build()
    {
        enableAll = new JCheckBox("Enable Emoji");
        enableTwitchBadges = new JCheckBox("Enable Twitch Badges");
        enableFfzBadges = new JCheckBox("Enable FrankerFaceZ Badges");

        progressPanel = new EmojiLoadProgressPanel(chat, this);

        emojiScaleToLineHeight = new JCheckBox("Relative to Line Height");
        emojiScale = new LabeledSlider("Emoji Scale", "%", ConfigEmoji.MIN_SCALE, ConfigEmoji.MAX_SCALE, 100, 3);

        badgeScaleToLineHeight = new JCheckBox("Relative to Line Height");
        badgeScale = new LabeledSlider("Badge Scale", "%", ConfigEmoji.MIN_SCALE, ConfigEmoji.MAX_SCALE, 100, 3);
        badgeHeightOffset = new LabeledSlider("Badge Height Offset", "pixels", ConfigEmoji.MIN_BADGE_OFFSET, ConfigEmoji.MAX_BADGE_OFFSET, 0, 3);

        emojiLoadingDisplayStratLabel = new JLabel("Emoji Loading Display Strategy:");
        emojiLoadingDisplayStrat = new JComboBox<EmojiLoadingDisplayStragegy>(EmojiLoadingDisplayStragegy.values());

        enableTwitch = new JCheckBox("Enable Twitch Emotes");
        cacheTwitch = new JCheckBox("Cache Global Twitch Emotes");

        enableFrankerFaceZ = new JCheckBox("Enable FrankerFaceZ Emotes");
        cacheFrankerFaceZ = new JCheckBox("Cache FrankerFaceZ Emotes");

        enableBetterTtv = new JCheckBox("Enable BetterTTV Emotes");
        cacheBetterTtv = new JCheckBox("Cache BetterTTV Emotes");

        emojiScale.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                config.setEmojiScale(emojiScale.getValue());
                chat.repaint();
            }
        });

        badgeScale.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                config.setBadgeScale(badgeScale.getValue());
                chat.repaint();
            }
        });

        badgeHeightOffset.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                config.setBadgeHeightOffset(badgeHeightOffset.getValue());
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
                    Set<EmojiJob> jobsToRun = new HashSet<EmojiJob>();
                    Set<EmojiJob> jobsToCancel = new HashSet<EmojiJob>();

                    final boolean clickAll = enableAll.equals(source);
                    final boolean clickTwitchLoad = clickAll || enableTwitch.equals(source);
                    final boolean clickTwitchCache = clickAll || cacheTwitch.equals(source);
                    final boolean clickFfzLoad = clickAll || enableFrankerFaceZ.equals(source);
                    final boolean clickFfzCache = clickAll || cacheFrankerFaceZ.equals(source);
                    final boolean clickBttvLoad = clickAll || enableBetterTtv.equals(source);
                    final boolean clickBttvCache = clickAll || cacheBetterTtv.equals(source);

                    // Badges is independent of enableAll
                    final boolean clickTwitchBadges = !config.isTwitchBadgesLoaded(getConnectChannel()) && enableTwitchBadges.equals(source);
                    final boolean clickFfzBadges = !config.isFfzBadgesLoaded(getConnectChannel()) && enableFfzBadges.equals(source);

                    if (clickTwitchLoad && !config.isTwitchLoaded())
                    {
                        EmojiJob job = new EmojiJob(TWITCH_EMOTE_VERSION, EmojiOperation.LOAD, getConnectChannel());
                        if (enableAll.isSelected() && enableTwitch.isSelected())
                        {
                            jobsToRun.add(job);
                        }
                        else
                        {
                            jobsToCancel.add(job);
                        }
                    }

                    if (clickTwitchCache && !config.isTwitchCached())
                    {
                        EmojiJob job = new EmojiJob(TWITCH_EMOTE_VERSION, EmojiOperation.CACHE);
                        if (enableAll.isSelected() && cacheTwitch.isSelected())
                        {
                            jobsToRun.add(job);
                        }
                        else
                        {
                            jobsToCancel.add(job);
                        }
                    }

                    if (clickFfzLoad && !config.isFfzLoaded(getConnectChannel()))
                    {
                        EmojiJob jobA = new EmojiJob(EmojiType.FRANKERFACEZ_CHANNEL, EmojiOperation.LOAD, getConnectChannel());
                        EmojiJob jobB = new EmojiJob(EmojiType.FRANKERFACEZ_GLOBAL, EmojiOperation.LOAD);

                        if (enableAll.isSelected() && enableFrankerFaceZ.isSelected())
                        {
                            jobsToRun.add(jobA);
                            jobsToRun.add(jobB);
                        }
                        else
                        {
                            jobsToCancel.add(jobA);
                            jobsToCancel.add(jobB);
                        }
                    }

                    if (clickBttvLoad && !config.isBttvLoaded(getConnectChannel()))
                    {
                        EmojiJob jobA = new EmojiJob(EmojiType.BETTER_TTV_CHANNEL, EmojiOperation.LOAD, getConnectChannel());
                        EmojiJob jobB = new EmojiJob(EmojiType.BETTER_TTV_GLOBAL, EmojiOperation.LOAD);

                        if (enableAll.isSelected() && enableBetterTtv.isSelected())
                        {
                            jobsToRun.add(jobA);
                            jobsToRun.add(jobB);
                        }
                        else
                        {
                            jobsToCancel.add(jobA);
                            jobsToCancel.add(jobB);
                        }
                    }

                    if (clickFfzCache && !config.isFfzCached())
                    {
                        EmojiJob jobA = new EmojiJob(EmojiType.FRANKERFACEZ_CHANNEL, EmojiOperation.CACHE);
                        EmojiJob jobB = new EmojiJob(EmojiType.FRANKERFACEZ_GLOBAL, EmojiOperation.CACHE);
                        if (enableAll.isSelected() && cacheFrankerFaceZ.isSelected())
                        {
                            jobsToRun.add(jobA);
                            jobsToRun.add(jobB);
                        }
                        else
                        {
                            jobsToCancel.add(jobA);
                            jobsToCancel.add(jobB);
                        }
                    }

                    if (clickBttvCache && !config.isBttvCached())
                    {
                        EmojiJob jobA = new EmojiJob(EmojiType.BETTER_TTV_CHANNEL, EmojiOperation.CACHE);
                        EmojiJob jobB = new EmojiJob(EmojiType.BETTER_TTV_GLOBAL, EmojiOperation.CACHE);
                        if (enableAll.isSelected() && cacheBetterTtv.isSelected())
                        {
                            jobsToRun.add(jobA);
                            jobsToRun.add(jobB);
                        }
                        else
                        {
                            jobsToCancel.add(jobA);
                            jobsToCancel.add(jobB);
                        }
                    }

                    if (clickTwitchBadges && !config.isTwitchBadgesLoaded(getConnectChannel()))
                    {
                        EmojiJob job = new EmojiJob(EmojiType.TWITCH_BADGE, EmojiOperation.LOAD, getConnectChannel());
                        // No check for enable all here, because badges are independent of the emoji enableAll toggle
                        if (enableTwitchBadges.isSelected())
                        {
                            jobsToRun.add(job);
                        }
                        else
                        {
                            jobsToCancel.add(job);
                        }
                    }

                    if (clickFfzBadges && !config.isFfzBadgesLoaded(getConnectChannel()))
                    {
                        EmojiJob job = new EmojiJob(EmojiType.FRANKERFACEZ_BADGE, EmojiOperation.LOAD, getConnectChannel());
                        // No check for enable all here, because badges are independent of the emoji enableAll toggle
                        if (enableFfzBadges.isSelected())
                        {
                            jobsToRun.add(job);
                        }
                        else
                        {
                            jobsToCancel.add(job);
                        }
                    }

                    loadEmojiWork(jobsToRun);

                    cancelEmojiWork(jobsToCancel);

                    if (!jobsToRun.isEmpty() && !progressPanel.isCurrentlyRunning())
                    {
                        runEmojiWork();
                    }
                }

                // These are only the checkboxes handled in fillConfigFromInput()
                config.setEmojiEnabled(enableAll.isSelected());
                config.setTwitchBadgesEnabled(enableTwitchBadges.isSelected());
                config.setFfzBadgesEnabled(enableFfzBadges.isSelected());
                config.setTwitchEnabled(enableTwitch.isSelected());
                config.setTwitchCacheEnabled(cacheTwitch.isSelected());
                config.setFfzEnabled(enableFrankerFaceZ.isSelected());
                config.setFfzCacheEnabled(cacheFrankerFaceZ.isSelected());
                config.setBttvEnabled(enableBetterTtv.isSelected());
                config.setBttvCacheEnabled(cacheBetterTtv.isSelected());
                config.setEmojiScaleToLine(emojiScaleToLineHeight.isSelected());
                config.setBadgeScaleToLine(badgeScaleToLineHeight.isSelected());
                resolveEnables();

                chat.repaint();
            }
        };

        enableAll.addActionListener(cbal);
        enableTwitchBadges.addActionListener(cbal);
        enableFfzBadges.addActionListener(cbal);
        emojiScaleToLineHeight.addActionListener(cbal);
        badgeScaleToLineHeight.addActionListener(cbal);
        enableTwitch.addActionListener(cbal);
        cacheTwitch.addActionListener(cbal);
        enableFrankerFaceZ.addActionListener(cbal);
        cacheFrankerFaceZ.addActionListener(cbal);
        enableBetterTtv.addActionListener(cbal);
        cacheBetterTtv.addActionListener(cbal);

        JPanel allEnabledPanel = new JPanel(new GridBagLayout());
        GridBagConstraints allGbc = getGbc();
        allGbc.anchor = GridBagConstraints.CENTER;
        allGbc.fill = GridBagConstraints.NONE;
        allGbc.weightx = 0.333;
        allEnabledPanel.add(enableAll, allGbc);
        allGbc.gridx++;
        allEnabledPanel.add(enableTwitchBadges, allGbc);
        allGbc.gridx++;
        allEnabledPanel.add(enableFfzBadges, allGbc);
        allGbc.gridx++;
        allGbc.gridwidth = 2;
        allGbc.gridy++;

        scaleAndDisplayPanel = new JPanel(new GridBagLayout());
        scaleAndDisplayPanel.setBorder(baseBorder);
        GridBagConstraints scaleAndDisplayGbc = getGbc();

        // Emoji scaling
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

        // Badge scaling
        scaleAndDisplayGbc.anchor = GridBagConstraints.CENTER;
        scaleAndDisplayGbc.fill = GridBagConstraints.HORIZONTAL;
        scaleAndDisplayGbc.weightx = 1.0;
        scaleAndDisplayPanel.add(badgeScale, scaleAndDisplayGbc);
        scaleAndDisplayGbc.gridx++;
        scaleAndDisplayGbc.fill = GridBagConstraints.NONE;
        scaleAndDisplayGbc.weightx = 0.0;
        scaleAndDisplayPanel.add(badgeScaleToLineHeight, scaleAndDisplayGbc);
        scaleAndDisplayGbc.gridx = 0;
        scaleAndDisplayGbc.gridy++;
        scaleAndDisplayGbc.fill = GridBagConstraints.HORIZONTAL;
        scaleAndDisplayGbc.gridwidth = 2;
        scaleAndDisplayPanel.add(badgeHeightOffset, scaleAndDisplayGbc);
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

        betterPanel = new JPanel(new GridBagLayout());
        betterPanel.setBorder(BorderFactory.createTitledBorder(baseBorder, "BetterTTV Emotes"));
        GridBagConstraints betterGbc = getGbc();
        betterPanel.add(enableBetterTtv, betterGbc);
        betterGbc.gridy++;
        betterPanel.add(cacheBetterTtv, betterGbc);
        betterGbc.gridy++;

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.gridwidth = 3;
        add(allEnabledPanel, gbc);
        gbc.gridy++;

        gbc.gridwidth = 1;
        add(twitchPanel, gbc);
        gbc.gridx++;

        add(frankerPanel, gbc);
        gbc.gridx++;

        add(betterPanel, gbc);
        gbc.gridx++;
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.gridwidth = 3;
        add(scaleAndDisplayPanel, gbc);
        gbc.gridy++;

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        add(progressPanel, gbc);
        gbc.gridy++;
    }

    private String getConnectChannel()
    {
        return fProps.getIrcConfig().getChannelNoHash();
    }

    /**
     * Load emoji based on what's already happened and what is checked. This is called by the IRC control panel when a
     * connection is first made to the IRC channel or by the manual load button on the progress panel at the bottom of
     * the Emoji tab of the Control Window.
     */
    public void loadAndRunEmojiWork()
    {
        boolean atLeastOneBadgeSelected = enableTwitchBadges.isSelected() || enableFfzBadges.isSelected();
        if (enableAll.isSelected() || atLeastOneBadgeSelected)
        {
            Set<EmojiJob> jobs = collectJobs();

            if (jobs.isEmpty())
            {
                progressPanel.log("No new work found.");
            }
            else
            {
                loadEmojiWork(jobs);
            }
            runEmojiWork();
        }
        progressPanel.handleButtonEnables();
    }

    public int countJobs()
    {
        int count = 0;
        if (config == null)
        {
            return count;
        }

        final String channel = getConnectChannel();

        if (enableAll.isSelected())
        {
            final boolean workTwitchLoad = !config.isTwitchLoaded() && enableTwitch.isSelected();
            final boolean workTwitchCache = !config.isTwitchCached() && cacheTwitch.isSelected();
            final boolean workFfzLoad = !config.isFfzLoaded(channel) && enableFrankerFaceZ.isSelected();
            final boolean workFfzGlobalLoad = !config.isFfzGlobalLoaded() && enableFrankerFaceZ.isSelected();
            final boolean workFfzCache = !config.isFfzCached() && cacheFrankerFaceZ.isSelected();
            final boolean workBttvLoad = !config.isBttvLoaded(channel) && enableBetterTtv.isSelected();
            final boolean workBttvGlobalLoad = !config.isBttvGlobalLoaded() && enableBetterTtv.isSelected();
            final boolean workBttvCache = !config.isBttvCached() && cacheBetterTtv.isSelected();

            count += countBooleans(workTwitchLoad, workTwitchCache, workFfzLoad, workFfzGlobalLoad, workFfzCache, workBttvLoad, workBttvGlobalLoad, workBttvCache);
        }

        final boolean workTwitchBadges = enableTwitchBadges.isSelected() && !config.isTwitchBadgesLoaded(channel);
        final boolean workFfzBadges = enableFfzBadges.isSelected() && !config.isFfzBadgesLoaded(channel);

        count += countBooleans(workTwitchBadges, workFfzBadges);

        return count;
    }

    private static int countBooleans(boolean... bools)
    {
        int count = 0;
        for (int i = 0; i < bools.length; i++)
        {
            if (bools[i])
            {
                count++;
            }
        }
        return count;
    }

    /**
     * Parse through the selected UI options to determine what jobs need to be done. This will return an empty job list
     * if any of the jobs specified by the UI require a channel and no channel is provided on the Connection tab. A
     * popup will present this information to the user.
     * 
     * @return jobs
     */
    public Set<EmojiJob> collectJobs()
    {
        Set<EmojiJob> jobs = new HashSet<EmojiJob>();

        if (config == null)
        {
            return jobs;
        }

        final String channel = getConnectChannel();

        if (enableAll.isSelected())
        {
            final boolean workTwitchLoad = !config.isTwitchLoaded() && enableTwitch.isSelected();
            final boolean workTwitchCache = !config.isTwitchCached() && cacheTwitch.isSelected();
            final boolean workFfzLoad = !config.isFfzLoaded(channel) && enableFrankerFaceZ.isSelected();
            final boolean workFfzGlobalLoad = !config.isFfzGlobalLoaded() && enableFrankerFaceZ.isSelected();
            final boolean workFfzCache = !config.isFfzCached() && cacheFrankerFaceZ.isSelected();
            final boolean workBttvLoad = !config.isBttvLoaded(channel) && enableBetterTtv.isSelected();
            final boolean workBttvGlobalLoad = !config.isBttvGlobalLoaded() && enableBetterTtv.isSelected();
            final boolean workBttvCache = !config.isBttvCached() && cacheBetterTtv.isSelected();

            if (workTwitchLoad)
            {
                jobs.add(new EmojiJob(TWITCH_EMOTE_VERSION, EmojiOperation.LOAD, channel));
                if (channel == null)
                {
                    ChatWindow.popup.handleProblem("Please specify a channel on the Connection tab to load emoji");
                    jobs.clear();
                    return jobs;
                }
            }

            if (workTwitchCache)
            {
                jobs.add(new EmojiJob(TWITCH_EMOTE_VERSION, EmojiOperation.CACHE));
            }

            if (workFfzLoad)
            {
                jobs.add(new EmojiJob(EmojiType.FRANKERFACEZ_CHANNEL, EmojiOperation.LOAD, channel));
                if (channel == null)
                {
                    ChatWindow.popup.handleProblem("Please specify a channel on the Connection tab to load emoji");
                    jobs.clear();
                    return jobs;
                }
            }

            if (workFfzGlobalLoad)
            {
                jobs.add(new EmojiJob(EmojiType.FRANKERFACEZ_GLOBAL, EmojiOperation.LOAD));
            }

            if (workFfzCache)
            {
                jobs.add(new EmojiJob(EmojiType.FRANKERFACEZ_CHANNEL, EmojiOperation.CACHE));
                jobs.add(new EmojiJob(EmojiType.FRANKERFACEZ_GLOBAL, EmojiOperation.CACHE));
            }

            if (workBttvLoad)
            {
                jobs.add(new EmojiJob(EmojiType.BETTER_TTV_CHANNEL, EmojiOperation.LOAD, channel));
                if (channel == null)
                {
                    ChatWindow.popup.handleProblem("Please specify a channel on the Connection tab to load emoji");
                    jobs.clear();
                    return jobs;
                }
            }

            if (workBttvGlobalLoad)
            {
                jobs.add(new EmojiJob(EmojiType.BETTER_TTV_GLOBAL, EmojiOperation.LOAD));
            }

            if (workBttvCache)
            {
                jobs.add(new EmojiJob(EmojiType.BETTER_TTV_CHANNEL, EmojiOperation.CACHE));
                jobs.add(new EmojiJob(EmojiType.BETTER_TTV_GLOBAL, EmojiOperation.CACHE));
            }
        }

        final boolean workTwitchBadges = enableTwitchBadges.isSelected() && !config.isTwitchBadgesLoaded(channel);
        final boolean workFfzBadges = enableFfzBadges.isSelected() && !config.isFfzBadgesLoaded(channel);

        if (workTwitchBadges)
        {
            jobs.add(new EmojiJob(EmojiType.TWITCH_BADGE, EmojiOperation.LOAD, channel));
            if (channel == null)
            {
                ChatWindow.popup.handleProblem("Please specify a channel on the Connection tab to load badges");
                jobs.clear();
                return jobs;
            }
        }

        if (workFfzBadges)
        {
            jobs.add(new EmojiJob(EmojiType.FRANKERFACEZ_BADGE, EmojiOperation.LOAD, channel));
            if (channel == null)
            {
                ChatWindow.popup.handleProblem("Please specify a channel on the Connection tab to load badges");
                jobs.clear();
                return jobs;
            }
        }

        return jobs;
    }

    /**
     * Queue the work of a specified operation (load or cache) on a specified type of emote (Twitch or FrankerFaceZ).
     * Call runEmoteWork to run the loaded work in series.
     * 
     * @param types
     * @param ops
     */
    private void loadEmojiWork(Collection<EmojiJob> jobs)
    {
        for (EmojiJob job : jobs)
        {
            EmojiWorkerReport initialReport = new EmojiWorkerReport(job.toString(), 0, false, false);

            // A SwingWorkers can only be run once because... reasons. So each call to do work must be on a freshly
            // instantiated worker object.
            EmojiWorker worker = new EmojiWorker(chat.getEmojiManager(), progressPanel, job, logBox, initialReport);

            progressPanel.addWorkToQueue(worker);
        }
    }

    /**
     * Cancel
     * 
     * @param typesToCancel
     * @param opsToCancel
     */
    private void cancelEmojiWork(Collection<EmojiJob> jobs)
    {
        for (EmojiJob job : jobs)
        {
            progressPanel.removeWorkFromQueue(job);
        }
    }

    /**
     * To be called after loadEmoteWork has set up the tasks
     */
    private void runEmojiWork()
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
        this.enableTwitchBadges.setSelected(config.isTwitchBadgesEnabled());
        this.enableFfzBadges.setSelected(config.isFfzBadgesEnabled());
        this.emojiScaleToLineHeight.setSelected(config.isEmojiScaleToLine());
        this.badgeScaleToLineHeight.setSelected(config.isBadgeScaleToLine());
        this.emojiScale.setValue(config.getEmojiScale());
        this.badgeScale.setValue(config.getBadgeScale());
        this.emojiLoadingDisplayStrat.setSelectedItem(config.getDisplayStrategy());
        this.enableTwitch.setSelected(config.isTwitchEnabled());
        this.cacheTwitch.setSelected(config.isTwitchCacheEnabled());
        this.enableFrankerFaceZ.setSelected(config.isFfzEnabled());
        this.cacheFrankerFaceZ.setSelected(config.isFfzCacheEnabled());
        this.enableBetterTtv.setSelected(config.isBttvEnabled());
        this.cacheBetterTtv.setSelected(config.isBttvCacheEnabled());

        resolveEnables();
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
        config.setTwitchBadgesEnabled(enableTwitchBadges.isSelected());
        config.setFfzBadgesEnabled(enableFfzBadges.isSelected());
        config.setEmojiScaleToLine(emojiScaleToLineHeight.isSelected());
        config.setBadgeScaleToLine(badgeScaleToLineHeight.isSelected());
        config.setEmojiScale(emojiScale.getValue());
        config.setBadgeScale(badgeScale.getValue());
        config.setDisplayStrategy((EmojiLoadingDisplayStragegy) emojiLoadingDisplayStrat.getSelectedItem());
        config.setTwitchEnabled(enableTwitch.isSelected());
        config.setTwitchCacheEnabled(cacheTwitch.isSelected());
        config.setFfzEnabled(enableFrankerFaceZ.isSelected());
        config.setFfzCacheEnabled(cacheFrankerFaceZ.isSelected());
        config.setBttvEnabled(enableBetterTtv.isSelected());
        config.setBttvCacheEnabled(cacheBetterTtv.isSelected());
    }

}
