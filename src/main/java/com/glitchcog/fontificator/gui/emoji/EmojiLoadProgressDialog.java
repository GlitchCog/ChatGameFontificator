package com.glitchcog.fontificator.gui.emoji;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;

/**
 * A dialog to display progress for loading and caching emoji
 * 
 * @author Matt Yanos
 */
public class EmojiLoadProgressDialog extends JDialog
{
    private static final long serialVersionUID = 1L;

    /**
     * Text for when the button is an okay button, after the work is completed
     */
    private static final String OK_BUTTON_TEXT = "OK";

    /**
     * Text for when the button is a cancel button, while the work is being done
     */
    private static final String CANCEL_BUTTON_TEXT = "Cancel";

    /**
     * 4 spaces for the number of characters in "100%", the widest it will display
     */
    private static final String EMPTY_VALUE_TEXT = "    ";

    /**
     * Reference to the worker, so the message to cancel can be sent on the button press
     */
    private EmojiWorker emojiWorker;

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
     * The button, both cancel and okay
     */
    private JButton button;

    /**
     * A reference to the chat panel to repaint once loading is complete
     */
    private ChatPanel chat;

    public EmojiLoadProgressDialog(ChatPanel chat)
    {
        super(ControlWindow.me, true);

        this.chat = chat;

        setTitle("Emote Progress");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        this.label = new JLabel("Progress");
        this.bar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        this.value = new JLabel(EMPTY_VALUE_TEXT);
        this.value.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        this.button = new JButton("Cancel");

        this.button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();
                if (CANCEL_BUTTON_TEXT.equals(source.getText()))
                {
                    emojiWorker.cancel();
                }
                hideDialog();
            }
        });

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = ControlPanelBase.getGbc();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        top.add(label, gbc);
        gbc.gridx++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        top.add(value, gbc);

        JPanel mid = new JPanel(new GridLayout(1, 1));
        mid.add(this.bar, gbc);

        JPanel bot = new JPanel();
        bot.add(this.button);

        JPanel all = new JPanel(new GridBagLayout());
        gbc = ControlPanelBase.getGbc();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        all.add(top, gbc);
        gbc.gridy++;
        gbc.weighty = 0.0;
        all.add(mid, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        all.add(bot, gbc);
        gbc.gridy++;

        add(all);

        pack();
        setSize(312, getHeight());
        setResizable(false);
    }

    public void update(EmojiWorkerReport report)
    {
        label.setText(report.getMessage());
        bar.setValue(report.getPercentComplete());
        value.setText(report.getPercentText());
        if (report.isComplete())
        {
            button.setText(OK_BUTTON_TEXT);
        }
        repaint();
    }

    public void showDialog(String title, EmojiWorker emojiWorker, EmojiWorkerReport initialReport)
    {
        reset();
        setLocation(getParent().getLocation().x + (getParent().getWidth() - getWidth()) / 2, getParent().getLocation().y + (getParent().getHeight() - getHeight()) / 2);
        setTitle(title);
        this.emojiWorker = emojiWorker;
        update(initialReport);
        setVisible(true);
    }

    public void hideDialog()
    {
        reset();
        setVisible(false);
        chat.repaint();
    }

    private void reset()
    {
        this.label.setText("");
        this.bar.setValue(0);
        this.value.setText(EMPTY_VALUE_TEXT);
        this.button.setText(CANCEL_BUTTON_TEXT);
        this.emojiWorker = null;
        button.setText("Cancel");
    }
}