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
import java.util.ArrayList;
import java.util.List;
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
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;
import com.glitchcog.fontificator.gui.AssetIndexLoader;
import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.MenuComponent;
import com.glitchcog.fontificator.gui.controls.messages.MessageDialog;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelDebug;
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

        Map<String, List<String[]>> presetMapSubmenuToItem = AssetIndexLoader.loadPresets();

        final List<String[]> allPresets = new ArrayList<String[]>();
        for (String key : presetMapSubmenuToItem.keySet())
        {
            for (String[] value : presetMapSubmenuToItem.get(key))
            {
                allPresets.add(value);
            }
        }

        ActionListener presetListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String sourceText = ((JMenuItem) e.getSource()).getText();
                for (String[] presets : allPresets)
                {
                    if (presets[0].equals(sourceText))
                    {
                        loadPreset(presets[0], presets[1]);
                        break;
                    }
                }
            }
        };

        for (String submenuKey : presetMapSubmenuToItem.keySet())
        {
            List<String[]> submenuItems = presetMapSubmenuToItem.get(submenuKey);
            if (submenuKey != null)
            {
                JMenu submenu = new JMenu(submenuKey);
                for (String[] itemAndFilename : submenuItems)
                {
                    JMenuItem submenuItem = new JMenuItem(itemAndFilename[0]);
                    submenuItem.addActionListener(presetListener);
                    submenu.add(submenuItem);
                }
                menus[1].add(submenu);
            }
            else
            {
                for (String[] submenuRootItemAndFilename : submenuItems)
                {
                    JMenuItem submenuRootItem = new JMenuItem(submenuRootItemAndFilename[0]);
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
    public static File getTargetSaveFile(JFileChooser chooser, String extension)
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
        return null;
    }

    /**
     * Save configuration to a file
     * 
     * @return whether the file was saved
     */
    public boolean saveConfig()
    {
        final boolean configReadyToSave = controlTabs.refreshConfigFromUi();
        File saveFile = null;
        if (configReadyToSave)
        {
            saveFile = getTargetSaveFile(configSaver, DEFAULT_CONFIG_FILE_EXTENSION);
        }
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

    public ControlPanelDebug getDebugPanel()
    {
        return controlTabs.getDebugTab();
    }

}
