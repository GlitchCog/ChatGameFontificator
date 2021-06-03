package com.glitchcog.fontificator.gui.component.combomenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.Timer;

public class MenuScrollItem extends JMenuItem implements ChangeListener
{
    private static final long serialVersionUID = 1L;

    private Timer timer;

    public MenuScrollItem(final ComboMenuBar cmb, final MenuIcon icon)
    {
        setIcon(icon);
        setDisabledIcon(icon);
        timer = new Timer(64, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cmb.incrementFirstIndex(icon.getIncrement());
                cmb.updateScroll();
            }
        });
        addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        if (isArmed() && !timer.isRunning())
        {
            timer.start();
        }
        if (!isArmed() && timer.isRunning())
        {
            timer.stop();
        }
    }
}