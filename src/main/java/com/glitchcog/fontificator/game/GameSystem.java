package com.glitchcog.fontificator.game;

/**
 * A gaming system, console, or platform
 * 
 * @author Matt Yanos
 */
public enum GameSystem
{
    // @formatter:off
    NES("NES", "Nintendo", "Nintendo Entertainment System", "Famicom"), 
    SNES("SNES", "Super Nintendo", "Super Nintendo Entertainment System", "SFC", "Super Famicom"), 
    N64("N64", "Nintendo 64", "Nintendo64", "Ultra 64"), 
    GAME_BOY("Game Boy", "GB"), 
    GAME_BOY_COLOR("Game Boy Color", "GBC", "Game Boy Colour"), 
    GAME_BOY_ADVANCE("GBA", "Game Boy Advance", "Game Boy Advance SP", "Game Boy SP", "Game Boy Micro"), 
    NDS("DS", "NDS", "Nintendo DS", "iQue DS", "Nintendo DS Lite", "DS Lite", "Nintendo DSi", "DSi", "Nintendo DSi XL"), 
    PS("PlayStation", "PS", "Sony Playstation"), 
    PS2("PS2", "PlayStation 2", "Sony Playstation 2"), 
    GCN("GameCube", "GCN", "NGC"), 
    SMS("Master System", "SMS", "Sega Master System"), 
    GENESIS("Genesis", "Sega Genesis", "Mega Drive", "Sega Mega Drive"), 
    SATURN("Saturn", "Sega Saturn"), 
    DREAMCAST("Dreamcast", "Sega Dreamcast"), 
    PC("PC", "Personal Computer", "Computer");
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

    @Override
    public String toString()
    {
        return names[0];
    }
}
