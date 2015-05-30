package com.glitchcog.fontificator.gui;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Class that is the one place to handle problems. Opens a JOptionPane to display them to the user.
 * 
 * @author Matt Yanos
 */
public class FontificatorError
{
    private JFrame parent;

    public FontificatorError(JFrame parent)
    {
        this.parent = parent;
    }

    public void handleProblem(String description)
    {
        handleProblem(description, null);
    }

    public void handleProblem(List<String> errors)
    {
        String allErrors = "<html>Error" + (errors.size() == 1 ? "" : "s") + ":<br />";
        for (String er : errors)
        {
            allErrors += er + "<br />";
        }
        allErrors += "</html>";
        handleProblem(allErrors);
    }

    public void handleProblem(String description, Throwable t)
    {
        JOptionPane.showMessageDialog(parent, description);
    }
}
