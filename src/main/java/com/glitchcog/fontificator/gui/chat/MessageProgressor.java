package com.glitchcog.fontificator.gui.chat;

import java.util.Timer;
import java.util.TimerTask;

import com.glitchcog.fontificator.bot.Message;

/**
 * Wraps a Timer and TimerTask object and stores a link to the ChatPanel. Does not extend TimerTask because TimerTask
 * needs to be reconstructed with each speed change. Also sets a shutdown hook to make sure the timer thread is killed
 * on exit
 * 
 * @author Matt Yanos
 */
public class MessageProgressor
{
    private ChatPanel chat;

    private Timer clock;

    private TimerTask task;

    private boolean somethingBeingDrawn;

    public MessageProgressor(ChatPanel chat)
    {
        this.chat = chat;

        this.somethingBeingDrawn = false;

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
     * Get whether something is in the process of being drawn
     * 
     * @return somethingBeingDrawn
     */
    public boolean isSomethingBeingDrawn()
    {
        return somethingBeingDrawn;
    }

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

    public void refreshTimer(long messageDelay)
    {
        if (task != null)
        {
            task.cancel();
        }

        task = new TimerTask()
        {
            @Override
            public void run()
            {
                somethingBeingDrawn = false;

                Message[] messageArray = chat.getMessages();

                for (int i = 0; i < messageArray.length; i++)
                {
                    if (messageArray[i].isCompletelyDrawn())
                    {
                        continue;
                    }
                    else
                    {
                        messageArray[i].incrementDrawCursor(chat.getMessageConfig());
                        somethingBeingDrawn = true;
                        chat.repaint();
                        break;
                    }
                }

                if (!somethingBeingDrawn)
                {
                    cancel();
                }
            }
        };

        clock = new Timer();

        clock.schedule(task, 0L, messageDelay);
    }

    public void startMessageClock(long messageDelay)
    {
        if (!somethingBeingDrawn)
        {
            refreshTimer(messageDelay);
        }
    }

}
