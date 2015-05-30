package com.glitchcog.fontificator.gui.component.palette;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * A swatch panel displays a collection of colors. It sits on a Palette next to the add and remove buttons to display
 * and to make selectable the chosen colors
 * 
 * @author Matt Yanos
 */
public class SwatchPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private List<ColorSwatch> swatches;

    private MouseListener ml;

    private GridBagConstraints gbc;

    private DashTimer dashTimer;

    public SwatchPanel(Color bgColor)
    {
        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);

        dashTimer = new DashTimer(this);

        this.ml = new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                ColorSwatch source = (ColorSwatch) e.getSource();
                source.toggleSelected();

                if (anySwatchesSelected())
                {
                    dashTimer.start();
                }
                else
                {
                    dashTimer.stop();
                }

                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
            }
        };

        this.swatches = new ArrayList<ColorSwatch>();
        setBackground(bgColor);

        Color borderColor = bgColor.darker();
        if (borderColor.equals(bgColor))
        {
            borderColor = bgColor.brighter();
        }
        setBorder(BorderFactory.createLineBorder(borderColor));
    }

    private boolean anySwatchesSelected()
    {
        for (ColorSwatch swatch : swatches)
        {
            if (swatch.isSelected())
            {
                return true;
            }
        }
        return false;
    }

    public List<Color> getColors()
    {
        List<Color> colors = new ArrayList<Color>();
        for (ColorSwatch swatch : swatches)
        {
            colors.add(swatch.getColor());
        }
        return colors;
    }

    public void clearSelection()
    {
        for (ColorSwatch swatch : swatches)
        {
            swatch.setSelected(false);
        }
        dashTimer.stop();
        repaint();
    }

    public void addColor(Color addition)
    {
        clearSelection();
        ColorSwatch swatch = new ColorSwatch(addition, dashTimer, ml);
        this.swatches.add(swatch);
        gbc.gridx = getCount();
        add(swatch, gbc);
    }

    public void removeSelectedColors()
    {
        List<Color> colors = new ArrayList<Color>();
        for (ColorSwatch swatch : swatches)
        {
            if (!swatch.isSelected())
            {
                colors.add(swatch.getColor());
            }
        }
        swatches.clear();
        removeAll();
        for (Color color : colors)
        {
            addColor(color);
        }
    }

    public boolean isEmpty()
    {
        return swatches.isEmpty();
    }

    public int getCount()
    {
        return swatches == null ? 0 : swatches.size();
    }

    public void clear()
    {
        clearSelection();
        swatches.clear();
        removeAll();
        validate();
        repaint();
    }

}
