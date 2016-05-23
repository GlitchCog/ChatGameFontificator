package com.glitchcog.fontificator.gui.component.combomenu;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * Adapted from http://stackoverflow.com/questions/1738966/java-jtextfield-with-input-hint#answer-24571681
 */
public class HintTextField extends JTextField
{
    private static final long serialVersionUID = 1L;

    private final String hint;

    public HintTextField(String hint, int size)
    {
        super(size);
        addFocusListener(new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                repaint();
            }
        });
        this.hint = hint;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if (!hasFocus() && getText().length() == 0)
        {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
}