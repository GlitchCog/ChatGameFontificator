package com.glitchcog.fontificator.gui.component.palette;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * Times the marching ant selection for the selection color swatches
 * 
 * @author Matt Yanos
 */
public class DashTimer
{
    /**
     * The offset for the dash stroke so the dashes move
     */
    private int offset;

    /**
     * The timer for marching the ants
     */
    private Timer timer;

    /**
     * The panel within which the ants march, so it must be repainted
     */
    private final SwatchPanel sp;

    /**
     * Construct the dash timer for the specified swatch panel
     * 
     * @param swatchPanel
     *            The swatch panel to be redrawn with ever iteration of the timer
     */
    public DashTimer(SwatchPanel swatchPanel)
    {
        this.sp = swatchPanel;
        timer = new Timer(63, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                offset++;
                sp.repaint();
            }
        });
    }

    /**
     * Start the dash timer
     */
    public void start()
    {
        timer.start();
    }

    /**
     * Stop the dash timer
     */
    public void stop()
    {
        timer.stop();
    }

    /**
     * Get the offset for the dash stroke so the dashes move
     * 
     * @return offset
     */
    public int getOffset()
    {
        return offset;
    }
}
