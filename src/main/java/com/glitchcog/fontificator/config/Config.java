package com.glitchcog.fontificator.config;

import java.awt.Color;
import java.util.List;
import java.util.Properties;

/**
 * Config objects bridge the gap between the raw, unvalidated properties object and the processed values the
 * ControlPanels need for configuration
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
     * @return errors
     */
    public abstract List<String> load(Properties props, List<String> errors);

    /**
     * Clear out existing config data
     */
    public abstract void reset();

    protected static final String[] TRUES = new String[] { Boolean.toString(true), "yes", "+", "t", "1" };
    protected static final String[] FALSES = new String[] { Boolean.toString(false), "no", "-", "f", "0" };

    protected List<String> baseValidation(Properties props, String[] keys, List<String> errors)
    {
        for (int i = 0; i < keys.length; i++)
        {
            if (!props.containsKey(keys[i]))
            {
                errors.add("Key " + keys[i] + " missing in the configuration");
            }
            else if (props.getProperty(keys[i]) == null || props.getProperty(keys[i]).trim().isEmpty())
            {
                errors.add("Value for key " + keys[i] + " missing in the configuration");
            }
        }

        return errors;
    }

    /**
     * Check that the string values of a collection of booleans are all valid booleans
     * 
     * @param errors
     * @param booleans
     * @return errors
     */
    protected List<String> validateBooleanStrings(List<String> errors, String... booleans)
    {
        for (int i = 0; i < booleans.length; i++)
        {
            evaluateBooleanString(booleans[i], errors);
        }
        return errors;
    }

    /**
     * Collect and return errors for the specified String representation value of an integer with the specified minimum.
     * The key is just used to report the error.
     * 
     * @param key
     * @param value
     * @param minimum
     * @param errors
     * @return errors
     */
    protected List<String> validateIntegerWithLimitString(String key, String value, int minimum, List<String> errors)
    {
        return validateIntegerWithLimitString(key, value, minimum, Integer.MAX_VALUE, errors);
    }

    /**
     * Collect and return errors for the specified String representation value of an integer with the specified minimum
     * and maximum. The key is just used to report the error.
     * 
     * @param key
     *            the key for the value to check
     * @param value
     *            the value to check
     * @param minimum
     *            exclusive minimum
     * @param maximum
     *            exclusive maximum
     * @param errors
     * @return errors
     */
    protected List<String> validateIntegerWithLimitString(String key, String value, int minimum, int maximum, List<String> errors)
    {
        validateIntegerString(key, value, errors);
        if (errors.isEmpty())
        {
            final int test = Integer.parseInt(value);
            if (test < minimum)
            {
                errors.add("Value \"" + value + "\" for key \"" + key + "\" must be at least " + minimum);
            }
            else if (test > maximum)
            {
                errors.add("Value \"" + value + "\" for key \"" + key + "\" must be at most " + maximum);
            }
        }

        return errors;
    }

    /**
     * Check that the specified value is a valid integer without any range checking. The key is just used to report the
     * error.
     * 
     * @param key
     * @param value
     * @param errors
     * @return errors
     */
    protected List<String> validateIntegerString(String key, String value, List<String> errors)
    {
        if (value == null)
        {
            errors.add("Value missing for key \"" + key + "\"");
        }
        else
        {
            try
            {
                Integer.parseInt(value);
            }
            catch (Exception e)
            {
                errors.add("Unable to parse the value \"" + value + "\" for key \"" + key + "\"");
            }
        }

        return errors;
    }

    /**
     * Get the Boolean value of the String, filling the specified error array with any problems encountered
     * 
     * @param value
     * @param errors
     * @return Boolean value
     */
    protected Boolean evaluateBooleanString(String value, List<String> errors)
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
        errors.add("Value \"" + value + "\" must be a boolean (true or false)");

        return result;
    }

    /**
     * Get a Boolean value from a string in a Properties object. There is no default value. A missing value will be
     * returned as null, not false. Any errors beyond a missing value will be added to the specified errors list.
     * 
     * @param props
     * @param key
     * @param errors
     * @return boolean value
     */
    protected Boolean evaluateBooleanString(Properties props, String key, List<String> errors)
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
            errors.add("Value \"" + value + "\" for key \"" + key + "\" must be a boolean (true or false)");
        }
        else
        {
            errors.add("key \"" + key + "\" missing in the configuration");
        }

        return result;
    }

    /**
     * Get an Integer value from a string in a Properties object. There is no default value. A missing value will be
     * returned as null, not zero. Any errors beyond a missing value will be added to the specified errors list.
     * 
     * @param props
     * @param key
     * @param errors
     * @return integer value
     */
    protected Integer evaluateIntegerString(Properties props, String key, List<String> errors)
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
                errors.add("Value \"" + value + "\" for key \"" + key + "\" must be an integer");
            }
        }

        return result;
    }

    /**
     * Get a Color value from a string in a Properties object. There is no default value. A missing value will be
     * returned as null, not zero. Any errors beyond a missing value will be added to the specified errors list.
     * 
     * @param props
     * @param key
     * @param errors
     * @return color value
     */
    protected Color evaluateColorString(Properties props, String key, List<String> errors)
    {
        final String hexString = props.getProperty(key);
        Color result = evaluateColorString(hexString, errors);
        return result;
    }

    /**
     * Get a color by parsing the specified String containing the hexadecimal numeric representation of the color,
     * adding to the specified error list if any problems are encountered during the translation
     * 
     * @param hexString
     * @param errors
     * @return color
     */
    protected Color evaluateColorString(String hexString, List<String> errors)
    {
        try
        {
            return new Color(Integer.parseInt(hexString, 16));
        }
        catch (Exception e)
        {
            errors.add("Color value \"" + hexString + "\" is invalid");
        }
        return null;
    }
}
