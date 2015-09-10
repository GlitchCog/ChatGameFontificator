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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jasypt.util.text.BasicTextEncryptor;

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
    public static final String KEY_IRC_CHAN = "ircChannel";

    public static final String[] IRC_KEYS = new String[] { KEY_IRC_USER, KEY_IRC_HOST, KEY_IRC_PORT, KEY_IRC_AUTH, KEY_IRC_CHAN };

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
    public static final String KEY_FONT_UNKNOWN_CHAR = "fontUnknownChar";
    public static final String KEY_FONT_CHARACTERS = "fontCharacters";
    public static final String KEY_FONT_SPACING_LINE = "fontLineSpacing";
    public static final String KEY_FONT_SPACING_CHAR = "fontCharSpacing";

    public static final String[] FONT_KEYS = new String[] { KEY_FONT_FILE_BORDER, KEY_FONT_FILE_FONT, KEY_FONT_TYPE, KEY_FONT_GRID_WIDTH, KEY_FONT_GRID_HEIGHT, KEY_FONT_SCALE, KEY_FONT_BORDER_SCALE, KEY_FONT_BORDER_INSET_X, KEY_FONT_BORDER_INSET_Y, KEY_FONT_SPACE_WIDTH, KEY_FONT_UNKNOWN_CHAR, KEY_FONT_CHARACTERS, KEY_FONT_SPACING_LINE, KEY_FONT_SPACING_CHAR };

    public static final String KEY_CHAT_SCROLL = "chatScrollEnabled";
    public static final String KEY_CHAT_RESIZABLE = "chatResizable";
    public static final String KEY_CHAT_WIDTH = "chatWidth";
    public static final String KEY_CHAT_HEIGHT = "chatHeight";
    public static final String KEY_CHAT_CHROMA_ENABLED = "chromaEnabled";
    public static final String KEY_CHAT_INVERT_CHROMA = "invertChroma";
    public static final String KEY_CHAT_CHROMA_LEFT = "chromaLeft";
    public static final String KEY_CHAT_CHROMA_TOP = "chromaTop";
    public static final String KEY_CHAT_CHROMA_RIGHT = "chromaRight";
    public static final String KEY_CHAT_CHROMA_BOTTOM = "chromaBottom";
    public static final String KEY_CHAT_CHROMA_CORNER = "chromaCornerRadius";
    public static final String KEY_CHAT_ALWAYS_ON_TOP = "chatAlwaysOnTop";

    public static final String[] CHAT_KEYS = new String[] { KEY_CHAT_SCROLL, KEY_CHAT_RESIZABLE, KEY_CHAT_WIDTH, KEY_CHAT_HEIGHT, KEY_CHAT_CHROMA_ENABLED, KEY_CHAT_INVERT_CHROMA, KEY_CHAT_CHROMA_LEFT, KEY_CHAT_CHROMA_TOP, KEY_CHAT_CHROMA_RIGHT, KEY_CHAT_CHROMA_BOTTOM, KEY_CHAT_CHROMA_CORNER, KEY_CHAT_ALWAYS_ON_TOP };

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

    public static final String[] COLOR_KEYS = new String[] { KEY_COLOR_BG, KEY_COLOR_FG, KEY_COLOR_BORDER, KEY_COLOR_HIGHLIGHT, KEY_COLOR_CHROMA_KEY, KEY_COLOR_PALETTE, KEY_COLOR_USERNAME, KEY_COLOR_TIMESTAMP, KEY_COLOR_MESSAGE, KEY_COLOR_JOIN };

    public static final String KEY_MESSAGE_JOIN = "messageShowJoin";
    public static final String KEY_MESSAGE_USERNAME = "messageShowUsername";
    public static final String KEY_MESSAGE_TIMESTAMP = "messageShowTimestamp";
    public static final String KEY_MESSAGE_TIMEFORMAT = "messageTimestampFormat";
    public static final String KEY_MESSAGE_QUEUE_SIZE = "messageQueueSize";
    public static final String KEY_MESSAGE_SPEED = "messageSpeed";
    public static final String KEY_MESSAGE_CASE_TYPE = "messageUserCase";
    public static final String KEY_MESSAGE_CASE_SPECIFY = "messageUserCaseSpecify";

    public static final String[] MESSAGE_KEYS = new String[] { KEY_MESSAGE_JOIN, KEY_MESSAGE_USERNAME, KEY_MESSAGE_TIMESTAMP, KEY_MESSAGE_TIMEFORMAT, KEY_MESSAGE_QUEUE_SIZE, KEY_MESSAGE_SPEED, KEY_MESSAGE_CASE_TYPE, KEY_MESSAGE_CASE_SPECIFY };

    public static final String[][] ALL_KEY = new String[][] { IRC_KEYS, FONT_KEYS, CHAT_KEYS, COLOR_KEYS, MESSAGE_KEYS };

    private ConfigIrc ircConfig = new ConfigIrc();

    private ConfigFont fontConfig = new ConfigFont();

    private ConfigChat chatConfig = new ConfigChat();

    private ConfigColor colorConfig = new ConfigColor();

    private ConfigMessage messageConfig = new ConfigMessage();

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
                if (ctrlWindow.save())
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
     * @return success
     * @throws Exception
     */
    public boolean loadFile(String filename) throws Exception
    {
        if (filename.startsWith(ConfigFont.INTERNAL_FILE_PREFIX))
        {
            final String plainFilename = filename.substring(ConfigFont.INTERNAL_FILE_PREFIX.length());

            if (getClass().getClassLoader().getResource(plainFilename) == null)
            {
                ChatWindow.popup.handleProblem("Preset font " + plainFilename + " not found");
                return false;
            }

            InputStream is = getClass().getClassLoader().getResourceAsStream(plainFilename);
            boolean success = loadFile(is, filename, true);
            is.close();
            return success;
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
     * @return success
     * @throws Exception
     */
    public boolean loadFile(File file) throws Exception
    {
        logger.trace("Loading file " + file.getAbsolutePath());
        InputStream is = new FileInputStream(file);
        boolean success = loadFile(is, file.getAbsolutePath(), false);
        is.close();
        return success;
    }

    /**
     * Load file from InputStream. Does not close InputStream
     * 
     * @param is
     * @param filename
     * @param isPreset
     * @return
     * @throws Exception
     */
    private boolean loadFile(InputStream is, String filename, boolean isPreset) throws Exception
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

        boolean success = loadConfigs(!isPreset);

        if (success && !isPreset)
        {
            rememberLastConfigFile(filename);
        }

        return success;
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
     * Try to load the configuration file stored i the last config file location conf file. This method reports its own
     * errors.
     * 
     * @return success
     */
    public boolean loadLast()
    {
        logger.trace("Load last");

        final String previousConfigNotFound = "Previous configuration not found. Loading default values.";
        final String previousConfigError = "Error loading previous configuration. Loading default values.";

        BufferedReader reader = null;
        try
        {
            File lastFile = new File(CONFIG_FILE_LAST_LOCATION);
            if (!lastFile.exists())
            {
                ChatWindow.popup.handleProblem(previousConfigNotFound);
                return false;
            }
            reader = new BufferedReader(new FileReader(lastFile));
            final String lastConfigFilename = reader.readLine();
            reader.close();
            boolean success = loadFile(lastConfigFilename);
            if (!success)
            {
                ChatWindow.popup.handleProblem(previousConfigError);
            }
            return success;
        }
        catch (Exception e)
        {
            ChatWindow.popup.handleProblem(previousConfigError);
            return false;
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
     * Load a default configuration, for if something goes wrong, or if no previously used configuration file is stored
     */
    public void loadDefaultValues()
    {
        logger.trace("Loading default values");

        clear();

        setProperty(KEY_IRC_HOST, "irc.twitch.tv");
        setProperty(KEY_IRC_PORT, Integer.toString(6667));

        setProperty(KEY_FONT_FILE_BORDER, ConfigFont.INTERNAL_FILE_PREFIX + "borders/dw3_border.png");
        setProperty(KEY_FONT_FILE_FONT, ConfigFont.INTERNAL_FILE_PREFIX + "fonts/dw3_font.png");
        setProperty(KEY_FONT_TYPE, FontType.FIXED_WIDTH.name());
        setProperty(KEY_FONT_GRID_WIDTH, Integer.toString(8));
        setProperty(KEY_FONT_GRID_HEIGHT, Integer.toString(12));
        setProperty(KEY_FONT_SCALE, Integer.toString(2));
        setProperty(KEY_FONT_BORDER_SCALE, Integer.toString(3));
        setProperty(KEY_FONT_BORDER_INSET_X, Integer.toString(1));
        setProperty(KEY_FONT_BORDER_INSET_Y, Integer.toString(1));
        setProperty(KEY_FONT_SPACE_WIDTH, Integer.toString(25));
        setProperty(KEY_FONT_CHARACTERS, SpriteFont.NORMAL_ASCII_KEY);
        setProperty(KEY_FONT_UNKNOWN_CHAR, Character.toString((char) 127));
        setProperty(KEY_FONT_SPACING_LINE, Integer.toString(2));
        setProperty(KEY_FONT_SPACING_CHAR, Integer.toString(0));

        setProperty(KEY_CHAT_SCROLL, Boolean.toString(false));
        setProperty(KEY_CHAT_RESIZABLE, Boolean.toString(true));
        setProperty(KEY_CHAT_WIDTH, Integer.toString(550));
        setProperty(KEY_CHAT_HEIGHT, Integer.toString(450));
        setProperty(KEY_CHAT_CHROMA_ENABLED, Boolean.toString(false));
        setProperty(KEY_CHAT_INVERT_CHROMA, Boolean.toString(false));
        setProperty(KEY_CHAT_CHROMA_LEFT, Integer.toString(10));
        setProperty(KEY_CHAT_CHROMA_TOP, Integer.toString(10));
        setProperty(KEY_CHAT_CHROMA_RIGHT, Integer.toString(10));
        setProperty(KEY_CHAT_CHROMA_BOTTOM, Integer.toString(10));
        setProperty(KEY_CHAT_CHROMA_CORNER, Integer.toString(10));
        setProperty(KEY_CHAT_ALWAYS_ON_TOP, Boolean.toString(false));

        setProperty(KEY_COLOR_BG, "000000");
        setProperty(KEY_COLOR_FG, "FFFFFF");
        setProperty(KEY_COLOR_BORDER, "FFFFFF");
        setProperty(KEY_COLOR_HIGHLIGHT, "6699FF");
        setProperty(KEY_COLOR_PALETTE, "F7977A,FDC68A,FFF79A,A2D39C,6ECFF6,A187BE,F6989D");
        setProperty(KEY_COLOR_CHROMA_KEY, "00FF00");
        setProperty(KEY_COLOR_USERNAME, Boolean.toString(true));
        setProperty(KEY_COLOR_TIMESTAMP, Boolean.toString(false));
        setProperty(KEY_COLOR_MESSAGE, Boolean.toString(false));
        setProperty(KEY_COLOR_JOIN, Boolean.toString(false));

        setProperty(KEY_MESSAGE_JOIN, Boolean.toString(true));
        setProperty(KEY_MESSAGE_USERNAME, Boolean.toString(true));
        setProperty(KEY_MESSAGE_TIMESTAMP, Boolean.toString(false));
        setProperty(KEY_MESSAGE_TIMEFORMAT, "[HH:mm:ss]");
        setProperty(KEY_MESSAGE_QUEUE_SIZE, Integer.toString(64));
        setProperty(KEY_MESSAGE_SPEED, Integer.toString((int)(ConfigMessage.MAX_MESSAGE_SPEED * 0.25f)));
        setProperty(KEY_MESSAGE_CASE_TYPE, UsernameCaseResolutionType.LOOKUP.name());
        setProperty(KEY_MESSAGE_CASE_SPECIFY, Boolean.toString(false));

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
     * Load properties into specialized config objects. This method reports its own errors.
     * 
     * @return success
     */
    private boolean loadConfigs(boolean loadNonFontConfig)
    {
        List<String> errors = new ArrayList<String>();
        if (loadNonFontConfig)
        {
            ircConfig.load(this, errors);
            chatConfig.load(this, errors);
            messageConfig.load(this, errors);
        }
        fontConfig.load(this, errors);
        colorConfig.load(this, errors);

        if (!errors.isEmpty())
        {
            String allErrors = "";
            for (String er : errors)
            {
                allErrors += er + "\n";
            }
            ChatWindow.popup.handleProblem(allErrors);
        }

        return errors.isEmpty();
    }

    private boolean hasUnsavedChanges()
    {
        return !equals(lastSavedCopy);
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

        return FontificatorProperties.propertyBatchMatch(ALL_KEY, this, otherFp);
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
