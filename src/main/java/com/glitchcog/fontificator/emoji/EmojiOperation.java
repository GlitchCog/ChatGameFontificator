package com.glitchcog.fontificator.emoji;

public enum EmojiOperation
{
    LOAD("load"), CACHE("cache");

    private final String description;

    private EmojiOperation(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
