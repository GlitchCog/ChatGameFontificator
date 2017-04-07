package com.glitchcog.fontificator.gui.controls.messages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;
import com.glitchcog.fontificator.gui.controls.panel.MessageCensorPanel;

/**
 * GUI component to manually censor or permit specific messages on the queue
 * 
 * @author Matt Yanos
 */
public class MessageCheckList extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JTable messageTable;

    private TableModel model;

    private JButton undoPurge;

    private JButton undoManualCensorship;

    public MessageCheckList(ChatPanel chat, final MessageCensorPanel censorPanel)
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = ControlPanelBase.getGbc();

        undoPurge = new JButton("Undo Purge");
        undoPurge.setToolTipText("Adds purged messages from a Twitch ban or timeout back into chat");

        undoPurge.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                censorPanel.undoPurge();
            }
        });

        undoManualCensorship = new JButton("Reapply Rules");
        undoManualCensorship.setToolTipText("Undoes any manual censoring or uncensoring done with the checkboxes");
        undoManualCensorship.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                censorPanel.recheckCensorship(true);
            }
        });

        model = new MessageCheckListModel(chat, this);
        messageTable = new JTable(model);
        messageTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        add(new JLabel("Manually toggle message censorship:"), gbc);
        gbc.gridx++;
        gbc.weightx = 0.0;
        add(undoManualCensorship, gbc);
        gbc.gridx++;
        add(undoPurge, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        add(new JScrollPane(messageTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), gbc);
        gbc.gridy++;
    }

    public void addMessage(Message msg)
    {
        int row = 0;
        model.setValueAt(msg.getTimestamp(), row, 1);
        model.setValueAt(msg.getUsername(), row, 2);
        model.setValueAt(msg.getContent(), row, 3);
        model.setValueAt("", row, 4);
        model.setValueAt(Boolean.FALSE, row, 5);
    }

    public void revalidateTable()
    {
        messageTable.revalidate();
        messageTable.repaint();
    }

}
