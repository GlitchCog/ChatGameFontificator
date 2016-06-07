package com.glitchcog.fontificator.gui.chat.clock;

import java.util.Timer;
import java.util.TimerTask;

import com.glitchcog.fontificator.gui.chat.ChatPanel;

/**
 * Timer to continually repaint the chat in case a message expiration time is set
 * 
 * @author Matt Yanos
 */
public class MessageExpirer extends MessageClock
{
    public MessageExpirer(ChatPanel chat)
    {
        super(chat);
    }

    public void startClock()
    {
        startClock(100L);
    }

    public void startClock(long messageDelay)
    {
        cancelLatest();
        initTask();
        clock = new Timer();
        clock.schedule(task, 0L, messageDelay);
    }

    @Override
    public void initTask()
    {
        super.task = new TimerTask()
        {
            @Override
            public void run()
            {
                if (chat != null)
                {
                    chat.repaint();
                }
            }
        };
    }

}
