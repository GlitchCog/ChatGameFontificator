package com.glitchcog.fontificator.gui.controls.panel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.ChatViewerBot;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.controls.ControlWindow;

/**
 * The main component that contains all the specialized control panels
 * 
 * @author Matt Yanos
 */
public class ControlTabs extends JTabbedPane
{
    private static final Logger logger = Logger.getLogger(ControlTabs.class);

    private static final long serialVersionUID = 1L;

    private ChatViewerBot bot;

    private FontificatorProperties fProps;

    private ControlPanelBase[] subpanels;

    private ControlPanelIrc ircPanel;

    private ControlPanelChat chatPanel;

    private ControlPanelFont fontPanel;

    private ControlPanelColor colorPanel;

    private ControlPanelMessage messagePanel;

    public ControlTabs(FontificatorProperties fProps, ChatViewerBot bot)
    {
        super(TOP, SCROLL_TAB_LAYOUT);
        this.fProps = fProps;
        this.bot = bot;
    }

    public void build(ChatWindow chatWindow, ControlWindow ctrlWindow)
    {
        logger.trace("Building ControlMainPanel");

        ircPanel = new ControlPanelIrc(fProps, chatWindow, bot);
        chatPanel = new ControlPanelChat(fProps, chatWindow, ctrlWindow);
        fontPanel = new ControlPanelFont(fProps, chatWindow);
        messagePanel = new ControlPanelMessage(fProps, chatWindow, bot);
        colorPanel = new ControlPanelColor(fProps, chatWindow);

        subpanels = new ControlPanelBase[5];

        subpanels[0] = ircPanel;
        subpanels[1] = chatPanel;
        subpanels[2] = fontPanel;
        subpanels[3] = messagePanel;
        subpanels[4] = colorPanel;

        for (int i = 0; i < subpanels.length; i++)
        {
            addTab(subpanels[i].getLabel(), subpanels[i]);
        }
    }

    public void refreshUiFromConfig(FontificatorProperties fProps)
    {
        for (int i = 0; i < subpanels.length; i++)
        {
            subpanels[i].fillInputFromConfig();
        }
    }

    public boolean refreshConfigFromUi()
    {
        List<String> errors = new ArrayList<String>();
        for (int i = 0; i < subpanels.length; i++)
        {
            errors.addAll(subpanels[i].validateInput());
        }

        if (errors.isEmpty())
        {
            for (int i = 0; i < subpanels.length; i++)
            {
                try
                {
                    subpanels[i].fillConfigFromInput();
                }
                catch (Exception e)
                {
                    errors.add("Excepton thrown trying to interpret input on the " + subpanels[i].getLabel() + " tab");
                }
            }
        }

        if (!errors.isEmpty())
        {
            ChatWindow.popup.handleProblem(errors);
        }

        return errors.isEmpty();
    }

    public void setAlwaysOnTopConfig(boolean alwaysOnTop)
    {
        chatPanel.setAlwaysOnTop(alwaysOnTop);
    }

}
