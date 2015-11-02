package com.glitchcog.fontificator.gui.controls.messages;

import javax.swing.table.DefaultTableModel;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.gui.chat.ChatPanel;

/**
 * @author Matt Yanos
 */
public class MessageCheckListModel extends DefaultTableModel
{
    private static final long serialVersionUID = 1L;

    private static final int TOGGLE_COLUMN_INDEX = 4;

    private static final String[] HEADER = new String[] { "Timestamp", "User", "Message", "Offense", "Censored" };

    private ChatPanel chat;

    private MessageCheckList list;

    public MessageCheckListModel(ChatPanel chat, MessageCheckList list)
    {
        this.chat = chat;
        this.list = list;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return columnIndex == TOGGLE_COLUMN_INDEX ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return columnIndex == TOGGLE_COLUMN_INDEX;
    }

    @Override
    public int getRowCount()
    {
        return chat == null ? 0 : chat.getMessageQueue().size();
    }

    @Override
    public int getColumnCount()
    {
        return HEADER.length;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        return HEADER[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Message[] messages = getMessages();
        Message msg = messages[messages.length - rowIndex - 1];
        if (columnIndex == 0)
        {
            return msg.getTimestamp();
        }
        else if (columnIndex == 1)
        {
            return msg.getUsername();
        }
        else if (columnIndex == 2)
        {
            return msg.getContent();
        }
        else if (columnIndex == 3)
        {
            return msg.getCensoredReason();
        }
        else if (columnIndex == 4)
        {
            return msg.isCensored();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        if (columnIndex == TOGGLE_COLUMN_INDEX)
        {
            Message[] messages = getMessages();
            Message msg = messages[messages.length - rowIndex - 1];
            msg.setCensored((Boolean)(aValue == null ? false : aValue));
            msg.setManualCensorship(true);
            list.revalidateTable();
            chat.repaint();
        }
    }

    private Message[] getMessages()
    {
        return chat.getMessageQueue().toArray(new Message[chat.getMessageQueue().size()]);
    }
}
