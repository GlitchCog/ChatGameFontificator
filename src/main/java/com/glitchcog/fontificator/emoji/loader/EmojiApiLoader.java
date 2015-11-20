package com.glitchcog.fontificator.emoji.loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.glitchcog.fontificator.emoji.EmojiType;

/**
 * Loads emoji JSON information from an API
 * 
 * @author Matt Yanos
 */
public class EmojiApiLoader
{
    private static final String CHANNEL_NAME_REPLACE = "%CHANNEL_NAME%";

    /**
     * The base URL for getting the channel specific Twitch emotes from V2 of the API
     */
    private static final String TWITCH_URL_V2_BASE = "https://api.twitch.tv/kraken/chat/" + CHANNEL_NAME_REPLACE + "/emoticons";

    /**
     * The URL for getting all Twitch emotes from V3 of the API
     */
    private static final String TWITCH_URL_V3 = "https://api.twitch.tv/kraken/chat/emoticons";

    /**
     * The base URL for getting the channel specific Twitch badges from the API
     */
    private static final String TWITCH_URL_BADGES = "https://api.twitch.tv/kraken/chat/" + CHANNEL_NAME_REPLACE + "/badges";

    /**
     * The URL for getting the global FrankerFaceZ emotes from the API
     */
    private static final String FRANKER_GLOBAL_URL = "https://api.frankerfacez.com/v1/set/global";

    /**
     * The base URL for getting the FrankerFaceZ emotes for whatever username is appended to the end from the API
     */
    private static final String FRANKER_BASE_URL = "https://api.frankerfacez.com/v1/room/" + CHANNEL_NAME_REPLACE;

    private static final int BUFFER_SIZE = 512;

    private final char[] buffer;

    private StringBuilder jsonStringBuilder;

    private String url;

    private float jsonLength;

    private BufferedReader reader;

    private boolean loadComplete;

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

    public void prepLoad(EmojiType emojiType, String channel)
    {
        this.loadComplete = false;
        this.jsonStringBuilder = new StringBuilder();
        // FrankerFaceZ API requires the channel name to be all lowercase, Twitch V2 is case agnostic
        channel = channel == null ? null : channel.toLowerCase();
        this.url = getUrl(emojiType, channel);
    }

    private String getUrl(EmojiType emojiType, String channel)
    {
        switch (emojiType)
        {
        case FRANKERFACEZ_CHANNEL:
            return FRANKER_BASE_URL.replaceAll(CHANNEL_NAME_REPLACE, channel);
        case FRANKERFACEZ_GLOBAL:
            return FRANKER_GLOBAL_URL;
        case TWITCH_V2:
            return TWITCH_URL_V2_BASE.replaceAll(CHANNEL_NAME_REPLACE, channel);
        case TWITCH_V3:
            return TWITCH_URL_V3;
        case TWITCH_BADGE:
            return TWITCH_URL_BADGES.replaceAll(CHANNEL_NAME_REPLACE, channel);
        default:
            return null;
        }
    }

    public void initLoad() throws IOException, MalformedURLException, FileNotFoundException
    {
        URL url = new URL(this.url);
        URLConnection conn = url.openConnection();
        this.jsonLength = conn.getContentLengthLong();
        this.reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
