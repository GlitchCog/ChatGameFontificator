package com.glitchcog.fontificator.gui.controls.panel.model;

/**
 * Two strings to make a dropdown label that goes in a group, but only the label is used in the equals method, so it can
 * be compared against regardless of the group it's in
 * 
 * @author Matt Yanos
 */
public class DropdownLabel
{
    /**
     * The parent menu the dropdown label is displayed in
     */
    private final String group;

    /**
     * The label of the dropdown menu item
     */
    private final String label;

    public DropdownLabel(String label)
    {
        this.group = null;
        this.label = label;
    }

    public DropdownLabel(String group, String label)
    {
        this.group = group;
        this.label = label;
    }

    public String getGroup()
    {
        return group;
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        DropdownLabel other = (DropdownLabel) obj;
        if (label == null)
        {
            if (other.label != null)
            {
                return false;
            }
        }
        else if (!label.equals(other.label))
        {
            return false;
        }
        return true;
    }
}
