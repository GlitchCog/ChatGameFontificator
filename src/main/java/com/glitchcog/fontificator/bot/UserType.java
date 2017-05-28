package com.glitchcog.fontificator.bot;

public enum UserType
{
    NONE(""), MOD("mod", "moderator"), GLOBAL_MOD("global_mod"), ADMIN("admin"), STAFF("staff");

    private final String key;
    private final String badge;

    private UserType(String key)
    {
        this(key, key);
    }

    private UserType(String key, String badge)
    {
        this.key = key;
        this.badge = badge;
    }

    public String getKey()
    {
        return key;
    }

    public String getBadge()
    {
        return badge;
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
