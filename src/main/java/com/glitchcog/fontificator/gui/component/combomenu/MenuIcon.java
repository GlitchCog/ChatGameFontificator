package com.glitchcog.fontificator.gui.component.combomenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;

public enum MenuIcon implements Icon
{
    UP(true), DOWN(false);

    private final int[] xPoints;
    private final int[] yPoints;

    private final boolean isUp;

    MenuIcon(boolean isUp)
    {
        this.isUp = isUp;
        this.xPoints = new int[] { 1, 5, 9 };

        if (isUp)
        {
            this.yPoints = new int[] { 9, 1, 9 };
        }
        else
        {
            this.yPoints = new int[] { 1, 9, 1 };
        }
    }

    public int getIncrement()
    {
        return isUp ? -1 : 1;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        Dimension size = c.getSize();
        Graphics g2 = g.create(size.width / 2 - 5, size.height / 2 - 5, 10, 10);
        g2.setColor(Color.GRAY);
        g2.drawPolygon(xPoints, yPoints, 3);
        if (c.isEnabled())
        {
            g2.setColor(Color.BLACK);
            g2.fillPolygon(xPoints, yPoints, 3);
        }
        g2.dispose();
    }

    @Override
    public int getIconWidth()
    {
        return 0;
    }

    @Override
    public int getIconHeight()
    {
        return 10;
    }
}