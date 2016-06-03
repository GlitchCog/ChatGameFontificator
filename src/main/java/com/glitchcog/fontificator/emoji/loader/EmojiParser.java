package com.glitchcog.fontificator.emoji.loader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.emoji.EmojiType;
import com.glitchcog.fontificator.emoji.LazyLoadEmoji;
import com.glitchcog.fontificator.emoji.TypedEmojiMap;
import com.glitchcog.fontificator.emoji.loader.betterttv.BttvEmote;
import com.glitchcog.fontificator.emoji.loader.frankerfacez.FfzEmote;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchBadges;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchEmoteV2;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchEmoteV3;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchIdSetLink;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Parses emote JSON data for Twitch V2, V3, and FrankerFaceZ
 * 
 * @author Matt Yanos
 */
public class EmojiParser
{
    private static final Logger logger = Logger.getLogger(EmojiParser.class);

    private static final int TWITCH_BADGE_PIXEL_SIZE = 18;

    /**
     * Reference to the LogBox that displays on the Connection (IRC) control panel to display the results of loading and
     * caching emotes
     */
    private LogBox logBox;

    public EmojiParser(LogBox logBox)
    {
        this.logBox = logBox;
    }

    /**
     * @param manager
     *            the emoji manager to load the parsed emoji into
     * @param type
     *            the type of the emoji
     * @param jsonData
     *            the JSON data of the emoji to parse
     * @param mapData
     *            can be null for non-Twitch emoji
     * @throws IOException
     */
    public void putJsonEmojiIntoManager(EmojiManager manager, EmojiType type, String jsonData, String jsonMapData) throws IOException
    {
        TypedEmojiMap emojiMap = manager.getEmojiByType(type);
        switch (type)
        {
        case FRANKERFACEZ_CHANNEL:
        case FRANKERFACEZ_GLOBAL:
            parseFrankerFaceZEmoteJson(emojiMap, jsonData, type == EmojiType.FRANKERFACEZ_GLOBAL);
            break;
        case TWITCH_V2:
            parseTwitchEmoteJsonV2(manager, jsonData, jsonMapData);
            break;
        case TWITCH_V3:
            parseTwitchEmoteJsonV3(manager, jsonData, jsonMapData);
            break;
        case TWITCH_BADGE:
            parseTwitchBadges(emojiMap, jsonData);
        case BETTER_TTV_CHANNEL:
        case BETTER_TTV_GLOBAL:
            parseBetterTtvEmoteJson(emojiMap, jsonData, type == EmojiType.BETTER_TTV_GLOBAL);
            break;
        default:
            break;
        }
    }

    /**
     * Parse emotes loaded using Twitch's emote API version 2
     * 
     * @param emoji
     * @param jsonData
     * @return
     * @throws IOException
     * @throws MalformedURLException
     */
    private TypedEmojiMap parseTwitchEmoteJsonV2(EmojiManager manager, String jsonData, String jsonMapData) throws IOException
    {
        TypedEmojiMap emoji = manager.getEmojiByType(EmojiType.TWITCH_V2);
        JsonElement emoteElement = new JsonParser().parse(jsonData).getAsJsonObject().get("emoticons");

        Gson gson = new Gson();

        Type emoteType = new TypeToken<TwitchEmoteV2[]>()
        {
        }.getType();
        TwitchEmoteV2[] jsonEmoteObjects = gson.fromJson(emoteElement, emoteType);
        for (TwitchEmoteV2 e : jsonEmoteObjects)
        {
            // For Twitch emotes V2, there are no multi-image emotes, I think, based on the JSON structure
            LazyLoadEmoji[] lle = new LazyLoadEmoji[1];
            lle[0] = new LazyLoadEmoji(e.getRegex(), e.getUrl(), e.getWidth(), e.getHeight(), EmojiType.TWITCH_V2);
            lle[0].setSubscriber(e.isSubscriber_only());
            lle[0].setState(e.getState());
            emoji.put(e.getRegex(), lle);
        }

        logBox.log(jsonEmoteObjects.length + " Twitch emote" + (jsonEmoteObjects.length == 1 ? "" : "s") + " loaded");

        return emoji;
    }

    /**
     * Parses emotes loaded using Twitch's emote API version 3. It parses emotes into two different maps, one of all
     * emoji, and one that are accessible via set ID.
     * 
     * @param setKeyedMaps
     * @param emoji
     * @param jsonData
     * @return
     * @throws IOException
     */
    private TypedEmojiMap parseTwitchEmoteJsonV3(EmojiManager manager, String jsonData, String jsonMapData) throws IOException
    {
        logger.trace(jsonData.substring(0, Math.min(jsonData.length(), 512)));

        TypedEmojiMap emoji = manager.getEmojiByType(EmojiType.TWITCH_V3);

        Gson gson = new Gson();

        // Handle actual emotes
        JsonElement emoteElement = new JsonParser().parse(jsonData).getAsJsonObject().get("emoticons");

        Type emoteType = new TypeToken<TwitchEmoteV3[]>()
        {
        }.getType();
        TwitchEmoteV3[] jsonEmoteObjects = gson.fromJson(emoteElement, emoteType);
        int eMultiCount = 0;

        for (TwitchEmoteV3 e : jsonEmoteObjects)
        {
            LazyLoadEmoji[] lle = new LazyLoadEmoji[e.getImages().length];
            for (int i = 0; i < e.getImages().length; i++)
            {
                lle[i] = new LazyLoadEmoji(e.getRegex(), e.getImages()[i].getUrl(), e.getImages()[i].getWidth(), e.getImages()[i].getHeight(), EmojiType.TWITCH_V3);
            }
            if (e.getImages().length > 1)
            {
                eMultiCount++;
            }

            emoji.put(e.getRegex(), lle);
        }

        logBox.log(jsonEmoteObjects.length + " Twitch emote" + (jsonEmoteObjects.length == 1 ? "" : "s") + " loaded (" + eMultiCount + " multi-image emote" + (eMultiCount == 1 ? "" : "s") + ")");

        return emoji;
    }

    /**
     * This used to have a purpose, but now all Twitch emotes are loaded via the V1 emote ID in the IRC tag emote
     * information
     * 
     * @param jsonMapData
     * @return setIdByEmoteId
     */
    protected Map<Integer, Integer> parseSetMap(String jsonMapData)
    {
        Gson gson = new Gson();
        JsonElement setLinksElement = new JsonParser().parse(jsonMapData).getAsJsonObject().get("emoticons");
        Type setLinkType = new TypeToken<TwitchIdSetLink[]>()
        {
        }.getType();
        TwitchIdSetLink[] idToSetTwitchLinks = gson.fromJson(setLinksElement, setLinkType);

        Map<Integer, Integer> setIdByEmoteId = new HashMap<Integer, Integer>();

        for (TwitchIdSetLink tLink : idToSetTwitchLinks)
        {
            Integer emoteId = Integer.parseInt(tLink.getId());
            Integer setId = tLink.getEmoticon_set() == null ? null : Integer.parseInt(tLink.getEmoticon_set());
            setIdByEmoteId.put(emoteId, setId);
        }

        return setIdByEmoteId;
    }

    /**
     * Utility method, currently unused, to parse out the emote ID from the URL for a Twitch V3 emote. Global emotes do
     * not have the emote ID in their URLs, so this method will return null for them.
     * 
     * @param e
     *            Twitch V3 emote model
     * @return emoteId The emote ID from the URL
     */
    protected static Integer parseEmoteIdFromUrl(TwitchEmoteV3 e) throws Exception
    {
        final String pngName = e.getImages()[0].getUrl().substring(e.getImages()[0].getUrl().lastIndexOf('/') + 1);
        final int dashIdxBeg = pngName.indexOf('-') + 1;
        final int dashIdxEnd = pngName.indexOf('-', dashIdxBeg);
        final String emoteIdStr = pngName.substring(dashIdxBeg, dashIdxEnd);
        final Integer emoteId = Integer.parseInt(emoteIdStr);
        return emoteId;
    }

    private TypedEmojiMap parseTwitchBadges(TypedEmojiMap badgeMap, String jsonData) throws IOException
    {
        JsonElement jsonElement = new JsonParser().parse(jsonData);

        Gson gson = new Gson();

        Type emoteType = new TypeToken<Map<String, TwitchBadges>>()
        {
        }.getType();
        Map<String, TwitchBadges> jsonMap = gson.fromJson(jsonElement, emoteType);

        int badgeCount = 0;
        for (Entry<String, TwitchBadges> badge : jsonMap.entrySet())
        {
            if (badge.getValue() != null && badge.getValue().getImage() != null)
            {
                badgeCount++;
                LazyLoadEmoji[] llBadge = new LazyLoadEmoji[] { new LazyLoadEmoji(badge.getKey(), badge.getValue().getImage(), TWITCH_BADGE_PIXEL_SIZE, TWITCH_BADGE_PIXEL_SIZE, EmojiType.TWITCH_BADGE) };
                badgeMap.put(badge.getKey(), llBadge);
            }
        }

        logBox.log(badgeCount + " Twitch badge" + (badgeCount == 1 ? "" : "s") + " loaded");

        return badgeMap;
    }

    /**
     * Parse emotes loaded using the FrankerFaceZ emote API
     * 
     * @param emoji
     * @param jsonData
     * @param isGlobal
     *            Whether the FFZ emotes to be loaded are the FFZ global emotes
     * @return typedEmojiMap
     * @throws IOException
     */
    private TypedEmojiMap parseFrankerFaceZEmoteJson(TypedEmojiMap emoji, String jsonData, boolean isGlobal) throws IOException
    {
        JsonParser jp = new JsonParser();

        JsonObject root = jp.parse(jsonData).getAsJsonObject();

        if (root.get("error") != null)
        {
            String errorMessage = "Unable to load FrankerFaceZ emotes";
            if (!root.get("message").isJsonNull())
            {
                errorMessage += ": " + root.get("message");
            }
            logBox.log(errorMessage);
            return emoji;
        }
        else if (root.get("sets").isJsonNull() || (isGlobal && root.get("default_sets").isJsonNull()))
        {
            logBox.log("Unable to load FrankerFaceZ global emotes");
            return emoji;
        }

        List<String> setsToLoad;

        if (isGlobal)
        {
            setsToLoad = new ArrayList<String>();
            JsonArray defaultSetsArray = root.get("default_sets").getAsJsonArray();
            for (int i = 0; i < defaultSetsArray.size(); i++)
            {
                setsToLoad.add(defaultSetsArray.get(i).getAsString());
            }
        }
        else
        {
            setsToLoad = null;
        }

        JsonObject sets = root.get("sets").getAsJsonObject();

        Gson gson = new Gson();
        Type emoteType = new TypeToken<FfzEmote[]>()
        {
        }.getType();

        int frankerCount = 0;
        int eMultiCount = 0;
        List<String> setNames = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : sets.entrySet())
        {
            setNames.add(entry.getKey());

            JsonElement emoteElement = entry.getValue().getAsJsonObject().get("emoticons");

            FfzEmote[] jsonEmoteObjects = gson.fromJson(emoteElement, emoteType);
            for (FfzEmote e : jsonEmoteObjects)
            {
                LazyLoadEmoji[] lle = new LazyLoadEmoji[e.getUrls().size()];
                int i = 0;
                for (String key : e.getUrls().keySet())
                {
                    lle[i++] = new LazyLoadEmoji(e.getName(), "http:" + e.getUrls().get(key), e.getWidth(), e.getHeight(), isGlobal ? EmojiType.FRANKERFACEZ_GLOBAL : EmojiType.FRANKERFACEZ_CHANNEL);
                }
                if (e.getUrls().size() > 1)
                {
                    eMultiCount++;
                }
                emoji.put(e.getName(), lle);
                frankerCount++; // Used to cache just the FrankerFaceZ emotes
            }
        }

        String allSets = "";
        for (int n = 0; n < setNames.size(); n++)
        {
            allSets += (n == 0 ? "" : ", ") + setNames.get(n);
        }
        logBox.log(setNames.size() + " FrankerFaceZ set" + (setNames.size() == 1 ? "" : "s") + " found: {" + allSets + "}");
        logBox.log(frankerCount + " FrankerFaceZ emote" + (frankerCount == 1 ? "" : "s") + " loaded (" + eMultiCount + " multi-image emote" + (eMultiCount == 1 ? "" : "s") + ")");

        return emoji;
    }

    /**
     * Parse emotes loaded using the BetterTTV emote API
     * 
     * @param emoji
     * @param jsonData
     * @param isGlobal
     *            Whether the BetterTTV emotes to be loaded are the BetterTTV global emotes
     * @return typedEmojiMap
     * @throws IOException
     */
    private TypedEmojiMap parseBetterTtvEmoteJson(TypedEmojiMap emoji, String jsonData, boolean isGlobal) throws IOException
    {
        JsonParser jp = new JsonParser();

        JsonObject root = jp.parse(jsonData).getAsJsonObject();

        if (root.get("emotes").isJsonNull())
        {
            logBox.log("Unable to load Better TTV global emotes");
            return emoji;
        }

        Gson gson = new Gson();
        Type emoteType = new TypeToken<BttvEmote[]>()
        {
        }.getType();

        final String urlTemplate = "https:" + root.get("urlTemplate").getAsString().replace("{{image}}", "2x");

        BttvEmote[] bttvEmotes = gson.fromJson(root.get("emotes").getAsJsonArray(), emoteType);

        int bttvCount = 0;
        for (BttvEmote be : bttvEmotes)
        {
            LazyLoadEmoji[] lle = new LazyLoadEmoji[1];
            lle[0] = new LazyLoadEmoji(be.getCode(), urlTemplate.replace("{{id}}", be.getId()), isGlobal ? EmojiType.BETTER_TTV_GLOBAL : EmojiType.BETTER_TTV_CHANNEL);
            lle[0].setAnimatedGif("gif".equals(be.getImageType()));
            emoji.put(be.getCode(), lle);
            bttvCount++; // Used to cache
        }

        logBox.log(bttvCount + " Better TTV emote" + (bttvCount == 1 ? "" : "s") + " found");

        return emoji;
    }

}
