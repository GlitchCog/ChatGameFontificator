package com.glitchcog.fontificator.gui.chat;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;

import com.glitchcog.fontificator.gui.controls.ControlWindow;

/**
 * Mouse Listeners for the Chat Window, also handles clearing out username cases since it needs to store a reference to
 * the Control Window anyway
 * 
 * @author Matt Yanos
 */
public class ChatMouseListeners implements MouseMotionListener, MouseInputListener
{
    /**
     * The offset into the window where the mouse button was pressed, to offset the window location when it is dragged
     * around.
     */
    private Point pressOffset = new Point();

    /**
     * Whether the mouse button is currently pressed
     */
    private boolean pressed;

    /**
     * Reference to the chat window
     */
    private JFrame chatWindow;

    /**
     * Reference to the control window
     */
    private ControlWindow ctrlWindow;

    public ChatMouseListeners(JFrame chatWindow, ControlWindow ctrlWindow)
    {
        this.chatWindow = chatWindow;
        this.ctrlWindow = ctrlWindow;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        int x = e.getLocationOnScreen().x - pressOffset.x;
        int y = e.getLocationOnScreen().y - pressOffset.y;

        chatWindow.setLocation(x, y);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            ctrlWindow.setVisible(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            if (!pressed)
            {
                pressOffset.x = e.getX();
                pressOffset.y = e.getY();
                pressed = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        pressed = false;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * Clear out the username casing cache. This is shoehorned into this class because it already has a reference to the
     * control window.
     */
    public void clearUsernameCases()
    {
        ctrlWindow.clearUsernameCases();
    }
}
