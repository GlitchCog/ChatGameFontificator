package com.glitchcog.fontificator.bot;

public enum UserType
{
    NONE(""), MOD("mod"), GLOBAL_MOD("global_mod"), ADMIN("admin"), STAFF("staff"), BOT("bot");

    private final String key;

    private UserType(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    public static UserType getByKey(String key)
    {
        for (UserType type : values())
        {
            if (type.getKey().equals(key))
            {
                return type;
            }
        }
        return NONE;
    }
}
