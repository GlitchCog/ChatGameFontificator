package com.glitchcog.fontificator.gui.controls;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.bot.ChatViewerBot;
import com.glitchcog.fontificator.bot.MessageType;
import com.glitchcog.fontificator.bot.TwitchPrivmsg;
import com.glitchcog.fontificator.config.ConfigFont;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.MenuComponent;
import com.glitchcog.fontificator.gui.controls.messages.MessageDialog;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelFont;
import com.glitchcog.fontificator.gui.controls.panel.ControlTabs;
import com.glitchcog.fontificator.gui.controls.panel.LogBox;

/**
 * This is the window for containing all the options of the chat display window
 * 
 * @author Matt Yanos
 */
public class ControlWindow extends JDialog
{
    private static final Logger logger = Logger.getLogger(ControlWindow.class);

    private static final long serialVersionUID = 1L;

    private final String PRESET_DIRECTORY = "presets/";

    private final String DEFAULT_CONFIG_FILE_EXTENSION = "cgf";

    private final String DEFAULT_SCREENSHOT_FILE_EXTENSION = "png";

    private ControlTabs controlTabs;

    private ChatViewerBot bot;

    private ChatWindow chatWindow;

    private MessageDialog messageDialog;

    private FontificatorProperties fProps;

    private JEditorPane aboutPane;

    // @formatter:off
    private static String ABOUT_CONTENTS = "<html><table bgcolor=#EEEEEE width=100% border=1><tr><td>" + 
        "<center><font face=\"Arial, Helvetica\"><b>Chat Game Fontificator</b> is a Twitch chat display that makes<br />" + 
        "the chat look like the text boxes from various video games.<br /><br />" + 
        "It is free, open source, and in the public domain to the furthest<br />" + 
        "extent I am permitted to forfeit my copyright over this software.<br /><br />" + 
        "Please enjoy!<br /><br />" + 
        "By Matt Yanos<br /><br />" + 
        "<a href=\"www.github.com/GlitchCog/ChatGameFontificator\">www.github.com/GlitchCog/ChatGameFontificator</a>" + 
        "</font></center>" + 
        "</td></tr></table></html>";
    // @formatter:on

    private String helpText;

    public static ControlWindow me;

    private JDialog help;

    private JFileChooser opener;

    private JFileChooser configSaver;

    private JFileChooser screenshotSaver;

    private ScreenshotOptions screenshotOptions;

    public ControlWindow(JFrame parent, FontificatorProperties fProps, LogBox logBox)
    {
        super(parent);

        BufferedReader br = null;
        try
        {

            InputStream is = getClass().getClassLoader().getResourceAsStream("help.html");
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder helpBuilder = new StringBuilder();
            while ((line = br.readLine()) != null)
            {
                helpBuilder.append(line);
            }
            helpText = helpBuilder.toString();
        }
        catch (Exception e)
        {
            helpText = "Unable to load help file";
            logger.error(helpText, e);
            ChatWindow.popup.handleProblem(helpText);
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (Exception e)
                {
                    logger.error(e.toString(), e);
                }
            }
        }

        ControlWindow.me = this;

        setTitle("Fontificator Configuration");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        this.fProps = fProps;

        FileFilter cgfFileFilter = new FileNameExtensionFilter("Chat Game Fontificator Configuration (*." + DEFAULT_CONFIG_FILE_EXTENSION + ")", DEFAULT_CONFIG_FILE_EXTENSION.toLowerCase());
        FileFilter pngFileFilter = new FileNameExtensionFilter("PNG Image (*." + DEFAULT_SCREENSHOT_FILE_EXTENSION + ")", DEFAULT_SCREENSHOT_FILE_EXTENSION.toLowerCase());

        this.opener = new JFileChooser();
        this.opener.setFileFilter(cgfFileFilter);

        this.configSaver = new JFileChooser();
        this.configSaver.setFileFilter(cgfFileFilter);

        this.screenshotOptions = new ScreenshotOptions();

        this.screenshotSaver = new JFileChooser();
        this.screenshotSaver.setFileFilter(pngFileFilter);
        this.screenshotSaver.setAccessory(screenshotOptions);
    }

    public void loadLastData(ChatWindow chatWindow)
    {
        this.chatWindow = chatWindow;

        ChatWindow.setupHideOnEscape(this);

        LoadConfigReport report = new LoadConfigReport();

        fProps.clear();
        try
        {
            report = fProps.loadLast();
        }
        catch (Exception e)
        {
            final String errorMsg = "Unknown error loading last config file";
            logger.error(errorMsg, e);
            report.addError(errorMsg, LoadConfigErrorType.UNKNOWN_ERROR);
        }

        if (!report.isErrorFree())
        {
            final boolean overwriteExistingValues = report.isProblem();
            if (overwriteExistingValues)
            {
                fProps.forgetLastConfigFile();
            }
            fProps.loadDefaultValues(overwriteExistingValues);
        }
    }

    /**
     * Called separately from construction.
     * 
     * @param logBox
     */
    public void build(LogBox logBox)
    {
        constructAboutPopup();

        this.messageDialog = new MessageDialog(fProps, chatWindow, this, logBox);

        this.bot = new ChatViewerBot();
        this.bot.setUsername(fProps.getIrcConfig().getUsername());

        this.controlTabs = new ControlTabs(fProps, bot, messageDialog.getCensorPanel(), logBox);
        this.controlTabs.build(chatWindow, this);

        this.bot.setChatPanel(chatWindow.getChatPanel());

        setLayout(new GridLayout(1, 1));
        add(controlTabs);

        initMenus();

        // This wasn't built before the config was loaded into the chat control
        // tab, so set it here
        setAlwaysOnTopMenu(fProps.getChatConfig().isAlwaysOnTop());
        setRememberPositionMenu(fProps.getChatConfig().isRememberPosition());
        setAntiAliasMenu(fProps.getChatConfig().isAntiAlias());

        setupHelp();

        pack();
        setMinimumSize(getSize());
        setResizable(false);
    }

    private void setupHelp()
    {
        final String helpTitle = "Chat Game Fontificator Help";

        help = new JDialog(this, true);
        help.setTitle(helpTitle);
        help.setSize(640, 480);
        help.setLayout(new GridBagLayout());

        JEditorPane helpPane = new JEditorPane();
        helpPane.setContentType("text/html");
        helpPane.setText(helpText);
        helpPane.setEditable(false);
        JScrollPane scrollHelp = new JScrollPane(helpPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton ok = new JButton("Close");
        ok.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                help.setVisible(false);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        help.add(new JLabel("The function of each option available in the Control Window tabs is explained below"), gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridy = 1;
        help.add(scrollHelp, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 0.0;
        help.add(ok, gbc);

        help.setResizable(false);
    }

    /**
     * Builds the menus from the static arrays
     */
    private void initMenus()
    {
        JMenuBar menuBar = new JMenuBar();

        final String[] mainMenuText = { "File", "Presets", "View", "Message", "Help" };
        final int[] mainMnomonics = { KeyEvent.VK_F, KeyEvent.VK_P, KeyEvent.VK_V, KeyEvent.VK_M, KeyEvent.VK_H };

        JMenu[] menus = new JMenu[mainMenuText.length];

        for (int i = 0; i < mainMenuText.length; i++)
        {
            menus[i] = new JMenu(mainMenuText[i]);
            menus[i].setMnemonic(mainMnomonics[i]);
        }

        /* File Menu Item Text */
        final String strFileOpen = "Open Configuration";
        final String strFileSave = "Save Configuration";
        final String strFileRestore = "Restore Default Configuration";
        final String strScreenshot = "Screenshot";
        final String strFileExit = "Exit";
        // @formatter:off
        final MenuComponent[] fileComponents = new MenuComponent[] { 
            new MenuComponent(strFileOpen, KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK)), 
            new MenuComponent(strFileSave, KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK)), 
            new MenuComponent(strFileRestore, KeyEvent.VK_R, null), 
            new MenuComponent(strScreenshot, KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0)), 
            new MenuComponent(strFileExit, KeyEvent.VK_X, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK)) 
        };
        // @formatter:on

        /* View Menu Item Text */
        final String strAntiAlias = "Anti-Aliased";
        final String strViewTop = "Always On Top";
        final String strRememberPos = "Remember Chat Window Position";
        final String strViewHide = "Hide Control Window";
        final MenuComponent[] viewComponents = new MenuComponent[] { new MenuComponent(strAntiAlias, KeyEvent.VK_A, null, true), null, new MenuComponent(strViewTop, KeyEvent.VK_T, null, true), new MenuComponent(strRememberPos, KeyEvent.VK_P, null, true), new MenuComponent(strViewHide, KeyEvent.VK_H, KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK)) };

        /* Message Menu Item Text */
        final String strMsgMsg = "Message Management";
        final MenuComponent[] messageComponents = new MenuComponent[] { new MenuComponent(strMsgMsg, KeyEvent.VK_M, KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK)) };

        /* Help Menu Item Text */
        final String strHelpHelp = "Help";
        final String strHelpDebug = "Debug Mode";
        final String strHelpAbout = "About";
        final MenuComponent[] helpComponents = new MenuComponent[] { new MenuComponent(strHelpHelp, KeyEvent.VK_R, null), new MenuComponent(strHelpDebug, KeyEvent.VK_D, null, true), null, new MenuComponent(strHelpAbout, KeyEvent.VK_A, null) };

        /* All menu components, with a placeholder for the Presets menu */
        final MenuComponent[][] allMenuComponents = new MenuComponent[][] { fileComponents, new MenuComponent[] {}, viewComponents, messageComponents, helpComponents };

        ActionListener mal = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JMenuItem mi = (JMenuItem) e.getSource();
                if (strFileOpen.equals(mi.getText()))
                {
                    open();
                }
                else if (strFileSave.equals(mi.getText()))
                {
                    saveConfig();
                }
                else if (strFileRestore.equals(mi.getText()))
                {
                    restoreDefaults(true);
                    controlTabs.refreshUiFromConfig(fProps);
                }
                else if (strScreenshot.equals(mi.getText()))
                {
                    saveScreenshot();
                }
                else if (strFileExit.equals(mi.getText()))
                {
                    attemptToExit();
                }
                else if (strAntiAlias.equals(mi.getText()))
                {
                    JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem) e.getSource();
                    controlTabs.setAntiAlias(checkBox.isSelected());
                    chatWindow.getChatPanel().repaint();
                }
                else if (strViewTop.equals(mi.getText()))
                {
                    JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem) e.getSource();
                    ((JFrame) getParent()).setAlwaysOnTop(checkBox.isSelected());
                    controlTabs.setAlwaysOnTopConfig(checkBox.isSelected());
                }
                else if (strRememberPos.equals(mi.getText()))
                {
                    JCheckBoxMenuItem checkBox = (JCheckBoxMenuItem) e.getSource();
                    controlTabs.setRememberChatWindowPosition(checkBox.isSelected());
                    if (checkBox.isSelected())
                    {
                        final int sx = (int) chatWindow.getLocationOnScreen().getX();
                        final int sy = (int) chatWindow.getLocationOnScreen().getY();
                        fProps.getChatConfig().setChatWindowPositionX(sx);
                        fProps.getChatConfig().setChatWindowPositionY(sy);
                    }
                }
                else if (strViewHide.equals(mi.getText()))
                {
                    setVisible(false);
                }
                else if (strMsgMsg.equals(mi.getText()))
                {
                    messageDialog.showDialog();
                }
                else if (strHelpHelp.equals(mi.getText()))
                {
                    help.setVisible(true);
                }
                else if (strHelpDebug.equals(mi.getText()))
                {
                    toggleDebugTab();
                }
                else if (strHelpAbout.equals(mi.getText()))
                {
                    showAboutPane();
                }
            }
        };

        /* Set all menu items but presets */
        JMenuItem item = null;
        for (int i = 0; i < allMenuComponents.length; i++)
        {
            for (int j = 0; j < allMenuComponents[i].length; j++)
            {
                MenuComponent mc = allMenuComponents[i][j];
                if (mc == null)
                {
                    menus[i].add(new JSeparator());
                }
                else
                {
                    item = mc.checkbox ? new JCheckBoxMenuItem(mc.label) : new JMenuItem(mc.label);
                    item.addActionListener(mal);
                    item.setMnemonic(mc.mnemonic);
                    if (mc.accelerator != null)
                    {
                        item.setAccelerator(mc.accelerator);
                    }
                    menus[i].add(item);
                }
            }
            menuBar.add(menus[i]);
        }

        /* Presets Breath of Fire */
        final String[] strBof1 = new String[] { "Breath of Fire", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "bof1.cgf" };
        final String[] strBof2 = new String[] { "Breath of Fire 2", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "bof2.cgf" };
        /* Presets Chrono */
        final String[] strChrono = new String[] { "Chrono Trigger", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ct.cgf" };
        final String[] strChronoCross = new String[] { "Chrono Cross", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "cc.cgf" };
        /* Presets Dragon Warrior */
        final String[] strDw1 = new String[] { "Dragon Warrior", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dw1.cgf" };
        final String[] strDw2 = new String[] { "Dragon Warrior II", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dw2.cgf" };
        final String[] strDq1_2 = new String[] { "Dragon Quest I.II (SFC)", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dq1_2_sfc.cgf" };
        final String[] strDw3 = new String[] { "Dragon Warrior III", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dw3.cgf" };
        final String[] strDw3Gbc = new String[] { "Dragon Warrior III (GBC)", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dw3gbc.cgf" };
        final String[] strDq3 = new String[] { "Dragon Quest III (SFC)", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dq3_sfc.cgf" };
        final String[] strDw4 = new String[] { "Dragon Warrior IV", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dw4.cgf" };
        final String[] strDqhrs = new String[] { "Dragon Quest Heroes: Rocket Slime", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "dqhrs.cgf" };
        /* Presets Earthbound */
        final String[] strEb0 = new String[] { "Earthbound Zero", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "eb0.cgf" };
        final String[] strEbPlain = new String[] { "Earthbound Plain", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "eb_plain.cgf" };
        final String[] strEbMint = new String[] { "Earthbound Mint", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "eb_mint.cgf" };
        final String[] strEbStrawberry = new String[] { "Earthbound Strawberry", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "eb_strawberry.cgf" };
        final String[] strEbBanana = new String[] { "Earthbound Banana", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "eb_banana.cgf" };
        final String[] strEbPeanut = new String[] { "Earthbound Peanut", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "eb_peanut.cgf" };
        final String[] strEbSaturn = new String[] { "Earthbound Mr. Saturn", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "eb_saturn.cgf" };
        final String[] strM3 = new String[] { "Mother 3", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "m3.cgf" };
        /* Presets Final Fantasy */
        final String[] strFinalFantasy1 = new String[] { "Final Fantasy", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ff1.cgf" };
        final String[] strFinalFantasy4 = new String[] { "Final Fantasy IV", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ff4.cgf" };
        final String[] strFinalFantasy6 = new String[] { "Final Fantasy VI", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ff6.cgf" };
        final String[] strFinalFantasy7 = new String[] { "Final Fantasy VII", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ff7.cgf" };
        /* Presets Mario */
        final String[] strMario1 = new String[] { "Super Mario Bros.", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smb1.cgf" };
        final String[] strMario1Underworld = new String[] { "Super Mario Bros. Underworld", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smb1_underworld.cgf" };
        final String[] strMario2 = new String[] { "Super Mario Bros. 2", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smb2.cgf" };
        final String[] strMario3hud = new String[] { "Super Mario Bros. 3 HUD", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smb3_hud.cgf" };
        final String[] strMario3letter = new String[] { "Super Mario Bros. 3 Letter", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smb3_letter.cgf" };
        final String[] strMarioWorld = new String[] { "Super Mario World", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smw.cgf" };
        final String[] strYoshisIsland = new String[] { "Super Mario World 2: Yoshi's Island", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "yi.cgf" };
        final String[] strMarioRpg = new String[] { "Super Mario RPG", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smrpg.cgf" };
        /* Presets Metroid */
        final String[] strMetroid = new String[] { "Metroid", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "metroid.cgf" };
        final String[] strMetroidBoss = new String[] { "Metroid Mother Brain", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "metroidboss.cgf" };
        final String[] strMetroid2 = new String[] { "Metroid II", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "metroid2.cgf" };
        final String[] strSuperMetroid = new String[] { "Super Metroid", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "smetroid.cgf" };
        final String[] strMetroidFusion = new String[] { "Metroid Fusion", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "metroid_fusion.cgf" };
        final String[] strMetroidZero = new String[] { "Metroid Zero Mission", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "metroid_zm.cgf" };
        /* Presets Phantasy Star */
        final String[] strPhanStar1 = new String[] { "Phantasy Star", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ps1.cgf" };
        final String[] strPhanStar2 = new String[] { "Phantasy Star II", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ps2.cgf" };
        /* Presets Pokemon */
        final String[] strPkmnRb = new String[] { "Pokemon Red/Blue", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "pkmnrb.cgf" };
        final String[] strPkmnFrlg = new String[] { "Pokemon Fire Red/Leaf Green", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "pkmnfrlg.cgf" };
        /* Presets Ys */
        final String[] strYs1fc = new String[] { "Ys (FC)", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ys1_fc.cgf" };
        final String[] strYs3fc = new String[] { "Ys III (FC)", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ys3_fc.cgf" };
        final String[] strYs3snes = new String[] { "Ys III (SNES)", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "ys3_snes.cgf" };
        /* Presets Zelda */
        final String[] strLozBush = new String[] { "The Legend of Zelda Bushes", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "zelda1_bush.cgf" };
        final String[] strLozRock = new String[] { "The Legend of Zelda Moutains", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "zelda1_rock.cgf" };
        final String[] strLozDungeon = new String[] { "The Legend of Zelda Dungeon", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "zelda1_dungeon.cgf" };
        final String[] strZelda2 = new String[] { "Zelda II: The Adventures of Link", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "zelda2.cgf" };
        final String[] strLozLa = new String[] { "The Legend of Zelda: Link's Awakening", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "zelda_la.cgf" };
        final String[] strZelda3 = new String[] { "The Legend of Zelda: A Link to the Past", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "zelda3.cgf" };
        final String[] strZeldaWw = new String[] { "The Legend of Zelda: The Wind Waker", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "zelda_ww.cgf" };
        /* Ungrouped Presets */
        final String[] strClash = new String[] { "Clash at Demonhead", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "cad.cgf" };
        final String[] strCrystalis = new String[] { "Crystalis", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "crystalis.cgf" };
        final String[] strFreedomPlanet = new String[] { "Freedom Planet", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "freep.cgf" };
        final String[] strGoldenSun = new String[] { "Golden Sun", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "gsun.cgf" };
        final String[] strHarvestMoonFmt = new String[] { "Harvest Moon: Friends of Mineral Town", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "hm_fmt.cgf" };
        final String[] strRiverCityRansom = new String[] { "River City Ransom", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "rcr.cgf" };
        final String[] strRygarNes = new String[] { "Rygar (NES)", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "rygar_nes.cgf" };
        final String[] strSecretOfEvermore = new String[] { "Secret of Evermore", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "soe.cgf" };
        final String[] strShantae = new String[] { "Shantae", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "shantae.cgf" };
        final String[] strShovel = new String[] { "Shovel Knight", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "sk.cgf" };
        final String[] strSuikoden = new String[] { "Suikoden", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "suiko.cgf" };
        final String[] strTalesOfSymphonia = new String[] { "Tales of Symphonia", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "tos.cgf" };
        final String[] strWarioLand4 = new String[] { "Wario Land 4", ConfigFont.INTERNAL_FILE_PREFIX + PRESET_DIRECTORY + "wl4.cgf" };

        // @formatter:off
        final String[][] allPresets = new String[][]
        {
            strBof1, strBof2, 
            strChrono, strChronoCross, 
            strDw1, strDw2, strDq1_2, strDw3, strDw3Gbc, strDq3, strDw4, strDqhrs, 
            strEb0, strEbPlain, strEbMint, strEbStrawberry, strEbBanana, strEbPeanut, strEbSaturn, strM3,
            strFinalFantasy1, strFinalFantasy4, strFinalFantasy6, strFinalFantasy7,  
            strMario1, strMario1Underworld, strMario2, strMario3hud, strMario3letter, strMarioWorld, strYoshisIsland, strMarioRpg, 
            strMetroid, strMetroidBoss, strMetroid2, strSuperMetroid, strMetroidFusion, strMetroidZero, 
            strPhanStar1, strPhanStar2, 
            strPkmnRb, strPkmnFrlg, 
            strYs1fc, strYs3fc, strYs3snes, 
            strLozBush, strLozRock, strLozDungeon, strZelda2, strLozLa, strZelda3, strZeldaWw,  
            strClash, strCrystalis, strFreedomPlanet, strGoldenSun, strHarvestMoonFmt, strRiverCityRansom, strRygarNes, strSecretOfEvermore, strShantae, strShovel, strSuikoden, strTalesOfSymphonia, strWarioLand4
        };
        // @formatter:on

        ActionListener presetListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String sourceText = ((JMenuItem) e.getSource()).getText();
                for (int i = 0; i < allPresets.length; i++)
                {
                    if (allPresets[i][0].equals(sourceText))
                    {
                        loadPreset(allPresets[i][0], allPresets[i][1]);
                        break;
                    }
                }
            }
        };

        // Put all the presets into this map to convert them into the submenu items
        final Map<String, String[]> presetMapSubmenuToItem = new LinkedHashMap<String, String[]>();
        presetMapSubmenuToItem.put("Breath of Fire", new String[] { strBof1[0], strBof2[0] });
        presetMapSubmenuToItem.put("Chrono", new String[] { strChrono[0], strChronoCross[0] });
        presetMapSubmenuToItem.put("Dragon Warrior", new String[] { strDw1[0], strDw2[0], strDq1_2[0], strDw3[0], strDw3Gbc[0], strDq3[0], strDw4[0], strDqhrs[0] });
        presetMapSubmenuToItem.put("Earthbound", new String[] { strEb0[0], strEbPlain[0], strEbMint[0], strEbStrawberry[0], strEbBanana[0], strEbPeanut[0], strEbSaturn[0], strM3[0] });
        presetMapSubmenuToItem.put("Final Fantasy", new String[] { strFinalFantasy1[0], strFinalFantasy4[0], strFinalFantasy6[0], strFinalFantasy7[0] });
        presetMapSubmenuToItem.put("Mario", new String[] { strMario1[0], strMario1Underworld[0], strMario2[0], strMario3hud[0], strMario3letter[0], strMarioWorld[0], strYoshisIsland[0], strMarioRpg[0] });
        presetMapSubmenuToItem.put("Metroid", new String[] { strMetroid[0], strMetroidBoss[0], strMetroid2[0], strSuperMetroid[0], strMetroidFusion[0], strMetroidZero[0] });
        presetMapSubmenuToItem.put("Phantasy Star", new String[] { strPhanStar1[0], strPhanStar2[0] });
        presetMapSubmenuToItem.put("Pokemon", new String[] { strPkmnRb[0], strPkmnFrlg[0] });
        presetMapSubmenuToItem.put("Ys", new String[] { strYs1fc[0], strYs3fc[0], strYs3snes[0] });
        presetMapSubmenuToItem.put("Zelda", new String[] { strLozBush[0], strLozRock[0], strLozDungeon[0], strZelda2[0], strLozLa[0], strZelda3[0], strZeldaWw[0] });
        presetMapSubmenuToItem.put(null, new String[] { strClash[0], strCrystalis[0], strFreedomPlanet[0], strGoldenSun[0], strHarvestMoonFmt[0], strRiverCityRansom[0], strRygarNes[0], strSecretOfEvermore[0], strShantae[0], strShovel[0], strSuikoden[0], strTalesOfSymphonia[0], strWarioLand4[0] });

        for (String submenuKey : presetMapSubmenuToItem.keySet())
        {
            String[] submenuItems = presetMapSubmenuToItem.get(submenuKey);
            if (submenuKey != null)
            {
                JMenu submenu = new JMenu(submenuKey);
                for (String itemStr : submenuItems)
                {
                    JMenuItem submenuItem = new JMenuItem(itemStr);
                    submenuItem.addActionListener(presetListener);
                    submenu.add(submenuItem);
                }
                menus[1].add(submenu);
            }
            else
            {
                for (String submenuRootItemStr : submenuItems)
                {
                    JMenuItem submenuRootItem = new JMenuItem(submenuRootItemStr);
                    submenuRootItem.addActionListener(presetListener);
                    menus[1].add(submenuRootItem);
                }
            }
        }

        for (int i = 0; i < menus.length; i++)
        {
            menuBar.add(menus[i]);
        }

        setJMenuBar(menuBar); // add the whole menu bar
    }

    /**
     * Restore default values to the configuration
     * 
     * @param overrideExistingValues
     */
    private void restoreDefaults(boolean overrideExistingValues)
    {
        boolean okayToProceed = fProps.checkForUnsavedProps(this, this);

        if (okayToProceed)
        {
            int result = JOptionPane.showConfirmDialog(this, "Reset to default configuration?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION)
            {
                fProps.loadDefaultValues(overrideExistingValues);
                controlTabs.refreshUiFromConfig(fProps);
                chatWindow.getChatPanel().repaint();
            }
        }
    }

    private void open()
    {
        boolean okayToProceed = fProps.checkForUnsavedProps(this, this);

        if (okayToProceed)
        {
            int result = opener.showOpenDialog(me);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    LoadConfigReport report = fProps.loadFile(opener.getSelectedFile());
                    if (report.isProblem())
                    {
                        throw new Exception("Configuration file open error");
                    }
                    controlTabs.refreshUiFromConfig(fProps);
                    chatWindow.getChatPanel().repaint();
                }
                catch (Exception ex)
                {
                    final String errorMsg = "Unable to open file " + (opener.getSelectedFile() == null ? "null" : opener.getSelectedFile().getName());
                    logger.error(errorMsg, ex);
                    ChatWindow.popup.handleProblem(errorMsg);
                }
            }
        }
    }

    private void loadPreset(String presetName, String presetFilename)
    {
        boolean okayToProceed = fProps.checkForUnsavedProps(this, this);
        if (okayToProceed)
        {
            try
            {
                LoadConfigReport report = fProps.loadFile(presetFilename);
                if (report.isProblem())
                {
                    logger.error("Unsuccessful call to FontificatorProperties.loadFile(String)");
                    throw new Exception();
                }
            }
            catch (Exception ex)
            {
                logger.error(ex.toString(), ex);
                ChatWindow.popup.handleProblem("Unable to load preset " + presetName + " (" + presetFilename + ")");
            }
            controlTabs.refreshUiFromConfig(fProps);
            chatWindow.getChatPanel().repaint();
        }
    }

    /**
     * Gets a file to save to from the given file chooser, including options to overwrite
     * 
     * @param chooser
     *            the chooser to use to get the file
     * @param extension
     *            the extension of the type of file being saved, or null if there is no default extension
     * @return file or null if selection is canceled
     */
    private File getTargetSaveFile(JFileChooser chooser, String extension)
    {
        final boolean configReadyToSave = controlTabs.refreshConfigFromUi();
        if (configReadyToSave)
        {
            int overwrite = JOptionPane.YES_OPTION;
            // Do while overwrite is no, so it loops back around to try again if someone says they don't want to
            // overwrite an existing file, but if they select cancel it just breaks out of the loop
            do
            {
                int result = chooser.showSaveDialog(me);

                // Default to yes, so it writes even if there's no existing file
                overwrite = JOptionPane.YES_OPTION;

                if (result == JFileChooser.APPROVE_OPTION)
                {
                    File saveFile = chooser.getSelectedFile();

                    if (chooser.getFileFilter() instanceof FileNameExtensionFilter)
                    {
                        String[] exts = ((FileNameExtensionFilter) (chooser.getFileFilter())).getExtensions();
                        boolean endsInExt = false;
                        for (String ext : exts)
                        {
                            if (saveFile.getName().toLowerCase().endsWith(ext.toLowerCase()))
                            {
                                endsInExt = true;
                                break;
                            }
                        }
                        if (extension != null && !endsInExt)
                        {
                            saveFile = new File(saveFile.getPath() + "." + extension);
                        }
                    }

                    if (saveFile.exists())
                    {
                        overwrite = JOptionPane.showConfirmDialog(me, "File " + saveFile.getName() + " already exists. Overwrite?", "Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    }

                    if (overwrite == JOptionPane.YES_OPTION)
                    {
                        return saveFile;
                    }
                }
            } while (overwrite == JOptionPane.NO_OPTION);
        }
        return null;
    }

    /**
     * Save configuration to a file
     * 
     * @return whether the file was saved
     */
    public boolean saveConfig()
    {
        File saveFile = getTargetSaveFile(configSaver, DEFAULT_CONFIG_FILE_EXTENSION);
        if (saveFile != null)
        {
            try
            {
                fProps.saveFile(saveFile);
                return true;
            }
            catch (Exception ex)
            {
                logger.error("Configuration file save error", ex);
                return false;
            }
        }
        return false;
    }

    /**
     * Takes and saves a screenshot of the current chat window
     * 
     * @return whether the screenshot was saved
     */
    private boolean saveScreenshot()
    {
        // Take the screenshot before the save file chooser is shown
        ChatPanel chat = chatWindow.getChatPanel();
        BufferedImage chatImage = new BufferedImage(chat.getWidth(), chat.getHeight(), screenshotOptions.isTransparencyEnabled() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics chatGraphics = chatImage.getGraphics();
        chat.paint(chatGraphics);

        final boolean chromaEnabled = Boolean.toString(true).equalsIgnoreCase(fProps.getProperty(FontificatorProperties.KEY_CHAT_CHROMA_ENABLED));
        if (screenshotOptions.isTransparencyEnabled() && chromaEnabled)
        {
            final int chromaKey = new Color(Integer.parseInt(fProps.getProperty(FontificatorProperties.KEY_COLOR_CHROMA_KEY), 16)).getRGB();
            final int transparentPixel = new Color(0, true).getRGB();

            for (int r = 0; r < chatImage.getHeight(); r++)
            {
                for (int c = 0; c < chatImage.getWidth(); c++)
                {
                    if (chatImage.getRGB(c, r) == chromaKey)
                    {
                        chatImage.setRGB(c, r, transparentPixel);
                    }
                }
            }
        }

        File saveFile = getTargetSaveFile(screenshotSaver, DEFAULT_SCREENSHOT_FILE_EXTENSION);
        if (saveFile != null)
        {
            try
            {
                if (screenshotOptions.isMetadataEnabled())
                {
                    ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
                    ImageOutputStream stream = ImageIO.createImageOutputStream(saveFile);
                    writer.setOutput(stream);

                    IIOMetadata metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromRenderedImage(chatImage), writer.getDefaultWriteParam());

                    IIOMetadataNode title = generateMetadataNode("Title", "CGF Screenshot");
                    IIOMetadataNode software = generateMetadataNode("Software", "Chat Game Fontificator");
                    final String fontGameName = ControlPanelFont.getFontGameName(fProps.getProperty(FontificatorProperties.KEY_FONT_FILE_FONT));
                    final String borderGameName = ControlPanelFont.getBorderGameName(fProps.getProperty(FontificatorProperties.KEY_FONT_FILE_BORDER));
                    IIOMetadataNode description = generateMetadataNode("Description", fontGameName + " Font / " + borderGameName + " Border");

                    IIOMetadataNode text = new IIOMetadataNode("tEXt");
                    text.appendChild(title);
                    text.appendChild(software);
                    text.appendChild(description);

                    final String metadataFormatStr = "javax_imageio_png_1.0";
                    IIOMetadataNode root = new IIOMetadataNode(metadataFormatStr);
                    root.appendChild(text);
                    metadata.mergeTree(metadataFormatStr, root);

                    writer.write(metadata, new IIOImage(chatImage, null, metadata), writer.getDefaultWriteParam());

                    stream.close();
                }
                else
                {
                    ImageIO.write(chatImage, "png", saveFile);
                }

                return true;
            }
            catch (Exception e)
            {
                logger.error("Unable to save screenshot", e);
                return false;
            }
        }
        return false;
    }

    private IIOMetadataNode generateMetadataNode(String key, String value)
    {
        IIOMetadataNode node = new IIOMetadataNode("tEXtEntry");
        node.setAttribute("keyword", key);
        node.setAttribute("value", value);
        return node;
    }

    public void setAlwaysOnTopMenu(boolean alwaysOnTop)
    {
        ((JCheckBoxMenuItem) (getJMenuBar().getMenu(2).getItem(2))).setSelected(alwaysOnTop);
    }

    public void setRememberPositionMenu(boolean rememberPosition)
    {
        ((JCheckBoxMenuItem) (getJMenuBar().getMenu(2).getItem(3))).setSelected(rememberPosition);
    }

    public void setAntiAliasMenu(boolean antiAlias)
    {
        ((JCheckBoxMenuItem) (getJMenuBar().getMenu(2).getItem(0))).setSelected(antiAlias);
    }

    public void clearUsernameCases()
    {
        bot.clearUsernameCases();
    }

    public void addManualMessage(String username, String message)
    {
        bot.sendMessageToChat(MessageType.MANUAL, message, new TwitchPrivmsg(username));
    }

    public void disconnect()
    {
        bot.disconnect();
    }

    public void attemptToExit()
    {
        attemptToExit(this);
    }

    /**
     * Any program exit should call this method to do so
     */
    public void attemptToExit(Component parent)
    {
        boolean okayToProceed = fProps.checkForUnsavedProps(this, parent);
        if (okayToProceed)
        {
            disconnect();
            System.exit(0);
        }
    }

    /**
     * Construct the popup dialog containing the About message
     */
    private void constructAboutPopup()
    {
        aboutPane = new JEditorPane("text/html", ABOUT_CONTENTS);
        aboutPane.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent e)
            {
                if (EventType.ACTIVATED.equals(e.getEventType()))
                {
                    if (Desktop.isDesktopSupported())
                    {
                        try
                        {
                            Desktop.getDesktop().browse(URI.create("https://" + e.getDescription()));
                        }
                        catch (IOException e1)
                        {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        aboutPane.setEditable(false);
    }

    private void toggleDebugTab()
    {
        controlTabs.toggleDebugTab();
    }

    private void showAboutPane()
    {
        JOptionPane.showMessageDialog(this, aboutPane, "About", JOptionPane.PLAIN_MESSAGE);
    }

    public MessageDialog getMessageDialog()
    {
        return messageDialog;
    }

    public void loadAfterInit()
    {
        controlTabs.setChatWindowPosition();
    }

}
