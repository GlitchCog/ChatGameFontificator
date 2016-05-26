
package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.GridBagConstraints;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.DebugAppender;
import com.glitchcog.fontificator.gui.chat.ChatWindow;

/**
 * Control Panel containing debugging options and information
 * 
 * @author Matt Yanos
 */
public class ControlPanelDebug extends ControlPanelBase
{
    private static final long serialVersionUID = 1L;

    private DebugAppender debugAppender;

    /**
     * Construct an IRC (Connection) control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param bot
     * @param logBox
     */
    public ControlPanelDebug(FontificatorProperties fProps, ChatWindow chatWindow)
    {
        super("Debug", fProps, chatWindow, new LogBox());
        debugAppender = new DebugAppender(logBox);
    }

    @Override
    protected void build()
    {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.BOTH;
        add(logBox, gbc);
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        return new LoadConfigReport();
    }

    @Override
    public void fillConfigFromInput() throws Exception
    {
    }

    /**
     * Enable or disable debugging
     * 
     * @param debugging
     */
    public void setDebugging(boolean debugging)
    {
        if (debugging)
        {
            Thread.setDefaultUncaughtExceptionHandler(debugAppender);
            BasicConfigurator.configure(debugAppender);
        }
        else
        {
            Thread.setDefaultUncaughtExceptionHandler(null);
            Logger.getRootLogger().removeAppender(debugAppender);
        }
    }
}
