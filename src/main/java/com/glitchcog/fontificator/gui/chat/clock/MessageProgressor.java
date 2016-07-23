package com.glitchcog.fontificator.gui.chat.clock;

import java.util.Timer;
import java.util.TimerTask;

import com.glitchcog.fontificator.bot.Message;
import com.glitchcog.fontificator.gui.chat.ChatPanel;

/**
 * Timer to progress drawing messages one character at a time
 * 
 * @author Matt Yanos
 */
public class MessageProgressor extends MessageClock
{
    public MessageProgressor(ChatPanel chat)
    {
        super(chat);
    }

    /**
     * Get whether something is in the process of being drawn
     * 
     * @return somethingBeingDrawn
     */
    public boolean isSomethingBeingDrawn()
    {
        return active;
    }

    @Override
    public void startClock(long messageDelay)
    {
        if (!active)
        {
            refreshTimer(messageDelay);
        }
    }

    @Override
    public void initTask()
    {
        super.task = new TimerTask()
        {
            @Override
            public void run()
            {
                active = false;

                Message[] messageArray = chat.getMessages();

                for (int i = 0; i < messageArray.length; i++)
                {
                    if (messageArray[i].isCompletelyDrawn() || messageArray[i].isCensored())
                    {
                        continue;
                    }
                    else
                    {
                        messageArray[i].incrementDrawCursor(chat.getEmojiManager(), chat.getMessageConfig(), chat.getEmojiConfig());
                        active = true;
                        chat.repaint();
                        break;
                    }
                }

                if (!active)
                {
                    cancel();
                }
            }
        };
    }

    public void refreshTimer(long messageDelay)
    {
        cancelLatest();
        initTask();
        clock = new Timer();
        clock.schedule(task, 0L, messageDelay);
    }

}
