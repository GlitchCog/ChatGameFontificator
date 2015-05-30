package com.glitchcog.fontificator.gui.component.palette;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * A color swatch is a selectable panel that displays a color
 * 
 * @author Matt Yanos
 */
public class ColorSwatch extends JPanel
{
    private static final long serialVersionUID = 1L;

    private static final int BORDER_THICKNESS = 2;

    private DashTimer dashTimer;

    private boolean selected;

    private static final Border BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE, BORDER_THICKNESS), BorderFactory.createLineBorder(Color.BLACK, BORDER_THICKNESS));

    public ColorSwatch(Color color, DashTimer dashTimer, MouseListener ml)
    {
        this.dashTimer = dashTimer;
        setBackground(color);
        setBorder(BORDER);
        addMouseListener(ml);
    }

    /**
     * Get the color of this swatch
     * 
     * @return color
     */
    public Color getColor()
    {
        return getBackground();
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void toggleSelected()
    {
        setSelected(!selected);
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if (isSelected())
        {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(Color.BLACK);
            Stroke stroke = new BasicStroke(BORDER_THICKNESS, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[]
            { 3, 3, 3, 3 }, dashTimer.getOffset());
            g2d.setStroke(stroke);
            final int halfThickness = Math.max(1, BORDER_THICKNESS / 2);
            g2d.drawRect(halfThickness, halfThickness, getWidth() - BORDER_THICKNESS, getHeight() - BORDER_THICKNESS);
        }
    }
}
