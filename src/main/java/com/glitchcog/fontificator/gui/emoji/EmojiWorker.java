package com.glitchcog.fontificator.gui.emoji;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.emoji.EmojiManager;
import com.glitchcog.fontificator.emoji.EmojiOperation;
import com.glitchcog.fontificator.emoji.EmojiType;
import com.glitchcog.fontificator.emoji.LazyLoadEmoji;
import com.glitchcog.fontificator.emoji.loader.EmojiApiLoader;
import com.glitchcog.fontificator.emoji.loader.EmojiParser;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;

/**
 * Loads and caches emoji in a SwingWorker thread
 * 
 * @author Matt Yanos
 */
public class EmojiWorker extends SwingWorker<Integer, EmojiWorkerReport>
{
    private static final Logger logger = Logger.getLogger(EmojiWorker.class);

    // @formatter:off
    private final String[] TWITCH_EMOTES_GLOBAL = new String[] { 
        "4Head", "ANELE", "ArgieB8", "ArsonNoSexy", "AsianGlow", "AtGL", "AthenaPMS", "AtIvy", "AtWW", "BabyRage", "BatChest", "BCWarrior", "BibleThump", "BigBrother", "BionicBunion", "BlargNaut", 
        "bleedPurple", "BloodTrail", "BORT", "BrainSlug", "BrokeBack", "BuddhaBar", "CoolCat", "CorgiDerp", "CougarHunt", "DAESuppy", "DansGame", "DatHass", "DatSheffy", "DBstyle", "deExcite", 
        "deIlluminati", "DendiFace", "DogFace", "DOOMGuy", "duDudu", "EagleEye", "EleGiggle", "EvilFetus", "FailFish", "FPSMarksman", "FrankerZ", "FreakinStinkin", "FUNgineer", "FunRun", "FuzzyOtterOO", 
        "GasJoker", "GingerPower", "GrammarKing", "HassanChop", "HeyGuys", "HotPokket", "HumbleLife", "ItsBoshyTime", "Jebaited", "JKanStyle", "JonCarnage", "KAPOW", "Kappa", "KappaPride", "Keepo", 
        "KevinTurtle", "Kippa", "Kreygasm", "KZskull", "Mau5", "mcaT", "MechaSupes", "MrDestructoid", "MVGame", "NightBat", "NinjaTroll", "NoNoSpot", "NotATK", "NotLikeThis", "OMGScoots", "OneHand", 
        "OpieOP", "OptimizePrime", "OSbeaver", "OSbury", "OSdeo", "OSfrog", "OSkomodo", "OSrob", "OSsloth", "panicBasket", "PanicVis", "PazPazowitz", "PeoplesChamp", "PermaSmug", "PicoMause", "PipeHype", 
        "PJHarley", "PJSalt", "PMSTwin", "PogChamp", "Poooound", "PraiseIt", "PRChase", "PunchTrees", "PuppeyFace", "RaccAttack", "RalpherZ", "RedCoat", "ResidentSleeper", "riPepperonis", "RitzMitz", 
        "RuleFive", "ShadyLulu", "Shazam", "shazamicon", "ShazBotstix", "ShibeZ", "SMOrc", "SMSkull", "SoBayed", "SoonerLater", "SriHead", "SSSsss", "StoneLightning", "StrawBeary", "SuperVinlin", 
        "SwiftRage", "tbBaconBiscuit", "tbChickenBiscuit", "tbQuesarito", "tbSausageBiscuit", "tbSpicy", "tbSriracha", "TF2John", "TheKing", "TheRinger", "TheTarFu", "TheThing", "ThunBeast", "TinyFace", 
        "TooSpicy", "TriHard", "TTours", "twitchRaid", "UleetBackup", "UncleNox", "UnSane", "VaultBoy", "Volcania", "WholeWheat", "WinWaker", "WTRuck", "WutFace", "YouWHY"
    };

    private final String[] TWITCH_EMOTES_BASIC = new String[] { 
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

    private final String channel;

    private final EmojiType emojiType;

    private final EmojiOperation opType;

    private boolean terminateWork;

    private EmojiLoadProgressPanel progressPopup;

    private final EmojiWorkerReport initialReport;

    /**
     * Construct an emoji worker
     * 
     * @param manager
     * @param progressPopup
     * @param channel
     * @param emojiType
     * @param opType
     * @param logBox
     * @param initialReport
     */
    public EmojiWorker(EmojiManager manager, EmojiLoadProgressPanel progressPopup, String channel, EmojiType emojiType, EmojiOperation opType, LogBox logBox, EmojiWorkerReport initialReport)
    {
        this.terminateWork = false;

        this.manager = manager;
        this.progressPopup = progressPopup;
        this.channel = channel;
        this.emojiType = emojiType;
        this.opType = opType;
        this.initialReport = initialReport;

        loader = new EmojiApiLoader();
        parser = new EmojiParser(logBox);
    }

    @Override
    protected Integer doInBackground() throws Exception
    {
        if (channel == null || emojiType == null || opType == null)
        {
            return Integer.valueOf(1);
        }

        try
        {
            terminateWork = false;

            if (EmojiOperation.LOAD == opType)
            {
                loader.prepLoad(emojiType, channel);
                loader.initLoad();
                while (!loader.isLoadComplete())
                {
                    int percentComplete = loader.loadChunk();
                    // Let the thread rest so the main thread can get the publish
                    publish(new EmojiWorkerReport("Downloading " + emojiType.getDescription(), percentComplete));
                    if (terminateWork)
                    {
                        throw new EmojiCancelException();
                    }
                    Thread.sleep(1L);
                }
                String jsonData = loader.getLoadedJson();
                loader.reset();
                if (terminateWork)
                {
                    throw new EmojiCancelException();
                }
                publish(new EmojiWorkerReport("Parsing " + emojiType.getDescription() + " data...", 0));
                Thread.sleep(1L);
                parser.putJsonEmojiIntoManager(manager, emojiType, jsonData);
                publish(new EmojiWorkerReport(emojiType.getDescription() + " loading complete", 100));
                Thread.sleep(1L);
            }
            else if (EmojiOperation.CACHE == opType)
            {
                List<String> regexes;
                if (emojiType.isTwitchEmote())
                {
                    Collection<String> ffzChannelRegexes = manager.getEmojiByType(EmojiType.FRANKERFACEZ_CHANNEL).keySet();
                    Collection<String> ffzGlobalRegexes = manager.getEmojiByType(EmojiType.FRANKERFACEZ_GLOBAL).keySet();
                    regexes = new ArrayList<String>(ffzChannelRegexes.size() + ffzGlobalRegexes.size());
                    regexes.addAll(ffzChannelRegexes);
                    regexes.addAll(ffzGlobalRegexes);
                }
                else if (emojiType.isFrankerFaceZEmote())
                {
                    regexes = new ArrayList<String>(TWITCH_EMOTES_BASIC.length + TWITCH_EMOTES_GLOBAL.length);
                    Collections.addAll(regexes, TWITCH_EMOTES_BASIC);
                    Collections.addAll(regexes, TWITCH_EMOTES_GLOBAL);
                }
                else
                {
                    regexes = new ArrayList<String>();
                }

                publish(new EmojiWorkerReport("Caching " + emojiType.getDescription(), 0));
                Thread.sleep(1L);
                int count = 0;
                for (String regex : regexes)
                {
                    LazyLoadEmoji[] emoji = manager.getEmoji(regex);
                    // Null check here in the for loop test for safety
                    for (int e = 0; emoji != null && e < emoji.length; e++)
                    {
                        emoji[e].getImage();
                    }
                    count++;
                    int percentComplete = (int) (100.0f * count / regexes.size());
                    publish(new EmojiWorkerReport("Caching " + emojiType.getDescription(), percentComplete));

                    if (terminateWork)
                    {
                        throw new EmojiCancelException();
                    }

                    Thread.sleep(1L);
                }
                publish(new EmojiWorkerReport(emojiType.getDescription() + " caching complete", 100));
                Thread.sleep(1L);
            }

            return Integer.valueOf(0);
        }
        catch (EmojiCancelException e)
        {
            // Don't let it get caught by the generic Exception, send it on up instead
            throw e;
        }
        catch (FileNotFoundException e)
        {
            publish(new EmojiWorkerReport("Error", 100));
            Thread.sleep(1L);
            final String errorMsg = "Unable to open URL to " + opType.getDescription() + " " + emojiType.getDescription() + " for channel " + channel;
            ChatWindow.popup.handleProblem(errorMsg, e);
            return Integer.valueOf(2);
        }
        catch (Exception e)
        {
            publish(new EmojiWorkerReport("Error", 100));
            Thread.sleep(1L);
            final String errorMsg = "Unknown error trying to " + opType.getDescription() + " " + emojiType.getDescription() + " for channel " + channel;
            logger.error(errorMsg, e);
            ChatWindow.popup.handleProblem(errorMsg, e);
            throw new EmojiCancelException();
        }
    }

    @Override
    protected void process(List<EmojiWorkerReport> reports)
    {
        if (reports != null && !reports.isEmpty())
        {
            EmojiWorkerReport latestReport = reports.get(reports.size() - 1);
            reports.clear();
            progressPopup.update(latestReport);
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

    public EmojiType getEmojiType()
    {
        return emojiType;
    }

    public EmojiOperation getEmojiOp()
    {
        return opType;
    }

    public String getChannel()
    {
        return channel;
    }
}
