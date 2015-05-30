package com.glitchcog.fontificator.gui.component;

import javax.swing.KeyStroke;

/**
 * Simple grouping of a label, a mnemonic key to press, and a shortcut key
 * combination accelerator, together representing an entry on the main menu
 * 
 * @author Matt Yanos
 */
public class MenuComponent
{
    /**
     * The String to be displayed on the menu
     */
    public final String label;

    /**
     * The letter to underline and the key to press to make a selection when
     * using the keyboard to navigate the menu
     */
    public final int mnemonic;

    /**
     * The keyboard shortcut combination for the menu item
     */
    public final KeyStroke accelerator;

    /**
     * Whether the menu item is a checkbox menu item
     */
    public final boolean checkbox;

    /**
     * A constructor to simply set all the values
     * 
     * @param label
     *            The String to be displayed on the menu
     * @param mnemonic
     *            The letter to underline and the key to press to make a
     *            selection when using the keyboard to navigate the menu
     * @param accelerator
     *            The keyboard shortcut combination for the menu item
     */
    public MenuComponent(String label, int mnemonic, KeyStroke accelerator)
    {
        this(label, mnemonic, accelerator, false);
    }

    /**
     * A constructor to simply set all the values
     * 
     * @param label
     *            The String to be displayed on the menu
     * @param mnemonic
     *            The letter to underline and the key to press to make a
     *            selection when using the keyboard to navigate the menu
     * @param accelerator
     *            The keyboard shortcut combination for the menu item
     * @param checkbox
     *            Whether the menu item is a checkbox menu item
     */
    public MenuComponent(String label, int mnemonic, KeyStroke accelerator, boolean checkbox)
    {
        this.label = label;
        this.mnemonic = mnemonic;
        this.accelerator = accelerator;
        this.checkbox = checkbox;
    }
}
