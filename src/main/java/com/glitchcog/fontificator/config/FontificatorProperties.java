package com.glitchcog.fontificator.config;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;

import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.controls.ControlWindow;
import com.glitchcog.fontificator.sprite.SpriteFont;

/**
 * Contains all the properties for configuring the font display. They are divided into member Config variables whose
 * reference is shared by the ControlPanels and ChatPanel:
 * <ul>
 * <li>ConfigIrc</li>
 * <li>ConfigFont</li>
 * <li>ConfigChat</li>
 * <li>ConfigColor</li>
 * <li>ConfigMessage</li>
 * <li>ConfigEmoji</li>
 * <li>ConfigCensor</li>
 * <li></li>
 * </ul>
 * 
 * @author Matt Yanos
 */
public class FontificatorProperties extends Properties
{
    private static final Logger logger = Logger.getLogger(FontificatorProperties.class);

    private static final long serialVersionUID = 1L;

    /**
     * Copy of properties to compare with when checking if there have been changes
     */
    private FontificatorProperties lastSavedCopy;

    private final static String ENC_PASSWORD = "Eastmost penninsula is the secret.";

    /**
     * The name of the file that holds the location of the last file saved or loaded by a user, to be used when the
     * program starts to automatically load the previously used configuration
     */
    private static final String CONFIG_FILE_LAST_LOCATION = ".fontificator.conf";

    public static final String KEY_IRC_USER = "ircUser";
    public static final String KEY_IRC_HOST = "ircHost";
    public static final String KEY_IRC_PORT = "ircPort";
    public static final String KEY_IRC_AUTH = "ircAuth";
    public static final String KEY_IRC_ANON = "ircAnon";
    public static final String KEY_IRC_CHAN = "ircChannel";
    public static final String KEY_IRC_AUTO_RECONNECT = "ircAutoReconnect";

    public static final String[] IRC_KEYS = new String[] { KEY_IRC_USER, KEY_IRC_HOST, KEY_IRC_PORT, KEY_IRC_AUTH, KEY_IRC_ANON, KEY_IRC_CHAN, KEY_IRC_AUTO_RECONNECT };

    public static final String KEY_FONT_FILE_BORDER = "fontBorderFile";
    public static final String KEY_FONT_FILE_FONT = "fontFile";
    public static final String KEY_FONT_TYPE = "fontType";
    public static final String KEY_FONT_GRID_WIDTH = "fontGridWidth";
    public static final String KEY_FONT_GRID_HEIGHT = "fontGridHeight";
    public static final String KEY_FONT_SCALE = "fontScale";
    public static final String KEY_FONT_BORDER_SCALE = "fontBorderScale";
    public static final String KEY_FONT_BORDER_INSET_X = "fontBorderInsetX";
    public static final String KEY_FONT_BORDER_INSET_Y = "fontBorderInsetY";
    public static final String KEY_FONT_SPACE_WIDTH = "fontSpaceWidth";
    public static final String KEY_FONT_BASELINE_OFFSET = "fontBaselineOffset";
    public static final String KEY_FONT_UNKNOWN_CHAR = "fontUnknownChar";
    public static final String KEY_FONT_EXTENDED_CHAR = "fontExtendedChar";
    public static final String KEY_FONT_CHARACTERS = "fontCharacters";
    public static final String KEY_FONT_SPACING_LINE = "fontLineSpacing";
    public static final String KEY_FONT_SPACING_CHAR = "fontCharSpacing";
    public static final String KEY_FONT_SPACING_MESSAGE = "fontMessageSpacing";

    public static final String[] FONT_KEYS = new String[] { KEY_FONT_FILE_BORDER, KEY_FONT_FILE_FONT, KEY_FONT_TYPE, KEY_FONT_GRID_WIDTH, KEY_FONT_GRID_HEIGHT, KEY_FONT_SCALE, KEY_FONT_BORDER_SCALE, KEY_FONT_BORDER_INSET_X, KEY_FONT_BORDER_INSET_Y, KEY_FONT_SPACE_WIDTH, KEY_FONT_BASELINE_OFFSET, KEY_FONT_UNKNOWN_CHAR, KEY_FONT_EXTENDED_CHAR, KEY_FONT_CHARACTERS, KEY_FONT_SPACING_LINE, KEY_FONT_SPACING_CHAR, KEY_FONT_SPACING_MESSAGE };

    public static final String KEY_CHAT_SCROLL = "chatScrollEnabled";
    public static final String KEY_CHAT_RESIZABLE = "chatResizable";
    public static final String KEY_CHAT_POSITION = "chatPosition";
    public static final String KEY_CHAT_POSITION_X = "chatPositionX";
    public static final String KEY_CHAT_POSITION_Y = "chatPositionY";
    public static final String KEY_CHAT_FROM_BOTTOM = "chatFromBottom";
    public static final String KEY_CHAT_WINDOW_WIDTH = "chatWidth"; // Legacy, no longer saved, used for chat window, not chat window content pane
    public static final String KEY_CHAT_WINDOW_HEIGHT = "chatHeight"; // Legacy, no longer saved, used for chat window, not chat window content pane
    public static final String KEY_CHAT_WIDTH = "chatPixelWidth";
    public static final String KEY_CHAT_HEIGHT = "chatPixelHeight";
    public static final String KEY_CHAT_CHROMA_ENABLED = "chromaEnabled";
    public static final String KEY_CHAT_INVERT_CHROMA = "invertChroma";
    public static final String KEY_CHAT_REVERSE_SCROLLING = "reverseScrolling";
    public static final String KEY_CHAT_CHROMA_LEFT = "chromaLeft";
    public static final String KEY_CHAT_CHROMA_TOP = "chromaTop";
    public static final String KEY_CHAT_CHROMA_RIGHT = "chromaRight";
    public static final String KEY_CHAT_CHROMA_BOTTOM = "chromaBottom";
    public static final String KEY_CHAT_CHROMA_CORNER = "chromaCornerRadius";
    public static final String KEY_CHAT_ALWAYS_ON_TOP = "chatAlwaysOnTop";
    public static final String KEY_CHAT_ANTIALIAS = "chatAntialias";

    public static final String[] CHAT_KEYS = new String[] { KEY_CHAT_SCROLL, KEY_CHAT_RESIZABLE, KEY_CHAT_POSITION, KEY_CHAT_POSITION_X, KEY_CHAT_POSITION_Y, KEY_CHAT_FROM_BOTTOM, KEY_CHAT_WIDTH, KEY_CHAT_HEIGHT, KEY_CHAT_CHROMA_ENABLED, KEY_CHAT_INVERT_CHROMA, KEY_CHAT_REVERSE_SCROLLING, KEY_CHAT_CHROMA_LEFT, KEY_CHAT_CHROMA_TOP, KEY_CHAT_CHROMA_RIGHT, KEY_CHAT_CHROMA_BOTTOM, KEY_CHAT_CHROMA_CORNER, KEY_CHAT_ALWAYS_ON_TOP, KEY_CHAT_ANTIALIAS };

    public static final String[] CHAT_KEYS_EXCEPT_WINDOW_POSITION = new String[] { KEY_CHAT_SCROLL, KEY_CHAT_RESIZABLE, KEY_CHAT_POSITION, KEY_CHAT_FROM_BOTTOM, KEY_CHAT_WIDTH, KEY_CHAT_HEIGHT, KEY_CHAT_CHROMA_ENABLED, KEY_CHAT_INVERT_CHROMA, KEY_CHAT_REVERSE_SCROLLING, KEY_CHAT_CHROMA_LEFT, KEY_CHAT_CHROMA_TOP, KEY_CHAT_CHROMA_RIGHT, KEY_CHAT_CHROMA_BOTTOM, KEY_CHAT_CHROMA_CORNER, KEY_CHAT_ALWAYS_ON_TOP, KEY_CHAT_ANTIALIAS };

    public static final String KEY_COLOR_BG = "colorBackground";
    public static final String KEY_COLOR_FG = "colorForeground";
    public static final String KEY_COLOR_BORDER = "colorBorder";
    public static final String KEY_COLOR_HIGHLIGHT = "colorHighlight";
    public static final String KEY_COLOR_CHROMA_KEY = "chromaKey";
    public static final String KEY_COLOR_PALETTE = "colorPalette";
    public static final String KEY_COLOR_USERNAME = "colorUsername";
    public static final String KEY_COLOR_TIMESTAMP = "colorTimestamp";
    public static final String KEY_COLOR_MESSAGE = "colorMessage";
    public static final String KEY_COLOR_JOIN = "colorJoin";
    public static final String KEY_COLOR_TWITCH = "colorUseTwitch";

    public static final String[] COLOR_KEYS = new String[] { KEY_COLOR_BG, KEY_COLOR_FG, KEY_COLOR_BORDER, KEY_COLOR_HIGHLIGHT, KEY_COLOR_CHROMA_KEY, KEY_COLOR_PALETTE, KEY_COLOR_USERNAME, KEY_COLOR_TIMESTAMP, KEY_COLOR_MESSAGE, KEY_COLOR_JOIN, KEY_COLOR_TWITCH };

    public static final String[] COLOR_KEYS_WITHOUT_PALETTE = new String[] { KEY_COLOR_BG, KEY_COLOR_FG, KEY_COLOR_BORDER, KEY_COLOR_HIGHLIGHT, KEY_COLOR_CHROMA_KEY, KEY_COLOR_USERNAME, KEY_COLOR_TIMESTAMP, KEY_COLOR_MESSAGE, KEY_COLOR_JOIN, KEY_COLOR_TWITCH };

    public static final String KEY_MESSAGE_JOIN = "messageShowJoin";
    public static final String KEY_MESSAGE_USERNAME = "messageShowUsername";
    public static final String KEY_MESSAGE_TIMESTAMP = "messageShowTimestamp";
    public static final String KEY_MESSAGE_USERFORMAT = "messageUsernameFormat";
    public static final String KEY_MESSAGE_TIMEFORMAT = "messageTimestampFormat";
    public static final String KEY_MESSAGE_CONTENT_BREAK = "messageContentBreak";
    public static final String KEY_MESSAGE_QUEUE_SIZE = "messageQueueSize";
    public static final String KEY_MESSAGE_SPEED = "messageSpeed";
    public static final String KEY_MESSAGE_EXPIRATION_TIME = "messageExpirationTime";
    public static final String KEY_MESSAGE_HIDE_EMPTY_BORDER = "messageHideEmptyBorder";
    public static final String KEY_MESSAGE_HIDE_EMPTY_BACKGROUND = "messageHideEmptyBackground";
    public static final String KEY_MESSAGE_CASE_TYPE = "messageUserCase";
    public static final String KEY_MESSAGE_CASE_SPECIFY = "messageUserCaseSpecify";
    public static final String KEY_MESSAGE_CASING = "messageCasing";

    public static final String[] MESSAGE_KEYS = new String[] { KEY_MESSAGE_JOIN, KEY_MESSAGE_USERNAME, KEY_MESSAGE_TIMESTAMP, KEY_MESSAGE_USERFORMAT, KEY_MESSAGE_TIMEFORMAT, KEY_MESSAGE_CONTENT_BREAK, KEY_MESSAGE_QUEUE_SIZE, KEY_MESSAGE_SPEED, KEY_MESSAGE_EXPIRATION_TIME, KEY_MESSAGE_HIDE_EMPTY_BORDER, KEY_MESSAGE_HIDE_EMPTY_BACKGROUND, KEY_MESSAGE_CASE_TYPE, KEY_MESSAGE_CASE_SPECIFY, KEY_MESSAGE_CASING };

    public static final String KEY_EMOJI_ENABLED = "emojiEnabled";
    public static final String KEY_EMOJI_ANIMATION = "emojiAnimationEnabled";
    public static final String KEY_EMOJI_TWITCH_BADGES = "badgesEnabled";
    public static final String KEY_EMOJI_FFZ_BADGES = "badgesFfzEnabled";
    public static final String KEY_EMOJI_SCALE_TO_LINE = "emojiScaleToLine";
    public static final String KEY_EMOJI_BADGE_SCALE_TO_LINE = "badgeScaleToLine";
    public static final String KEY_EMOJI_BADGE_HEIGHT_OFFSET = "badgeHeightOffset";
    public static final String KEY_EMOJI_SCALE = "emojiScale";
    public static final String KEY_EMOJI_BADGE_SCALE = "badgeScale";
    public static final String KEY_EMOJI_DISPLAY_STRAT = "emojiDisplayStrat";
    public static final String KEY_EMOJI_TWITCH_ENABLE = "emojiTwitchEnabled";
    public static final String KEY_EMOJI_TWITCH_CACHE = "emojiTwitchCached";
    public static final String KEY_EMOJI_FFZ_ENABLE = "emojiFfzEnabled";
    public static final String KEY_EMOJI_FFZ_CACHE = "emojiFfzCached";
    public static final String KEY_EMOJI_BTTV_ENABLE = "emojiBttvEnabled";
    public static final String KEY_EMOJI_BTTV_CACHE = "emojiBttvCached";
    public static final String KEY_EMOJI_TWITTER_ENABLE = "emojiTwitterEnabled";

    public static final String[] EMOJI_KEYS = new String[] { KEY_EMOJI_ENABLED, KEY_EMOJI_ANIMATION, KEY_EMOJI_TWITCH_BADGES, KEY_EMOJI_FFZ_BADGES, KEY_EMOJI_SCALE_TO_LINE, KEY_EMOJI_BADGE_SCALE_TO_LINE, KEY_EMOJI_BADGE_HEIGHT_OFFSET, KEY_EMOJI_SCALE, KEY_EMOJI_BADGE_SCALE, KEY_EMOJI_DISPLAY_STRAT, KEY_EMOJI_TWITCH_ENABLE, KEY_EMOJI_TWITCH_CACHE, KEY_EMOJI_FFZ_ENABLE, KEY_EMOJI_FFZ_CACHE, KEY_EMOJI_BTTV_ENABLE, KEY_EMOJI_BTTV_CACHE, KEY_EMOJI_TWITTER_ENABLE };

    public static final String KEY_CENSOR_ENABLED = "censorEnabled";
    public static final String KEY_CENSOR_PURGE_ON_TWITCH_BAN = "censorPurgeOnTwitchBan";
    public static final String KEY_CENSOR_URL = "censorUrl";
    public static final String KEY_CENSOR_FIRST_URL = "censorFirstUrl";
    public static final String KEY_CENSOR_UNKNOWN_CHARS = "censorUnknownChars";
    public static final String KEY_CENSOR_UNKNOWN_CHARS_PERCENT = "censorUnknownCharsPercent";
    public static final String KEY_CENSOR_WHITE = "censorWhitelist";
    public static final String KEY_CENSOR_BLACK = "censorBlacklist";
    public static final String KEY_CENSOR_BANNED = "censorBannedWords";

    public static final String[] CENSOR_KEYS = new String[] { KEY_CENSOR_ENABLED, KEY_CENSOR_PURGE_ON_TWITCH_BAN, KEY_CENSOR_URL, KEY_CENSOR_FIRST_URL, KEY_CENSOR_UNKNOWN_CHARS, KEY_CENSOR_UNKNOWN_CHARS_PERCENT, KEY_CENSOR_WHITE, KEY_CENSOR_BLACK, KEY_CENSOR_BANNED };

    public static final String[][] ALL_KEYS_EXCEPT_CHAT_WINDOW_POSITION = new String[][] { IRC_KEYS, FONT_KEYS, CHAT_KEYS_EXCEPT_WINDOW_POSITION, COLOR_KEYS, MESSAGE_KEYS, EMOJI_KEYS, CENSOR_KEYS };

    public static final String[][] ALL_KEYS = new String[][] { IRC_KEYS, FONT_KEYS, CHAT_KEYS, COLOR_KEYS, MESSAGE_KEYS, EMOJI_KEYS, CENSOR_KEYS };

    /**
     * Get all the property keys to be tested to determine if two fProps are equal. The parameter FontificatorProperties
     * is to be used as a collection of configuration that is used to select which properties matter. For example, if
     * chat window position should be remembered or not, so if remember position is unchecked, it doesn't ask you to
     * save changes just because you moved the window.
     * 
     * @param fProps
     *            contains configuration to be used to determine which keys are needed
     * @return all keys to be tested for equals
     */
    private static String[][] getKeysForEqualsTest(FontificatorProperties fProps)
    {
        if (fProps != null && fProps.getChatConfig() != null && fProps.getChatConfig().isRememberPosition())
        {
            return ALL_KEYS_EXCEPT_CHAT_WINDOW_POSITION;
        }

        return ALL_KEYS;
    }

    private ConfigIrc ircConfig = new ConfigIrc();

    private ConfigFont fontConfig = new ConfigFont();

    private ConfigChat chatConfig = new ConfigChat();

    private ConfigColor colorConfig = new ConfigColor();

    private ConfigMessage messageConfig = new ConfigMessage();

    private ConfigEmoji emojiConfig = new ConfigEmoji();

    private ConfigCensor censorConfig = new ConfigCensor();

    public FontificatorProperties()
    {
    }

    @Override
    public void clear()
    {
        ircConfig.reset();
        fontConfig.reset();
        chatConfig.reset();
        colorConfig.reset();
        messageConfig.reset();
        emojiConfig.reset();
        censorConfig.reset();
        super.clear();
    }

    public ConfigIrc getIrcConfig()
    {
        return ircConfig;
    }

    public ConfigFont getFontConfig()
    {
        return fontConfig;
    }

    public ConfigChat getChatConfig()
    {
        return chatConfig;
    }

    public ConfigColor getColorConfig()
    {
        return colorConfig;
    }

    public ConfigMessage getMessageConfig()
    {
        return messageConfig;
    }

    public ConfigEmoji getEmojiConfig()
    {
        return emojiConfig;
    }

    public ConfigCensor getCensorConfig()
    {
        return censorConfig;
    }

    /**
     * Called whenever unsaved changes might be lost to let the user have the option to save them
     * 
     * @param ctrlWindow
     * @return okayToContinue
     */
    public boolean checkForUnsavedProps(ControlWindow ctrlWindow, Component parent)
    {
        if (hasUnsavedChanges())
        {
            int response = JOptionPane.showConfirmDialog(parent, "Save configuration changes?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION)
            {
                if (ctrlWindow.saveConfig())
                {
                    return true;
                }
            }
            else if (response == JOptionPane.NO_OPTION)
            {
                return true;
            }

            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Load configuration from the file indicated by the specified filename, or a preset file contained in the classpath
     * resource directory
     * 
     * @param filename
     * @return report
     * @throws Exception
     */
    public LoadConfigReport loadFile(String filename) throws Exception
    {
        if (filename.startsWith(ConfigFont.INTERNAL_FILE_PREFIX))
        {
            final String plainFilename = filename.substring(ConfigFont.INTERNAL_FILE_PREFIX.length());

            if (getClass().getClassLoader().getResource(plainFilename) == null)
            {
                LoadConfigReport report = new LoadConfigReport();
                final String errorMsg = "Preset theme " + plainFilename + " not found";
                ChatWindow.popup.handleProblem(errorMsg);
                report.addError(errorMsg, LoadConfigErrorType.FILE_NOT_FOUND);
                return report;
            }

            InputStream is = getClass().getClassLoader().getResourceAsStream(plainFilename);
            LoadConfigReport report = loadFile(is, filename, true);

            if (report.isErrorFree())
            {
                // This is a temporary copy, not to be saved to the last saved file for loading next time, but to avoid
                // having to not save when switching between presets
                this.lastSavedCopy = getCopy();
            }

            is.close();
            return report;
        }
        else
        {
            return loadFile(new File(filename));
        }
    }

    /**
     * Load configuration from the specified file
     * 
     * @param file
     * @return report
     * @throws Exception
     */
    public LoadConfigReport loadFile(File file) throws Exception
    {
        String absFilename = file.getAbsolutePath();
        logger.trace("Loading file " + absFilename);

        // Get directory from the last config filename to set JFileChosers with via the report
        String lastDirectory = getLastDirectory(absFilename);

        InputStream is = new FileInputStream(file);
        LoadConfigReport report = loadFile(is, absFilename, false);
        report.setDirectory(lastDirectory);
        is.close();
        return report;
    }

    private String getLastDirectory(String absFilename)
    {
        String lastDirectory = null;
        if (absFilename != null)
        {
            int slashIdx = absFilename.lastIndexOf('/');
            if (slashIdx >= 0)
            {
                lastDirectory = absFilename.substring(0, slashIdx);
            }
        }
        return lastDirectory;
    }

    /**
     * Load file from InputStream. Does not close InputStream
     * 
     * @param is
     * @param filename
     * @param isPreset
     * @return report
     * @throws Exception
     */
    private LoadConfigReport loadFile(InputStream is, String filename, boolean isPreset) throws Exception
    {
        final String prevAuth = getProperty(KEY_IRC_AUTH);
        super.load(is);
        final String currAuth = getProperty(KEY_IRC_AUTH);

        // Only decrypt the auth token here if there wasn't a previously loaded one that was already decrypted. This is
        // determined by whether the previous authorization wasn't loaded (null) or if there has been a change. If the
        // inputstream data didn't have an auth token, then the previous and current ones will match, meaning a new one
        // wasn't loaded and there's no need to decrypt.
        if (prevAuth == null || !prevAuth.equals(currAuth))
        {
            decryptProperty(KEY_IRC_AUTH);
        }

        LoadConfigReport report = loadConfigs(!isPreset);

        if (report.isErrorFree() && !isPreset)
        {
            rememberLastConfigFile(filename);
        }

        return report;
    }

    /**
     * Store the specified path to a configuration file in the last config file location conf file
     * 
     * @param path
     *            The path of the configuration file to remember
     */
    public void rememberLastConfigFile(String path)
    {
        logger.trace("Remembering last loaded file " + path);

        // Keep track of the last saved or loaded copy to compare with to see if a save prompt is required when exiting
        this.lastSavedCopy = getCopy();

        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter(new FileWriter(CONFIG_FILE_LAST_LOCATION, false));
            writer.write(path);
        }
        catch (Exception e)
        {
            // Don't alert the user, just log behind the scenes, not important enough to warrant a popup
            logger.error("Unable to save last file loaded", e);
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (Exception e)
                {
                    logger.error(e.toString(), e);
                }
            }
        }
    }

    /**
     * Delete the last config file location conf file that stored the path to a configuration file. To be used to remove
     * a file that is not found or has errors.
     */
    public void forgetLastConfigFile()
    {
        logger.trace("Forgetting last loaded");

        File f = new File(CONFIG_FILE_LAST_LOCATION);
        f.delete();
    }

    /**
     * Save the configuration to the specified file
     * 
     * @param file
     * @throws Exception
     */
    public void saveFile(File file) throws Exception
    {
        OutputStream os = new FileOutputStream(file);
        encryptProperty(KEY_IRC_AUTH);
        super.store(os, null);
        decryptProperty(KEY_IRC_AUTH);
        rememberLastConfigFile(file.getAbsolutePath());
    }

    public void encryptProperty(String key)
    {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(ENC_PASSWORD);

        final String decryptedValue = getProperty(key);
        if (decryptedValue != null && !decryptedValue.isEmpty())
        {
            try
            {
                final String encryptedValue = textEncryptor.encrypt(decryptedValue);
                setProperty(key, encryptedValue);
            }
            catch (Exception e)
            {
                final String errorMessage = "Error encrypting value for " + key + " property";
                logger.error(errorMessage, e);
                ChatWindow.popup.handleProblem(errorMessage);
                setProperty(key, "");
            }
        }
    }

    public void decryptProperty(String key)
    {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(ENC_PASSWORD);
        final String encryptedValue = getProperty(key);
        if (encryptedValue != null && !encryptedValue.isEmpty())
        {
            try
            {
                final String decryptedValue = textEncryptor.decrypt(encryptedValue);
                setProperty(key, decryptedValue);
            }
            catch (Exception e)
            {
                final String errorMessage = "Error decrypting value for " + key + " property";
                logger.error(errorMessage, e);
                ChatWindow.popup.handleProblem(errorMessage);
                setProperty(key, "");
            }
        }
    }

    /**
     * Try to load the configuration file stored i the last config file location conf file.
     * 
     * @return report
     * @throws Exception
     */
    public LoadConfigReport loadLast() throws Exception
    {
        logger.trace("Load last");

        final String previousConfigNotFound = "Previous configuration not found.";
        final String previousConfigError = "Error loading previous configuration.";

        BufferedReader reader = null;
        try
        {
            File lastFile = new File(CONFIG_FILE_LAST_LOCATION);
            if (!lastFile.exists())
            {
                LoadConfigReport errorReport = new LoadConfigReport();
                errorReport.addError(previousConfigNotFound, LoadConfigErrorType.FILE_NOT_FOUND);
                return errorReport;
            }
            reader = new BufferedReader(new FileReader(lastFile));
            final String lastConfigFilename = reader.readLine();
            reader.close();

            LoadConfigReport report = loadFile(lastConfigFilename);
            if (report.isProblem())
            {
                report.setMainMessage(previousConfigError);
            }
            return report;
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (Exception e)
                {
                    logger.error(e.toString(), e);
                }
            }
        }
    }

    /**
     * Set a value only if it isn't already set, optionally overriding if specified
     * 
     * @param key
     * @param value
     * @param override
     */
    private void setPropertyOverride(final String key, final String value, final boolean override)
    {
        final boolean valueExists = getProperty(key) != null && !getProperty(key).isEmpty();
        if (override || !valueExists)
        {
            setProperty(key, value);
        }
    }

    /**
     * Load a default configuration, for if something goes wrong, or if no previously used configuration file is stored
     * 
     * @param override
     *            Whether to override values should they already exist
     */
    public void loadDefaultValues(boolean override)
    {
        logger.trace("Loading default values");

        final String trueString = Boolean.toString(true);
        final String falseString = Boolean.toString(false);

        setPropertyOverride(KEY_IRC_HOST, "irc.twitch.tv", override);
        setPropertyOverride(KEY_IRC_PORT, Integer.toString(6667), override);
        setPropertyOverride(KEY_IRC_ANON, trueString, override);
        setPropertyOverride(KEY_IRC_AUTO_RECONNECT, trueString, override);

        setPropertyOverride(KEY_FONT_FILE_BORDER, ConfigFont.INTERNAL_FILE_PREFIX + "borders/dw3_border.png", override);
        setPropertyOverride(KEY_FONT_FILE_FONT, ConfigFont.INTERNAL_FILE_PREFIX + "fonts/dw3_font.png", override);
        setPropertyOverride(KEY_FONT_TYPE, FontType.FIXED_WIDTH.name(), override);
        setPropertyOverride(KEY_FONT_GRID_WIDTH, Integer.toString(8), override);
        setPropertyOverride(KEY_FONT_GRID_HEIGHT, Integer.toString(12), override);
        setPropertyOverride(KEY_FONT_SCALE, Integer.toString(2), override);
        setPropertyOverride(KEY_FONT_BORDER_SCALE, Integer.toString(3), override);
        setPropertyOverride(KEY_FONT_BORDER_INSET_X, Integer.toString(1), override);
        setPropertyOverride(KEY_FONT_BORDER_INSET_Y, Integer.toString(1), override);
        setPropertyOverride(KEY_FONT_SPACE_WIDTH, Integer.toString(25), override);
        setPropertyOverride(KEY_FONT_BASELINE_OFFSET, Integer.toString(0), override);
        setPropertyOverride(KEY_FONT_CHARACTERS, SpriteFont.NORMAL_ASCII_KEY, override);
        setPropertyOverride(KEY_FONT_UNKNOWN_CHAR, Character.toString((char) 127), override);
        setPropertyOverride(KEY_FONT_EXTENDED_CHAR, trueString, override);
        setPropertyOverride(KEY_FONT_SPACING_LINE, Integer.toString(2), override);
        setPropertyOverride(KEY_FONT_SPACING_CHAR, Integer.toString(0), override);
        setPropertyOverride(KEY_FONT_SPACING_MESSAGE, Integer.toString(0), override);

        setPropertyOverride(KEY_CHAT_SCROLL, falseString, override);
        setPropertyOverride(KEY_CHAT_RESIZABLE, trueString, override);
        setPropertyOverride(KEY_CHAT_POSITION, falseString, override);
        setPropertyOverride(KEY_CHAT_POSITION_X, Integer.toString(0), override);
        setPropertyOverride(KEY_CHAT_POSITION_Y, Integer.toString(0), override);
        setPropertyOverride(KEY_CHAT_FROM_BOTTOM, falseString, override);
        setPropertyOverride(KEY_CHAT_WIDTH, Integer.toString(550), override);
        setPropertyOverride(KEY_CHAT_HEIGHT, Integer.toString(450), override);
        setPropertyOverride(KEY_CHAT_CHROMA_ENABLED, falseString, override);
        setPropertyOverride(KEY_CHAT_INVERT_CHROMA, falseString, override);
        setPropertyOverride(KEY_CHAT_REVERSE_SCROLLING, falseString, override);
        setPropertyOverride(KEY_CHAT_CHROMA_LEFT, Integer.toString(10), override);
        setPropertyOverride(KEY_CHAT_CHROMA_TOP, Integer.toString(10), override);
        setPropertyOverride(KEY_CHAT_CHROMA_RIGHT, Integer.toString(10), override);
        setPropertyOverride(KEY_CHAT_CHROMA_BOTTOM, Integer.toString(10), override);
        setPropertyOverride(KEY_CHAT_CHROMA_CORNER, Integer.toString(10), override);
        setPropertyOverride(KEY_CHAT_ALWAYS_ON_TOP, falseString, override);
        setPropertyOverride(KEY_CHAT_ANTIALIAS, falseString, override);

        setPropertyOverride(KEY_COLOR_BG, "000000", override);
        setPropertyOverride(KEY_COLOR_FG, "FFFFFF", override);
        setPropertyOverride(KEY_COLOR_BORDER, "FFFFFF", override);
        setPropertyOverride(KEY_COLOR_HIGHLIGHT, "6699FF", override);
        setPropertyOverride(KEY_COLOR_PALETTE, "F7977A,FDC68A,FFF79A,A2D39C,6ECFF6,A187BE,F6989D", override);
        setPropertyOverride(KEY_COLOR_CHROMA_KEY, "00FF00", override);
        setPropertyOverride(KEY_COLOR_USERNAME, trueString, override);
        setPropertyOverride(KEY_COLOR_TIMESTAMP, falseString, override);
        setPropertyOverride(KEY_COLOR_MESSAGE, falseString, override);
        setPropertyOverride(KEY_COLOR_JOIN, falseString, override);
        setPropertyOverride(KEY_COLOR_TWITCH, falseString, override);

        setPropertyOverride(KEY_MESSAGE_JOIN, falseString, override);
        setPropertyOverride(KEY_MESSAGE_USERNAME, trueString, override);
        setPropertyOverride(KEY_MESSAGE_TIMESTAMP, falseString, override);
        setPropertyOverride(KEY_MESSAGE_USERFORMAT, ConfigMessage.USERNAME_REPLACE, override);
        setPropertyOverride(KEY_MESSAGE_TIMEFORMAT, "[HH:mm:ss]", override);
        setPropertyOverride(KEY_MESSAGE_CONTENT_BREAK, ConfigMessage.DEFAULT_CONTENT_BREAKER, override);
        setPropertyOverride(KEY_MESSAGE_QUEUE_SIZE, Integer.toString(64), override);
        setPropertyOverride(KEY_MESSAGE_SPEED, Integer.toString((int) (ConfigMessage.MAX_MESSAGE_SPEED * 0.25f)), override);
        setPropertyOverride(KEY_MESSAGE_EXPIRATION_TIME, Integer.toString(0), override);
        setPropertyOverride(KEY_MESSAGE_HIDE_EMPTY_BORDER, falseString, override);
        setPropertyOverride(KEY_MESSAGE_HIDE_EMPTY_BACKGROUND, falseString, override);
        setPropertyOverride(KEY_MESSAGE_CASE_TYPE, UsernameCaseResolutionType.NONE.name(), override);
        setPropertyOverride(KEY_MESSAGE_CASE_SPECIFY, falseString, override);
        setPropertyOverride(KEY_MESSAGE_CASING, MessageCasing.MIXED_CASE.name(), override);

        setPropertyOverride(KEY_EMOJI_ENABLED, trueString, override);
        setPropertyOverride(KEY_EMOJI_ANIMATION, falseString, override);
        setPropertyOverride(KEY_EMOJI_TWITCH_BADGES, trueString, override);
        setPropertyOverride(KEY_EMOJI_FFZ_BADGES, falseString, override);
        setPropertyOverride(KEY_EMOJI_SCALE_TO_LINE, trueString, override);
        setPropertyOverride(KEY_EMOJI_BADGE_SCALE_TO_LINE, falseString, override);
        setPropertyOverride(KEY_EMOJI_BADGE_HEIGHT_OFFSET, Integer.toString(0), override);
        setPropertyOverride(KEY_EMOJI_SCALE, Integer.toString(100), override);
        setPropertyOverride(KEY_EMOJI_BADGE_SCALE, Integer.toString(100), override);
        setPropertyOverride(KEY_EMOJI_DISPLAY_STRAT, EmojiLoadingDisplayStragegy.SPACE.name(), override);
        setPropertyOverride(KEY_EMOJI_TWITCH_ENABLE, trueString, override);
        setPropertyOverride(KEY_EMOJI_TWITCH_CACHE, falseString, override);
        setPropertyOverride(KEY_EMOJI_FFZ_ENABLE, falseString, override);
        setPropertyOverride(KEY_EMOJI_FFZ_CACHE, falseString, override);
        setPropertyOverride(KEY_EMOJI_BTTV_ENABLE, falseString, override);
        setPropertyOverride(KEY_EMOJI_BTTV_CACHE, falseString, override);
        setPropertyOverride(KEY_EMOJI_TWITTER_ENABLE, trueString, override);

        setPropertyOverride(KEY_CENSOR_ENABLED, trueString, override);
        setPropertyOverride(KEY_CENSOR_PURGE_ON_TWITCH_BAN, trueString, override);
        setPropertyOverride(KEY_CENSOR_URL, falseString, override);
        setPropertyOverride(KEY_CENSOR_FIRST_URL, falseString, override);
        setPropertyOverride(KEY_CENSOR_UNKNOWN_CHARS, falseString, override);
        setPropertyOverride(KEY_CENSOR_UNKNOWN_CHARS_PERCENT, Integer.toString(20), override);
        setPropertyOverride(KEY_CENSOR_WHITE, "", override);
        setPropertyOverride(KEY_CENSOR_BLACK, "", override);
        setPropertyOverride(KEY_CENSOR_BANNED, "", override);

        loadConfigs(true);
    }

    private FontificatorProperties getCopy()
    {
        FontificatorProperties copy = new FontificatorProperties();
        for (String key : stringPropertyNames())
        {
            copy.setProperty(key, getProperty(key));
        }
        return copy;
    }

    /**
     * Load properties into specialized config objects. This method returns its own report.
     * 
     * @return success
     */
    private LoadConfigReport loadConfigs(boolean loadNonFontConfig)
    {
        LoadConfigReport report = new LoadConfigReport();
        if (loadNonFontConfig)
        {
            ircConfig.load(this, report);
            chatConfig.load(this, report);
            messageConfig.load(this, report);
            emojiConfig.load(this, report);
            censorConfig.load(this, report);
        }
        fontConfig.load(this, report);
        colorConfig.load(this, report);

        if (!report.isErrorFree())
        {
            if (report.isOnlyMissingKeys())
            {
                report.setMainMessage("<center>Please resave the configuration file<br />to fix these issues:</center>");
            }
            ChatWindow.popup.handleProblem(report);
        }

        return report;
    }

    private boolean hasUnsavedChanges()
    {
        return lastSavedCopy != null && !equals(lastSavedCopy);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((chatConfig == null) ? 0 : chatConfig.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;

        FontificatorProperties otherFp = (FontificatorProperties) obj;

        return FontificatorProperties.propertyBatchMatch(getKeysForEqualsTest(this), this, otherFp);
    }

    private static boolean propertyBatchMatch(String[][] keys, FontificatorProperties a, FontificatorProperties b)
    {
        for (int i = 0; i < keys.length; i++)
        {
            for (int j = 0; j < keys[i].length; j++)
            {
                if (!propertyEquals(a.getProperty(keys[i][j]), b.getProperty(keys[i][j])))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean propertyEquals(String one, String two)
    {
        if (one == null)
        {
            return two == null;
        }
        else
        {
            return one.equals(two);
        }
    }

}
