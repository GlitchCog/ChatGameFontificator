package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.URI;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import com.glitchcog.fontificator.bot.ChatViewerBot;
import com.glitchcog.fontificator.config.ConfigIrc;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
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

    private JCheckBox anonymous;

    private LabeledInput authInput;

    private JButton authHelpButton;

    private LabeledInput chanInput;

    private JButton clearChatButton;

    private JCheckBox autoReconnectBox;

    private ConfigIrc config;

    /**
     * Reference to Emoji Control Panel to load emotes on connection
     */
    private ControlPanelEmoji emojiControl;

    /**
     * Construct an IRC (Connection) control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param bot
     * @param logBox
     */
    public ControlPanelIrc(FontificatorProperties fProps, ChatWindow chatWindow, ControlPanelEmoji emojiControl, ChatViewerBot bot, LogBox logBox)
    {
        super("Connection", fProps, chatWindow, logBox);
        this.emojiControl = emojiControl;
        this.bot = bot;
        this.bot.setControlPanel(this);
    }

    /**
     * No validation is done internally by this function when run, so ensure all input is safe before calling
     * 
     * @throws NumberFormatException
     * @throws NickAlreadyInUseException
     * @throws IOException
     * @throws IrcException
     * @throws Exception
     */
    private void connect() throws NumberFormatException, NickAlreadyInUseException, IOException, IrcException, Exception
    {
        anonymous.setEnabled(false);
        fillConfigFromInput();

        final boolean anon = config.isAnonymous();

        logBox.setAuthCode(config.getAuthorization());
        String user = config.getUsername();
        String host = config.getHost();
        int port = Integer.parseInt(config.getPort());
        String auth = config.getAuthorization();

        if (anon)
        {
            String rndNumStr = "" + new Random().nextInt(99999);
            while (rndNumStr.length() < 5)
            {
                rndNumStr = "0" + rndNumStr;
            }
            user = "justinfan" + rndNumStr;
        }

        bot.setUsername(user);
        logger.trace("Attempting to connect " + user + " to " + host + ":" + port);
        bot.reset();

        if (anon)
        {
            bot.connect(host, port);
        }
        else
        {
            bot.connect(host, port, auth);
        }

        joinChannel();
    }

    /**
     * Join the specified channel for after a connection is made
     */
    public void joinChannel()
    {
        final String host = config.getHost();
        String connectChannel = config.getChannel();

        // Force lowercase channel names for twitch.tv
        if ("irc.twitch.tv".equals(host))
        {
            connectChannel = connectChannel.toLowerCase();
        }

        bot.joinChannel(connectChannel);
    }

    private LoadConfigReport validateInputForConnect()
    {
        // This call does nothing
        LoadConfigReport report = validateInput();

        if (userInput.getText().isEmpty() && !anonymous.isSelected())
        {
            report.addError("An input value for Username is required", LoadConfigErrorType.MISSING_VALUE);
        }

        if (authInput.getText().trim().isEmpty() && !anonymous.isSelected())
        {
            report.addError("An input value for the OAuth Token is required", LoadConfigErrorType.MISSING_VALUE);
        }

        if (chanInput.getText().isEmpty())
        {
            report.addError("An input value for the Channel is required", LoadConfigErrorType.MISSING_VALUE);
        }

        if (hostInput.getText().trim().isEmpty())
        {
            report.addError("An input value for Host is required", LoadConfigErrorType.MISSING_VALUE);
        }

        if (portInput.getText().isEmpty())
        {
            report.addError("An input value for the Port is required", LoadConfigErrorType.MISSING_VALUE);
        }
        else
        {
            try
            {
                int port = Integer.parseInt(portInput.getText());
                if (port < 0 || port > 65535)
                {
                    report.addError("Port value is out of range", LoadConfigErrorType.VALUE_OUT_OF_RANGE);
                }
            }
            catch (Exception e)
            {
                report.addError("Port value must be a valid integer", LoadConfigErrorType.PARSE_ERROR_INT);
            }
        }

        return report;
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

        anonymous = new JCheckBox("Read Only (Credentials not required)");
        anonymous.setToolTipText("Connect without credentials, but also without access to custom Twitch badges");

        anonymous.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                userInput.setEnabled(!anonymous.isSelected());
                authInput.setEnabled(!anonymous.isSelected());
                config.setAnonymous(anonymous.isSelected());
            }
        });

        FocusListener fl = new FocusListener()
        {
            @Override
            public void focusGained(FocusEvent e)
            {
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                // The user was in the input field, and then clicked on something other than the input field, so update
                // the configuration
                try
                {
                    fillConfigFromInput();
                }
                catch (Exception ex)
                {
                    logger.trace(ex.toString(), ex);
                }
            }
        };

        userInput.addFocusListener(fl);
        authInput.addFocusListener(fl);
        chanInput.addFocusListener(fl);
        hostInput.addFocusListener(fl);
        portInput.addFocusListener(fl);

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
                    bot.setDisconnectExpected(true);
                    bot.disconnect();
                }
                else
                {

                    try
                    {
                        LoadConfigReport report = validateInputForConnect();

                        if (report.isErrorFree())
                        {
                            // Connect to the IRC channel
                            connect();
                            emojiControl.loadAndRunEmojiWork();
                        }
                        else
                        {
                            ChatWindow.popup.handleProblem(report);
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        ChatWindow.popup.handleProblem("Invalid login port value", ex);
                    }
                    catch (NickAlreadyInUseException ex)
                    {
                        ChatWindow.popup.handleProblem("Nickname already in use", ex);
                    }
                    catch (IOException ex)
                    {
                        ChatWindow.popup.handleProblem("Error connecting to the IRC server. Verify the Internet connection and then the host and port values.", ex);
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

        autoReconnectBox = new JCheckBox("Automatically attempt to reconnect if connection is lost");
        autoReconnectBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                config.setAutoReconnect(autoReconnectBox.isSelected());
            }
        });

        JPanel everything = new JPanel(new GridBagLayout());

        everything.setBorder(new TitledBorder(baseBorder, "IRC Connection Properties / Clear Chat", TitledBorder.CENTER, TitledBorder.TOP));

        JPanel topRow = new JPanel(new GridBagLayout());
        JPanel midRow = new JPanel(new GridBagLayout());
        JPanel botRow = new JPanel(new GridBagLayout());

        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        topRow.add(anonymous, gbc);
        gbc.gridx++;
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
        everything.add(autoReconnectBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        add(everything, gbc);

        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.BOTH;
        add(logBox, gbc);
    }

    public void toggleConnect(boolean connected)
    {
        userInput.setEnabled(!anonymous.isSelected() && !connected);
        authInput.setEnabled(!anonymous.isSelected() && !connected);
        chanInput.setEnabled(!connected);
        hostInput.setEnabled(!connected);
        portInput.setEnabled(!connected);
        anonymous.setEnabled(!connected);

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
        anonymous.setSelected(config.isAnonymous());

        logBox.setAuthCode(config.getAuthorization());

        hostInput.setText(config.getHost());
        portInput.setText(config.getPort());

        autoReconnectBox.setSelected(config.isAutoReconnect());

        userInput.setEnabled(!anonymous.isSelected());
        authInput.setEnabled(!anonymous.isSelected());
    }

    @Override
    protected LoadConfigReport validateInput()
    {
        // Nothing to check because all IRC connection fields are optional
        return new LoadConfigReport();
    }

    @Override
    public void fillConfigFromInput() throws Exception
    {
        config.setUsername(userInput.getText());
        config.setAuthorization(authInput.getText());
        config.setAnonymous(anonymous.isSelected());
        config.setChannel(chanInput.getText());

        config.setHost(hostInput.getText());
        config.setPort(portInput.getText());

        config.setAutoReconnect(autoReconnectBox.isSelected());
    }

    public void log(String line)
    {
        logBox.log(line);
    }

    /**
     * This value is exposed so the ChatViewerBot that has a reference to this object can access the current no-hash
     * version of the channel value. It uses it to determine whether to give a user the broadcaster badge because Twitch
     * does not set that usertype in its IRC tags.
     * 
     * @return channel, no hash ('#')
     */
    public String getChannelNoHash()
    {
        return config.getChannelNoHash();
    }

    public boolean isAutoReconnect()
    {
        return autoReconnectBox.isSelected();
    }
}
