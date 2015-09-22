package com.glitchcog.fontificator.config.loadreport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * When a config file is loaded and its contents are validated, this report describes the results of the load.
 * 
 * @author Matt Yanos
 */
public class LoadConfigReport
{
    private static final int MAX_NUMBER_OF_NON_PROBLEMS = 15;

    /**
     * A set of types of errors that have been collected in this report
     */
    private Set<LoadConfigErrorType> types;;

    /**
     * Human-readable messages describing the errors collected so far
     */
    private List<String> messages;

    /**
     * Instantiates an empty report
     */
    public LoadConfigReport()
    {
        types = new HashSet<LoadConfigErrorType>();
        messages = new ArrayList<String>();
    }

    /**
     * Instantiate this report with only a type, when there is no need for human-readable explanations
     * 
     * @param type
     */
    public LoadConfigReport(LoadConfigErrorType type)
    {
        this();
        types.add(type);
    }

    /**
     * Add an error to this report
     * 
     * @param message
     * @param type
     */
    public void addError(String message, LoadConfigErrorType type)
    {
        messages.add(message);
        types.add(type);
    }

    /**
     * Whether no errors have been reported, whether they are problems or not. This check identifies the load as being
     * capable of handling any subsequent work, no need to supplement the results with defaults.
     * 
     * @return error free
     */
    public boolean isErrorFree()
    {
        return messages.isEmpty();
    }

    /**
     * Get the opposite of whether there is a problem
     * 
     * @return success
     */
    public boolean isSuccess()
    {
        return !isProblem();
    }

    /**
     * Get whether there is a problem. A problem is whenever at least one of the types of errors is marked as a
     * problem-type error.
     * 
     * @return problem
     */
    public boolean isProblem()
    {
        for (LoadConfigErrorType result : types)
        {
            if (result.isProblem())
            {
                return true;
            }
        }
        // No real problems exist, but if there are more than the max number allowed, consider it a problem. For
        // example, someone might be loading a random text file as a config file that has nothing to do with this
        // program. That should be a problem, even though it will register as merely having a bunch of missing values
        return messages.size() > MAX_NUMBER_OF_NON_PROBLEMS;
    }

    /**
     * Get the messages
     * 
     * @return messages
     */
    public List<String> getMessages()
    {
        return messages;
    }

    /**
     * Get the types
     * 
     * @return types
     */
    public Set<LoadConfigErrorType> getTypes()
    {
        return types;
    }

    /**
     * Adds all the contents of the specified other report to this report
     * 
     * @param otherReport
     */
    public void addFromReport(LoadConfigReport otherReport)
    {
        for (String msg : otherReport.getMessages())
        {
            messages.add(msg);
        }
        for (LoadConfigErrorType rslt : otherReport.getTypes())
        {
            types.add(rslt);
        }
    }
}
