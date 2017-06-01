package com.glitchcog.fontificator.emoji.loader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.*;
import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.UserType;
import com.glitchcog.fontificator.config.ConfigEmoji;
import com.glitchcog.fontificator.emoji.AnimatedGifUtil;
import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.emoji.EmojiType;
import com.glitchcog.fontificator.emoji.LazyLoadEmoji;
import com.glitchcog.fontificator.emoji.TypedEmojiMap;
import com.glitchcog.fontificator.emoji.loader.betterttv.BttvEmote;
import com.glitchcog.fontificator.emoji.loader.frankerfacez.Badge;
import com.glitchcog.fontificator.emoji.loader.frankerfacez.FfzBadgesAndUsers;
import com.glitchcog.fontificator.emoji.loader.frankerfacez.FfzEmote;
import com.glitchcog.fontificator.emoji.loader.frankerfacez.Room;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchBadges;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchBadgesNew;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchEmoteV2;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchEmoteV3;
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchIdSetLink;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;
import com.google.gson.reflect.TypeToken;

/**
 * Parses emote JSON data for Twitch V2, V3, FrankerFaceZ, and BetterTTV
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
     * @param jsonMapData
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
        case FRANKERFACEZ_BADGE:
            parseFrankerFaceZBadges(manager, jsonData);
            break;
        // case TWITCH_V2:
        //     parseTwitchEmoteJsonV2(manager, jsonData, jsonMapData);
        //     break;
        // case TWITCH_V3:
        //     parseTwitchEmoteJsonV3(manager, jsonData, jsonMapData);
        //     break;
        case TWITCH_BADGE:
        case TWITCH_BADGE_GLOBAL:
            parseTwitchBadges(emojiMap, jsonData);
            break;
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
     * Twitch V2 API retired on February 14, 2017
     * 
     * @param manager
     * @param jsonData
     * @param jsonMapData
     * @throws IOException
     */
    @Deprecated
    private void parseTwitchEmoteJsonV2(EmojiManager manager, String jsonData, String jsonMapData) throws IOException
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
            LazyLoadEmoji lle = new LazyLoadEmoji(e.getRegex(), e.getUrl(), e.getWidth(), e.getHeight(), EmojiType.TWITCH_V2);
            lle.setSubscriber(e.isSubscriber_only());
            lle.setState(e.getState());
            emoji.put(e.getRegex(), lle);
        }

        logBox.log(jsonEmoteObjects.length + " Twitch emote" + (jsonEmoteObjects.length == 1 ? "" : "s") + " loaded");
    }

    /**
     * Parses emotes loaded using Twitch's emote API version 3. It parses emotes into two different maps, one of all
     * emoji, and one that are accessible via set ID.
     * 
     * Twitch V3 API retired on February 14, 2017
     * 
     * @param manager
     * @param jsonData
     * @param jsonMapData
     * @throws IOException
     */
    @Deprecated
    private void parseTwitchEmoteJsonV3(EmojiManager manager, String jsonData, String jsonMapData) throws IOException
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
            LazyLoadEmoji lle = new LazyLoadEmoji(e.getRegex(), e.getImages()[0].getUrl(), e.getImages()[0].getWidth(), e.getImages()[0].getHeight(), EmojiType.TWITCH_V3);
            if (e.getImages().length > 1)
            {
                eMultiCount++;
            }

            emoji.put(e.getRegex(), lle);
        }

        logBox.log(jsonEmoteObjects.length + " Twitch emote" + (jsonEmoteObjects.length == 1 ? "" : "s") + " loaded (" + eMultiCount + " multi-image emote" + (eMultiCount == 1 ? "" : "s") + ")");
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

    private void parseTwitchBadges(TypedEmojiMap badgeMap, String jsonData) throws IOException
    {
        JsonElement jsonElement = new JsonParser().parse(jsonData);

        Boolean isNewStyle = jsonElement.getAsJsonObject().has("badge_sets");

        Gson gson = new Gson();
        int badgeCount = 0;

        if (!isNewStyle)
        {
            Type emoteType = new TypeToken<Map<String, TwitchBadges>>() {
            }.getType();
            Map<String, TwitchBadges> jsonMap = gson.fromJson(jsonElement, emoteType);

            for (Entry<String, TwitchBadges> badge : jsonMap.entrySet()) {
                if (badge.getValue() != null && badge.getValue().getImage() != null) {
                    badgeCount++;
                    LazyLoadEmoji llBadge = new LazyLoadEmoji(badge.getKey(), badge.getValue().getImage(), TWITCH_BADGE_PIXEL_SIZE, TWITCH_BADGE_PIXEL_SIZE, EmojiType.TWITCH_BADGE);
                    badgeMap.put(badge.getKey(), llBadge);
                }
            }
        }
        else
        {
            JsonElement badge_sets = jsonElement.getAsJsonObject().get("badge_sets");
            JsonObject badge_sets_object = badge_sets.isJsonObject() ? badge_sets.getAsJsonObject() : new JsonObject();

            for (Map.Entry<String,JsonElement> entry : badge_sets_object.entrySet())
            {
                String unversioned_key = entry.getKey();
                JsonElement badge_set_element = entry.getValue();
                if (!badge_set_element.isJsonObject() || !badge_set_element.getAsJsonObject().has("versions"))
                {
                    // TODO: Error message?
                    continue;
                }
                JsonElement versions_element = badge_set_element.getAsJsonObject().get("versions");
                if (!versions_element.isJsonObject())
                {
                    // TODO: Error message?
                    continue;
                }
                Type emoteType = new TypeToken<Map<String, TwitchBadgesNew>>() {
                }.getType();
                Map<String, TwitchBadgesNew> jsonMap;
                try {
                    jsonMap = gson.fromJson(versions_element, emoteType);
                }
                catch( JsonSyntaxException e ) {
                    // TODO: Error message?
                    continue;
                }

                for (Entry<String, TwitchBadgesNew> sub_badge : jsonMap.entrySet()) {
                    // The unversioned key is something like "subscriber".
                    // The sub_badge is something like "1" or "6".
                    // In the specific case of subscriber badges, "subscriber/1" is a 1 month badge and
                    //  "subscriber/6" is a 6 month badge
                    if (sub_badge.getValue() != null && sub_badge.getValue().getImage_url_1x() != null) {
                        String badge_key = unversioned_key + "/" + sub_badge.getKey();
                        LazyLoadEmoji llBadge = new LazyLoadEmoji(badge_key, sub_badge.getValue().getImage_url_1x(), TWITCH_BADGE_PIXEL_SIZE, TWITCH_BADGE_PIXEL_SIZE, EmojiType.TWITCH_BADGE, unversioned_key);
                        badgeMap.put(badge_key, llBadge);
                        badgeCount++;
                    }
                }
            }
        }
        logBox.log(badgeCount + " Twitch badge" + (badgeCount == 1 ? "" : "s") + " loaded");
    }

    private void parseFrankerFaceZBadges(EmojiManager manager, String jsonData) throws IOException
    {
        JsonElement ffzBadgesAndUsersElement = new JsonParser().parse(jsonData);

        Gson gson = new Gson();

        Type emoteType = new TypeToken<FfzBadgesAndUsers>()
        {
        }.getType();
        FfzBadgesAndUsers badgesAndUsers = gson.fromJson(ffzBadgesAndUsersElement, emoteType);

        for (Badge b : badgesAndUsers.getBadges())
        {
            manager.getEmojiByType(EmojiType.FRANKERFACEZ_BADGE).put("" + b.getId(), new LazyLoadEmoji(b.getName(), b.getReplaces(), "http:" + b.getImage(), b.getColorParsed(), EmojiType.FRANKERFACEZ_BADGE));
        }

        manager.setFfzBadgeUsers(badgesAndUsers.getUsers());
    }

    /**
     * Parse the FrankerFaceZ room data for the optional moderator badge
     * 
     * @param manager
     * @param jsonData
     * @throws IOException
     */
    public void parseFrankerFaceZModBadge(EmojiManager manager, String jsonData) throws IOException
    {
        Gson gson = new Gson();

        Type roomType = new TypeToken<Room>()
        {
        }.getType();
        JsonObject jsonObject = new JsonParser().parse(jsonData).getAsJsonObject();
        Room room = gson.fromJson(jsonObject.get("room"), roomType);

        final boolean customFfzModBadgeExists = room != null && room.getModerator_badge() != null;
        if (customFfzModBadgeExists)
        {
            LazyLoadEmoji modLle = new LazyLoadEmoji(UserType.MOD.getBadge(), UserType.MOD.getBadge(), "https:" + room.getModerator_badge(), ConfigEmoji.MOD_BADGE_COLOR, EmojiType.FRANKERFACEZ_BADGE);
            manager.getEmojiByType(EmojiType.FRANKERFACEZ_BADGE).put(UserType.MOD.getKey(), modLle);
            logBox.log("Loaded the custom FrankerFaceZ moderator badge");
        }
    }

    /**
     * Parse emotes loaded using the FrankerFaceZ emote API
     * 
     * @param emoji
     * @param jsonData
     * @param isGlobal
     *            Whether the FFZ emotes to be loaded are the FFZ global emotes
     * @throws IOException
     */
    private void parseFrankerFaceZEmoteJson(TypedEmojiMap emoji, String jsonData, boolean isGlobal) throws IOException
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
            return;
        }
        else if (root.get("sets").isJsonNull() || (isGlobal && root.get("default_sets").isJsonNull()))
        {
            logBox.log("Unable to load FrankerFaceZ global emotes");
            return;
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
                LazyLoadEmoji lle = null;
                for (String key : e.getUrls().keySet())
                {
                    lle = new LazyLoadEmoji(e.getName(), "http:" + e.getUrls().get(key), e.getWidth(), e.getHeight(), isGlobal ? EmojiType.FRANKERFACEZ_GLOBAL : EmojiType.FRANKERFACEZ_CHANNEL);
                    break;
                }
                if (e.getUrls().size() > 1)
                {
                    eMultiCount++;
                }
                emoji.put(e.getName(), lle);
                frankerCount++;
            }
        }

        String allSets = "";
        for (int n = 0; n < setNames.size(); n++)
        {
            allSets += (n == 0 ? "" : ", ") + setNames.get(n);
        }
        logBox.log(setNames.size() + " FrankerFaceZ set" + (setNames.size() == 1 ? "" : "s") + " found: {" + allSets + "}");
        logBox.log(frankerCount + " FrankerFaceZ emote" + (frankerCount == 1 ? "" : "s") + " loaded (" + eMultiCount + " multi-image emote" + (eMultiCount == 1 ? "" : "s") + ")");
    }

    /**
     * Parse emotes loaded using the BetterTTV emote API
     * 
     * @param emoji
     * @param jsonData
     * @param isGlobal
     *            Whether the BetterTTV emotes to be loaded are the BetterTTV global emotes
     * @throws IOException
     */
    private void parseBetterTtvEmoteJson(TypedEmojiMap emoji, String jsonData, boolean isGlobal) throws IOException
    {
        JsonParser jp = new JsonParser();

        JsonObject root = jp.parse(jsonData).getAsJsonObject();

        if (root.get("emotes").isJsonNull())
        {
            logBox.log("Unable to load Better TTV global emotes");
            return;
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
            LazyLoadEmoji lle = new LazyLoadEmoji(be.getCode(), urlTemplate.replace("{{id}}", be.getId()), isGlobal ? EmojiType.BETTER_TTV_GLOBAL : EmojiType.BETTER_TTV_CHANNEL);
            lle.setAnimatedGif(AnimatedGifUtil.GIF_EXTENSION.equals(be.getImageType()));
            emoji.put(be.getCode(), lle);
            bttvCount++;
        }

        logBox.log(bttvCount + " Better TTV emote" + (bttvCount == 1 ? "" : "s") + " found");
    }

}
