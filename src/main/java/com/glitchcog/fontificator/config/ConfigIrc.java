package com.glitchcog.fontificator.config;

import java.util.Properties;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;

/**
 * The configuration for the IRC Connection
 * 
 * @author Matt Yanos
 */
public class ConfigIrc extends Config
{
    private String username;

    private String host;

    private String port;

    private String authorization;

    private String channel;

    private Boolean autoReconnect;

    @Override
    public void reset()
    {
        username = null;
        host = null;
        port = null;
        authorization = null;
        channel = null;
        setAutoReconnect(null);
    }

    @Override
    public LoadConfigReport load(Properties props, LoadConfigReport report)
    {
        this.props = props;

        reset();

        // No validation here because these values are not required to be saved

        final String propUser = props.getProperty(FontificatorProperties.KEY_IRC_USER);
        final String propAuth = props.getProperty(FontificatorProperties.KEY_IRC_AUTH);
        final String propChan = props.getProperty(FontificatorProperties.KEY_IRC_CHAN);
        final String propHost = props.getProperty(FontificatorProperties.KEY_IRC_HOST);
        final String propPort = props.getProperty(FontificatorProperties.KEY_IRC_PORT);

        if (propUser != null && !propUser.isEmpty())
        {
            this.username = propUser;
        }

        if (propAuth != null && !propAuth.isEmpty())
        {
            this.authorization = propAuth;
        }

        if (propChan != null && !propChan.isEmpty())
        {
            this.channel = propChan;
        }

        if (propHost != null && !propHost.isEmpty())
        {
            this.host = propHost;
        }

        if (propPort != null && !propPort.isEmpty())
        {
            this.port = propPort;
        }

        setAutoReconnect(!Boolean.FALSE.toString().equalsIgnoreCase(props.getProperty(FontificatorProperties.KEY_IRC_AUTO_RECONNECT)));

        return report;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
        props.setProperty(FontificatorProperties.KEY_IRC_USER, username);
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
        props.setProperty(FontificatorProperties.KEY_IRC_HOST, host);

    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
        props.setProperty(FontificatorProperties.KEY_IRC_PORT, port);
    }

    public String getAuthorization()
    {
        return authorization;
    }

    public void setAuthorization(String authorization)
    {
        this.authorization = authorization;
        props.setProperty(FontificatorProperties.KEY_IRC_AUTH, authorization);
    }

    public String getChannel()
    {
        if (channel != null && !channel.startsWith("#"))
        {
            return "#" + channel;
        }
        else
        {
            return channel;
        }
    }

    public String getChannelNoHash()
    {
        return getChannel() == null ? null : getChannel().length() < 1 ? "" : getChannel().substring(1);
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
        props.setProperty(FontificatorProperties.KEY_IRC_CHAN, channel);
    }

    public Boolean isAutoReconnect()
    {
        return autoReconnect;
    }

    public void setAutoReconnect(Boolean autoReconnect)
    {
        this.autoReconnect = autoReconnect;
        if (autoReconnect != null)
        {
            props.setProperty(FontificatorProperties.KEY_IRC_AUTO_RECONNECT, Boolean.toString(autoReconnect));
        }
    }

}
