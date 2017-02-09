package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import com.glitchcog.fontificator.gui.chat.ChatPanel;
import com.glitchcog.fontificator.gui.controls.ControlWindow;

/**
 * Panel on the Debug Control Panel for creating collages of different presets
 * 
 * @author Matt Yanos
 */
public class CollagePanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(CollagePanel.class);

    private ChatPanel chat;

    private Font font;

    private JButton addCaptureButton;

    private JButton remCaptureButton;

    private JCheckBox transparencyBox;

    private JTextField imageLabelInput;

    private JTextArea headerTextInput;

    private JButton saveButton;

    private JButton resetButton;

    private List<BufferedImage> images;

    private JFileChooser collageChooser;

    private JPanel previewPanel;

    private BufferedImage previewImage;

    private static final String TEXT_FONT = "%FONTNAME%";

    private static final String TEXT_BORDER = "%BORDERNAME%";

    private static final String DEFAULT_HEADER_TEXT = "Chat Game Fontificator ~ github.com/GlitchCog/ChatGameFontificator ~ [FREE & OPEN SOURCE]";

    private final Color drawBlockColor = Color.DARK_GRAY;

    private final Color drawTextColor = Color.WHITE;

    public CollagePanel(ChatPanel chat)
    {
        this.chat = chat;
        this.font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

        this.collageChooser = new JFileChooser();
        FileFilter pngFileFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
        this.collageChooser.setFileFilter(pngFileFilter);

        images = new ArrayList<BufferedImage>();
        build();
    }

    private void build()
    {
        addCaptureButton = new JButton("+Screenshot");
        remCaptureButton = new JButton("Undo");
        transparencyBox = new JCheckBox("Image Transparency");
        imageLabelInput = new JTextField(TEXT_FONT, 12);
        headerTextInput = new JTextArea(DEFAULT_HEADER_TEXT);
        headerTextInput.setLineWrap(true);
        headerTextInput.setWrapStyleWord(true);
        saveButton = new JButton("Save Collage");
        resetButton = new JButton("Reset");

        previewPanel = new JPanel()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void paint(Graphics g)
            {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                if (previewImage != null)
                {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    final double zoom = Math.min((double) getWidth() / previewImage.getWidth(), (double) getHeight() / previewImage.getHeight());

                    final int dw = (int) (previewImage.getWidth() * zoom);
                    final int dh = (int) (previewImage.getHeight() * zoom);
                    final int dx = (getWidth() - dw) / 2;
                    final int dy = (getHeight() - dh) / 2;

                    g2d.drawImage(previewImage, dx, dy, dx + dw, dy + dh, 0, 0, previewImage.getWidth(), previewImage.getHeight(), null);
                }
            }
        };

        addCaptureButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                addCapture();
                refreshPreviewImage();
            }
        });

        remCaptureButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (images.size() > 0)
                {
                    images.remove(images.size() - 1);
                    refreshPreviewImage();
                }
            }
        });

        saveButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BufferedImage collageImage = generateCollageImage();
                if (collageImage != null)
                {
                    saveToFile(collageImage);
                }
            }
        });

        resetButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                images.clear();
                refreshPreviewImage();
            }
        });

        setBorder(new TitledBorder(ControlPanelBase.getBaseBorder(), "Collage Generator", TitledBorder.CENTER, TitledBorder.TOP));

        setLayout(new GridLayout(1, 3));

        JPanel left = new JPanel(new GridBagLayout());
        GridBagConstraints lgbc = ControlPanelBase.getGbc();
        lgbc.weighty = 0.0;
        JPanel right = new JPanel(new GridBagLayout());
        GridBagConstraints rgbc = ControlPanelBase.getGbc();
        rgbc.weighty = 0.0;

        JPanel addRemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints addRemGbc = ControlPanelBase.getGbc();
        addRemPanel.add(addCaptureButton, addRemGbc);
        addRemGbc.gridx++;
        addRemPanel.add(remCaptureButton, addRemGbc);
        addRemGbc.gridx++;

        JPanel labelPanel = new JPanel(new GridBagLayout());
        GridBagConstraints labelGbc = ControlPanelBase.getGbc();
        labelPanel.add(new JLabel("Label:"), labelGbc);
        labelGbc.gridx++;
        labelGbc.fill = GridBagConstraints.BOTH;
        labelGbc.weightx = 1.0;
        labelPanel.add(imageLabelInput, labelGbc);
        labelGbc.gridx++;

        lgbc.fill = GridBagConstraints.HORIZONTAL;
        lgbc.anchor = GridBagConstraints.CENTER;
        lgbc.weightx = 1.0;
        left.add(addRemPanel, lgbc);
        lgbc.gridy++;
        left.add(labelPanel, lgbc);
        lgbc.gridy++;
        left.add(transparencyBox, lgbc);
        lgbc.gridy++;
        left.add(resetButton, lgbc);
        lgbc.gridy++;
        lgbc.weighty = 1.0;
        lgbc.fill = GridBagConstraints.BOTH;
        left.add(new JPanel());

        rgbc.fill = GridBagConstraints.HORIZONTAL;
        rgbc.weightx = 1.0;
        right.add(saveButton, rgbc);
        rgbc.gridy++;
        right.add(new JLabel("Header text:"), rgbc);
        rgbc.gridy++;
        rgbc.weighty = 1.0;
        rgbc.fill = GridBagConstraints.BOTH;
        right.add(new JScrollPane(headerTextInput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), rgbc);
        rgbc.gridy++;

        add(left);
        add(previewPanel);
        add(right);
    }

    private void refreshPreviewImage()
    {
        previewImage = generateCollageImage();
        previewPanel.repaint();
    }

    private void addCapture()
    {
        final String imageText = getImageText();
        final int labelHeight = imageText.isEmpty() ? 0 : 24;

        final int width = chat.getWidth();
        final int height = chat.getHeight() + labelHeight;

        BufferedImage chatImage = new BufferedImage(width, height, transparencyBox.isSelected() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics chatGraphics = chatImage.getGraphics();
        chat.paint(chatGraphics);

        if (!imageText.isEmpty())
        {
            try
            {
                chatGraphics.setColor(drawBlockColor);
                chatGraphics.fillRect(1, chat.getHeight(), width - 2, labelHeight - 1);
                chatGraphics.setColor(drawTextColor);
                chatGraphics.setFont(font);
                chatGraphics.drawString(imageText, 6, height - 8);
            }
            catch (Exception e)
            {
                logger.error("Unable to write text to image", e);
            }
        }

        images.add(chatImage);
    }

    private String getImageText()
    {
        String text = imageLabelInput.getText();
        text = text.replaceAll(TEXT_FONT, chat.getFontGameName());
        text = text.replaceAll(TEXT_BORDER, chat.getBorderGameName());
        return text;
    }

    private BufferedImage generateCollageImage()
    {
        if (images == null || images.isEmpty())
        {
            return null;
        }

        // TODO: Implement a 2D bin packing algorithm here

        final int headerHeight = headerTextInput.getText().isEmpty() ? 0 : 24;
        int maxWidth = 0;
        int maxHeight = 0;
        for (BufferedImage bi : images)
        {
            maxWidth = Math.max(maxWidth, bi.getWidth());
            maxHeight = Math.max(maxHeight, bi.getHeight());
        }
        int gridWidth = (int) Math.ceil(Math.sqrt(images.size()));
        int gridHeight = (int) Math.ceil(images.size() / (double) gridWidth);

        BufferedImage collageImage = new BufferedImage(maxWidth * gridWidth, maxHeight * gridHeight + headerHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D collageGraphics = (Graphics2D) collageImage.getGraphics();

        collageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        collageGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int x, y, offsetX, offsetY;
        for (int i = 0; i < images.size(); i++)
        {
            x = i % gridWidth;
            y = i / gridWidth;
            offsetX = (maxWidth - images.get(i).getWidth()) / 2;
            offsetY = (maxHeight - images.get(i).getHeight()) / 2 + headerHeight;
            collageGraphics.drawImage(images.get(i), x * maxWidth + offsetX, y * maxHeight + offsetY, null);
        }

        // Draw header
        if (!headerTextInput.getText().isEmpty())
        {
            collageGraphics.setColor(drawBlockColor);
            collageGraphics.fillRect(0, 0, collageImage.getWidth(), headerHeight);
            collageGraphics.setColor(drawTextColor);
            collageGraphics.drawString(headerTextInput.getText(), 3, 16);
        }

        return collageImage;
    }

    private boolean saveToFile(BufferedImage collageImage)
    {
        File saveFile = ControlWindow.getTargetSaveFile(collageChooser, "png");
        if (saveFile != null && collageImage != null)
        {
            try
            {
                ImageIO.write(collageImage, "png", saveFile);
                return true;
            }
            catch (Exception e)
            {
                logger.error("Unable to save collage", e);
                return false;
            }
        }
        return false;
    }
}
