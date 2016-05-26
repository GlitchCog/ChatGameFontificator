package com.glitchcog.fontificator.gui.controls.panel;

import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.ChatViewerBot;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
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

    /**
     * The bot that reads from the IRC channel and relays the messages to the chat view
     */
    private ChatViewerBot bot;

    /**
     * The reference to the properties to be passed into each control panel
     */
    private FontificatorProperties fProps;

    /**
     * An array containing each of the control panels
     */
    private ControlPanelBase[] subpanels;

    /**
     * A reference to the connection control panel, containing the specifications for connecting to the channel
     */
    private ControlPanelIrc ircPanel;

    /**
     * A reference to the chat control panel, containing all the chat window options
     */
    private ControlPanelChat chatPanel;

    /**
     * A reference to the font/border control panel, containing all the options for specifying the font and border
     */
    private ControlPanelFont fontPanel;

    /**
     * A reference to the color control panel, containing all the options for specifying the colors used in the chat
     * view
     */
    private ControlPanelColor colorPanel;

    /**
     * A reference to the message control panel, containing all the options for formatting the chat messages
     */
    private ControlPanelMessage messagePanel;

    /**
     * A reference to the emoji control panel, containing all the options for whether to and how to display emoji in the
     * chat messages
     */
    private ControlPanelEmoji emojiPanel;

    /**
     * A reference to the debug control panel, hidden until selected from the Help menu option to enter debug mode,
     * containing all the options for debugging
     */
    private ControlPanelDebug debugPanel;

    /**
     * A reference to the message censorship control panel. This panel is not in a tab, but is accessible via the
     * Message menu item. Nevertheless, it must be updated from the config objects in the same way as the tabbed control
     * panels, so it is managed here in this tabs object.
     */
    private MessageCensorPanel censorPanel;

    private LogBox logBox;

    /**
     * Construct a ControlTabs object to create all the control panel tabs
     * 
     * @param fProps
     * @param bot
     */
    public ControlTabs(FontificatorProperties fProps, ChatViewerBot bot, MessageCensorPanel censorPanel, LogBox logBox)
    {
        super(TOP, SCROLL_TAB_LAYOUT);
        this.fProps = fProps;
        this.bot = bot;
        this.censorPanel = censorPanel;
        this.logBox = logBox;
    }

    public void build(ChatWindow chatWindow, ControlWindow ctrlWindow)
    {
        logger.trace("Building ControlMainPanel");

        emojiPanel = new ControlPanelEmoji(fProps, chatWindow, bot, logBox);
        ircPanel = new ControlPanelIrc(fProps, chatWindow, emojiPanel, bot, logBox);
        logBox.setAuthCode(fProps.getIrcConfig().getAuthorization());
        chatPanel = new ControlPanelChat(fProps, chatWindow, ctrlWindow, logBox);
        fontPanel = new ControlPanelFont(fProps, chatWindow, logBox);
        messagePanel = new ControlPanelMessage(fProps, chatWindow, bot, logBox);
        colorPanel = new ControlPanelColor(fProps, chatWindow, logBox);
        debugPanel = new ControlPanelDebug(fProps, chatWindow);

        subpanels = new ControlPanelBase[6];

        subpanels[0] = ircPanel;
        subpanels[1] = chatPanel;
        subpanels[2] = fontPanel;
        subpanels[3] = messagePanel;
        subpanels[4] = colorPanel;
        subpanels[5] = emojiPanel;

        for (int i = 0; i < subpanels.length; i++)
        {
            addControlTab(subpanels[i]);
        }
    }

    public void refreshUiFromConfig(FontificatorProperties fProps)
    {
        for (int i = 0; i < subpanels.length; i++)
        {
            subpanels[i].fillInputFromConfig();
        }
        censorPanel.fillInputFromConfig();
    }

    public boolean refreshConfigFromUi()
    {
        LoadConfigReport report = new LoadConfigReport();
        for (int i = 0; i < subpanels.length; i++)
        {
            report.addFromReport(subpanels[i].validateInput());
        }

        if (report.isErrorFree())
        {
            for (int i = 0; i < subpanels.length; i++)
            {
                try
                {
                    subpanels[i].fillConfigFromInput();
                }
                catch (Exception e)
                {
                    report.addError("Excepton thrown trying to interpret input on the " + subpanels[i].getLabel() + " tab", LoadConfigErrorType.UNKNOWN_ERROR);
                }
            }
        }

        if (!report.isErrorFree())
        {
            ChatWindow.popup.handleProblem(report);
        }

        return report.isErrorFree();
    }

    public void setAlwaysOnTopConfig(boolean alwaysOnTop)
    {
        chatPanel.setAlwaysOnTop(alwaysOnTop);
    }

    public void setRememberChatWindowPosition(boolean rememberPosition)
    {
        chatPanel.setRememberPosition(rememberPosition);
    }

    public void setChatWindowPosition()
    {
        chatPanel.setRememberedPosition();
    }

    public void setAntiAlias(boolean antiAlias)
    {
        chatPanel.setAntiAlias(antiAlias);
    }

    public void toggleDebugTab()
    {
        int debugTabIndex = indexOfComponent(debugPanel);
        if (debugTabIndex < 0)
        {
            addControlTab(debugPanel);
            debugPanel.setDebugging(true);
        }
        else
        {
            removeTabAt(debugTabIndex);
            debugPanel.setDebugging(false);
        }
    }

    /**
     * Adds a control panel tab to the control tabs, using its label as the tab name
     * 
     * @param cpTab tab to add
     */
    private void addControlTab(ControlPanelBase cpTab)
    {
        addTab(cpTab.getLabel(), cpTab);
    }
}
