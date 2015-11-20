package com.glitchcog.fontificator.gui.emoji;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;

/**
 * A panel to display progress for loading and caching emoji
 * 
 * @author Matt Yanos
 */
public class EmojiLoadProgressPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    /**
     * 4 spaces for the number of characters in "100%", the widest it will display
     */
    private static final String EMPTY_VALUE_TEXT = "    ";

    /**
     * Describes what is being progressed
     */
    private JLabel label;

    /**
     * Visually shows the progress
     */
    private JProgressBar bar;

    /**
     * Displays the percentage on the bar
     */
    private JLabel value;

    /**
     * The cancel button for terminating the current work task
     */
    private JButton cancelButton;

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
     * List of tasks to be executed in series
     */
    private ConcurrentLinkedQueue<EmojiWorker> workerTaskList;

    /**
     * The cursor for the emoji worker task currently being run
     */
    private EmojiWorker currentWorker;

    public EmojiLoadProgressPanel(ChatPanel chat)
    {
        this.chat = chat;

        this.workerTaskList = new ConcurrentLinkedQueue<EmojiWorker>();
        this.currentWorker = null;
        this.emojiLogBox = new LogBox();

        this.label = new JLabel("");
        this.bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        this.value = new JLabel(EMPTY_VALUE_TEXT);
        this.value.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setEnabled(false);

        this.cancelButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (currentWorker != null)
                {
                    emojiLogBox.log("Canceled");
                    reset();
                }
            }
        });

        setLayout(new GridBagLayout());

        JPanel workPanel = new JPanel(new GridLayout(2, 1));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = ControlPanelBase.getGbc();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        top.add(label, gbc);
        gbc.gridx++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        top.add(value, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JPanel bottom = new JPanel(new GridBagLayout());
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        bottom.add(this.bar, gbc);
        gbc.gridx++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        bottom.add(this.cancelButton, gbc);

        workPanel.add(top);
        workPanel.add(bottom);

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

    /**
     * Must be called before using this progress panel
     * 
     * @param emojiConfig
     */
    public void setEmojiConfig(ConfigEmoji emojiConfig)
    {
        this.emojiConfig = emojiConfig;
    }

    public void update(EmojiWorkerReport report)
    {
        label.setText(report.getMessage());
        bar.setValue(report.getPercentComplete());
        value.setText(report.getPercentText());
        if (report.isComplete())
        {
            emojiLogBox.log(report.getMessage());
            if (currentWorker != null)
            {
                emojiConfig.setWorkCompleted(currentWorker.getEmojiType(), currentWorker.getEmojiOp(), currentWorker.getChannel());
            }
            initiateNextWork();
        }
        repaint();
    }

    private void reset()
    {
        if (currentWorker != null)
        {
            currentWorker.cancel();
        }
        label.setText("");
        bar.setValue(0);
        cancelButton.setEnabled(false);
        workerTaskList.clear();
    }

    /**
     * Call to initialize work
     * 
     * @param label
     * @param emojiWorker
     * @param initialReport
     */
    public void addWorkToQueue(EmojiWorker emojiWorker)
    {
        workerTaskList.add(emojiWorker);
    }

    public void initiateWork()
    {
        if (!workerTaskList.isEmpty())
        {
            currentWorker = workerTaskList.poll();
            emojiLogBox.log(currentWorker.getInitialReport().getMessage());
            setLocation(getParent().getLocation().x + (getParent().getWidth() - getWidth()) / 2, getParent().getLocation().y + (getParent().getHeight() - getHeight()) / 2);
            cancelButton.setEnabled(true);
            update(currentWorker.getInitialReport());
            currentWorker.execute();
        }
    }

    /**
     * Reset and optionally repaint the chat window, and finally execute the next worker in the queue
     */
    private void initiateNextWork()
    {
        this.value.setText(EMPTY_VALUE_TEXT);
        this.currentWorker = workerTaskList.poll();
        chat.repaint();
        if (currentWorker == null)
        {
            reset();
        }
        else
        {
            currentWorker.execute();
        }
    }
}