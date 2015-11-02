package com.glitchcog.fontificator.config;

import java.util.Properties;

import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;

/**
 * The Configuration for Message Censorship
 * 
 * @author Matt Yanos
 */
public class ConfigCensor extends Config
{
    public static final int MIN_UNKNOWN_CHAR_PCT = 0;
    public static final int MAX_UNKNOWN_CHAR_PCT = 100;

    private Boolean censorshipEnabled;

    private Boolean censorAllUrls;

    private Boolean censorFirstUrls;

    private Boolean censorUnknownChars;

    private Integer unknownCharsPercent;

    private String[] userWhitelist;

    private String[] userBlacklist;

    private String[] bannedWords;

    @Override
    public void reset()
    {
        censorshipEnabled = null;
        censorAllUrls = null;
        censorFirstUrls = null;
        censorUnknownChars = null;
        unknownCharsPercent = null;
        userWhitelist = null;
        userBlacklist = null;
    }

    public LoadConfigReport validateStrings(LoadConfigReport report, String enabledStr, String urlStr, String firstUrlStr, String unknownCharStr, String unknownCharPctStr)
    {
        validateBooleanStrings(report, enabledStr, urlStr, firstUrlStr);
        validateIntegerWithLimitString(FontificatorProperties.KEY_CENSOR_UNKNOWN_CHARS_PERCENT, unknownCharPctStr, MIN_UNKNOWN_CHAR_PCT, MAX_UNKNOWN_CHAR_PCT, report);
        return report;
    }

    @Override
    public LoadConfigReport load(Properties props, LoadConfigReport report)
    {
        this.props = props;

        reset();

        if (report.isErrorFree())
        {
            final String enabledStr = props.getProperty(FontificatorProperties.KEY_CENSOR_ENABLED);
            final String urlStr = props.getProperty(FontificatorProperties.KEY_CENSOR_URL);
            final String firstUrlStr = props.getProperty(FontificatorProperties.KEY_CENSOR_FIRST_URL);
            final String unknownCharStr = props.getProperty(FontificatorProperties.KEY_CENSOR_UNKNOWN_CHARS);
            final String unknownCharPctStr = props.getProperty(FontificatorProperties.KEY_CENSOR_UNKNOWN_CHARS_PERCENT);
            final String whiteStr = props.containsKey(FontificatorProperties.KEY_CENSOR_WHITE) ? props.getProperty(FontificatorProperties.KEY_CENSOR_WHITE) : "";
            final String blackStr = props.containsKey(FontificatorProperties.KEY_CENSOR_BLACK) ? props.getProperty(FontificatorProperties.KEY_CENSOR_BLACK) : "";
            final String bannedStr = props.containsKey(FontificatorProperties.KEY_CENSOR_BANNED) ? props.getProperty(FontificatorProperties.KEY_CENSOR_BANNED) : "";

            // Check that the values are valid
            validateStrings(report, enabledStr, urlStr, firstUrlStr, unknownCharStr, unknownCharPctStr);

            // Fill the values
            if (report.isErrorFree())
            {
                censorshipEnabled = evaluateBooleanString(props, FontificatorProperties.KEY_CENSOR_ENABLED, report);
                censorAllUrls = evaluateBooleanString(props, FontificatorProperties.KEY_CENSOR_URL, report);
                censorFirstUrls = evaluateBooleanString(props, FontificatorProperties.KEY_CENSOR_FIRST_URL, report);

                censorUnknownChars = evaluateBooleanString(props, FontificatorProperties.KEY_CENSOR_UNKNOWN_CHARS, report);
                unknownCharsPercent = evaluateIntegerString(props, FontificatorProperties.KEY_CENSOR_UNKNOWN_CHARS_PERCENT, report);

                userWhitelist = whiteStr.split(",");
                userBlacklist = blackStr.split(",");
                bannedWords = bannedStr.split(",");
            }
        }

        return report;
    }

    public boolean isCensorshipEnabled()
    {
        return censorshipEnabled;
    }

    public void setCensorshipEnabled(Boolean censorshipEnabled)
    {
        this.censorshipEnabled = censorshipEnabled;
    }

    public boolean isCensorAllUrls()
    {
        return censorAllUrls;
    }

    public void setCensorAllUrls(Boolean censorAllUrls)
    {
        this.censorAllUrls = censorAllUrls;
    }

    public boolean isCensorFirstUrls()
    {
        return censorFirstUrls;
    }

    public void setCensorFirstUrls(Boolean censorFirstUrls)
    {
        this.censorFirstUrls = censorFirstUrls;
    }

    public boolean isCensorUnknownChars()
    {
        return censorUnknownChars;
    }

    public void setCensorUnknownChars(Boolean censorUnknownChars)
    {
        this.censorUnknownChars = censorUnknownChars;
    }

    public int getUnknownCharPercentage()
    {
        return unknownCharsPercent;
    }

    public void setUnknownCharPercentage(Integer unknownCharsPercent)
    {
        this.unknownCharsPercent = unknownCharsPercent;
    }

    public String[] getUserWhitelist()
    {
        return userWhitelist;
    }

    public void setUserWhitelist(String[] userWhitelist)
    {
        this.userWhitelist = userWhitelist;
    }

    public String[] getUserBlacklist()
    {
        return userBlacklist;
    }

    public void setUserBlacklist(String[] userBlacklist)
    {
        this.userBlacklist = userBlacklist;
    }

    public String[] getBannedWords()
    {
        return bannedWords;
    }

    public void setBannedWords(String[] bannedWords)
    {
        this.bannedWords = bannedWords;
    }

    public String getUserWhitelistString()
    {
        return getListAsString(userWhitelist);
    }

    public String getUserBalckListString()
    {
        return getListAsString(userBlacklist);
    }

    public String getBannedWordsString()
    {
        return getListAsString(bannedWords);
    }

    private String getListAsString(String[] list)
    {
        String output = "";
        for (int i = 0; i < list.length; i++)
        {
            output += (i == 0 ? "" : ",") + list[i];
        }
        return output;

    }
}
