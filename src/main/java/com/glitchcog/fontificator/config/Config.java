package com.glitchcog.fontificator.config;

import java.awt.Color;
import java.util.Properties;

import com.glitchcog.fontificator.config.loadreport.LoadConfigErrorType;
import com.glitchcog.fontificator.config.loadreport.LoadConfigReport;

/**
 * Config objects bridge the gap between the raw, unvalidated properties object and the processed values the ControlPanels need for configuration
 * 
 * @author Matt Yanos
 */
public abstract class Config
{
    /**
     * Keep a reference to the properties for updating when changes are made
     */
    protected Properties props;

    /**
     * Loads all fields from a Properties object
     * 
     * @param props
     * @return report
     */
    public abstract LoadConfigReport load(Properties props, LoadConfigReport report);

    /**
     * Clear out existing config data
     */
    public abstract void reset();

    protected static final String[] TRUES = new String[] { Boolean.toString(true), "yes", "+", "t", "1" };
    protected static final String[] FALSES = new String[] { Boolean.toString(false), "no", "-", "f", "0" };

    protected LoadConfigReport baseValidation(Properties props, String[] keys, LoadConfigReport report)
    {
        for (int i = 0; i < keys.length; i++)
        {
            if (!props.containsKey(keys[i]))
            {
                report.addError("Key " + keys[i] + " missing in the configuration", LoadConfigErrorType.MISSING_KEY);
            }
            else if (props.getProperty(keys[i]) == null || props.getProperty(keys[i]).trim().isEmpty())
            {
                report.addError("Value for key " + keys[i] + " missing in the configuration", LoadConfigErrorType.MISSING_VALUE);
            }
        }

        return report;
    }

    /**
     * Check that the string values of a collection of booleans are all valid booleans
     * 
     * @param report
     * @param booleans
     * @return report
     */
    protected LoadConfigReport validateBooleanStrings(LoadConfigReport report, String... booleans)
    {
        for (int i = 0; i < booleans.length; i++)
        {
            evaluateBooleanString(booleans[i], report);
        }
        return report;
    }

    /**
     * Collect and return report errors for the specified String representation value of an integer with the specified minimum. The key is just used to report the error.
     * 
     * @param key
     *            the key for the value to check
     * @param value
     *            the value to check
     * @param minimum
     *            inclusive minimum
     * @param report
     * @return report
     */
    protected LoadConfigReport validateIntegerWithLimitString(String key, String value, int minimum, LoadConfigReport report)
    {
        return validateIntegerWithLimitString(key, value, minimum, Integer.MAX_VALUE, report);
    }

    /**
     * Collect and return load report for the specified String representation value of an integer with the specified minimum and maximum. The key is just used to report the error.
     * 
     * @param key
     *            the key for the value to check
     * @param value
     *            the value to check
     * @param minimum
     *            inclusive minimum
     * @param maximum
     *            inclusive maximum
     * @param report
     * @return report
     */
    protected LoadConfigReport validateIntegerWithLimitString(String key, String value, int minimum, int maximum, LoadConfigReport report)
    {
        validateIntegerString(key, value, report);
        if (report.isErrorFree())
        {
            final int test = Integer.parseInt(value);
            if (test < minimum)
            {
                report.addError("Value \"" + value + "\" for key \"" + key + "\" must be at least " + minimum, LoadConfigErrorType.VALUE_OUT_OF_RANGE);
            }
            else if (test > maximum)
            {
                report.addError("Value \"" + value + "\" for key \"" + key + "\" must be at most " + maximum, LoadConfigErrorType.VALUE_OUT_OF_RANGE);
            }
        }

        return report;
    }

    /**
     * Check that the specified value is a valid integer without any range checking. The key is just used to report the error.
     * 
     * @param key
     *            the key for the value to check
     * @param value
     *            the value to check
     * @param report
     * @return report
     */
    protected LoadConfigReport validateIntegerString(String key, String value, LoadConfigReport report)
    {
        if (value == null)
        {
            report.addError("Value missing for key \"" + key + "\"", LoadConfigErrorType.MISSING_VALUE);
        }
        else
        {
            try
            {
                Integer.parseInt(value);
            }
            catch (Exception e)
            {
                report.addError("Unable to parse the value \"" + value + "\" for key \"" + key + "\"", LoadConfigErrorType.PARSE_ERROR_INT);
            }
        }

        return report;
    }

    /**
     * Collect and return load report for the specified String representation value of a float with the specified minimum and maximum. The key is just used to report the error.
     * 
     * @param key
     *            the key for the value to check
     * @param value
     *            the value to check
     * @param minimum
     *            inclusive minimum
     * @param maximum
     *            inclusive maximum
     * @param report
     * @return report
     */
    protected LoadConfigReport validateFloatWithLimitString(String key, String value, float minimum, float maximum, LoadConfigReport report)
    {
        validateFloatString(key, value, report);
        if (report.isErrorFree())
        {
            final float test = Float.parseFloat(value);
            if (test < minimum)
            {
                report.addError("Value \"" + value + "\" for key \"" + key + "\" must be at least " + minimum, LoadConfigErrorType.VALUE_OUT_OF_RANGE);
            }
            else if (test > maximum)
            {
                report.addError("Value \"" + value + "\" for key \"" + key + "\" must be at most " + maximum, LoadConfigErrorType.VALUE_OUT_OF_RANGE);
            }
        }

        return report;
    }

    /**
     * Check that the specified value is a valid float without any range checking. The key is just used to report the error.
     * 
     * @param key
     *            the key for the value to check
     * @param value
     *            the value to check
     * @param report
     * @return report
     */
    protected LoadConfigReport validateFloatString(String key, String value, LoadConfigReport report)
    {
        if (value == null)
        {
            report.addError("Value missing for key \"" + key + "\"", LoadConfigErrorType.MISSING_VALUE);
        }
        else
        {
            try
            {
                Float.parseFloat(value);
            }
            catch (Exception e)
            {
                report.addError("Unable to parse the value \"" + value + "\" for key \"" + key + "\"", LoadConfigErrorType.PARSE_ERROR_FLOAT);
            }
        }

        return report;
    }

    /**
     * Get the Boolean value of the String, filling the specified error array with any problems encountered
     * 
     * @param value
     *            the value to check
     * @param report
     * @return Boolean value
     */
    protected Boolean evaluateBooleanString(String value, LoadConfigReport report)
    {
        Boolean result = null;

        for (int i = 0; i < TRUES.length; i++)
        {
            if (TRUES[i].equalsIgnoreCase(value))
            {
                return Boolean.TRUE;
            }
        }
        for (int i = 0; i < FALSES.length; i++)
        {
            if (FALSES[i].equalsIgnoreCase(value))
            {
                return Boolean.FALSE;
            }
        }

        // If we reach this point, the value exists, but doesn't match
        report.addError("Value \"" + value + "\" must be a boolean (true or false)", LoadConfigErrorType.PARSE_ERROR_BOOL);

        return result;
    }

    /**
     * Get a Boolean value from a string in a Properties object. There is no default value. A missing value will be returned as null, not false. Any errors beyond a missing value
     * will be added to the specified report errors list.
     * 
     * @param props
     * @param key
     * @param report
     * @return boolean value
     */
    protected Boolean evaluateBooleanString(Properties props, String key, LoadConfigReport report)
    {
        Boolean result = null;

        if (props.containsKey(key))
        {
            final String value = props.getProperty(key);
            for (int i = 0; i < TRUES.length; i++)
            {
                if (TRUES[i].equalsIgnoreCase(value))
                {
                    return Boolean.TRUE;
                }
            }
            for (int i = 0; i < FALSES.length; i++)
            {
                if (FALSES[i].equalsIgnoreCase(value))
                {
                    return Boolean.FALSE;
                }
            }

            // If we reach this point, the value exists, but doesn't match
            report.addError("Value \"" + value + "\" for key \"" + key + "\" must be a boolean (true or false)", LoadConfigErrorType.PARSE_ERROR_BOOL);
        }
        else
        {
            report.addError("key \"" + key + "\" missing in the configuration", LoadConfigErrorType.MISSING_KEY);
        }

        return result;
    }

    /**
     * Get an Integer value from a string in a Properties object. There is no default value. A missing value will be returned as null, not zero. Any errors beyond a missing value
     * will be added to the specified report error list.
     * 
     * @param props
     * @param key
     * @param report
     * @return integer value
     */
    protected Integer evaluateIntegerString(Properties props, String key, LoadConfigReport report)
    {
        Integer result = null;

        if (props.containsKey(key))
        {
            final String value = props.getProperty(key);
            try
            {
                result = Integer.parseInt(value);
            }
            catch (Exception e)
            {
                report.addError("Value \"" + value + "\" for key \"" + key + "\" must be an integer", LoadConfigErrorType.PARSE_ERROR_INT);
            }
        }

        return result;
    }

    /**
     * Get a Color value from a string in a Properties object. There is no default value. A missing value will be returned as null, not zero. Any errors beyond a missing value will
     * be added to the specified report errors list.
     * 
     * @param props
     * @param key
     * @param report
     * @return color value
     */
    protected Color evaluateColorString(Properties props, String key, LoadConfigReport report)
    {
        final String hexString = props.getProperty(key);
        Color result = evaluateColorString(hexString, report);
        return result;
    }

    /**
     * Get a color by parsing the specified String containing the hexadecimal numeric representation of the color, adding to the specified report error list if any problems are
     * encountered during the translation
     * 
     * @param hexString
     * @param report
     * @return color
     */
    protected Color evaluateColorString(String hexString, LoadConfigReport report)
    {
        try
        {
            return new Color(Integer.parseInt(hexString, 16));
        }
        catch (Exception e)
        {
            report.addError("Color value \"" + hexString + "\" is invalid", LoadConfigErrorType.PARSE_ERROR_COLOR);
        }
        return null;
    }
}
