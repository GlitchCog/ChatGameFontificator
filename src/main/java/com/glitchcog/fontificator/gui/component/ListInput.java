package com.glitchcog.fontificator.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.glitchcog.fontificator.gui.chat.ChatWindow;
import com.glitchcog.fontificator.gui.controls.panel.ControlPanelBase;
import com.glitchcog.fontificator.gui.controls.panel.MessageCensorPanel;

/**
 * GUI component that permits a user to maintain a list
 * 
 * @author Matt Yanos
 */
public class ListInput extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JLabel label;

    private JLabel description;

    private JTextField input;

    private List list;

    private JButton add;

    private JButton remove;

    private MessageCensorPanel censor;

    public ListInput(String label, String description, MessageCensorPanel censor)
    {
        this.censor = censor;
        build(label, description, 5);
    }

    private void build(String label, String description, int size)
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = ControlPanelBase.getGbc();
        gbc.insets = new Insets(0, 0, 0, 0);

        this.label = label == null ? null : new JLabel(label);
        if (description != null)
        {
            this.description = new JLabel(description);
            Font defFont = this.description.getFont();
            this.description.setFont(new Font(defFont.getName(), defFont.getStyle(), defFont.getSize() - 2));
        }
        this.input = new JTextField(12);
        this.list = new List(size);
        this.list.setMultipleMode(true);
        this.add = new JButton("\u271A");
        this.add.setBackground(Color.WHITE);
        this.add.setMargin(new Insets(0, 0, 0, 0));
        this.remove = new JButton("\u2718");
        this.remove.setBackground(Color.WHITE);
        this.remove.setMargin(new Insets(0, 0, 0, 0));

        // Backup in case the fancy unicode characters for the buttons aren't supported by the font
        if (!this.add.getFont().canDisplay(add.getText().charAt(0)))
        {
            this.add.setText("+");
        }
        if (!this.remove.getFont().canDisplay(remove.getText().charAt(0)))
        {
            this.add.setText("-");
        }

        // Listener to enable remove button when at least one item on the list is selected
        this.list.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                enableRemoveButton();
            }
        });

        // Listener to enable add button when a valid value has been entered in the input
        this.input.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                change(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                change(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                change(e);
            }

            private void change(DocumentEvent e)
            {
                enableAddButton();
            }
        });

        this.input.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                enableAddButton();
                if (add.isEnabled())
                {
                    addInput();
                }
            }
        });

        // Listener to handle add and remove button clicks
        ActionListener al = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JButton source = (JButton) e.getSource();
                if (add.equals(source))
                {
                    addInput();
                }
                else if (remove.equals(source))
                {
                    String[] selectedItems = list.getSelectedItems();
                    input.setText(selectedItems.length == 1 ? selectedItems[0] : "");
                    for (String item : selectedItems)
                    {
                        list.remove(item);
                    }
                    remove.setEnabled(false);
                    censor.recheckCensorship();
                }
            }
        };

        this.add.addActionListener(al);
        this.remove.addActionListener(al);

        // Start off the add and remove buttons disabled
        this.add.setEnabled(false);
        this.remove.setEnabled(false);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints ipGbc = ControlPanelBase.getGbc();
        ipGbc.insets = new Insets(0, 0, 0, 0);
        ipGbc.weightx = 1.0;
        ipGbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(this.input, ipGbc);
        ipGbc.gridx++;
        ipGbc.weightx = 0.0;
        ipGbc.fill = GridBagConstraints.VERTICAL;
        inputPanel.add(this.add, ipGbc);
        ipGbc.gridx++;
        inputPanel.add(this.remove, ipGbc);
        ipGbc.gridx++;

        gbc.anchor = GridBagConstraints.CENTER;
        if (this.label != null)
        {
            add(this.label, gbc);
            gbc.gridy++;
        }

        if (this.description != null)
        {
            add(this.description, gbc);
            gbc.gridy++;
        }

        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(inputPanel, gbc);
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy++;
        add(this.list, gbc);
        gbc.gridy++;
    }

    /**
     * To be called when the add button is clicked, or the enter key is pressed in the input box
     */
    private void addInput()
    {
        if (!input.getText().isEmpty() && !contains(input.getText().trim()))
        {
            addItem(input.getText());
            input.setText("");
            censor.recheckCensorship();
            censor.updateConfig();
        }
    }

    public String[] getItems()
    {
        return list.getItems();
    }

    public boolean contains(String value)
    {
        for (String item : list.getItems())
        {
            if (item.equalsIgnoreCase(value))
            {
                return true;
            }
        }
        return false;
    }

    public void addItem(String value)
    {
        if (value != null && !value.trim().isEmpty())
        {
            if (value.contains(","))
            {
                ChatWindow.popup.handleProblem("Value cannot contain a comma.");
            }
            else if (value.contains(" ") || value.contains("\t"))
            {
                ChatWindow.popup.handleProblem("Please insert only a single value.");
            }
            else
            {
                list.add(value.trim());
            }
        }
    }

    public void removeItem(String value)
    {
        for (String item : list.getItems())
        {
            if (item.equalsIgnoreCase(value))
            {
                list.remove(item);
            }
        }
    }

    public void setList(String[] itemList)
    {
        list.removeAll();
        for (String item : itemList)
        {
            if (item != null && !item.trim().isEmpty() && !item.trim().contains(",") && !item.trim().contains(" ") && !item.trim().contains("\t"))
            {
                list.add(item.trim());
            }
        }
    }

    public String[] getList()
    {
        return list.getItems();
    }

    /**
     * Used when the input dialog is typed in, and when the whole component is enabled or disabled, and also before
     * permitting an enter keystroke to trigger the add action to make sure the validation criteria tested here passes
     */
    private void enableAddButton()
    {
        add.setEnabled(!input.getText().isEmpty() && !input.getText().contains(",") && !input.getText().contains(" ") && !input.getText().contains("\t"));
    }

    /**
     * Used when the list is selected or deselected, and when the whole component is enabled or disabled
     */
    private void enableRemoveButton()
    {
        remove.setEnabled(list.getSelectedItems().length > 0);
    }

    public void setEnabled(boolean enabled)
    {
        label.setEnabled(enabled);
        description.setEnabled(enabled);
        input.setEnabled(enabled);
        list.setEnabled(enabled);
        enableAddButton();
        enableRemoveButton();
        if (!enabled)
        {
            input.setText("");
        }
    }
}
