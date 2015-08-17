package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.config.ConfigChat;
import com.glitchcog.fontificator.config.FontificatorProperties;
import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.component.LabeledInput;
import com.glitchcog.fontificator.gui.component.LabeledSlider;
import com.glitchcog.fontificator.gui.controls.ControlWindow;

/**
 * Control Panel containing all the Chat Window options
 * 
 * @author Matt Yanos
 */
public class ControlPanelChat extends ControlPanelBase
{
    private static final long serialVersionUID = 1L;

    private JCheckBox resizableBox;

    private JCheckBox scrollableBox;

    private LabeledInput widthInput;

    private LabeledInput heightInput;

    /**
     * The chat config object that bridges the UI to the properties file
     */
    private ConfigChat config;

    private ComponentListener resizeListener;

    private JButton updateSizeButton;

    private JCheckBox chromaEnabledBox;

    private JCheckBox chromaInvertBox;

    private LabeledInput[] chromaBorderInput;

    private JButton updateChromaSizeButton;

    private LabeledSlider chromaCornerSlider;

    private ControlWindow ctrlWindow;

    /**
     * Construct a chat control panel
     * 
     * @param fProps
     * @param chatWindow
     * @param ctrlWindow
     */
    public ControlPanelChat(FontificatorProperties fProps, ChatWindow chatWindow, ControlWindow ctrlWindow)
    {
        super("Chat Window", fProps, chatWindow);

        this.ctrlWindow = ctrlWindow;

        resizeListener = new ComponentListener()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                Component c = (Component) e.getSource();
                final int w = c.getWidth();
                final int h = c.getHeight();
                widthInput.setText(Integer.toString(w));
                heightInput.setText(Integer.toString(h));
                config.setWidth(w);
                config.setHeight(h);
                updateSizeButton.setEnabled(false);
            }

            @Override
            public void componentMoved(ComponentEvent e)
            {
            }

            @Override
            public void componentShown(ComponentEvent e)
            {
            }

            @Override
            public void componentHidden(ComponentEvent e)
            {
            }
        };
        this.chatWindow.addComponentListener(resizeListener);

    }

    @Override
    protected void build()
    {
        resizableBox = new JCheckBox("Resize Chat by Dragging");
        scrollableBox = new JCheckBox("Scrollable Chat");
        widthInput = new LabeledInput("Width", 3);
        heightInput = new LabeledInput("Height", 3);

        DocumentListener chatSizeDocListener = new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                somethingChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                somethingChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                somethingChanged(e);
            }

            /**
             * Something changed, so try to get the new width and height and set the updateSizeButton to enabled or
             * disabled accordingly
             * 
             * @param e
             */
            private void somethingChanged(DocumentEvent e)
            {
                try
                {
                    int w = Integer.parseInt(widthInput.getText());
                    int h = Integer.parseInt(heightInput.getText());
                    updateSizeButton.setEnabled(w != config.getWidth() || h != config.getHeight());
                }
                catch (Exception ex)
                {
                    updateSizeButton.setEnabled(false);
                }
            }
        };

        DocumentListener chromaDocListener = new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                toggleChromaButtonEnabled();
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                toggleChromaButtonEnabled();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                toggleChromaButtonEnabled();
            }
        };

        widthInput.addDocumentListener(chatSizeDocListener);
        heightInput.addDocumentListener(chatSizeDocListener);

        updateSizeButton = new JButton("Update Chat Size");
        chromaEnabledBox = new JCheckBox("Enable Chroma Key Border");
        chromaInvertBox = new JCheckBox("Invert Chroma Key Border");
        chromaCornerSlider = new LabeledSlider("Corner Radius", "pixels", ConfigChat.MIN_CHROMA_CORNER_RADIUS, ConfigChat.MAX_CHROMA_CORNER_RADIUS);

        final String[] chromaLabels = new String[] { "Left", "Top", "Right", "Bottom" };
        chromaBorderInput = new LabeledInput[chromaLabels.length];
        for (int i = 0; i < chromaBorderInput.length; i++)
        {
            chromaBorderInput[i] = new LabeledInput(chromaLabels[i], 4);
            chromaBorderInput[i].addDocumentListener(chromaDocListener);
        }
        updateChromaSizeButton = new JButton("Update Chroma Border");

        ActionListener boxListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JCheckBox source = (JCheckBox) e.getSource();
                if (resizableBox.equals(source))
                {
                    chatWindow.setResizable(resizableBox.isSelected());
                    config.setResizable(resizableBox.isSelected());
                }
                else if (scrollableBox.equals(source))
                {
                    config.setScrollable(scrollableBox.isSelected());
                    if (!scrollableBox.isSelected())
                    {
                        // No scrolling, so reset any existing scroll offset
                        chat.resetScrollOffset();
                    }
                }
                else if (chromaEnabledBox.equals(source))
                {
                    config.setChromaEnabled(chromaEnabledBox.isSelected());
                    toggleChromaInputFields();
                }
                else if (chromaInvertBox.equals(source))
                {
                    config.setChromaInvert(chromaInvertBox.isSelected());
                }
                chat.repaint();
            }
        };

        resizableBox.addActionListener(boxListener);
        scrollableBox.addActionListener(boxListener);
        chromaEnabledBox.addActionListener(boxListener);
        chromaInvertBox.addActionListener(boxListener);

        ActionListener dimListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    List<String> errors = new ArrayList<String>();
                    config.validateDimStrings(errors, widthInput.getText(), heightInput.getText());
                    if (errors.isEmpty())
                    {
                        final int width = Integer.parseInt(widthInput.getText());
                        final int height = Integer.parseInt(heightInput.getText());
                        config.setWidth(width);
                        config.setHeight(height);
                        chatWindow.setSize(config.getWidth(), config.getHeight());
                    }
                    else
                    {
                        ChatWindow.popup.handleProblem(errors);
                    }
                }
                catch (Exception ex)
                {
                    ChatWindow.popup.handleProblem("Chat Width and Chat Height values could not be parsed", ex);
                }
            }
        };

        updateSizeButton.addActionListener(dimListener);

        ActionListener chromaDimListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    List<String> errors = new ArrayList<String>();
                    config.validateChromaDimStrings(errors, chromaBorderInput[0].getText(), chromaBorderInput[1].getText(), chromaBorderInput[2].getText(), chromaBorderInput[3].getText());
                    if (errors.isEmpty())
                    {
                        inputToConfigChromaBorders();
                        chat.repaint();
                    }
                    else
                    {
                        ChatWindow.popup.handleProblem(errors);
                    }
                }
                catch (Exception ex)
                {
                    ChatWindow.popup.handleProblem("Chat Chroma border values could not be parsed", ex);
                }
            }
        };

        updateChromaSizeButton.addActionListener(chromaDimListener);

        chromaCornerSlider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                config.setChromaCornerRadius(chromaCornerSlider.getValue());
                chat.repaint();
            }
        });

        JPanel chatDimPanel = new JPanel(new GridBagLayout());
        JPanel chatOptionsPanel = new JPanel(new GridBagLayout());
        JPanel chromaDimPanel = new JPanel(new GridBagLayout());

        chatDimPanel.setBorder(new TitledBorder(baseBorder, "Chat Window Size", TitledBorder.CENTER, TitledBorder.TOP));
        chatOptionsPanel.setBorder(new TitledBorder(baseBorder, "Chat Window Options", TitledBorder.CENTER, TitledBorder.TOP));
        chromaDimPanel.setBorder(baseBorder);

        GridBagConstraints dGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0);
        dGbc.gridx = 0;
        dGbc.gridy = 0;
        dGbc.gridwidth = 1;
        chatDimPanel.add(widthInput, dGbc);
        dGbc.gridx++;
        chatDimPanel.add(heightInput, dGbc);
        dGbc.gridx = 0;
        dGbc.gridy++;
        dGbc.gridwidth = 2;
        chatDimPanel.add(updateSizeButton, dGbc);
        dGbc.gridy++;

        GridBagConstraints coGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, DEFAULT_INSETS, 0, 0);
        coGbc.anchor = GridBagConstraints.WEST;
        chatOptionsPanel.add(resizableBox, coGbc);
        coGbc.gridy++;
        chatOptionsPanel.add(scrollableBox, coGbc);
        coGbc.gridy++;

        GridBagConstraints chromaGbc = new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, NO_INSETS, 0, 0);
        chromaGbc.gridwidth = 3;
        chromaDimPanel.add(chromaEnabledBox, chromaGbc);
        chromaGbc.gridy++;
        chromaDimPanel.add(chromaBorderInput[1], chromaGbc);
        chromaGbc.gridy++;
        chromaGbc.gridwidth = 1;
        chromaGbc.anchor = GridBagConstraints.EAST;
        chromaDimPanel.add(chromaBorderInput[0], chromaGbc);
        chromaGbc.gridx++;
        chromaGbc.anchor = GridBagConstraints.CENTER;
        chromaDimPanel.add(updateChromaSizeButton, chromaGbc);
        chromaGbc.gridx++;
        chromaGbc.anchor = GridBagConstraints.WEST;
        chromaDimPanel.add(chromaBorderInput[2], chromaGbc);
        chromaGbc.gridx = 0;
        chromaGbc.anchor = GridBagConstraints.CENTER;
        chromaGbc.gridy++;
        chromaGbc.gridwidth = 3;
        chromaDimPanel.add(chromaBorderInput[3], chromaGbc);
        chromaGbc.gridy++;
        chromaDimPanel.add(chromaInvertBox, chromaGbc);
        chromaGbc.gridy++;
        chromaDimPanel.add(chromaCornerSlider, chromaGbc);

        JPanel everything = new JPanel(new GridBagLayout());
        GridBagConstraints eGbc = new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, NO_INSETS, 0, 0);

        eGbc.weighty = 0.5;
        eGbc.weightx = 0.5;
        eGbc.fill = GridBagConstraints.HORIZONTAL;
        eGbc.insets = new Insets(0, 3, 0, 3);
        everything.add(chatDimPanel, eGbc);
        eGbc.weightx = 0.5;
        eGbc.gridx++;
        everything.add(chatOptionsPanel, eGbc);
        eGbc.weightx = 1.0;
        eGbc.weighty = 0.0;
        eGbc.gridx = 0;
        eGbc.gridy++;
        eGbc.gridwidth = 2;
        eGbc.fill = GridBagConstraints.BOTH;
        eGbc.anchor = GridBagConstraints.CENTER;
        everything.add(chromaDimPanel, eGbc);
        eGbc.gridy++;

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(everything, gbc);
    }

    /**
     * Set the chroma borders from the input fields. Input fields must be validated first.
     */
    private void inputToConfigChromaBorders()
    {
        int[] values = new int[chromaBorderInput.length];
        for (int i = 0; i < chromaBorderInput.length; i++)
        {
            values[i] = Integer.parseInt(chromaBorderInput[i].getText());
        }
        config.setChromaBorder(values[0], values[1], values[2], values[3]);
    }

    private void toggleChromaInputFields()
    {
        final boolean enabled = config.isChromaEnabled();

        updateChromaSizeButton.setEnabled(enabled);
        for (int i = 0; i < chromaBorderInput.length; i++)
        {
            chromaBorderInput[i].setEnabled(enabled);
        }
        chromaInvertBox.setEnabled(enabled);
        chromaCornerSlider.setEnabled(enabled);

        if (enabled)
        {
            toggleChromaButtonEnabled();
        }
    }

    private void toggleChromaButtonEnabled()
    {
        try
        {
            // @formatter:off
            boolean differenceFound = 
            Integer.parseInt(chromaBorderInput[0].getText()) != config.getChromaBorder().x || 
            Integer.parseInt(chromaBorderInput[1].getText()) != config.getChromaBorder().y || 
            Integer.parseInt(chromaBorderInput[2].getText()) != config.getChromaBorder().width || 
            Integer.parseInt(chromaBorderInput[3].getText()) != config.getChromaBorder().height;
            // @formatter:on
            updateChromaSizeButton.setEnabled(differenceFound);
        }
        catch (Exception ex)
        {
            updateChromaSizeButton.setEnabled(false);
        }
    }

    @Override
    protected void fillInputFromProperties(FontificatorProperties fProps)
    {
        this.config = fProps.getChatConfig();
        fillInputFromConfig();
    }

    @Override
    protected void fillInputFromConfig()
    {
        this.resizableBox.setSelected(config.isResizable());
        this.scrollableBox.setSelected(config.isScrollable());
        this.widthInput.setText(Integer.toString(config.getWidth()));
        this.heightInput.setText(Integer.toString(config.getHeight()));

        this.chromaEnabledBox.setSelected(config.isChromaEnabled());

        this.chromaBorderInput[0].setText(Integer.toString(config.getChromaBorder().x));
        this.chromaBorderInput[1].setText(Integer.toString(config.getChromaBorder().y));
        this.chromaBorderInput[2].setText(Integer.toString(config.getChromaBorder().width));
        this.chromaBorderInput[3].setText(Integer.toString(config.getChromaBorder().height));

        this.chromaInvertBox.setSelected(config.isChromaInvert());
        this.chromaCornerSlider.setValue(config.getChromaCornerRadius());

        chatWindow.setSize(config.getWidth(), config.getHeight());

        toggleChromaInputFields();

        // Because it won't be set when this is called from the super class constructor
        if (ctrlWindow != null)
        {
            ctrlWindow.setAlwaysOnTopMenu(config.isAlwaysOnTop());
        }
    }

    @Override
    protected List<String> validateInput()
    {
        List<String> errors = new ArrayList<String>();
        config.validateDimStrings(errors, widthInput.getText(), heightInput.getText());
        config.validateChromaDimStrings(errors, chromaBorderInput[0].getText(), chromaBorderInput[1].getText(), chromaBorderInput[2].getText(), chromaBorderInput[3].getText());
        return errors;
    }

    @Override
    protected void fillConfigFromInput() throws Exception
    {
        config.setResizable(resizableBox.isSelected());
        config.setScrollable(scrollableBox.isSelected());
        chatWindow.setAlwaysOnTop(config.isAlwaysOnTop());
        final int width = Integer.parseInt(widthInput.getText());
        final int height = Integer.parseInt(heightInput.getText());
        config.setWidth(width);
        config.setHeight(height);

        config.setChromaEnabled(chromaEnabledBox.isSelected());
        inputToConfigChromaBorders();
        config.setChromaCornerRadius(chromaCornerSlider.getValue());
        config.setChromaInvert(chromaInvertBox.isSelected());
    }

    public void setAlwaysOnTop(boolean alwaysOnTop)
    {
        Logger.getLogger(ControlPanelChat.class).debug("Always on top passed through ControlPanelChat");
        config.setAlwaysOnTop(alwaysOnTop);
    }
}
