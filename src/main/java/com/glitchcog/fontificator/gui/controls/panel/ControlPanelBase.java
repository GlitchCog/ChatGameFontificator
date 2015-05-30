package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.chat.ChatWindow;

/**
 * The base control panel for all the components that get added to the
 * ControlWindow
 * 
 * @author Matt Yanos
 */
public abstract class ControlPanelBase extends JPanel
{
    private static final long serialVersionUID = 1L;

    protected String label;

    protected final static Insets DEFAULT_INSETS = new Insets(2, 4, 2, 4);
    protected final static Insets NO_INSETS = new Insets(0, 0, 0, 0);

    protected final ChatWindow chatWindow;

    protected final ChatPanel chat;

    protected GridBagConstraints gbc;

    protected FontificatorProperties fProps;

    protected Border baseBorder;

    /**
     * Sets the grid bag constraints layout, calls build(), and calls fill()
     */
    public ControlPanelBase(String label, FontificatorProperties fProps, ChatWindow chatWindow)
    {
        this.fProps = fProps;
        this.label = label;
        this.chatWindow = chatWindow;
        this.chat = chatWindow.getChatPanel();
        setLayout(new GridBagLayout());
        this.gbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0);
        this.baseBorder = BorderFactory.createLineBorder(Color.GRAY);
        build();
        fillInputFromProperties(fProps);
    }

    public String getLabel()
    {
        return label;
    }

    /**
     * Get the config file that holds all the configuration for this control
     * panel
     * 
     * @return fProps
     */
    public FontificatorProperties getProperties()
    {
        return fProps;
    }

    /**
     * Builds the panel by constructing and adding all the components. Called by
     * the base constructor.
     */
    protected abstract void build();

    /**
     * Sets the config from the properties file, then calls fillUiFromConfig
     */
    protected abstract void fillInputFromProperties(FontificatorProperties fProps);

    /**
     * Fills all the input fields with the configuration. Called by the base
     * constructor. Typically fill is where the specialized config for the
     * extension of this base class should be set if it is to be kept as a
     * member variable
     */
    protected abstract void fillInputFromConfig();

    /**
     * Validates UI input and updates config objects
     */
    public void update()
    {
        List<String> errors = validateInput();
        if (errors.isEmpty())
        {
            try
            {
                fillConfigFromInput();
                chat.repaint();
            }
            catch (Exception ex)
            {
                ChatWindow.popup.handleProblem("Unexpected Error: " + ex.toString(), ex);
            }
        }
        else
        {
            ChatWindow.popup.handleProblem(errors);
        }
    }

    /**
     * Must return an error if any input field value is invalid for storing on
     * the config object or in the final properties destination should it be
     * saved
     * 
     * @return errors
     */
    protected abstract List<String> validateInput();

    /**
     * Takes user data and parses it into config file objects. Because no errors
     * were returned by validateInput when this is run, it should be safe from
     * Exceptions
     */
    protected abstract void fillConfigFromInput() throws Exception;

}
