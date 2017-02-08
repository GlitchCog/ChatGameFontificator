package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

    private JButton saveButton;

    private JButton resetButton;

    private List<BufferedImage> images;

    private JFileChooser collageChooser;

    private JPanel previewPanel;

    private BufferedImage previewImage;

    private static final String TEXT_FONT = "%FONTNAME%";

    private static final String TEXT_BORDER = "%BORDERNAME%";

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
        addCaptureButton = new JButton("Add Screenshot");
        remCaptureButton = new JButton("Undo Screenshot");
        transparencyBox = new JCheckBox("Transparency");
        imageLabelInput = new JTextField(TEXT_FONT, 12);
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
                saveToFile(collageImage);
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

        setLayout(new GridLayout(1, 2));

        JPanel left = new JPanel(new GridLayout(5, 1));
        left.add(addCaptureButton);
        JPanel optionsPanel = new JPanel();
        optionsPanel.add(new JLabel("Label:"));
        optionsPanel.add(imageLabelInput);
        optionsPanel.add(transparencyBox);
        left.add(optionsPanel);
        left.add(remCaptureButton);
        left.add(saveButton);
        left.add(resetButton);

        add(left);
        add(previewPanel);
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
                chatGraphics.setColor(Color.WHITE);
                chatGraphics.setFont(font);
                chatGraphics.drawString(imageText, 3, height - 8);
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

        int maxWidth = 0;
        int maxHeight = 0;
        for (BufferedImage bi : images)
        {
            maxWidth = Math.max(maxWidth, bi.getWidth());
            maxHeight = Math.max(maxHeight, bi.getHeight());
        }
        int gridWidth = (int) Math.ceil(Math.sqrt(images.size()));
        int gridHeight = (int) Math.ceil(images.size() / (double) gridWidth);

        BufferedImage collageImage = new BufferedImage(maxWidth * gridWidth, maxHeight * gridHeight, transparencyBox.isSelected() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics collageGraphics = collageImage.getGraphics();
        int x, y, offsetX, offsetY;
        for (int i = 0; i < images.size(); i++)
        {
            x = i % gridWidth;
            y = i / gridWidth;
            offsetX = (maxWidth - images.get(i).getWidth()) / 2;
            offsetY = (maxHeight - images.get(i).getHeight()) / 2;
            collageGraphics.drawImage(images.get(i), x * maxWidth + offsetX, y * maxHeight + offsetY, null);
        }

        return collageImage;
    }

    private boolean saveToFile(BufferedImage collageImage)
    {
        File saveFile = ControlWindow.getTargetSaveFile(collageChooser, "png");
        if (saveFile != null)
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
