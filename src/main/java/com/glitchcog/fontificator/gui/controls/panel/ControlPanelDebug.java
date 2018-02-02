
package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.DebugAppender;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.ColorButton;
import com.glitchcog.fontificator.gui.component.LabeledSlider;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.sprite.SpriteFont;

/**
 * Control Panel containing debugging options and information
 * 
 * @author Matt Yanos
 */
public class ControlPanelDebug extends ControlPanelBase
{
    private static final long serialVersionUID = 1L;

    /**
     * Whether debugging is activated, which should correspond to when this panel is displayed
     */
    private boolean debugging;

    private ControlWindow ctrlWindow;

    private DebugAppender debugAppender;

    private JButton postTestMessage;

    private JToggleButton postMessagesButton;

    private LabeledSlider postRateSlider;

    private JCheckBox drawTextGridBox;

    private ColorButton textGridColorButton;

    private JCheckBox drawBorderGridBox;

    private ColorButton borderGridColorButton;

    private Random rnd;

    private Timer postClock;

    // @formatter:off
    private String[] TEST_USERNAMES = new String[] { "Mario", "Luigi", "Peach", "Toad", "Bowser", "Shyguy", "Yoshi", "Birdo", "Goomba", "Koopa", 
                                                     "Link", "Zelda", "Impa", "Shiek", "Navi", "Tingle", "Error", "Bagu", "Agahnim", "Ganon", "Ganondorf", 
                                                     "Ryu", "Chun-Li", "Ken", "Blanka", "Guile", "Sagat", "Vega", "Zangief", "Balrog", "Dhalsim", 
                                                     "Samus", "Ridley", "Kraid", "Simon", "Alucard", "Kirby", "Chell", "GLaDOS", "Guycott", "Pacman", "Guybrush", 
                                                     "Sonic", "Tails", "Knuckles", "Robotnik", "Eggman", 
                                                     "Bulbasaur", "Squirtle", "Charmander", "Pikachu", "Zubat", "Caterpie", "Jigglypuff", "Psyduck", "Meowth", "Mewtwo", "Snorlax", "Magikarp", 
                                                     "Ness", "Paula", "Jeff", "Poo", "Lucas", "Claus", "Flint", "Hinawa", 
                                                     "Chrono", "Marle", "Lucca", "Robo", "Magus", "Frog", "Ayla", "Gato", "Lavos", "Schala", "Dalton", "Serge", "Lynx", "Kid", "Harle", 
                                                     "Alena", "Talloon", "Ragnar", "Brey", "Cristo", "Maya", "Nara", "Maribel", "Kiefer", "Gabo", "Melvin", "Aira", 
                                                     "Hargon", "Malroth", "Kandar", "Baramos", "Zoma", "Estark", "Aamon", "Balzack", "Psaro", "Necrosaro",  
                                                     "Terra", "Locke", "Celes", "Edgar", "Sabin", "Kefka", "Cloud", "Tifa", "Aerith", "Barret", "Sephiroth" };

    private String[] TEST_MESSAGES = new String[] { "You presumptuous little twit. I'll have you for lunch!", 
                                                    "Thank you Mario! But our princess is in another castle!", 
                                                    "It's dangerous to go alone. Take this.", 
                                                    "It's a secret to everybody.", 
                                                    "I feel asleep!!", 
                                                    "Now it is the beginning of a fantastic story!", 
                                                    "Let us make a journey to the cave of monsters!", 
                                                    "A winner is you!", 
                                                    "Let's play money making game.", 
                                                    "A Slime draws near! Command?", 
                                                    "I am Error.", 
                                                    "Hey, listen!", 
                                                    "Bagu is my name. Show my note to river man.", 
                                                    "What a horrible night to have a curse.", 
                                                    "The morning sun has vanquished the horrible night.", 
                                                    "You now prossess Dracula's Rib", 
                                                    "Take my daughter, please!!", 
                                                    "Get a Silk Bag from the Graveyard Duck to live longer.", 
                                                    "All your base are belong to us.", 
                                                    "The morning sun has vanquished the horrible night.", 
                                                    "Get a silk bag from the graveyard duck to live longer.", 
                                                    "Someone set us up the bomb.", 
                                                    "Are you a bad enough dude to rescue the president?", 
                                                    "That's the second biggest monkey head I've ever seen!", 
                                                    "Now you're thinking with portals.", 
                                                    "Would you kindly?", 
                                                    "The cake is a lie.", 
                                                    "We've both said a lot of things that you're going to regret.", 
                                                    "It's like my Rattata is in the top percentage of rattatas.", 
                                                    "You have died of dysentery.", 
                                                    "Curse you Link!", 
                                                    "Time passes, people move. Like a river's flow, it never ends.", 
                                                    "I... I shall consume. Consume... consume everything...", 
                                                    "You've met with a terrible fate, haven't you?", 
                                                    "I really am the richest duck in the world.", 
                                                    "The fate of the forest nay the world depends upon thee.", 
                                                    "Well excuuuuuse me, princess!", 
                                                    "Bombs! Bombs are the answer!", 
                                                    "The name of the Archfiend Baramos is yet unknown in the world of men.", 
                                                    "But Thou Must!", 
                                                    "'Tis about as useful as a chocolate teapot.", 
                                                    "Thou wilt regret thy coming indeed! You shall be dead for good, for I wilt surely feast on thine innards!", 
                                                    "What is a man? A miserable little pile of secrets! But enough talk! Have at you!", 
                                                    "I'll not ask you to return to our side, but I demand you cease your attack.", 
                                                    "Perhaps you can save his haunted soul.", 
                                                    "Machines aren't capable of evil. Humans make them that way.", 
                                                    "Lower thine guard, and thou'rt allowing the enemy in.", 
                                                    "Old man breathe, but dead on inside!", 
                                                    "Do you wish to fight me?", 
                                                    "Beat me up and earn fifteen silver points!", 
                                                    "This is not enough golds.", 
                                                    "Your fists of evil are about to meet my steel wall of niceness.", 
                                                    "Thanks a million!", 
                                                    "You have completed a great game.", 
                                                    "You spoony bard!", 
                                                    "Welcome to die!", 
                                                    "You must defeat my Dragon Punch to stand a chance!", 
                                                    "You did quite well, but you need more training to defeat me!", 
                                                    "It's natural for a sumo wrestler to become the world's strongest!", 
                                                    "Can't you do better than that?", 
                                                    "Seeing you in action is a joke!", 
                                                    "Now you realize the powers I possess!", 
                                                    "Are you man enough to fight with me?", 
                                                    "Go home and be a family man!", 
                                                    "Get up!! It's too early for you to be defeated!", 
                                                    "Attack me if you dare, I will crush you!", 
                                                    "There is no chance for you to beat me! Challenge someone else!", 
                                                    "I'm the strongest woman in the world!", 
                                                    "My strength is much greater than yours!", 
                                                    "Next time we meet, I'm gonna break your arms!", 
                                                    "Now you've realized the inner mysteries of yoga!", 
                                                    "I will meditate and then destroy you!!", 
                                                    "Get up you wimp!", 
                                                    "Hey, What happened? I'm not through with you yet!", 
                                                    "Handsome fighters never lose a battle!", 
                                                    "Thank you for a gorgeous time!", 
                                                    "You are not a warrior, you're a beginner!", 
                                                    "I am not satisfied until I have the world's strongest title again!", 
                                                    "Get lost, you can't compare with my powers!", 
                                                    "Anyone who opposes me will be destroyed!"
                                                   };
    // @formatter:on

    private void postRandomMessage()
    {
        final String username = TEST_USERNAMES[rnd.nextInt(TEST_USERNAMES.length)];
        final String message = TEST_MESSAGES[rnd.nextInt(TEST_MESSAGES.length)];
        ctrlWindow.addManualMessage(username, message);
    }

    /**
     * Construct a debug control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param ctrlWindow
     */
    public ControlPanelDebug(FontificatorProperties fProps, ChatWindow chatWindow, ControlWindow ctrlWindow)
    {
        super("Debug", fProps, chatWindow, new LogBox());
        this.debugging = false;
        this.ctrlWindow = ctrlWindow;
        rnd = new Random();
        debugAppender = new DebugAppender(logBox);
    }

    public boolean isDrawTextGrid()
    {
        return isDebugging() && drawTextGridBox.isSelected();
    }

    public boolean isDrawBorderGrid()
    {
        return isDebugging() && drawBorderGridBox.isSelected();
    }

    public Color getTextGridColor()
    {
        return textGridColorButton.getColor();
    }

    public Color getBorderGridColor()
    {
        return borderGridColorButton.getColor();
    }

    @Override
    protected void build()
    {
        postTestMessage = new JButton("Post Debug Message");

        postTestMessage.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ctrlWindow.addManualMessage("Test", SpriteFont.NORMAL_ASCII_KEY);
            }
        });

        postMessagesButton = new JToggleButton("Post Messages");
        postRateSlider = new LabeledSlider("Post Rate ", " / min", 1, 120, 30, 3);

        postClock = new Timer(60000 / postRateSlider.getValue(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                postRandomMessage();
            }
        });

        postRateSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                if (!((JSlider) e.getSource()).getValueIsAdjusting())
                {
                    postClock.setDelay(60000 / postRateSlider.getValue());
                    if (postClock.isRunning())
                    {
                        postRandomMessage();
                        postClock.restart();
                    }
                }
            }
        });

        postMessagesButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                postRandomMessage();
                if (postMessagesButton.isSelected())
                {
                    postClock.start();
                }
                else
                {
                    postClock.stop();
                }
            }
        });

        drawTextGridBox = new JCheckBox("Draw Text Grid");
        textGridColorButton = new ColorButton("Text Grid Color", new Color(0x99FF88), "Color of the border grid", this);
        drawBorderGridBox = new JCheckBox("Draw Border Grid");
        borderGridColorButton = new ColorButton("Border Grid Color", new Color(0x9988FF), "Color of the border grid", this);

        ActionListener refreshListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (chat != null)
                {
                    chat.repaint();
                }
            }
        };

        drawTextGridBox.addActionListener(refreshListener);
        drawBorderGridBox.addActionListener(refreshListener);

        JPanel topPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(postTestMessage, gbc);
        gbc.gridx++;
        topPanel.add(postMessagesButton, gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(postRateSlider, gbc);

        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(topPanel, gbc);
        gbc.gridy++;

        gbc.gridwidth = 1;

        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(drawTextGridBox, gbc);
        gbc.gridx++;
        add(textGridColorButton, gbc);
        gbc.gridx++;
        add(drawBorderGridBox, gbc);
        gbc.gridx++;
        add(borderGridColorButton, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;

        gbc.weighty = 0.1;
        CollagePanel collagePanel = new CollagePanel(chat);
        add(collagePanel, gbc);
        gbc.gridy++;

        ExamplePanel examplePanel = new ExamplePanel(this);
        add(examplePanel, gbc);
        gbc.gridy++;

        gbc.weighty = 0.9;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridwidth = 4;

        add(logBox, gbc);
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        return new LoadConfigReport();
    }

    @Override
    public void fillConfigFromInput() throws Exception
    {
    }

    /**
     * Enable or disable debugging
     * 
     * @param debugging
     */
    public void setDebugging(boolean debugging)
    {
        this.debugging = debugging;
        if (debugging)
        {
            Thread.setDefaultUncaughtExceptionHandler(debugAppender);
            BasicConfigurator.configure(debugAppender);
        }
        else
        {
            // Turn off everything before disabling the debug tab
            postClock.stop();
            postMessagesButton.setSelected(false);
            drawTextGridBox.setSelected(false);
            drawBorderGridBox.setSelected(false);
            chat.repaint();
            Thread.setDefaultUncaughtExceptionHandler(null);
            Logger.getRootLogger().removeAppender(debugAppender);
        }
    }

    public boolean isDebugging()
    {
        return debugging;
    }
}
