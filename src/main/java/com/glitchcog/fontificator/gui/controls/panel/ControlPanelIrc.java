package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.glitchcog.fontificator.bot.ChatViewerBot;
import com.glitchcog.fontificator.config.ConfigIrc;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledInput;

/**
 * Control Panel containing all the IRC connection options
 * 
 * @author Matt Yanos
 */
public class ControlPanelIrc extends ControlPanelBase
{
    private static final Logger logger = Logger.getLogger(ControlPanelIrc.class);

    private static final long serialVersionUID = 1L;

    private static final String BUTTON_TEXT_CONNECT = "Connect";

    private static final String BUTTON_TEXT_DISCONNECT = "Disconnect";

    private ChatViewerBot bot;

    private JButton connectButton;

    private LabeledInput userInput;

    private LabeledInput hostInput;

    private LabeledInput portInput;

    private LabeledInput authInput;

    private JButton authHelpButton;

    private LabeledInput chanInput;

    private JButton clearChatButton;

    private JTextArea output;

    private JScrollPane outputScroll;

    private ConfigIrc config;

    /**
     * Construct an IRC (Connection) control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param bot
     */
    public ControlPanelIrc(FontificatorProperties fProps, ChatWindow chatWindow, ChatViewerBot bot)
    {
        super("Connection", fProps, chatWindow);
        this.bot = bot;
        this.bot.setControlPanel(this);
    }

    private void connect() throws NumberFormatException, NickAlreadyInUseException, IOException, IrcException, Exception
    {
        List<String> errors = validateInputForConnect();

        if (errors.isEmpty())
        {
            fillConfigFromInput();
            output.setText("");
            String user = config.getUsername();
            String host = config.getHost();
            int port = Integer.parseInt(config.getPort());
            String auth = config.getAuthorization();
            bot.setUsername(user);
            logger.trace("Attempting to connect " + user + " to " + host + ":" + port);
            bot.connect(host, port, auth);

            // Force lowercase channel names for twitch.tv
            String connectChannel = config.getChannel();
            if ("irc.twitch.tv".equals(host))
            {
                connectChannel = connectChannel.toLowerCase();
            }

            bot.joinChannel(connectChannel);
        }
        else
        {
            ChatWindow.popup.handleProblem(errors);
        }
    }

    private List<String> validateInputForConnect()
    {
        // This call does nothing
        List<String> errors = validateInput();

        if (userInput.getText().isEmpty())
        {
            errors.add("An input value for Username is required");
        }

        if (authInput.getText().trim().isEmpty())
        {
            errors.add("An input value for the OAuth Token is required");
        }

        if (chanInput.getText().isEmpty())
        {
            errors.add("An input value for the channel is required");
        }

        if (hostInput.getText().trim().isEmpty())
        {
            errors.add("An input value for Host is required");
        }

        if (portInput.getText().isEmpty())
        {
            errors.add("An input value for the port is required");
        }
        else
        {
            try
            {
                int port = Integer.parseInt(portInput.getText());
                if (port < 0 || port > 65535)
                {
                    errors.add("Port value is out of range");
                }
            }
            catch (Exception e)
            {
                errors.add("Port value must be a valid integer");
            }
        }

        return errors;
    }

    @Override
    protected void build()
    {
        userInput = new LabeledInput("Username", 11);
        authInput = new LabeledInput("OAuth Token", true, 25);
        authHelpButton = new JButton("Get OAuth Token");
        chanInput = new LabeledInput("Channel", 11);

        hostInput = new LabeledInput("Host", 7);
        portInput = new LabeledInput("Port", 3);

        authHelpButton.addActionListener(new ActionListener()
        {
            final String url = "http://www.twitchapps.com/tmi/";

            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    Desktop.getDesktop().browse(URI.create(url));
                }
                catch (java.io.IOException ex)
                {
                    ChatWindow.popup.handleProblem("Unable to open website at URL: " + url);
                }
            }
        });

        connectButton = new JButton(BUTTON_TEXT_CONNECT);
        connectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();

                // Reset it just in case
                source.setText(bot.isConnected() ? BUTTON_TEXT_DISCONNECT : BUTTON_TEXT_CONNECT);

                if (bot.isConnected())
                {
                    bot.disconnect();
                }
                else
                {
                    try
                    {
                        connect();
                    }
                    catch (NumberFormatException ex)
                    {
                        ChatWindow.popup.handleProblem("Invalid login information", ex);
                    }
                    catch (NickAlreadyInUseException ex)
                    {
                        ChatWindow.popup.handleProblem("Nickname already in use", ex);
                    }
                    catch (IOException ex)
                    {
                        ChatWindow.popup.handleProblem("Error connecting to the IRC server. Verify the host and port values.", ex);
                    }
                    catch (IrcException ex)
                    {
                        ChatWindow.popup.handleProblem("The host IRC server rejected the connection", ex);
                    }
                    catch (Exception ex)
                    {
                        ChatWindow.popup.handleProblem("Unanticipated error connecting", ex);
                    }
                }
            }
        });

        clearChatButton = new JButton("Clear Chat");

        clearChatButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                chat.clearChat();
                chat.repaint();
            }
        });

        JPanel everything = new JPanel(new GridBagLayout());

        everything.setBorder(new TitledBorder(baseBorder, "IRC Connection Properties / Clear Chat", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel topRow = new JPanel(new GridBagLayout());
        JPanel midRow = new JPanel(new GridBagLayout());
        JPanel botRow = new JPanel(new GridBagLayout());

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        topRow.add(userInput, gbc);
        gbc.gridx++;
        topRow.add(chanInput, gbc);
        gbc.gridx++;

        gbc.gridx = 0;
        midRow.add(authInput, gbc);
        gbc.gridx++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        midRow.add(authHelpButton, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        botRow.add(hostInput, gbc);
        gbc.gridx++;
        botRow.add(portInput, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        botRow.add(connectButton, gbc);
        gbc.gridx++;
        botRow.add(clearChatButton, gbc);
        gbc.gridx++;

        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0;
        everything.add(topRow, gbc);
        gbc.gridy++;
        everything.add(midRow, gbc);
        gbc.gridy++;
        everything.add(botRow, gbc);
        gbc.gridy++;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        add(everything, gbc);

        output = new JTextArea();
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setWrapStyleWord(true);
        output.setLineWrap(true);
        output.setEditable(false);
        output.setBackground(getBackground());

        outputScroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.BOTH;
        add(outputScroll, gbc);
    }

    public void toggleConnect(boolean connected)
    {
        userInput.setEnabled(!connected);
        authInput.setEnabled(!connected);
        chanInput.setEnabled(!connected);
        hostInput.setEnabled(!connected);
        portInput.setEnabled(!connected);

        connectButton.setText(connected ? "Disconnect" : "Connect");
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        config = fProps.getIrcConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        userInput.setText(config.getUsername());
        chanInput.setText(config.getChannel());
        authInput.setText(config.getAuthorization());

        hostInput.setText(config.getHost());
        portInput.setText(config.getPort());
    }

    @Override
    protected List<String> validateInput()
    {
        List<String> errors = new ArrayList<String>();
        return errors;
    }

    @Override
    public void fillConfigFromInput() throws Exception
    {
        config.setUsername(userInput.getText());
        config.setAuthorization(authInput.getText());
        config.setChannel(chanInput.getText());

        config.setHost(hostInput.getText());
        config.setPort(portInput.getText());
    }

    public void log(String line)
    {
        if (line.contains(config.getAuthorization()))
        {
            String blocks = "";
            for (int i = 0; i < config.getAuthorization().length(); i++)
            {
                blocks += "*";
            }
            line = line.replaceAll(config.getAuthorization(), blocks);
        }
        output.append((output.getText().isEmpty() ? "" : "\n") + line);
        output.setCaretPosition(output.getDocument().getLength());
    }
}
