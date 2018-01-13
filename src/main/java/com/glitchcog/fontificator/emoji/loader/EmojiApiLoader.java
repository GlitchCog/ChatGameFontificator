package com.glitchcog.fontificator.emoji.loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.emoji.EmojiType;

/**
 * Loads emoji JSON information from an API
 * 
 * @author Matt Yanos
 */
public class EmojiApiLoader
{
    private static final Logger logger = Logger.getLogger(EmojiApiLoader.class);

    private static final String CHANNEL_NAME_REPLACE = "%CHANNEL_NAME%";

    public static final String OAUTH_REPLACE = "%OAUTH%";

    public static final String EMOTE_ID_REPLACE = "%EMOTE_ID%";

    public static final String EMOTE_SIZE_REPLACE = "%EMOTE_SIZE%";

    /**
     * The URL template for loading V1 Twitch emotes. The ID in this URL is the emote ID value in the IRC tag prepended
     * to a post to the Twitch IRC server.
     */
    public static final String TWITCH_EMOTE_ID_V1_URL = "http://static-cdn.jtvnw.net/emoticons/v1/" + EMOTE_ID_REPLACE + "/" + EMOTE_SIZE_REPLACE;

    /**
     * The base URL for getting the channel specific Twitch badges from the API
     */
    private static final String TWITCH_URL_BADGES = "https://api.twitch.tv/kraken/chat/" + CHANNEL_NAME_REPLACE + "/badges" + OAUTH_REPLACE;

    /**
     * The URL for getting the global FrankerFaceZ emotes from the API
     */
    private static final String FFZ_GLOBAL_URL = "https://api.frankerfacez.com/v1/set/global";

    /**
     * The base URL for getting the FrankerFaceZ emotes for whatever username is appended to the end from the API
     */
    private static final String FFZ_BASE_URL = "https://api.frankerfacez.com/v1/room/" + CHANNEL_NAME_REPLACE;

    /**
     * FFZ API for all badges, or just supporters can be found at http://api.frankerfacez.com/v1/badge/supporter
     */
    private static final String FFZ_BADGES = "http://api.frankerfacez.com/v1/badges";

    /**
     * The URL for getting the global BetterTTV emotes from the API
     */
    private static final String BTTV_GLOBAL_URL = "https://api.betterttv.net/2/emotes";

    /**
     * The base URL for getting the BetterTTV emotes for whatever username is appended to the end from the API
     */
    private static final String BTTV_BASE_URL = "https://api.betterttv.net/2/channels/" + CHANNEL_NAME_REPLACE;

    private static final int BUFFER_SIZE = 512;

    private final char[] buffer;

    private StringBuilder jsonStringBuilder;

    private String url;

    private float jsonLength;

    private BufferedReader reader;

    private boolean loadComplete;

    /**
     * Get the URL for a V1 Twitch emote URL with the default size. The specified ID is the value given by the IRC tags
     * prepended onto posts from the Twitch server
     * 
     * @param emoteId
     *            The ID of the desired emote.
     * @return URL
     */
    public static String getTwitchEmoteV1Url(Integer emoteId)
    {
        return getTwitchEmoteV1Url(emoteId, 2);
    }

    /**
     * Get the URL for a V1 Twitch emote URL. The specified ID is the value given by the IRC tags prepended onto posts
     * from the Twitch server
     * 
     * @param emoteId
     *            The ID of the desired emote.
     * @param emoteSize
     *            The desired size of the emote. 1, 2, or 3
     * @return URL
     */
    public static String getTwitchEmoteV1Url(Integer emoteId, int emoteSize)
    {
        if (1 > emoteSize || emoteSize > 3)
        {
            logger.warn("Invalid emote size: " + emoteSize + ". Valid sizes are 1, 2, or 3.");
            emoteSize = 2;
            logger.info("Defaulting to size " + emoteSize + ".");
        }
        return TWITCH_EMOTE_ID_V1_URL.replaceAll(EMOTE_ID_REPLACE, Integer.toString(emoteId)).replaceAll(EMOTE_SIZE_REPLACE, Integer.toString(emoteSize) + ".0");
    }

    public EmojiApiLoader()
    {
        buffer = new char[BUFFER_SIZE];
    }

    public void reset()
    {
        this.jsonStringBuilder = null;
        this.url = null;
        this.jsonLength = 0.0f;
        this.reader = null;
        this.loadComplete = false;
    }

    public void prepLoad(String url)
    {
        this.loadComplete = false;
        this.jsonStringBuilder = new StringBuilder();
        this.url = url;
    }

    public void prepLoad(EmojiType emojiType, String channel, String oauth)
    {
        // FrankerFaceZ API requires the channel name to be all lowercase, Twitch V2 is case agnostic
        channel = channel == null ? null : channel.toLowerCase();
        String chanUrl = getUrl(emojiType, channel, oauth);
        prepLoad(chanUrl);
    }

    private String getUrl(EmojiType emojiType, String channel, String oauth)
    {
        switch (emojiType)
        {
        case FRANKERFACEZ_CHANNEL:
            return FFZ_BASE_URL.replaceAll(CHANNEL_NAME_REPLACE, channel);
        case FRANKERFACEZ_GLOBAL:
            return FFZ_GLOBAL_URL;
        case FRANKERFACEZ_BADGE:
            return FFZ_BADGES;
        case BETTER_TTV_CHANNEL:
            return BTTV_BASE_URL.replaceAll(CHANNEL_NAME_REPLACE, channel);
        case BETTER_TTV_GLOBAL:
            return BTTV_GLOBAL_URL;
        // case TWITCH_V2:
        //     return TWITCH_URL_V2_BASE.replaceAll(CHANNEL_NAME_REPLACE, channel).replaceAll(OAUTH_REPLACE, oauth);
        // case TWITCH_V3:
        //     return TWITCH_URL_V3;
        case TWITCH_BADGE:
            oauth = formatOauth(oauth);
            return TWITCH_URL_BADGES.replaceAll(CHANNEL_NAME_REPLACE, channel).replaceAll(OAUTH_REPLACE, oauth);
        default:
            return null;
        }
    }

    private static String formatOauth(String oauth)
    {
        if (oauth == null)
        {
            oauth = "";
        }
        final String oauthLabel = "oauth:";
        if (oauth.contains(oauthLabel))
        {
            oauth = oauth.substring(oauthLabel.length());
        }
        oauth = "?oauth_token=" + oauth;
        return oauth;
    }

    public boolean initLoad() throws IOException, MalformedURLException, FileNotFoundException
    {
        if (this.url != null)
        {
            URL url = new URL(this.url);
            URLConnection conn = url.openConnection();
            this.jsonLength = conn.getContentLengthLong();
            this.reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isPercentCalculationPossible()
    {
        return jsonLength > 0.0f;
    }

    /**
     * Loads piecemeal to permit reporting to the user how much work has been done
     * 
     * @return percent loaded, or byte count loaded depending on isPercentCalculationPossible()
     * @throws IOException
     * @throws MalformedURLException
     */
    public int loadChunk() throws IOException, MalformedURLException
    {
        int n = reader.read(buffer);
        if (n == -1)
        {
            loadComplete = true;
        }
        else
        {
            jsonStringBuilder.append(buffer, 0, n);
        }
        return isPercentCalculationPossible() ? (int) (100.0f * (jsonStringBuilder.length() / jsonLength)) : jsonStringBuilder.length();
    }

    public boolean isLoadComplete()
    {
        return loadComplete;
    }

    public String getLoadedJson()
    {
        return jsonStringBuilder == null ? null : jsonStringBuilder.toString();
    }

}
