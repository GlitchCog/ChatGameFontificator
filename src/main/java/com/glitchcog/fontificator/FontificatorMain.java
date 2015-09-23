package com.glitchcog.fontificator;

import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;

/**
 * Houses the main method for the program
 * 
 * @author Matt Yanos
 */
public class FontificatorMain
{
    private static final Logger logger = Logger.getLogger(FontificatorMain.class);

    /**
     * The main method for the program
     * 
     * @param args
     *            unused
     * @throws Exception
     */
    public static void main(String[] args)
    {
        // Configure the logger
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%p] %d{MM-dd-yyyy HH:mm:ss} %c %M - %m%n")));
        Logger.getRootLogger().setLevel(Level.INFO);

        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e)
        {
            logger.error(e.toString(), e);
        }

        LogBox logBox = new LogBox();

        // These properties contain all the configuration for the program
        FontificatorProperties fProps = new FontificatorProperties();

        // The ChatWindow is the main window and shows the visualization of the chat
        ChatWindow chatWindow = new ChatWindow();

        // The ControlWindow is the dependent window that has all the options for modifying the properties of the chat
        ControlWindow controlWindow = new ControlWindow(chatWindow, fProps, logBox);

        // Attempt to load the last opened data, or fall back to defaults if nothing has been loaded or if there are any
        // errors loading
        controlWindow.loadLastData(chatWindow);

        try
        {
            // Feed the properties into the chat to give it hooks into the properties' configuration models; Feed the
            // ControlWindow into the ChatWindow to give the chat hooks back into the controls; Sets the loaded member
            // Boolean in the chat to indicate it has everything it needs to begin rendering the visualization
            chatWindow.initChat(fProps, controlWindow);
        }
        catch (Exception e)
        {
            logger.error(e.toString(), e);
            ChatWindow.popup.handleProblem(e.toString(), e);
            System.exit(1);
        }

        // Build the GUI of the control window
        controlWindow.build(logBox);

        // Finally, display the chat and control windows now that everything has been constructed and connected
        chatWindow.setVisible(true);
        controlWindow.setVisible(true);

    }

}
