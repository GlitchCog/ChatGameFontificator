package com.glitchcog.fontificator.gui.chat;

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

    private ChatPanel chatPanel;

    public static ChatWindow me;

    public static FontificatorError popup;

    private ChatMouseListeners mouseListeners;

    private static final KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

    public ChatWindow()
    {
        me = this;
        setTitle("Fontificator Chat");
        popup = new FontificatorError(null);
    }

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
                callExit();
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
            private void callExit()
            {
                ctrlWindow.attemptToExit();
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

    public ChatPanel getChatPanel()
    {
        return chatPanel;
    }

    public void clearUsernameCases()
    {
        // This is a small hack to expose the ctrl window to the message control
        // panel so its username case map can be cleared out if the user changes
        // the type of username case resolution
        mouseListeners.clearUsernameCases();
    }

}
