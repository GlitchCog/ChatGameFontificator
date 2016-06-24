package com.glitchcog.fontificator.gui.emoji;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.emoji.EmojiJob;
import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.emoji.EmojiOperation;
import com.glitchcog.fontificator.emoji.EmojiType;
import com.glitchcog.fontificator.emoji.LazyLoadEmoji;
import com.glitchcog.fontificator.emoji.loader.EmojiApiLoader;
import com.glitchcog.fontificator.emoji.loader.EmojiParser;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;

/**
 * Loads and caches emoji in a SwingWorker thread
 * 
 * @author Matt Yanos
 */
public class EmojiWorker extends SwingWorker<Integer, EmojiWorkerReport>
{
    private static final Logger logger = Logger.getLogger(EmojiWorker.class);

    /**
     * Twitch gives a specific emote ID with each post, but identifies all its V3 emotes with a broader "set" ID, so
     * this API links the narrow emote ID to a wider emote set ID. This is an ineffective way to map these data, but
     * Twitch's V3 API demands it.
     */
    private static final String TWITCH_URL_EMOTE_ID_TO_SET_ID_MAP = "https://api.twitch.tv/kraken/chat/emoticon_images";

    /**
     * The base URL for getting just the room data from the FrankerFaceZ API, without any emote data
     */
    private static final String FFZ_BASE_NO_EMOTES_URL = "https://api.frankerfacez.com/v1/_room/";

    // @formatter:off

    /**
     * V1 Twitch emote IDs for the global emotes, last updated 2015 November 30
     */
    private static final Integer[] TWITCH_EMOTE_IDS_GLOBAL = new Integer[] {
        27, 15, 25, 21, 19, 22, 18, 
        29, 28, 17, 16, 26, 24, 20, 
        33, 30, 45, 38, 40, 44, 34, 
        31, 41, 42, 39, 36, 32, 37, 
        46, 69, 47, 50, 70, 66, 51, 
        48, 67, 49, 52, 68, 65, 71, 
        88, 72, 169, 91, 166, 74, 87, 
        167, 73, 170, 92, 86, 168, 90, 
        171, 361, 357, 354, 243, 881, 244, 
        358, 355, 245, 359, 360, 356, 973, 
        1896, 1904, 1900, 1905, 1897, 3632, 1901, 
        1906, 1902, 3666, 1898, 3412, 1903, 1899, 
        7427, 3668, 4337, 9804, 9803, 9800, 3792, 
        4338, 4057, 4339, 9801, 4240, 9802, 5253, 
        9805, 27679, 9809, 22998, 9806, 27509, 27903, 
        14706, 20225, 28087, 9807, 9808, 22639, 27602, 
        28328, 35063, 46881, 30134, 38436, 47005, 30259, 
        46248, 38586, 46249, 47007, 32035, 44499, 34875, 
        47008, 56879, 47010, 49106, 54089, 50901, 56880, 
        47011, 51838, 54090, 47302, 52492, 47420, 55338, 
        56881, 70433, 62835, 58765, 58127, 62836, 62833, 
        68856, 58135, 56882, 58136, 56883, 64138, 62834, 
        72748, 72752, 72753, 72751, 72750, 72749
    };

    /**
     * Word keys for Twitch global emotes, last updated 2015 November 30
     */
    private static final String[] TWITCH_EMOTES_GLOBAL = new String[] { 
        "JKanStyle", "OptimizePrime", "StoneLightning", "TheRinger", "PazPazowitz", "EagleEye", "CougarHunt", 
        "RedCoat", "BionicBunion", "Kappa", "JonCarnage", "PicoMause", "MrDestructoid", "MVGame", 
        "BCWarrior", "SuperVinlin", "GingerPower", "DansGame", "SwiftRage", "PJSalt", "StrawBeary", 
        "BlargNaut", "FreakinStinkin", "KevinTurtle", "Kreygasm", "FPSMarksman", "NoNoSpot", "NinjaTroll", 
        "SSSsss", "PunchTrees", "FunRun", "UleetBackup", "ArsonNoSexy", "SMSkull", "SMOrc", 
        "FrankerZ", "OneHand", "TinyFace", "HassanChop", "BloodTrail", "TheTarFu", "UnSane", 
        "EvilFetus", "DBstyle", "AsianGlow", "BibleThump", "ShazBotstix", "PogChamp", "Jebaited", 
        "OMGScoots", "PMSTwin", "Volcania", "WinWaker", "FuzzyOtterOO", "ItsBoshyTime", "DatSheffy", 
        "TriHard", "BORT", "FUNgineer", "ResidentSleeper", "4Head", "SoonerLater", "OpieOP", 
        "HotPokket", "Poooound", "TooSpicy", "FailFish", "RuleFive", "BrainSlug", "DAESuppy", 
        "WholeWheat", "WTRuck", "ThunBeast", "TF2John", "RalpherZ", "Kippa", "Keepo", 
        "DogFace", "BigBrother", "BatChest", "SoBayed", "PeoplesChamp", "GrammarKing", "UncleNox", 
        "PanicVis", "ANELE", "BrokeBack", "PipeHype", "YouWHY", "RitzMitz", "EleGiggle", 
        "KZskull", "TheThing", "AtIvy", "AtWW", "GasJoker", "KAPOW", "MechaSupes", 
        "NightBat", "shazamicon", "Shazam", "PJHarley", "AtGL", "SriHead", "DatHass", 
        "BabyRage", "panicBasket", "PermaSmug", "BuddhaBar", "RaccAttack", "ShibeZ", "WutFace", 
        "PRChase", "Mau5", "HeyGuys", "AthenaPMS", "NotATK", "mcaT", "TTours", 
        "PraiseIt", "tbBaconBiscuit", "deIlluminati", "deExcite", "HumbleLife", "OSbeaver", "OSdeo", 
        "OSfrog", "OSkomodo", "OSsloth", "OSrob", "OSbury", "CorgiDerp", "TheKing", 
        "ArgieB8", "ShadyLulu", "DOOMGuy", "VaultBoy", "KappaPride", "tbChickenBiscuit", "tbSriracha", 
        "tbSausageBiscuit", "tbSpicy", "tbQuesarito", "CoolCat", "DendiFace", "PuppeyFace", "NotLikeThis", 
        "riPepperonis", "duDudu", "bleedPurple", "twitchRaid", "SeemsGood", "MingLee", "KappaRoss", 
        "PeteZaroll", "PeteZarollTie", "Stormtrooper", "Sullustan", "Ackbar", "ItsATrap"
    };

    /**
     * V1 Twitch emote IDs for the basic emotes (not turbo), last updated 2015 November 30
     */
    private static final Integer[] TWITCH_EMOTE_IDS_BASIC = new Integer[] {
        1, 12, 7, 2, 3, 13, 8, 
        9, 4, 14, 5, 10, 11, 6
    };

    /**
     * Regular expression keys for Twitch basic emotes, last updated 2015 November 30
     */
    private static final String[] TWITCH_EMOTES_BASIC = new String[] { 
        "B-?\\)", "\\:-?[z|Z|\\|]", "\\:-?\\)", "\\:-?\\(", 
        "\\:-?(p|P)", "\\;-?(p|P)", 
        "\\<3", // Modified from Twitch Emote API V3, because they have it wrong
        "\\;-?\\)", 
        "R-?\\)", "[o|O](_|\\.)[o|O]", "\\:-?D", "\\:-?(o|O)", 
        "\\>\\;\\(", "\\:-?[\\\\/]"
    };

    // @formatter:on

    private EmojiManager manager;

    private EmojiApiLoader loader;

    private EmojiParser parser;

    private EmojiJob job;

    private boolean terminateWork;

    private EmojiLoadProgressPanel progressPanel;

    private final EmojiWorkerReport initialReport;

    /**
     * Construct an emoji worker
     * 
     * @param manager
     * @param progressPanel
     * @param channel
     * @param emojiType
     * @param opType
     * @param logBox
     * @param initialReport
     */
    public EmojiWorker(EmojiManager manager, EmojiLoadProgressPanel progressPanel, EmojiJob job, LogBox logBox, EmojiWorkerReport initialReport)
    {
        this.terminateWork = false;

        this.manager = manager;
        this.progressPanel = progressPanel;
        this.job = job;
        this.initialReport = initialReport;
        logBox.log(initialReport.getMessage());

        loader = new EmojiApiLoader();
        parser = new EmojiParser(logBox);
    }

    @Override
    protected Integer doInBackground() throws Exception
    {
        if (job.getType() == null || job.getOp() == null)
        {
            return Integer.valueOf(1);
        }

        try
        {
            terminateWork = false;

            final EmojiOperation opType = job.getOp();
            final EmojiType emojiType = job.getType();
            final String channel = job.getChannel();

            if (EmojiOperation.LOAD == opType)
            {
                // Some custom loading required to get the set map from Twitch API, if it's Twitch V2/V3 emotes being loaded
                String jsonSetMapData = null;
                if (emojiType.isLoadSetMap())
                {
                    loader.prepLoad(TWITCH_URL_EMOTE_ID_TO_SET_ID_MAP);
                    jsonSetMapData = runLoader(emojiType);
                }

                // The proper load for the emoji
                loader.prepLoad(emojiType, channel);
                String data = runLoader(emojiType);
                if (data != null)
                {
                    parser.putJsonEmojiIntoManager(manager, emojiType, data, jsonSetMapData);
                }

                // Some custom loading required for custom FFZ moderator badges
                if (emojiType == EmojiType.FRANKERFACEZ_BADGE)
                {
                    loader.prepLoad(FFZ_BASE_NO_EMOTES_URL + channel);
                    String ffzRoomJson = runLoader(emojiType);
                    parser.parseFrankerFaceZModBadge(manager, ffzRoomJson);
                }

                Thread.sleep(1L);
            }
            else if (EmojiOperation.CACHE == opType)
            {
                List<String> regexes;
                List<Integer> ids;
                if (emojiType.isTwitchEmote())
                {
                    // This is for Twitch emote V2 or V3. It typically will be whichever is set as the default in the
                    // emoji control panel
                    regexes = new ArrayList<String>(TWITCH_EMOTES_BASIC.length + TWITCH_EMOTES_GLOBAL.length);
                    Collections.addAll(regexes, TWITCH_EMOTES_BASIC);
                    Collections.addAll(regexes, TWITCH_EMOTES_GLOBAL);

                    // This is for Twitch V1 emotes, done by ID. This is how Twitch emotes are actually used, so it
                    // should always be tripped
                    ids = new ArrayList<Integer>(TWITCH_EMOTE_IDS_BASIC.length + TWITCH_EMOTE_IDS_GLOBAL.length);
                    Collections.addAll(ids, TWITCH_EMOTE_IDS_BASIC);
                    Collections.addAll(ids, TWITCH_EMOTE_IDS_GLOBAL);
                }
                else if (emojiType.isFrankerFaceZEmote())
                {
                    Collection<String> ffzChannelRegexes = manager.getEmojiByType(EmojiType.FRANKERFACEZ_CHANNEL).keySet();
                    Collection<String> ffzGlobalRegexes = manager.getEmojiByType(EmojiType.FRANKERFACEZ_GLOBAL).keySet();
                    regexes = new ArrayList<String>(ffzChannelRegexes.size() + ffzGlobalRegexes.size());
                    regexes.addAll(ffzChannelRegexes);
                    regexes.addAll(ffzGlobalRegexes);
                    ids = new ArrayList<Integer>(); // Unused for FFZ
                }
                else if (emojiType.isBetterTtvEmote())
                {
                    Collection<String> bttvChannelRegexes = manager.getEmojiByType(EmojiType.BETTER_TTV_CHANNEL).keySet();
                    Collection<String> bttvGlobalRegexes = manager.getEmojiByType(EmojiType.BETTER_TTV_GLOBAL).keySet();
                    regexes = new ArrayList<String>(bttvChannelRegexes.size() + bttvGlobalRegexes.size());
                    regexes.addAll(bttvChannelRegexes);
                    regexes.addAll(bttvGlobalRegexes);
                    ids = new ArrayList<Integer>(); // Unused for BTTV
                }
                else
                {
                    // Nothing to do
                    regexes = new ArrayList<String>();
                    ids = new ArrayList<Integer>();
                }

                publish(new EmojiWorkerReport("Caching " + emojiType.getDescription(), 0, false, false));
                Thread.sleep(1L);
                int count = 0;
                List<LazyLoadEmoji> emojiToCache = new ArrayList<LazyLoadEmoji>();
                for (String regex : regexes)
                {
                    LazyLoadEmoji emoji = manager.getEmoji(emojiType, regex, null);
                    emojiToCache.add(emoji);
                }
                for (Integer id : ids)
                {
                    LazyLoadEmoji emoji = manager.getEmojiById(id, null, null);
                    emojiToCache.add(emoji);
                }

                for (LazyLoadEmoji emoji : emojiToCache)
                {
                    if (emoji != null)
                    {
                        emoji.getImage();
                    }
                    count++;
                    // This is safe from divide by zero exceptions, because we won't be here if emojiToCache is empty
                    int percentComplete = (int) (100.0f * count / emojiToCache.size());
                    publish(new EmojiWorkerReport("Caching " + emojiType.getDescription(), percentComplete, false, false));

                    if (terminateWork)
                    {
                        throw new EmojiCancelException();
                    }

                    Thread.sleep(1L);
                }

                publish(new EmojiWorkerReport(emojiType.getDescription() + " caching complete", 100, false, false));
                Thread.sleep(1L);
            }

            return Integer.valueOf(0);
        }
        catch (EmojiCancelException e)
        {
            // Don't let it get caught by the generic Exception, send it on up instead
            publish(new EmojiWorkerReport(job.toString() + " canceled", 100, false, true));
            try
            {
                Thread.sleep(1L);
            }
            catch (Exception sleepException)
            {
                logger.debug("Couldn't sleep", sleepException);
            }
            throw e;
        }
        catch (FileNotFoundException e)
        {
            publish(new EmojiWorkerReport("URL not found trying to " + job.toString(), 100, true, false));
            Thread.sleep(1L);
            return Integer.valueOf(2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            final String errorMsg = "Unknown error trying to " + job.getOp().getDescription() + " " + job.getType().getDescription() + (job.getChannel() != null ? " for channel " + job.getChannel() : "");
            publish(new EmojiWorkerReport(errorMsg, 100, true, false));
            Thread.sleep(1L);
            return Integer.valueOf(3);
        }
    }

    /**
     * Gives you back the data from a website, used to get JSON data for emoji, or for loading the FFZ donor list
     * 
     * @param emojiType
     * @return
     * @throws Exception
     */
    private String runLoader(EmojiType emojiType) throws Exception
    {
        String data = null;
        if (loader.initLoad())
        {
            while (!loader.isLoadComplete())
            {
                int percentComplete = loader.loadChunk();
                // Let the thread rest so the main thread can get the publish
                publish(new EmojiWorkerReport("Downloading " + emojiType.getDescription(), percentComplete, false, false));
                if (terminateWork)
                {
                    throw new EmojiCancelException();
                }
                Thread.sleep(1L);
            }
            data = loader.getLoadedJson();
            loader.reset();
            if (terminateWork)
            {
                throw new EmojiCancelException();
            }
            publish(new EmojiWorkerReport("Parsing " + emojiType.getDescription() + " data...", 0, false, false));
        }
        else
        {
            logger.debug("EmojiApiLoader run for " + emojiType.getDescription() + " without required call to prepLoad.");
        }
        publish(new EmojiWorkerReport(emojiType.getDescription() + " loading complete", 100, false, false));

        loader.reset();

        return data;
    }

    @Override
    protected void process(List<EmojiWorkerReport> reports)
    {
        if (reports != null && !reports.isEmpty())
        {
            EmojiWorkerReport latestReport = reports.get(reports.size() - 1);
            reports.clear();
            progressPanel.update(latestReport);
        }
    }

    @Override
    protected void done()
    {
        terminateWork = true;
    }

    public void cancel()
    {
        terminateWork = true;
    }

    public EmojiWorkerReport getInitialReport()
    {
        return initialReport;
    }

    public EmojiJob getEmojiJob()
    {
        return job;
    }

}
