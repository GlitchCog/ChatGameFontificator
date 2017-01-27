package com.glitchcog.fontificator.gui.emoji;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.emoji.EmojiJob;
import com.glitchcog.fontificator.emoji.EmojiOperation;
import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelEmoji;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;

/**
 * A panel to display progress for loading and caching emoji that sits on the bottom of the Emoji tab of the Control
 * Window. It also contains a cancel button to stop jobs currently being worked and a manual reload button for canceling
 * everything and starting all loads from scratch. This panel contains methods for adding and removing work to and from
 * the worker queue as well.
 * 
 * @author Matt Yanos
 */
public class EmojiLoadProgressPanel extends JPanel
{
    private static final Logger logger = Logger.getLogger(EmojiLoadProgressPanel.class);

    private static final long serialVersionUID = 1L;

    /**
     * 4 spaces for the number of characters in "100%", the widest it will display
     */
    private static final String EMPTY_VALUE_TEXT = "    ";

    /**
     * Visually shows the progress
     */
    private JProgressBar bar;

    /**
     * Displays the percentage on the bar
     */
    private JLabel percentValue;

    /**
     * The cancel button for terminating the current work task
     */
    private JButton cancelButton;

    /**
     * Button for loading and caching everything. This is intended as a catch-all for any trouble encountered while
     * loading or caching emotes or badges.
     */
    private JButton manualButton;

    /**
     * The reset button for setting all emoji work (loads and caches) back to zero
     */
    private JButton resetButton;

    /**
     * A log box to document and display all the emote loading
     */
    private LogBox emojiLogBox;

    /**
     * A reference to the chat panel to repaint once loading is complete
     */
    private ChatPanel chat;

    /**
     * A reference to the emoji config object to set when things are loaded or cached or reset
     */
    private ConfigEmoji emojiConfig;

    /**
     * List of load tasks to be executed in series
     */
    private ConcurrentLinkedQueue<EmojiWorker> workerTaskListLoad;

    /**
     * List of cache tasks to be executed in series, after all the loading is done
     */
    private ConcurrentLinkedQueue<EmojiWorker> workerTaskListCache;

    /**
     * The cursor for the emoji worker task currently being run
     */
    private EmojiWorker currentWorker;

    private final ControlPanelEmoji emojiControlPanel;

    /**
     * Construct the emote loading/caching progress panel that sits on the bottom of the Emoji tab of the Control Window
     * 
     * @param chat
     *            The chat panel, for repainting after a load
     * @param emojiControlPanel
     *            Used by the reload button
     */
    public EmojiLoadProgressPanel(ChatPanel chatPanel, final ControlPanelEmoji emojiControlPanel)
    {
        this.chat = chatPanel;

        this.emojiControlPanel = emojiControlPanel;

        this.workerTaskListLoad = new ConcurrentLinkedQueue<EmojiWorker>();
        this.workerTaskListCache = new ConcurrentLinkedQueue<EmojiWorker>();
        this.currentWorker = null;
        this.emojiLogBox = new LogBox();

        this.bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        this.percentValue = new JLabel(EMPTY_VALUE_TEXT);
        this.percentValue.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setToolTipText("Cancel any emoji or badge loading or caching currently running or queued to run");
        this.manualButton = new JButton("Load/Cache");
        this.manualButton.setToolTipText("Manually load and or cache all emoji and badges");
        this.resetButton = new JButton("Reset");
        this.resetButton.setToolTipText("Reset all work done loading and or caching emoji and badges");
        handleButtonEnables();

        ActionListener bal = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();
                if (cancelButton.equals(source))
                {
                    if (currentWorker != null)
                    {
                        reset();
                    }
                }
                else if (resetButton.equals(source))
                {
                    workerTaskListLoad.clear();
                    workerTaskListCache.clear();
                    emojiConfig.resetWorkCompleted();
                    log("Reset all loaded and or cached emoji");
                    chat.repaint();
                }
                else if (manualButton.equals(source))
                {
                    reset();
                    emojiControlPanel.loadAndRunEmojiWork();
                }
                handleButtonEnables();
            }
        };

        this.cancelButton.addActionListener(bal);
        this.resetButton.addActionListener(bal);
        this.manualButton.addActionListener(bal);

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = ControlPanelBase.getGbc();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JPanel workPanel = new JPanel(new GridBagLayout());
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        workPanel.add(this.bar, gbc);
        gbc.gridx++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        workPanel.add(percentValue, gbc);
        gbc.gridx++;
        workPanel.add(this.cancelButton, gbc);
        gbc.gridx++;
        workPanel.add(this.manualButton, gbc);
        gbc.gridx++;
        workPanel.add(this.resetButton, gbc);

        gbc = ControlPanelBase.getGbc();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(emojiLogBox, gbc);
        gbc.gridy++;

        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(workPanel, gbc);
    }

    synchronized public boolean isCurrentlyRunning()
    {
        return currentWorker != null;
    }

    synchronized public void handleButtonEnables()
    {
        cancelButton.setEnabled(isCurrentlyRunning());
        resetButton.setEnabled(emojiConfig != null && emojiConfig.isAnyWorkDone());
        final int remainingJobs = emojiControlPanel.countJobs();
        manualButton.setEnabled(remainingJobs > 0 && currentWorker == null && workerTaskListLoad.isEmpty() && workerTaskListCache.isEmpty());

        // @formatter:off
        logger.trace("Cancel: " + cancelButton.isEnabled() + " b/c currWork " + (currentWorker == null ? "is null" : "is NOT null") + 
        "; Reset: " + resetButton.isEnabled() + " b/c " + (emojiConfig != null && emojiConfig.isAnyWorkDone() ? "some work is done" : "no work has been done") + 
        "; Manual: " + manualButton.isEnabled() + " b/c there " + (remainingJobs == 1 ? "is" : "are") + " " + remainingJobs + " job" + (remainingJobs == 1 ? "" : "s") + " left undone and " + 
        (currentWorker == null && workerTaskListLoad.isEmpty() && workerTaskListCache.isEmpty() ? "nothing" : "something") + " is currently running or is queued");
        // @formatter:on
    }

    /**
     * Must be called before using this progress panel
     * 
     * @param emojiConfig
     */
    public void setEmojiConfig(ConfigEmoji emojiConfig)
    {
        this.emojiConfig = emojiConfig;
    }

    /**
     * Takes a report and updates all the display components with the appropriate information
     * 
     * @param report
     */
    synchronized public void update(EmojiWorkerReport report)
    {
        if (report.isCanceled())
        {
            emojiLogBox.log(report.getMessage());
            blankAllValues();
            reset();
            handleButtonEnables();
        }
        else if (report.isError())
        {
            emojiLogBox.log(report.getMessage());
            blankAllValues();

            if (currentWorker != null)
            {
                currentWorker.cancel();
            }
            currentWorker = null;
            initiateNextWork();
        }
        else
        {
            bar.setValue(report.getPercentComplete());
            percentValue.setText(report.getPercentText());

            if (report.isComplete())
            {
                emojiLogBox.log(report.getMessage());
                if (currentWorker != null)
                {
                    emojiConfig.setWorkCompleted(currentWorker.getEmojiJob());
                }
                initiateNextWork();
            }
        }
        repaint();
    }

    /**
     * Reverts the panel to its ready state
     */
    synchronized private void reset()
    {
        if (currentWorker != null)
        {
            currentWorker.cancel();
        }
        cancelButton.setEnabled(false);
        blankAllValues();
        workerTaskListLoad.clear();
        workerTaskListCache.clear();
        currentWorker = null;
    }

    /**
     * Removes all labels and values from the display
     */
    synchronized private void blankAllValues()
    {
        percentValue.setText(EMPTY_VALUE_TEXT);
        bar.setValue(0);
    }

    /**
     * Call to add work
     * 
     * @param label
     * @param emojiWorker
     * @param initialReport
     */
    synchronized public void addWorkToQueue(EmojiWorker emojiWorker)
    {
        ConcurrentLinkedQueue<EmojiWorker> taskList = getTaskList(emojiWorker.getEmojiJob());
        for (EmojiWorker worker : taskList)
        {
            if (!worker.isCancelled() && worker.getEmojiJob().equals(emojiWorker.getEmojiJob()))
            {
                // Already exists in a non-canceled way, so don't add it
                return;
            }
        }
        taskList.add(emojiWorker);
    }

    /**
     * Call to initiate work
     */
    synchronized public void initiateWork()
    {
        ConcurrentLinkedQueue<EmojiWorker> taskList = getTaskList();
        if (!taskList.isEmpty())
        {
            currentWorker = taskList.poll();
            setLocation(getParent().getLocation().x + (getParent().getWidth() - getWidth()) / 2, getParent().getLocation().y + (getParent().getHeight() - getHeight()) / 2);
            cancelButton.setEnabled(true);
            update(currentWorker.getInitialReport());
            currentWorker.execute();
        }
    }

    /**
     * Kick off the next worker when the current worker is done
     */
    synchronized private void initiateNextWork()
    {
        this.percentValue.setText(EMPTY_VALUE_TEXT);

        ConcurrentLinkedQueue<EmojiWorker> taskList = getTaskList();
        this.currentWorker = taskList.poll();

        chat.repaint();
        if (currentWorker == null)
        {
            reset();
            handleButtonEnables();
        }
        else
        {
            currentWorker.execute();
        }
    }

    /**
     * Take any worker containing a job that matches the specified job off the queue of workers to be executed. Also
     * cancels the currently running worker if it matches.
     * 
     * @param job
     */
    synchronized public void removeWorkFromQueue(EmojiJob job)
    {
        if (currentWorker != null)
        {
            if (jobMatch(job, currentWorker))
            {
                logger.trace("Canceling " + job.toString());
                currentWorker.haltCurrentJob();
            }
        }

        ConcurrentLinkedQueue<EmojiWorker> taskList = getTaskList(job);
        for (EmojiWorker worker : taskList)
        {
            if (jobMatch(job, worker))
            {
                logger.trace("Canceling " + job.toString());
                worker.haltCurrentJob();
                // Is this thread safe?
                workerTaskListLoad.remove(worker);
            }
        }
    }

    /**
     * Get a task list (load or cache) based on job op type
     * 
     * @param job
     * @return task list
     */
    synchronized private ConcurrentLinkedQueue<EmojiWorker> getTaskList(EmojiJob job)
    {
        return job.getOp() == EmojiOperation.CACHE ? workerTaskListCache : workerTaskListLoad;
    }

    /**
     * Get a task list (load or cache) based on whether the load list is empty
     * 
     * @return task list
     */
    synchronized private ConcurrentLinkedQueue<EmojiWorker> getTaskList()
    {
        return workerTaskListLoad.isEmpty() ? workerTaskListCache : workerTaskListLoad;
    }

    synchronized private boolean jobMatch(EmojiJob job, EmojiWorker worker)
    {
        return worker != null && worker.getEmojiJob() != null && !worker.isDone() && !worker.isCancelled() && worker.getEmojiJob().equals(job);
    }

    public void log(String msg)
    {
        emojiLogBox.log(msg);
    }
}