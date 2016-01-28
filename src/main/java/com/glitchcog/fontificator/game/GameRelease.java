package com.glitchcog.fontificator.game;

import java.util.Date;

/**
 * An instance of a game being released, defined by the name of the release, which can be local and different than the cannonical name for the purposes of this program, the locale,
 * the system, and the date of the release. Also, whether or not the release is "official", meaning the owner of the copyright released it rather than an unofficial translation
 * patch.
 * 
 * @author Matt Yanos
 */
public class GameRelease
{
    /**
     * The video game name associated with this release
     */
    private final String name;

    /**
     * The date of the release
     */
    private final Date releaseDate;

    /**
     * The region of the release
     */
    private final GameLocale locale;

    /**
     * The game system the release is for
     */
    private final GameSystem system;

    /**
     * Whether the release is official or an unofficial release like a fan translation
     */
    private final boolean official;

    public GameRelease(String name, Date releaseDate, GameLocale locale, GameSystem system, boolean official)
    {
        this.name = name;
        this.releaseDate = releaseDate;
        this.locale = locale;
        this.system = system;
        this.official = official;
    }

    public String getName()
    {
        return name;
    }

    public Date getReleaseDate()
    {
        return releaseDate;
    }

    public GameLocale getLocale()
    {
        return locale;
    }

    public GameSystem getSystem()
    {
        return system;
    }

    public boolean isOfficial()
    {
        return official;
    }
}
