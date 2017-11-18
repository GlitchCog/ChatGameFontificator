package com.glitchcog.fontificator.gui.component.combomenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Adapted from https://www.crionics.com/public/swing_examples/JMenuExamples1.html and
 * https://tips4java.wordpress.com/2009/02/01/menu-scroller/
 */
public class ComboMenuBar extends JMenuBar
{
    private static final long serialVersionUID = 1L;

    private JMenu mainMenu;

    private MenuScrollItem upItem;

    private MenuScrollItem downItem;

    public static final int SCROLL_COUNT = 12;

    private int firstIndex = 0;

    private Dimension preferredSize;

    private HintTextField filterInput;

    private class MenuVisibilityStatus
    {
        private boolean filtered;

        private boolean outOfScrollBounds;

        public void setFiltered(boolean filtered)
        {
            this.filtered = filtered;
        }

        public void setOutOfScrollBounds(boolean outOfScrollBounds)
        {
            this.outOfScrollBounds = outOfScrollBounds;
        }

        public boolean isFiltered()
        {
            return filtered;
        }

        public boolean isOutOfScrollBounds()
        {
            return outOfScrollBounds;
        }

        public void reset()
        {
            filtered = false;
            outOfScrollBounds = false;
        }

        public String toString()
        {
            return "Filtered=" + filtered + "; OutOfScrollBounds=" + outOfScrollBounds;
        }
    }

    /**
     * All the folders containing actual selectable menu items. These are not GameFontMenuItem objects.
     */
    private Map<JMenuItem, MenuVisibilityStatus> allMenuItems;
    private Map<JMenu, MenuVisibilityStatus> allMenuFolders;

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

        allMenuItems = new LinkedHashMap<JMenuItem, MenuVisibilityStatus>();
        allMenuFolders = new LinkedHashMap<JMenu, MenuVisibilityStatus>();

        /*
         * rootItems is the list of items on the root of the menu along with all the 'allMenuFolders' menus. Essentially
         * just the Custom... item
         */
        List<JMenuItem> rootItems = new ArrayList<JMenuItem>();

        for (String label : menuLabels)
        {
            MenuVisibilityStatus status = new MenuVisibilityStatus();
            // Check if there is no submenu and add that as a root item
            if (menuTextMap.get(label) == null)
            {
                JMenuItem item = new JMenuItem(label);
                // Custom
                item.addActionListener(al);
                rootItems.add(item);
                allMenuItems.put(item, status);
            }
            else
            {
                JMenu item = new JMenu(label);
                allMenuFolders.put(item, status);
                allMenuItems.put(item, status);
            }

        }

        JMenu menu = new ComboMenu(); // The top-level menu

        /*
         * This populates the menu with all the submenus, and also fills each submenu with the selectable items
         */
        for (JMenuItem m : allMenuItems.keySet())
        {
            List<String> submenuText = menuTextMap.get(m.getText());
            if (submenuText != null)
            {
                for (String submenuLabel : submenuText)
                {
                    JMenuItem item = new GameFontMenuItem(submenuLabel);
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
        setupScroll(menu);
        this.mainMenu = menu;
    }

    private void setup(final JMenu menu)
    {
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

    private void setupScroll(final JMenu menu)
    {
        upItem = new MenuScrollItem(this, MenuIcon.UP);
        downItem = new MenuScrollItem(this, MenuIcon.DOWN);

        PopupMenuListener menuListener = new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                updateScroll();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
            {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e)
            {
            }
        };

        MenuSelectionManager.defaultManager().clearSelectedPath();
        menu.getPopupMenu().addPopupMenuListener(menuListener);
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

    private void applyFilter(String filterText)
    {
        resetMenu();

        for (JMenu menuFolder : allMenuFolders.keySet())
        {
            boolean atLeastOneHit = false;
            boolean matchesMenu = menuFolder.getText().toLowerCase().contains(filterText.toLowerCase().trim());

            for (int i = 0; i < menuFolder.getItemCount(); i++)
            {
                GameFontMenuItem gfmi = (GameFontMenuItem) menuFolder.getItem(i);
                final boolean matchesGame = gfmi.isMatchingFilterGame(filterText);
                final boolean matchesGenre = gfmi.isMatchingFilterGenre(filterText);
                final boolean matchesSystem = gfmi.isMatchingFilterSystem(filterText);

                boolean hit = matchesMenu || matchesGame || matchesGenre || matchesSystem;

                gfmi.setVisible(hit);
                if (hit)
                {
                    atLeastOneHit = true;
                }
            }
            allMenuItems.get(menuFolder).setFiltered(!atLeastOneHit);
        }

        setMenuItemFilterVisibility();
    }

    private void setMenuItemFilterVisibility()
    {
        for (JMenuItem menuFolder : allMenuItems.keySet())
        {
            final boolean menuFolderVisibility = !allMenuItems.get(menuFolder).isFiltered() && !menuFolder.getText().isEmpty();
            menuFolder.setVisible(menuFolderVisibility);
        }
    }

    private List<JMenuItem> getInBoundScrollMenuFolders()
    {
        List<JMenuItem> menuItemKeyList = new ArrayList<JMenuItem>();
        for (JMenuItem menuFolder : allMenuItems.keySet())
        {
            if (!allMenuItems.get(menuFolder).isOutOfScrollBounds())
            {
                menuItemKeyList.add(menuFolder);
            }
        }
        return menuItemKeyList;
    }

    public void updateScroll()
    {
        List<JMenuItem> menuKeyList = getKeyList();

        // Keep the first index less than the total number available in the list (unfiltered) minus the length of the scroll count, and no lower than zero
        firstIndex = Math.min(firstIndex, menuKeyList.size() - SCROLL_COUNT);
        firstIndex = Math.max(firstIndex, 0);
        int lastIndex = firstIndex + Math.min(menuKeyList.size(), SCROLL_COUNT);

        int runningUnfilteredCount = 0;
        for (int i = 0; i < menuKeyList.size(); i++)
        {
            JMenuItem jmi = menuKeyList.get(i);
            MenuVisibilityStatus status = allMenuItems.get(jmi);
            final boolean inScrollBounds = !status.isFiltered() && runningUnfilteredCount >= firstIndex && runningUnfilteredCount < lastIndex;
            status.setOutOfScrollBounds(!inScrollBounds);
            if (!status.isFiltered())
            {
                runningUnfilteredCount++;
            }
        }

        mainMenu.getPopupMenu().removeAll();
        setMenuItemFilterVisibility();

        upItem.setEnabled(firstIndex > 0);
        mainMenu.getPopupMenu().add(upItem);
        List<JMenuItem> inBoundsScrollMenuFolders = getInBoundScrollMenuFolders();

        for (JMenuItem menuFolder : inBoundsScrollMenuFolders)
        {
            mainMenu.getPopupMenu().add(menuFolder);
        }
        downItem.setEnabled(lastIndex < runningUnfilteredCount);
        mainMenu.getPopupMenu().add(downItem);

        JComponent parent = (JComponent) upItem.getParent();
        parent.revalidate();
        parent.repaint();
    }

    private void resetMenu()
    {
        firstIndex = 0;
        // Clear menu
        mainMenu.getPopupMenu().removeAll();
        // Set everything to visible and reset all map values to zero
        for (JMenuItem menuFolder : allMenuItems.keySet())
        {
            allMenuItems.get(menuFolder).reset();
            menuFolder.setVisible(true);
            if (menuFolder instanceof JMenu)
            {
                JMenu jMenuFolder = (JMenu) menuFolder;
                for (int i = 0; i < jMenuFolder.getItemCount(); i++)
                {
                    jMenuFolder.getItem(i).setVisible(true);
                }
            }
        }
        // Add all menu folders to the popup menu
        for (JMenuItem menuFolder : allMenuItems.keySet())
        {
            menuFolder.setVisible(true);
            mainMenu.getPopupMenu().add(menuFolder);
        }
    }

    public String getSelectedItem()
    {
        return mainMenu.getText();
    }

    public void setSelectedText(String label)
    {
        mainMenu.setText(label);
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
            Dimension menuD = getItemSize(mainMenu);
            Insets margin = mainMenu.getMargin();
            Dimension retD = new Dimension(menuD.width, margin.top + margin.bottom + menuD.height);
            mainMenu.setPreferredSize(retD);
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

    private List<JMenuItem> getKeyList()
    {
        List<JMenuItem> menuItemKeyList = new ArrayList<JMenuItem>();
        for (JMenuItem menuFolder : allMenuItems.keySet())
        {
            if (!menuFolder.getText().isEmpty())
            {
                menuItemKeyList.add(menuFolder);
            }
        }
        return menuItemKeyList;
    }

    public void incrementFirstIndex(int increment)
    {
        firstIndex += increment;
    }
}