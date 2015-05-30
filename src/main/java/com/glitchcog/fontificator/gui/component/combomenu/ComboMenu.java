package com.glitchcog.fontificator.gui.component.combomenu;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * Adapted from https://www.crionics.com/public/swing_examples/JMenuExamples1.html
 */
public class ComboMenu extends JMenu
{
    private static final long serialVersionUID = 1L;

    private ArrowIcon iconRenderer;

    public ComboMenu(String label)
    {
        super(label);
        iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
        setBorder(new EtchedBorder());
        setIcon(new BlankIcon(null, 11));
        setHorizontalTextPosition(JButton.LEFT);
        setFocusPainted(true);
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Dimension d = this.getPreferredSize();
        int x = Math.max(0, d.width - iconRenderer.getIconWidth() - 3);
        int y = Math.max(0, (d.height - iconRenderer.getIconHeight()) / 2 - 2);
        iconRenderer.paintIcon(this, g, x, y);
    }

}
