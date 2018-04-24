package com.glitchcog.fontificator.emoji.loader;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.glitchcog.fontificator.emoji.loader.twitch.TwitchIdSetLink;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Parses emote JSON data for Twitch V2, V3, FrankerFaceZ, and BetterTTV
 * 
 * @author Matt Yanos
 */
public class EmojiParser
{
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
    public void putJsonEmojiIntoManager(EmojiManager manager, EmojiType type, String jsonData) throws IOException
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

    private void parseTwitchBadges(TypedEmojiMap badgeMap, String jsonData) throws IOException
    {
        JsonElement jsonElement = new JsonParser().parse(jsonData);

        Gson gson = new Gson();

        Type emoteType = new TypeToken<Map<String, TwitchBadges>>()
        {
        }.getType();
        Map<String, TwitchBadges> jsonMap = gson.fromJson(jsonElement, emoteType);

        final Color subBgColor = new Color(0x6441A4);
        int badgeCount = 0;
        for (Entry<String, TwitchBadges> badge : jsonMap.entrySet())
        {
            if (badge.getValue() != null && badge.getValue().getImage() != null)
            {
                // Sub badge color is a background color hack to make the sub badge visible against black backgrounds until the new Twitch badge system is implemented
                final boolean isSubBadge = "subscriber".equals(badge.getKey());
                badgeCount++;
                LazyLoadEmoji llBadge = new LazyLoadEmoji(badge.getKey(), badge.getValue().getImage(), TWITCH_BADGE_PIXEL_SIZE, TWITCH_BADGE_PIXEL_SIZE, isSubBadge ? subBgColor : null, EmojiType.TWITCH_BADGE);
                badgeMap.put(badge.getKey(), llBadge);
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
            manager.getEmojiByType(EmojiType.FRANKERFACEZ_BADGE).put("" + b.getId(), new LazyLoadEmoji(b.getName(), "moderator".equals(b.getReplaces()) ? UserType.MOD.getKey() : b.getReplaces(), "https:" + b.getImage(), b.getColorParsed(), EmojiType.FRANKERFACEZ_BADGE));
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
            LazyLoadEmoji modLle = new LazyLoadEmoji(UserType.MOD.getKey(), UserType.MOD.getKey(), "https:" + room.getModerator_badge(), ConfigEmoji.MOD_BADGE_COLOR, EmojiType.FRANKERFACEZ_BADGE);
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
                    lle = new LazyLoadEmoji(e.getName(), "https:" + e.getUrls().get(key), e.getWidth(), e.getHeight(), isGlobal ? EmojiType.FRANKERFACEZ_GLOBAL : EmojiType.FRANKERFACEZ_CHANNEL);
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
