package com.glitchcog.fontificator.gui.chat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.gui.FontificatorError;
import com.glitchcog.fontificator.gui.controls.ControlWindow;

/**
 * This window displays the chat. It is intended to be minimalistic and solely display the chat.
 * 
 * @author Matt Yanos
 */
public class ChatWindow extends JFrame
{
    private static final long serialVersionUID = 1L;

    /**
     * A stored reference to the chat panel, used whenever some part of the system has a reference to this ChatWindow,
     * but needs to modify something on the panel. This is in lieu of putting a bunch of accessor methods here to do
     * pass through the function to the panel in an encapsulated way.
     */
    private ChatPanel chatPanel;

    /**
     * A static copy of this ChatWindow for accessing it globally
     */
    public static ChatWindow me;

    /**
     * The popup for submitting errors that the user needs to see
     */
    public static FontificatorError popup;

    /**
     * Mouse listeners for dragging the Chat Window around when dragging the mouse inside the chat
     */
    private ChatMouseListeners mouseListeners;

    /**
     * Escape stroke to close popups
     */
    private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    /**
     * Construct the Chat Window
     */
    public ChatWindow()
    {
        me = this;
        setTitle("Fontificator Chat");
        popup = new FontificatorError(null);
    }

    /**
     * Sets the properties to get hooks into the properties' configuration models; Sets the ControlWindow to get hooks
     * back into the controls; Sets the loaded member Boolean to indicate it has everything it needs to begin rendering
     * the visualization
     * 
     * @param fProps
     * @param ctrlWindow
     * @throws IOException
     */
    public void initChat(final FontificatorProperties fProps, final ControlWindow ctrlWindow) throws IOException
    {
        chatPanel = new ChatPanel();
        add(chatPanel);

        mouseListeners = new ChatMouseListeners(this, ctrlWindow);
        addMouseListener(mouseListeners);
        addMouseMotionListener(mouseListeners);
        addMouseWheelListener(chatPanel);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(fProps.getChatConfig().getWidth(), fProps.getChatConfig().getHeight());
        setResizable(fProps.getChatConfig().isResizable());
        setAlwaysOnTop(fProps.getChatConfig().isAlwaysOnTop());

        chatPanel.setConfig(fProps);

        addWindowListener(new WindowListener()
        {
            @Override
            public void windowOpened(WindowEvent e)
            {
            }

            @Override
            public void windowClosing(WindowEvent e)
            {
                callExit(e.getComponent());
            }

            @Override
            public void windowClosed(WindowEvent e)
            {
            }

            @Override
            public void windowIconified(WindowEvent e)
            {
            }

            @Override
            public void windowDeiconified(WindowEvent e)
            {
            }

            @Override
            public void windowActivated(WindowEvent e)
            {
            }

            @Override
            public void windowDeactivated(WindowEvent e)
            {
            }

            /**
             * Calls exit from the control window
             */
            private void callExit(Component caller)
            {
                ctrlWindow.attemptToExit(caller);
            }
        });

    }

    /**
     * Does the work required to make the parameter JDialog be hidden when pressing escape
     * 
     * @param popup
     */
    public static void setupHideOnEscape(final JDialog popup)
    {
        Action aa = new AbstractAction()
        {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent event)
            {
                popup.setVisible(false);
            }
        };
        final String mapKey = "escapePressed";
        JRootPane root = popup.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, mapKey);
        root.getActionMap().put(mapKey, aa);
    }

    /**
     * Access to the chat panel through this window, for any places there is a reference to just the ChatWindow, but
     * that needs to affect the ChatPanel, which has all the actual chat options
     * 
     * @return chatPanel
     */
    public ChatPanel getChatPanel()
    {
        return chatPanel;
    }

    /**
     * This is a small hack to expose the ctrl window to the message control panel so its username case map can be
     * cleared out if the user changes the type of username case resolution
     */
    public void clearUsernameCases()
    {
        mouseListeners.clearUsernameCases();
    }

}
