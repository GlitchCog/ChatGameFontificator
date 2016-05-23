package com.glitchcog.fontificator.game;

/**
 * A genre of video game
 * 
 * @author Matt Yanos
 */
public enum GameGenre
{
    // @formatter:off
    NONE("None", "NA", "N/A", "NOT APPLICABLE"),
    ACTION_RPG("Action RPG", "Action", "RPG", "Action Adventure"), 
    ADVENTURE("Adventure", "Action Adventure"), 
    EDUCATIONAL("Educational", "Learning", "Edutainment"),
    FIGHTING("Fighting", "Combat", "Fight"), 
    FPS("FPS", "First Person Shooter", "Shooter"), 
    JRPG("JRPG", "RPG", "Japanese RPG", "Japan RPG"), 
    METROIDVANIA("Metroidvania"), 
    MMO_RPG("MMORPG", "MMO", "RPG"), 
    PLATFORMER("Platformer"), 
    PUZZLE("Puzzle"), 
    ROUGELIKE("Rougelike"), 
    RPG("RPG"), 
    SHMUP("Shmup", "Shoot 'em up", "Shooter", "Top Down Shooter"), 
    SIMULATION("Simulation", "Sim", "Simulator"), 
    STEALTH("Stealth"),
    STRATEGY("Strategy", "Real Time Strategy", "RTS"), 
    TACTICAL_RPG("Tactical RPG", "Tactical"), 
    INDIE("Indie", "Independent", "Independently Developed");
    // @formatter:on

    /**
     * All the things the genre might be called
     */
    private String[] names;

    private GameGenre(String... names)
    {
        this.names = names;
    }

    public String[] getNames()
    {
        return names;
    }

    public boolean matchesName(String testName)
    {
        for (String n : names)
        {
            if (testName.equalsIgnoreCase(n))
            {
                return true;
            }
        }
        return false;
    }

    public static GameGenre getByName(String testName)
    {
        if (testName != null)
        {
            for (GameGenre genre : values())
            {
                for (String n : genre.getNames())
                {
                    if (testName.toLowerCase().equals(n))
                    {
                        return genre;
                    }
                }
            }
        }
        return null;
    }
}
