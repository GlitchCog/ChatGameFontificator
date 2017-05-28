package com.glitchcog.fontificator.bot;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * A TwitchPrivmsg model to house the data that comes in the header of a Twitch IRC message. Could also be a whisper
 * message, which is specified by the messageClassification member variable.
 * 
 * @author Matt Yanos
 */
public class TwitchPrivmsg
{
    /**
     * The number of posts for this user this connection session, used to optionally censor first posts that contain
     * URLs in the MessageCensorPanel
     */
    private int postCount;

    /**
     * The color in which to display the username
     */
    private Color color;

    /**
     * The name to display on the message
     */
    private String displayName;

    /**
     * The list of emotes
     */
    private Map<Integer, EmoteAndIndices> emotes;

    /**
     * The list of badges
     */
    private ArrayList<String> badges;

    /**
     * Whether the user has a Twitch subscription to the channel to which the message is posted
     */
    private boolean subscriber;

    /**
     * Whether the user has a Twitch turbo badge
     */
    private boolean turbo;

    /**
     * Whether the user has a Twitch prime badge
     */
    private boolean prime;

    /**
     * The type of the user, for example channel moderator
     */
    private UserType userType;

    /**
     * The class of message, for example PRIVMSG or WHISPER
     */
    private String messageClassification;

    /**
     * Default constructor that sets default values for everything but displayName
     */
    public TwitchPrivmsg()
    {
        setDefaults();
    }

    /**
     * Constructor sets default values for members other than displayName
     * 
     * @param displayName
     */
    public TwitchPrivmsg(String displayName)
    {
        this.displayName = displayName;
        setDefaults();
    }

    /**
     * Set default values for all the members other than the displayName
     */
    public void setDefaults()
    {
        postCount = 0;
        color = null;
        emotes = new HashMap<Integer, EmoteAndIndices>();
        badges = new ArrayList<String>();
        turbo = false;
        prime = false;
        subscriber = false;
        userType = UserType.NONE;
    }

    /**
     * Get the number of posts for this user this connection session, used to optionally censor first posts that contain
     * URLs in the MessageCensorPanel
     * 
     * @return postCount
     */
    public int getPostCount()
    {
        return postCount;
    }

    /**
     * Increment the number of posts for this user this connection session
     */
    public void incrementPostCount()
    {
        postCount++;
    }

    /**
     * Rest the nubmer of posts to zero for this user, used when a new session is opened
     */
    public void resetPostCount()
    {
        postCount = 0;
    }

    /**
     * Get the color in which to display the username
     * 
     * @return color
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Set the color in which to display the username
     * 
     * @param color
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Get the name to display on the message
     * 
     * @return displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Set the name to display on the message
     * 
     * @param displayName
     */
    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * Get the map of emotes, keyed off of character index of the message
     * 
     * @return
     */
    public Map<Integer, EmoteAndIndices> getEmotes()
    {
        return emotes;
    }

    /**
     * Add to the list of emotes
     * 
     * @param emoteSet
     */
    public void addEmote(EmoteAndIndices emoteSet)
    {
        emotes.put(emoteSet.getBegin(), emoteSet);
    }

    public ArrayList<String> getBadges() {
        return badges;
    }

    public void addBadges(String newbadges[]) {
        this.badges.addAll( Arrays.asList(newbadges) );
    }

    /**
     * Get whether the user has a Twitch subscription to the channel to which the message is posted
     * 
     * @return subscriber
     */
    public boolean isSubscriber()
    {
        return subscriber;
    }

    /**
     * Set whether the user has a Twitch subscription to the channel to which the message is posted
     * 
     * @param subscriber
     */
    public void setSubscriber(boolean subscriber)
    {
        this.subscriber = subscriber;
    }

    /**
     * Get whether the user has a Twitch turbo badge
     * 
     * @return turbo
     */
    public boolean isTurbo()
    {
        return turbo;
    }

    /**
     * Set whether the user has a Twitch turbo badge
     * 
     * @param turbo
     */
    public void setTurbo(boolean turbo)
    {
        this.turbo = turbo;
    }

    /**
     * Get whether the user has a Twitch prime badge
     * 
     * @return prime
     */
    public boolean isPrime()
    {
        return prime;
    }

    /**
     * Set whether the user has a Twitch prime badge
     * 
     * @param prime
     */
    public void setPrime(boolean prime)
    {
        this.prime = prime;
    }

    /**
     * Get the type of the user, for example channel moderator
     * 
     * @return userType
     */
    public UserType getUserType()
    {
        return userType;
    }

    /**
     * Set the type of the user, for example channel moderator
     * 
     * @param userType
     */
    public void setUserType(UserType userType)
    {
        this.userType = userType;
    }

    /**
     * Set the messageClassification
     * 
     * @param messageClassification
     */
    public void setMessageClassification(String messageClassification)
    {
        this.messageClassification = messageClassification;
    }

    /**
     * Get whether the message is a PRIVMSG to be displayed in chat, and specifically not a WHISPER message which should
     * not be shown
     * 
     * @return whether the message should be displayed in chat
     */
    public boolean isDisplayMessage()
    {
        return "PRIVMSG".equalsIgnoreCase(messageClassification);
    }

}
