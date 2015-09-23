package com.glitchcog.fontificator.gui.controls.panel;

import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;

public class LogBox extends JScrollPane
{
    private static final long serialVersionUID = 1L;

    private JTextArea output;

    private String authCode;

    public LogBox()
    {
        super(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.authCode = null;

        output = new JTextArea();
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        output.setWrapStyleWord(true);
        output.setLineWrap(true);
        output.setEditable(false);
        output.setBackground(getBackground());

        super.setViewportView(output);
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public void log(LoadConfigReport report)
    {
        for (String msg : report.getMessages())
        {
            log(msg);
        }
    }

    public void log(String line)
    {
        if (authCode != null && line.contains(authCode))
        {
            String blocks = "";
            for (int i = 0; i < authCode.length(); i++)
            {
                blocks += "*";
            }
            // Blank out any time the oauth key is logged- this output might accidentally wind up in a video stream
            line = line.replaceAll(authCode, blocks);
        }
        output.append((output.getText().isEmpty() ? "" : "\n") + line);
        output.setCaretPosition(output.getDocument().getLength());
    }

    public void clear()
    {
        output.setText("");
    }

}
