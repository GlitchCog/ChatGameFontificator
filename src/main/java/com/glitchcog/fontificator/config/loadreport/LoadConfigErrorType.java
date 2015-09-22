package com.glitchcog.fontificator.config.loadreport;

/**
 * Types of errors encountered while loading config files and validating their contents
 * 
 * @author Matt Yanos
 */
public enum LoadConfigErrorType
{
    // @formatter:off
    FILE_NOT_FOUND(true), 
    MISSING_KEY(false), 
    MISSING_VALUE(false), 
    VALUE_OUT_OF_RANGE(true), 
    PARSE_ERROR_INT(true), 
    PARSE_ERROR_BOOL(true), 
    PARSE_ERROR_CHAR(true), 
    PARSE_ERROR_COLOR(true), 
    PARSE_ERROR_STRING(true), 
    PARSE_ERROR_ENUM(true), 
    UNKNOWN_ERROR(true);
    // @formatter:on

    /**
     * Whether the error represents a problem that renders the configuration unusable
     */
    private final boolean problem;

    /**
     * Construct a LoadConfigErrorType enum
     * 
     * @param error
     *            Whether the error represents a problem that renders the configuration unusable
     */
    private LoadConfigErrorType(boolean error)
    {
        this.problem = error;
    }

    /**
     * Get whether the error represents a problem that renders the configuration unusable
     * 
     * @return problem
     */
    public boolean isProblem()
    {
        return problem;
    }
}
