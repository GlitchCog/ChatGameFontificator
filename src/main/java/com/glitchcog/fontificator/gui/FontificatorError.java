package com.glitchcog.fontificator.gui;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;

/**
 * Class that is the one place to handle problems. Opens a JOptionPane to display them to the user.
 * 
 * @author Matt Yanos
 */
public class FontificatorError
{
    private JFrame parent;

    private static final int MAX_ERRORS_TO_DISPLAY = 12;

    public FontificatorError(JFrame parent)
    {
        this.parent = parent;
    }

    public void handleProblem(String description)
    {
        handleProblem(description, (Throwable) null);
    }

    public void handleProblem(LoadConfigReport report)
    {
        if (report.getMainMessage() == null)
        {
            handleProblem("", report.getMessages());
        }
        else
        {
            handleProblem(report.getMainMessage(), report.getMessages());
        }
    }

    public void handleProblem(List<String> errors)
    {
        handleProblem("Error" + (errors.size() == 1 ? "" : "s") + ":<br />", errors);
    }

    public void handleProblem(String summary, List<String> errors)
    {
        String allErrors = "<html>" + summary + (summary == null || summary.isEmpty() ? "" : "<br />");
        int eCount = 0;
        for (String er : errors)
        {
            allErrors += er + "<br />";
            eCount++;
            if (eCount > MAX_ERRORS_TO_DISPLAY && errors.size() - eCount > 0)
            {
                final int remaining = errors.size() - eCount;
                allErrors += "and " + remaining + " other error" + (remaining == 1 ? "" : "s") + "<br />";
                break;
            }
        }
        allErrors += "</html>";
        handleProblem(allErrors);
    }

    public void handleProblem(String description, Throwable t)
    {
        JOptionPane.showMessageDialog(parent, description);
    }
}
