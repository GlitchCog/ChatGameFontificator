package com.glitchcog.fontificator.gui.controls.messages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledInput;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;
import com.glitchcog.fontificator.sprite.SpriteFont;

/**
 * Panel for manually posting messages to the chat window
 * 
 * @author Matt Yanos
 */
public class MessagePostPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JLabel manualInfo;

    private LabeledInput usernameInput;

    private LabeledInput textInput;

    private JCheckBox retainMessageBox;

    private JButton submitButton;

    private JButton clearButton;

    private ControlWindow ctrlWindow;

    private int modCount;

    private Random rnd;

    // @formatter:off
    private String[] TEST_USERNAMES = new String[] { "Mario", "Luigi", "Peach", "Toad", "Bowser", "Shyguy", "Yoshi", "Birdo", "Goomba", "Koopa", 
                                                     "Link", "Zelda", "Impa", "Shiek", "Navi", "Tingle", 
                                                     "Ryu", "Chun-Li", "Ken", "Blanka", "Guile", "Sagat", "Vega", "Zangief", "Balrog", "Dhalsim", 
                                                     "Samus", "Ridley", "Kraid", "Simon", "Alucard", "Kirby", "Chell", "GLaDOS", 
                                                     "Bulbasaur", "Squirtle", "Charmander", "Pikachu", "Zubat", "Caterpie", "Jigglypuff", "Psyduck", "Meowth", "Mewtwo", "Snorlax", "Magikarp", 
                                                     "Ness", "Paula", "Jeff", "Poo", "Lucas", "Claus", "Flint", "Hinawa", 
                                                     "Chrono", "Marle", "Lucca", "Robo", "Magus", "Frog", "Ayla", "Lavos", "Schala", "Serge", "Lynx", "Kid", "Harle", 
                                                     "Alena", "Talloon", "Ragnar", "Brey", "Cristo", "Maya", "Nara", "Maribel", "Kiefer", "Gabo", "Melvin", "Aira", 
                                                     "Terra", "Locke", "Celes", "Edgar", "Sabin", "Kefka", "Cloud", "Tifa", "Aerith", "Barret", "Sephiroth" };

    private String[] TEST_MESSAGES = new String[] { "You presumptious little twit. I'll have you for lunch!", 
                                                    "Thank you Mario! But our princess is in another castle!", 
                                                    "It's dangerous to go alone. Take this.", 
                                                    "It's a secret to everybody.", 
                                                    "I feel asleep!!", 
                                                    "Let us make a jourey to the cave of monsters!", 
                                                    "A winner is you!", 
                                                    "Let's play money making game.", 
                                                    "A Slime draws near! Command?", 
                                                    "I am error.", 
                                                    "Hey, listen!", 
                                                    "Bagu is my name. Show my note to river man.", 
                                                    "What a horrible night to have a curse.", 
                                                    "All your base are belong to us.", 
                                                    "The morning sun has vanquished the horrible night.", 
                                                    "Get a silk bag from the graveyard duck to live longer.", 
                                                    "Someone set us up the bomb.", 
                                                    "Are you a bad enough dude to rescue the president?", 
                                                    "That's the second biggest monkey head I've ever seen!", 
                                                    "Now you're thinking with portals.", 
                                                    "Would you kindly?", 
                                                    "The cake is a lie.", 
                                                    "We've both said a lot of things that you're going to regret", 
                                                    "It's like my Rattata is in the top percentage of rattatas.", 
                                                    "You have died of dysentery."
                                                   };
    // @formatter:on

    public MessagePostPanel(ControlWindow ctrlWindow)
    {
        this.ctrlWindow = ctrlWindow;
        this.modCount = 0;
        rnd = new Random();
        build();
    }

    private void build()
    {
        setBorder(new TitledBorder(ControlPanelBase.getBaseBorder(), "Manually Post Message to Chat", TitledBorder.CENTER, TitledBorder.TOP));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 2, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);

        manualInfo = new JLabel("This message will be posted to the visualization only; it will not be sent to the IRC channel.");

        usernameInput = new LabeledInput("Username", 8);
        textInput = new LabeledInput("Message", 32);

        textInput.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                submit();
            }
        });

        retainMessageBox = new JCheckBox("Retain message input after post");

        submitButton = new JButton("Post");
        clearButton = new JButton("Clear");

        ActionListener al = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();
                if (submitButton.equals(source))
                {
                    submit();
                }
                else if (clearButton.equals(source))
                {
                    if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
                    {
                        if (modCount > 0)
                        {
                            usernameInput.setText(TEST_USERNAMES[rnd.nextInt(TEST_USERNAMES.length)]);
                            textInput.setText(TEST_MESSAGES[rnd.nextInt(TEST_MESSAGES.length)]);
                        }
                        else
                        {
                            usernameInput.setText("Test");
                            textInput.setText(SpriteFont.NORMAL_ASCII_KEY);
                        }
                        modCount++;
                    }
                    else
                    {
                        reset();
                    }
                }
            }
        };

        submitButton.addActionListener(al);
        clearButton.addActionListener(al);

        add(manualInfo, gbc);
        gbc.gridy++;

        gbc.gridwidth = 1;

        add(usernameInput, gbc);
        gbc.gridx++;
        add(textInput, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 0;
        gbc.gridwidth = 0;
        gbc.anchor = GridBagConstraints.WEST;
        buttonPanel.add(retainMessageBox, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.EAST;
        buttonPanel.add(submitButton, gbc);
        gbc.gridx++;
        buttonPanel.add(clearButton, gbc);
    }

    private void reset()
    {
        usernameInput.setText("");
        textInput.setText("");
        modCount = 0;
    }

    public void submit()
    {
        List<String> report = validateInput();
        if (report.isEmpty())
        {
            ctrlWindow.addManualMessage(usernameInput.getText(), textInput.getText());
            if (!retainMessageBox.isSelected())
            {
                reset();
            }
        }
        else
        {
            ChatWindow.popup.handleProblem(report);
        }
    }

    private List<String> validateInput()
    {
        List<String> errors = new ArrayList<String>();

        if (usernameInput.getText().isEmpty())
        {
            errors.add("Please enter a username under which to post");
        }

        if (textInput.getText().isEmpty())
        {
            errors.add("Please enter a message to post");
        }

        return errors;
    }

}
