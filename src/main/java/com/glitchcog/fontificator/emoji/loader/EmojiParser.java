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
            String errorMessage = "Unable to loadFrankerFaceZ emoji";
            if (!root.get("message").isJsonNull())
            {
                errorMessage += ": " + root.get("message");
            }
            logBox.log(errorMessage);
            return emoji;
        }
        else if (root.get("sets").isJsonNull() || (isGlobal && root.get("default_sets").isJsonNull()))
        {
            logBox.log("Unable to load FrankerFaceZ global emoji");
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

    // global:
    // {"default_sets": [3], "sets": {"3": {"_type": 0, "css": null, "description": null, "emoticons": [{"css": null,
    // "height": 29, "hidden": false, "id": 25927, "margins": null, "name": "CatBag", "owner": {"display_name": "Wolsk",
    // "id": 3, "name": "wolsk"}, "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/25927/1", "2":
    // "//cdn.frankerfacez.com/emoticon/25927/2", "4": "//cdn.frankerfacez.com/emoticon/25927/4"}, "width": 32}, {"css":
    // null, "height": 30, "hidden": false, "id": 27081, "margins": null, "name": "ZreknarF", "owner": {"display_name":
    // "SirStendec", "id": 1, "name": "sirstendec"}, "public": false, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/27081/1", "2": "//cdn.frankerfacez.com/emoticon/27081/2", "4":
    // "//cdn.frankerfacez.com/emoticon/27081/4"}, "width": 40}, {"css": null, "height": 23, "hidden": false, "id":
    // 28136, "margins": null, "name": "LilZ", "owner": {"display_name": "SirStendec", "id": 1, "name": "sirstendec"},
    // "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/28136/1", "2":
    // "//cdn.frankerfacez.com/emoticon/28136/2", "4": "//cdn.frankerfacez.com/emoticon/28136/4"}, "width": 32}, {"css":
    // null, "height": 23, "hidden": true, "id": 28138, "margins": null, "name": "ZliL", "owner": {"display_name":
    // "SirStendec", "id": 1, "name": "sirstendec"}, "public": false, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/28138/1", "2": "//cdn.frankerfacez.com/emoticon/28138/2", "4":
    // "//cdn.frankerfacez.com/emoticon/28138/4"}, "width": 32}, {"css": null, "height": 30, "hidden": true, "id": 9,
    // "margins": null, "name": "ZrehplaR", "owner": {"display_name": "dansalvato", "id": 2, "name": "dansalvato"},
    // "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/9/1"}, "width": 33}, {"css": null, "height": 34,
    // "hidden": false, "id": 6, "margins": null, "name": "YooHoo", "owner": {"display_name": "dansalvato", "id": 2,
    // "name": "dansalvato"}, "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/6/1"}, "width": 28},
    // {"css": null, "height": 29, "hidden": false, "id": 5, "margins": null, "name": "YellowFever", "owner":
    // {"display_name": "dansalvato", "id": 2, "name": "dansalvato"}, "public": false, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/5/1"}, "width": 23}, {"css": null, "height": 29, "hidden": false, "id": 4,
    // "margins": null, "name": "ManChicken", "owner": {"display_name": "dansalvato", "id": 2, "name": "dansalvato"},
    // "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/4/1"}, "width": 30}, {"css": null, "height": 33,
    // "hidden": false, "id": 3, "margins": null, "name": "BeanieHipster", "owner": {"display_name": "dansalvato", "id":
    // 2, "name": "dansalvato"}, "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/3/1"}, "width": 28}],
    // "icon": null, "id": 3, "title": "Global Emoticons"}, "4330": {"_type": 0, "css": null, "description": null,
    // "emoticons": [{"css": null, "height": 11, "hidden": false, "id": 24999, "margins": null, "name": "AndKnuckles",
    // "owner": {"display_name": "SirStendec", "id": 1, "name": "sirstendec"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/24999/1", "2": "//cdn.frankerfacez.com/emoticon/24999/2", "4":
    // "//cdn.frankerfacez.com/emoticon/24999/4"}, "width": 80}, {"css": null, "height": 29, "hidden": false, "id":
    // 20722, "margins": null, "name": "PepsiMan", "owner": {"display_name": "HayaiKawaiiMarathon", "id": 3030, "name":
    // "hayaikawaiimarathon"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/20722/1"}, "width": 32},
    // {"css": null, "height": 32, "hidden": false, "id": 26608, "margins": null, "name": "BibleBag", "owner":
    // {"display_name": "SirStendec", "id": 1, "name": "sirstendec"}, "public": false, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/26608/1", "2": "//cdn.frankerfacez.com/emoticon/26608/2", "4":
    // "//cdn.frankerfacez.com/emoticon/26608/4"}, "width": 36}, {"css": null, "height": 32, "hidden": false, "id":
    // 26640, "margins": null, "name": "FeaturingDanteFromTheDevilMayCrySeries", "owner": {"display_name": "SirStendec",
    // "id": 1, "name": "sirstendec"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/26640/1", "2":
    // "//cdn.frankerfacez.com/emoticon/26640/2", "4": "//cdn.frankerfacez.com/emoticon/26640/4"}, "width": 32}, {"css":
    // null, "height": 27, "hidden": false, "id": 37715, "margins": null, "name": "HappyThump", "owner":
    // {"display_name": "AdamantAndrew28", "id": 9490, "name": "adamantandrew28"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/37715/1"}, "width": 30}, {"css": null, "height": 28, "hidden": false, "id":
    // 39384, "margins": null, "name": "SwedishW", "owner": {"display_name": "Drastnikov", "id": 2600, "name":
    // "drastnikov"}, "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/39384/1", "2":
    // "//cdn.frankerfacez.com/emoticon/39384/2", "4": "//cdn.frankerfacez.com/emoticon/39384/4"}, "width": 28}, {"css":
    // null, "height": 15, "hidden": false, "id": 30435, "margins": null, "name": "CatBagMini", "owner":
    // {"display_name": "Wolsk", "id": 3, "name": "wolsk"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/30435/1", "2": "//cdn.frankerfacez.com/emoticon/30435/2", "4":
    // "//cdn.frankerfacez.com/emoticon/30435/4"}, "width": 16}, {"css": null, "height": 32, "hidden": false, "id":
    // 40431, "margins": null, "name": "AndKnucklesLookingOutHisWindow", "owner": {"display_name": "SirStendec", "id":
    // 1, "name": "sirstendec"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/40431/1", "2":
    // "//cdn.frankerfacez.com/emoticon/40431/2", "4": "//cdn.frankerfacez.com/emoticon/40431/4"}, "width": 32}, {"css":
    // null, "height": 29, "hidden": false, "id": 41091, "margins": null, "name": "CoolCatBag", "owner":
    // {"display_name": "SirStendec", "id": 1, "name": "sirstendec"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/41091/1", "2": "//cdn.frankerfacez.com/emoticon/41091/2", "4":
    // "//cdn.frankerfacez.com/emoticon/41091/4"}, "width": 32}, {"css": null, "height": 32, "hidden": false, "id":
    // 41767, "margins": null, "name": "DoILookLikeIKnowWhatAFrankerFaceZeeIs", "owner": {"display_name": "Zantkin",
    // "id": 4983, "name": "zantkin"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/41767/1", "2":
    // "//cdn.frankerfacez.com/emoticon/41767/2", "4": "//cdn.frankerfacez.com/emoticon/41767/4"}, "width": 32}, {"css":
    // null, "height": 32, "hidden": false, "id": 41220, "margins": null, "name": "Coppa", "owner": {"display_name":
    // "Potato_Wyvern", "id": 8671, "name": "potato_wyvern"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/41220/1", "2": "//cdn.frankerfacez.com/emoticon/41220/2", "4":
    // "//cdn.frankerfacez.com/emoticon/41220/4"}, "width": 29}, {"css": null, "height": 32, "hidden": false, "id":
    // 25335, "margins": null, "name": "Tuturu", "owner": {"display_name": "Melrose23", "id": 3792, "name":
    // "melrose23"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/25335/1", "2":
    // "//cdn.frankerfacez.com/emoticon/25335/2", "4": "//cdn.frankerfacez.com/emoticon/25335/4"}, "width": 32}, {"css":
    // null, "height": 32, "hidden": false, "id": 26933, "margins": null, "name": "AlmightyBob", "owner":
    // {"display_name": "Andrick11x", "id": 386, "name": "andrick11x"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/26933/1", "2": "//cdn.frankerfacez.com/emoticon/26933/2", "4":
    // "//cdn.frankerfacez.com/emoticon/26933/4"}, "width": 30}, {"css": null, "height": 32, "hidden": false, "id":
    // 26931, "margins": null, "name": "BillyMazing", "owner": {"display_name": "Andrick11x", "id": 386, "name":
    // "andrick11x"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/26931/1", "2":
    // "//cdn.frankerfacez.com/emoticon/26931/2", "4": "//cdn.frankerfacez.com/emoticon/26931/4"}, "width": 22}, {"css":
    // null, "height": 32, "hidden": false, "id": 47819, "margins": null, "name": "NinetiesKids", "owner":
    // {"display_name": "SirStendec", "id": 1, "name": "sirstendec"}, "public": false, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/47819/1", "2": "//cdn.frankerfacez.com/emoticon/47819/2", "4":
    // "//cdn.frankerfacez.com/emoticon/47819/4"}, "width": 24}, {"css": null, "height": 32, "hidden": false, "id":
    // 47941, "margins": null, "name": "KermitComfy", "owner": {"display_name": "BeanyBeanBean", "id": 14213, "name":
    // "beanybeanbean"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/47941/1", "2":
    // "//cdn.frankerfacez.com/emoticon/47941/2", "4": "//cdn.frankerfacez.com/emoticon/47941/4"}, "width": 32}, {"css":
    // null, "height": 32, "hidden": false, "id": 11426, "margins": null, "name": "DealWithBag", "owner":
    // {"display_name": "P4ntz", "id": 1646, "name": "p4ntz"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/11426/1"}, "width": 32}, {"css": null, "height": 28, "hidden": false, "id":
    // 65975, "margins": null, "name": "NotExcellent", "owner": {"display_name": "SirStendec", "id": 1, "name":
    // "sirstendec"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/65975/1", "2":
    // "//cdn.frankerfacez.com/emoticon/65975/2", "4": "//cdn.frankerfacez.com/emoticon/65975/4"}, "width": 31}, {"css":
    // null, "height": 20, "hidden": false, "id": 29868, "margins": null, "name": "ChatPyramid", "owner":
    // {"display_name": "SirStendec", "id": 1, "name": "sirstendec"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/29868/1", "2": "//cdn.frankerfacez.com/emoticon/29868/2", "4":
    // "//cdn.frankerfacez.com/emoticon/29868/4"}, "width": 62}, {"css": null, "height": 32, "hidden": false, "id":
    // 67523, "margins": null, "name": "CoolDeveloper", "owner": {"display_name": "Cresentrend", "id": 1733, "name":
    // "cresentrend"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/67523/1", "2":
    // "//cdn.frankerfacez.com/emoticon/67523/2", "4": "//cdn.frankerfacez.com/emoticon/67523/4"}, "width": 24}, {"css":
    // null, "height": 32, "hidden": false, "id": 44708, "margins": null, "name":
    // "DougDimmadomeOwnerOfTheDimmsdaleDimmadomeLookingOutHisDimmsdaleDimmadome", "owner": {"display_name":
    // "SirStendec", "id": 1, "name": "sirstendec"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/44708/1", "2": "//cdn.frankerfacez.com/emoticon/44708/2", "4":
    // "//cdn.frankerfacez.com/emoticon/44708/4"}, "width": 39}, {"css": null, "height": 28, "hidden": false, "id":
    // 43984, "margins": null, "name": "DougDimmadomeOwnerOfTheDimmsdaleDimmadome", "owner": {"display_name":
    // "Cresentrend", "id": 1733, "name": "cresentrend"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/43984/1", "2": "//cdn.frankerfacez.com/emoticon/43984/2", "4":
    // "//cdn.frankerfacez.com/emoticon/43984/4"}, "width": 32}, {"css": null, "height": 23, "hidden": false, "id":
    // 12558, "margins": null, "name": "LilTurtle", "owner": {"display_name": "xYulf", "id": 1778, "name": "xyulf"},
    // "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/12558/1"}, "width": 32}, {"css": null, "height":
    // 32, "hidden": false, "id": 25492, "margins": null, "name": "GreySpaceNoFace", "owner": {"display_name":
    // "MolluskMoth", "id": 2669, "name": "molluskmoth"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/25492/1", "2": "//cdn.frankerfacez.com/emoticon/25492/2", "4":
    // "//cdn.frankerfacez.com/emoticon/25492/4"}, "width": 24}, {"css": null, "height": 28, "hidden": false, "id":
    // 28681, "margins": null, "name": "sFlexRight", "owner": {"display_name": "wallyaldo", "id": 1456, "name":
    // "wallyaldo"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/28681/1"}, "width": 27}, {"css":
    // null, "height": 32, "hidden": false, "id": 26214, "margins": null, "name": "sFlex", "owner": {"display_name":
    // "Skype_Emotes", "id": 4063, "name": "skype_emotes"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/26214/1"}, "width": 32}, {"css": null, "height": 27, "hidden": false, "id":
    // 31146, "margins": null, "name": "NoTuturu", "owner": {"display_name": "Colinberry", "id": 3649, "name":
    // "colinberry"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/31146/1", "2":
    // "//cdn.frankerfacez.com/emoticon/31146/2", "4": "//cdn.frankerfacez.com/emoticon/31146/4"}, "width": 32}, {"css":
    // null, "height": 32, "hidden": false, "id": 26220, "margins": null, "name": "SeriousSpeedrun", "owner":
    // {"display_name": "DrTChops", "id": 2163, "name": "drtchops"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/26220/1", "2": "//cdn.frankerfacez.com/emoticon/26220/2", "4":
    // "//cdn.frankerfacez.com/emoticon/26220/4"}, "width": 26}, {"css": null, "height": 27, "hidden": false, "id":
    // 59847, "margins": "-8px 8px 8px -40px", "name": "StabZ", "owner": {"display_name": "SirStendec", "id": 1, "name":
    // "sirstendec"}, "public": false, "urls": {"1": "//cdn.frankerfacez.com/emoticon/59847/1", "2":
    // "//cdn.frankerfacez.com/emoticon/59847/2", "4": "//cdn.frankerfacez.com/emoticon/59847/4"}, "width": 31}, {"css":
    // null, "height": 32, "hidden": false, "id": 27039, "margins": null, "name": "PresidentSleeper", "owner":
    // {"display_name": "xxshawn", "id": 3589, "name": "xxshawn"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/27039/1", "2": "//cdn.frankerfacez.com/emoticon/27039/2", "4":
    // "//cdn.frankerfacez.com/emoticon/27039/4"}, "width": 28}], "icon": null, "id": 4330, "title":
    // ": Sten's Cheaty Emotes"}}}
    // swishface:
    // {"room": {"_id": 3541, "_tid": 21920186, "css": null, "display_name": "Swishface", "id": "swishface", "is_group":
    // false, "moderator_badge": null, "set": 3542}, "sets": {"3542": {"_type": 1, "css": null, "description": null,
    // "emoticons": [{"css": null, "height": 30, "hidden": false, "id": 50036, "margins": null, "name": "SuchMcNeil",
    // "owner": {"display_name": "Swishface", "id": 3541, "name": "swishface"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/50036/1"}, "width": 32}, {"css": null, "height": 32, "hidden": false, "id":
    // 50037, "margins": null, "name": "LewdYuka", "owner": {"display_name": "Swishface", "id": 3541, "name":
    // "swishface"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/50037/1"}, "width": 31}, {"css":
    // null, "height": 23, "hidden": false, "id": 3303, "margins": null, "name": "ResidentKirby", "owner":
    // {"display_name": "Andrick11x", "id": 386, "name": "andrick11x"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/3303/1", "2": "//cdn.frankerfacez.com/emoticon/3303/2", "4":
    // "//cdn.frankerfacez.com/emoticon/3303/4"}, "width": 31}, {"css": null, "height": 32, "hidden": false, "id":
    // 50684, "margins": null, "name": "LookMorty", "owner": {"display_name": "Swishface", "id": 3541, "name":
    // "swishface"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/50684/1"}, "width": 32}, {"css":
    // null, "height": 32, "hidden": false, "id": 30445, "margins": null, "name": "TeeHee", "owner": {"display_name":
    // "CloudxMiku", "id": 2638, "name": "cloudxmiku"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/30445/1", "2": "//cdn.frankerfacez.com/emoticon/30445/2", "4":
    // "//cdn.frankerfacez.com/emoticon/30445/4"}, "width": 32}, {"css": null, "height": 32, "hidden": false, "id":
    // 1327, "margins": null, "name": "megaPalm", "owner": {"display_name": "Skavenger216", "id": 97, "name":
    // "skavenger216"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/1327/1"}, "width": 29}, {"css":
    // null, "height": 26, "hidden": false, "id": 57124, "margins": null, "name": "Smugfrog", "owner": {"display_name":
    // "UmbraNoti", "id": 18388, "name": "umbranoti"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/57124/1", "2": "//cdn.frankerfacez.com/emoticon/57124/2", "4":
    // "//cdn.frankerfacez.com/emoticon/57124/4"}, "width": 32}, {"css": null, "height": 30, "hidden": false, "id":
    // 24119, "margins": null, "name": "DirtDrgn", "owner": {"display_name": "Swishface", "id": 3541, "name":
    // "swishface"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/24119/1"}, "width": 26}, {"css":
    // null, "height": 27, "hidden": false, "id": 21937, "margins": null, "name": "RBMRIP", "owner": {"display_name":
    // "RedBalloonMan", "id": 3226, "name": "redballoonman"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/21937/1"}, "width": 24}, {"css": null, "height": 27, "hidden": false, "id":
    // 58818, "margins": null, "name": "sansWink", "owner": {"display_name": "roboticanxiety", "id": 20289, "name":
    // "roboticanxiety"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/58818/1", "2":
    // "//cdn.frankerfacez.com/emoticon/58818/2", "4": "//cdn.frankerfacez.com/emoticon/58818/4"}, "width": 28}, {"css":
    // null, "height": 28, "hidden": false, "id": 28403, "margins": null, "name": "FrogFlex", "owner": {"display_name":
    // "Syntheticuh", "id": 359, "name": "syntheticuh"}, "public": true, "urls": {"1":
    // "//cdn.frankerfacez.com/emoticon/28403/1"}, "width": 32}, {"css": null, "height": 32, "hidden": false, "id":
    // 20706, "margins": null, "name": "BibleBag", "owner": {"display_name": "HayaiKawaiiMarathon", "id": 3030, "name":
    // "hayaikawaiimarathon"}, "public": true, "urls": {"1": "//cdn.frankerfacez.com/emoticon/20706/1", "2":
    // "//cdn.frankerfacez.com/emoticon/20706/2", "4": "//cdn.frankerfacez.com/emoticon/20706/4"}, "width": 32}],
    // "icon": null, "id": 3542, "title": "Channel: Swishface"}}}

}
