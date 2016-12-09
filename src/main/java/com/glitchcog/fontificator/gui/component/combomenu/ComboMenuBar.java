package com.glitchcog.fontificator.gui.component.combomenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.glitchcog.fontificator.game.VideoGame;

/**
 * Adapted from https://www.crionics.com/public/swing_examples/JMenuExamples1.html
 */
public class ComboMenuBar extends JMenuBar
{
    private static final long serialVersionUID = 1L;

    private JMenu menu;

    private Dimension preferredSize;

    private HintTextField filterInput;

    private List<JMenu> menus;

    public ComboMenuBar(Map<String, List<String>> menuTextMap, ActionListener al)
    {
        filterInput = new HintTextField("Filter", 7);
        filterInput.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                applyFilter(filterInput.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                applyFilter(filterInput.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                applyFilter(filterInput.getText());
            }
        });
        add(filterInput);

        Collection<String> menuLabels = menuTextMap.keySet();

        menus = new ArrayList<JMenu>();
        List<JMenuItem> rootItems = new ArrayList<JMenuItem>();

        for (String label : menuLabels)
        {
            // Check if there is no submenu and add that as a root item if
            if (menuTextMap.get(label) == null)
            {
                JMenuItem item = new JMenuItem(label);
                item.addActionListener(al);
                rootItems.add(item);
            }
            else
            {
                menus.add(new JMenu(label));
            }
        }

        JMenu menu = new ComboMenu(null);

        for (JMenu m : menus)
        {
            List<String> submenuText = menuTextMap.get(m.getText());
            if (submenuText != null)
            {
                for (String submenuLabel : submenuText)
                {
                    JMenuItem item = new GameFontMenuItem(submenuLabel, new VideoGame(submenuLabel));
                    item.addActionListener(al);
                    m.add(item);
                }
                menu.add(m);
            }
        }

        for (JMenuItem item : rootItems)
        {
            menu.add(item);
        }

        setup(menu);
    }

    private void setup(final JMenu menu)
    {
        this.menu = menu;

        Color color = UIManager.getColor("Menu.selectionBackground");
        UIManager.put("Menu.selectionBackground", UIManager.getColor("Menu.background"));
        menu.updateUI();
        UIManager.put("Menu.selectionBackground", color);

        ActionListener listener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JMenuItem item = (JMenuItem) e.getSource();
                menu.setText(item.getText());
                menu.requestFocus();
            }
        };
        setListener(menu, listener);

        add(menu);
    }

    private void setListener(JMenuItem item, ActionListener listener)
    {
        if (item instanceof JMenu)
        {
            JMenu menu = (JMenu) item;
            int n = menu.getItemCount();
            for (int i = 0; i < n; i++)
            {
                setListener(menu.getItem(i), listener);
            }
        }
        else if (item != null)
        {
            item.addActionListener(listener);
        }
    }

    public String getSelectedItem()
    {
        return menu.getText();
    }

    public void setSelectedText(String label)
    {
        menu.setText(label);
    }

    @Override
    public void setPreferredSize(Dimension size)
    {
        preferredSize = size;
    }

    @Override
    public Dimension getPreferredSize()
    {
        if (preferredSize == null)
        {
            Dimension menuD = getItemSize(menu);
            Insets margin = menu.getMargin();
            Dimension retD = new Dimension(menuD.width, margin.top + margin.bottom + menuD.height);
            menu.setPreferredSize(retD);
            preferredSize = retD;
        }
        return preferredSize;
    }

    private Dimension getItemSize(JMenu menu)
    {
        Dimension d = new Dimension(0, 0);
        int n = menu.getItemCount();
        for (int i = 0; i < n; i++)
        {
            Dimension itemD;
            JMenuItem item = menu.getItem(i);
            if (item instanceof JMenu)
            {
                itemD = getItemSize((JMenu) item);
            }
            else if (item != null)
            {
                itemD = item.getPreferredSize();
            }
            else
            {
                itemD = new Dimension(0, 0);
            }
            d.width = Math.max(d.width, itemD.width);
            d.height = Math.max(d.height, itemD.height);
        }
        return d;
    }

    private void applyFilter(String filterText)
    {
        for (JMenu menu : menus)
        {
            boolean atLeastOneHit = false;
            boolean matchesMenu = menu.getText().toLowerCase().contains(filterText.toLowerCase().trim());
            for (int i = 0; i < menu.getItemCount(); i++)
            {
                GameFontMenuItem item = (GameFontMenuItem) menu.getItem(i);
                final boolean matchesGame = item.isMatchingFilterGame(filterText);
                final boolean matchesGenre = item.isMatchingFilterGenre(filterText);
                final boolean matchesSystem = item.isMatchingFilterSystem(filterText);

                boolean hit = matchesMenu || matchesGame || matchesGenre || matchesSystem;

                item.setVisible(hit);
                if (hit)
                {
                    atLeastOneHit = true;
                }
            }
            menu.setVisible(atLeastOneHit);
        }
    }
}