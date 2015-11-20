package com.glitchcog.fontificator.bot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jibble.pircbot.PircBot;

import com.glitchcog.fontificator.config.ConfigMessage;
import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelIrc;
import com.google.gson.Gson;

/**
 * The IRC bot that handles connecting to the IRC server and receiving all the posts. It also managed username casing.
 * 
 * @author Matt Yanos
 */
public class ChatViewerBot extends PircBot
{
    private static final Logger logger = Logger.getLogger(ChatViewerBot.class);

    /**
     * Separates segments of the post, the parameters, the prefix, and the message itself, for Twitch messages
     */
    private static final String POST_SEPARATOR = " :";

    /**
     * Indicates a Client-to-Client Protocol (CTCP) message if this character is at the start of the message content.
     * There can be an optional terminating character at the end of the message.
     */
    private static final String CTCP_INDICATOR = Character.toString((char) 1);

    /**
     * The base URL for looking up username casing on the Twitch API
     */
    private static final String USERNAME_LOOKUP_BASE_URL = "https://api.twitch.tv/kraken/users/";

    /**
     * Reference to the chat panel to add messages as they're posted
     */
    private ChatPanel chat;

    /**
     * The control panel that calls to connect or disconnect the bot, stored as a member to enable or to disable buttons
     * based on successful connections
     */
    private ControlPanelIrc controlPanel;

    /**
     * Used to determine the selected username case resolution type
     */
    private ConfigMessage messageConfig;

    /**
     * A map of name casing where the key is the username in all lowercase, and the value is the correctly cased
     * username
     */
    private Map<String, String> usernameCases;

    /**
     * Map of Pvivmsg objects keyed off of a lowercase username. These user states contain the information prepended to
     * each message a user sends to the chat. The user states also contain the post count.
     */
    private Map<String, TwitchPrivmsg> privmsgs;

    /**
     * Default constructor, just initializes the username case map
     */
    public ChatViewerBot()
    {
        this.usernameCases = new HashMap<String, String>();
        this.privmsgs = new HashMap<String, TwitchPrivmsg>();
    }

    public void reset()
    {
        for (TwitchPrivmsg state : privmsgs.values())
        {
            state.resetPostCount();
        }
        usernameCases.clear();
    }

    @Override
    public void log(String line)
    {
        controlPanel.log(line);
    }

    /**
     * Empty out the cache of usernames. Used when the option of how to case usernames is changed via the Message
     * control panel tab
     */
    public void clearUsernameCases()
    {
        usernameCases.clear();
    }

    /**
     * Set the reference to the chat panel to add messages as they're posted
     * 
     * @param chat
     */
    public void setChatPanel(ChatPanel chat)
    {
        this.chat = chat;
    }

    /**
     * PircBot doesn't let you set the username if you're connected, so this checks for that
     * 
     * @param name
     */
    public void setUsername(String name)
    {
        if (!isConnected())
        {
            super.setName(name);
        }
    }

    /**
     * This method is called once the PircBot has success to the IRC server.
     * <p>
     * The implementation of this method in the PircBot abstract class performs no actions and may be overridden as
     * required.
     * 
     * @since PircBot 0.9.6
     */
    @Override
    protected void onConnect()
    {
        logger.info("Connected");
        controlPanel.toggleConnect(true);

        // Register for Twitch-specific capabilities.
        // Sending this message to a Twitch IRC server will prepend all the user posts with subscriber, emote, and other
        // information. This will prevent the onMessage method from being fired because PircBot no longer recognizes the
        // message. It will instead trigger the onUnknown method.
        sendRawLine("CAP REQ :twitch.tv/membership");
        // Enables USERSTATE, GLOBALUSERSTATE, ROOMSTATE, HOSTTARGET, NOTICE and CLEARCHAT raw commands.
        sendRawLine("CAP REQ :twitch.tv/tags");
    }

    /**
     * This method is called whenever someone (possibly us) joins a channel which we are on.
     * <p>
     * The implementation of this method in the PircBot abstract class performs no actions and may be overridden as
     * required.
     *
     * @param channel
     *            The channel which somebody joined.
     * @param sender
     *            The nick of the user who joined the channel.
     * @param login
     *            The login of the user who joined the channel.
     * @param hostname
     *            The hostname of the user who joined the channel.
     */
    @Override
    protected void onJoin(String channel, String sender, String login, String hostname)
    {
        TwitchPrivmsg privmsg = getPrivmsg(sender);
        sendMessageToChat(MessageType.JOIN, "joined " + channel + ".", privmsg);
    }

    /**
     * This method is called whenever an ACTION is sent from a user. E.g. such events generated by typing
     * "/me goes shopping" in most IRC clients.
     * <p>
     * The implementation of this method in the PircBot abstract class performs no actions and may be overridden as
     * required.
     * 
     * @param sender
     *            The nick of the user that sent the action.
     * @param login
     *            The login of the user that sent the action.
     * @param hostname
     *            The hostname of the user that sent the action.
     * @param target
     *            The target of the action, be it a channel or our nick.
     * @param action
     *            The action carried out by the user.
     */
    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action)
    {
        TwitchPrivmsg privmsg = getPrivmsg(sender);
        sendMessageToChat(MessageType.ACTION, action, privmsg);
    }

    /**
     * When the bot registers for Twitch-specific capabilities, the message will be prepended with subscriber, emote,
     * and other information. This will prevent the onMessage method from being fired because PircBot no longer
     * recognizes the message. It will instead trigger this onUnknown method.
     */
    @Override
    protected void onUnknown(String response)
    {
        if (":tmi.twitch.tv CAP * ACK :twitch.tv/membership".equals(response) || ":tmi.twitch.tv CAP * ACK :twitch.tv/tags".equals(response))
        {
            return;
        }

        try
        {
            TwitchPrivmsg privmsg = parseRawTwitchMessage(response);
            String message = response.substring(response.indexOf(POST_SEPARATOR, response.indexOf(POST_SEPARATOR) + POST_SEPARATOR.length()) + POST_SEPARATOR.length());
            if (message.startsWith(CTCP_INDICATOR))
            {
                // Remove leading character
                message = message.substring(CTCP_INDICATOR.length());
                if (message.endsWith(CTCP_INDICATOR))
                {
                    // Remove terminating character
                    message = message.substring(0, message.length() - 1);
                }
                final String commandSplit = " ";
                if (message.contains(commandSplit))
                {
                    final String command = message.substring(0, message.indexOf(commandSplit));
                    // Take the command off the message
                    message = message.substring(message.indexOf(commandSplit) + commandSplit.length());
                    if ("ACTION".equals(command))
                    {
                        sendMessageToChat(MessageType.ACTION, message, privmsg);
                    }
                    else
                    {
                        log("Unknown CTCP command: " + command);
                    }
                }
                else
                {
                    log("CTCP message missing command type: " + message);
                }
            }
            else
            {
                sendMessageToChat(MessageType.NORMAL, message, privmsg);
            }
        }
        catch (Exception e)
        {
            log("Unparsable: " + response);
        }
    }

    /**
     * Get the TwitchPrivmsg object from the map, or add a newly instantiated one to the map and return it
     * 
     * @param sender
     * @return privmsg
     */
    private TwitchPrivmsg getPrivmsg(String sender)
    {
        TwitchPrivmsg privmsg = privmsgs.get(sender.toLowerCase());
        if (privmsg == null)
        {
            privmsg = new TwitchPrivmsg(sender);
            privmsgs.put(sender.toLowerCase(), privmsg);
        }
        return privmsg;
    }

    /**
     * Turn a raw message post containing the Twitch header information into a TwitchPrivmsg object
     * 
     * @param rawMessage
     * @return privmsg
     */
    private TwitchPrivmsg parseRawTwitchMessage(String rawMessage)
    {
        int firstBreak = rawMessage.indexOf(POST_SEPARATOR);
        int secondBreak = rawMessage.indexOf(POST_SEPARATOR, firstBreak + POST_SEPARATOR.length());

        // Custom Twitch message parameters:
        final int startIndex = rawMessage.length() > 1 && rawMessage.charAt(0) == '@' ? 1 : 0;
        String[] params = rawMessage.substring(startIndex, firstBreak).split(";");
        Map<String, String> paramMap = new HashMap<String, String>();
        final String paramSplitter = "=";

        TwitchPrivmsg privmsg = new TwitchPrivmsg();

        for (int i = 0; i < params.length; i++)
        {
            String[] paramHalves = params[i].split(paramSplitter);
            final String key = paramHalves[0];
            String value = "";
            for (int v = 1; v < paramHalves.length; v++)
            {
                value += paramHalves[v] + (v > 1 ? paramSplitter : "");
            }
            paramMap.put(key, value);
        }

        String colorStr = paramMap.get("color");
        if (colorStr != null && !colorStr.trim().isEmpty())
        {
            final String hexString = colorStr.substring((colorStr.startsWith("#") ? 1 : 0) + (colorStr.startsWith("0x") ? 2 : 0));
            Color color = new Color(Integer.parseInt(hexString, 16));
            privmsg.setColor(color);
        }
        String displayName = paramMap.get("display-name");
        if (displayName != null && !displayName.trim().isEmpty())
        {
            privmsg.setDisplayName(displayName);
        }
        String emotesStr = paramMap.get("emotes");
        if (emotesStr != null && !emotesStr.trim().isEmpty())
        {
            try
            {
                for (String eaiStr : emotesStr.split("/"))
                {
                    String[] idIndexSplit = eaiStr.split(":");
                    Integer emoteId = "null".equals(idIndexSplit[0]) ? null : Integer.parseInt(idIndexSplit[0]);
                    final String indices = idIndexSplit[1];
                    String[] indicesStr = indices.split(",");
                    for (int i = 0; i < indicesStr.length; i++)
                    {
                        String[] begEndSplit = indicesStr[i].split("-");

                        int beg = Integer.parseInt(begEndSplit[0]);
                        int end = Integer.parseInt(begEndSplit[1]);

                        EmoteAndIndices eai = new EmoteAndIndices(emoteId, beg, end);
                        privmsg.addEmote(eai);
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("Something went wrong parsing the message emote data: " + emotesStr, e);
            }
        }
        String subStr = paramMap.get("subscriber");
        if (subStr != null && !subStr.trim().isEmpty())
        {
            try
            {
                int subVal = Integer.parseInt(subStr);
                privmsg.setSubscriber(subVal > 0);
            }
            catch (Exception e)
            {
                log("Error parsing subscriber value \"" + subStr + "\" in Twitch header");
            }
        }
        String turboStr = paramMap.get("turbo");
        if (turboStr != null && !turboStr.trim().isEmpty())
        {
            try
            {
                int turboVal = Integer.parseInt(turboStr);
                privmsg.setTurbo(turboVal > 0);
            }
            catch (Exception e)
            {
                log("Error parsing subscriber value \"" + turboStr + "\" in Twitch header");
            }
        }
        String userTypeStr = paramMap.get("user-type");
        if (userTypeStr != null && !userTypeStr.trim().isEmpty())
        {
            // If the string value is something weird, the enum will just return NONE
            privmsg.setUserType(UserType.getByKey(userTypeStr));
        }

        // message prefix: <servername> | <nick> [ '!' <user> ] [ '@' <host> ]
        String prefix = rawMessage.substring(firstBreak + POST_SEPARATOR.length(), secondBreak);

        if (displayName == null || displayName.trim().isEmpty())
        {
// Unparsable: @color=;display-name=;emotes=;subscriber=0;turbo=0;user-id=61077493;user-type= :andyofs44!andyofs44@andyofs44.tmi.twitch.tv PRIVMSG #dansgaming :they look sweet
            privmsg.setDisplayName(prefix.substring(0, prefix.indexOf("!")));
            
        }

        return privmsg;
    }

    /**
     * Get the TwitchPrivmsg for a given sender and or raw message. This method processes the Twitch header text of a
     * message and turns it into a TwitchPrivmsg object.
     * 
     * @param sender
     *            The username of the user who sent the message. This value will be used in lowercase as the key, and it
     *            will also be used to construct a TwitchPrivmsg object, so if the casing is incorrect, it will need to
     *            be fixed before the message is displayed.
     * @param rawMessage
     *            Can be either the raw message for parsing into a user state, or null
     * @return
     */
    private TwitchPrivmsg getPrivmsg(String sender, String rawMessage)
    {
        sender = sender == null ? null : sender.toLowerCase();
        TwitchPrivmsg privmsg = privmsgs.get(sender.toLowerCase());
        if (privmsg == null)
        {
            privmsg = new TwitchPrivmsg(sender);
            if (rawMessage != null)
            {
            }
            privmsgs.put(sender.toLowerCase(), privmsg);
        }
        return privmsg;
    }

    /**
     * This method is called whenever a message is sent to a channel.
     * <p>
     * The implementation of this method in the PircBot abstract class performs no actions and may be overridden as
     * required.
     *
     * @param channel
     *            The channel to which the message was sent.
     * @param sender
     *            The nick of the person who sent the message.
     * @param login
     *            The login of the person who sent the message.
     * @param hostname
     *            The hostname of the person who sent the message.
     * @param message
     *            The actual message sent to the channel.
     */
    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        TwitchPrivmsg privmsg = getPrivmsg(sender, null);
        sendMessageToChat(message, privmsg);
    }

    /**
     * Post a message to chat with just a username and message content, which defaults to a NORMAL type message
     * 
     * @param username
     * @param message
     * @param privmsg
     */
    public void sendMessageToChat(String message, TwitchPrivmsg privmsg)
    {
        sendMessageToChat(MessageType.NORMAL, message, privmsg);
    }

    /**
     * Post a message to chat, specifying the message type, username, and message content
     * 
     * @param type
     * @param username
     * @param message
     * @param privmsg
     */
    public void sendMessageToChat(MessageType type, String message, TwitchPrivmsg privmsg)
    {
        String casedUsername = handleUsernameCasing(type, privmsg.getDisplayName(), message);

        // Update privmsg with name and post count increment
        privmsg.setDisplayName(casedUsername);
        privmsg.incrementPostCount();

        // Finally, construct the message and send it on to the chat display
        Message msg = new Message(type, casedUsername, message, privmsg);
        chat.addMessage(msg);
    }

    /**
     * Handles custom username casing
     * 
     * @param type
     * @param username
     * @param message
     * @return the appropriately cased username
     */
    private String handleUsernameCasing(MessageType type, String username, String message)
    {
        String casedUsername = username;
        final String lowerCaseUsername = username.toLowerCase();

        if (type != MessageType.JOIN)
        {
            if (messageConfig.isSpecifyCaseAllowed())
            {
                if (message.toLowerCase().contains(lowerCaseUsername))
                {
                    // Run a quick regex find to make sure the username is not inside another word
                    Pattern pat = Pattern.compile("\\b" + lowerCaseUsername + "\\b");
                    Matcher mtch = pat.matcher(message.toLowerCase());
                    if (mtch.find())
                    {
                        final int usernameIndex = message.toLowerCase().indexOf(lowerCaseUsername);
                        casedUsername = message.substring(usernameIndex, usernameIndex + username.length());
                        usernameCases.put(lowerCaseUsername, casedUsername);
                    }
                }
            }

            if (!usernameCases.containsKey(lowerCaseUsername))
            {
                switch (messageConfig.getCaseResolutionType())
                {
                case ALL_CAPS:
                    casedUsername = username.toUpperCase();
                    break;
                case ALL_LOWERCASE:
                    casedUsername = lowerCaseUsername;
                    break;
                case FIRST:
                    casedUsername = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
                    break;
                case LOOKUP:
                    if (type.containsParsableUsername())
                    {
                        try
                        {
                            URL url = new URL(USERNAME_LOOKUP_BASE_URL + lowerCaseUsername);
                            URLConnection conn = url.openConnection();
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                            String jsonResult = "";
                            String line;
                            while ((line = br.readLine()) != null)
                            {
                                jsonResult += line;
                            }
                            br.close();

                            casedUsername = (String) new Gson().fromJson(jsonResult, Map.class).get("display_name");
                        }
                        catch (Exception e)
                        {
                            logger.error("Attempt to look up " + username + " on Twitch API failed.");
                        }
                    }
                    break;
                case NONE:
                default:
                    casedUsername = username;
                    break;
                }
                usernameCases.put(lowerCaseUsername, casedUsername);
            }

            if (usernameCases.containsKey(lowerCaseUsername))
            {
                casedUsername = usernameCases.get(lowerCaseUsername);
            }
        }

        return casedUsername;
    }

    /**
     * This method is called whenever a private message is sent to the PircBot.
     * <p>
     * The implementation of this method in the PircBot abstract class performs no actions and may be overridden as
     * required.
     *
     * @param sender
     *            The nick of the person who sent the private message.
     * @param login
     *            The login of the person who sent the private message.
     * @param hostname
     *            The hostname of the person who sent the private message.
     * @param message
     *            The actual message.
     */
    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message)
    {
        logger.info("Private message from " + sender + "(" + login + "@" + hostname + "): " + message);
    }

    @Override
    protected void onSetChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask)
    {
        chat.banUser(hostmask);
    }

    @Override
    protected void onRemoveChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String hostmask)
    {
        chat.unbanUser(hostmask);
    }

    /**
     * This method carries out the actions to be performed when the PircBot gets disconnected. This may happen if the
     * PircBot quits from the server, or if the connection is unexpectedly lost.
     * <p>
     * Disconnection from the IRC server is detected immediately if either we or the server close the connection
     * normally. If the connection to the server is lost, but neither we nor the server have explicitly closed the
     * connection, then it may take a few minutes to detect (this is commonly referred to as a "ping timeout").
     * <p>
     * If you wish to get your IRC bot to automatically rejoin a server after the connection has been lost, then this is
     * probably the ideal method to override to implement such functionality.
     * <p>
     * The implementation of this method in the PircBot abstract class performs no actions and may be overridden as
     * required.
     */
    @Override
    protected void onDisconnect()
    {
        logger.info("Disconnected");
        controlPanel.toggleConnect(false);
    }

    /**
     * Set a reference to the control panel to update the connect/disconnect button text and log events on the
     * Connection tab
     * 
     * @param controlPanel
     */
    public void setControlPanel(ControlPanelIrc controlPanel)
    {
        this.controlPanel = controlPanel;
    }

    /**
     * Set a reference to the message config, used to determine the current username casing resolution type
     * 
     * @param messageConfig
     */
    public void setMessageConfig(ConfigMessage messageConfig)
    {
        this.messageConfig = messageConfig;
    }
}
