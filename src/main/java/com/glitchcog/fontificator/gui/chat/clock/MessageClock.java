package com.glitchcog.fontificator.gui.chat.clock;

import java.util.Timer;
import java.util.TimerTask;

import com.glitchcog.fontificator.gui.chat.ChatPanel;

/**
 * A stored reference to the chatPanek, a timer and its associated task related to doing something with messages. Also
 * sets a shutdown hook to make sure the timer thread is killed on exit
 * 
 * @author Matt Yanos
 */
public abstract class MessageClock
{
    protected ChatPanel chat;

    protected Timer clock;

    protected TimerTask task;

    protected boolean active;

    public MessageClock(ChatPanel chat)
    {
        this.chat = chat;
        this.active = false;

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                cancelLatest();
            }
        });
    }

    /**
     * Instantiate a new task to run
     */
    public abstract void initTask();

    /**
     * Kicks off the timer with a given delay
     * 
     * @param messageDelay
     */
    public abstract void startClock(long messageDelay);

    /**
     * Shutdown the latest instantiation of the timer task, used during shutdown via the shutdown hook
     */
    public void cancelLatest()
    {
        if (task != null)
        {
            task.cancel();
        }
    }

}
