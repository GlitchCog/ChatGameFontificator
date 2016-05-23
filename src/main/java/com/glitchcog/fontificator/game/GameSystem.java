package com.glitchcog.fontificator.game;

/**
 * A gaming system, console, or platform
 * 
 * @author Matt Yanos
 */
public enum GameSystem
{
    // @formatter:off
    NES("NES", "Nintendo", "Nintendo Entertainment System", "Famicom", "Famicom Disk System", "FamicomDiskSystem", "FDS"), 
    SNES("SNES", "Super Nintendo", "SuperNintendo", "Super Nintendo Entertainment System", "SuperNintendoEntertainmentSystem", "SFC", "Super Famicom", "SuperFamicom"), 
    N64("N64", "Nintendo 64", "Nintendo64", "Ultra 64", "Ultra64"), 
    GAME_BOY("Game Boy", "Gameboy", "GB"), 
    GAME_BOY_COLOR("Game Boy Color", "Gameboy Color", "GameboyColor", "GBC", "Game Boy Colour", "Gameboy Colour", "GameboyColour"), 
    GAME_BOY_ADVANCE("GBA", "Game Boy Advance", "Gameboy Advance", "Game Boy Advance SP", "Game Boy Advanced", "Gameboy Advanced", "Game Boy Advanced SP", "Game Boy SP", "Game Boy Micro"), 
    NDS("DS", "NDS", "Nintendo DS", "iQue DS", "Nintendo DS Lite", "DS Lite", "Nintendo DSi", "DSi", "Nintendo DSi XL"), 
    PS("PlayStation", "Play Station", "PS", "Sony Playstation", "Sony Play Station"), 
    PS2("PS2", "PlayStation 2", "Play Station 2", "Sony Playstation 2", "Sony Play Station 2"), 
    GCN("GameCube", "Game Cube", "GCN", "NGC"), 
    SMS("Master System", "MasterSystem", "SMS", "Sega Master System", "Sega MasterSystem"), 
    GENESIS("Genesis", "Sega Genesis", "SegaGenesis", "Mega Drive", "MegaDrive", "Sega Mega Drive", "Sega MegaDrive"), 
    SATURN("Saturn", "Sega Saturn", "SegaSaturn"), 
    DREAMCAST("Dreamcast", "Sega Dreamcast", "SegaDreamcast"), 
    PC("PC", "Personal Computer", "Computer", "Mac");
    // @formatter:on

    /**
     * All the things the system might be called
     */
    private final String[] names;

    private GameSystem(String... names)
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

    @Override
    public String toString()
    {
        return names[0];
    }

    public static GameSystem getByName(String testName)
    {
        if (testName != null)
        {
            for (GameSystem system : values())
            {
                for (String n : system.getNames())
                {
                    if (testName.toLowerCase().equals(n))
                    {
                        return system;
                    }
                }
            }
        }
        return null;
    }
}
