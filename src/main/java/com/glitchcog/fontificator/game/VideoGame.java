package com.glitchcog.fontificator.game;

/**
 * A video game represents a non-empty set of video game releases, sharing the same font and border aesthetic,
 * regardless of when they were released or what system they were released for. For example, the VideoGame object
 * representing the original Super Mario Bros. would include the 1985 NES release and the 1999 Game Boy Color release
 * which maintained the original's graphics, but it would not include the SNES Super Mario All Stars release because of
 * the new font and style. This class exists to create a better method for searching through existing game fonts and
 * borders.
 * 
 * @author Matt Yanos
 */
public class VideoGame
{
    private final String name;

    private GameGenre[] genres = new GameGenre[0];

    private GameRelease[] releases = new GameRelease[0];

    public VideoGame(String name)
    {
        this.name = name;
    }

    public void addGenre(GameGenre genre)
    {
        GameGenre[] swap = new GameGenre[genres.length + 1];
        for (int i = 0; i < genres.length; i++)
        {
            swap[i] = genres[i];
        }
        swap[genres.length] = genre;
        genres = swap;
    }

    public void addRelease(GameRelease release)
    {
        GameRelease[] swap = new GameRelease[releases.length + 1];
        for (int i = 0; i < releases.length; i++)
        {
            swap[i] = releases[i];
        }
        swap[releases.length] = release;
        releases = swap;
    }

    public String getName()
    {
        return name;
    }

    public GameGenre[] getGenres()
    {
        return genres;
    }

    public GameRelease[] getReleases()
    {
        return releases;
    }
}
